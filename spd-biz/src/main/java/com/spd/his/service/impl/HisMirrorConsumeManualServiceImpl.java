package com.spd.his.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.HisMatchTextUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.uuid.IdUtils;
import com.spd.department.domain.DeptBatchConsume;
import com.spd.department.domain.DeptBatchConsumeEntry;
import com.spd.department.domain.StkDepInventory;
import com.spd.department.mapper.StkDepInventoryMapper;
import com.spd.department.service.IDeptBatchConsumeService;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdDepartmentMapper;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.gz.domain.GzDepInventory;
import com.spd.gz.mapper.GzDepInventoryMapper;
import com.spd.his.domain.HisInpatientChargeMirror;
import com.spd.his.domain.HisChargeItemMirror;
import com.spd.his.domain.HisMirrorConsumeLink;
import com.spd.his.domain.HisOutpatientChargeMirror;
import com.spd.his.domain.dto.HisGenerateConsumeResultVo;
import com.spd.his.domain.dto.HisMirrorHighApplyBody;
import com.spd.his.domain.dto.HisMirrorHighApplyLine;
import com.spd.his.domain.dto.HisMirrorHighApplyResultVo;
import com.spd.his.domain.dto.HisMirrorHighScanBody;
import com.spd.his.domain.dto.HisMirrorHighScanResultVo;
import com.spd.his.domain.dto.HisMirrorManualRowBody;
import com.spd.his.mapper.HisInpatientChargeMirrorMapper;
import com.spd.his.mapper.HisChargeItemMirrorMapper;
import com.spd.his.mapper.HisMirrorConsumeLinkMapper;
import com.spd.his.mapper.HisOutpatientChargeMirrorMapper;
import com.spd.his.service.IHisMirrorConsumeManualService;

@Service
public class HisMirrorConsumeManualServiceImpl implements IHisMirrorConsumeManualService
{
    private static final Logger log = LoggerFactory.getLogger(HisMirrorConsumeManualServiceImpl.class);

    private static final String KIND_IN = "INPATIENT";
    private static final String KIND_OUT = "OUTPATIENT";
    private static final String STATUS_PENDING = "PENDING_CONSUME";
    private static final String STATUS_PARTIAL = "PARTIALLY_CONSUMED";
    private static final String STATUS_CONSUMED = "CONSUMED";
    private static final String BILL_LOW = "HIS_MIRROR_ROW_LOW";
    private static final String BILL_HIGH = "HIS_MIRROR_ROW_HIGH";
    private static final String LEGACY_BATCH = "HIS_MIRROR_BATCH";
    private static final String PROC_TYPE_LOW = "LOW_VALUE";
    private static final String PROC_TYPE_HIGH = "HIGH_VALUE";

    @Autowired
    private HisInpatientChargeMirrorMapper hisInpatientChargeMirrorMapper;
    @Autowired
    private HisOutpatientChargeMirrorMapper hisOutpatientChargeMirrorMapper;
    @Autowired
    private HisMirrorConsumeLinkMapper hisMirrorConsumeLinkMapper;
    @Autowired
    private FdDepartmentMapper fdDepartmentMapper;
    @Autowired
    private FdMaterialMapper fdMaterialMapper;
    @Autowired
    private StkDepInventoryMapper stkDepInventoryMapper;
    @Autowired
    private GzDepInventoryMapper gzDepInventoryMapper;
    @Autowired
    private IDeptBatchConsumeService deptBatchConsumeService;
    @Autowired
    private HisChargeItemMirrorMapper hisChargeItemMirrorMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HisGenerateConsumeResultVo processLowValue(HisMirrorManualRowBody body)
    {
        String visitKind = resolveVisitKind(body == null ? null : body.getVisitKind());
        String mirrorRowId = body == null ? null : StringUtils.trimToEmpty(body.getMirrorRowId());
        if (StringUtils.isEmpty(mirrorRowId))
        {
            throw new ServiceException("请指定 mirrorRowId");
        }
        String tenantId = requireTenant();
        MirrorLine line = loadMirrorLine(tenantId, visitKind, mirrorRowId);
        if (!STATUS_PENDING.equals(line.processStatus))
        {
            throw new ServiceException("仅「待处理」状态的镜像行可做低值一次性消耗，当前状态：" + line.processStatus);
        }
        assertSourcesForLow(listDistinctSources(tenantId, visitKind, mirrorRowId));
        BigDecimal qty = line.quantity == null ? BigDecimal.ZERO : line.quantity;
        if (qty.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("计费数量为 0，无需生成消耗");
        }
        List<AllocPiece> pieces = new ArrayList<>();
        int skipped = appendPiecesForLine(tenantId, line.fetchBatchId, visitKind, line.id, line.deptHisCode, line.chargeItemId, qty, pieces);
        if (skipped > 0 || pieces.isEmpty())
        {
            throw new ServiceException("低值处理失败：科室二级库库存不满足全量扣减或缺少对照，镜像行仍保持未处理");
        }
        Map<String, List<AllocPiece>> groups = new LinkedHashMap<>();
        for (AllocPiece p : pieces)
        {
            Long wh = p.inv.getWarehouseId();
            String key = p.departmentId + "|" + (wh == null ? "null" : wh.toString());
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(p);
        }
        HisGenerateConsumeResultVo vo = new HisGenerateConsumeResultVo();
        vo.setMirrorLineSkippedZeroQty(skipped);
        List<HisMirrorConsumeLink> linkBuffer = new ArrayList<>();
        Date linkTime = DateUtils.getNowDate();
        String auditName = SecurityUtils.getUsername();
        String createBy = SecurityUtils.getUserIdStr();
        for (Map.Entry<String, List<AllocPiece>> en : groups.entrySet())
        {
            List<AllocPiece> gPieces = en.getValue();
            AllocPiece first = gPieces.get(0);
            Long deptId = first.departmentId;
            Long warehouseId = first.inv.getWarehouseId();
            List<DeptBatchConsumeEntry> entries = new ArrayList<>();
            for (AllocPiece p : gPieces)
            {
                entries.add(buildLowConsumeEntry(p, deptId, createBy));
            }
            DeptBatchConsume bill = new DeptBatchConsume();
            bill.setDepartmentId(deptId);
            bill.setWarehouseId(warehouseId);
            bill.setUserId(SecurityUtils.getUserId());
            bill.setConsumeBillDate(DateUtils.getNowDate());
            bill.setRemark("HIS计费镜像低值手动处理 mirrorRowId=" + mirrorRowId);
            bill.setBillSource(BILL_LOW);
            bill.setDisallowReverse(1);
            bill.setHisFetchBatchId(line.fetchBatchId);
            bill.setCreateBy(createBy);
            bill.setDisableEntryDedup(Boolean.TRUE);
            bill.setDeptBatchConsumeEntryList(entries);
            deptBatchConsumeService.insertDeptBatchConsume(bill);
            Long consumeId = bill.getId();
            if (consumeId == null)
            {
                throw new ServiceException("生成消耗主单失败");
            }
            vo.getConsumeBillIds().add(consumeId);
            deptBatchConsumeService.auditConsume(String.valueOf(consumeId), auditName);
            List<DeptBatchConsumeEntry> persisted = bill.getDeptBatchConsumeEntryList();
            if (persisted == null || persisted.size() != gPieces.size())
            {
                throw new ServiceException("消耗明细保存数量与预期不一致");
            }
            for (int i = 0; i < gPieces.size(); i++)
            {
                AllocPiece p = gPieces.get(i);
                DeptBatchConsumeEntry e = persisted.get(i);
                HisMirrorConsumeLink lk = new HisMirrorConsumeLink();
                lk.setId(IdUtils.fastUUID());
                lk.setTenantId(tenantId);
                lk.setVisitKind(visitKind);
                lk.setMirrorRowId(p.mirrorRowId);
                lk.setFetchBatchId(p.fetchBatchId);
                lk.setDeptBatchConsumeId(consumeId);
                lk.setDeptBatchConsumeEntryId(e.getId());
                lk.setAllocQty(p.take);
                lk.setCreateTime(linkTime);
                linkBuffer.add(lk);
            }
        }
        flushLinks(linkBuffer);
        Date procTime = DateUtils.getNowDate();
        String procBy = currentMirrorProcessBy();
        if (KIND_IN.equals(visitKind))
        {
            hisInpatientChargeMirrorMapper.updateMirrorProcessByIds(tenantId, java.util.Collections.singletonList(mirrorRowId),
                STATUS_CONSUMED, PROC_TYPE_LOW, procTime, procBy);
        }
        else
        {
            hisOutpatientChargeMirrorMapper.updateMirrorProcessByIds(tenantId, java.util.Collections.singletonList(mirrorRowId),
                STATUS_CONSUMED, PROC_TYPE_LOW, procTime, procBy);
        }
        vo.setConsumeBillCount(vo.getConsumeBillIds().size());
        vo.setConsumeEntryCount(pieces.size());
        vo.setLinkRowCount(linkBuffer.size());
        vo.setMirrorLineConsumedCount(1);
        vo.getMessages().add("低值处理完成：已生成并审核科室批量消耗");
        return vo;
    }

    @Override
    public HisMirrorHighScanResultVo scanHighBarcode(HisMirrorHighScanBody body)
    {
        String visitKind = resolveVisitKind(body == null ? null : body.getVisitKind());
        String mirrorRowId = body == null ? null : StringUtils.trimToEmpty(body.getMirrorRowId());
        String codeRaw = body == null ? null : body.getInHospitalCode();
        if (StringUtils.isAnyEmpty(mirrorRowId, codeRaw))
        {
            throw new ServiceException("请指定 mirrorRowId 与 inHospitalCode");
        }
        String tenantId = requireTenant();
        MirrorLine line = loadMirrorLine(tenantId, visitKind, mirrorRowId);
        if (!STATUS_PENDING.equals(line.processStatus) && !STATUS_PARTIAL.equals(line.processStatus))
        {
            throw new ServiceException("仅「待处理」或「部分消耗」可做高值扫码，当前状态：" + line.processStatus);
        }
        assertSourcesForHigh(listDistinctSources(tenantId, visitKind, mirrorRowId));
        BigDecimal billQty = line.quantity == null ? BigDecimal.ZERO : line.quantity;
        BigDecimal allocated = nz(hisMirrorConsumeLinkMapper.sumAllocQtyForMirrorRow(tenantId, visitKind, mirrorRowId));
        BigDecimal remaining = billQty.subtract(allocated);
        if (remaining.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("该镜像行计费数量已全部消耗完毕");
        }
        FdDepartment dept = resolveDepartment(tenantId, line.deptHisCode, mirrorRowId);
        FdMaterial mat = resolveMaterial(tenantId, line.chargeItemId, mirrorRowId);
        assertChargeItemValueLevelForHigh(tenantId, line.chargeItemId, mat);
        GzDepInventory hit = findGzByNormalizedCode(tenantId, dept.getId(), mat.getId(), codeRaw);
        if (hit.getQty() == null || hit.getQty().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("本科室该院内码高值虚拟库存不足或数量为 0");
        }
        BigDecimal maxApply = remaining.min(hit.getQty());
        HisMirrorHighScanResultVo vo = new HisMirrorHighScanResultVo();
        vo.setGzDepInventoryId(hit.getId());
        vo.setInHospitalCode(hit.getInHospitalCode());
        vo.setGzAvailableQty(hit.getQty());
        vo.setBillQty(billQty);
        vo.setAlreadyConsumedQty(allocated);
        vo.setBillRemainingQty(remaining);
        vo.setMaxApplyQty(maxApply);
        vo.setMaterialName(mat.getName());
        vo.setBatchNo(hit.getBatchNo());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HisMirrorHighApplyResultVo applyHighConsume(HisMirrorHighApplyBody body)
    {
        String visitKind = resolveVisitKind(body == null ? null : body.getVisitKind());
        String mirrorRowId = body == null ? null : StringUtils.trimToEmpty(body.getMirrorRowId());
        if (StringUtils.isEmpty(mirrorRowId) || body.getLines() == null || body.getLines().isEmpty())
        {
            throw new ServiceException("请指定 mirrorRowId 与至少一行高值消耗明细");
        }
        String tenantId = requireTenant();
        MirrorLine line = loadMirrorLine(tenantId, visitKind, mirrorRowId);
        if (!STATUS_PENDING.equals(line.processStatus) && !STATUS_PARTIAL.equals(line.processStatus))
        {
            throw new ServiceException("镜像行状态不允许继续高值消耗：" + line.processStatus);
        }
        assertSourcesForHigh(listDistinctSources(tenantId, visitKind, mirrorRowId));
        BigDecimal billQty = line.quantity == null ? BigDecimal.ZERO : line.quantity;
        BigDecimal allocatedBefore = nz(hisMirrorConsumeLinkMapper.sumAllocQtyForMirrorRow(tenantId, visitKind, mirrorRowId));
        BigDecimal remaining = billQty.subtract(allocatedBefore);
        if (remaining.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("该镜像行计费数量已全部消耗完毕");
        }
        FdDepartment dept = resolveDepartment(tenantId, line.deptHisCode, mirrorRowId);
        FdMaterial mat = resolveMaterial(tenantId, line.chargeItemId, mirrorRowId);
        assertChargeItemValueLevelForHigh(tenantId, line.chargeItemId, mat);
        Map<Long, BigDecimal> mergedByGz = new LinkedHashMap<>();
        for (HisMirrorHighApplyLine ln : body.getLines())
        {
            if (ln == null || ln.getGzDepInventoryId() == null || ln.getQty() == null)
            {
                continue;
            }
            if (ln.getQty().compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException("高值消耗数量须大于 0");
            }
            mergedByGz.merge(ln.getGzDepInventoryId(), ln.getQty(), BigDecimal::add);
        }
        if (mergedByGz.isEmpty())
        {
            throw new ServiceException("没有有效的高值消耗明细");
        }
        BigDecimal applySum = mergedByGz.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        List<DeptBatchConsumeEntry> entries = new ArrayList<>();
        Long billWarehouseId = null;
        String createBy = SecurityUtils.getUserIdStr();
        for (Map.Entry<Long, BigDecimal> en : mergedByGz.entrySet())
        {
            Long gzId = en.getKey();
            BigDecimal lineQty = en.getValue();
            GzDepInventory gz = gzDepInventoryMapper.selectGzDepInventoryById(gzId);
            if (gz == null)
            {
                throw new ServiceException("高值科室库存不存在：" + gzId);
            }
            if (gz.getDepartmentId() == null || !gz.getDepartmentId().equals(dept.getId()))
            {
                throw new ServiceException("高值库存不属于当前计费镜像行对应科室，请检查院内码是否为本部门");
            }
            BigDecimal gq = gz.getQty() == null ? BigDecimal.ZERO : gz.getQty();
            if (gq.compareTo(lineQty) < 0)
            {
                throw new ServiceException(String.format("保存失败：院内码对应高值虚拟库存不足（当前%s，申请%s）", gq, lineQty));
            }
            if (gz.getMaterialId() == null || !gz.getMaterialId().equals(mat.getId()))
            {
                throw new ServiceException("扫码耗材与计费项目对照的耗材不一致");
            }
            DeptBatchConsumeEntry e = buildHighEntryFromGz(gz, lineQty, mirrorRowId, dept.getId(), createBy, mat);
            entries.add(e);
            if (billWarehouseId == null && gz.getWarehouse() != null && gz.getWarehouse().getId() != null)
            {
                billWarehouseId = gz.getWarehouse().getId();
            }
        }
        if (applySum.compareTo(remaining) > 0)
        {
            throw new ServiceException(String.format("本次消耗合计 %s 超过镜像剩余计费数量 %s", applySum, remaining));
        }
        DeptBatchConsume bill = new DeptBatchConsume();
        bill.setDepartmentId(dept.getId());
        bill.setWarehouseId(billWarehouseId);
        bill.setUserId(SecurityUtils.getUserId());
        bill.setConsumeBillDate(DateUtils.getNowDate());
        bill.setRemark("HIS计费镜像高值手动扫码 mirrorRowId=" + mirrorRowId);
        bill.setBillSource(BILL_HIGH);
        bill.setDisallowReverse(1);
        bill.setHisFetchBatchId(line.fetchBatchId);
        bill.setCreateBy(createBy);
        bill.setDisableEntryDedup(Boolean.TRUE);
        bill.setDeptBatchConsumeEntryList(entries);
        deptBatchConsumeService.insertDeptBatchConsume(bill);
        Long consumeId = bill.getId();
        if (consumeId == null)
        {
            throw new ServiceException("生成消耗主单失败");
        }
        String auditName = SecurityUtils.getUsername();
        deptBatchConsumeService.auditConsume(String.valueOf(consumeId), auditName);
        List<DeptBatchConsumeEntry> persisted = bill.getDeptBatchConsumeEntryList();
        if (persisted == null || persisted.size() != entries.size())
        {
            throw new ServiceException("消耗明细保存数量与预期不一致");
        }
        Date linkTime = DateUtils.getNowDate();
        List<HisMirrorConsumeLink> links = new ArrayList<>();
        for (int i = 0; i < persisted.size(); i++)
        {
            HisMirrorConsumeLink lk = new HisMirrorConsumeLink();
            lk.setId(IdUtils.fastUUID());
            lk.setTenantId(tenantId);
            lk.setVisitKind(visitKind);
            lk.setMirrorRowId(mirrorRowId);
            lk.setFetchBatchId(line.fetchBatchId);
            lk.setDeptBatchConsumeId(consumeId);
            lk.setDeptBatchConsumeEntryId(persisted.get(i).getId());
            lk.setAllocQty(persisted.get(i).getQty());
            lk.setCreateTime(linkTime);
            links.add(lk);
        }
        flushLinks(links);
        BigDecimal allocatedAfter = nz(hisMirrorConsumeLinkMapper.sumAllocQtyForMirrorRow(tenantId, visitKind, mirrorRowId));
        BigDecimal newRemaining = billQty.subtract(allocatedAfter);
        String newStatus = newRemaining.compareTo(BigDecimal.ZERO) <= 0 ? STATUS_CONSUMED : STATUS_PARTIAL;
        Date procTime = DateUtils.getNowDate();
        String procBy = currentMirrorProcessBy();
        if (KIND_IN.equals(visitKind))
        {
            hisInpatientChargeMirrorMapper.updateMirrorProcessByIds(tenantId, java.util.Collections.singletonList(mirrorRowId),
                newStatus, PROC_TYPE_HIGH, procTime, procBy);
        }
        else
        {
            hisOutpatientChargeMirrorMapper.updateMirrorProcessByIds(tenantId, java.util.Collections.singletonList(mirrorRowId),
                newStatus, PROC_TYPE_HIGH, procTime, procBy);
        }
        HisMirrorHighApplyResultVo vo = new HisMirrorHighApplyResultVo();
        vo.setConsumeBillId(consumeId);
        vo.setAppliedQty(applySum);
        vo.setRemainingBillQty(newRemaining.max(BigDecimal.ZERO));
        vo.setMirrorProcessStatus(newStatus);
        return vo;
    }

    private static BigDecimal nz(BigDecimal v)
    {
        return v == null ? BigDecimal.ZERO : v;
    }

    private String requireTenant()
    {
        String tenantId = SecurityUtils.getCustomerId();
        if (StringUtils.isEmpty(tenantId))
        {
            throw new ServiceException("无法解析当前租户");
        }
        return tenantId;
    }

    private String currentMirrorProcessBy()
    {
        try
        {
            com.spd.common.core.domain.model.LoginUser lu = SecurityUtils.getLoginUser();
            if (lu != null && lu.getUser() != null && StringUtils.isNotBlank(lu.getUser().getNickName()))
            {
                return lu.getUser().getNickName();
            }
        }
        catch (Exception ignored)
        {
        }
        return SecurityUtils.getUsername();
    }

    private String resolveVisitKind(String raw)
    {
        if (StringUtils.isBlank(raw))
        {
            throw new ServiceException("请指定 visitKind（INPATIENT/OUTPATIENT）");
        }
        String v = raw.trim().toUpperCase();
        if (!KIND_IN.equals(v) && !KIND_OUT.equals(v))
        {
            throw new ServiceException("visitKind 仅支持 INPATIENT 或 OUTPATIENT");
        }
        return v;
    }

    private List<String> listDistinctSources(String tenantId, String visitKind, String mirrorRowId)
    {
        List<String> raw = hisMirrorConsumeLinkMapper.selectDistinctBillSourcesForMirror(tenantId, visitKind, mirrorRowId);
        if (raw == null)
        {
            return new ArrayList<>();
        }
        return raw.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    private void assertSourcesForLow(List<String> sources)
    {
        if (sources == null || sources.isEmpty())
        {
            return;
        }
        for (String s : sources)
        {
            String t = StringUtils.trimToEmpty(s);
            if (LEGACY_BATCH.equalsIgnoreCase(t))
            {
                throw new ServiceException("该镜像行已由历史「按批次」生成过消耗，不能改为低值手动处理。");
            }
            if (BILL_HIGH.equalsIgnoreCase(t))
            {
                throw new ServiceException("该镜像行已走高值扫码路径，请继续在「高值处理」中完成计费数量。");
            }
            if (BILL_LOW.equalsIgnoreCase(t))
            {
                throw new ServiceException("该镜像行已存在低值消耗记录。");
            }
        }
    }

    private void assertSourcesForHigh(List<String> sources)
    {
        if (sources == null || sources.isEmpty())
        {
            return;
        }
        for (String s : sources)
        {
            String t = StringUtils.trimToEmpty(s);
            if (LEGACY_BATCH.equalsIgnoreCase(t))
            {
                throw new ServiceException("该镜像行已由历史「按批次」生成过消耗，不能再走高值扫码路径。");
            }
            if (BILL_LOW.equalsIgnoreCase(t))
            {
                throw new ServiceException("该镜像行已按低值一次性消耗，不能再走高值扫码。");
            }
        }
    }

    private static class MirrorLine
    {
        String id;
        String fetchBatchId;
        String deptHisCode;
        String chargeItemId;
        BigDecimal quantity;
        String processStatus;
    }

    private MirrorLine loadMirrorLine(String tenantId, String visitKind, String mirrorRowId)
    {
        MirrorLine ml = new MirrorLine();
        ml.id = mirrorRowId;
        if (KIND_IN.equals(visitKind))
        {
            HisInpatientChargeMirror r = hisInpatientChargeMirrorMapper.selectByIdAndTenant(tenantId, mirrorRowId);
            if (r == null)
            {
                throw new ServiceException("住院镜像行不存在");
            }
            ml.fetchBatchId = r.getFetchBatchId();
            ml.deptHisCode = r.getDeptCode();
            ml.chargeItemId = r.getChargeItemId();
            ml.quantity = r.getQuantity();
            ml.processStatus = r.getProcessStatus();
            return ml;
        }
        HisOutpatientChargeMirror r = hisOutpatientChargeMirrorMapper.selectByIdAndTenant(tenantId, mirrorRowId);
        if (r == null)
        {
            throw new ServiceException("门诊镜像行不存在");
        }
        ml.fetchBatchId = r.getFetchBatchId();
        ml.deptHisCode = r.getClinicCode();
        ml.chargeItemId = r.getChargeItemId();
        ml.quantity = r.getQuantity();
        ml.processStatus = r.getProcessStatus();
        return ml;
    }

    private FdDepartment resolveDepartment(String tenantId, String deptHisCode, String mirrorRowId)
    {
        if (StringUtils.isBlank(deptHisCode))
        {
            throw new ServiceException("镜像行「" + mirrorRowId + "」缺少科室/就诊对照编码");
        }
        FdDepartment dept = fdDepartmentMapper.selectFdDepartmentByTenantAndHisId(tenantId, HisMatchTextUtils.normalizeMatchKey(deptHisCode));
        if (dept == null || dept.getId() == null)
        {
            throw new ServiceException("镜像行「" + mirrorRowId + "」无法匹配科室（HIS编码）");
        }
        return dept;
    }

    private FdMaterial resolveMaterial(String tenantId, String chargeItemId, String mirrorRowId)
    {
        if (StringUtils.isBlank(chargeItemId))
        {
            throw new ServiceException("镜像行「" + mirrorRowId + "」缺少 charge_item_id");
        }
        String normalizedChargeItemId = HisMatchTextUtils.normalizeMatchKey(chargeItemId);
        FdMaterial mat = fdMaterialMapper.selectFdMaterialByTenantAndHisChargeItemId(
            tenantId, normalizedChargeItemId);
        if (log.isInfoEnabled())
        {
            log.info("HIS镜像耗材匹配 tenantId={}, mirrorRowId={}, chargeItemIdRaw={}, chargeItemIdNorm={}, materialId={}",
                tenantId, mirrorRowId, chargeItemId, normalizedChargeItemId, mat == null ? null : mat.getId());
        }
        if (mat == null || mat.getId() == null)
        {
            throw new ServiceException("镜像行「" + mirrorRowId + "」无法匹配耗材档案（charge_item_id）");
        }
        return mat;
    }

    private GzDepInventory findGzByNormalizedCode(String tenantId, Long departmentId, Long materialId, String scanCode)
    {
        GzDepInventory probe = new GzDepInventory();
        probe.setDepartmentId(departmentId);
        probe.setMaterialId(materialId);
        probe.setShowZeroStock(Boolean.TRUE);
        List<GzDepInventory> list = gzDepInventoryMapper.selectGzDepInventoryList(probe);
        if (list == null || list.isEmpty())
        {
            throw new ServiceException("未找到本科室该耗材的高值虚拟库存记录");
        }
        String nScan = HisMatchTextUtils.normalizeMatchKey(scanCode);
        for (GzDepInventory g : list)
        {
            if (g == null || StringUtils.isBlank(g.getInHospitalCode()))
            {
                continue;
            }
            if (HisMatchTextUtils.normalizeMatchKey(g.getInHospitalCode()).equals(nScan))
            {
                return g;
            }
        }
        throw new ServiceException("未匹配到院内码对应的高值科室库存（请确认已对照且条码属于本科室）");
    }

    private void assertChargeItemValueLevelForLow(String tenantId, String chargeItemId, FdMaterial mat)
    {
        String level = resolveChargeItemValueLevel(tenantId, chargeItemId, mat);
        if ("1".equals(level))
        {
            throw new ServiceException("收费项目为高值属性，请使用高值扫码处理");
        }
    }

    private void assertChargeItemValueLevelForHigh(String tenantId, String chargeItemId, FdMaterial mat)
    {
        String level = resolveChargeItemValueLevel(tenantId, chargeItemId, mat);
        if (!"1".equals(level))
        {
            throw new ServiceException("收费项目为低值属性，请使用低值自动处理");
        }
    }

    private String resolveChargeItemValueLevel(String tenantId, String chargeItemId, FdMaterial mat)
    {
        if (StringUtils.isNotBlank(chargeItemId))
        {
            HisChargeItemMirror ci = hisChargeItemMirrorMapper.selectByTenantAndChargeItemId(tenantId, chargeItemId.trim());
            if (ci != null && StringUtils.isNotBlank(ci.getValueLevel()))
            {
                return ci.getValueLevel().trim();
            }
        }
        if (mat != null && "1".equals(StringUtils.trimToEmpty(mat.getIsGz())))
        {
            return "1";
        }
        return "2";
    }

    private int appendPiecesForLine(String tenantId, String fetchBatchId, String visitKind, String mirrorRowId,
        String deptHisCode, String chargeItemId, BigDecimal qty, List<AllocPiece> out)
    {
        if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0)
        {
            return 1;
        }
        FdDepartment dept = resolveDepartment(tenantId, deptHisCode, mirrorRowId);
        FdMaterial mat = resolveMaterial(tenantId, chargeItemId, mirrorRowId);
        assertChargeItemValueLevelForLow(tenantId, chargeItemId, mat);
        out.addAll(allocateLine(tenantId, dept.getId(), mat.getId(), qty, mirrorRowId, visitKind, fetchBatchId));
        return 0;
    }

    private List<AllocPiece> allocateLine(String tenantId, Long departmentId, Long materialId, BigDecimal need,
        String mirrorRowId, String visitKind, String fetchBatchId)
    {
        StkDepInventory probe = new StkDepInventory();
        probe.setTenantId(tenantId);
        probe.setDepartmentId(departmentId);
        probe.setMaterialId(materialId);
        probe.setReceiptConfirmStatus(1);
        List<StkDepInventory> rows = stkDepInventoryMapper.selectStkDepInventoryList(probe);
        if (rows == null)
        {
            rows = new ArrayList<>();
        }
        rows.sort(Comparator
            .comparing(StkDepInventory::getWarehouseDate, Comparator.nullsLast(Date::compareTo))
            .thenComparing(r -> r.getId() == null ? 0L : r.getId()));
        Long chosenWarehouseId = null;
        for (StkDepInventory r : rows)
        {
            if (r.getQty() != null && r.getQty().compareTo(BigDecimal.ZERO) > 0)
            {
                chosenWarehouseId = r.getWarehouseId();
                break;
            }
        }
        boolean hasPositive = rows.stream().anyMatch(r -> r.getQty() != null && r.getQty().compareTo(BigDecimal.ZERO) > 0);
        if (!hasPositive)
        {
            throw new ServiceException("科室「" + departmentId + "」耗材「" + materialId + "」无可用二级库库存（镜像行 " + mirrorRowId + "）");
        }
        final Long whKey = chosenWarehouseId;
        List<StkDepInventory> filtered = rows.stream()
            .filter(r -> Objects.equals(r.getWarehouseId(), whKey))
            .collect(Collectors.toList());
        BigDecimal remain = need;
        List<AllocPiece> pieces = new ArrayList<>();
        for (StkDepInventory r : filtered)
        {
            if (remain.compareTo(BigDecimal.ZERO) <= 0)
            {
                break;
            }
            if (r.getQty() == null || r.getQty().compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            BigDecimal take = r.getQty().min(remain);
            if (take.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            AllocPiece p = new AllocPiece();
            p.mirrorRowId = mirrorRowId;
            p.visitKind = visitKind;
            p.fetchBatchId = fetchBatchId;
            p.departmentId = departmentId;
            p.inv = r;
            p.take = take;
            pieces.add(p);
            remain = remain.subtract(take);
        }
        if (remain.compareTo(BigDecimal.ZERO) > 0)
        {
            throw new ServiceException("科室二级库库存不足：科室" + departmentId + " 耗材" + materialId + " 需求 " + need + " 镜像行 " + mirrorRowId);
        }
        return pieces;
    }

    private DeptBatchConsumeEntry buildLowConsumeEntry(AllocPiece p, Long departmentId, String createBy)
    {
        StkDepInventory inv = p.inv;
        DeptBatchConsumeEntry e = new DeptBatchConsumeEntry();
        e.setDepInventoryId(inv.getId());
        e.setKcNo(inv.getKcNo());
        e.setMaterialId(inv.getMaterialId());
        e.setBatchNo(inv.getBatchNo());
        e.setBatchNumer(inv.getBatchNumber());
        e.setBatchId(inv.getBatchId());
        e.setWarehouseId(inv.getWarehouseId());
        e.setDepartmentId(departmentId);
        e.setSupplierId(inv.getSupplierId());
        e.setFactoryId(inv.getFactoryId());
        e.setMaterialNo(inv.getMaterialNo());
        e.setQty(p.take);
        BigDecimal up = inv.getUnitPrice() != null ? inv.getUnitPrice() : BigDecimal.ZERO;
        e.setUnitPrice(up);
        e.setPrice(up);
        e.setAmt(up.multiply(p.take));
        e.setBeginTime(inv.getBeginDate());
        e.setEndTime(inv.getEndDate());
        e.setMaterialDate(inv.getMaterialDate());
        e.setWarehouseDate(inv.getWarehouseDate());
        e.setSettlementType(inv.getSettlementType());
        e.setMaterialName(inv.getSnapMaterialName());
        e.setMaterialSpeci(inv.getSnapMaterialSpeci());
        e.setMaterialModel(inv.getSnapMaterialModel());
        e.setMaterialFactoryId(inv.getSnapMaterialFactoryId());
        e.setMainBarcode(inv.getMainBarcode());
        e.setSubBarcode(inv.getSubBarcode());
        e.setRemark("HIS镜像低值:" + p.mirrorRowId);
        e.setCreateBy(createBy);
        return e;
    }

    private DeptBatchConsumeEntry buildHighEntryFromGz(GzDepInventory gz, BigDecimal qty, String mirrorRowId, Long departmentId, String createBy, FdMaterial mat)
    {
        DeptBatchConsumeEntry e = new DeptBatchConsumeEntry();
        e.setGzDepInventoryId(gz.getId());
        e.setDepInventoryId(null);
        e.setMaterialId(gz.getMaterialId());
        e.setBatchNo(gz.getBatchNo());
        e.setBatchNumer(gz.getMaterialNo());
        e.setMaterialNo(gz.getMaterialNo());
        e.setWarehouseId(gz.getWarehouse() != null ? gz.getWarehouse().getId() : null);
        e.setDepartmentId(departmentId);
        if (gz.getSupplierId() != null)
        {
            e.setSupplierId(String.valueOf(gz.getSupplierId()));
        }
        if (mat != null && mat.getFactoryId() != null)
        {
            e.setFactoryId(mat.getFactoryId());
        }
        BigDecimal up = gz.getUnitPrice() != null ? gz.getUnitPrice() : BigDecimal.ZERO;
        e.setUnitPrice(up);
        e.setPrice(up);
        e.setQty(qty);
        e.setAmt(up.multiply(qty));
        e.setBeginTime(gz.getMaterialDate());
        e.setEndTime(gz.getEndTime());
        e.setMaterialDate(gz.getMaterialDate());
        e.setWarehouseDate(gz.getWarehouseDate());
        e.setMainBarcode(gz.getMasterBarcode());
        e.setSubBarcode(gz.getSecondaryBarcode());
        if (gz.getMaterial() != null)
        {
            e.setMaterialName(gz.getMaterial().getName());
            e.setMaterialSpeci(gz.getMaterial().getSpeci());
            e.setMaterialModel(gz.getMaterial().getModel());
        }
        else if (mat != null)
        {
            e.setMaterialName(mat.getName());
            e.setMaterialSpeci(mat.getSpeci());
            e.setMaterialModel(mat.getModel());
        }
        e.setRemark("HIS镜像高值:" + mirrorRowId);
        e.setCreateBy(createBy);
        return e;
    }

    private void flushLinks(List<HisMirrorConsumeLink> linkBuffer)
    {
        final int n = 80;
        for (int i = 0; i < linkBuffer.size(); i += n)
        {
            int end = Math.min(i + n, linkBuffer.size());
            hisMirrorConsumeLinkMapper.insertBatch(linkBuffer.subList(i, end));
        }
    }

    private static class AllocPiece
    {
        String mirrorRowId;
        String visitKind;
        String fetchBatchId;
        Long departmentId;
        StkDepInventory inv;
        BigDecimal take;
    }
}

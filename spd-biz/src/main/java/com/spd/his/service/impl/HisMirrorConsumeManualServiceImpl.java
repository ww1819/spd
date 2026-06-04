package com.spd.his.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.spd.common.utils.uuid.UUID7;
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
import com.spd.gz.domain.GzTraceability;
import com.spd.gz.domain.GzTraceabilityEntry;
import com.spd.gz.mapper.GzDepInventoryMapper;
import com.spd.gz.service.IGzTraceabilityService;
import com.spd.his.constant.HisMirrorProcessConstants;
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
import com.spd.his.mapper.HisPatientChargeMirrorUnifiedMapper;
import com.spd.his.service.IHisMirrorConsumeManualService;
import com.spd.his.support.HisMirrorProcessUserMessages;
import com.spd.gz.support.GzTraceSourceConstants;
import com.spd.his.support.HisMirrorValueLevelSupport;

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
    @Autowired
    private HisPatientChargeMirrorUnifiedMapper hisPatientChargeMirrorUnifiedMapper;
    @Autowired
    private HisMirrorProcessOutcomeRecorder hisMirrorProcessOutcomeRecorder;
    @Autowired
    private IGzTraceabilityService gzTraceabilityService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HisGenerateConsumeResultVo processLowValue(HisMirrorManualRowBody body)
    {
        String processParty = HisMirrorProcessConstants.resolveParty(body == null ? null : body.getProcessParty());
        String visitKind = resolveVisitKind(body == null ? null : body.getVisitKind());
        String mirrorRowId = body == null ? null : StringUtils.trimToEmpty(body.getMirrorRowId());
        try
        {
            return processLowValueInternal(body, processParty);
        }
        catch (ServiceException e)
        {
            hisMirrorProcessOutcomeRecorder.recordFailure(requireTenant(), visitKind, mirrorRowId, processParty, e.getMessage());
            throw e;
        }
        catch (Exception e)
        {
            String msg = HisMirrorProcessUserMessages.safeFailureMessage(e, "低值核销失败");
            hisMirrorProcessOutcomeRecorder.recordFailure(requireTenant(), visitKind, mirrorRowId, processParty, msg);
            throw new ServiceException(msg);
        }
    }

    private HisGenerateConsumeResultVo processLowValueInternal(HisMirrorManualRowBody body, String processParty)
    {
        String visitKind = resolveVisitKind(body == null ? null : body.getVisitKind());
        String mirrorRowId = body == null ? null : StringUtils.trimToEmpty(body.getMirrorRowId());
        if (StringUtils.isEmpty(mirrorRowId))
        {
            throw new ServiceException("请选择要核销的计费记录");
        }
        String tenantId = requireTenant();
        MirrorLine line = loadMirrorLine(tenantId, visitKind, mirrorRowId);
        if (!STATUS_PENDING.equals(line.processStatus))
        {
            throw new ServiceException(HisMirrorProcessUserMessages.lowPendingOnly(line.processStatus));
        }
        assertSourcesForLow(listDistinctSources(tenantId, visitKind, mirrorRowId));
        BigDecimal qty = line.quantity == null ? BigDecimal.ZERO : line.quantity;
        if (qty.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException(HisMirrorProcessUserMessages.zeroQuantity());
        }
        requireMirrorUnitPrice(line);
        List<AllocPiece> pieces = new ArrayList<>();
        int skipped = appendPiecesForLine(tenantId, line.fetchBatchId, visitKind, line.id, line.deptHisCode, line.chargeItemId,
            line.itemName, qty, line.unitPrice, pieces);
        if (skipped > 0 || pieces.isEmpty())
        {
            FdDepartment dept = resolveDepartment(tenantId, line.deptHisCode);
            FdMaterial mat = resolveMaterial(tenantId, line.chargeItemId, line.itemName);
            throwInsufficientStock(mat);
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
            deptBatchConsumeService.auditConsume(String.valueOf(consumeId), createBy);
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
                lk.setId(UUID7.generateUUID7());
                lk.setTenantId(tenantId);
                lk.setVisitKind(visitKind);
                lk.setMirrorRowId(p.mirrorRowId);
                lk.setFetchBatchId(p.fetchBatchId);
                lk.setDeptBatchConsumeId(consumeId);
                lk.setDeptBatchConsumeEntryId(e.getId());
                lk.setAllocQty(p.take);
                lk.setDepInventoryId(p.inv.getId());
                lk.setStkDepEndDate(p.inv.getEndDate());
                lk.setReturnedQty(BigDecimal.ZERO);
                lk.setRefundableRemainingQty(p.take);
                lk.setDelFlag(0);
                lk.setCreateTime(linkTime);
                linkBuffer.add(lk);
            }
        }
        flushLinks(linkBuffer);
        persistProcessSuccess(tenantId, visitKind, mirrorRowId, STATUS_CONSUMED, PROC_TYPE_LOW, processParty);
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
            throw new ServiceException("请扫描院内码");
        }
        String tenantId = requireTenant();
        MirrorLine line = loadMirrorLine(tenantId, visitKind, mirrorRowId);
        if (!STATUS_PENDING.equals(line.processStatus) && !STATUS_PARTIAL.equals(line.processStatus))
        {
            throw new ServiceException(HisMirrorProcessUserMessages.highScanNotAllowed(line.processStatus));
        }
        assertSourcesForHigh(listDistinctSources(tenantId, visitKind, mirrorRowId));
        BigDecimal billQty = line.quantity == null ? BigDecimal.ZERO : line.quantity;
        BigDecimal allocated = nz(hisMirrorConsumeLinkMapper.sumAllocQtyForMirrorRow(tenantId, visitKind, mirrorRowId));
        BigDecimal remaining = billQty.subtract(allocated);
        if (remaining.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException(HisMirrorProcessUserMessages.fullyConsumed());
        }
        requireMirrorUnitPrice(line);
        FdDepartment dept = resolveDepartment(tenantId, line.deptHisCode);
        FdMaterial mat = resolveMaterial(tenantId, line.chargeItemId, line.itemName);
        assertChargeItemValueLevelForHigh(tenantId, line.chargeItemId, line.itemName, mat);
        GzDepInventory hit = findGzByNormalizedCode(tenantId, dept, mat, codeRaw, line.unitPrice);
        if (hit.getQty() == null || hit.getQty().compareTo(BigDecimal.ZERO) <= 0)
        {
            throwInsufficientStock(mat);
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
        String processParty = HisMirrorProcessConstants.resolveParty(body == null ? null : body.getProcessParty());
        String visitKind = resolveVisitKind(body == null ? null : body.getVisitKind());
        String mirrorRowId = body == null ? null : StringUtils.trimToEmpty(body.getMirrorRowId());
        try
        {
            return applyHighConsumeInternal(body, processParty);
        }
        catch (ServiceException e)
        {
            hisMirrorProcessOutcomeRecorder.recordFailure(requireTenant(), visitKind, mirrorRowId, processParty, e.getMessage());
            throw e;
        }
        catch (Exception e)
        {
            String msg = HisMirrorProcessUserMessages.safeFailureMessage(e, "高值核销失败");
            hisMirrorProcessOutcomeRecorder.recordFailure(requireTenant(), visitKind, mirrorRowId, processParty, msg);
            throw new ServiceException(msg);
        }
    }

    private HisMirrorHighApplyResultVo applyHighConsumeInternal(HisMirrorHighApplyBody body, String processParty)
    {
        String visitKind = resolveVisitKind(body == null ? null : body.getVisitKind());
        String mirrorRowId = body == null ? null : StringUtils.trimToEmpty(body.getMirrorRowId());
        if (StringUtils.isEmpty(mirrorRowId) || body.getLines() == null || body.getLines().isEmpty())
        {
            throw new ServiceException("请添加高值消耗明细");
        }
        String tenantId = requireTenant();
        MirrorLine line = loadMirrorLine(tenantId, visitKind, mirrorRowId);
        if (!STATUS_PENDING.equals(line.processStatus) && !STATUS_PARTIAL.equals(line.processStatus))
        {
            throw new ServiceException(HisMirrorProcessUserMessages.highScanNotAllowed(line.processStatus));
        }
        assertSourcesForHigh(listDistinctSources(tenantId, visitKind, mirrorRowId));
        BigDecimal billQty = line.quantity == null ? BigDecimal.ZERO : line.quantity;
        BigDecimal allocatedBefore = nz(hisMirrorConsumeLinkMapper.sumAllocQtyForMirrorRow(tenantId, visitKind, mirrorRowId));
        BigDecimal remaining = billQty.subtract(allocatedBefore);
        if (remaining.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException(HisMirrorProcessUserMessages.fullyConsumed());
        }
        requireMirrorUnitPrice(line);
        FdDepartment dept = resolveDepartment(tenantId, line.deptHisCode);
        FdMaterial mat = resolveMaterial(tenantId, line.chargeItemId, line.itemName);
        assertChargeItemValueLevelForHigh(tenantId, line.chargeItemId, line.itemName, mat);
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
        List<GzTraceabilityEntry> traceEntries = new ArrayList<>();
        String createBy = SecurityUtils.getUserIdStr();
        for (Map.Entry<Long, BigDecimal> en : mergedByGz.entrySet())
        {
            Long gzId = en.getKey();
            BigDecimal lineQty = en.getValue();
            GzDepInventory gz = gzDepInventoryMapper.selectGzDepInventoryById(gzId);
            if (gz == null)
            {
                throw new ServiceException(HisMirrorProcessUserMessages.stockInsufficient(productDisplayName(mat)));
            }
            if (gz.getDepartmentId() == null || !gz.getDepartmentId().equals(dept.getId()))
            {
                throw new ServiceException(HisMirrorProcessUserMessages.wrongDeptScan());
            }
            if (!unitPriceMatches(line.unitPrice, gz.getUnitPrice()))
            {
                throw new ServiceException(HisMirrorProcessUserMessages.priceMismatch(line.unitPrice, gz.getUnitPrice()));
            }
            BigDecimal gq = gz.getQty() == null ? BigDecimal.ZERO : gz.getQty();
            if (gq.compareTo(lineQty) < 0)
            {
                throwInsufficientStock(mat);
            }
            if (gz.getMaterialId() == null || !gz.getMaterialId().equals(mat.getId()))
            {
                throw new ServiceException(HisMirrorProcessUserMessages.materialMismatch());
            }
            traceEntries.add(buildTraceabilityEntryFromGz(gz, lineQty, line.unitPrice, mat, createBy));
        }
        if (applySum.compareTo(remaining) > 0)
        {
            throw new ServiceException(HisMirrorProcessUserMessages.qtyExceedRemaining());
        }
        GzTraceability trace = new GzTraceability();
        trace.setTenantId(tenantId);
        trace.setVisitKind(visitKind);
        trace.setMirrorRowId(mirrorRowId);
        trace.setTraceSource(GzTraceSourceConstants.HIS_MIRROR_HIGH);
        trace.setExecDeptId(dept.getId());
        trace.setApplyDeptId(dept.getId());
        trace.setCreateBy(createBy);
        trace.setRemark("HIS高值扫码核销 mirrorRowId=" + mirrorRowId);
        fillTraceabilityPatientFromMirror(trace, tenantId, visitKind, mirrorRowId, line);
        trace.setTraceabilityEntryList(traceEntries);
        GzTraceability persisted = gzTraceabilityService.insertAuditedMirrorHighBill(trace);
        if (persisted == null || persisted.getId() == null
            || persisted.getTraceabilityEntryList() == null || persisted.getTraceabilityEntryList().isEmpty())
        {
            throw new ServiceException("生成高值计费单失败");
        }
        Date linkTime = DateUtils.getNowDate();
        List<HisMirrorConsumeLink> links = new ArrayList<>();
        for (GzTraceabilityEntry te : persisted.getTraceabilityEntryList())
        {
            if (te == null || te.getId() == null)
            {
                continue;
            }
            HisMirrorConsumeLink lk = new HisMirrorConsumeLink();
            lk.setId(UUID7.generateUUID7());
            lk.setTenantId(tenantId);
            lk.setVisitKind(visitKind);
            lk.setMirrorRowId(mirrorRowId);
            lk.setFetchBatchId(line.fetchBatchId);
            lk.setTraceabilityId(persisted.getId());
            lk.setTraceabilityEntryId(te.getId());
            lk.setAllocQty(te.getQuantity());
            lk.setGzDepInventoryId(te.getInventoryId());
            GzDepInventory gzOne = te.getInventoryId() != null
                ? gzDepInventoryMapper.selectGzDepInventoryById(te.getInventoryId()) : null;
            lk.setInHospitalCode(gzOne != null ? gzOne.getInHospitalCode()
                : (te.getInHospitalCode() != null ? te.getInHospitalCode() : null));
            lk.setReturnedQty(BigDecimal.ZERO);
            lk.setRefundableRemainingQty(te.getQuantity());
            lk.setDelFlag(0);
            lk.setCreateTime(linkTime);
            links.add(lk);
        }
        if (links.isEmpty())
        {
            throw new ServiceException("高值计费明细关联失败");
        }
        flushLinks(links);
        BigDecimal allocatedAfter = nz(hisMirrorConsumeLinkMapper.sumAllocQtyForMirrorRow(tenantId, visitKind, mirrorRowId));
        BigDecimal newRemaining = billQty.subtract(allocatedAfter);
        String newStatus = newRemaining.compareTo(BigDecimal.ZERO) <= 0 ? STATUS_CONSUMED : STATUS_PARTIAL;
        persistProcessSuccess(tenantId, visitKind, mirrorRowId, newStatus, PROC_TYPE_HIGH, processParty);
        HisMirrorHighApplyResultVo vo = new HisMirrorHighApplyResultVo();
        vo.setTraceBillId(persisted.getId());
        vo.setTraceNo(persisted.getTraceNo());
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
        return SecurityUtils.getUserIdStr();
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
                throw new ServiceException(HisMirrorProcessUserMessages.legacyBatchLow());
            }
            if (BILL_HIGH.equalsIgnoreCase(t))
            {
                throw new ServiceException(HisMirrorProcessUserMessages.alreadyHighPath());
            }
            if (BILL_LOW.equalsIgnoreCase(t))
            {
                throw new ServiceException(HisMirrorProcessUserMessages.alreadyLowConsumed());
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
                throw new ServiceException(HisMirrorProcessUserMessages.legacyBatchHigh());
            }
            if (BILL_LOW.equalsIgnoreCase(t))
            {
                throw new ServiceException(HisMirrorProcessUserMessages.alreadyLowCannotHigh());
            }
        }
    }

    private static class MirrorLine
    {
        String id;
        String fetchBatchId;
        String deptHisCode;
        String chargeItemId;
        String itemName;
        BigDecimal quantity;
        String processStatus;
        BigDecimal unitPrice;
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
                throw new ServiceException(HisMirrorProcessUserMessages.chargeNotFound(true));
            }
            ml.fetchBatchId = r.getFetchBatchId();
            ml.deptHisCode = r.getDeptCode();
            ml.chargeItemId = r.getChargeItemId();
            ml.itemName = r.getItemName();
            ml.quantity = r.getQuantity();
            ml.unitPrice = r.getUnitPrice();
            ml.processStatus = r.getProcessStatus();
            return ml;
        }
        HisOutpatientChargeMirror r = hisOutpatientChargeMirrorMapper.selectByIdAndTenant(tenantId, mirrorRowId);
        if (r == null)
        {
            throw new ServiceException(HisMirrorProcessUserMessages.chargeNotFound(false));
        }
        ml.fetchBatchId = r.getFetchBatchId();
        ml.deptHisCode = r.getClinicCode();
        ml.chargeItemId = r.getChargeItemId();
        ml.itemName = r.getItemName();
        ml.quantity = r.getQuantity();
        ml.unitPrice = r.getUnitPrice();
        ml.processStatus = r.getProcessStatus();
        return ml;
    }

    private FdDepartment resolveDepartment(String tenantId, String deptHisCode)
    {
        if (StringUtils.isBlank(deptHisCode))
        {
            throw new ServiceException(HisMirrorProcessUserMessages.deptNotMapped());
        }
        FdDepartment dept = fdDepartmentMapper.selectFdDepartmentByTenantAndHisId(tenantId, HisMatchTextUtils.normalizeMatchKey(deptHisCode));
        if (dept == null || dept.getId() == null)
        {
            throw new ServiceException(HisMirrorProcessUserMessages.deptNotMapped());
        }
        return dept;
    }

    private FdMaterial resolveMaterial(String tenantId, String chargeItemId, String itemName)
    {
        if (StringUtils.isBlank(chargeItemId))
        {
            throw new ServiceException(HisMirrorProcessUserMessages.missingChargeItem());
        }
        String normalizedChargeItemId = HisMatchTextUtils.normalizeMatchKey(chargeItemId);
        FdMaterial mat = fdMaterialMapper.selectFdMaterialByTenantAndHisChargeItemId(
            tenantId, normalizedChargeItemId);
        if (log.isInfoEnabled())
        {
            log.info("HIS计费耗材对照 tenantId={}, chargeItemIdRaw={}, chargeItemIdNorm={}, materialId={}",
                tenantId, chargeItemId, normalizedChargeItemId, mat == null ? null : mat.getId());
        }
        if (mat == null || mat.getId() == null)
        {
            throw new ServiceException(HisMirrorProcessUserMessages.notMapped(chargeItemId, itemName));
        }
        return mat;
    }

    private GzDepInventory findGzByNormalizedCode(String tenantId, FdDepartment dept, FdMaterial mat, String scanCode,
        BigDecimal mirrorUnitPrice)
    {
        GzDepInventory probe = new GzDepInventory();
        probe.setDepartmentId(dept.getId());
        probe.setMaterialId(mat.getId());
        probe.setShowZeroStock(Boolean.TRUE);
        List<GzDepInventory> list = gzDepInventoryMapper.selectGzDepInventoryList(probe);
        if (list == null || list.isEmpty())
        {
            throw new ServiceException(HisMirrorProcessUserMessages.scanCodeNotFound(productDisplayName(mat)));
        }
        String nScan = HisMatchTextUtils.normalizeMatchKey(scanCode);
        GzDepInventory hit = null;
        for (GzDepInventory g : list)
        {
            if (g != null && matchesGzScanCode(g, nScan))
            {
                hit = g;
                break;
            }
        }
        if (hit == null)
        {
            GzDepInventory probeByCode = new GzDepInventory();
            probeByCode.setDepartmentId(dept.getId());
            probeByCode.setInHospitalCode(scanCode);
            probeByCode.setShowZeroStock(Boolean.TRUE);
            List<GzDepInventory> byCode = gzDepInventoryMapper.selectGzDepInventoryList(probeByCode);
            if (byCode != null)
            {
                for (GzDepInventory g : byCode)
                {
                    if (g != null && matchesGzScanCode(g, nScan))
                    {
                        throw new ServiceException(HisMirrorProcessUserMessages.materialMismatch());
                    }
                }
            }
            throw new ServiceException(HisMirrorProcessUserMessages.scanCodeNotFound(productDisplayName(mat)));
        }
        if (!unitPriceMatches(mirrorUnitPrice, hit.getUnitPrice()))
        {
            throw new ServiceException(HisMirrorProcessUserMessages.priceMismatch(mirrorUnitPrice, hit.getUnitPrice()));
        }
        if (hit.getQty() == null || hit.getQty().compareTo(BigDecimal.ZERO) <= 0)
        {
            throwInsufficientStock(mat);
        }
        return hit;
    }

    /** 院内码 / 主条码 / 辅条码 任一匹配即视为扫中 */
    private static boolean matchesGzScanCode(GzDepInventory g, String normalizedScan)
    {
        if (g == null || StringUtils.isBlank(normalizedScan))
        {
            return false;
        }
        if (codeKeyEquals(g.getInHospitalCode(), normalizedScan))
        {
            return true;
        }
        if (codeKeyEquals(g.getMasterBarcode(), normalizedScan))
        {
            return true;
        }
        return codeKeyEquals(g.getSecondaryBarcode(), normalizedScan);
    }

    private static boolean codeKeyEquals(String stored, String normalizedScan)
    {
        return StringUtils.isNotBlank(stored)
            && HisMatchTextUtils.normalizeMatchKey(stored).equals(normalizedScan);
    }

    private void assertChargeItemValueLevelForLow(String tenantId, String chargeItemId, String itemName, FdMaterial mat)
    {
        String level = resolveChargeItemValueLevel(tenantId, chargeItemId, mat);
        if (HisMirrorValueLevelSupport.isUnknown(level))
        {
            throw new ServiceException(HisMirrorProcessUserMessages.valueLevelUnknown(chargeItemId, itemName));
        }
        if (HisMirrorValueLevelSupport.isHighValue(level))
        {
            throw new ServiceException(HisMirrorProcessUserMessages.valueLevelHigh(chargeItemId, itemName));
        }
    }

    private void assertChargeItemValueLevelForHigh(String tenantId, String chargeItemId, String itemName, FdMaterial mat)
    {
        String level = resolveChargeItemValueLevel(tenantId, chargeItemId, mat);
        if (HisMirrorValueLevelSupport.isUnknown(level))
        {
            throw new ServiceException(HisMirrorProcessUserMessages.valueLevelUnknown(chargeItemId, itemName));
        }
        if (HisMirrorValueLevelSupport.isLowValue(level))
        {
            throw new ServiceException(HisMirrorProcessUserMessages.valueLevelLow(chargeItemId, itemName));
        }
    }

    private String resolveChargeItemValueLevel(String tenantId, String chargeItemId, FdMaterial mat)
    {
        if (mat != null)
        {
            return HisMirrorValueLevelSupport.resolveFromMaterial(mat);
        }
        return HisMirrorValueLevelSupport.resolveFromMaterial(fdMaterialMapper, tenantId, chargeItemId);
    }

    private int appendPiecesForLine(String tenantId, String fetchBatchId, String visitKind, String mirrorRowId,
        String deptHisCode, String chargeItemId, String itemName, BigDecimal qty, BigDecimal mirrorUnitPrice,
        List<AllocPiece> out)
    {
        if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0)
        {
            return 1;
        }
        FdDepartment dept = resolveDepartment(tenantId, deptHisCode);
        FdMaterial mat = resolveMaterial(tenantId, chargeItemId, itemName);
        assertChargeItemValueLevelForLow(tenantId, chargeItemId, itemName, mat);
        out.addAll(allocateLine(tenantId, dept, mat, qty, mirrorUnitPrice, mirrorRowId, visitKind, fetchBatchId));
        return 0;
    }

    private List<AllocPiece> allocateLine(String tenantId, FdDepartment dept, FdMaterial mat, BigDecimal need,
        BigDecimal mirrorUnitPrice, String mirrorRowId, String visitKind, String fetchBatchId)
    {
        Long departmentId = dept.getId();
        Long materialId = mat.getId();
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
        List<StkDepInventory> atMirrorPrice = rows.stream()
            .filter(r -> r != null && unitPriceMatches(mirrorUnitPrice, r.getUnitPrice()))
            .collect(Collectors.toList());
        if (atMirrorPrice.isEmpty())
        {
            throwInsufficientStock(mat);
        }
        atMirrorPrice.sort(Comparator
            .comparing(StkDepInventory::getWarehouseDate, Comparator.nullsLast(Date::compareTo))
            .thenComparing(r -> r.getId() == null ? 0L : r.getId()));
        Long chosenWarehouseId = null;
        for (StkDepInventory r : atMirrorPrice)
        {
            if (r.getQty() != null && r.getQty().compareTo(BigDecimal.ZERO) > 0)
            {
                chosenWarehouseId = r.getWarehouseId();
                break;
            }
        }
        boolean hasPositive = atMirrorPrice.stream()
            .anyMatch(r -> r.getQty() != null && r.getQty().compareTo(BigDecimal.ZERO) > 0);
        if (!hasPositive)
        {
            throwInsufficientStock(mat);
        }
        final Long whKey = chosenWarehouseId;
        List<StkDepInventory> filtered = atMirrorPrice.stream()
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
            throwInsufficientStock(mat);
        }
        return pieces;
    }

    private static void requireMirrorUnitPrice(MirrorLine line)
    {
        if (line == null || line.unitPrice == null)
        {
            throw new ServiceException(HisMirrorProcessUserMessages.noUnitPrice(
                line == null ? null : line.chargeItemId,
                line == null ? null : line.itemName));
        }
    }

    /** 计费镜像单价与库存单价一致（均按 4 位小数比较） */
    private static boolean unitPriceMatches(BigDecimal mirrorUnitPrice, BigDecimal stockUnitPrice)
    {
        if (mirrorUnitPrice == null || stockUnitPrice == null)
        {
            return false;
        }
        return mirrorUnitPrice.setScale(4, RoundingMode.HALF_UP)
            .compareTo(stockUnitPrice.setScale(4, RoundingMode.HALF_UP)) == 0;
    }

    private static String productDisplayName(FdMaterial mat)
    {
        if (mat == null)
        {
            return "未知产品";
        }
        return StringUtils.isNotBlank(mat.getName()) ? mat.getName().trim() : String.valueOf(mat.getId());
    }

    private static void throwInsufficientStock(FdMaterial mat)
    {
        throw new ServiceException(HisMirrorProcessUserMessages.stockInsufficient(productDisplayName(mat)));
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

    /**
     * 高值核销完成后写入高值追溯消耗记录（库存已在消耗单审核时扣减）
     */
    private void recordHighConsumeTraceability(String tenantId, String visitKind, String mirrorRowId,
        FdDepartment dept, MirrorLine line, DeptBatchConsume bill, List<DeptBatchConsumeEntry> persistedEntries,
        String createBy)
    {
        if (persistedEntries == null || persistedEntries.isEmpty())
        {
            return;
        }
        GzTraceability trace = new GzTraceability();
        trace.setTenantId(tenantId);
        trace.setExecDeptId(dept.getId());
        trace.setApplyDeptId(dept.getId());
        trace.setCreateBy(createBy);
        trace.setRemark("HIS高值扫码核销 consumeBillNo=" + StringUtils.defaultString(bill.getConsumeBillNo())
            + " mirrorRowId=" + mirrorRowId);
        fillTraceabilityPatientFromMirror(trace, tenantId, visitKind, mirrorRowId, line);
        List<GzTraceabilityEntry> traceEntries = new ArrayList<>();
        for (DeptBatchConsumeEntry pe : persistedEntries)
        {
            if (pe == null || pe.getGzDepInventoryId() == null)
            {
                continue;
            }
            GzDepInventory gz = gzDepInventoryMapper.selectGzDepInventoryById(pe.getGzDepInventoryId());
            if (gz == null)
            {
                continue;
            }
            FdMaterial matDetail = pe.getMaterialId() != null
                ? fdMaterialMapper.selectFdMaterialById(pe.getMaterialId()) : null;
            GzTraceabilityEntry te = buildTraceabilityEntryFromConsume(pe, gz, matDetail, line.unitPrice);
            traceEntries.add(te);
        }
        if (traceEntries.isEmpty())
        {
            return;
        }
        trace.setTraceabilityEntryList(traceEntries);
        gzTraceabilityService.insertConsumedTraceability(trace);
    }

    private void fillTraceabilityPatientFromMirror(GzTraceability trace, String tenantId, String visitKind,
        String mirrorRowId, MirrorLine line)
    {
        if (KIND_IN.equals(visitKind))
        {
            HisInpatientChargeMirror r = hisInpatientChargeMirrorMapper.selectByIdAndTenant(tenantId, mirrorRowId);
            if (r == null)
            {
                return;
            }
            trace.setPatientName(r.getPatientName());
            trace.setHospitalNumber(r.getInpatientNo());
            trace.setChiefSurgeon(r.getDoctorName());
            trace.setSurgeryDate(r.getChargeDate() != null ? r.getChargeDate() : r.getUseDate());
            trace.setAdmissionDiagnosis(StringUtils.defaultIfBlank(r.getItemName(), line.itemName));
            return;
        }
        HisOutpatientChargeMirror r = hisOutpatientChargeMirrorMapper.selectByIdAndTenant(tenantId, mirrorRowId);
        if (r == null)
        {
            return;
        }
        trace.setPatientName(r.getPatientName());
        trace.setHospitalNumber(r.getOutpatientNo());
        trace.setChiefSurgeon(r.getDoctorName());
        trace.setSurgeryDate(parseMirrorChargeDate(r.getChargeDate()));
        trace.setAdmissionDiagnosis(StringUtils.defaultIfBlank(r.getItemName(), line.itemName));
    }

    private static Date parseMirrorChargeDate(String chargeDateRaw)
    {
        if (StringUtils.isBlank(chargeDateRaw))
        {
            return null;
        }
        String normalized = chargeDateRaw.trim().replace('/', '-');
        Date parsed = DateUtils.parseDate(normalized);
        if (parsed != null)
        {
            return parsed;
        }
        if (normalized.length() >= 10)
        {
            return DateUtils.parseDate(normalized.substring(0, 10));
        }
        return null;
    }

    private static GzTraceabilityEntry buildTraceabilityEntryFromGz(GzDepInventory gz, BigDecimal qty,
        BigDecimal mirrorUnitPrice, FdMaterial matDetail, String createBy)
    {
        GzTraceabilityEntry te = new GzTraceabilityEntry();
        te.setMaterialId(gz.getMaterialId());
        te.setInventoryId(gz.getId());
        te.setMaterialName(StringUtils.defaultIfBlank(gz.getMaterialName(),
            matDetail != null ? matDetail.getName() : null));
        te.setSpecification(matDetail != null ? matDetail.getSpeci() : null);
        te.setModel(matDetail != null ? matDetail.getModel() : null);
        if (matDetail != null && matDetail.getFdUnit() != null)
        {
            te.setUnit(matDetail.getFdUnit().getUnitName());
        }
        te.setQuantity(qty);
        BigDecimal chargePrice = mirrorUnitPrice != null ? mirrorUnitPrice : gz.getUnitPrice();
        te.setChargePrice(chargePrice);
        te.setBatchNo(gz.getBatchNo());
        te.setExpiryDate(gz.getEndTime());
        te.setInHospitalCode(gz.getInHospitalCode());
        te.setMasterBarcode(gz.getMasterBarcode());
        te.setSecondaryBarcode(gz.getSecondaryBarcode());
        te.setSupplierId(gz.getSupplierId() != null ? gz.getSupplierId()
            : (matDetail != null ? matDetail.getSupplierId() : null));
        if (matDetail != null)
        {
            if (matDetail.getFdFactory() != null)
            {
                te.setManufacturer(matDetail.getFdFactory().getFactoryName());
            }
            if (matDetail.getSupplier() != null)
            {
                te.setSupplier(matDetail.getSupplier().getName());
            }
            te.setCertificateNo(matDetail.getRegisterNo());
            if (StringUtils.isNotBlank(matDetail.getIsFollow()))
            {
                te.setBillingFollow("1".equals(matDetail.getIsFollow()) ? "1" : "0");
            }
        }
        te.setCreateBy(createBy);
        return te;
    }

    private static GzTraceabilityEntry buildTraceabilityEntryFromConsume(DeptBatchConsumeEntry pe, GzDepInventory gz,
        FdMaterial matDetail, BigDecimal mirrorUnitPrice)
    {
        GzTraceabilityEntry te = new GzTraceabilityEntry();
        te.setMaterialId(pe.getMaterialId());
        te.setInventoryId(pe.getGzDepInventoryId());
        te.setMaterialName(StringUtils.defaultIfBlank(pe.getMaterialName(),
            matDetail != null ? matDetail.getName() : gz.getMaterialName()));
        te.setSpecification(StringUtils.defaultIfBlank(pe.getMaterialSpeci(),
            matDetail != null ? matDetail.getSpeci() : null));
        te.setModel(StringUtils.defaultIfBlank(pe.getMaterialModel(),
            matDetail != null ? matDetail.getModel() : null));
        if (matDetail != null && matDetail.getFdUnit() != null)
        {
            te.setUnit(matDetail.getFdUnit().getUnitName());
        }
        te.setQuantity(pe.getQty());
        BigDecimal chargePrice = pe.getUnitPrice() != null ? pe.getUnitPrice() : mirrorUnitPrice;
        if (chargePrice == null && gz.getUnitPrice() != null)
        {
            chargePrice = gz.getUnitPrice();
        }
        te.setChargePrice(chargePrice);
        te.setBatchNo(gz.getBatchNo());
        te.setExpiryDate(gz.getEndTime());
        te.setInHospitalCode(gz.getInHospitalCode());
        te.setMasterBarcode(gz.getMasterBarcode());
        te.setSecondaryBarcode(gz.getSecondaryBarcode());
        te.setSupplierId(gz.getSupplierId() != null ? gz.getSupplierId()
            : (matDetail != null ? matDetail.getSupplierId() : null));
        if (matDetail != null)
        {
            if (matDetail.getFdFactory() != null)
            {
                te.setManufacturer(matDetail.getFdFactory().getFactoryName());
            }
            if (matDetail.getSupplier() != null)
            {
                te.setSupplier(matDetail.getSupplier().getName());
            }
            te.setCertificateNo(matDetail.getRegisterNo());
            if (StringUtils.isNotBlank(matDetail.getIsFollow()))
            {
                te.setBillingFollow("1".equals(matDetail.getIsFollow()) ? "1" : "0");
            }
        }
        te.setCreateBy(pe.getCreateBy());
        return te;
    }

    private void persistProcessSuccess(String tenantId, String visitKind, String mirrorRowId, String processStatus,
        String processType, String processParty)
    {
        if (StringUtils.isAnyEmpty(tenantId, visitKind, mirrorRowId))
        {
            return;
        }
        List<String> ids = java.util.Collections.singletonList(mirrorRowId);
        Date procTime = DateUtils.getNowDate();
        String procBy = currentMirrorProcessBy();
        String situation = HisMirrorProcessConstants.RESULT_SUCCESS;
        String party = HisMirrorProcessConstants.resolveParty(processParty);
        if (KIND_IN.equals(visitKind))
        {
            hisInpatientChargeMirrorMapper.updateMirrorProcessByIds(tenantId, ids, processStatus, processType, procTime,
                procBy, situation, party);
        }
        else
        {
            hisOutpatientChargeMirrorMapper.updateMirrorProcessByIds(tenantId, ids, processStatus, processType, procTime,
                procBy, situation, party);
        }
        hisPatientChargeMirrorUnifiedMapper.updateMirrorProcessByIds(tenantId, ids, processStatus, processType, procTime,
            procBy, situation, party);
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

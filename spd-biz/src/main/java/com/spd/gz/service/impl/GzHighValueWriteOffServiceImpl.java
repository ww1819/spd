package com.spd.gz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.department.domain.DeptBatchConsume;
import com.spd.department.domain.DeptBatchConsumeReverseReq;
import com.spd.department.service.IDeptBatchConsumeService;
import com.spd.gz.domain.GzDepInventory;
import com.spd.gz.domain.GzTraceability;
import com.spd.gz.domain.GzTraceabilityEntry;
import com.spd.gz.domain.dto.GzHighChargeConfirmResultVo;
import com.spd.gz.domain.dto.GzHighValueWriteOffBody;
import com.spd.gz.domain.dto.GzHighValueWriteOffResultVo;
import com.spd.gz.domain.dto.GzInstantIoReverseBody;
import com.spd.gz.mapper.GzDepInventoryMapper;
import com.spd.gz.service.IGzHighValueWriteOffService;
import com.spd.gz.service.IGzInstantIoService;
import com.spd.gz.service.IGzTraceabilityService;
import com.spd.hc.service.IHcBarcodeLifecycleService;
import com.spd.his.domain.HisMirrorConsumeLink;
import com.spd.his.mapper.HisInpatientChargeMirrorMapper;
import com.spd.his.mapper.HisMirrorConsumeLinkMapper;
import com.spd.his.mapper.HisOutpatientChargeMirrorMapper;
import com.spd.his.mapper.HisPatientChargeMirrorUnifiedMapper;

/**
 * 高值冲销（整单）：按正向计费单冲销全部明细并回补科室库存；已即入即出审核的明细另建 301/401（仅本单明细）；
 * 消耗侧另建负数反向计费单（HV-F-006/HV-F-007）。
 * 冲销不等于作废：不软删原正向计费单；确认/即入即出状态保留，冲销状态单独留痕。
 */
@Service
public class GzHighValueWriteOffServiceImpl implements IGzHighValueWriteOffService
{
    private static final String KIND_IN = "INPATIENT";
    private static final String STATUS_PENDING = "PENDING_CONSUME";
    private static final String PROC_TYPE_EMPTY = "";
    private static final String SOURCE_CONFIRM = "CONFIRM";
    private static final String SOURCE_HIGH_SCAN = "HIGH_SCAN";
    private static final String MSG_CONTACT_WAREHOUSE =
        "此单存在已确认或已即入即出的明细，请联系库房人员在「高值即入即出」界面进行冲销操作";

    @Autowired
    private HisMirrorConsumeLinkMapper hisMirrorConsumeLinkMapper;
    @Autowired
    private GzDepInventoryMapper gzDepInventoryMapper;
    @Autowired
    private IGzTraceabilityService gzTraceabilityService;
    @Autowired
    private IHcBarcodeLifecycleService hcBarcodeLifecycleService;
    @Autowired
    private IDeptBatchConsumeService deptBatchConsumeService;
    @Lazy
    @Autowired
    private IGzInstantIoService gzInstantIoService;
    @Autowired
    private HisInpatientChargeMirrorMapper hisInpatientChargeMirrorMapper;
    @Autowired
    private HisOutpatientChargeMirrorMapper hisOutpatientChargeMirrorMapper;
    @Autowired
    private HisPatientChargeMirrorUnifiedMapper hisPatientChargeMirrorUnifiedMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GzHighValueWriteOffResultVo writeOff(GzHighValueWriteOffBody body)
    {
        boolean noLinks = body == null || body.getLinkIds() == null || body.getLinkIds().isEmpty();
        boolean noTraces = body == null || body.getTraceabilityIds() == null || body.getTraceabilityIds().isEmpty();
        if (noLinks && noTraces)
        {
            throw new ServiceException("请选择要冲销的高值核销单");
        }
        String tenantId = requireTenant();
        String operator = SecurityUtils.getUserIdStr();
        String remark = StringUtils.defaultIfBlank(body.getRemark(), "高值核销冲销");
        List<String> linkIds = noLinks ? new ArrayList<>() : body.getLinkIds().stream()
            .filter(StringUtils::isNotBlank).map(String::trim).distinct().collect(Collectors.toList());

        String source = StringUtils.trimToEmpty(body.getSource()).toUpperCase();
        // 确认页 / 住院高值扫码页：仅允许整单全部未确认（档 A）
        boolean clinicalEntry = SOURCE_CONFIRM.equals(source) || SOURCE_HIGH_SCAN.equals(source);

        List<HisMirrorConsumeLink> links = loadWholeTraceLinks(tenantId, linkIds, body.getTraceabilityIds());
        for (HisMirrorConsumeLink lk : links)
        {
            if (lk.getGzDepInventoryId() == null && lk.getTraceabilityId() == null)
            {
                throw new ServiceException("非高值核销关联，无法高值冲销：" + lk.getId());
            }
            if (refundableOnLink(lk).compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException("明细已无剩余可冲销量：" + lk.getId());
            }
            if (clinicalEntry)
            {
                int confirmStatus = lk.getConfirmStatus() == null ? 0 : lk.getConfirmStatus();
                int ioStatus = lk.getInstantIoAuditStatus() == null ? 0 : lk.getInstantIoAuditStatus();
                if (confirmStatus == 1 || ioStatus == 1 || ioStatus == 2)
                {
                    throw new ServiceException(MSG_CONTACT_WAREHOUSE);
                }
            }
        }

        GzHighValueWriteOffResultVo vo = new GzHighValueWriteOffResultVo();
        vo.setLinkCount(links.size());
        // 档 C（方案 X）：仅对本单已即入即出审核的明细生成 301/401，同确认批次其他核销单不动
        List<String> auditedLinkIds = links.stream()
            .filter(l -> Integer.valueOf(1).equals(l.getConfirmStatus())
                && Integer.valueOf(1).equals(l.getInstantIoAuditStatus()))
            .map(HisMirrorConsumeLink::getId)
            .collect(Collectors.toList());
        if (!auditedLinkIds.isEmpty())
        {
            if (clinicalEntry)
            {
                throw new ServiceException(MSG_CONTACT_WAREHOUSE);
            }
            GzInstantIoReverseBody revBody = new GzInstantIoReverseBody();
            revBody.setLinkIds(auditedLinkIds);
            GzHighChargeConfirmResultVo revResult = gzInstantIoService.reverse(revBody);
            vo.setReverseBillPairCount(revResult == null ? 0 : revResult.getBills().size());
            vo.getMessages().add("已为 " + auditedLinkIds.size() + " 条已即入即出明细生成退货/退库单");
        }

        Date writeOffTime = DateUtils.getNowDate();
        Set<String> mirrorKeys = new LinkedHashSet<>();
        // 原计费单ID → (原明细ID → 冲销数量)，用于生成反向计费单
        Map<Long, Map<Long, BigDecimal>> reverseByTrace = new LinkedHashMap<>();
        Map<Long, String> entryLinkIds = new LinkedHashMap<>();
        Map<Long, List<String>> linkIdsByTrace = new LinkedHashMap<>();
        List<String> legacyLinkIds = new ArrayList<>();

        for (HisMirrorConsumeLink lk : links)
        {
            BigDecimal rem = refundableOnLink(lk);
            // 回补为冲销固有步骤：同库存行另有核销时也照常回补；数量异常应暴露以便排查，禁止静默跳过
            restoreInventory(lk, rem, remark);
            hisMirrorConsumeLinkMapper.markLinkFullyReturnedById(tenantId, lk.getId(), operator);
            if (lk.getTraceabilityId() != null && lk.getTraceabilityEntryId() != null)
            {
                reverseByTrace
                    .computeIfAbsent(lk.getTraceabilityId(), k -> new LinkedHashMap<>())
                    .merge(lk.getTraceabilityEntryId(), rem, BigDecimal::add);
                entryLinkIds.put(lk.getTraceabilityEntryId(), lk.getId());
                linkIdsByTrace.computeIfAbsent(lk.getTraceabilityId(), k -> new ArrayList<>()).add(lk.getId());
            }
            else
            {
                legacyLinkIds.add(lk.getId());
            }
            vo.setRestoredCount(vo.getRestoredCount() + 1);
            if (StringUtils.isNotBlank(lk.getVisitKind()) && StringUtils.isNotBlank(lk.getMirrorRowId()))
            {
                mirrorKeys.add(lk.getVisitKind().trim().toUpperCase() + "|" + lk.getMirrorRowId().trim());
            }
        }

        // 确认/即入即出状态保持原样（反向明细据此继承）；冲销留痕后软删关联，退出确认/即入即出队列与净核销量统计
        int reverseBillCount = 0;
        for (Map.Entry<Long, Map<Long, BigDecimal>> en : reverseByTrace.entrySet())
        {
            GzTraceability original = gzTraceabilityService.selectGzTraceabilityById(en.getKey());
            if (original == null)
            {
                throw new ServiceException("冲销反向单失败：原计费单不存在 id=" + en.getKey());
            }
            GzTraceability rev = gzTraceabilityService.insertReverseWriteOffBill(original, en.getValue(), entryLinkIds, remark);
            if (rev == null || rev.getId() == null)
            {
                throw new ServiceException("冲销反向单生成失败：原计费单 " + StringUtils.defaultString(original.getTraceNo()));
            }
            reverseBillCount++;
            for (String linkId : linkIdsByTrace.getOrDefault(en.getKey(), java.util.Collections.emptyList()))
            {
                stampAndRelease(tenantId, linkId, operator, writeOffTime, rev.getTraceNo());
            }
            vo.getMessages().add("已生成反向计费单 " + rev.getTraceNo() + "（冲销原单 "
                + StringUtils.defaultString(original.getTraceNo()) + "）");
        }
        for (String linkId : legacyLinkIds)
        {
            stampAndRelease(tenantId, linkId, operator, writeOffTime, null);
        }

        for (String key : mirrorKeys)
        {
            int p = key.indexOf('|');
            String visitKind = key.substring(0, p);
            String mirrorRowId = key.substring(p + 1);
            BigDecimal net = nz(hisMirrorConsumeLinkMapper.sumActiveNetAllocQtyForMirrorRow(tenantId, visitKind, mirrorRowId));
            if (net.compareTo(BigDecimal.ZERO) <= 0)
            {
                resetMirrorToPending(tenantId, visitKind, mirrorRowId);
                vo.setMirrorResetCount(vo.getMirrorResetCount() + 1);
            }
        }
        if (reverseBillCount > 0)
        {
            vo.getMessages().add("冲销完成：已回补科室库存，并生成 " + reverseBillCount
                + " 张反向计费单；计费行可重新核销（不影响即入即出）");
        }
        else
        {
            vo.getMessages().add("冲销完成：已回补科室库存，计费行可重新核销");
        }
        return vo;
    }

    /**
     * 整单冲销：所选明细按正向计费单展开为该单全部有效明细（行锁）；历史无计费单的关联按单条处理。
     */
    private List<HisMirrorConsumeLink> loadWholeTraceLinks(String tenantId, List<String> linkIds, List<Long> traceabilityIds)
    {
        Set<Long> traceIds = new LinkedHashSet<>();
        if (traceabilityIds != null)
        {
            traceabilityIds.stream().filter(java.util.Objects::nonNull).forEach(traceIds::add);
        }
        List<HisMirrorConsumeLink> legacy = new ArrayList<>();
        for (String id : linkIds)
        {
            HisMirrorConsumeLink lk = hisMirrorConsumeLinkMapper.selectById(tenantId, id);
            if (lk == null)
            {
                throw new ServiceException("消耗关联不存在或已冲销，请刷新后重试");
            }
            if (lk.getTraceabilityId() != null)
            {
                traceIds.add(lk.getTraceabilityId());
            }
            else
            {
                legacy.add(lk);
            }
        }
        List<HisMirrorConsumeLink> out = new ArrayList<>();
        for (Long traceId : traceIds)
        {
            List<HisMirrorConsumeLink> whole = hisMirrorConsumeLinkMapper.selectActiveLinksByTraceabilityIdForUpdate(tenantId, traceId);
            if (whole == null || whole.isEmpty())
            {
                throw new ServiceException("核销单已冲销或不存在，请刷新后重试");
            }
            out.addAll(whole);
        }
        out.addAll(legacy);
        if (out.isEmpty())
        {
            throw new ServiceException("请选择要冲销的高值核销明细");
        }
        return out;
    }

    private void stampAndRelease(String tenantId, String linkId, String operator, Date writeOffTime, String reverseNo)
    {
        if (hisMirrorConsumeLinkMapper.markLinkWrittenOff(tenantId, linkId, operator, writeOffTime, reverseNo) != 1)
        {
            throw new ServiceException("明细已被他人冲销，请刷新后重试");
        }
        hisMirrorConsumeLinkMapper.softDeleteLinkById(tenantId, linkId, operator);
    }

    private void restoreInventory(HisMirrorConsumeLink lk, BigDecimal returnQty, String remark)
    {
        if (lk.getTraceabilityId() != null && lk.getTraceabilityEntryId() != null)
        {
            restoreTracePath(lk, returnQty);
            return;
        }
        if (lk.getDeptBatchConsumeId() != null && lk.getDeptBatchConsumeEntryId() != null)
        {
            restoreLegacyPath(lk, returnQty, remark);
            return;
        }
        // 仅有 gz 库存引用时直接加回
        if (lk.getGzDepInventoryId() != null)
        {
            GzDepInventory gz = gzDepInventoryMapper.selectGzDepInventoryById(lk.getGzDepInventoryId());
            if (gz == null)
            {
                throw new ServiceException("高值科室库存不存在，无法冲销");
            }
            gz.setQty(nz(gz.getQty()).add(returnQty));
            gz.setAmt(nz(gz.getUnitPrice()).multiply(gz.getQty()));
            gzDepInventoryMapper.updateGzDepInventory(gz);
            return;
        }
        throw new ServiceException("消耗关联缺少追溯/库存信息，无法冲销");
    }

    private void restoreTracePath(HisMirrorConsumeLink lk, BigDecimal returnQty)
    {
        GzTraceability trace = gzTraceabilityService.selectGzTraceabilityById(lk.getTraceabilityId());
        if (trace == null || trace.getTraceabilityEntryList() == null)
        {
            throw new ServiceException("高值计费单不存在，无法冲销");
        }
        GzTraceabilityEntry entry = trace.getTraceabilityEntryList().stream()
            .filter(e -> e != null && lk.getTraceabilityEntryId().equals(e.getId()))
            .findFirst()
            .orElse(null);
        if (entry == null)
        {
            throw new ServiceException("高值计费明细不存在，无法冲销");
        }
        Long gzId = lk.getGzDepInventoryId() != null ? lk.getGzDepInventoryId() : entry.getInventoryId();
        if (gzId == null)
        {
            throw new ServiceException("缺少高值库存关联，无法冲销");
        }
        GzDepInventory gz = gzDepInventoryMapper.selectGzDepInventoryById(gzId);
        if (gz == null)
        {
            throw new ServiceException("高值科室库存不存在，无法冲销");
        }
        hcBarcodeLifecycleService.onMirrorHighChargeRefund(trace, entry, gz, returnQty);
    }

    private void restoreLegacyPath(HisMirrorConsumeLink lk, BigDecimal returnQty, String remark)
    {
        DeptBatchConsumeReverseReq.ReverseItem it = new DeptBatchConsumeReverseReq.ReverseItem();
        it.setSrcConsumeEntryId(lk.getDeptBatchConsumeEntryId());
        it.setReverseQty(returnQty);
        DeptBatchConsumeReverseReq req = new DeptBatchConsumeReverseReq();
        req.setConsumeId(lk.getDeptBatchConsumeId());
        req.setItems(java.util.Collections.singletonList(it));
        req.setRemark(remark + " link=" + lk.getId());
        DeptBatchConsume rev = deptBatchConsumeService.reverseConsumeForBillingRefund(req, SecurityUtils.getUserIdStr());
        if (rev == null || rev.getId() == null)
        {
            throw new ServiceException("历史高值消耗反消耗失败");
        }
    }

    private void resetMirrorToPending(String tenantId, String visitKind, String mirrorRowId)
    {
        Date procTime = DateUtils.getNowDate();
        String procBy = SecurityUtils.getUserIdStr();
        List<String> ids = java.util.Collections.singletonList(mirrorRowId);
        // processSituation/processParty 传空串以清空「核销成功」等旧文案（null 不会更新列）
        if (KIND_IN.equals(visitKind))
        {
            hisInpatientChargeMirrorMapper.updateMirrorProcessByIds(tenantId, ids,
                STATUS_PENDING, PROC_TYPE_EMPTY, procTime, procBy, "", "");
        }
        else
        {
            hisOutpatientChargeMirrorMapper.updateMirrorProcessByIds(tenantId, ids,
                STATUS_PENDING, PROC_TYPE_EMPTY, procTime, procBy, "", "");
        }
        hisPatientChargeMirrorUnifiedMapper.updateMirrorProcessByIds(tenantId, ids,
            STATUS_PENDING, PROC_TYPE_EMPTY, procTime, procBy, "", "");
    }

    private static BigDecimal refundableOnLink(HisMirrorConsumeLink lk)
    {
        if (lk.getRefundableRemainingQty() != null)
        {
            return lk.getRefundableRemainingQty().max(BigDecimal.ZERO);
        }
        return nz(lk.getAllocQty()).subtract(nz(lk.getReturnedQty())).max(BigDecimal.ZERO);
    }

    private static BigDecimal nz(BigDecimal v)
    {
        return v == null ? BigDecimal.ZERO : v;
    }

    private String requireTenant()
    {
        String tenantId = SecurityUtils.getCustomerId();
        if (StringUtils.isBlank(tenantId))
        {
            throw new ServiceException("无法解析当前租户");
        }
        return tenantId.trim();
    }
}

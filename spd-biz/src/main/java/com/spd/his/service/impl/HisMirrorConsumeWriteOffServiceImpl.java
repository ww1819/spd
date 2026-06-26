package com.spd.his.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.department.domain.DeptBatchConsume;
import com.spd.department.domain.DeptBatchConsumeEntry;
import com.spd.department.domain.DeptBatchConsumeReverseReq;
import com.spd.department.domain.StkDepInventory;
import com.spd.department.mapper.DeptBatchConsumeMapper;
import com.spd.department.mapper.StkDepInventoryMapper;
import com.spd.department.service.IDeptBatchConsumeService;
import com.spd.his.constants.HisBillingTenantConstants;
import com.spd.his.domain.HisBillingRefundOrder;
import com.spd.his.domain.HisBillingRefundOrderLine;
import com.spd.his.domain.HisInpatientChargeMirror;
import com.spd.his.domain.HisMirrorConsumeLink;
import com.spd.his.domain.HisOutpatientChargeMirror;
import com.spd.his.domain.dto.HisMirrorWriteOffBody;
import com.spd.his.domain.dto.HisMirrorWriteOffResultVo;
import com.spd.his.mapper.HisBillingRefundOrderLineMapper;
import com.spd.his.mapper.HisBillingRefundOrderMapper;
import com.spd.his.mapper.HisInpatientChargeMirrorMapper;
import com.spd.his.mapper.HisMirrorConsumeLinkMapper;
import com.spd.his.mapper.HisOutpatientChargeMirrorMapper;
import com.spd.his.mapper.HisPatientChargeMirrorUnifiedMapper;
import com.spd.his.service.IHisMirrorConsumeWriteOffService;
import com.spd.his.service.refund.LvRefundConsumeLinkOrderStrategy;

@Service
public class HisMirrorConsumeWriteOffServiceImpl implements IHisMirrorConsumeWriteOffService
{
    private static final String KIND_IN = "INPATIENT";
    private static final String STATUS_PENDING = "PENDING_CONSUME";
    private static final String STATUS_CONSUMED = "CONSUMED";
    private static final String STATUS_REFUNDED = "REFUNDED";
    private static final String PROC_TYPE_LOW = "LOW_VALUE";
    private static final String PROC_TYPE_HIGH = "HIGH_VALUE";
    private static final String PROC_TYPE_EMPTY = "";
    private static final String REFUND_ORDER_REVERSED = "REVERSED";

    @Autowired
    private HisInpatientChargeMirrorMapper hisInpatientChargeMirrorMapper;
    @Autowired
    private HisOutpatientChargeMirrorMapper hisOutpatientChargeMirrorMapper;
    @Autowired
    private HisMirrorConsumeLinkMapper hisMirrorConsumeLinkMapper;
    @Autowired
    private HisPatientChargeMirrorUnifiedMapper hisPatientChargeMirrorUnifiedMapper;
    @Autowired
    private HisBillingRefundOrderMapper hisBillingRefundOrderMapper;
    @Autowired
    private HisBillingRefundOrderLineMapper hisBillingRefundOrderLineMapper;
    @Autowired
    private IDeptBatchConsumeService deptBatchConsumeService;
    @Autowired
    private DeptBatchConsumeMapper deptBatchConsumeMapper;
    @Autowired
    private StkDepInventoryMapper stkDepInventoryMapper;
    @Autowired
    private LvRefundConsumeLinkOrderStrategy lvRefundConsumeLinkOrderStrategy;

    private void assertHengsuiTenant()
    {
        if (!HisBillingTenantConstants.TENANT_HENGSHUI_THIRD.equals(SecurityUtils.getCustomerId()))
        {
            throw new ServiceException("该功能仅对衡水三院租户开放");
        }
    }

    private String resolveVisitKind(String raw)
    {
        if (StringUtils.isBlank(raw))
        {
            throw new ServiceException("请指定 visitKind（INPATIENT/OUTPATIENT）");
        }
        String v = raw.trim().toUpperCase();
        if (!KIND_IN.equals(v) && !"OUTPATIENT".equals(v))
        {
            throw new ServiceException("visitKind 仅支持 INPATIENT 或 OUTPATIENT");
        }
        return v;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HisMirrorWriteOffResultVo processLowValueWriteOff(HisMirrorWriteOffBody body)
    {
        assertHengsuiTenant();
        if (body == null || StringUtils.isBlank(body.getMirrorRowId()))
        {
            throw new ServiceException("请指定 mirrorRowId");
        }
        String tenantId = SecurityUtils.getCustomerId();
        String visitKind = resolveVisitKind(body.getVisitKind());
        String mirrorRowId = body.getMirrorRowId().trim();
        MirrorSnapshot snap = loadMirrorSnapshot(tenantId, visitKind, mirrorRowId);
        assertLowValueMirror(snap);

        HisMirrorWriteOffResultVo vo = new HisMirrorWriteOffResultVo();
        vo.setMirrorRowId(mirrorRowId);
        String operator = SecurityUtils.getUserIdStr();
        String remark = StringUtils.defaultIfBlank(body.getRemark(), "HIS计费低值冲销");

        if (snap.isRefundRow())
        {
            writeOffSingleRefundMirror(tenantId, visitKind, snap, remark, operator, vo);
        }
        else
        {
            writeOffChargeMirrorWithLinkedRefunds(tenantId, visitKind, snap, remark, operator, vo);
        }
        vo.getMessages().add("冲销完成，镜像行已恢复为待处理");
        return vo;
    }

    private void writeOffChargeMirrorWithLinkedRefunds(String tenantId, String visitKind, MirrorSnapshot charge,
        String remark, String operator, HisMirrorWriteOffResultVo vo)
    {
        if (!STATUS_CONSUMED.equals(charge.processStatus))
        {
            throw new ServiceException("仅「已处理」状态的计费行可冲销，当前：" + charge.processStatus);
        }
        if (!PROC_TYPE_LOW.equals(StringUtils.trimToEmpty(charge.processType)))
        {
            throw new ServiceException("仅低值核销的计费行可冲销");
        }
        String hisChargeId = charge.hisChargeId;
        if (StringUtils.isBlank(hisChargeId))
        {
            throw new ServiceException("计费行缺少 HIS 费用明细主键，无法关联退费数据");
        }
        List<MirrorSnapshot> linkedRefunds = listLinkedRefundMirrors(tenantId, visitKind, hisChargeId);
        for (MirrorSnapshot refund : linkedRefunds)
        {
            if (STATUS_REFUNDED.equals(refund.processStatus))
            {
                undoBillingRefundForMirror(tenantId, visitKind, refund, remark, operator, vo);
                vo.setRelatedRefundWriteOffCount(vo.getRelatedRefundWriteOffCount() + 1);
            }
            else if (STATUS_CONSUMED.equals(refund.processStatus)
                && PROC_TYPE_LOW.equals(StringUtils.trimToEmpty(refund.processType)))
            {
                reverseLowValueConsumesForMirror(tenantId, visitKind, refund.id, remark, operator, vo);
                resetMirrorToPending(tenantId, visitKind, refund.id);
                vo.setRelatedRefundWriteOffCount(vo.getRelatedRefundWriteOffCount() + 1);
                vo.getMessages().add("关联退费行「" + refund.id + "」已冲销");
            }
        }
        reverseLowValueConsumesForMirror(tenantId, visitKind, charge.id, remark, operator, vo);
        resetMirrorToPending(tenantId, visitKind, charge.id);
    }

    private void writeOffSingleRefundMirror(String tenantId, String visitKind, MirrorSnapshot refund,
        String remark, String operator, HisMirrorWriteOffResultVo vo)
    {
        if (STATUS_REFUNDED.equals(refund.processStatus))
        {
            undoBillingRefundForMirror(tenantId, visitKind, refund, remark, operator, vo);
            resetMirrorToPending(tenantId, visitKind, refund.id);
            return;
        }
        if (STATUS_CONSUMED.equals(refund.processStatus)
            && PROC_TYPE_LOW.equals(StringUtils.trimToEmpty(refund.processType)))
        {
            reverseLowValueConsumesForMirror(tenantId, visitKind, refund.id, remark, operator, vo);
            resetMirrorToPending(tenantId, visitKind, refund.id);
            return;
        }
        throw new ServiceException("当前退费行状态不支持冲销：" + refund.processStatus);
    }

    private void undoBillingRefundForMirror(String tenantId, String visitKind, MirrorSnapshot refund,
        String remark, String operator, HisMirrorWriteOffResultVo vo)
    {
        if (!STATUS_REFUNDED.equals(refund.processStatus))
        {
            return;
        }
        HisBillingRefundOrder order = hisBillingRefundOrderMapper.selectDoneByRefundMirrorRowId(tenantId, refund.id);
        if (order == null)
        {
            throw new ServiceException("未找到已完成的退费返还单，无法冲销退费行「" + refund.id + "」");
        }
        List<HisBillingRefundOrderLine> lines = hisBillingRefundOrderLineMapper.selectByRefundOrderId(tenantId, order.getId());
        if (lines == null || lines.isEmpty())
        {
            throw new ServiceException("退费返还单无明细，无法冲销");
        }
        String uid = SecurityUtils.getUserIdStr();
        for (HisBillingRefundOrderLine line : lines)
        {
            if (line == null || line.getReturnQty() == null || line.getReturnQty().compareTo(BigDecimal.ZERO) <= 0
                || StringUtils.isBlank(line.getConsumeLinkId()))
            {
                continue;
            }
            HisMirrorConsumeLink lk = hisMirrorConsumeLinkMapper.selectById(tenantId, line.getConsumeLinkId());
            if (lk == null)
            {
                throw new ServiceException("退费关联消耗行不存在：" + line.getConsumeLinkId());
            }
            Long reapplyBillId = reapplyLowValueConsumeForLink(tenantId, lk, line.getReturnQty(), refund.id, remark, operator);
            if (reapplyBillId != null)
            {
                vo.getReapplyConsumeBillIds().add(reapplyBillId);
            }
            hisMirrorConsumeLinkMapper.decreaseReturnedQtyById(line.getConsumeLinkId(), line.getReturnQty(), uid);
        }
        hisBillingRefundOrderMapper.updateProcessStatus(tenantId, order.getId(), REFUND_ORDER_REVERSED,
            "计费冲销撤销退费返还", uid);
        resetMirrorToPending(tenantId, visitKind, refund.id);
        vo.getMessages().add("退费返还行「" + refund.id + "」已撤销并恢复待处理");
    }

    private Long reapplyLowValueConsumeForLink(String tenantId, HisMirrorConsumeLink lk, BigDecimal qty,
        String refundMirrorRowId, String remark, String operator)
    {
        if (lk.getDepInventoryId() == null)
        {
            throw new ServiceException("消耗关联缺少低值库存行，无法撤销退费返还");
        }
        StkDepInventory inv = stkDepInventoryMapper.selectStkDepInventoryById(lk.getDepInventoryId());
        if (inv == null)
        {
            throw new ServiceException("科室库存不存在：" + lk.getDepInventoryId());
        }
        if (inv.getQty() == null || inv.getQty().compareTo(qty) < 0)
        {
            throw new ServiceException(String.format("科室库存不足，无法撤销退费返还（需扣减 %s，当前 %s）", qty, inv.getQty()));
        }
        Long consumeId = lk.getDeptBatchConsumeId();
        if (consumeId == null)
        {
            throw new ServiceException("消耗关联缺少来源消耗单");
        }
        DeptBatchConsume srcBill = deptBatchConsumeMapper.selectDeptBatchConsumeById(consumeId);
        if (srcBill == null)
        {
            throw new ServiceException("来源消耗单不存在");
        }
        DeptBatchConsumeEntry e = buildReapplyEntry(inv, qty, refundMirrorRowId, srcBill.getDepartmentId());
        DeptBatchConsume bill = new DeptBatchConsume();
        bill.setDepartmentId(srcBill.getDepartmentId());
        bill.setWarehouseId(srcBill.getWarehouseId());
        bill.setUserId(SecurityUtils.getUserId());
        bill.setConsumeBillDate(DateUtils.getNowDate());
        bill.setRemark(StringUtils.defaultIfBlank(remark, "HIS计费冲销-撤销退费返还") + " refundMirror=" + refundMirrorRowId);
        bill.setBillSource("HIS_MIRROR_WRITEOFF_UNDO_REFUND");
        bill.setDisallowReverse(1);
        bill.setCreateBy(operator);
        bill.setDisableEntryDedup(Boolean.TRUE);
        bill.setDeptBatchConsumeEntryList(java.util.Collections.singletonList(e));
        deptBatchConsumeService.insertDeptBatchConsume(bill);
        deptBatchConsumeService.auditConsume(String.valueOf(bill.getId()), operator);
        return bill.getId();
    }

    private DeptBatchConsumeEntry buildReapplyEntry(StkDepInventory inv, BigDecimal qty, String mirrorRowId, Long departmentId)
    {
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
        e.setQty(qty);
        BigDecimal up = inv.getUnitPrice() != null ? inv.getUnitPrice() : BigDecimal.ZERO;
        e.setUnitPrice(up);
        e.setPrice(up);
        e.setAmt(up.multiply(qty));
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
        e.setRemark("HIS冲销撤销退费:" + mirrorRowId);
        e.setCreateBy(SecurityUtils.getUserIdStr());
        return e;
    }

    private void reverseLowValueConsumesForMirror(String tenantId, String visitKind, String mirrorRowId,
        String remark, String operator, HisMirrorWriteOffResultVo vo)
    {
        List<HisMirrorConsumeLink> links = hisMirrorConsumeLinkMapper.selectAllLowValueLinksForMirror(
            tenantId, visitKind, mirrorRowId);
        if (links == null || links.isEmpty())
        {
            throw new ServiceException("未找到可冲销的低值消耗关联");
        }
        List<HisMirrorConsumeLink> sorted = lvRefundConsumeLinkOrderStrategy.sortForRefund(links);
        Map<Long, List<DeptBatchConsumeReverseReq.ReverseItem>> byConsume = new LinkedHashMap<>();
        Map<Long, Map<Long, BigDecimal>> canCache = new HashMap<>();
        for (HisMirrorConsumeLink lk : sorted)
        {
            if (lk == null || lk.getDeptBatchConsumeId() == null || lk.getDeptBatchConsumeEntryId() == null)
            {
                continue;
            }
            BigDecimal linkRem = refundableOnLink(lk);
            if (linkRem.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            Long consumeId = lk.getDeptBatchConsumeId();
            Long entryId = lk.getDeptBatchConsumeEntryId();
            Map<Long, BigDecimal> canByEntry = canCache.computeIfAbsent(consumeId, this::loadCanReverseByEntry);
            BigDecimal canEntry = canByEntry.get(entryId);
            if (canEntry == null || canEntry.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            BigDecimal take = linkRem.min(canEntry);
            if (take.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            DeptBatchConsumeReverseReq.ReverseItem it = new DeptBatchConsumeReverseReq.ReverseItem();
            it.setSrcConsumeEntryId(entryId);
            it.setReverseQty(take);
            byConsume.computeIfAbsent(consumeId, k -> new ArrayList<>()).add(it);
            canByEntry.put(entryId, canEntry.subtract(take));
        }
        if (byConsume.isEmpty())
        {
            throw new ServiceException("当前行无可反消耗数量，无法冲销");
        }
        for (Map.Entry<Long, List<DeptBatchConsumeReverseReq.ReverseItem>> en : byConsume.entrySet())
        {
            DeptBatchConsumeReverseReq req = new DeptBatchConsumeReverseReq();
            req.setConsumeId(en.getKey());
            req.setItems(mergeReverseItems(en.getValue()));
            req.setRemark(remark + " mirrorRow=" + mirrorRowId);
            DeptBatchConsume rev = deptBatchConsumeService.reverseConsumeForBillingRefund(req, operator);
            if (rev != null && rev.getId() != null)
            {
                vo.getReverseConsumeBillIds().add(rev.getId());
            }
        }
        hisMirrorConsumeLinkMapper.resetReturnedQtyForMirror(tenantId, visitKind, mirrorRowId, operator);
    }

    private void resetMirrorToPending(String tenantId, String visitKind, String mirrorRowId)
    {
        Date procTime = DateUtils.getNowDate();
        String procBy = SecurityUtils.getUserIdStr();
        List<String> ids = java.util.Collections.singletonList(mirrorRowId);
        if (KIND_IN.equals(visitKind))
        {
            hisInpatientChargeMirrorMapper.updateMirrorProcessByIds(tenantId, ids,
                STATUS_PENDING, PROC_TYPE_EMPTY, procTime, procBy, null, null);
        }
        else
        {
            hisOutpatientChargeMirrorMapper.updateMirrorProcessByIds(tenantId, ids,
                STATUS_PENDING, PROC_TYPE_EMPTY, procTime, procBy, null, null);
        }
        hisPatientChargeMirrorUnifiedMapper.updateMirrorProcessByIds(tenantId, ids,
            STATUS_PENDING, PROC_TYPE_EMPTY, procTime, procBy, null, null);
    }

    private List<MirrorSnapshot> listLinkedRefundMirrors(String tenantId, String visitKind, String hisChargeId)
    {
        List<MirrorSnapshot> out = new ArrayList<>();
        if (KIND_IN.equals(visitKind))
        {
            List<HisInpatientChargeMirror> rows = hisInpatientChargeMirrorMapper.selectLinkedRefundByChargeIdTf(
                tenantId, hisChargeId);
            if (rows != null)
            {
                for (HisInpatientChargeMirror r : rows)
                {
                    out.add(MirrorSnapshot.fromIn(r));
                }
            }
        }
        else
        {
            List<HisOutpatientChargeMirror> rows = hisOutpatientChargeMirrorMapper.selectLinkedRefundByChargeIdTf(
                tenantId, hisChargeId);
            if (rows != null)
            {
                for (HisOutpatientChargeMirror r : rows)
                {
                    out.add(MirrorSnapshot.fromOut(r));
                }
            }
        }
        return out;
    }

    private MirrorSnapshot loadMirrorSnapshot(String tenantId, String visitKind, String mirrorRowId)
    {
        if (KIND_IN.equals(visitKind))
        {
            HisInpatientChargeMirror row = hisInpatientChargeMirrorMapper.selectByIdAndTenant(tenantId, mirrorRowId);
            if (row == null)
            {
                throw new ServiceException("未找到住院计费镜像行");
            }
            return MirrorSnapshot.fromIn(row);
        }
        HisOutpatientChargeMirror row = hisOutpatientChargeMirrorMapper.selectByIdAndTenant(tenantId, mirrorRowId);
        if (row == null)
        {
            throw new ServiceException("未找到门诊计费镜像行");
        }
        return MirrorSnapshot.fromOut(row);
    }

    private void assertLowValueMirror(MirrorSnapshot snap)
    {
        // 冲销资格以实际核销路径 processType 为准，不以耗材档案当前高低值为准
        if (PROC_TYPE_LOW.equals(StringUtils.trimToEmpty(snap.processType)))
        {
            return;
        }
        if (snap.isRefundRow())
        {
            if (!"1".equals(StringUtils.trimToEmpty(snap.valueLevel)))
            {
                return;
            }
            throw new ServiceException("高值退费返还请使用高值相关处理，不支持低值冲销");
        }
        if (PROC_TYPE_HIGH.equals(StringUtils.trimToEmpty(snap.processType)))
        {
            throw new ServiceException("高值核销的计费行请在高值模块处理，不支持低值冲销");
        }
        if ("1".equals(StringUtils.trimToEmpty(snap.valueLevel)))
        {
            throw new ServiceException("高值计费行请使用高值相关处理，不支持低值冲销");
        }
    }

    private static BigDecimal nz(BigDecimal v)
    {
        return v == null ? BigDecimal.ZERO : v;
    }

    private BigDecimal refundableOnLink(HisMirrorConsumeLink lk)
    {
        if (lk.getRefundableRemainingQty() != null)
        {
            return lk.getRefundableRemainingQty().max(BigDecimal.ZERO);
        }
        return nz(lk.getAllocQty()).subtract(nz(lk.getReturnedQty())).max(BigDecimal.ZERO);
    }

    private Map<Long, BigDecimal> loadCanReverseByEntry(Long consumeId)
    {
        List<Map<String, Object>> rows = deptBatchConsumeService.selectReverseableEntryList(consumeId);
        Map<Long, BigDecimal> m = new HashMap<>();
        if (rows == null)
        {
            return m;
        }
        for (Map<String, Object> row : rows)
        {
            Long eid = toLong(row.get("srcConsumeEntryId"));
            BigDecimal can = toBigDecimal(row.get("canReverseQty"));
            if (eid != null && can != null)
            {
                m.put(eid, can);
            }
        }
        return m;
    }

    private List<DeptBatchConsumeReverseReq.ReverseItem> mergeReverseItems(List<DeptBatchConsumeReverseReq.ReverseItem> items)
    {
        Map<Long, BigDecimal> merged = new LinkedHashMap<>();
        if (items != null)
        {
            for (DeptBatchConsumeReverseReq.ReverseItem it : items)
            {
                if (it == null || it.getSrcConsumeEntryId() == null || it.getReverseQty() == null)
                {
                    continue;
                }
                merged.merge(it.getSrcConsumeEntryId(), it.getReverseQty(), BigDecimal::add);
            }
        }
        List<DeptBatchConsumeReverseReq.ReverseItem> out = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> me : merged.entrySet())
        {
            DeptBatchConsumeReverseReq.ReverseItem it = new DeptBatchConsumeReverseReq.ReverseItem();
            it.setSrcConsumeEntryId(me.getKey());
            it.setReverseQty(me.getValue());
            out.add(it);
        }
        return out;
    }

    private Long toLong(Object v)
    {
        if (v == null)
        {
            return null;
        }
        if (v instanceof Number)
        {
            return ((Number) v).longValue();
        }
        try
        {
            return Long.parseLong(String.valueOf(v));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private BigDecimal toBigDecimal(Object v)
    {
        if (v == null)
        {
            return null;
        }
        if (v instanceof BigDecimal)
        {
            return (BigDecimal) v;
        }
        try
        {
            return new BigDecimal(String.valueOf(v));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static final class MirrorSnapshot
    {
        String id;
        String hisChargeId;
        String chargeIdTf;
        String processStatus;
        String processType;
        String valueLevel;

        boolean isRefundRow()
        {
            return StringUtils.isNotBlank(chargeIdTf);
        }

        static MirrorSnapshot fromIn(HisInpatientChargeMirror r)
        {
            MirrorSnapshot s = new MirrorSnapshot();
            s.id = r.getId();
            s.hisChargeId = r.getHisInpatientChargeId();
            s.chargeIdTf = r.getHisInpatientChargeIdTf();
            s.processStatus = r.getProcessStatus();
            s.processType = r.getProcessType();
            s.valueLevel = r.getValueLevel();
            return s;
        }

        static MirrorSnapshot fromOut(HisOutpatientChargeMirror r)
        {
            MirrorSnapshot s = new MirrorSnapshot();
            s.id = r.getId();
            s.hisChargeId = r.getHisOutpatientChargeId();
            s.chargeIdTf = r.getHisOutpatientChargeIdTf();
            s.processStatus = r.getProcessStatus();
            s.processType = r.getProcessType();
            s.valueLevel = r.getValueLevel();
            return s;
        }
    }
}

package com.spd.his.support;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import com.spd.common.utils.DateUtils;
import com.spd.his.domain.HisInpatientChargeMirror;
import com.spd.his.domain.HisOutpatientChargeMirror;
import com.spd.his.domain.HisPatientChargeMirrorUnified;
import com.spd.his.domain.dto.HisPatientChargeAllQuery;
import com.spd.his.domain.dto.HisPatientChargeDetailRow;
import com.spd.his.domain.dto.HisPatientChargeMirrorUnifiedQuery;

/**
 * 统一镜像行与住院/门诊查询对象、展示 DTO 的转换。
 */
public final class HisPatientChargeMirrorUnifiedSupport
{
    private HisPatientChargeMirrorUnifiedSupport()
    {
    }

    public static HisPatientChargeMirrorUnified fromInpatient(HisInpatientChargeMirror e)
    {
        if (e == null)
        {
            return null;
        }
        HisPatientChargeMirrorUnified m = new HisPatientChargeMirrorUnified();
        m.setId(e.getId());
        m.setTenantId(e.getTenantId());
        m.setVisitKind("INPATIENT");
        m.setFetchBatchId(e.getFetchBatchId());
        m.setHisInpatientChargeId(e.getHisInpatientChargeId());
        m.setHisInpatientChargeIdTf(e.getHisInpatientChargeIdTf());
        m.setPatientId(e.getPatientId());
        m.setPatientName(e.getPatientName());
        m.setInpatientNo(e.getInpatientNo());
        m.setDeptCode(e.getDeptCode());
        m.setDeptName(e.getDeptName());
        m.setDoctorId(e.getDoctorId());
        m.setDoctorName(e.getDoctorName());
        m.setChargeItemId(normalizeChargeItemId(e.getChargeItemId()));
        m.setValueLevel(StringUtils.trimToNull(e.getValueLevel()));
        m.setItemName(e.getItemName());
        m.setSpecModel(e.getSpecModel());
        m.setBatchNo(e.getBatchNo());
        m.setExpireDate(e.getExpireDate());
        m.setUseDate(e.getUseDate());
        m.setChargeAt(e.getChargeDate());
        m.setChargeDateDisplay(e.getChargeDate() != null ? DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, e.getChargeDate()) : null);
        m.setQuantity(e.getQuantity());
        m.setUnitPrice(e.getUnitPrice());
        m.setTotalAmount(e.getTotalAmount());
        m.setChargeOperator(e.getChargeOperator());
        m.setRemark(e.getRemark());
        m.setRowFingerprint(e.getRowFingerprint());
        m.setProcessStatus(StringUtils.defaultIfBlank(e.getProcessStatus(), "PENDING_CONSUME"));
        m.setProcessType(e.getProcessType());
        m.setProcessTime(e.getProcessTime());
        m.setProcessBy(e.getProcessBy());
        m.setProcessSituation(e.getProcessSituation());
        m.setProcessParty(e.getProcessParty());
        m.setCreateBy(e.getCreateBy());
        m.setCreateTime(e.getCreateTime());
        return m;
    }

    public static HisPatientChargeMirrorUnified fromOutpatient(HisOutpatientChargeMirror e)
    {
        if (e == null)
        {
            return null;
        }
        HisPatientChargeMirrorUnified m = new HisPatientChargeMirrorUnified();
        m.setId(e.getId());
        m.setTenantId(e.getTenantId());
        m.setVisitKind("OUTPATIENT");
        m.setFetchBatchId(e.getFetchBatchId());
        m.setHisOutpatientChargeId(e.getHisOutpatientChargeId());
        m.setHisOutpatientChargeIdTf(e.getHisOutpatientChargeIdTf());
        m.setPatientId(e.getPatientId());
        m.setPatientName(e.getPatientName());
        m.setOutpatientNo(e.getOutpatientNo());
        m.setClinicCode(e.getClinicCode());
        m.setClinicName(e.getClinicName());
        m.setDoctorId(e.getDoctorId());
        m.setDoctorName(e.getDoctorName());
        m.setChargeItemId(normalizeChargeItemId(e.getChargeItemId()));
        m.setValueLevel(StringUtils.trimToNull(e.getValueLevel()));
        m.setItemName(e.getItemName());
        m.setSpecModel(e.getSpecModel());
        m.setBatchNo(e.getBatchNo());
        m.setExpireDate(e.getExpireDate());
        m.setChargeDateDisplay(e.getChargeDate());
        m.setChargeAt(DateUtils.parseDate(e.getChargeDate()));
        m.setQuantity(e.getQuantity());
        m.setUnitPrice(e.getUnitPrice());
        m.setTotalAmount(e.getTotalAmount());
        m.setChargeOperator(e.getChargeOperator());
        m.setPaymentType(e.getPaymentType());
        m.setReceiptNo(e.getReceiptNo());
        m.setRemark(e.getRemark());
        m.setRowFingerprint(e.getRowFingerprint());
        m.setProcessStatus(StringUtils.defaultIfBlank(e.getProcessStatus(), "PENDING_CONSUME"));
        m.setProcessType(e.getProcessType());
        m.setProcessTime(e.getProcessTime());
        m.setProcessBy(e.getProcessBy());
        m.setProcessSituation(e.getProcessSituation());
        m.setProcessParty(e.getProcessParty());
        m.setCreateBy(e.getCreateBy());
        m.setCreateTime(e.getCreateTime());
        return m;
    }

    public static HisPatientChargeMirrorUnifiedQuery fromInpatientQuery(HisInpatientChargeMirror q)
    {
        HisPatientChargeMirrorUnifiedQuery u = new HisPatientChargeMirrorUnifiedQuery();
        if (q == null)
        {
            return u;
        }
        u.setTenantId(q.getTenantId());
        u.setVisitKind("INPATIENT");
        u.setPatientName(q.getPatientName());
        u.setInpatientNo(q.getInpatientNo());
        u.setChargeItemId(normalizeChargeItemId(q.getChargeItemId()));
        u.setChargeIdTf(q.getHisInpatientChargeIdTf());
        u.setDepartmentId(q.getDepartmentId());
        u.setProcessed(q.getProcessed());
        u.setValueLevel(q.getValueLevel());
        u.setBeginChargeDate(q.getBeginChargeDate());
        u.setEndChargeDate(q.getEndChargeDate());
        u.setBeginProcessTime(q.getBeginProcessTime());
        u.setEndProcessTime(q.getEndProcessTime());
        u.setDeptNameLike(q.getDeptName());
        u.setOrderByColumn(q.getOrderByColumn());
        u.setIsAsc(q.getIsAsc());
        u.setParams(q.getParams());
        return u;
    }

    public static HisPatientChargeMirrorUnifiedQuery fromOutpatientQuery(HisOutpatientChargeMirror q)
    {
        HisPatientChargeMirrorUnifiedQuery u = new HisPatientChargeMirrorUnifiedQuery();
        if (q == null)
        {
            return u;
        }
        u.setTenantId(q.getTenantId());
        u.setVisitKind("OUTPATIENT");
        u.setPatientName(q.getPatientName());
        u.setOutpatientNo(q.getOutpatientNo());
        u.setChargeItemId(normalizeChargeItemId(q.getChargeItemId()));
        u.setChargeIdTf(q.getHisOutpatientChargeIdTf());
        u.setDepartmentId(q.getDepartmentId());
        u.setProcessed(q.getProcessed());
        u.setValueLevel(q.getValueLevel());
        u.setBeginChargeDate(q.getBeginChargeDate());
        u.setEndChargeDate(q.getEndChargeDate());
        u.setBeginProcessTime(q.getBeginProcessTime());
        u.setEndProcessTime(q.getEndProcessTime());
        u.setClinicNameLike(q.getClinicName());
        u.setOrderByColumn(q.getOrderByColumn());
        u.setIsAsc(q.getIsAsc());
        u.setParams(q.getParams());
        return u;
    }

    public static HisPatientChargeMirrorUnifiedQuery fromAllQuery(HisPatientChargeAllQuery q)
    {
        HisPatientChargeMirrorUnifiedQuery u = new HisPatientChargeMirrorUnifiedQuery();
        if (q == null)
        {
            return u;
        }
        u.setTenantId(q.getTenantId());
        u.setVisitKind(null);
        u.setPatientName(q.getPatientName());
        u.setVisitNo(q.getVisitNo());
        u.setChargeItemId(normalizeChargeItemId(q.getChargeItemId()));
        u.setChargeIdTf(q.getChargeIdTf());
        u.setDepartmentId(q.getDepartmentId());
        u.setProcessed(q.getProcessed());
        u.setValueLevel(q.getValueLevel());
        u.setBeginChargeDate(q.getBeginChargeDate());
        u.setEndChargeDate(q.getEndChargeDate());
        u.setBeginProcessTime(q.getBeginProcessTime());
        u.setEndProcessTime(q.getEndProcessTime());
        u.setDeptNameLike(null);
        u.setClinicNameLike(null);
        u.setOrderByColumn(q.getOrderByColumn());
        u.setIsAsc(q.getIsAsc());
        u.setParams(q.getParams());
        return u;
    }

    public static HisInpatientChargeMirror toInpatientMirror(HisPatientChargeMirrorUnified u)
    {
        if (u == null)
        {
            return null;
        }
        HisInpatientChargeMirror r = new HisInpatientChargeMirror();
        r.setId(u.getId());
        r.setTenantId(u.getTenantId());
        r.setFetchBatchId(u.getFetchBatchId());
        r.setHisInpatientChargeId(u.getHisInpatientChargeId());
        r.setHisInpatientChargeIdTf(u.getHisInpatientChargeIdTf());
        r.setPatientId(u.getPatientId());
        r.setPatientName(u.getPatientName());
        r.setInpatientNo(u.getInpatientNo());
        r.setDeptCode(u.getDeptCode());
        r.setDeptName(u.getDeptName());
        r.setDoctorId(u.getDoctorId());
        r.setDoctorName(u.getDoctorName());
        r.setChargeItemId(u.getChargeItemId());
        r.setItemName(u.getItemName());
        r.setSpecModel(u.getSpecModel());
        r.setBatchNo(u.getBatchNo());
        r.setExpireDate(u.getExpireDate());
        r.setUseDate(u.getUseDate());
        r.setChargeDate(u.getChargeAt() != null ? u.getChargeAt() : DateUtils.parseDate(u.getChargeDateDisplay()));
        r.setQuantity(u.getQuantity());
        r.setUnitPrice(u.getUnitPrice());
        r.setTotalAmount(u.getTotalAmount());
        r.setChargeOperator(u.getChargeOperator());
        r.setRemark(u.getRemark());
        r.setRowFingerprint(u.getRowFingerprint());
        r.setProcessStatus(u.getProcessStatus());
        r.setProcessType(u.getProcessType());
        r.setProcessTime(u.getProcessTime());
        r.setProcessBy(u.getProcessBy());
        r.setProcessSituation(u.getProcessSituation());
        r.setProcessParty(u.getProcessParty());
        r.setCreateBy(u.getCreateBy());
        r.setCreateTime(u.getCreateTime());
        r.setUpdateBy(u.getUpdateBy());
        r.setUpdateTime(u.getUpdateTime());
        r.setValueLevel(u.getValueLevel());
        r.setHighValueStockQty(u.getHighValueStockQty() != null ? u.getHighValueStockQty() : BigDecimal.ZERO);
        r.setLowValueStockQty(u.getLowValueStockQty() != null ? u.getLowValueStockQty() : BigDecimal.ZERO);
        return r;
    }

    public static HisOutpatientChargeMirror toOutpatientMirror(HisPatientChargeMirrorUnified u)
    {
        if (u == null)
        {
            return null;
        }
        HisOutpatientChargeMirror r = new HisOutpatientChargeMirror();
        r.setId(u.getId());
        r.setTenantId(u.getTenantId());
        r.setFetchBatchId(u.getFetchBatchId());
        r.setHisOutpatientChargeId(u.getHisOutpatientChargeId());
        r.setHisOutpatientChargeIdTf(u.getHisOutpatientChargeIdTf());
        r.setPatientId(u.getPatientId());
        r.setPatientName(u.getPatientName());
        r.setOutpatientNo(u.getOutpatientNo());
        r.setClinicCode(u.getClinicCode());
        r.setClinicName(u.getClinicName());
        r.setDoctorId(u.getDoctorId());
        r.setDoctorName(u.getDoctorName());
        r.setChargeItemId(u.getChargeItemId());
        r.setItemName(u.getItemName());
        r.setSpecModel(u.getSpecModel());
        r.setBatchNo(u.getBatchNo());
        r.setExpireDate(u.getExpireDate());
        r.setChargeDate(StringUtils.isNotBlank(u.getChargeDateDisplay()) ? u.getChargeDateDisplay()
            : (u.getChargeAt() != null ? DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, u.getChargeAt()) : null));
        r.setQuantity(u.getQuantity());
        r.setUnitPrice(u.getUnitPrice());
        r.setTotalAmount(u.getTotalAmount());
        r.setChargeOperator(u.getChargeOperator());
        r.setPaymentType(u.getPaymentType());
        r.setReceiptNo(u.getReceiptNo());
        r.setRemark(u.getRemark());
        r.setRowFingerprint(u.getRowFingerprint());
        r.setProcessStatus(u.getProcessStatus());
        r.setProcessType(u.getProcessType());
        r.setProcessTime(u.getProcessTime());
        r.setProcessBy(u.getProcessBy());
        r.setProcessSituation(u.getProcessSituation());
        r.setProcessParty(u.getProcessParty());
        r.setCreateBy(u.getCreateBy());
        r.setCreateTime(u.getCreateTime());
        r.setUpdateBy(u.getUpdateBy());
        r.setUpdateTime(u.getUpdateTime());
        r.setValueLevel(u.getValueLevel());
        r.setHighValueStockQty(u.getHighValueStockQty() != null ? u.getHighValueStockQty() : BigDecimal.ZERO);
        r.setLowValueStockQty(u.getLowValueStockQty() != null ? u.getLowValueStockQty() : BigDecimal.ZERO);
        return r;
    }

    public static HisPatientChargeDetailRow toDetailRow(HisPatientChargeMirrorUnified u)
    {
        if (u == null)
        {
            return null;
        }
        HisPatientChargeDetailRow r = new HisPatientChargeDetailRow();
        r.setId(u.getId());
        r.setVisitType(u.getVisitKind());
        r.setPatientName(u.getPatientName());
        r.setInpatientNo(u.getInpatientNo());
        r.setOutpatientNo(u.getOutpatientNo());
        if ("OUTPATIENT".equals(u.getVisitKind()))
        {
            r.setVisitNo(u.getOutpatientNo());
            r.setDeptName(null);
            r.setClinicName(u.getClinicName());
            r.setDeptDisplayName(u.getClinicName());
            r.setChargeIdTf(u.getHisOutpatientChargeIdTf());
        }
        else
        {
            r.setVisitNo(u.getInpatientNo());
            r.setDeptName(u.getDeptName());
            r.setClinicName(null);
            r.setDeptDisplayName(u.getDeptName());
            r.setChargeIdTf(u.getHisInpatientChargeIdTf());
        }
        r.setChargeItemId(u.getChargeItemId());
        r.setItemName(u.getItemName());
        r.setSpecModel(u.getSpecModel());
        Date cd = u.getChargeAt() != null ? u.getChargeAt() : DateUtils.parseDate(u.getChargeDateDisplay());
        r.setChargeDate(cd);
        r.setQuantity(u.getQuantity());
        r.setTotalAmount(u.getTotalAmount());
        r.setProcessStatus(u.getProcessStatus());
        r.setProcessType(u.getProcessType());
        r.setProcessTime(u.getProcessTime());
        r.setProcessBy(u.getProcessBy());
        r.setProcessSituation(u.getProcessSituation());
        r.setProcessParty(u.getProcessParty());
        r.setCreateTime(u.getCreateTime());
        r.setValueLevel(u.getValueLevel());
        r.setHighValueStockQty(u.getHighValueStockQty() != null ? u.getHighValueStockQty() : BigDecimal.ZERO);
        r.setLowValueStockQty(u.getLowValueStockQty() != null ? u.getLowValueStockQty() : BigDecimal.ZERO);
        return r;
    }

    /** 库存汇总用：住院=dept_code，门诊=clinic_code */
    public static String stockLocHisCode(HisPatientChargeMirrorUnified u)
    {
        if (u == null)
        {
            return "";
        }
        if ("OUTPATIENT".equals(u.getVisitKind()))
        {
            return StringUtils.trimToEmpty(u.getClinicCode());
        }
        return StringUtils.trimToEmpty(u.getDeptCode());
    }

    public static String stockPairKey(HisPatientChargeMirrorUnified u)
    {
        String loc = stockLocHisCode(u);
        String item = u != null ? StringUtils.trimToEmpty(u.getChargeItemId()) : "";
        if (loc.isEmpty() || item.isEmpty())
        {
            return null;
        }
        return loc + "|" + item;
    }

    public static String normalizeChargeItemId(String chargeItemId)
    {
        return StringUtils.trimToNull(chargeItemId);
    }
}

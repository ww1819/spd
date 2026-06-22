package com.spd.his.support;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.PinyinUtils;
import com.spd.his.domain.HisInpatientChargeMirror;
import com.spd.his.domain.HisOutpatientChargeMirror;
import com.spd.his.domain.HisPatientChargeMirrorUnified;
import com.spd.his.domain.dto.HisPatientChargeAllQuery;
import com.spd.his.domain.dto.HisPatientChargeDetailRow;
import com.spd.his.domain.dto.HisPatientChargeMirrorExportVo;
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
        fillPatientNameReferred(m);
        m.setInpatientNo(e.getInpatientNo());
        m.setDeptCode(e.getDeptCode());
        m.setDeptName(e.getDeptName());
        m.setExecDeptId(e.getExecDeptId());
        m.setExecDeptName(e.getExecDeptName());
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
        fillPatientNameReferred(m);
        m.setOutpatientNo(e.getOutpatientNo());
        m.setClinicCode(e.getClinicCode());
        m.setClinicName(e.getClinicName());
        m.setExecDeptId(e.getExecDeptId());
        m.setExecDeptName(e.getExecDeptName());
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

    /** 高值扫码核销列表：未指定高低值筛选时默认仅高值（未维护 is_gz 视同低值，见 resolvedValueLevelExpr） */
    public static void applyHighChargeListScope(HisPatientChargeMirrorUnifiedQuery u)
    {
        if (u == null || StringUtils.isNotBlank(u.getValueLevel()))
        {
            return;
        }
        if (u.getValueLevelIn() == null || u.getValueLevelIn().isEmpty())
        {
            u.setValueLevel(HisMirrorValueLevelSupport.LEVEL_HIGH);
        }
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
        u.setHisChargeId(StringUtils.trimToNull(q.getHisInpatientChargeId()));
        u.setChargeIdTf(q.getHisInpatientChargeIdTf());
        u.setDepartmentId(q.getDepartmentId());
        u.setProcessed(q.getProcessed());
        u.setValueLevel(q.getValueLevel());
        u.setBeginChargeDate(q.getBeginChargeDate());
        u.setEndChargeDate(q.getEndChargeDate());
        u.setBeginProcessTime(q.getBeginProcessTime());
        u.setEndProcessTime(q.getEndProcessTime());
        u.setDeptNameLike(q.getDeptName());
        u.setExecDeptNameLike(q.getExecDeptName());
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
        u.setHisChargeId(StringUtils.trimToNull(q.getHisOutpatientChargeId()));
        u.setChargeIdTf(q.getHisOutpatientChargeIdTf());
        u.setDepartmentId(q.getDepartmentId());
        u.setProcessed(q.getProcessed());
        u.setValueLevel(q.getValueLevel());
        u.setBeginChargeDate(q.getBeginChargeDate());
        u.setEndChargeDate(q.getEndChargeDate());
        u.setBeginProcessTime(q.getBeginProcessTime());
        u.setEndProcessTime(q.getEndProcessTime());
        u.setClinicNameLike(q.getClinicName());
        u.setExecDeptNameLike(q.getExecDeptName());
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
        u.setHisChargeId(StringUtils.trimToNull(q.getHisChargeId()));
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
        u.setExecDeptNameLike(q.getExecDeptName());
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
        r.setPatientSex(u.getPatientSex());
        r.setInpatientNo(u.getInpatientNo());
        r.setDeptCode(u.getDeptCode());
        r.setDeptName(u.getDeptName());
        r.setExecDeptId(u.getExecDeptId());
        r.setExecDeptName(u.getExecDeptName());
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
        r.setProcessParty(u.getProcessParty());
        r.setProcessSituation(u.getProcessSituation());
        r.setProcessByName(u.getProcessByName());
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
        r.setPatientSex(u.getPatientSex());
        r.setOutpatientNo(u.getOutpatientNo());
        r.setClinicCode(u.getClinicCode());
        r.setClinicName(u.getClinicName());
        r.setExecDeptId(u.getExecDeptId());
        r.setExecDeptName(u.getExecDeptName());
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
        r.setProcessParty(u.getProcessParty());
        r.setProcessSituation(u.getProcessSituation());
        r.setProcessByName(u.getProcessByName());
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
        r.setPatientSex(u.getPatientSex());
        r.setInpatientNo(u.getInpatientNo());
        r.setOutpatientNo(u.getOutpatientNo());
        if ("OUTPATIENT".equals(u.getVisitKind()))
        {
            r.setVisitNo(u.getOutpatientNo());
            r.setDeptName(null);
            r.setClinicName(u.getClinicName());
            r.setDeptDisplayName(u.getClinicName());
            r.setHisChargeId(u.getHisOutpatientChargeId());
            r.setChargeIdTf(u.getHisOutpatientChargeIdTf());
        }
        else
        {
            r.setVisitNo(u.getInpatientNo());
            r.setDeptName(u.getDeptName());
            r.setClinicName(null);
            r.setDeptDisplayName(u.getDeptName());
            r.setHisChargeId(u.getHisInpatientChargeId());
            r.setChargeIdTf(u.getHisInpatientChargeIdTf());
        }
        r.setExecDeptId(u.getExecDeptId());
        r.setExecDeptName(u.getExecDeptName());
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
        r.setProcessParty(u.getProcessParty());
        r.setProcessSituation(u.getProcessSituation());
        r.setProcessByName(u.getProcessByName());
        r.setCreateTime(u.getCreateTime());
        r.setValueLevel(u.getValueLevel());
        r.setHighValueStockQty(u.getHighValueStockQty() != null ? u.getHighValueStockQty() : BigDecimal.ZERO);
        r.setLowValueStockQty(u.getLowValueStockQty() != null ? u.getLowValueStockQty() : BigDecimal.ZERO);
        return r;
    }

    public static HisPatientChargeMirrorExportVo toExportVo(HisPatientChargeMirrorUnified u)
    {
        if (u == null)
        {
            return null;
        }
        HisPatientChargeDetailRow d = toDetailRow(u);
        if (d == null)
        {
            return null;
        }
        HisPatientChargeMirrorExportVo vo = new HisPatientChargeMirrorExportVo();
        vo.setVisitType(d.getVisitType());
        vo.setVisitNo(d.getVisitNo());
        vo.setDeptDisplayName(d.getDeptDisplayName());
        vo.setExecDeptName(d.getExecDeptName());
        vo.setPatientName(d.getPatientName());
        vo.setChargeItemId(d.getChargeItemId());
        vo.setHisChargeId(d.getHisChargeId());
        vo.setChargeIdTf(d.getChargeIdTf());
        vo.setItemName(d.getItemName());
        vo.setSpecModel(d.getSpecModel());
        vo.setValueLevel(d.getValueLevel());
        vo.setChargeDate(d.getChargeDate());
        vo.setQuantity(d.getQuantity());
        vo.setTotalAmount(d.getTotalAmount());
        vo.setProcessType(d.getProcessType());
        vo.setProcessStatus(d.getProcessStatus());
        vo.setProcessParty(d.getProcessParty());
        vo.setProcessByName(StringUtils.isNotBlank(d.getProcessByName()) ? d.getProcessByName() : d.getProcessBy());
        vo.setProcessSituation(d.getProcessSituation());
        vo.setProcessTime(d.getProcessTime());
        vo.setCreateTime(d.getCreateTime());
        return vo;
    }

    public static HisPatientChargeMirrorUnifiedQuery buildExportUnifiedQuery(
        HisPatientChargeAllQuery query, String visitKind, String inpatientNo, String outpatientNo)
    {
        HisPatientChargeMirrorUnifiedQuery uq = fromAllQuery(query);
        if ("INPATIENT".equals(visitKind))
        {
            uq.setVisitKind("INPATIENT");
            if (StringUtils.isNotBlank(inpatientNo))
            {
                uq.setInpatientNo(inpatientNo);
            }
            uq.setVisitNo(null);
        }
        else if ("OUTPATIENT".equals(visitKind))
        {
            uq.setVisitKind("OUTPATIENT");
            if (StringUtils.isNotBlank(outpatientNo))
            {
                uq.setOutpatientNo(outpatientNo);
            }
            uq.setVisitNo(null);
        }
        return uq;
    }

    /** 库存汇总用：执行科室 exec_dept_id */
    public static String stockLocHisCode(HisPatientChargeMirrorUnified u)
    {
        if (u == null)
        {
            return "";
        }
        return StringUtils.trimToEmpty(u.getExecDeptId());
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

    public static void fillPatientNameReferred(HisPatientChargeMirrorUnified m)
    {
        if (m == null)
        {
            return;
        }
        m.setPatientNameReferred(PinyinUtils.getPinyinInitials(StringUtils.trimToEmpty(m.getPatientName())));
    }

    /** 列表检索关键词：去首尾空白，首字母检索统一转大写 */
    public static void normalizeListQueryKeywords(HisPatientChargeMirrorUnifiedQuery u)
    {
        if (u == null)
        {
            return;
        }
        if (StringUtils.isNotBlank(u.getPatientName()))
        {
            u.setPatientName(normalizeSearchKeyword(u.getPatientName()));
        }
        if (StringUtils.isNotBlank(u.getChargeItemId()))
        {
            u.setChargeItemId(normalizeSearchKeyword(u.getChargeItemId()));
        }
        if (StringUtils.isNotBlank(u.getVisitNo()))
        {
            u.setVisitNo(StringUtils.trim(u.getVisitNo()));
        }
        if (StringUtils.isNotBlank(u.getInpatientNo()))
        {
            u.setInpatientNo(StringUtils.trim(u.getInpatientNo()));
        }
        if (StringUtils.isNotBlank(u.getOutpatientNo()))
        {
            u.setOutpatientNo(StringUtils.trim(u.getOutpatientNo()));
        }
    }

    private static String normalizeSearchKeyword(String raw)
    {
        String trimmed = StringUtils.trim(raw);
        if (StringUtils.isEmpty(trimmed))
        {
            return trimmed;
        }
        if (trimmed.matches("[A-Za-z0-9]+"))
        {
            return trimmed.toUpperCase();
        }
        return trimmed;
    }
}

package com.spd.his.domain.dto;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.spd.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 统一镜像列表查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HisPatientChargeMirrorUnifiedQuery extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String tenantId;
    /** INPATIENT / OUTPATIENT；空表示住院+门诊 */
    private String visitKind;
    private String patientName;
    private String inpatientNo;
    private String outpatientNo;
    /** 全部模式下住院号/门诊号统一筛选 */
    private String visitNo;
    private String chargeItemId;
    private String chargeIdTf;
    private Long departmentId;
    private String processed;
    private String valueLevel;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date beginChargeDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endChargeDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date beginProcessTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endProcessTime;
    /** 住院：科室名称模糊 */
    private String deptNameLike;
    /** 门诊：就诊名称模糊 */
    private String clinicNameLike;
    /** highValueStockQty / lowValueStockQty：仅用于分页后内存排序 */
    private String orderByColumn;
    private String isAsc;
}

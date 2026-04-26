package com.spd.his.domain.dto;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.spd.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 患者费用明细“全部类型”查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HisPatientChargeAllQuery extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String tenantId;
    private String patientName;
    /** 住院号/门诊号统一查询值 */
    private String visitNo;
    private String chargeItemId;
    private Long departmentId;
    /** Y=已处理 N=未处理 */
    private String processed;
    /** 收费项目高低值筛选：1高值 2低值，空=全部 */
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
    private String orderByColumn;
    private String isAsc;
    private Integer pageNum;
    private Integer pageSize;
}

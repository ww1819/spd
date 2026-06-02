package com.spd.gz.domain.dto;

import lombok.Data;

/**
 * 高值核销确认列表查询
 */
@Data
public class GzHighChargeConfirmQuery
{
    private String tenantId;
    private Long departmentId;
    /** 0 未确认 / 1 已确认 / 空 全部 */
    private String confirmStatus;
    /** 核销完成时间起 */
    private String beginConsumeAuditTime;
    /** 核销完成时间止 */
    private String endConsumeAuditTime;
    private String patientName;
    private String visitNo;
    private String chargeItemId;
    private String materialName;
}

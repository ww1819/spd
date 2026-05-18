package com.spd.his.domain.dto;

import lombok.Data;

@Data
public class HisProcessFetchBatchInternalBody
{
    private String tenantId;
    private String fetchBatchId;
    /** INPATIENT / OUTPATIENT */
    private String visitKind;
    /** 可选：覆盖默认操作人用户 ID（scminterface 调用） */
    private Long operatorUserId;
}

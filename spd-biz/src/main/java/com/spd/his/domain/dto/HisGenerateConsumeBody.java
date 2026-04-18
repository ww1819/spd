package com.spd.his.domain.dto;

import lombok.Data;

@Data
public class HisGenerateConsumeBody
{
    /** 与镜像表 fetch_batch_id 一致 */
    private String fetchBatchId;
    /** INPATIENT 或 OUTPATIENT，须与抓取批次 charge_kind 一致 */
    private String visitKind;
}

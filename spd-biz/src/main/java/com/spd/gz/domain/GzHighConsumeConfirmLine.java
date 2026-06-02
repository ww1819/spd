package com.spd.gz.domain;

import java.util.Date;
import lombok.Data;

/**
 * 高值消耗确认明细行
 */
@Data
public class GzHighConsumeConfirmLine
{
    private String id;
    private String tenantId;
    private String confirmId;
    private String consumeLinkId;
    private Long deptBatchConsumeEntryId;
    private Integer delFlag;
    private Date createTime;
}

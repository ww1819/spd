package com.spd.gz.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 高值消耗确认批次
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GzHighConsumeConfirm extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String id;
    private String tenantId;
    private String confirmNo;
    private Long departmentId;
    private Long warehouseId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date confirmTime;
    private String confirmBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date periodBegin;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date periodEnd;
    private Integer delFlag;
}

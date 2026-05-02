package com.spd.foundation.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 租户级业务开关 sb_tenant_setting
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SbTenantSetting extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String id;
    private String tenantId;
    private String settingKey;
    private String settingValue;
    private String remark;
    private Integer delFlag;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;
    private String deleteBy;
}

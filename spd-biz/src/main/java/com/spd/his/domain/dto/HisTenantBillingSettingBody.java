package com.spd.his.domain.dto;

import lombok.Data;

@Data
public class HisTenantBillingSettingBody
{
    /** 1 开启 0 关闭：低值计费抓取后自动生成消耗 */
    private String lvAutoConsumeEnabled;
}

package com.spd.foundation.support;

import com.spd.common.enums.TenantEnum;
import com.spd.common.utils.StringUtils;

/**
 * SPD 已接入众阳 HIS 的租户登记册，与 scminterface {@code MsunHospitalRegistry} 成对维护。
 */
public enum MsunHisTenantRegistry
{
    ZAOQIANG_TCM(TenantEnum.ZQ_TCM.getCustomerId(), "枣强县中医院");

    private final String hospitalKey;

    private final String hospitalName;

    MsunHisTenantRegistry(String hospitalKey, String hospitalName)
    {
        this.hospitalKey = hospitalKey;
        this.hospitalName = hospitalName;
    }

    public String getHospitalKey()
    {
        return hospitalKey;
    }

    public String getHospitalName()
    {
        return hospitalName;
    }

    public static MsunHisTenantRegistry resolve(String tenantId)
    {
        String key = StringUtils.trimToEmpty(tenantId);
        if (StringUtils.isEmpty(key))
        {
            return null;
        }
        for (MsunHisTenantRegistry item : values())
        {
            if (item.hospitalKey.equalsIgnoreCase(key))
            {
                return item;
            }
        }
        return null;
    }
}

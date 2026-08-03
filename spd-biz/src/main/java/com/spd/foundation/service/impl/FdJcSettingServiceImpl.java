package com.spd.foundation.service.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.common.constant.JcConstants;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.foundation.mapper.FdJcReportMapper;
import com.spd.foundation.service.IFdJcSettingService;
import com.spd.foundation.service.ISbTenantSettingService;

@Service
public class FdJcSettingServiceImpl implements IFdJcSettingService
{
    @Autowired
    private ISbTenantSettingService sbTenantSettingService;

    @Autowired
    private FdJcReportMapper fdJcReportMapper;

    @Override
    public String getReportMode()
    {
        String tenantId = SecurityUtils.requiredScopedTenantIdForSql();
        String mode = sbTenantSettingService.getSettingValue(tenantId, JcConstants.SETTING_REPORT_MODE,
            JcConstants.DEFAULT_REPORT_MODE);
        if (!JcConstants.MODE_PRODUCT.equals(mode) && !JcConstants.MODE_TYPE.equals(mode))
        {
            return JcConstants.DEFAULT_REPORT_MODE;
        }
        return mode;
    }

    @Override
    public void saveReportMode(String reportMode)
    {
        if (StringUtils.isEmpty(reportMode))
        {
            throw new ServiceException("报量模式不能为空");
        }
        String mode = reportMode.trim().toUpperCase();
        if (!JcConstants.MODE_PRODUCT.equals(mode) && !JcConstants.MODE_TYPE.equals(mode))
        {
            throw new ServiceException("报量模式仅支持 PRODUCT（按产品）或 TYPE（按类型）");
        }
        String current = getReportMode();
        if (current.equals(mode))
        {
            return;
        }
        String tenantId = SecurityUtils.requiredScopedTenantIdForSql();
        // 切换只改配置，绝不删除任一模式的报量；切回后历史数据仍可用
        sbTenantSettingService.saveSettingValue(tenantId, JcConstants.SETTING_REPORT_MODE, mode,
            "集采报量模式（切换保留旧数据）");
    }

    @Override
    public Map<String, Object> getSettingSummary()
    {
        String mode = getReportMode();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("reportMode", mode);
        map.put("productReportCount", fdJcReportMapper.countByMode(JcConstants.MODE_PRODUCT));
        map.put("typeReportCount", fdJcReportMapper.countByMode(JcConstants.MODE_TYPE));
        return map;
    }
}

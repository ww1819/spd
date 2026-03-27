package com.spd.system.service.impl;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.system.domain.SysPrintSetting;
import com.spd.system.mapper.SysPrintSettingMapper;
import com.spd.system.service.ISysPrintSettingService;

/**
 * 打印设置 服务层实现
 * 
 * @author spd
 */
@Service
public class SysPrintSettingServiceImpl implements ISysPrintSettingService
{
    @Autowired
    private SysPrintSettingMapper sysPrintSettingMapper;

    /**
     * 查询打印设置信息
     * 
     * @param id 打印设置ID
     * @return 打印设置信息
     */
    @Override
    public SysPrintSetting selectSysPrintSettingById(Long id)
    {
        return sysPrintSettingMapper.selectSysPrintSettingById(id);
    }

    /**
     * 查询打印设置列表
     * 
     * @param sysPrintSetting 打印设置信息
     * @return 打印设置集合
     */
    @Override
    public List<SysPrintSetting> selectSysPrintSettingList(SysPrintSetting sysPrintSetting)
    {
        return sysPrintSettingMapper.selectSysPrintSettingList(sysPrintSetting);
    }

    /**
     * 根据入库单类型查询默认模板
     * 
     * @param billType 入库单类型
     * @return 打印设置信息
     */
    @Override
    public SysPrintSetting selectDefaultByBillType(Integer billType)
    {
        return selectEffectiveDefault(billType, SecurityUtils.getCustomerId());
    }

    @Override
    public SysPrintSetting selectEffectiveDefault(Integer billType, String tenantId)
    {
        String tid = StringUtils.isNotEmpty(tenantId) ? tenantId.trim() : null;
        if (tid != null)
        {
            SysPrintSetting s = sysPrintSettingMapper.selectDefaultByBillTypeAndTenant(billType, tid);
            if (s != null)
            {
                return s;
            }
        }
        return sysPrintSettingMapper.selectDefaultByBillTypeAndTenant(billType, null);
    }

    /** 空串视为未填；未填时租户用户默认落到当前 customerId */
    private void normalizeTenantIdOnSave(SysPrintSetting sysPrintSetting)
    {
        if (sysPrintSetting.getTenantId() != null)
        {
            String t = sysPrintSetting.getTenantId().trim();
            sysPrintSetting.setTenantId(StringUtils.isEmpty(t) ? null : t);
        }
        if (sysPrintSetting.getTenantId() == null && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            sysPrintSetting.setTenantId(SecurityUtils.getCustomerId());
        }
    }

    /**
     * 新增打印设置
     * 
     * @param sysPrintSetting 打印设置信息
     * @return 结果
     */
    @Override
    public int insertSysPrintSetting(SysPrintSetting sysPrintSetting)
    {
        normalizeTenantIdOnSave(sysPrintSetting);
        sysPrintSetting.setCreateTime(DateUtils.getNowDate());
        // 如果设置为默认模板，取消同类型其他模板的默认状态
        if (sysPrintSetting.getIsDefault() != null && sysPrintSetting.getIsDefault() == 1)
        {
            sysPrintSettingMapper.cancelOtherDefault(sysPrintSetting);
        }
        return sysPrintSettingMapper.insertSysPrintSetting(sysPrintSetting);
    }

    /**
     * 修改打印设置
     * 
     * @param sysPrintSetting 打印设置信息
     * @return 结果
     */
    @Override
    public int updateSysPrintSetting(SysPrintSetting sysPrintSetting)
    {
        if (sysPrintSetting.getTenantId() != null)
        {
            String t = sysPrintSetting.getTenantId().trim();
            sysPrintSetting.setTenantId(StringUtils.isEmpty(t) ? null : t);
        }
        sysPrintSetting.setUpdateTime(DateUtils.getNowDate());
        // 如果设置为默认模板，取消同类型其他模板的默认状态
        if (sysPrintSetting.getIsDefault() != null && sysPrintSetting.getIsDefault() == 1)
        {
            sysPrintSettingMapper.cancelOtherDefault(sysPrintSetting);
        }
        return sysPrintSettingMapper.updateSysPrintSetting(sysPrintSetting);
    }

    /**
     * 批量删除打印设置
     * 
     * @param ids 需要删除的打印设置ID
     * @return 结果
     */
    @Override
    public int deleteSysPrintSettingByIds(Long[] ids)
    {
        return sysPrintSettingMapper.deleteSysPrintSettingByIds(ids);
    }

    /**
     * 设置默认模板
     * 
     * @param sysPrintSetting 打印设置信息
     * @return 结果
     */
    @Override
    public int setDefaultTemplate(SysPrintSetting sysPrintSetting)
    {
        SysPrintSetting db = sysPrintSettingMapper.selectSysPrintSettingById(sysPrintSetting.getId());
        if (db == null)
        {
            return 0;
        }
        // 按库内 bill_type + tenant_id 作用域取消其它默认，避免误清其它租户或全库默认
        SysPrintSetting scope = new SysPrintSetting();
        scope.setId(db.getId());
        scope.setBillType(db.getBillType());
        scope.setTenantId(db.getTenantId());
        sysPrintSettingMapper.cancelOtherDefault(scope);
        sysPrintSetting.setIsDefault(1);
        sysPrintSetting.setUpdateTime(DateUtils.getNowDate());
        return sysPrintSettingMapper.updateSysPrintSetting(sysPrintSetting);
    }
}

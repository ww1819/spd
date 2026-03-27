package com.spd.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.system.domain.SysPrintSetting;

/**
 * 打印设置 数据层
 * 
 * @author spd
 */
public interface SysPrintSettingMapper
{
    /**
     * 查询打印设置信息
     * 
     * @param id 打印设置ID
     * @return 打印设置信息
     */
    public SysPrintSetting selectSysPrintSettingById(Long id);

    /**
     * 查询打印设置列表
     * 
     * @param sysPrintSetting 打印设置信息
     * @return 打印设置集合
     */
    public List<SysPrintSetting> selectSysPrintSettingList(SysPrintSetting sysPrintSetting);

    /**
     * 按单据类型 + 租户查询默认模板（is_default=1、status=0）。
     * tenantId 为空时仅匹配全库默认（tenant_id IS NULL 或空串）。
     */
    public SysPrintSetting selectDefaultByBillTypeAndTenant(@Param("billType") Integer billType, @Param("tenantId") String tenantId);

    /**
     * 新增打印设置
     * 
     * @param sysPrintSetting 打印设置信息
     * @return 结果
     */
    public int insertSysPrintSetting(SysPrintSetting sysPrintSetting);

    /**
     * 修改打印设置
     * 
     * @param sysPrintSetting 打印设置信息
     * @return 结果
     */
    public int updateSysPrintSetting(SysPrintSetting sysPrintSetting);

    /**
     * 删除打印设置
     * 
     * @param id 打印设置ID
     * @return 结果
     */
    public int deleteSysPrintSettingById(Long id);

    /**
     * 批量删除打印设置
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteSysPrintSettingByIds(Long[] ids);

    /**
     * 取消同类型其他模板的默认状态
     * 
     * @param sysPrintSetting 打印设置信息
     * @return 结果
     */
    public int cancelOtherDefault(SysPrintSetting sysPrintSetting);
}

package com.spd.system.service;

import java.util.List;
import com.spd.system.domain.SysPrintSetting;

/**
 * 打印设置 服务层
 * 
 * @author spd
 */
public interface ISysPrintSettingService
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
     * 根据入库单类型查询默认模板
     * 
     * @param billType 入库单类型
     * @return 打印设置信息
     */
    public SysPrintSetting selectDefaultByBillType(Integer billType);

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
     * 批量删除打印设置
     * 
     * @param ids 需要删除的打印设置ID
     * @return 结果
     */
    public int deleteSysPrintSettingByIds(Long[] ids);

    /**
     * 设置默认模板
     * 
     * @param sysPrintSetting 打印设置信息
     * @return 结果
     */
    public int setDefaultTemplate(SysPrintSetting sysPrintSetting);
}

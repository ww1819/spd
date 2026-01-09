package com.spd.department.service;

import java.util.List;
import com.spd.department.domain.DepInventoryWarning;

/**
 * 科室库存预警设置Service接口
 *
 * @author spd
 * @date 2026-01-03
 */
public interface IDepInventoryWarningService
{
    /**
     * 查询科室库存预警设置
     *
     * @param id 科室库存预警设置主键
     * @return 科室库存预警设置
     */
    public DepInventoryWarning selectDepInventoryWarningById(Long id);

    /**
     * 查询科室库存预警设置列表
     *
     * @param depInventoryWarning 科室库存预警设置
     * @return 科室库存预警设置集合
     */
    public List<DepInventoryWarning> selectDepInventoryWarningList(DepInventoryWarning depInventoryWarning);

    /**
     * 新增科室库存预警设置
     *
     * @param depInventoryWarning 科室库存预警设置
     * @return 结果
     */
    public int insertDepInventoryWarning(DepInventoryWarning depInventoryWarning);

    /**
     * 修改科室库存预警设置
     *
     * @param depInventoryWarning 科室库存预警设置
     * @return 结果
     */
    public int updateDepInventoryWarning(DepInventoryWarning depInventoryWarning);

    /**
     * 批量删除科室库存预警设置
     *
     * @param ids 需要删除的科室库存预警设置主键集合
     * @return 结果
     */
    public int deleteDepInventoryWarningByIds(Long[] ids);

    /**
     * 删除科室库存预警设置信息
     *
     * @param id 科室库存预警设置主键
     * @return 结果
     */
    public int deleteDepInventoryWarningById(Long id);
}


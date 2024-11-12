package com.spd.department.service;

import java.util.List;
import com.spd.department.domain.StkDepInventory;

/**
 * 科室库存Service接口
 * 
 * @author spd
 * @date 2024-03-04
 */
public interface IStkDepInventoryService 
{
    /**
     * 查询科室库存
     * 
     * @param id 科室库存主键
     * @return 科室库存
     */
    public StkDepInventory selectStkDepInventoryById(Long id);

    /**
     * 查询科室库存列表
     * 
     * @param stkDepInventory 科室库存
     * @return 科室库存集合
     */
    public List<StkDepInventory> selectStkDepInventoryList(StkDepInventory stkDepInventory);

    /**
     * 新增科室库存
     * 
     * @param stkDepInventory 科室库存
     * @return 结果
     */
    public int insertStkDepInventory(StkDepInventory stkDepInventory);

    /**
     * 修改科室库存
     * 
     * @param stkDepInventory 科室库存
     * @return 结果
     */
    public int updateStkDepInventory(StkDepInventory stkDepInventory);

    /**
     * 批量删除科室库存
     * 
     * @param ids 需要删除的科室库存主键集合
     * @return 结果
     */
    public int deleteStkDepInventoryByIds(Long[] ids);

    /**
     * 删除科室库存信息
     * 
     * @param id 科室库存主键
     * @return 结果
     */
    public int deleteStkDepInventoryById(Long id);
}

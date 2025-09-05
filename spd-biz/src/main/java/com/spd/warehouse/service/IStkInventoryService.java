package com.spd.warehouse.service;

import java.util.List;
import java.util.Map;

import com.spd.warehouse.domain.StkInventory;

/**
 * 库存明细Service接口
 *
 * @author spd
 * @date 2023-12-17
 */
public interface IStkInventoryService
{
    /**
     * 查询库存明细
     *
     * @param id 库存明细主键
     * @return 库存明细
     */
    public StkInventory selectStkInventoryById(Long id);

    /**
     * 查询库存明细列表
     *
     * @param stkInventory 库存明细
     * @return 库存明细集合
     */
    public List<StkInventory> selectStkInventoryList(StkInventory stkInventory);

    public Map selectStkInventoryListTotal(StkInventory stkInventory);

    /**
     * 新增库存明细
     *
     * @param stkInventory 库存明细
     * @return 结果
     */
    public int insertStkInventory(StkInventory stkInventory);

    /**
     * 修改库存明细
     *
     * @param stkInventory 库存明细
     * @return 结果
     */
    public int updateStkInventory(StkInventory stkInventory);

    /**
     * 批量删除库存明细
     *
     * @param ids 需要删除的库存明细主键集合
     * @return 结果
     */
    public int deleteStkInventoryByIds(Long[] ids);

    /**
     * 删除库存明细信息
     *
     * @param id 库存明细主键
     * @return 结果
     */
    public int deleteStkInventoryById(Long id);

    /**
     * 按仓库筛选实时库存耗材
     * @param stkInventory
     * @return
     */
    List<StkInventory> selectStkMaterialList(StkInventory stkInventory);

    /**
     * 按仓库筛选实时库存、基础耗材
     * @param stkInventory
     * @return
     */
    List<StkInventory> selectPDInventoryFilter(StkInventory stkInventory);

    /**
     * 查询库存明细汇总列表
     * @param stkInventory
     * @return
     */
    List<Map<String, Object>> selectStkInventoryListSummary(StkInventory stkInventory);

}

package com.spd.warehouse.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.spd.common.core.page.TotalInfo;
import com.spd.warehouse.domain.StkInventory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 库存明细Mapper接口
 *
 * @author spd
 * @date 2023-12-17
 */
@Mapper
@Repository
public interface StkInventoryMapper
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

    /**
     * 查询库存汇总数量
     *
     * @param stkInventory 库存明细
     * @return 库存明细集合
     */
    public TotalInfo selectStkInventoryListTotal(StkInventory stkInventory);



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
     * 删除库存明细
     *
     * @param id 库存明细主键
     * @return 结果
     */
    public int deleteStkInventoryById(Long id);

    /**
     * 批量删除库存明细
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStkInventoryByIds(Long[] ids);

    /**
     * 按仓库筛选实时库存耗材
     */
    List<StkInventory> selectStkMaterialList(StkInventory stkInventory);

    /**
     * 按批次查询库存实际数量
     * @param batchNo
     * @return
     */
    BigDecimal selectStkInvntoryByBatchNo(String batchNo);

    /**
     * 按批次号查询库存明细
     *
     * @param batchNo 批次号
     * @return 库存明细
     */
    public StkInventory selectStkInventoryOne(String batchNo);

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

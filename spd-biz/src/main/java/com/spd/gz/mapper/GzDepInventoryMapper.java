package com.spd.gz.mapper;

import java.math.BigDecimal;
import java.util.List;
import com.spd.gz.domain.GzDepInventory;

/**
 * 高值科室库存Mapper接口
 *
 * @author spd
 * @date 2024-06-22
 */
public interface GzDepInventoryMapper
{
    /**
     * 查询高值科室库存
     *
     * @param id 高值科室库存主键
     * @return 高值科室库存
     */
    public GzDepInventory selectGzDepInventoryById(Long id);

    /**
     * 查询高值科室库存列表
     *
     * @param gzDepInventory 高值科室库存
     * @return 高值科室库存集合
     */
    public List<GzDepInventory> selectGzDepInventoryList(GzDepInventory gzDepInventory);

    /**
     * 新增高值科室库存
     *
     * @param gzDepInventory 高值科室库存
     * @return 结果
     */
    public int insertGzDepInventory(GzDepInventory gzDepInventory);

    /**
     * 修改高值科室库存
     *
     * @param gzDepInventory 高值科室库存
     * @return 结果
     */
    public int updateGzDepInventory(GzDepInventory gzDepInventory);

    /**
     * 删除高值科室库存
     *
     * @param id 高值科室库存主键
     * @return 结果
     */
    public int deleteGzDepInventoryById(Long id);

    /**
     * 批量删除高值科室库存
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGzDepInventoryByIds(Long[] ids);

    /**
     * 按批次号查询科室库存
     * @param batchNo
     * @return
     */
    GzDepInventory selectGzDepInventoryOne(String batchNo);

    /**
     * 根据批次号查询高值科室库存实际数量
     * @param batchNo
     * @return
     */
    BigDecimal selectTKDepInvntoryByBatchNo(String batchNo);
}

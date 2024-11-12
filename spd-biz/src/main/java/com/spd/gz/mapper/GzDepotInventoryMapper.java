package com.spd.gz.mapper;

import java.math.BigDecimal;
import java.util.List;
import com.spd.gz.domain.GzDepotInventory;

/**
 * 高值备货库存明细Mapper接口
 *
 * @author spd
 * @date 2024-06-11
 */
public interface GzDepotInventoryMapper
{
    /**
     * 查询高值备货库存明细
     *
     * @param id 高值备货库存明细主键
     * @return 高值备货库存明细
     */
    public GzDepotInventory selectGzDepotInventoryById(Long id);

    /**
     * 查询高值备货库存明细列表
     *
     * @param gzDepotInventory 高值备货库存明细
     * @return 高值备货库存明细集合
     */
    public List<GzDepotInventory> selectGzDepotInventoryList(GzDepotInventory gzDepotInventory);

    /**
     * 新增高值备货库存明细
     *
     * @param gzDepotInventory 高值备货库存明细
     * @return 结果
     */
    public int insertGzDepotInventory(GzDepotInventory gzDepotInventory);

    /**
     * 修改高值备货库存明细
     *
     * @param gzDepotInventory 高值备货库存明细
     * @return 结果
     */
    public int updateGzDepotInventory(GzDepotInventory gzDepotInventory);

    /**
     * 删除高值备货库存明细
     *
     * @param id 高值备货库存明细主键
     * @return 结果
     */
    public int deleteGzDepotInventoryById(Long id);

    /**
     * 批量删除高值备货库存明细
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGzDepotInventoryByIds(Long[] ids);

    /**
     * 按批次查询库存实际数量
     * @param batchNo
     * @return
     */
    BigDecimal selectGzDepotInventoryByBatchNo(String batchNo);

    /**
     * 按批次号查询高值库存明细
     * @param batchNo
     * @return
     */
    GzDepotInventory selectGzDepotInventoryOne(String batchNo);
}

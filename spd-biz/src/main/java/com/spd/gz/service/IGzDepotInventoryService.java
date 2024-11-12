package com.spd.gz.service;

import java.util.List;
import com.spd.gz.domain.GzDepotInventory;

/**
 * 高值备货库存明细Service接口
 * 
 * @author spd
 * @date 2024-06-11
 */
public interface IGzDepotInventoryService 
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
     * 批量删除高值备货库存明细
     * 
     * @param ids 需要删除的高值备货库存明细主键集合
     * @return 结果
     */
    public int deleteGzDepotInventoryByIds(Long[] ids);

    /**
     * 删除高值备货库存明细信息
     * 
     * @param id 高值备货库存明细主键
     * @return 结果
     */
    public int deleteGzDepotInventoryById(Long id);
}

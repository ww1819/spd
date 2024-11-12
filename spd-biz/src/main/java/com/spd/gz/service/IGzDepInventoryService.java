package com.spd.gz.service;

import java.util.List;
import com.spd.gz.domain.GzDepInventory;

/**
 * 高值科室库存Service接口
 * 
 * @author spd
 * @date 2024-06-22
 */
public interface IGzDepInventoryService 
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
     * 批量删除高值科室库存
     * 
     * @param ids 需要删除的高值科室库存主键集合
     * @return 结果
     */
    public int deleteGzDepInventoryByIds(Long[] ids);

    /**
     * 删除高值科室库存信息
     * 
     * @param id 高值科室库存主键
     * @return 结果
     */
    public int deleteGzDepInventoryById(Long id);
}

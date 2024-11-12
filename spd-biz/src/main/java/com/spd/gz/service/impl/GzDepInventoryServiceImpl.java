package com.spd.gz.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.gz.mapper.GzDepInventoryMapper;
import com.spd.gz.domain.GzDepInventory;
import com.spd.gz.service.IGzDepInventoryService;

/**
 * 高值科室库存Service业务层处理
 * 
 * @author spd
 * @date 2024-06-22
 */
@Service
public class GzDepInventoryServiceImpl implements IGzDepInventoryService 
{
    @Autowired
    private GzDepInventoryMapper gzDepInventoryMapper;

    /**
     * 查询高值科室库存
     * 
     * @param id 高值科室库存主键
     * @return 高值科室库存
     */
    @Override
    public GzDepInventory selectGzDepInventoryById(Long id)
    {
        return gzDepInventoryMapper.selectGzDepInventoryById(id);
    }

    /**
     * 查询高值科室库存列表
     * 
     * @param gzDepInventory 高值科室库存
     * @return 高值科室库存
     */
    @Override
    public List<GzDepInventory> selectGzDepInventoryList(GzDepInventory gzDepInventory)
    {
        return gzDepInventoryMapper.selectGzDepInventoryList(gzDepInventory);
    }

    /**
     * 新增高值科室库存
     * 
     * @param gzDepInventory 高值科室库存
     * @return 结果
     */
    @Override
    public int insertGzDepInventory(GzDepInventory gzDepInventory)
    {
        return gzDepInventoryMapper.insertGzDepInventory(gzDepInventory);
    }

    /**
     * 修改高值科室库存
     * 
     * @param gzDepInventory 高值科室库存
     * @return 结果
     */
    @Override
    public int updateGzDepInventory(GzDepInventory gzDepInventory)
    {
        return gzDepInventoryMapper.updateGzDepInventory(gzDepInventory);
    }

    /**
     * 批量删除高值科室库存
     * 
     * @param ids 需要删除的高值科室库存主键
     * @return 结果
     */
    @Override
    public int deleteGzDepInventoryByIds(Long[] ids)
    {
        return gzDepInventoryMapper.deleteGzDepInventoryByIds(ids);
    }

    /**
     * 删除高值科室库存信息
     * 
     * @param id 高值科室库存主键
     * @return 结果
     */
    @Override
    public int deleteGzDepInventoryById(Long id)
    {
        return gzDepInventoryMapper.deleteGzDepInventoryById(id);
    }
}

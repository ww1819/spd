package com.spd.gz.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.gz.mapper.GzDepotInventoryMapper;
import com.spd.gz.domain.GzDepotInventory;
import com.spd.gz.service.IGzDepotInventoryService;

/**
 * 高值备货库存明细Service业务层处理
 * 
 * @author spd
 * @date 2024-06-11
 */
@Service
public class GzDepotInventoryServiceImpl implements IGzDepotInventoryService 
{
    @Autowired
    private GzDepotInventoryMapper gzDepotInventoryMapper;

    /**
     * 查询高值备货库存明细
     * 
     * @param id 高值备货库存明细主键
     * @return 高值备货库存明细
     */
    @Override
    public GzDepotInventory selectGzDepotInventoryById(Long id)
    {
        return gzDepotInventoryMapper.selectGzDepotInventoryById(id);
    }

    /**
     * 查询高值备货库存明细列表
     * 
     * @param gzDepotInventory 高值备货库存明细
     * @return 高值备货库存明细
     */
    @Override
    public List<GzDepotInventory> selectGzDepotInventoryList(GzDepotInventory gzDepotInventory)
    {
        return gzDepotInventoryMapper.selectGzDepotInventoryList(gzDepotInventory);
    }

    /**
     * 新增高值备货库存明细
     * 
     * @param gzDepotInventory 高值备货库存明细
     * @return 结果
     */
    @Override
    public int insertGzDepotInventory(GzDepotInventory gzDepotInventory)
    {
        return gzDepotInventoryMapper.insertGzDepotInventory(gzDepotInventory);
    }

    /**
     * 修改高值备货库存明细
     * 
     * @param gzDepotInventory 高值备货库存明细
     * @return 结果
     */
    @Override
    public int updateGzDepotInventory(GzDepotInventory gzDepotInventory)
    {
        return gzDepotInventoryMapper.updateGzDepotInventory(gzDepotInventory);
    }

    /**
     * 批量删除高值备货库存明细
     * 
     * @param ids 需要删除的高值备货库存明细主键
     * @return 结果
     */
    @Override
    public int deleteGzDepotInventoryByIds(Long[] ids)
    {
        return gzDepotInventoryMapper.deleteGzDepotInventoryByIds(ids);
    }

    /**
     * 删除高值备货库存明细信息
     * 
     * @param id 高值备货库存明细主键
     * @return 结果
     */
    @Override
    public int deleteGzDepotInventoryById(Long id)
    {
        return gzDepotInventoryMapper.deleteGzDepotInventoryById(id);
    }
}

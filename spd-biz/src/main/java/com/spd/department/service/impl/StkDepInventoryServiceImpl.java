package com.spd.department.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.department.mapper.StkDepInventoryMapper;
import com.spd.department.domain.StkDepInventory;
import com.spd.department.service.IStkDepInventoryService;

/**
 * 科室库存Service业务层处理
 * 
 * @author spd
 * @date 2024-03-04
 */
@Service
public class StkDepInventoryServiceImpl implements IStkDepInventoryService 
{
    @Autowired
    private StkDepInventoryMapper stkDepInventoryMapper;

    /**
     * 查询科室库存
     * 
     * @param id 科室库存主键
     * @return 科室库存
     */
    @Override
    public StkDepInventory selectStkDepInventoryById(Long id)
    {
        return stkDepInventoryMapper.selectStkDepInventoryById(id);
    }

    /**
     * 查询科室库存列表
     * 
     * @param stkDepInventory 科室库存
     * @return 科室库存
     */
    @Override
    public List<StkDepInventory> selectStkDepInventoryList(StkDepInventory stkDepInventory)
    {
        return stkDepInventoryMapper.selectStkDepInventoryList(stkDepInventory);
    }

    /**
     * 新增科室库存
     * 
     * @param stkDepInventory 科室库存
     * @return 结果
     */
    @Override
    public int insertStkDepInventory(StkDepInventory stkDepInventory)
    {
        return stkDepInventoryMapper.insertStkDepInventory(stkDepInventory);
    }

    /**
     * 修改科室库存
     * 
     * @param stkDepInventory 科室库存
     * @return 结果
     */
    @Override
    public int updateStkDepInventory(StkDepInventory stkDepInventory)
    {
        return stkDepInventoryMapper.updateStkDepInventory(stkDepInventory);
    }

    /**
     * 批量删除科室库存
     * 
     * @param ids 需要删除的科室库存主键
     * @return 结果
     */
    @Override
    public int deleteStkDepInventoryByIds(Long[] ids)
    {
        return stkDepInventoryMapper.deleteStkDepInventoryByIds(ids);
    }

    /**
     * 删除科室库存信息
     * 
     * @param id 科室库存主键
     * @return 结果
     */
    @Override
    public int deleteStkDepInventoryById(Long id)
    {
        return stkDepInventoryMapper.deleteStkDepInventoryById(id);
    }
}

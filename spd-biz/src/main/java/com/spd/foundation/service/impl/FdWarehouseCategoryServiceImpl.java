package com.spd.foundation.service.impl;

import java.util.Date;
import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.foundation.mapper.FdWarehouseCategoryMapper;
import com.spd.foundation.domain.FdWarehouseCategory;
import com.spd.foundation.service.IFdWarehouseCategoryService;

/**
 * 库房分类Service业务层处理
 *
 * @author spd
 */
@Service
public class FdWarehouseCategoryServiceImpl implements IFdWarehouseCategoryService
{
    @Autowired
    private FdWarehouseCategoryMapper fdWarehouseCategoryMapper;

    /**
     * 查询库房分类
     *
     * @param warehouseCategoryId 库房分类主键
     * @return 库房分类
     */
    @Override
    public FdWarehouseCategory selectFdWarehouseCategoryByWarehouseCategoryId(Long warehouseCategoryId)
    {
        return fdWarehouseCategoryMapper.selectFdWarehouseCategoryByWarehouseCategoryId(warehouseCategoryId);
    }

    /**
     * 查询库房分类列表
     *
     * @param fdWarehouseCategory 库房分类
     * @return 库房分类
     */
    @Override
    public List<FdWarehouseCategory> selectFdWarehouseCategoryList(FdWarehouseCategory fdWarehouseCategory)
    {
        return fdWarehouseCategoryMapper.selectFdWarehouseCategoryList(fdWarehouseCategory);
    }

    /**
     * 新增库房分类
     *
     * @param fdWarehouseCategory 库房分类
     * @return 结果
     */
    @Override
    public int insertFdWarehouseCategory(FdWarehouseCategory fdWarehouseCategory)
    {
        fdWarehouseCategory.setCreateTime(DateUtils.getNowDate());
        return fdWarehouseCategoryMapper.insertFdWarehouseCategory(fdWarehouseCategory);
    }

    /**
     * 修改库房分类
     *
     * @param fdWarehouseCategory 库房分类
     * @return 结果
     */
    @Override
    public int updateFdWarehouseCategory(FdWarehouseCategory fdWarehouseCategory)
    {
        fdWarehouseCategory.setUpdateTime(DateUtils.getNowDate());
        return fdWarehouseCategoryMapper.updateFdWarehouseCategory(fdWarehouseCategory);
    }

    /**
     * 批量删除库房分类
     *
     * @param warehouseCategoryIds 需要删除的库房分类主键
     * @return 结果
     */
    @Override
    public int deleteFdWarehouseCategoryByWarehouseCategoryIds(Long warehouseCategoryIds)
    {
        FdWarehouseCategory fdWarehouseCategory = fdWarehouseCategoryMapper.selectFdWarehouseCategoryByWarehouseCategoryId(warehouseCategoryIds);
        if(fdWarehouseCategory == null){
            throw new ServiceException(String.format("库房分类：%s，不存在!", warehouseCategoryIds));
        }
        fdWarehouseCategory.setDelFlag(1);
        fdWarehouseCategory.setUpdateTime(new Date());
        fdWarehouseCategory.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        return fdWarehouseCategoryMapper.updateFdWarehouseCategory(fdWarehouseCategory);
    }

//    /**
//     * 删除库房分类信息
//     *
//     * @param warehouseCategoryId 库房分类主键
//     * @return 结果
//     */
//    @Override
//    public int deleteFdWarehouseCategoryByWarehouseCategoryId(Long warehouseCategoryId)
//    {
//        return fdWarehouseCategoryMapper.deleteFdWarehouseCategoryByWarehouseCategoryId(warehouseCategoryId);
//    }
}

package com.spd.foundation.service.impl;

import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.foundation.mapper.FdMaterialCategoryMapper;
import com.spd.foundation.domain.FdMaterialCategory;
import com.spd.foundation.service.IFdMaterialCategoryService;

/**
 * 耗材分类维护Service业务层处理
 *
 * @author spd
 * @date 2024-03-04
 */
@Service
public class FdMaterialCategoryServiceImpl implements IFdMaterialCategoryService
{
    @Autowired
    private FdMaterialCategoryMapper fdMaterialCategoryMapper;

    /**
     * 查询耗材分类维护
     *
     * @param materialCategoryId 耗材分类维护主键
     * @return 耗材分类维护
     */
    @Override
    public FdMaterialCategory selectFdMaterialCategoryByMaterialCategoryId(Long materialCategoryId)
    {
        return fdMaterialCategoryMapper.selectFdMaterialCategoryByMaterialCategoryId(materialCategoryId);
    }

    /**
     * 查询耗材分类维护列表
     *
     * @param fdMaterialCategory 耗材分类维护
     * @return 耗材分类维护
     */
    @Override
    public List<FdMaterialCategory> selectFdMaterialCategoryList(FdMaterialCategory fdMaterialCategory)
    {
        return fdMaterialCategoryMapper.selectFdMaterialCategoryList(fdMaterialCategory);
    }

    /**
     * 新增耗材分类维护
     *
     * @param fdMaterialCategory 耗材分类维护
     * @return 结果
     */
    @Override
    public int insertFdMaterialCategory(FdMaterialCategory fdMaterialCategory)
    {
        fdMaterialCategory.setCreateTime(DateUtils.getNowDate());
        return fdMaterialCategoryMapper.insertFdMaterialCategory(fdMaterialCategory);
    }

    /**
     * 修改耗材分类维护
     *
     * @param fdMaterialCategory 耗材分类维护
     * @return 结果
     */
    @Override
    public int updateFdMaterialCategory(FdMaterialCategory fdMaterialCategory)
    {
        fdMaterialCategory.setUpdateTime(DateUtils.getNowDate());
        return fdMaterialCategoryMapper.updateFdMaterialCategory(fdMaterialCategory);
    }

//    /**
//     * 批量删除耗材分类维护
//     *
//     * @param materialCategoryIds 需要删除的耗材分类维护主键
//     * @return 结果
//     */
//    @Override
//    public int deleteFdMaterialCategoryByMaterialCategoryIds(Long[] materialCategoryIds)
//    {
//        return fdMaterialCategoryMapper.deleteFdMaterialCategoryByMaterialCategoryIds(materialCategoryIds);
//    }

    /**
     * 删除耗材分类维护信息
     *
     * @param materialCategoryId 耗材分类维护主键
     * @return 结果
     */
    @Override
    public int deleteFdMaterialCategoryByMaterialCategoryId(Long materialCategoryId)
    {
        FdMaterialCategory fdMaterialCategory = fdMaterialCategoryMapper.
                selectFdMaterialCategoryByMaterialCategoryId(materialCategoryId);
        if(fdMaterialCategory == null){
            throw new ServiceException(String.format("耗材分类：%s，不存在!", materialCategoryId));
        }
        fdMaterialCategory.setDelFlag(1);
        fdMaterialCategory.setUpdateTime(DateUtils.getNowDate());
        fdMaterialCategory.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        return fdMaterialCategoryMapper.updateFdMaterialCategory(fdMaterialCategory);
    }
}

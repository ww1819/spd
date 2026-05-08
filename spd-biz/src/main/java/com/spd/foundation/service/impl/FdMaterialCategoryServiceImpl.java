package com.spd.foundation.service.impl;

import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.PinyinUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
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
    public FdMaterialCategory selectFdMaterialCategoryByMaterialCategoryId(String materialCategoryId)
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
        if (fdMaterialCategory != null && StringUtils.isEmpty(fdMaterialCategory.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            fdMaterialCategory.setTenantId(SecurityUtils.getCustomerId());
        }
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
        if (StringUtils.isEmpty(fdMaterialCategory.getMaterialCategoryId())) {
            fdMaterialCategory.setMaterialCategoryId(UUID7.generateUUID7());
        }
        // 新增时按材料类别名称自动生成拼音简码
        fdMaterialCategory.setPinyinCode(PinyinUtils.getPinyinInitials(fdMaterialCategory.getMaterialCategoryName()));
        fdMaterialCategory.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(fdMaterialCategory.getCreateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            fdMaterialCategory.setCreateBy(SecurityUtils.getUserIdStr());
        }
        if (StringUtils.isEmpty(fdMaterialCategory.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            fdMaterialCategory.setTenantId(SecurityUtils.getCustomerId());
        }
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
        // 修改时按最新材料类别名称自动更新拼音简码
        fdMaterialCategory.setPinyinCode(PinyinUtils.getPinyinInitials(fdMaterialCategory.getMaterialCategoryName()));
        fdMaterialCategory.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(fdMaterialCategory.getUpdateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            fdMaterialCategory.setUpdateBy(SecurityUtils.getUserIdStr());
        }
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
    public int deleteFdMaterialCategoryByMaterialCategoryId(String materialCategoryId)
    {
        FdMaterialCategory fdMaterialCategory = fdMaterialCategoryMapper.
                selectFdMaterialCategoryByMaterialCategoryId(materialCategoryId);
        if(fdMaterialCategory == null){
            throw new ServiceException(String.format("材料类别：%s，不存在!", materialCategoryId));
        }
        fdMaterialCategory.setDelFlag(1);
        fdMaterialCategory.setUpdateTime(DateUtils.getNowDate());
        fdMaterialCategory.setUpdateBy(SecurityUtils.getUserIdStr());
        return fdMaterialCategoryMapper.updateFdMaterialCategory(fdMaterialCategory);
    }

    @Override
    public int updatePinyinCodeByMaterialCategoryIds(String[] materialCategoryIds)
    {
        if (materialCategoryIds == null || materialCategoryIds.length == 0) {
            throw new ServiceException("请先选择需要更新简码的材料类别");
        }
        int affected = 0;
        for (String materialCategoryId : materialCategoryIds) {
            if (StringUtils.isEmpty(materialCategoryId)) {
                continue;
            }
            FdMaterialCategory category = fdMaterialCategoryMapper.selectFdMaterialCategoryByMaterialCategoryId(materialCategoryId);
            if (category == null || Integer.valueOf(1).equals(category.getDelFlag())) {
                continue;
            }
            category.setPinyinCode(PinyinUtils.getPinyinInitials(category.getMaterialCategoryName()));
            category.setUpdateBy(SecurityUtils.getUserIdStr());
            category.setUpdateTime(DateUtils.getNowDate());
            affected += fdMaterialCategoryMapper.updateFdMaterialCategory(category);
        }
        return affected;
    }

    @Override
    public int updatePinyinCodeForAllMaterialCategory()
    {
        FdMaterialCategory query = new FdMaterialCategory();
        query.setTenantId(SecurityUtils.getCustomerId());
        List<FdMaterialCategory> categoryList = fdMaterialCategoryMapper.selectFdMaterialCategoryList(query);
        int affected = 0;
        for (FdMaterialCategory category : categoryList) {
            if (category == null || StringUtils.isEmpty(category.getMaterialCategoryId())) {
                continue;
            }
            category.setPinyinCode(PinyinUtils.getPinyinInitials(category.getMaterialCategoryName()));
            category.setUpdateBy(SecurityUtils.getUserIdStr());
            category.setUpdateTime(DateUtils.getNowDate());
            affected += fdMaterialCategoryMapper.updateFdMaterialCategory(category);
        }
        return affected;
    }
}

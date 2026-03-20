package com.spd.foundation.service.impl;

import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.PinyinUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.foundation.mapper.FdFinanceCategoryMapper;
import com.spd.foundation.domain.FdFinanceCategory;
import com.spd.foundation.service.IFdFinanceCategoryService;

/**
 * 财务分类维护Service业务层处理
 *
 * @author spd
 * @date 2024-03-04
 */
@Service
public class FdFinanceCategoryServiceImpl implements IFdFinanceCategoryService
{
    @Autowired
    private FdFinanceCategoryMapper fdFinanceCategoryMapper;

    /**
     * 查询财务分类维护
     *
     * @param financeCategoryId 财务分类维护主键
     * @return 财务分类维护
     */
    @Override
    public FdFinanceCategory selectFdFinanceCategoryByFinanceCategoryId(Long financeCategoryId)
    {
        FdFinanceCategory row = fdFinanceCategoryMapper.selectFdFinanceCategoryByFinanceCategoryId(financeCategoryId);
        if (row != null)
        {
            SecurityUtils.ensureTenantAccess(row.getTenantId());
        }
        return row;
    }

    /**
     * 查询财务分类维护列表
     *
     * @param fdFinanceCategory 财务分类维护
     * @return 财务分类维护
     */
    @Override
    public List<FdFinanceCategory> selectFdFinanceCategoryList(FdFinanceCategory fdFinanceCategory)
    {
        if (fdFinanceCategory != null && StringUtils.isEmpty(fdFinanceCategory.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            fdFinanceCategory.setTenantId(SecurityUtils.getCustomerId());
        }
        return fdFinanceCategoryMapper.selectFdFinanceCategoryList(fdFinanceCategory);
    }

    /**
     * 新增财务分类维护
     *
     * @param fdFinanceCategory 财务分类维护
     * @return 结果
     */
    @Override
    public int insertFdFinanceCategory(FdFinanceCategory fdFinanceCategory)
    {
        fdFinanceCategory.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(fdFinanceCategory.getCreateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr()))
        {
            fdFinanceCategory.setCreateBy(SecurityUtils.getUserIdStr());
        }
        if (StringUtils.isEmpty(fdFinanceCategory.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            fdFinanceCategory.setTenantId(SecurityUtils.getCustomerId());
        }
        if (fdFinanceCategory.getDelFlag() == null)
        {
            fdFinanceCategory.setDelFlag(0);
        }
        if (StringUtils.isEmpty(fdFinanceCategory.getIsUse()))
        {
            fdFinanceCategory.setIsUse("1");
        }
        return fdFinanceCategoryMapper.insertFdFinanceCategory(fdFinanceCategory);
    }

    /**
     * 修改财务分类维护
     *
     * @param fdFinanceCategory 财务分类维护
     * @return 结果
     */
    @Override
    public int updateFdFinanceCategory(FdFinanceCategory fdFinanceCategory)
    {
        if (fdFinanceCategory.getFinanceCategoryId() == null)
        {
            throw new ServiceException("财务分类主键不能为空");
        }
        FdFinanceCategory before = fdFinanceCategoryMapper.selectFdFinanceCategoryByFinanceCategoryId(fdFinanceCategory.getFinanceCategoryId());
        if (before == null)
        {
            throw new ServiceException("财务分类不存在或已删除");
        }
        SecurityUtils.ensureTenantAccess(before.getTenantId());
        fdFinanceCategory.setTenantId(before.getTenantId());
        fdFinanceCategory.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(fdFinanceCategory.getUpdateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr()))
        {
            fdFinanceCategory.setUpdateBy(SecurityUtils.getUserIdStr());
        }
        return fdFinanceCategoryMapper.updateFdFinanceCategory(fdFinanceCategory);
    }

//    /**
//     * 批量删除财务分类维护
//     *
//     * @param financeCategoryIds 需要删除的财务分类维护主键
//     * @return 结果
//     */
//    @Override
//    public int deleteFdFinanceCategoryByFinanceCategoryIds(Long[] financeCategoryIds)
//    {
//        return fdFinanceCategoryMapper.deleteFdFinanceCategoryByFinanceCategoryIds(financeCategoryIds);
//    }

    /**
     * 删除财务分类维护信息
     *
     * @param financeCategoryId 财务分类维护主键
     * @return 结果
     */
    @Override
    public int deleteFdFinanceCategoryByFinanceCategoryId(Long financeCategoryId)
    {
        FdFinanceCategory fdFinanceCategory = fdFinanceCategoryMapper.selectFdFinanceCategoryByFinanceCategoryId(financeCategoryId);
        if (fdFinanceCategory == null)
        {
            throw new ServiceException(String.format("财务分类：%s，不存在!", financeCategoryId));
        }
        SecurityUtils.ensureTenantAccess(fdFinanceCategory.getTenantId());
        fdFinanceCategory.setDelFlag(1);
        fdFinanceCategory.setDeleteBy(SecurityUtils.getUserIdStr());
        fdFinanceCategory.setDeleteTime(DateUtils.getNowDate());
        fdFinanceCategory.setUpdateTime(DateUtils.getNowDate());
        fdFinanceCategory.setUpdateBy(SecurityUtils.getUserIdStr());
        return fdFinanceCategoryMapper.updateFdFinanceCategory(fdFinanceCategory);
    }

    /**
     * 批量更新财务分类名称简码（根据名称生成拼音首字母）
     */
    @Override
    public void updateReferred(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            FdFinanceCategory category = fdFinanceCategoryMapper.selectFdFinanceCategoryByFinanceCategoryId(id);
            if (category == null) {
                continue;
            }
            SecurityUtils.ensureTenantAccess(category.getTenantId());
            String name = category.getFinanceCategoryName();
            if (StringUtils.isEmpty(name)) {
                continue;
            }
            category.setReferredName(PinyinUtils.getPinyinInitials(name));
            fdFinanceCategoryMapper.updateFdFinanceCategory(category);
        }
    }
}

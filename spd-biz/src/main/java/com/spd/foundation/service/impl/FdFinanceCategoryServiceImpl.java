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
        return fdFinanceCategoryMapper.selectFdFinanceCategoryByFinanceCategoryId(financeCategoryId);
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
        fdFinanceCategory.setUpdateTime(DateUtils.getNowDate());
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
        if(fdFinanceCategory == null){
            throw new ServiceException(String.format("财务分类：%s，不存在!", financeCategoryId));
        }
        fdFinanceCategory.setDelFlag(1);
        fdFinanceCategory.setUpdateTime(DateUtils.getNowDate());
        fdFinanceCategory.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
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
            String name = category.getFinanceCategoryName();
            if (StringUtils.isEmpty(name)) {
                continue;
            }
            category.setReferredName(PinyinUtils.getPinyinInitials(name));
            fdFinanceCategoryMapper.updateFdFinanceCategory(category);
        }
    }
}

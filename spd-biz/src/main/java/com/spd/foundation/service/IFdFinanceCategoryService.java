package com.spd.foundation.service;

import java.util.List;
import com.spd.foundation.domain.FdFinanceCategory;

/**
 * 财务分类维护Service接口
 *
 * @author spd
 * @date 2024-03-04
 */
public interface IFdFinanceCategoryService
{
    /**
     * 查询财务分类维护
     *
     * @param financeCategoryId 财务分类维护主键
     * @return 财务分类维护
     */
    public FdFinanceCategory selectFdFinanceCategoryByFinanceCategoryId(Long financeCategoryId);

    /**
     * 查询财务分类维护列表
     *
     * @param fdFinanceCategory 财务分类维护
     * @return 财务分类维护集合
     */
    public List<FdFinanceCategory> selectFdFinanceCategoryList(FdFinanceCategory fdFinanceCategory);

    /**
     * 新增财务分类维护
     *
     * @param fdFinanceCategory 财务分类维护
     * @return 结果
     */
    public int insertFdFinanceCategory(FdFinanceCategory fdFinanceCategory);

    /**
     * 修改财务分类维护
     *
     * @param fdFinanceCategory 财务分类维护
     * @return 结果
     */
    public int updateFdFinanceCategory(FdFinanceCategory fdFinanceCategory);

//    /**
//     * 批量删除财务分类维护
//     *
//     * @param financeCategoryIds 需要删除的财务分类维护主键集合
//     * @return 结果
//     */
//    public int deleteFdFinanceCategoryByFinanceCategoryIds(Long[] financeCategoryIds);

    /**
     * 删除财务分类维护信息
     *
     * @param financeCategoryId 财务分类维护主键
     * @return 结果
     */
    public int deleteFdFinanceCategoryByFinanceCategoryId(Long financeCategoryId);

    /**
     * 批量更新财务分类名称简码（根据名称生成拼音首字母）
     *
     * @param ids 财务分类主键集合
     */
    void updateReferred(List<Long> ids);
}

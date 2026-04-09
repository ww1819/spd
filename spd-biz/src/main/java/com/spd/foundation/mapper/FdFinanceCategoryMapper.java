package com.spd.foundation.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.foundation.domain.FdFinanceCategory;

/**
 * 财务分类维护Mapper接口
 * 
 * @author spd
 * @date 2024-03-04
 */
public interface FdFinanceCategoryMapper 
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

    /**
     * 按编码与租户查询财务分类（未删除）
     */
    FdFinanceCategory selectFdFinanceCategoryByCodeAndTenantId(@Param("code") String code, @Param("tenantId") String tenantId);

    /**
     * 租户下 HIS 财务分类 ID 出现次数（可排除某主键）
     */
    int countFinanceCategoryByTenantAndHisId(@Param("tenantId") String tenantId, @Param("hisId") String hisId, @Param("excludeId") Long excludeId);

    /**
     * 按租户 + HIS 财务分类 ID 查一条（未删除）
     */
    FdFinanceCategory selectFdFinanceCategoryByTenantAndHisId(@Param("tenantId") String tenantId, @Param("hisId") String hisId);

    /**
     * 租户下同名财务分类数量（未删除；可排除某主键，用于唯一校验）
     */
    int countFinanceCategoryByTenantAndName(@Param("tenantId") String tenantId, @Param("name") String name, @Param("excludeId") Long excludeId);

    /**
     * 删除财务分类维护
     * 
     * @param financeCategoryId 财务分类维护主键
     * @return 结果
     */
    public int deleteFdFinanceCategoryByFinanceCategoryId(Long financeCategoryId);

    /**
     * 批量删除财务分类维护
     * 
     * @param financeCategoryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFdFinanceCategoryByFinanceCategoryIds(Long[] financeCategoryIds);
}

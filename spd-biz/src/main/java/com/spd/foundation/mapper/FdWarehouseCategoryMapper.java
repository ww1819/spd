package com.spd.foundation.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.foundation.domain.FdWarehouseCategory;

/**
 * 库房分类Mapper接口
 *
 * @author spd
 * @date 2024-04-12
 */
public interface FdWarehouseCategoryMapper
{
    /**
     * 查询库房分类
     *
     * @param warehouseCategoryId 库房分类主键
     * @return 库房分类
     */
    public FdWarehouseCategory selectFdWarehouseCategoryByWarehouseCategoryId(Long warehouseCategoryId);

    /**
     * 查询库房分类列表
     *
     * @param fdWarehouseCategory 库房分类
     * @return 库房分类集合
     */
    public List<FdWarehouseCategory> selectFdWarehouseCategoryList(FdWarehouseCategory fdWarehouseCategory);

    /**
     * 查询库房分类树形列表（平台不传 tenantId 则查全部；租户登录时传当前 customerId）
     *
     * @param tenantId 租户ID，可为空
     * @return 库房分类集合
     */
    public List<FdWarehouseCategory> selectFdWarehouseCategoryTree(@Param("tenantId") String tenantId);

    /**
     * 新增库房分类
     *
     * @param fdWarehouseCategory 库房分类
     * @return 结果
     */
    public int insertFdWarehouseCategory(FdWarehouseCategory fdWarehouseCategory);

    /**
     * 修改库房分类
     *
     * @param fdWarehouseCategory 库房分类
     * @return 结果
     */
    public int updateFdWarehouseCategory(FdWarehouseCategory fdWarehouseCategory);

    /**
     * 按编码与租户查询库房分类（未删除）
     */
    FdWarehouseCategory selectFdWarehouseCategoryByCodeAndTenantId(@Param("code") String code, @Param("tenantId") String tenantId);

    /**
     * 租户下 HIS 库房分类 ID 出现次数（可排除某主键）
     */
    int countWarehouseCategoryByTenantAndHisId(@Param("tenantId") String tenantId, @Param("hisId") String hisId, @Param("excludeId") Long excludeId);

    /**
     * 租户下同名库房分类数量（未删除；可排除某主键，用于唯一校验）
     */
    int countWarehouseCategoryByTenantAndName(@Param("tenantId") String tenantId, @Param("name") String name, @Param("excludeId") Long excludeId);

//    /**
//     * 删除库房分类
//     *
//     * @param warehouseCategoryId 库房分类主键
//     * @return 结果
//     */
//    public int deleteFdWarehouseCategoryByWarehouseCategoryId(Long warehouseCategoryId);
//
//    /**
//     * 批量删除库房分类
//     *
//     * @param warehouseCategoryIds 需要删除的数据主键集合
//     * @return 结果
//     */
//    public int deleteFdWarehouseCategoryByWarehouseCategoryIds(Long[] warehouseCategoryIds);
}

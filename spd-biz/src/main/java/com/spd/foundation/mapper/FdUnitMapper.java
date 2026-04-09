package com.spd.foundation.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.foundation.domain.FdUnit;

/**
 * 单位明细Mapper接口
 *
 * @author spd
 * @date 2024-04-07
 */
public interface FdUnitMapper
{
    /**
     * 查询单位明细
     *
     * @param unitId 单位明细主键
     * @return 单位明细
     */
    public FdUnit selectFdUnitByUnitId(Long unitId);

    /**
     * 查询单位明细列表
     *
     * @param fdUnit 单位明细
     * @return 单位明细集合
     */
    public List<FdUnit> selectFdUnitList(FdUnit fdUnit);

    /**
     * 新增单位明细
     *
     * @param fdUnit 单位明细
     * @return 结果
     */
    public int insertFdUnit(FdUnit fdUnit);

    /**
     * 修改单位明细
     *
     * @param fdUnit 单位明细
     * @return 结果
     */
    public int updateFdUnit(FdUnit fdUnit);

    /**
     * 当前租户（或平台空租户）下 D 开头单位编码的最大一条
     */
    String selectMaxUnitCode(@Param("tenantId") String tenantId);

    /**
     * 按租户 + 单位名称（精确匹配，不含模糊）查一条（未删除）
     */
    FdUnit selectFdUnitByTenantAndUnitName(@Param("tenantId") String tenantId, @Param("unitName") String unitName);

    /**
     * 租户下同名单位数量（未删除；可排除某 unit_id，用于唯一校验）
     */
    int countUnitByTenantAndUnitName(@Param("tenantId") String tenantId, @Param("unitName") String unitName, @Param("excludeUnitId") Long excludeUnitId);

//    /**
//     * 删除单位明细
//     *
//     * @param unitId 单位明细主键
//     * @return 结果
//     */
//    public int deleteFdUnitByUnitId(Long unitId);
//
//    /**
//     * 批量删除单位明细
//     *
//     * @param unitIds 需要删除的数据主键集合
//     * @return 结果
//     */
//    public int deleteFdUnitByUnitIds(Long[] unitIds);
}

package com.spd.foundation.mapper;

import java.util.List;
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

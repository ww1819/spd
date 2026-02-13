package com.spd.monitoring.mapper;

import com.spd.monitoring.domain.DeptFixedNumber;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 科室定数监测 Mapper
 */
public interface DeptFixedNumberMapper {

    /**
     * 查询科室定数监测列表
     */
    List<DeptFixedNumber> selectDeptFixedNumberList(DeptFixedNumber query);

    /**
     * 新增科室定数监测
     */
    int insertDeptFixedNumber(DeptFixedNumber entity);

    /**
     * 更新科室定数监测
     */
    int updateDeptFixedNumber(DeptFixedNumber entity);

    /**
     * 根据科室ID和物资ID查询单条记录
     */
    DeptFixedNumber selectByDepartmentAndMaterial(@Param("departmentId") Long departmentId,
                                                  @Param("materialId") Long materialId);
}


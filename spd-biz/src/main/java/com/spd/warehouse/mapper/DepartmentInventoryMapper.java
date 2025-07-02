package com.spd.warehouse.mapper;

import com.spd.warehouse.domain.DepartmentInventoryQuery;
import com.spd.warehouse.vo.DepartmentInventoryVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 科室库存查询Mapper接口
 */
@Mapper
@Repository
public interface DepartmentInventoryMapper {

    /**
     * 查询科室库存明细
     *
     * @param query 查询参数
     * @return 科室库存明细列表
     */
    List<DepartmentInventoryVo> selectDepartmentInventoryList(DepartmentInventoryQuery query);
} 
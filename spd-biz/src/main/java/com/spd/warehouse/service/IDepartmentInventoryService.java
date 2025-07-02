package com.spd.warehouse.service;

import com.spd.warehouse.domain.DepartmentInventoryQuery;
import com.spd.warehouse.vo.DepartmentInventoryVo;

import java.util.List;

/**
 * 科室库存查询Service接口
 */
public interface IDepartmentInventoryService {

    /**
     * 查询科室库存明细列表
     *
     * @param query 查询参数
     * @return 科室库存明细列表
     */
    List<DepartmentInventoryVo> selectDepartmentInventoryList(DepartmentInventoryQuery query);
} 
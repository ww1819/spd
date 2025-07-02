package com.spd.warehouse.service.impl;

import com.spd.common.annotation.DataSource;
import com.spd.common.enums.DataSourceType;
import com.spd.warehouse.domain.DepartmentInventoryQuery;
import com.spd.warehouse.mapper.DepartmentInventoryMapper;
import com.spd.warehouse.service.IDepartmentInventoryService;
import com.spd.warehouse.vo.DepartmentInventoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 科室库存查询Service业务层处理
 */
@Service
public class DepartmentInventoryServiceImpl implements IDepartmentInventoryService {

    @Autowired
    private DepartmentInventoryMapper departmentInventoryMapper;

    /**
     * 查询科室库存明细列表
     *
     * @param query 查询参数
     * @return 科室库存明细列表
     */
    @Override
    @DataSource(DataSourceType.SLAVE)
    public List<DepartmentInventoryVo> selectDepartmentInventoryList(DepartmentInventoryQuery query) {
        return departmentInventoryMapper.selectDepartmentInventoryList(query);
    }
} 
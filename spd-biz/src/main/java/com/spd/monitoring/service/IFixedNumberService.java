package com.spd.monitoring.service;

import com.spd.monitoring.domain.DeptFixedNumber;
import com.spd.monitoring.domain.FixedNumberSaveRequest;
import com.spd.monitoring.domain.WhFixedNumber;

import java.util.List;

/**
 * 定数监测 Service
 */
public interface IFixedNumberService {

    /**
     * 查询仓库定数监测列表
     */
    List<WhFixedNumber> selectWhFixedNumberList(WhFixedNumber query);

    /**
     * 查询科室定数监测列表
     */
    List<DeptFixedNumber> selectDeptFixedNumberList(DeptFixedNumber query);

    /**
     * 保存定数监测设置（仓库/科室）
     */
    void saveFixedNumber(FixedNumberSaveRequest request, String operator);

    /**
     * 根据主键删除一条定数监测（仓库或科室，自动判定）
     */
    int deleteFixedNumberById(String id);

}


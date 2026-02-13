package com.spd.monitoring.service.impl;

import com.spd.common.utils.uuid.UUID7;
import com.spd.monitoring.domain.DeptFixedNumber;
import com.spd.monitoring.domain.FixedNumberSaveRequest;
import com.spd.monitoring.domain.WhFixedNumber;
import com.spd.monitoring.mapper.DeptFixedNumberMapper;
import com.spd.monitoring.mapper.WhFixedNumberMapper;
import com.spd.monitoring.service.IFixedNumberService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class FixedNumberServiceImpl implements IFixedNumberService {

    @Resource
    private WhFixedNumberMapper whFixedNumberMapper;

    @Resource
    private DeptFixedNumberMapper deptFixedNumberMapper;

    @Override
    public List<WhFixedNumber> selectWhFixedNumberList(WhFixedNumber query) {
        return whFixedNumberMapper.selectWhFixedNumberList(query);
    }

    @Override
    public List<DeptFixedNumber> selectDeptFixedNumberList(DeptFixedNumber query) {
        return deptFixedNumberMapper.selectDeptFixedNumberList(query);
    }

    @Override
    public void saveFixedNumber(FixedNumberSaveRequest request, String operator) {
        if (request == null || request.getDetailList() == null) {
            return;
        }
        String type = request.getFixedNumberType();
        if (type == null || "".equals(type) || "1".equals(type)) {
            Long warehouseId = request.getWarehouseId();
            if (warehouseId == null) {
                return;
            }
            for (FixedNumberSaveRequest.Detail d : request.getDetailList()) {
                if (d.getMaterialId() == null) {
                    continue;
                }
                WhFixedNumber existing = whFixedNumberMapper.selectByWarehouseAndMaterial(warehouseId, d.getMaterialId());
                if (existing == null) {
                    WhFixedNumber entity = new WhFixedNumber();
                    entity.setId(UUID7.generateUUID7());
                    entity.setWarehouseId(warehouseId);
                    entity.setMaterialId(d.getMaterialId());
                    entity.setUpperLimit(d.getUpperLimit());
                    entity.setLowerLimit(d.getLowerLimit());
                    entity.setExpiryReminder(d.getExpiryReminder());
                    entity.setMonitoring(d.getMonitoring());
                    entity.setLocation(d.getLocation());
                    entity.setLocationId(d.getLocationId());
                    entity.setDelFlag(0);
                    entity.setCreateBy(operator);
                    whFixedNumberMapper.insertWhFixedNumber(entity);
                } else {
                    existing.setUpperLimit(d.getUpperLimit());
                    existing.setLowerLimit(d.getLowerLimit());
                    existing.setExpiryReminder(d.getExpiryReminder());
                    existing.setMonitoring(d.getMonitoring());
                    existing.setLocation(d.getLocation());
                    existing.setLocationId(d.getLocationId());
                    existing.setUpdateBy(operator);
                    whFixedNumberMapper.updateWhFixedNumber(existing);
                }
            }
        } else if ("2".equals(type)) {
            Long departmentId = request.getDepartmentId();
            if (departmentId == null) {
                return;
            }
            for (FixedNumberSaveRequest.Detail d : request.getDetailList()) {
                if (d.getMaterialId() == null) {
                    continue;
                }
                DeptFixedNumber existing = deptFixedNumberMapper.selectByDepartmentAndMaterial(departmentId, d.getMaterialId());
                if (existing == null) {
                    DeptFixedNumber entity = new DeptFixedNumber();
                    entity.setId(UUID7.generateUUID7());
                    entity.setDepartmentId(departmentId);
                    entity.setMaterialId(d.getMaterialId());
                    entity.setUpperLimit(d.getUpperLimit());
                    entity.setLowerLimit(d.getLowerLimit());
                    entity.setExpiryReminder(d.getExpiryReminder());
                    entity.setMonitoring(d.getMonitoring());
                    entity.setLocation(d.getLocation());
                    entity.setLocationId(d.getLocationId());
                    entity.setDelFlag(0);
                    entity.setCreateBy(operator);
                    deptFixedNumberMapper.insertDeptFixedNumber(entity);
                } else {
                    existing.setUpperLimit(d.getUpperLimit());
                    existing.setLowerLimit(d.getLowerLimit());
                    existing.setExpiryReminder(d.getExpiryReminder());
                    existing.setMonitoring(d.getMonitoring());
                    existing.setLocation(d.getLocation());
                    existing.setLocationId(d.getLocationId());
                    existing.setUpdateBy(operator);
                    deptFixedNumberMapper.updateDeptFixedNumber(existing);
                }
            }
        }
    }

    @Override
    public int deleteFixedNumberById(String id) {
        // 先尝试删除仓库定数
        int rows = whFixedNumberMapper.deleteWhFixedNumberById(id);
        if (rows == 0) {
            // 如果仓库表中没有，再尝试删除科室定数
            rows = deptFixedNumberMapper.deleteDeptFixedNumberById(id);
        }
        return rows;
    }
}


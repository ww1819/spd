package com.spd.equipment.service.impl;

import java.util.List;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.PinyinUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.equipment.domain.SbEquipmentSupplier;
import com.spd.equipment.mapper.SbEquipmentSupplierMapper;
import com.spd.equipment.service.ISbEquipmentSupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SbEquipmentSupplierServiceImpl implements ISbEquipmentSupplierService {

    @Autowired
    private SbEquipmentSupplierMapper mapper;

    @Override
    public List<SbEquipmentSupplier> selectList(SbEquipmentSupplier q) {
        if (q != null && StringUtils.isEmpty(q.getCustomerId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            q.setCustomerId(SecurityUtils.getCustomerId());
        }
        return mapper.selectList(q);
    }

    @Override
    public SbEquipmentSupplier selectById(String id) {
        return mapper.selectById(id);
    }

    @Override
    public int insert(SbEquipmentSupplier row) {
        if (StringUtils.isEmpty(row.getCustomerId())) row.setCustomerId(SecurityUtils.getCustomerId());
        if (StringUtils.isNotEmpty(row.getName())) row.setNamePinyin(PinyinUtils.getPinyinInitials(row.getName()));
        row.setId(UUID7.generateUUID7());
        row.setDelFlag(0);
        row.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getCreateBy())) row.setCreateBy(SecurityUtils.getUserIdStr());
        return mapper.insert(row);
    }

    @Override
    public int update(SbEquipmentSupplier row) {
        if (StringUtils.isNotEmpty(row.getName())) row.setNamePinyin(PinyinUtils.getPinyinInitials(row.getName()));
        row.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getUpdateBy())) row.setUpdateBy(SecurityUtils.getUserIdStr());
        return mapper.update(row);
    }

    @Override
    public int deleteById(String id) {
        SbEquipmentSupplier row = new SbEquipmentSupplier();
        row.setId(id);
        row.setDelFlag(1);
        row.setDelBy(SecurityUtils.getUserIdStr());
        row.setDelTime(DateUtils.getNowDate());
        row.setUpdateBy(row.getDelBy());
        row.setUpdateTime(row.getDelTime());
        return mapper.update(row);
    }
}

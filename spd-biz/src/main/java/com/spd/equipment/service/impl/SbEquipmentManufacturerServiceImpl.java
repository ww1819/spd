package com.spd.equipment.service.impl;

import java.util.List;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.PinyinUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.equipment.domain.SbEquipmentManufacturer;
import com.spd.equipment.mapper.SbEquipmentManufacturerMapper;
import com.spd.equipment.service.ISbEquipmentManufacturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SbEquipmentManufacturerServiceImpl implements ISbEquipmentManufacturerService {

    @Autowired
    private SbEquipmentManufacturerMapper mapper;

    @Override
    public List<SbEquipmentManufacturer> selectList(SbEquipmentManufacturer q) {
        if (q != null && StringUtils.isEmpty(q.getCustomerId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            q.setCustomerId(SecurityUtils.getCustomerId());
        }
        return mapper.selectList(q);
    }

    @Override
    public SbEquipmentManufacturer selectById(String id) {
        return mapper.selectById(id);
    }

    @Override
    public SbEquipmentManufacturer getOrCreateByName(String name) {
        if (StringUtils.isEmpty(name)) return null;
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isEmpty(customerId)) return null;
        SbEquipmentManufacturer q = new SbEquipmentManufacturer();
        q.setCustomerId(customerId);
        q.setName(name.trim());
        List<SbEquipmentManufacturer> list = mapper.selectList(q);
        if (list != null) {
            for (SbEquipmentManufacturer m : list) {
                if (name.trim().equals(m.getName())) return m;
            }
        }
        SbEquipmentManufacturer row = new SbEquipmentManufacturer();
        row.setName(name.trim());
        row.setCustomerId(customerId);
        insert(row);
        return row;
    }

    @Override
    public int insert(SbEquipmentManufacturer row) {
        if (StringUtils.isEmpty(row.getCustomerId())) row.setCustomerId(SecurityUtils.getCustomerId());
        if (StringUtils.isNotEmpty(row.getName())) row.setNamePinyin(PinyinUtils.getPinyinInitials(row.getName()));
        row.setId(UUID7.generateUUID7());
        row.setDelFlag(0);
        row.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getCreateBy())) row.setCreateBy(SecurityUtils.getUserIdStr());
        return mapper.insert(row);
    }

    @Override
    public int update(SbEquipmentManufacturer row) {
        if (StringUtils.isNotEmpty(row.getName())) row.setNamePinyin(PinyinUtils.getPinyinInitials(row.getName()));
        row.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getUpdateBy())) row.setUpdateBy(SecurityUtils.getUserIdStr());
        return mapper.update(row);
    }

    @Override
    public int deleteById(String id) {
        SbEquipmentManufacturer row = new SbEquipmentManufacturer();
        row.setId(id);
        row.setDelFlag(1);
        row.setDelBy(SecurityUtils.getUserIdStr());
        row.setDelTime(DateUtils.getNowDate());
        row.setUpdateBy(row.getDelBy());
        row.setUpdateTime(row.getDelTime());
        return mapper.update(row);
    }
}

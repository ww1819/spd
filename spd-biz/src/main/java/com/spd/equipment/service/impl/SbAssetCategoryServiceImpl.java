package com.spd.equipment.service.impl;

import java.util.List;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.equipment.domain.SbAssetCategory;
import com.spd.equipment.mapper.SbAssetCategoryMapper;
import com.spd.equipment.service.ISbAssetCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SbAssetCategoryServiceImpl implements ISbAssetCategoryService {

    @Autowired
    private SbAssetCategoryMapper mapper;

    @Override
    public List<SbAssetCategory> selectList(SbAssetCategory q) {
        if (q != null && StringUtils.isEmpty(q.getCustomerId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            q.setCustomerId(SecurityUtils.getCustomerId());
        }
        return mapper.selectList(q);
    }

    @Override
    public SbAssetCategory selectById(String id) {
        return mapper.selectById(id);
    }

    @Override
    public int insert(SbAssetCategory row) {
        if (StringUtils.isEmpty(row.getCustomerId())) row.setCustomerId(SecurityUtils.getCustomerId());
        row.setId(UUID7.generateUUID7());
        row.setDelFlag(0);
        row.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getCreateBy())) row.setCreateBy(SecurityUtils.getUserIdStr());
        return mapper.insert(row);
    }

    @Override
    public int update(SbAssetCategory row) {
        row.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getUpdateBy())) row.setUpdateBy(SecurityUtils.getUserIdStr());
        return mapper.update(row);
    }

    @Override
    public int deleteById(String id) {
        SbAssetCategory row = new SbAssetCategory();
        row.setId(id);
        row.setDelFlag(1);
        row.setDelBy(SecurityUtils.getUserIdStr());
        row.setDelTime(DateUtils.getNowDate());
        row.setUpdateBy(row.getDelBy());
        row.setUpdateTime(row.getDelTime());
        return mapper.update(row);
    }
}

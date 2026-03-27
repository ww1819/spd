package com.spd.equipment.service.impl;

import java.util.List;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.equipment.domain.SbAssetInventoryItemPrint;
import com.spd.equipment.mapper.SbAssetInventoryItemPrintMapper;
import com.spd.equipment.service.ISbAssetInventoryItemPrintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 资产盘点单明细与标签打印关联表 Service 实现
 */
@Service
public class SbAssetInventoryItemPrintServiceImpl implements ISbAssetInventoryItemPrintService {

    @Autowired
    private SbAssetInventoryItemPrintMapper mapper;

    @Override
    public List<SbAssetInventoryItemPrint> selectList(SbAssetInventoryItemPrint q) {
        if (q != null && StringUtils.isEmpty(q.getCustomerId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            q.setCustomerId(SecurityUtils.getCustomerId());
        }
        return mapper.selectList(q);
    }

    @Override
    public List<SbAssetInventoryItemPrint> selectByInventoryItemId(String inventoryItemId) {
        return mapper.selectByInventoryItemId(inventoryItemId);
    }

    @Override
    public SbAssetInventoryItemPrint selectById(String id) {
        return mapper.selectById(id);
    }

    @Override
    public int insert(SbAssetInventoryItemPrint row) {
        if (StringUtils.isEmpty(row.getCustomerId())) {
            row.setCustomerId(SecurityUtils.getCustomerId());
        }
        if (StringUtils.isEmpty(row.getId())) {
            row.setId(UUID7.generateUUID7());
        }
        row.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getCreateBy())) {
            row.setCreateBy(SecurityUtils.getUserIdStr());
        }
        row.setUpdateTime(row.getCreateTime());
        if (StringUtils.isEmpty(row.getUpdateBy())) {
            row.setUpdateBy(row.getCreateBy());
        }
        if (row.getDelFlag() == null) row.setDelFlag(0);
        return mapper.insert(row);
    }

    @Override
    public int deleteById(String id) {
        return mapper.deleteById(id, SecurityUtils.getUserIdStr());
    }
}

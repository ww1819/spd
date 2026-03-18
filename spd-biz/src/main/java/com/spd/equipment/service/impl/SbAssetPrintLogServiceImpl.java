package com.spd.equipment.service.impl;

import java.util.List;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.equipment.domain.SbAssetPrintLog;
import com.spd.equipment.mapper.SbAssetPrintLogMapper;
import com.spd.equipment.service.ISbAssetPrintLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 资产条码打印日志表 Service 实现
 */
@Service
public class SbAssetPrintLogServiceImpl implements ISbAssetPrintLogService {

    @Autowired
    private SbAssetPrintLogMapper mapper;

    @Override
    public List<SbAssetPrintLog> selectList(SbAssetPrintLog q) {
        if (q != null && StringUtils.isEmpty(q.getCustomerId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            q.setCustomerId(SecurityUtils.getCustomerId());
        }
        return mapper.selectList(q);
    }

    @Override
    public SbAssetPrintLog selectById(String id) {
        return mapper.selectById(id);
    }

    @Override
    public int insert(SbAssetPrintLog row) {
        if (StringUtils.isEmpty(row.getCustomerId())) {
            row.setCustomerId(SecurityUtils.getCustomerId());
        }
        if (StringUtils.isEmpty(row.getId())) {
            row.setId(UUID7.generateUUID7());
        }
        row.setCreateTime(row.getPrintTime() != null ? row.getPrintTime() : DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getCreateBy())) {
            row.setCreateBy(SecurityUtils.getUserIdStr());
        }
        row.setUpdateTime(row.getCreateTime());
        if (StringUtils.isEmpty(row.getUpdateBy())) {
            row.setUpdateBy(row.getCreateBy());
        }
        if (row.getDelFlag() == null) {
            row.setDelFlag(0);
        }
        if (row.getPrintUserId() == null && SecurityUtils.getUserId() != null) {
            row.setPrintUserId(SecurityUtils.getUserId());
        }
        if (StringUtils.isEmpty(row.getPrintUserName()) && SecurityUtils.getUsername() != null) {
            row.setPrintUserName(SecurityUtils.getUsername());
        }
        return mapper.insert(row);
    }

    @Override
    public int deleteById(String id) {
        return mapper.deleteById(id, SecurityUtils.getUserIdStr());
    }
}

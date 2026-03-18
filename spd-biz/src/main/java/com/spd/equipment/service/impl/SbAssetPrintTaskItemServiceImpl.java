package com.spd.equipment.service.impl;

import java.util.List;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.equipment.domain.SbAssetPrintTaskItem;
import com.spd.equipment.mapper.SbAssetPrintTaskItemMapper;
import com.spd.equipment.service.ISbAssetPrintTaskItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 资产条码打印任务明细表 Service 实现
 */
@Service
public class SbAssetPrintTaskItemServiceImpl implements ISbAssetPrintTaskItemService {

    @Autowired
    private SbAssetPrintTaskItemMapper mapper;

    @Override
    public List<SbAssetPrintTaskItem> selectList(SbAssetPrintTaskItem q) {
        if (q != null && StringUtils.isEmpty(q.getCustomerId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            q.setCustomerId(SecurityUtils.getCustomerId());
        }
        return mapper.selectList(q);
    }

    @Override
    public List<SbAssetPrintTaskItem> selectByTaskId(String taskId) {
        return mapper.selectByTaskId(taskId);
    }

    @Override
    public SbAssetPrintTaskItem selectById(String id) {
        return mapper.selectById(id);
    }

    @Override
    public int insert(SbAssetPrintTaskItem row) {
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
        if (row.getDelFlag() == null) {
            row.setDelFlag(0);
        }
        if (row.getPrintCount() == null) row.setPrintCount(0);
        if (StringUtils.isEmpty(row.getPrintStatus())) row.setPrintStatus("未打印");
        if (StringUtils.isEmpty(row.getBarcodeType())) row.setBarcodeType("QRCODE");
        return mapper.insert(row);
    }

    @Override
    public int insertBatch(List<SbAssetPrintTaskItem> list) {
        if (list == null || list.isEmpty()) return 0;
        String createBy = SecurityUtils.getUserIdStr();
        String customerId = SecurityUtils.getCustomerId();
        java.util.Date now = DateUtils.getNowDate();
        for (SbAssetPrintTaskItem item : list) {
            if (StringUtils.isEmpty(item.getId())) item.setId(UUID7.generateUUID7());
            if (StringUtils.isEmpty(item.getCustomerId())) item.setCustomerId(customerId);
            item.setCreateTime(now);
            item.setCreateBy(createBy);
            item.setUpdateTime(now);
            item.setUpdateBy(createBy);
            if (item.getDelFlag() == null) item.setDelFlag(0);
            if (item.getPrintCount() == null) item.setPrintCount(0);
            if (StringUtils.isEmpty(item.getPrintStatus())) item.setPrintStatus("未打印");
            if (StringUtils.isEmpty(item.getBarcodeType())) item.setBarcodeType("QRCODE");
        }
        return mapper.insertBatch(list);
    }

    @Override
    public int update(SbAssetPrintTaskItem row) {
        row.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getUpdateBy())) {
            row.setUpdateBy(SecurityUtils.getUserIdStr());
        }
        return mapper.update(row);
    }

    @Override
    public int deleteById(String id) {
        return mapper.deleteById(id, SecurityUtils.getUserIdStr());
    }
}

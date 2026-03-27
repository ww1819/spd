package com.spd.equipment.service.impl;

import java.util.List;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.equipment.domain.SbAssetPrintTask;
import com.spd.equipment.mapper.SbAssetPrintTaskMapper;
import com.spd.equipment.service.ISbAssetPrintTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 资产条码打印任务主表 Service 实现
 */
@Service
public class SbAssetPrintTaskServiceImpl implements ISbAssetPrintTaskService {

    @Autowired
    private SbAssetPrintTaskMapper mapper;

    @Override
    public List<SbAssetPrintTask> selectList(SbAssetPrintTask q) {
        if (q != null && StringUtils.isEmpty(q.getCustomerId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            q.setCustomerId(SecurityUtils.getCustomerId());
        }
        return mapper.selectList(q);
    }

    @Override
    public SbAssetPrintTask selectById(String id) {
        return mapper.selectById(id);
    }

    @Override
    public SbAssetPrintTask selectByTaskNo(String customerId, String taskNo) {
        return mapper.selectByTaskNo(customerId, taskNo);
    }

    @Override
    public String generateTaskNo(String customerId) {
        if (StringUtils.isEmpty(customerId)) customerId = SecurityUtils.getCustomerId();
        String datePrefix = "DYRW" + DateUtils.dateTimeNow("yyyyMMdd");
        Integer maxSeq = mapper.selectMaxTaskNoSeqToday(customerId, datePrefix);
        int next = (maxSeq == null ? 0 : maxSeq) + 1;
        return datePrefix + String.format("%04d", next);
    }

    @Override
    public int insert(SbAssetPrintTask row) {
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
        if (row.getTotalCount() == null) row.setTotalCount(0);
        if (row.getPrintedCount() == null) row.setPrintedCount(0);
        if (StringUtils.isEmpty(row.getStatus())) row.setStatus("NEW");
        return mapper.insert(row);
    }

    @Override
    public int update(SbAssetPrintTask row) {
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

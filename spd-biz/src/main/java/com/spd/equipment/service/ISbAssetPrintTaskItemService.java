package com.spd.equipment.service;

import java.util.List;
import com.spd.equipment.domain.SbAssetPrintTaskItem;

/**
 * 资产条码打印任务明细表 Service
 */
public interface ISbAssetPrintTaskItemService {

    List<SbAssetPrintTaskItem> selectList(SbAssetPrintTaskItem q);
    List<SbAssetPrintTaskItem> selectByTaskId(String taskId);
    SbAssetPrintTaskItem selectById(String id);
    int insert(SbAssetPrintTaskItem row);
    int insertBatch(List<SbAssetPrintTaskItem> list);
    int update(SbAssetPrintTaskItem row);
    /** 逻辑删除 */
    int deleteById(String id);
}

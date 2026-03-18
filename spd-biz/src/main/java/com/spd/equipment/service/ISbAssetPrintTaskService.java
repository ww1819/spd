package com.spd.equipment.service;

import java.util.List;
import com.spd.equipment.domain.SbAssetPrintTask;

/**
 * 资产条码打印任务主表 Service
 */
public interface ISbAssetPrintTaskService {

    List<SbAssetPrintTask> selectList(SbAssetPrintTask q);
    SbAssetPrintTask selectById(String id);
    SbAssetPrintTask selectByTaskNo(String customerId, String taskNo);
    int insert(SbAssetPrintTask row);
    int update(SbAssetPrintTask row);
    /** 逻辑删除 */
    int deleteById(String id);
}

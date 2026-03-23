package com.spd.equipment.service;

import java.util.List;
import com.spd.equipment.domain.SbAssetPrintLog;

/**
 * 资产条码打印日志表 Service
 */
public interface ISbAssetPrintLogService {

    List<SbAssetPrintLog> selectList(SbAssetPrintLog q);
    SbAssetPrintLog selectById(String id);
    int insert(SbAssetPrintLog row);
    /** 逻辑删除 */
    int deleteById(String id);
}

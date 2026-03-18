package com.spd.equipment.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.equipment.domain.SbAssetPrintTask;

/**
 * 资产条码打印任务主表 Mapper
 */
public interface SbAssetPrintTaskMapper {

    List<SbAssetPrintTask> selectList(SbAssetPrintTask q);
    SbAssetPrintTask selectById(String id);
    SbAssetPrintTask selectByTaskNo(@Param("customerId") String customerId, @Param("taskNo") String taskNo);
    int insert(SbAssetPrintTask row);
    int update(SbAssetPrintTask row);
    /** 逻辑删除 */
    int deleteById(@Param("id") String id, @Param("delBy") String delBy);
}

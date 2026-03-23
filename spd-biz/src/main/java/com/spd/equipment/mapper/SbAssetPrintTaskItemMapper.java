package com.spd.equipment.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.equipment.domain.SbAssetPrintTaskItem;

/**
 * 资产条码打印任务明细表 Mapper
 */
public interface SbAssetPrintTaskItemMapper {

    List<SbAssetPrintTaskItem> selectList(SbAssetPrintTaskItem q);
    List<SbAssetPrintTaskItem> selectByTaskId(@Param("taskId") String taskId);
    SbAssetPrintTaskItem selectById(String id);
    int insert(SbAssetPrintTaskItem row);
    int insertBatch(@Param("list") List<SbAssetPrintTaskItem> list);
    int update(SbAssetPrintTaskItem row);
    /** 逻辑删除 */
    int deleteById(@Param("id") String id, @Param("delBy") String delBy);
    /** 按任务ID逻辑删除明细 */
    int deleteByTaskId(@Param("taskId") String taskId, @Param("delBy") String delBy);
}

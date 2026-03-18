package com.spd.equipment.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.equipment.domain.SbAssetPrintLog;

/**
 * 资产条码打印日志表 Mapper
 */
public interface SbAssetPrintLogMapper {

    List<SbAssetPrintLog> selectList(SbAssetPrintLog q);
    SbAssetPrintLog selectById(String id);
    int insert(SbAssetPrintLog row);
    /** 逻辑删除 */
    int deleteById(@Param("id") String id, @Param("delBy") String delBy);
}

package com.spd.equipment.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.equipment.domain.SbAssetInventory;

/**
 * 资产盘点单主表 Mapper
 */
public interface SbAssetInventoryMapper {

    List<SbAssetInventory> selectList(SbAssetInventory q);
    SbAssetInventory selectById(String id);
    SbAssetInventory selectByOrderNo(@Param("customerId") String customerId, @Param("orderNo") String orderNo);
    /** 当日单号最大序号（order_no 格式 PDyyyyMMddXXXX） */
    Integer selectMaxOrderNoSeqToday(@Param("customerId") String customerId, @Param("datePrefix") String datePrefix);
    int insert(SbAssetInventory row);
    int update(SbAssetInventory row);
    int deleteById(@Param("id") String id, @Param("delBy") String delBy);
}

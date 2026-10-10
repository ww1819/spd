package com.spd.caigou.mapper;

import com.spd.caigou.domain.PurchasePriceAdjust;
import com.spd.caigou.domain.PurchasePriceAdjustEntry;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 采购调价单 Mapper
 */
public interface PurchasePriceAdjustMapper {

    PurchasePriceAdjust selectPurchasePriceAdjustById(Long id);

    List<PurchasePriceAdjust> selectPurchasePriceAdjustList(PurchasePriceAdjust query);

    List<PurchasePriceAdjustEntry> selectEntryListByParentId(Long parentId);

    String selectMaxBillNo(@Param("date") String date);

    int insertPurchasePriceAdjust(PurchasePriceAdjust bill);

    int updatePurchasePriceAdjust(PurchasePriceAdjust bill);

    int updateBillStatus(PurchasePriceAdjust bill);

    int insertPurchasePriceAdjustEntry(PurchasePriceAdjustEntry entry);

    int batchInsertEntry(@Param("list") List<PurchasePriceAdjustEntry> list);

    int deleteEntryByParentId(@Param("parentId") Long parentId, @Param("updateBy") String updateBy);

    int deletePurchasePriceAdjustById(@Param("id") Long id, @Param("updateBy") String updateBy);
}

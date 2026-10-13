package com.spd.warehouse.mapper;

import com.spd.warehouse.domain.StkScrapBill;
import com.spd.warehouse.domain.StkScrapBillEntry;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 库房报废单 Mapper
 */
public interface StkScrapBillMapper {

    StkScrapBill selectStkScrapBillById(Long id);

    List<StkScrapBill> selectStkScrapBillList(StkScrapBill query);

    List<StkScrapBillEntry> selectEntryListByParentId(Long parentId);

    String selectMaxBillNo(@Param("date") String date);

    int insertStkScrapBill(StkScrapBill bill);

    int updateStkScrapBill(StkScrapBill bill);

    int updateBillStatus(StkScrapBill bill);

    int batchInsertEntry(@Param("list") List<StkScrapBillEntry> list);

    int deleteEntryByParentId(@Param("parentId") Long parentId, @Param("updateBy") String updateBy);

    int deleteStkScrapBillById(@Param("id") Long id, @Param("updateBy") String updateBy);
}

package com.spd.warehouse.mapper;

import com.spd.common.core.page.TotalInfo;
import com.spd.warehouse.domain.StkScrapBill;
import com.spd.warehouse.domain.StkScrapBillEntry;
import com.spd.warehouse.domain.StkScrapQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 库房报废单 Mapper
 */
public interface StkScrapBillMapper {

    StkScrapBill selectStkScrapBillById(Long id);

    List<StkScrapBill> selectStkScrapBillList(StkScrapBill query);

    List<StkScrapBillEntry> selectEntryListByParentId(Long parentId);

    List<StkScrapQueryVo> selectScrapDetailList(StkScrapQueryVo query);

    TotalInfo selectScrapDetailTotal(StkScrapQueryVo query);

    List<StkScrapQueryVo> selectScrapSummaryList(StkScrapQueryVo query);

    TotalInfo selectScrapSummaryTotal(StkScrapQueryVo query);

    String selectMaxBillNo(@Param("date") String date);

    int insertStkScrapBill(StkScrapBill bill);

    int updateStkScrapBill(StkScrapBill bill);

    int updateBillStatus(StkScrapBill bill);

    int batchInsertEntry(@Param("list") List<StkScrapBillEntry> list);

    int deleteEntryByParentId(@Param("parentId") Long parentId, @Param("updateBy") String updateBy);

    int deleteStkScrapBillById(@Param("id") Long id, @Param("updateBy") String updateBy);
}

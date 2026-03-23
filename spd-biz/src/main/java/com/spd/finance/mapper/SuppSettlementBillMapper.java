package com.spd.finance.mapper;

import com.spd.finance.domain.SuppSettlementBill;
import com.spd.finance.domain.SuppSettlementBillEntry;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 供应商结算单Mapper
 */
public interface SuppSettlementBillMapper {

    SuppSettlementBill selectById(String id);

    List<SuppSettlementBill> selectList(SuppSettlementBill query);

    int insert(SuppSettlementBill row);

    int update(SuppSettlementBill row);

    int insertEntry(SuppSettlementBillEntry row);

    int insertEntryBatch(List<SuppSettlementBillEntry> list);

    List<SuppSettlementBillEntry> selectEntriesByParenId(String parenId);

    int updateAuditStatus(@Param("id") String id, @Param("auditBy") String auditBy, @Param("auditTime") java.util.Date auditTime);

    String selectMaxBillNo(@Param("prefix") String prefix);
}

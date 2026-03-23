package com.spd.finance.mapper;

import com.spd.finance.domain.WhSettlementBill;
import com.spd.finance.domain.WhSettlementBillEntry;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 仓库结算单Mapper
 */
public interface WhSettlementBillMapper {

    WhSettlementBill selectById(String id);

    List<WhSettlementBill> selectList(WhSettlementBill query);

    int insert(WhSettlementBill row);

    int update(WhSettlementBill row);

    int insertEntry(WhSettlementBillEntry row);

    int insertEntryBatch(List<WhSettlementBillEntry> list);

    List<WhSettlementBillEntry> selectEntriesByParenId(String parenId);

    /** 逻辑删除该单下所有明细（保存新明细前调用）；设置 del_flag=1, delete_by, delete_time */
    int deleteEntriesByParenId(@Param("parenId") String parenId, @Param("deleteBy") String deleteBy);

    /** 逻辑删除指定明细（审核后不可用）；设置 del_flag=1, delete_by, delete_time */
    int deleteEntriesByIds(@Param("parenId") String parenId, @Param("ids") List<String> ids, @Param("deleteBy") String deleteBy);

    /** 审核：更新 audit_status=1, audit_by, audit_time */
    int updateAuditStatus(@Param("id") String id, @Param("auditBy") String auditBy, @Param("auditTime") java.util.Date auditTime);

    /** 逻辑删除 */
    int deleteById(@Param("id") String id, @Param("deleteBy") String deleteBy);

    /** 最大单号（如 CKD+yyyyMMdd+流水） */
    String selectMaxBillNo(@Param("prefix") String prefix);

    /** 提取数据：未结算的入库单明细（bill_type=101） */
    List<WhSettlementBillEntry> selectUnsettledInboundEntries(@Param("warehouseId") Long warehouseId, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime, @Param("tenantId") String tenantId);

    /** 提取数据：未结算的出库单明细（bill_type=201） */
    List<WhSettlementBillEntry> selectUnsettledOutboundEntries(@Param("warehouseId") Long warehouseId, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime, @Param("tenantId") String tenantId);
}

package com.spd.finance.mapper;

import com.spd.finance.domain.SuppSettlementInvoice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 供应商结算单与发票关联Mapper
 */
public interface SuppSettlementInvoiceMapper {

    int insert(SuppSettlementInvoice row);

    /** 逻辑删除：设置 delete_by、delete_time */
    int logicalDeleteBySuppSettlementAndInvoice(@Param("suppSettlementId") String suppSettlementId, @Param("invoiceId") String invoiceId, @Param("deleteBy") String deleteBy);

    List<SuppSettlementInvoice> selectBySuppSettlementId(String suppSettlementId);
}

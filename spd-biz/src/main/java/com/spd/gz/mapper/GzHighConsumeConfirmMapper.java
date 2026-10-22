package com.spd.gz.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.gz.domain.GzHighConsumeConfirm;
import com.spd.gz.domain.GzHighConsumeConfirmBill;
import com.spd.gz.domain.GzHighConsumeConfirmLine;
import com.spd.gz.domain.dto.GzHighChargeConfirmQuery;
import com.spd.gz.domain.dto.GzHighChargeConfirmRowVo;

public interface GzHighConsumeConfirmMapper
{
    List<GzHighChargeConfirmRowVo> selectConfirmList(GzHighChargeConfirmQuery query);

    long selectConfirmListCount(GzHighChargeConfirmQuery query);

    /** 列表合计金额（瘦 JOIN，勿走宽表） */
    BigDecimal selectConfirmListSumAmt(GzHighChargeConfirmQuery query);

    /** 分页第一步：仅返回本页 linkId */
    List<String> selectConfirmListIds(GzHighChargeConfirmQuery query);

    List<GzHighChargeConfirmRowVo> selectConfirmLineDetailsByLinkIds(@Param("tenantId") String tenantId,
        @Param("linkIds") List<String> linkIds);

    int insertConfirm(GzHighConsumeConfirm confirm);

    int insertConfirmLineBatch(@Param("list") List<GzHighConsumeConfirmLine> list);

    int insertConfirmBillBatch(@Param("list") List<GzHighConsumeConfirmBill> list);

    int updateLinkConfirmStatus(@Param("tenantId") String tenantId, @Param("linkIds") List<String> linkIds,
        @Param("confirmId") String confirmId, @Param("updateBy") String updateBy);

    int updateLinkInstantIoAudited(@Param("tenantId") String tenantId, @Param("linkIds") List<String> linkIds,
        @Param("auditBy") String auditBy);

    int updateLinkInstantIoReversed(@Param("tenantId") String tenantId, @Param("linkIds") List<String> linkIds,
        @Param("auditBy") String auditBy);

    /** 按明细反向后回写该明细的退货(301)/退库(401)单号 */
    int updateLinkIoReturnBills(@Param("tenantId") String tenantId, @Param("linkIds") List<String> linkIds,
        @Param("returnGoodsBillNo") String returnGoodsBillNo, @Param("returnDepotBillNo") String returnDepotBillNo);

    int updateConfirmWarehouse(@Param("tenantId") String tenantId, @Param("confirmId") String confirmId,
        @Param("warehouseId") Long warehouseId, @Param("updateBy") String updateBy);

    String selectMaxConfirmNo(@Param("tenantId") String tenantId, @Param("datePrefix") String datePrefix);

    List<GzHighConsumeConfirmBill> selectBillsByConfirmId(@Param("tenantId") String tenantId,
        @Param("confirmId") String confirmId);

    List<GzHighConsumeConfirmBill> selectBillsByConfirmIds(@Param("tenantId") String tenantId,
        @Param("confirmIds") List<String> confirmIds);
}

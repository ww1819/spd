package com.spd.gz.domain.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * 高值消耗确认结果
 */
@Data
public class GzHighChargeConfirmResultVo
{
    private String confirmId;
    private String confirmNo;
    private int lineCount;
    private List<GzHighChargeConfirmBillVo> bills = new ArrayList<>();

    @Data
    public static class GzHighChargeConfirmBillVo
    {
        private String supplierId;
        private String supplierName;
        private String inboundBillNo;
        private Long inboundBillId;
        private String outboundBillNo;
        private Long outboundBillId;
        private String returnGoodsBillNo;
        private Long returnGoodsBillId;
        private String returnDepotBillNo;
        private Long returnDepotBillId;
    }
}

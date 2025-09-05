package com.spd.common.core.page;

import com.spd.common.core.domain.BaseEntity;

import java.math.BigDecimal;

public class TotalInfo extends BaseEntity {

    private BigDecimal totalQty;
    private BigDecimal totalAmt;

    private BigDecimal totalInWarehouseQty;
    private BigDecimal totalInWarehouseAmt;

    private BigDecimal totalRefundGoodsApplyQty;
    private BigDecimal totalRefundGoodsApplyAmt;


    private BigDecimal totalOutWarehouseQty;
    private BigDecimal totalOutWarehouseAmt;

    private BigDecimal totalRefundDepotApplyQty;
    private BigDecimal totalRefundDepotApplyAmt;

    public BigDecimal getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(BigDecimal totalQty) {
        this.totalQty = totalQty;
    }

    public BigDecimal getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(BigDecimal totalAmt) {
        this.totalAmt = totalAmt;
    }

    public BigDecimal getTotalInWarehouseQty() {
        return totalInWarehouseQty;
    }

    public void setTotalInWarehouseQty(BigDecimal totalInWarehouseQty) {
        this.totalInWarehouseQty = totalInWarehouseQty;
    }

    public BigDecimal getTotalInWarehouseAmt() {
        return totalInWarehouseAmt;
    }

    public void setTotalInWarehouseAmt(BigDecimal totalInWarehouseAmt) {
        this.totalInWarehouseAmt = totalInWarehouseAmt;
    }

    public BigDecimal getTotalRefundGoodsApplyQty() {
        return totalRefundGoodsApplyQty;
    }

    public void setTotalRefundGoodsApplyQty(BigDecimal totalRefundGoodsApplyQty) {
        this.totalRefundGoodsApplyQty = totalRefundGoodsApplyQty;
    }

    public BigDecimal getTotalRefundGoodsApplyAmt() {
        return totalRefundGoodsApplyAmt;
    }

    public void setTotalRefundGoodsApplyAmt(BigDecimal totalRefundGoodsApplyAmt) {
        this.totalRefundGoodsApplyAmt = totalRefundGoodsApplyAmt;
    }

    public BigDecimal getTotalOutWarehouseQty() {
        return totalOutWarehouseQty;
    }

    public void setTotalOutWarehouseQty(BigDecimal totalOutWarehouseQty) {
        this.totalOutWarehouseQty = totalOutWarehouseQty;
    }

    public BigDecimal getTotalOutWarehouseAmt() {
        return totalOutWarehouseAmt;
    }

    public void setTotalOutWarehouseAmt(BigDecimal totalOutWarehouseAmt) {
        this.totalOutWarehouseAmt = totalOutWarehouseAmt;
    }

    public BigDecimal getTotalRefundDepotApplyQty() {
        return totalRefundDepotApplyQty;
    }

    public void setTotalRefundDepotApplyQty(BigDecimal totalRefundDepotApplyQty) {
        this.totalRefundDepotApplyQty = totalRefundDepotApplyQty;
    }

    public BigDecimal getTotalRefundDepotApplyAmt() {
        return totalRefundDepotApplyAmt;
    }

    public void setTotalRefundDepotApplyAmt(BigDecimal totalRefundDepotApplyAmt) {
        this.totalRefundDepotApplyAmt = totalRefundDepotApplyAmt;
    }
}

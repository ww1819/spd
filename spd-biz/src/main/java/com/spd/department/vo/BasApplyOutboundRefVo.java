package com.spd.department.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 科室申领单关联出库单明细（经库房申请单 wh_wh_apply_ck_entry_ref）
 */
public class BasApplyOutboundRefVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 科室申领明细 bas_apply_entry.id */
    private Long basApplyEntryId;

    /** 库房申请单号 */
    private String whApplyBillNo;

    /** 出库单主表 ID */
    private String ckBillId;

    /** 出库单号 */
    private String ckBillNo;

    /** 出库单状态（与 stk_io_bill.bill_status 一致） */
    private Integer ckBillStatus;

    /** 出库明细 ID */
    private String ckEntryId;

    /** 本关联行数量 */
    private BigDecimal refQty;

    /** 出库明细行数量（展示） */
    private BigDecimal ckEntryQty;

    /** 耗材名称 */
    private String materialName;

    public Long getBasApplyEntryId() {
        return basApplyEntryId;
    }

    public void setBasApplyEntryId(Long basApplyEntryId) {
        this.basApplyEntryId = basApplyEntryId;
    }

    public String getWhApplyBillNo() {
        return whApplyBillNo;
    }

    public void setWhApplyBillNo(String whApplyBillNo) {
        this.whApplyBillNo = whApplyBillNo;
    }

    public String getCkBillId() {
        return ckBillId;
    }

    public void setCkBillId(String ckBillId) {
        this.ckBillId = ckBillId;
    }

    public String getCkBillNo() {
        return ckBillNo;
    }

    public void setCkBillNo(String ckBillNo) {
        this.ckBillNo = ckBillNo;
    }

    public Integer getCkBillStatus() {
        return ckBillStatus;
    }

    public void setCkBillStatus(Integer ckBillStatus) {
        this.ckBillStatus = ckBillStatus;
    }

    public String getCkEntryId() {
        return ckEntryId;
    }

    public void setCkEntryId(String ckEntryId) {
        this.ckEntryId = ckEntryId;
    }

    public BigDecimal getRefQty() {
        return refQty;
    }

    public void setRefQty(BigDecimal refQty) {
        this.refQty = refQty;
    }

    public BigDecimal getCkEntryQty() {
        return ckEntryQty;
    }

    public void setCkEntryQty(BigDecimal ckEntryQty) {
        this.ckEntryQty = ckEntryQty;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }
}

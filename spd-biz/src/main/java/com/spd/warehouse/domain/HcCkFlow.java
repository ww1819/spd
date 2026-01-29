package com.spd.warehouse.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.BaseEntity;

/**
 * 仓库流水对象 t_hc_ck_flow
 *
 * @author spd
 */
public class HcCkFlow extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    /** 出库单id */
    private Long billId;
    /** 出库单明细id */
    private Long entryId;
    /** 仓库ID */
    private Long warehouseId;
    /** 耗材ID */
    private Long materialId;
    /** 批次号 */
    private String batchNo;
    /** 批号 */
    private String batchNumber;
    /** 数量 */
    private BigDecimal qty;
    /** 单价 */
    private BigDecimal unitPrice;
    /** 金额 */
    private BigDecimal amt;
    /** 生产日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;
    /** 有效期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    /** 供应商ID */
    private Long supplierId;
    /** 关联仓库库存id */
    private Long kcNo;
    /** 类型：RK入库/CK出库/TH退货/TK退库 */
    private String lx;
    /** 流水时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date flowTime;
    /** 删除标志 */
    private Integer delFlag;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }
    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    public BigDecimal getQty() { return qty; }
    public void setQty(BigDecimal qty) { this.qty = qty; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getAmt() { return amt; }
    public void setAmt(BigDecimal amt) { this.amt = amt; }
    public Date getBeginTime() { return beginTime; }
    public void setBeginTime(Date beginTime) { this.beginTime = beginTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public Long getKcNo() { return kcNo; }
    public void setKcNo(Long kcNo) { this.kcNo = kcNo; }
    public String getLx() { return lx; }
    public void setLx(String lx) { this.lx = lx; }
    public Date getFlowTime() { return flowTime; }
    public void setFlowTime(Date flowTime) { this.flowTime = flowTime; }
    public Integer getDelFlag() { return delFlag; }
    public void setDelFlag(Integer delFlag) { this.delFlag = delFlag; }
}

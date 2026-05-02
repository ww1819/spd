package com.spd.warehouse.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdWarehouse;

/**
 * 盈亏单主表对象 stk_io_profit_loss
 *
 * @author spd
 */
public class StkIoProfitLoss extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;
    /** 盈亏单号 */
    @Excel(name = "盈亏单号")
    private String billNo;
    /** 关联盘点单ID */
    private Long stocktakingId;
    /** 盘点单号（冗余） */
    private String stocktakingNo;
    /** 仓库ID */
    private Long warehouseId;
    /** WH 仓库盈亏 / DEP 科室盈亏 */
    private String bizScope;
    /** 科室ID（biz_scope=DEP） */
    private Long departmentId;
    /** 科室名称快照 */
    private String departmentNameSnap;
    /** 单据状态 1待审核 2已审核 */
    private Integer billStatus;
    /** 审核人 */
    private String auditBy;
    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditDate;
    /** 删除标志 */
    private Integer delFlag;
    /** 租户ID(同sb_customer.customer_id) */
    private String tenantId;
    /** 业务主键 UUID7 */
    private String uuidId;
    /** 关联盘点单 uuid_id */
    private String stocktakingUuid;
    private String deleteBy;
    private Date deleteTime;
    /** 盈亏单明细 */
    private List<StkIoProfitLossEntry> entryList;
    /** 仓库对象 */
    private FdWarehouse warehouse;
    /** 科室对象（列表/详情关联，非必持久化） */
    private FdDepartment department;
    /** 耗材名称（查询条件，不持久化） */
    private String materialName;
    /** 开始日期（查询条件，不持久化） */
    private String beginDate;
    /** 结束日期（查询条件，不持久化） */
    private String endDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBillNo() { return billNo; }
    public void setBillNo(String billNo) { this.billNo = billNo; }
    public Long getStocktakingId() { return stocktakingId; }
    public void setStocktakingId(Long stocktakingId) { this.stocktakingId = stocktakingId; }
    public String getStocktakingNo() { return stocktakingNo; }
    public void setStocktakingNo(String stocktakingNo) { this.stocktakingNo = stocktakingNo; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }

    public String getBizScope() { return bizScope; }
    public void setBizScope(String bizScope) { this.bizScope = bizScope; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getDepartmentNameSnap() { return departmentNameSnap; }
    public void setDepartmentNameSnap(String departmentNameSnap) { this.departmentNameSnap = departmentNameSnap; }
    public Integer getBillStatus() { return billStatus; }
    public void setBillStatus(Integer billStatus) { this.billStatus = billStatus; }
    public String getAuditBy() { return auditBy; }
    public void setAuditBy(String auditBy) { this.auditBy = auditBy; }
    public Date getAuditDate() { return auditDate; }
    public void setAuditDate(Date auditDate) { this.auditDate = auditDate; }
    public Integer getDelFlag() { return delFlag; }
    public void setDelFlag(Integer delFlag) { this.delFlag = delFlag; }
    public List<StkIoProfitLossEntry> getEntryList() { return entryList; }
    public void setEntryList(List<StkIoProfitLossEntry> entryList) { this.entryList = entryList; }
    public FdWarehouse getWarehouse() { return warehouse; }
    public void setWarehouse(FdWarehouse warehouse) { this.warehouse = warehouse; }

    public FdDepartment getDepartment() { return department; }
    public void setDepartment(FdDepartment department) { this.department = department; }
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public String getBeginDate() { return beginDate; }
    public void setBeginDate(String beginDate) { this.beginDate = beginDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getUuidId() { return uuidId; }
    public void setUuidId(String uuidId) { this.uuidId = uuidId; }

    public String getStocktakingUuid() { return stocktakingUuid; }
    public void setStocktakingUuid(String stocktakingUuid) { this.stocktakingUuid = stocktakingUuid; }

    public String getDeleteBy() { return deleteBy; }
    public void setDeleteBy(String deleteBy) { this.deleteBy = deleteBy; }

    public Date getDeleteTime() { return deleteTime; }
    public void setDeleteTime(Date deleteTime) { this.deleteTime = deleteTime; }
}

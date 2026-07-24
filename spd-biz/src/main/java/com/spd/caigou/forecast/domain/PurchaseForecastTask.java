package com.spd.caigou.forecast.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.BaseEntity;
import com.spd.foundation.domain.FdWarehouse;

import java.util.Date;
import java.util.List;

/**
 * 采购预测补货任务 purchase_forecast_task
 */
public class PurchaseForecastTask extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String taskNo;
    private Long warehouseId;
    /** 1高值 2低值 空全部 */
    private String isGz;
    private Integer calcDays;
    private Integer leadTimeDays;
    private Integer safetyDays;
    /** 0草稿 1已生成计划 */
    private String status;
    private String generatedPlanIds;
    private String generatedPlanNos;
    private String tenantId;
    private String delFlag;
    private String deleteBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;

    private FdWarehouse warehouse;
    private List<PurchaseForecastEntry> entryList;

    /** 查询：起始创建日期 */
    private String beginDate;
    /** 查询：截止创建日期 */
    private String endDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTaskNo() { return taskNo; }
    public void setTaskNo(String taskNo) { this.taskNo = taskNo; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public String getIsGz() { return isGz; }
    public void setIsGz(String isGz) { this.isGz = isGz; }
    public Integer getCalcDays() { return calcDays; }
    public void setCalcDays(Integer calcDays) { this.calcDays = calcDays; }
    public Integer getLeadTimeDays() { return leadTimeDays; }
    public void setLeadTimeDays(Integer leadTimeDays) { this.leadTimeDays = leadTimeDays; }
    public Integer getSafetyDays() { return safetyDays; }
    public void setSafetyDays(Integer safetyDays) { this.safetyDays = safetyDays; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getGeneratedPlanIds() { return generatedPlanIds; }
    public void setGeneratedPlanIds(String generatedPlanIds) { this.generatedPlanIds = generatedPlanIds; }
    public String getGeneratedPlanNos() { return generatedPlanNos; }
    public void setGeneratedPlanNos(String generatedPlanNos) { this.generatedPlanNos = generatedPlanNos; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public String getDeleteBy() { return deleteBy; }
    public void setDeleteBy(String deleteBy) { this.deleteBy = deleteBy; }
    public Date getDeleteTime() { return deleteTime; }
    public void setDeleteTime(Date deleteTime) { this.deleteTime = deleteTime; }
    public FdWarehouse getWarehouse() { return warehouse; }
    public void setWarehouse(FdWarehouse warehouse) { this.warehouse = warehouse; }
    public List<PurchaseForecastEntry> getEntryList() { return entryList; }
    public void setEntryList(List<PurchaseForecastEntry> entryList) { this.entryList = entryList; }
    public String getBeginDate() { return beginDate; }
    public void setBeginDate(String beginDate) { this.beginDate = beginDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}

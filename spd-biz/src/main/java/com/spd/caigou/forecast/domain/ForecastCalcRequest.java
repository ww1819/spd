package com.spd.caigou.forecast.domain;

/**
 * 预测补货计算入参
 */
public class ForecastCalcRequest {

    private Long warehouseId;
    /** 1高值 2低值 空全部 */
    private String isGz;
    private Integer calcDays;
    private Integer leadTimeDays;
    private Integer safetyDays;

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
}

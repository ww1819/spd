package com.spd.monitoring.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 定数监测保存请求 DTO
 *
 * 对应前端 fixedNumber/index.vue 中 saveData 结构
 */
public class FixedNumberSaveRequest implements Serializable {

    /** 定数类型：1=仓库定数，2=科室定数 */
    private String fixedNumberType;

    /** 仓库ID（仓库定数时必填） */
    private Long warehouseId;

    /** 科室ID（科室定数时必填） */
    private Long departmentId;

    /** 明细列表 */
    private List<Detail> detailList;

    public String getFixedNumberType() {
        return fixedNumberType;
    }

    public void setFixedNumberType(String fixedNumberType) {
        this.fixedNumberType = fixedNumberType;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public List<Detail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<Detail> detailList) {
        this.detailList = detailList;
    }

    /**
     * 明细行
     */
    public static class Detail implements Serializable {

        /** 耗材ID */
        private Long materialId;

        /** 上限数量 */
        private Integer upperLimit;

        /** 下限数量 */
        private Integer lowerLimit;

        /** 有效期提醒天数 */
        private Integer expiryReminder;

        /** 是否监测 1=是 2=否 */
        private String monitoring;

        /** 货位名称 */
        private String location;

        /** 货位ID */
        private Long locationId;

        public Long getMaterialId() {
            return materialId;
        }

        public void setMaterialId(Long materialId) {
            this.materialId = materialId;
        }

        public Integer getUpperLimit() {
            return upperLimit;
        }

        public void setUpperLimit(Integer upperLimit) {
            this.upperLimit = upperLimit;
        }

        public Integer getLowerLimit() {
            return lowerLimit;
        }

        public void setLowerLimit(Integer lowerLimit) {
            this.lowerLimit = lowerLimit;
        }

        public Integer getExpiryReminder() {
            return expiryReminder;
        }

        public void setExpiryReminder(Integer expiryReminder) {
            this.expiryReminder = expiryReminder;
        }

        public String getMonitoring() {
            return monitoring;
        }

        public void setMonitoring(String monitoring) {
            this.monitoring = monitoring;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public Long getLocationId() {
            return locationId;
        }

        public void setLocationId(Long locationId) {
            this.locationId = locationId;
        }
    }
}


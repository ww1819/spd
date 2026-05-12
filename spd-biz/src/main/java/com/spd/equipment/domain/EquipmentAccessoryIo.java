package com.spd.equipment.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 设备配件出入库主表 equipment_accessory_io
 */
public class EquipmentAccessoryIo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String id;
    private String tenantId;
    @Excel(name = "单据号")
    private String ioNo;
    /** IN / OUT / ADJUST */
    @Excel(name = "类型")
    private String ioType;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date bizDate;
    private String equipmentId;
    private Integer delFlag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getIoNo() {
        return ioNo;
    }

    public void setIoNo(String ioNo) {
        this.ioNo = ioNo;
    }

    public String getIoType() {
        return ioType;
    }

    public void setIoType(String ioType) {
        this.ioType = ioType;
    }

    public Date getBizDate() {
        return bizDate;
    }

    public void setBizDate(Date bizDate) {
        this.bizDate = bizDate;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }
}

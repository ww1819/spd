package com.spd.equipment.domain.dto;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.equipment.domain.EquipmentAccessoryIoEntry;

/**
 * 配件出入库提交体（主信息 + 明细行）
 */
public class EquipmentAccessoryIoSubmitBody {

    /** IN / OUT */
    private String ioType;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date bizDate;
    private String equipmentId;
    private String remark;
    private List<EquipmentAccessoryIoEntry> entries;

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<EquipmentAccessoryIoEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<EquipmentAccessoryIoEntry> entries) {
        this.entries = entries;
    }
}

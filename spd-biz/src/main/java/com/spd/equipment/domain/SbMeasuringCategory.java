package com.spd.equipment.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 计量器具分类表 sb_measuring_category（主键UUID7，客户id，删除标志/删除者/删除时间，编码+名称+拼音简码+检定周期天）
 */
public class SbMeasuringCategory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String id;
    private String customerId;
    @Excel(name = "计量器具分类编码")
    private String categoryCode;
    @Excel(name = "计量器具分类名称")
    private String categoryName;
    @Excel(name = "计量器具分类拼音简码")
    private String categoryPinyin;
    @Excel(name = "计量检定周期(天)")
    private Integer calibrationCycleDays;
    private Integer delFlag;
    private String delBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date delTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getCategoryCode() { return categoryCode; }
    public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getCategoryPinyin() { return categoryPinyin; }
    public void setCategoryPinyin(String categoryPinyin) { this.categoryPinyin = categoryPinyin; }
    public Integer getCalibrationCycleDays() { return calibrationCycleDays; }
    public void setCalibrationCycleDays(Integer calibrationCycleDays) { this.calibrationCycleDays = calibrationCycleDays; }
    public Integer getDelFlag() { return delFlag; }
    public void setDelFlag(Integer delFlag) { this.delFlag = delFlag; }
    public String getDelBy() { return delBy; }
    public void setDelBy(String delBy) { this.delBy = delBy; }
    public Date getDelTime() { return delTime; }
    public void setDelTime(Date delTime) { this.delTime = delTime; }
}

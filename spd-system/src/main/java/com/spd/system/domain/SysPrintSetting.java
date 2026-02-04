package com.spd.system.domain;

import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.annotation.Excel.ColumnType;
import com.spd.common.core.domain.BaseEntity;

/**
 * 打印设置表 sys_print_setting
 * 
 * @author spd
 */
public class SysPrintSetting extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Excel(name = "主键ID", cellType = ColumnType.NUMERIC)
    private Long id;

    /** 模板名称 */
    @Excel(name = "模板名称")
    private String templateName;

    /** 入库单类型（101普通入库，501调拨等，NULL表示通用） */
    @Excel(name = "入库单类型", cellType = ColumnType.NUMERIC)
    private Integer billType;

    /** 页面宽度（mm） */
    @Excel(name = "页面宽度", cellType = ColumnType.NUMERIC)
    private BigDecimal pageWidth;

    /** 页面高度（mm） */
    @Excel(name = "页面高度", cellType = ColumnType.NUMERIC)
    private BigDecimal pageHeight;

    /** 页面方向（portrait纵向，landscape横向） */
    @Excel(name = "页面方向", readConverterExp = "portrait=纵向,landscape=横向")
    private String orientation;

    /** 上边距（mm） */
    @Excel(name = "上边距", cellType = ColumnType.NUMERIC)
    private BigDecimal marginTop;

    /** 下边距（mm） */
    @Excel(name = "下边距", cellType = ColumnType.NUMERIC)
    private BigDecimal marginBottom;

    /** 左边距（mm） */
    @Excel(name = "左边距", cellType = ColumnType.NUMERIC)
    private BigDecimal marginLeft;

    /** 右边距（mm） */
    @Excel(name = "右边距", cellType = ColumnType.NUMERIC)
    private BigDecimal marginRight;

    /** 字体大小（px） */
    @Excel(name = "字体大小", cellType = ColumnType.NUMERIC)
    private Integer fontSize;

    /** 表格字体大小（px） */
    @Excel(name = "表格字体大小", cellType = ColumnType.NUMERIC)
    private Integer tableFontSize;

    /** 列间距（mm） */
    @Excel(name = "列间距", cellType = ColumnType.NUMERIC)
    private BigDecimal columnSpacing;

    /** 显示采购人（0否，1是） */
    @Excel(name = "显示采购人", readConverterExp = "0=否,1=是")
    private Integer showPurchaser;

    /** 显示制单人（0否，1是） */
    @Excel(name = "显示制单人", readConverterExp = "0=否,1=是")
    private Integer showCreator;

    /** 显示复核人（0否，1是） */
    @Excel(name = "显示复核人", readConverterExp = "0=否,1=是")
    private Integer showAuditor;

    /** 显示验收人（0否，1是） */
    @Excel(name = "显示验收人", readConverterExp = "0=否,1=是")
    private Integer showReceiver;

    /** 采购人标签 */
    @Excel(name = "采购人标签")
    private String purchaserLabel;

    /** 制单人标签 */
    @Excel(name = "制单人标签")
    private String creatorLabel;

    /** 复核人标签 */
    @Excel(name = "复核人标签")
    private String auditorLabel;

    /** 验收人标签 */
    @Excel(name = "验收人标签")
    private String receiverLabel;

    /** 列配置（JSON格式，存储列宽度、是否显示等） */
    private String columnConfig;

    /** 是否默认模板（0否，1是） */
    @Excel(name = "是否默认", readConverterExp = "0=否,1=是")
    private Integer isDefault;

    /** 状态（0正常，1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    @NotBlank(message = "模板名称不能为空")
    @Size(min = 0, max = 100, message = "模板名称不能超过100个字符")
    public String getTemplateName()
    {
        return templateName;
    }

    public void setTemplateName(String templateName)
    {
        this.templateName = templateName;
    }

    public Integer getBillType()
    {
        return billType;
    }

    public void setBillType(Integer billType)
    {
        this.billType = billType;
    }

    public BigDecimal getPageWidth()
    {
        return pageWidth;
    }

    public void setPageWidth(BigDecimal pageWidth)
    {
        this.pageWidth = pageWidth;
    }

    public BigDecimal getPageHeight()
    {
        return pageHeight;
    }

    public void setPageHeight(BigDecimal pageHeight)
    {
        this.pageHeight = pageHeight;
    }

    public String getOrientation()
    {
        return orientation;
    }

    public void setOrientation(String orientation)
    {
        this.orientation = orientation;
    }

    public BigDecimal getMarginTop()
    {
        return marginTop;
    }

    public void setMarginTop(BigDecimal marginTop)
    {
        this.marginTop = marginTop;
    }

    public BigDecimal getMarginBottom()
    {
        return marginBottom;
    }

    public void setMarginBottom(BigDecimal marginBottom)
    {
        this.marginBottom = marginBottom;
    }

    public BigDecimal getMarginLeft()
    {
        return marginLeft;
    }

    public void setMarginLeft(BigDecimal marginLeft)
    {
        this.marginLeft = marginLeft;
    }

    public BigDecimal getMarginRight()
    {
        return marginRight;
    }

    public void setMarginRight(BigDecimal marginRight)
    {
        this.marginRight = marginRight;
    }

    public Integer getFontSize()
    {
        return fontSize;
    }

    public void setFontSize(Integer fontSize)
    {
        this.fontSize = fontSize;
    }

    public Integer getTableFontSize()
    {
        return tableFontSize;
    }

    public void setTableFontSize(Integer tableFontSize)
    {
        this.tableFontSize = tableFontSize;
    }

    public BigDecimal getColumnSpacing()
    {
        return columnSpacing;
    }

    public void setColumnSpacing(BigDecimal columnSpacing)
    {
        this.columnSpacing = columnSpacing;
    }

    public Integer getShowPurchaser()
    {
        return showPurchaser;
    }

    public void setShowPurchaser(Integer showPurchaser)
    {
        this.showPurchaser = showPurchaser;
    }

    public Integer getShowCreator()
    {
        return showCreator;
    }

    public void setShowCreator(Integer showCreator)
    {
        this.showCreator = showCreator;
    }

    public Integer getShowAuditor()
    {
        return showAuditor;
    }

    public void setShowAuditor(Integer showAuditor)
    {
        this.showAuditor = showAuditor;
    }

    public Integer getShowReceiver()
    {
        return showReceiver;
    }

    public void setShowReceiver(Integer showReceiver)
    {
        this.showReceiver = showReceiver;
    }

    public String getPurchaserLabel()
    {
        return purchaserLabel;
    }

    public void setPurchaserLabel(String purchaserLabel)
    {
        this.purchaserLabel = purchaserLabel;
    }

    public String getCreatorLabel()
    {
        return creatorLabel;
    }

    public void setCreatorLabel(String creatorLabel)
    {
        this.creatorLabel = creatorLabel;
    }

    public String getAuditorLabel()
    {
        return auditorLabel;
    }

    public void setAuditorLabel(String auditorLabel)
    {
        this.auditorLabel = auditorLabel;
    }

    public String getReceiverLabel()
    {
        return receiverLabel;
    }

    public void setReceiverLabel(String receiverLabel)
    {
        this.receiverLabel = receiverLabel;
    }

    public String getColumnConfig()
    {
        return columnConfig;
    }

    public void setColumnConfig(String columnConfig)
    {
        this.columnConfig = columnConfig;
    }

    public Integer getIsDefault()
    {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault)
    {
        this.isDefault = isDefault;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("templateName", getTemplateName())
            .append("billType", getBillType())
            .append("pageWidth", getPageWidth())
            .append("pageHeight", getPageHeight())
            .append("orientation", getOrientation())
            .append("marginTop", getMarginTop())
            .append("marginBottom", getMarginBottom())
            .append("marginLeft", getMarginLeft())
            .append("marginRight", getMarginRight())
            .append("fontSize", getFontSize())
            .append("tableFontSize", getTableFontSize())
            .append("columnSpacing", getColumnSpacing())
            .append("showPurchaser", getShowPurchaser())
            .append("showCreator", getShowCreator())
            .append("showAuditor", getShowAuditor())
            .append("showReceiver", getShowReceiver())
            .append("purchaserLabel", getPurchaserLabel())
            .append("creatorLabel", getCreatorLabel())
            .append("auditorLabel", getAuditorLabel())
            .append("receiverLabel", getReceiverLabel())
            .append("columnConfig", getColumnConfig())
            .append("isDefault", getIsDefault())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

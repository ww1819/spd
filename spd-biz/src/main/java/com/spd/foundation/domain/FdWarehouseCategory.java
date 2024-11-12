package com.spd.foundation.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 库房分类对象 fd_warehouse_category
 * 
 * @author spd
 * @date 2024-04-12
 */
public class FdWarehouseCategory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long warehouseCategoryId;

    /** 库房分类编码 */
    @Excel(name = "库房分类编码")
    private String warehouseCategoryCode;

    /** 库房分类名称 */
    @Excel(name = "库房分类名称")
    private String warehouseCategoryName;

    /** 库房分类地址 */
    @Excel(name = "库房分类地址")
    private String warehouseCategoryAddress;

    /** 库房分类联系方式 */
    @Excel(name = "库房分类联系方式")
    private String warehouseCategoryContact;

    /** 删除标识 */
    private Integer delFlag;

    public void setWarehouseCategoryId(Long warehouseCategoryId) 
    {
        this.warehouseCategoryId = warehouseCategoryId;
    }

    public Long getWarehouseCategoryId() 
    {
        return warehouseCategoryId;
    }
    public void setWarehouseCategoryCode(String warehouseCategoryCode) 
    {
        this.warehouseCategoryCode = warehouseCategoryCode;
    }

    public String getWarehouseCategoryCode() 
    {
        return warehouseCategoryCode;
    }
    public void setWarehouseCategoryName(String warehouseCategoryName) 
    {
        this.warehouseCategoryName = warehouseCategoryName;
    }

    public String getWarehouseCategoryName() 
    {
        return warehouseCategoryName;
    }
    public void setWarehouseCategoryAddress(String warehouseCategoryAddress) 
    {
        this.warehouseCategoryAddress = warehouseCategoryAddress;
    }

    public String getWarehouseCategoryAddress() 
    {
        return warehouseCategoryAddress;
    }
    public void setWarehouseCategoryContact(String warehouseCategoryContact) 
    {
        this.warehouseCategoryContact = warehouseCategoryContact;
    }

    public String getWarehouseCategoryContact() 
    {
        return warehouseCategoryContact;
    }
    public void setDelFlag(Integer delFlag) 
    {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("warehouseCategoryId", getWarehouseCategoryId())
            .append("warehouseCategoryCode", getWarehouseCategoryCode())
            .append("warehouseCategoryName", getWarehouseCategoryName())
            .append("warehouseCategoryAddress", getWarehouseCategoryAddress())
            .append("warehouseCategoryContact", getWarehouseCategoryContact())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

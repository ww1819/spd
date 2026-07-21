package com.spd.foundation.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 货位对象 fd_location
 * 
 * @author spd
 * @date 2024-12-13
 */
public class FdLocation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long locationId;

    /** 父货位ID */
    private Long parentId;

    /** 子货位列表 */
    private java.util.List<FdLocation> children;

    /** 货位编码 */
    @Excel(name = "货位编码")
    private String locationCode;

    /** 货位名称 */
    @Excel(name = "货位名称")
    private String locationName;

    /** 仓库ID */
    @Excel(name = "仓库ID")
    private Long warehouseId;

    /** 仓库名称 */
    private String warehouseName;

    /**
     * 五区类型：PENDING_CHECK待验 / QUALIFIED合格 / UNQUALIFIED不合格 / RETURN退货 / PENDING_SHIP待发
     */
    @Excel(name = "五区类型")
    private String zoneType;

    /** 货架编码（如 A01） */
    @Excel(name = "货架编码")
    private String shelfCode;

    /** 层号 */
    @Excel(name = "层号")
    private Integer layerNo;

    /** 格口号 */
    @Excel(name = "格口号")
    private Integer slotNo;

    /** 平面 X 坐标（米） */
    private java.math.BigDecimal posX;

    /** 平面 Y 坐标（米） */
    private java.math.BigDecimal posY;

    /** 高度 Z 坐标（米） */
    private java.math.BigDecimal posZ;

    /** 容量（可选，用于占用率） */
    private java.math.BigDecimal capacity;

    /** 删除标识 */
    private Integer delFlag;

    /** 租户ID(同sb_customer.customer_id) */
    private String tenantId;

    public void setLocationId(Long locationId) 
    {
        this.locationId = locationId;
    }

    public Long getLocationId() 
    {
        return locationId;
    }
    
    public void setParentId(Long parentId) 
    {
        this.parentId = parentId;
    }

    public Long getParentId() 
    {
        return parentId;
    }
    
    public void setChildren(java.util.List<FdLocation> children) 
    {
        this.children = children;
    }

    public java.util.List<FdLocation> getChildren() 
    {
        return children;
    }
    
    public void setLocationCode(String locationCode) 
    {
        this.locationCode = locationCode;
    }

    public String getLocationCode() 
    {
        return locationCode;
    }
    
    public void setLocationName(String locationName) 
    {
        this.locationName = locationName;
    }

    public String getLocationName() 
    {
        return locationName;
    }
    
    public void setWarehouseId(Long warehouseId) 
    {
        this.warehouseId = warehouseId;
    }

    public Long getWarehouseId() 
    {
        return warehouseId;
    }
    
    public void setWarehouseName(String warehouseName) 
    {
        this.warehouseName = warehouseName;
    }

    public String getWarehouseName() 
    {
        return warehouseName;
    }

    public String getZoneType()
    {
        return zoneType;
    }

    public void setZoneType(String zoneType)
    {
        this.zoneType = zoneType;
    }

    public String getShelfCode()
    {
        return shelfCode;
    }

    public void setShelfCode(String shelfCode)
    {
        this.shelfCode = shelfCode;
    }

    public Integer getLayerNo()
    {
        return layerNo;
    }

    public void setLayerNo(Integer layerNo)
    {
        this.layerNo = layerNo;
    }

    public Integer getSlotNo()
    {
        return slotNo;
    }

    public void setSlotNo(Integer slotNo)
    {
        this.slotNo = slotNo;
    }

    public java.math.BigDecimal getPosX()
    {
        return posX;
    }

    public void setPosX(java.math.BigDecimal posX)
    {
        this.posX = posX;
    }

    public java.math.BigDecimal getPosY()
    {
        return posY;
    }

    public void setPosY(java.math.BigDecimal posY)
    {
        this.posY = posY;
    }

    public java.math.BigDecimal getPosZ()
    {
        return posZ;
    }

    public void setPosZ(java.math.BigDecimal posZ)
    {
        this.posZ = posZ;
    }

    public java.math.BigDecimal getCapacity()
    {
        return capacity;
    }

    public void setCapacity(java.math.BigDecimal capacity)
    {
        this.capacity = capacity;
    }
    
    public void setDelFlag(Integer delFlag) 
    {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag() 
    {
        return delFlag;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("locationId", getLocationId())
            .append("parentId", getParentId())
            .append("locationCode", getLocationCode())
            .append("locationName", getLocationName())
            .append("warehouseId", getWarehouseId())
            .append("warehouseName", getWarehouseName())
            .append("zoneType", getZoneType())
            .append("shelfCode", getShelfCode())
            .append("layerNo", getLayerNo())
            .append("slotNo", getSlotNo())
            .append("posX", getPosX())
            .append("posY", getPosY())
            .append("posZ", getPosZ())
            .append("capacity", getCapacity())
            .append("delFlag", getDelFlag())
            .append("tenantId", getTenantId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("deleteBy", getDeleteBy())
            .append("deleteTime", getDeleteTime())
            .toString();
    }
}


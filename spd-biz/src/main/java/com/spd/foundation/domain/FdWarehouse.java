package com.spd.foundation.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 仓库对象 fd_warehouse
 *
 * @author spd
 * @date 2023-11-26
 */
public class FdWarehouse extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Excel(name = "仓库序号", cellType = Excel.ColumnType.NUMERIC)
    private Long id;

    /** 仓库编码 */
    @Excel(name = "仓库编码")
    private String code;

    /** 仓库名称 */
    @Excel(name = "仓库名称")
    private String name;

    /** 删除标识 */
    private Integer delFlag;

    /** 仓库负责人 */
    @Excel(name = "仓库负责人")
    private String warehousePerson;

    /** 仓库电话 */
    @Excel(name = "仓库电话")
    private String warehousePhone;

    /** 仓库状态 */
    @Excel(name = "仓库状态")
    private String warehouseStatus;

    /** 仓库类型 */
    @Excel(name = "仓库类型")
    private String warehouseType;

    /** 结算类型 */
    @Excel(name = "结算类型")
    private String settlementType;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public String getWarehousePerson() {
        return warehousePerson;
    }

    public void setWarehousePerson(String warehousePerson) {
        this.warehousePerson = warehousePerson;
    }

    public String getWarehousePhone() {
        return warehousePhone;
    }

    public void setWarehousePhone(String warehousePhone) {
        this.warehousePhone = warehousePhone;
    }

    public String getWarehouseStatus() {
        return warehouseStatus;
    }

    public void setWarehouseStatus(String warehouseStatus) {
        this.warehouseStatus = warehouseStatus;
    }

    public String getWarehouseType() {
        return warehouseType;
    }

    public void setWarehouseType(String warehouseType) {
        this.warehouseType = warehouseType;
    }

    public String getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(String settlementType) {
        this.settlementType = settlementType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("name", getName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .append("warehousePerson", getWarehousePerson())
            .append("warehousePhone", getWarehousePhone())
            .append("warehouseStatus", getWarehouseStatus())
            .append("warehouseType", getWarehouseType())
            .append("settlementType", getSettlementType())
            .toString();
    }
}

package com.spd.foundation.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;


/**
 * 设备字典对象 fd_equipment_dict
 *
 * @author spd
 * @date 2024-12-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FdEquipmentDict extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 设备编码 */
    @Excel(name = "设备编码")
    private String code;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String name;

    /** 供应商ID */
    @Excel(name = "供应商ID")
    private Long supplierId;

    /** 规格 */
    @Excel(name = "规格")
    private String speci;

    /** 型号 */
    @Excel(name = "型号")
    private String model;

    /** 价格 */
    @Excel(name = "价格")
    private BigDecimal price;

    /** 供应商对象 */
    private FdSupplier supplier;

    /** 删除标识 */
    private Integer delFlag;

    /** 名称简码 */
    @Excel(name = "名称简码")
    private String referredName;

    /** 通用名称 */
    @Excel(name = "通用名称")
    private String useName;

    /** 生产厂家ID */
    @Excel(name = "生产厂家ID")
    private Long factoryId;

    /** 库房分类ID */
    @Excel(name = "库房分类ID")
    private Long storeroomId;

    /** 财务分类ID */
    @Excel(name = "财务分类ID")
    private Long financeCategoryId;

    /** 单位分类ID */
    @Excel(name = "单位分类ID")
    private Long unitId;

    /** 注册证名称 */
    @Excel(name = "注册证名称")
    private String registerName;

    /** 注册证件号 */
    @Excel(name = "注册证件号")
    private String registerNo;

    /** 医保名称 */
    @Excel(name = "医保名称")
    private String medicalName;

    /** 医保编码 */
    @Excel(name = "医保编码")
    private String medicalNo;

    /** 有效期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "有效期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date periodDate;

    /** 招标类别 */
    @Excel(name = "招标类别")
    private String successfulType;

    /** 中标号 */
    @Excel(name = "中标号")
    private String successfulNo;

    /** 中标价格 */
    @Excel(name = "中标价格")
    private BigDecimal successfulPrice;

    /** 销售价（最小单位） */
    @Excel(name = "销售价")
    private BigDecimal salePrice;

    /** 包装规格 */
    @Excel(name = "包装规格")
    private String packageSpeci;

    /**  产地 */
    @Excel(name = " 产地")
    private String producer;

    /** 设备级别  */
    @Excel(name = "设备级别 ")
    private String equipmentLevel;

    /** 注册证级别 */
    @Excel(name = "注册证级别")
    private String registerLevel;

    /** 风险级别 */
    @Excel(name = "风险级别")
    private String riskLevel;

    /** 急救类型 */
    @Excel(name = "急救类型")
    private String firstaidLevel;

    /** 医用级别 */
    @Excel(name = "医用级别")
    private String doctorLevel;

    /** 品牌 */
    @Excel(name = "品牌")
    private String brand;

    /** 用途 */
    @Excel(name = "用途")
    private String useto;

    /** 材质 */
    @Excel(name = "材质")
    private String quality;

    /** 功能 */
    @Excel(name = "功能")
    private String function;

    /** 储存方式 */
    @Excel(name = "储存方式")
    private String isWay;

    /** UDI码 */
    @Excel(name = "UDI码")
    private String udiNo;

    /** 许可证编号 */
    @Excel(name = "许可证编号")
    private String permitNo;

    /** 国家编码 */
    @Excel(name = "国家编码")
    private String countryNo;

    /** 国家医保名称 */
    @Excel(name = "国家医保名称")
    private String countryName;

    /** 商品说明 */
    @Excel(name = "商品说明")
    private String description;

    /** 使用状态（停用/在用） */
    @Excel(name = "使用状态", readConverterExp = "停=用/在用")
    private String isUse;

    /** 带量采购（是/否） */
    @Excel(name = "带量采购", readConverterExp = "是=/否")
    private String isProcure;

    /** 重点监测（是/否） */
    @Excel(name = "重点监测", readConverterExp = "是=/否")
    private String isMonitor;

    /** 厂家对象 */
    private FdFactory fdFactory;

    /** 库房分类对象 */
    private FdWarehouseCategory fdWarehouseCategory;

    /** 财务分类对象 */
    private FdFinanceCategory fdFinanceCategory;

    /** 单位分类对象 */
    private FdUnit fdUnit;

    /** 货位对象 */
    private FdLocation fdLocation;

    /** 是否高值 */
    @Excel(name = "是否高值", readConverterExp = "1=是,2=否")
    private String isGz;

    /** 是否跟台 */
    @Excel(name = "是否跟台", readConverterExp = "1=是,2=否")
    private String isFollow;

    /** 货位ID */
    @Excel(name = "货位ID")
    private Long locationId;

    /** 图片URL */
    private String imageUrl;

    /** 阳光平台编码 */
    private String sunshineCode;

    /** 是否计费 */
    @Excel(name = "是否计费", readConverterExp = "1=是,2=否")
    private String isBilling;

    /** 查询参数：起始日期 */
    private Date beginDate;

    /** 查询参数：截止日期 */
    private Date endDate;
}


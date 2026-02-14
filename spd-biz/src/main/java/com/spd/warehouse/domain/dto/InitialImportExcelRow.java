package com.spd.warehouse.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 期初库存导入 Excel 行 DTO（用于解析与预览）
 * 模板列顺序：基础信息 → 价格数量批号效期 → 厂家供应商 → 第三方ID 在末尾
 *
 * @author spd
 */
@Data
public class InitialImportExcelRow {

    @Excel(name = "耗材编码")
    private String materialCode;

    @Excel(name = "耗材名称")
    private String materialName;

    @Excel(name = "规格")
    private String speci;

    @Excel(name = "型号")
    private String model;

    @Excel(name = "注册证号")
    private String registerNo;

    @Excel(name = "医保编码")
    private String medicalNo;

    @Excel(name = "医保名称")
    private String medicalName;

    @Excel(name = "主条码")
    private String mainBarcode;

    @Excel(name = "库房分类")
    private String warehouseCategory;

    @Excel(name = "财务分类")
    private String financeCategory;

    @Excel(name = "仓库编码")
    private String warehouseCode;

    @Excel(name = "单价")
    private BigDecimal unitPrice;

    @Excel(name = "数量")
    private BigDecimal qty;

    @Excel(name = "批号")
    private String batchNumber;

    @Excel(name = "生产日期", dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;

    @Excel(name = "效期", dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    @Excel(name = "生产厂家名称")
    private String factoryName;

    @Excel(name = "供应商名称")
    private String supplierName;

    @Excel(name = "第三方系统库存明细ID")
    private String thirdPartyDetailId;

    @Excel(name = "第三方系统产品档案ID")
    private String thirdPartyMaterialId;
}

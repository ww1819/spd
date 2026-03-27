package com.spd.warehouse.domain.dto;

import com.spd.common.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 期初库存导入 Excel 行 DTO（用于解析与预览）
 * <p>
 * HIS 列用于与 SPD 基础资料对齐并赋值：
 * <ul>
 *   <li>HIS系统产品档案id → 非空时按租户匹配 {@code fd_material.his_id}；为空时按耗材编码匹配 {@code fd_material.code}，明细写入 SPD 耗材主键 {@code material_id}</li>
 *   <li>HIS系统生产厂家id → 匹配 {@code fd_factory.his_id}，明细写入 {@code factory_id}；若产品档案未维护厂家则补写档案</li>
 *   <li>HIS系统供应商id → 匹配 {@code fd_supplier.his_id}，明细写入 {@code supplier_id}；若产品档案未维护供应商则补写档案</li>
 *   <li>第三方系统库存明细ID → 写入明细表 {@code stk_initial_import_entry.his_id}（与「HIS系统产品档案id → third_party_material_id」为不同字段，勿混用）</li>
 * </ul>
 * 生产日期、效期支持 YYYYMMDD 与 yyyy-MM-dd（导入后以字符串进入本 DTO，由服务层解析）。
 * 列顺序与导出模板一致。
 *
 * @author spd
 */
@Data
public class InitialImportExcelRow {

    @Excel(name = "耗材编码")
    private String materialCode;

    @Excel(name = "耗材名称")
    private String materialName;

    @Excel(name = "HIS系统产品档案id", nameAliases = {
        "第三方系统产品档案ID", "第三方系统产品档案id", "HIS系统ID", "HIS产品档案ID"
    }, prompt = "选填；非空时按 HIS 匹配 SPD 产品档案 his_id；若为空则按「耗材编码」匹配产品档案。二者至少填一项")
    private String thirdPartyMaterialId;

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

    /** 生产日期原始文本：YYYYMMDD 或 yyyy-MM-dd 等，由 {@link com.spd.warehouse.util.InitialImportDateParser} 解析 */
    @Excel(name = "生产日期")
    private String beginDateRaw;

    /** 效期原始文本：YYYYMMDD 或 yyyy-MM-dd 等 */
    @Excel(name = "效期")
    private String endDateRaw;

    @Excel(name = "生产厂家名称")
    private String factoryName;

    @Excel(name = "HIS系统生产厂家id", nameAliases = {
        "HIS生产厂家ID", "HIS系统生产厂家ID", "生产厂家HIS ID"
    }, prompt = "选填；与 SPD 生产厂家 his_id 一致时可匹配；优先于生产厂家名称")
    private String hisFactoryId;

    @Excel(name = "供应商名称")
    private String supplierName;

    @Excel(name = "HIS系统供应商id", nameAliases = {
        "HIS供应商ID", "HIS系统供应商ID", "供应商HIS ID"
    }, prompt = "选填；与 SPD 供应商 his_id 一致时可匹配；优先于供应商名称")
    private String hisSupplierId;

    @Excel(name = "第三方系统库存明细ID", nameAliases = {
        "第三方系统库存明细id", "HIS库存明细ID", "库存明细ID"
    }, prompt = "选填；对应明细表 his_id（第三方/HIS 库存明细主键）")
    private String hisId;
}

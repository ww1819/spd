package com.spd.warehouse.domain.dto;

import com.spd.common.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 仓库盘点盘盈明细导入 Excel 行（与「盘盈明细模板」列一致）
 */
@Data
public class WhStocktakingProfitImportRow {

    @Excel(name = "SPD仓库ID")
    private Long warehouseId;

    @Excel(name = "SPD产品档案ID")
    private Long materialId;

    @Excel(name = "SPD供应商ID")
    private Long supplierId;

    @Excel(name = "单价")
    private BigDecimal unitPrice;

    @Excel(name = "数量")
    private BigDecimal qty;

    /** 批号；Excel 中可用前导单引号避免数字格式丢失 */
    @Excel(name = "批号")
    private String batchNumber;

    @Excel(name = "第三方系统批次号", nameAliases = { "第三方系统批次号;" })
    private String thirdPartyBatchNo;

    @Excel(name = "有效期", dateFormat = "yyyy-MM-dd")
    private String endDateRaw;

    @Excel(name = "生产日期", dateFormat = "yyyy-MM-dd")
    private String beginDateRaw;

    @Excel(name = "第三方系统库存明细id", nameAliases = { "第三方系统库存明细ID", "第三方系统库存明细Id" })
    private String hisId;
}

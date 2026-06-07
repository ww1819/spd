package com.spd.warehouse.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 仓库盘点盘盈明细确认导入请求
 */
@Data
public class WhStocktakingProfitImportConfirmRequest {

    /** 预览通过后的行数据（与 Excel 解析结构一致） */
    private List<WhStocktakingProfitImportRow> rows;
}

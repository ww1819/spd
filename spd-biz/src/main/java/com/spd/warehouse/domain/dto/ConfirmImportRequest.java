package com.spd.warehouse.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 期初库存确认导入请求体
 *
 * @author spd
 */
@Data
public class ConfirmImportRequest {

    /** 所属仓库ID */
    private Long warehouseId;

    /** 预览行数据（与 Excel 解析结构一致） */
    private List<InitialImportExcelRow> rows;
}

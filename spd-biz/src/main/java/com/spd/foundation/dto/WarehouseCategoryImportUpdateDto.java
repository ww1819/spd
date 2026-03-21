package com.spd.foundation.dto;

import com.spd.common.annotation.Excel;

public class WarehouseCategoryImportUpdateDto {
    @Excel(name = "库房分类ID")
    private Long warehouseCategoryId;

    @Excel(name = "库房分类名称")
    private String warehouseCategoryName;

    @Excel(name = "数据校验结果", width = 40, sort = 99999)
    private String validationResult;

    public Long getWarehouseCategoryId() {
        return warehouseCategoryId;
    }

    public void setWarehouseCategoryId(Long warehouseCategoryId) {
        this.warehouseCategoryId = warehouseCategoryId;
    }

    public String getWarehouseCategoryName() {
        return warehouseCategoryName;
    }

    public void setWarehouseCategoryName(String warehouseCategoryName) {
        this.warehouseCategoryName = warehouseCategoryName;
    }

    public String getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(String validationResult) {
        this.validationResult = validationResult;
    }
}

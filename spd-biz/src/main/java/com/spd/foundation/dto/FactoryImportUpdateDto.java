package com.spd.foundation.dto;

import com.spd.common.annotation.Excel;

public class FactoryImportUpdateDto {
    @Excel(name = "厂家ID")
    private Long factoryId;

    @Excel(name = "厂家名称")
    private String factoryName;

    @Excel(name = "数据校验结果", width = 40, sort = 99999)
    private String validationResult;

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(String validationResult) {
        this.validationResult = validationResult;
    }
}

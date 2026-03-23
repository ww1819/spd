package com.spd.foundation.dto;

import com.spd.common.annotation.Excel;

public class SupplierImportUpdateDto {
    @Excel(name = "ID")
    private Long id;

    @Excel(name = "供应商名称")
    private String name;

    @Excel(name = "数据校验结果", width = 40, sort = 99999)
    private String validationResult;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(String validationResult) {
        this.validationResult = validationResult;
    }
}

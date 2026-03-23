package com.spd.foundation.dto;

import com.spd.common.annotation.Excel;

public class FinanceCategoryImportUpdateDto {
    @Excel(name = "财务分类ID")
    private Long financeCategoryId;

    @Excel(name = "财务分类名称")
    private String financeCategoryName;

    @Excel(name = "数据校验结果", width = 40, sort = 99999)
    private String validationResult;

    public Long getFinanceCategoryId() {
        return financeCategoryId;
    }

    public void setFinanceCategoryId(Long financeCategoryId) {
        this.financeCategoryId = financeCategoryId;
    }

    public String getFinanceCategoryName() {
        return financeCategoryName;
    }

    public void setFinanceCategoryName(String financeCategoryName) {
        this.financeCategoryName = financeCategoryName;
    }

    public String getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(String validationResult) {
        this.validationResult = validationResult;
    }
}

package com.spd.system.dto;

import com.spd.common.annotation.Excel;

public class UserImportUpdateDto {
    @Excel(name = "用户ID")
    private Long userId;

    @Excel(name = "用户姓名")
    private String nickName;

    @Excel(name = "数据校验结果", width = 40, sort = 99999)
    private String validationResult;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(String validationResult) {
        this.validationResult = validationResult;
    }
}

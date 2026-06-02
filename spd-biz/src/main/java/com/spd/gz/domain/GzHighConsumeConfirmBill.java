package com.spd.gz.domain;

import java.util.Date;
import lombok.Data;

/**
 * 高值消耗确认生成的结算单据追溯
 */
@Data
public class GzHighConsumeConfirmBill
{
    private String id;
    private String tenantId;
    private String confirmId;
    private String supplierId;
    private Integer billType;
    private Long stkIoBillId;
    private String billNo;
    private Integer delFlag;
    private Date createTime;
}

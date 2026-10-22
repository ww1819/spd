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
    /** 1=按明细生成的反向单(301/401)；0=按确认批次 */
    private Integer linkScoped;
    private Integer delFlag;
    private Date createTime;
}

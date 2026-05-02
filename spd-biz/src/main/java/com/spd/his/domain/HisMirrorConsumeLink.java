package com.spd.his.domain;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * HIS 计费镜像与科室批量消耗明细追溯
 */
@Data
public class HisMirrorConsumeLink
{
    private String id;
    private String tenantId;
    private String visitKind;
    private String mirrorRowId;
    private String fetchBatchId;
    private Long deptBatchConsumeId;
    private Long deptBatchConsumeEntryId;
    private BigDecimal allocQty;
    private Long depInventoryId;
    private Long gzDepInventoryId;
    /** 低值科室库存有效期快照（退费返还排序） */
    private Date stkDepEndDate;
    private String inHospitalCode;
    private BigDecimal returnedQty;
    private BigDecimal refundableRemainingQty;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
    private Integer delFlag;
    private String deleteBy;
    private Date deleteTime;
}

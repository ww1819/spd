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
    private Long traceabilityId;
    private Long traceabilityEntryId;
    /** 高值消耗确认：0未确认 1已确认 */
    private Integer confirmStatus;
    /** 确认批次 gz_high_consume_confirm.id */
    private String confirmId;
    /** 即入即出审核：0待审核 1已审核 2已冲销 */
    private Integer instantIoAuditStatus;
    private String instantIoAuditBy;
    private Date instantIoAuditTime;
    /** 核销科室（SPD fd_department.id，高值扫码实际扣减科室） */
    private Long writeOffDeptId;
}

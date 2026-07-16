package com.spd.gz.domain.dto;

import lombok.Data;

/**
 * 高值核销确认列表查询
 */
@Data
public class GzHighChargeConfirmQuery
{
    private String tenantId;
    private Long departmentId;
    /** 开单科室（fd_department.id） */
    private Long orderingDepartmentId;
    /** 执行科室（fd_department.id） */
    private Long execDepartmentId;
    /** 0 未确认 / 1 已确认 / 空 全部 */
    private String confirmStatus;
    /** 即入即出：0待审核 / 1已审核 / 2已冲销 / 空全部 */
    private String instantIoAuditStatus;
    /**
     * 退费待反向队列：1=已正向审核、已有退费返还、尚未生成 301/401。
     * 为 1 时忽略 instantIoAuditStatus，固定筛已审核未冲销且 returned_qty&gt;0 且无退货退库单。
     */
    private String pendingRefundReverse;
    /** 核销完成时间起 */
    private String beginConsumeAuditTime;
    /** 核销完成时间止 */
    private String endConsumeAuditTime;
    private String patientName;
    private String visitNo;
    private String chargeItemId;
    /** HIS 收费/费用明细主键（模糊） */
    private String hisChargeId;
    private String materialName;

    /** 表头排序字段（白名单校验） */
    private String sortField;
    /** asc / desc */
    private String sortOrder;

    /** 分页偏移（服务端计算，非前端传入） */
    private Integer offset;

    /** 每页条数（服务端计算，非前端传入） */
    private Integer limitSize;
}

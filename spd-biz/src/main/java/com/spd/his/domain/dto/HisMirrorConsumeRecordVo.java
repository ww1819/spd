package com.spd.his.domain.dto;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * HIS 计费镜像行关联的科室消耗记录（用于前端「消耗记录」展示）
 */
@Data
public class HisMirrorConsumeRecordVo
{
    private String linkId;
    private String visitKind;
    private String mirrorRowId;
    private BigDecimal allocQty;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private Long consumeBillId;
    private String consumeBillNo;
    /** 消耗/审核时间（优先审核时间） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date consumeBillDate;
    /** CONSUME=正向消耗 REVERSE=低值冲销/反消耗 */
    private String recordType;
    private Integer reverseFlag;
    /** 冲销时来源正向消耗单号 */
    private String reverseOfBillNo;
    private Integer consumeBillStatus;
    private Long consumeEntryId;
    private String materialName;
    /** 规格 */
    private String materialSpeci;
    /** 型号 */
    private String materialModel;
    /** 单位 */
    private String unit;
    /** 批次号 */
    private String batchNo;
    /** 批号 */
    private String batchNumber;
    private BigDecimal unitPrice;
    /** 明细数量（冲销为负） */
    private BigDecimal entryQty;
    private BigDecimal amt;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    /** 高值场景可能记在条码字段 */
    private String inHospitalCode;
    /** 核销科室（SPD fd_department.id） */
    private Long writeOffDeptId;
    /** 核销科室名称 */
    private String writeOffDeptName;
}

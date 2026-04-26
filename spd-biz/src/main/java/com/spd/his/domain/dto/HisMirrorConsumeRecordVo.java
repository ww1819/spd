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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date consumeBillDate;
    private Integer consumeBillStatus;
    private Long consumeEntryId;
    private String materialName;
    private String batchNo;
    private String batchNumber;
    private BigDecimal entryQty;
    /** 高值场景可能记在条码字段 */
    private String inHospitalCode;
}

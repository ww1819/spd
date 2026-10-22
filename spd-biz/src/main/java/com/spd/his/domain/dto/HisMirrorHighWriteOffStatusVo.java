package com.spd.his.domain.dto;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 住院高值扫码列表：计费行的高值确认/冲销状态（按当前有效核销单 + 最近一次冲销汇总）。
 */
@Data
public class HisMirrorHighWriteOffStatusVo
{
    private String visitKind;
    private String mirrorRowId;
    /** 当前有效正向计费单 */
    private Long traceabilityId;
    private String traceNo;
    /** 当前有效核销单确认状态：0未确认 1已确认（多明细时任一已确认即 1） */
    private Integer confirmStatus;
    private String confirmBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date confirmTime;
    /** 当前有效核销单即入即出：0待审核 1已审核 2已冲销（多明细取最大） */
    private Integer instantIoAuditStatus;
    /** 是否有冲销记录：0无 1有（取最近一次） */
    private Integer writeOffStatus;
    private String writeOffBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date writeOffTime;
    private String writeOffBillNo;
    /** 当前有效核销单可在本页冲销（整单全部未确认） */
    private Boolean canWriteOff;
    /** 明细行：关联是否已软删（仅聚合前使用） */
    private Integer delFlag;
}

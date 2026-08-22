package com.spd.his.domain.dto;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 计费镜像行核销操作日志（前端「操作记录」展示）
 */
@Data
public class HisMirrorProcessLogVo
{
    private String id;
    private String visitKind;
    private String mirrorRowId;
    /** LOW_CONSUME / LOW_WRITE_OFF / HIGH_CONSUME */
    private String operation;
    /** SUCCESS / FAIL */
    private String outcome;
    private String processType;
    private String situation;
    private String processParty;
    private String processBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date processTime;
}

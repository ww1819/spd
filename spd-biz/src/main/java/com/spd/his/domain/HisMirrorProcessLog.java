package com.spd.his.domain;

import java.util.Date;
import lombok.Data;

/**
 * HIS 计费镜像核销/冲销操作日志（每次成功或失败一条）。
 */
@Data
public class HisMirrorProcessLog
{
    private String id;
    private String tenantId;
    private String visitKind;
    private String mirrorRowId;
    /** LOW_CONSUME / LOW_WRITE_OFF / HIGH_CONSUME */
    private String operation;
    /** SUCCESS / FAIL */
    private String outcome;
    /** LOW_VALUE / HIGH_VALUE */
    private String processType;
    private String situation;
    private String processParty;
    private String processBy;
    private Date processTime;
    private Date createTime;
}

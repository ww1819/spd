package com.spd.his.domain.dto;

import lombok.Data;

@Data
public class HisMirrorManualRowBody
{
    /** INPATIENT / OUTPATIENT */
    private String visitKind;
    /** 镜像表主键 id */
    private String mirrorRowId;
}

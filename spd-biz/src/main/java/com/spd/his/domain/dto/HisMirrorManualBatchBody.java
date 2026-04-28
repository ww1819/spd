package com.spd.his.domain.dto;

import java.util.List;
import lombok.Data;

@Data
public class HisMirrorManualBatchBody
{
    /** INPATIENT / OUTPATIENT */
    private String visitKind;
    /** 镜像表主键 id 列表 */
    private List<String> mirrorRowIds;
}

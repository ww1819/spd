package com.spd.his.domain.dto;

import java.util.List;
import lombok.Data;

@Data
public class HisMirrorHighApplyBody
{
    private String visitKind;
    private String mirrorRowId;
    private List<HisMirrorHighApplyLine> lines;
    /** 处理方：手动处理 / 自动处理；空则默认手动处理 */
    private String processParty;
    /** 核销科室（SPD fd_department.id）；空则按计费执行科室 */
    private Long consumeDepartmentId;
}

package com.spd.his.domain.dto;

import java.util.List;
import lombok.Data;

@Data
public class HisMirrorHighApplyBody
{
    private String visitKind;
    private String mirrorRowId;
    private List<HisMirrorHighApplyLine> lines;
}

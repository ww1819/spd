package com.spd.his.domain.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class HisMirrorWriteOffResultVo
{
    private String mirrorRowId;
    private int relatedRefundWriteOffCount;
    private List<Long> reverseConsumeBillIds = new ArrayList<>();
    private List<Long> reapplyConsumeBillIds = new ArrayList<>();
    private List<String> messages = new ArrayList<>();
}

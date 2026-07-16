package com.spd.gz.domain.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class GzHighValueWriteOffResultVo
{
    private int linkCount;
    private int restoredCount;
    private int reverseBillPairCount;
    private int mirrorResetCount;
    private List<String> messages = new ArrayList<>();
}

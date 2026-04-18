package com.spd.his.domain.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class HisGenerateConsumeResultVo
{
    private List<Long> consumeBillIds = new ArrayList<>();
    private int consumeBillCount;
    private int consumeEntryCount;
    private int linkRowCount;
    private int mirrorLineConsumedCount;
    private int mirrorLineSkippedZeroQty;
    private List<String> messages = new ArrayList<>();
}

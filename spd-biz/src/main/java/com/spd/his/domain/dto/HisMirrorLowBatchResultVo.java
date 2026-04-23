package com.spd.his.domain.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class HisMirrorLowBatchResultVo
{
    private int successCount;
    private int failCount;
    private List<String> failMessages = new ArrayList<>();
}

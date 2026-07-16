package com.spd.gz.domain.dto;

import java.util.List;
import lombok.Data;

/**
 * 高值即入即出反向单据（退货301+退库401）；不选仓库，取原入出库单仓库
 */
@Data
public class GzInstantIoReverseBody
{
    private List<String> linkIds;
}

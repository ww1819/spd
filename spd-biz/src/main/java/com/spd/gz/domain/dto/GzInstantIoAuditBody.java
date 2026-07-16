package com.spd.gz.domain.dto;

import java.util.List;
import lombok.Data;

/**
 * 库房高值即入即出审核（选仓建 G-RK/G-CK）
 */
@Data
public class GzInstantIoAuditBody
{
    private List<String> linkIds;
    private Long warehouseId;
}

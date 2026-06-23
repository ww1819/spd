package com.spd.gz.domain.dto;

import java.util.List;
import lombok.Data;

/**
 * 高值消耗确认提交
 */
@Data
public class GzHighChargeConfirmBody
{
    private List<String> linkIds;
    private Long warehouseId;
    /** 核销科室（SPD fd_department.id）；空则取所选明细科室 */
    private Long departmentId;
}

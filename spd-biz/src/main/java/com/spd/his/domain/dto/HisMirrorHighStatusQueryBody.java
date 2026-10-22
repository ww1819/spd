package com.spd.his.domain.dto;

import java.util.List;
import lombok.Data;

/** 住院高值扫码列表：按页批量查询计费行的高值确认/冲销状态 */
@Data
public class HisMirrorHighStatusQueryBody
{
    private List<String> inpatientRowIds;
    private List<String> outpatientRowIds;
}

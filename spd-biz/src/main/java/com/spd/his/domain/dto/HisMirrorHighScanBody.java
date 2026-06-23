package com.spd.his.domain.dto;

import lombok.Data;

@Data
public class HisMirrorHighScanBody
{
    private String visitKind;
    private String mirrorRowId;
    /** 本科室高值院内码 */
    private String inHospitalCode;
    /** 核销科室（SPD fd_department.id）；空则按计费执行科室 */
    private Long consumeDepartmentId;
}

package com.spd.his.domain.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class HisPatientChargeSummaryRow
{
    /** INPATIENT / OUTPATIENT */
    private String visitType;
    private String patientName;
    /** 住院号或门诊号 */
    private String visitNo;
    private String deptName;
    private Long lineCount;
    private BigDecimal sumAmount;
}

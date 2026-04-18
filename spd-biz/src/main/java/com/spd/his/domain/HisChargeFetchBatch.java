package com.spd.his.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HisChargeFetchBatch extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String id;
    private String tenantId;
    private String chargeKind;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date windowStart;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date windowEnd;
    private Integer insertedCount;
    private Integer skippedCount;
    private Integer driftCount;
}

package com.spd.gz.domain.dto;

import java.util.List;
import lombok.Data;

/**
 * 高值冲销（整单）：所选明细/计费单按正向计费单展开为该单全部明细冲销。
 */
@Data
public class GzHighValueWriteOffBody
{
    private List<String> linkIds;
    /** 正向高值计费单ID（住院高值扫码页按单冲销） */
    private List<Long> traceabilityIds;
    private String remark;
    /**
     * 冲销入口：CONFIRM=高值核销确认页、HIGH_SCAN=住院高值扫码页（仅整单全部未确认）；
     * INSTANT_IO=即入即出页（可混态整单冲销）。
     */
    private String source;
}

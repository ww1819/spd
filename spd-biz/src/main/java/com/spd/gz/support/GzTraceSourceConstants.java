package com.spd.gz.support;

/**
 * 高值计费单来源标识（gz_traceability.trace_source）
 */
public final class GzTraceSourceConstants
{
    /** HIS 高值扫码核销生成的计费单 */
    public static final String HIS_MIRROR_HIGH = "HIS_MIRROR_HIGH";

    /** HIS 高值冲销生成的反向计费单（单独单据，不影响即入即出） */
    public static final String HIS_MIRROR_HIGH_REVERSE = "HIS_MIRROR_HIGH_REVERSE";

    private GzTraceSourceConstants()
    {
    }
}

package com.spd.his.domain.dto;

import lombok.Data;

/**
 * 批量低值核销失败原因汇总项。
 */
@Data
public class HisMirrorFailReasonStatVo
{
    /** 失败原因（已去掉行 id 前缀） */
    private String reason;
    /** 出现次数 */
    private int count;

    public HisMirrorFailReasonStatVo()
    {
    }

    public HisMirrorFailReasonStatVo(String reason, int count)
    {
        this.reason = reason;
        this.count = count;
    }
}

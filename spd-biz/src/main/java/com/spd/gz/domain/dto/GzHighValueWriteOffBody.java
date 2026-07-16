package com.spd.gz.domain.dto;

import java.util.List;
import lombok.Data;

/**
 * 高值冲销：按消耗关联行分档回补科室库存；已审核则先生成/校验反向单据。
 */
@Data
public class GzHighValueWriteOffBody
{
    private List<String> linkIds;
    private String remark;
    /**
     * 冲销入口：CONFIRM=高值核销确认页（仅档 A）；INSTANT_IO=即入即出页（档 B/C）。
     * 空则按数据状态自动分档（兼容旧调用），确认页必须传 CONFIRM。
     */
    private String source;
}

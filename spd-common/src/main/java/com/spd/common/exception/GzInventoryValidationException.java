package com.spd.common.exception;

import java.util.Collections;
import java.util.List;

/**
 * 高值备货库存校验失败：携带明细行错误列表供前端弹窗展示
 */
public class GzInventoryValidationException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    private final List<?> lines;

    public GzInventoryValidationException(String message, List<?> lines)
    {
        super(message);
        this.lines = lines != null ? lines : Collections.emptyList();
    }

    public List<?> getLines()
    {
        return lines;
    }
}

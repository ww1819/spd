package com.spd.common.exception;

import java.util.Collections;
import java.util.List;

/**
 * 低值耗材引用单证明细数量校验失败：携带行级列表供前端弹窗
 */
public class DocRefQtyValidationException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    private final List<?> lines;

    public DocRefQtyValidationException(String message, List<?> lines)
    {
        super(message);
        this.lines = lines != null ? lines : Collections.emptyList();
    }

    public List<?> getLines()
    {
        return lines;
    }
}

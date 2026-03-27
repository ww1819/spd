package com.spd.common.utils.poi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 导入校验：收集整单错误与按 Excel 行号（表头为第 1 行，数据从第 2 行起）的错误，用于返回 previewRows。
 */
public final class ImportRowErrorCollector
{
    private final List<String> allErrors = new ArrayList<>();

    private final Map<Integer, List<String>> byExcelRow = new TreeMap<>();

    public void addGlobal(String msg)
    {
        if (msg != null && !msg.isEmpty())
        {
            allErrors.add(msg);
        }
    }

    public void addRow(int excelRow, String msg)
    {
        if (msg == null || msg.isEmpty())
        {
            return;
        }
        allErrors.add("第" + excelRow + "行：" + msg);
        byExcelRow.computeIfAbsent(excelRow, k -> new ArrayList<>()).add(msg);
    }

    public List<String> getAllErrors()
    {
        return allErrors;
    }

    public List<String> getRowMessages(int excelRow)
    {
        List<String> list = byExcelRow.get(excelRow);
        return list == null ? Collections.emptyList() : list;
    }

    public boolean hasRow(int excelRow)
    {
        List<String> list = byExcelRow.get(excelRow);
        return list != null && !list.isEmpty();
    }

    /**
     * 将 Map 放入导入接口返回体（LinkedHashMap 保证 JSON 字段顺序稳定）。
     */
    public static void putStandardEntries(Map<String, Object> result, ImportRowErrorCollector collector)
    {
        List<String> errors = collector.getAllErrors();
        result.put("valid", errors.isEmpty());
        result.put("errors", errors);
    }
}

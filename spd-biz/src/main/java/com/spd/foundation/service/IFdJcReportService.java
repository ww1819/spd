package com.spd.foundation.service;

import java.util.List;
import com.spd.foundation.domain.FdJcReport;

public interface IFdJcReportService
{
    FdJcReport selectFdJcReportById(Long id);

    List<FdJcReport> selectFdJcReportList(FdJcReport query);

    /** 按当前租户报量模式保存（同周期同维度 upsert） */
    int saveFdJcReport(FdJcReport row);

    int updateFdJcReport(FdJcReport row);

    int deleteFdJcReportById(Long id);

    /** 批量 upsert */
    int batchSave(List<FdJcReport> rows);
}

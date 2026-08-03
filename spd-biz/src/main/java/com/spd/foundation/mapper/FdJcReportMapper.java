package com.spd.foundation.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.foundation.domain.FdJcReport;

/**
 * 集采报量 Mapper
 */
public interface FdJcReportMapper
{
    FdJcReport selectFdJcReportById(Long id);

    List<FdJcReport> selectFdJcReportList(FdJcReport query);

    FdJcReport selectUniqueProduct(@Param("periodId") Long periodId, @Param("materialId") Long materialId,
        @Param("reportMode") String reportMode);

    FdJcReport selectUniqueType(@Param("periodId") Long periodId, @Param("jcTypeId") Long jcTypeId,
        @Param("reportMode") String reportMode);

    int insertFdJcReport(FdJcReport row);

    int updateFdJcReport(FdJcReport row);

    int deleteFdJcReportById(@Param("id") Long id, @Param("deleteBy") String deleteBy);

    int countByMode(@Param("reportMode") String reportMode);
}

package com.spd.foundation.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.foundation.domain.FdJcPeriod;

/**
 * 集采周期 Mapper
 */
public interface FdJcPeriodMapper
{
    FdJcPeriod selectFdJcPeriodById(Long id);

    List<FdJcPeriod> selectFdJcPeriodList(FdJcPeriod query);

    int insertFdJcPeriod(FdJcPeriod row);

    int updateFdJcPeriod(FdJcPeriod row);

    int deleteFdJcPeriodById(@Param("id") Long id, @Param("deleteBy") String deleteBy);

    int countReportRef(@Param("periodId") Long periodId);
}

package com.spd.foundation.service;

import java.util.List;
import com.spd.foundation.domain.FdJcPeriod;

public interface IFdJcPeriodService
{
    FdJcPeriod selectFdJcPeriodById(Long id);

    List<FdJcPeriod> selectFdJcPeriodList(FdJcPeriod query);

    int insertFdJcPeriod(FdJcPeriod row);

    int updateFdJcPeriod(FdJcPeriod row);

    int deleteFdJcPeriodById(Long id);
}

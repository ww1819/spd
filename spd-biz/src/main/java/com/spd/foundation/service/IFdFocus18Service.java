package com.spd.foundation.service;

import java.util.List;
import com.spd.foundation.domain.FdFocus18;

public interface IFdFocus18Service
{
    FdFocus18 selectFdFocus18ById(Long id);

    List<FdFocus18> selectFdFocus18List(FdFocus18 query);

    int insertFdFocus18(FdFocus18 row);

    int updateFdFocus18(FdFocus18 row);

    int deleteFdFocus18ById(Long id);
}

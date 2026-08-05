package com.spd.foundation.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.foundation.domain.FdFocus18;

/**
 * 18类重点耗材 Mapper
 */
public interface FdFocus18Mapper
{
    FdFocus18 selectFdFocus18ById(Long id);

    List<FdFocus18> selectFdFocus18List(FdFocus18 query);

    int insertFdFocus18(FdFocus18 row);

    int updateFdFocus18(FdFocus18 row);

    int deleteFdFocus18ById(@Param("id") Long id, @Param("deleteBy") String deleteBy);
}

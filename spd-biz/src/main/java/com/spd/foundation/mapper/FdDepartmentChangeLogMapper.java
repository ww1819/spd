package com.spd.foundation.mapper;

import com.spd.foundation.domain.FdDepartmentChangeLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FdDepartmentChangeLogMapper {

    int insert(FdDepartmentChangeLog record);

    List<FdDepartmentChangeLog> selectByDepartmentId(@Param("departmentId") Long departmentId);
}

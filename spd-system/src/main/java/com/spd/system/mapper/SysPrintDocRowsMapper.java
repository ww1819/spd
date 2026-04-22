package com.spd.system.mapper;

import org.apache.ibatis.annotations.Param;
import com.spd.system.domain.SysPrintDocRows;

/**
 * 单据打印每页行数 数据层
 *
 * @author spd
 */
public interface SysPrintDocRowsMapper
{
    SysPrintDocRows selectByDocKind(@Param("docKind") String docKind);

    int insertSysPrintDocRows(SysPrintDocRows row);

    int updateRowsByDocKind(SysPrintDocRows row);
}

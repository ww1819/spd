package com.spd.gz.mapper;

import com.spd.gz.domain.GzBillEntryChangeLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GzBillEntryChangeLogMapper {
    int insert(GzBillEntryChangeLog record);

    List<GzBillEntryChangeLog> selectByBill(@Param("billType") String billType, @Param("billId") Long billId);
}

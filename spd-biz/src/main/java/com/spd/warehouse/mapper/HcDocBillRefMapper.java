package com.spd.warehouse.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.spd.warehouse.domain.HcDocBillRef;

@Mapper
@Repository
public interface HcDocBillRefMapper {

    int insertHcDocBillRef(HcDocBillRef row);

    int softDeleteByTgtBillId(@Param("tenantId") String tenantId, @Param("tgtBillId") String tgtBillId,
        @Param("deleteBy") String deleteBy);

    List<HcDocBillRef> selectByTgtBillId(@Param("tgtBillId") String tgtBillId);

    List<Map<String, Object>> selectRefQtySumBySrcBillId(@Param("tenantId") String tenantId,
        @Param("srcBillId") String srcBillId);
}

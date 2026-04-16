package com.spd.warehouse.mapper;

import java.math.BigDecimal;
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

    /**
     * 源单明细已被其它目标单占用的引用数量（可选排除某目标单，用于修改本单时先扣掉本单旧关联）
     */
    BigDecimal sumRefQtyBySrcEntryExcludingTgtBill(@Param("tenantId") String tenantId,
        @Param("srcBillId") String srcBillId,
        @Param("srcEntryId") String srcEntryId,
        @Param("excludeTgtBillId") String excludeTgtBillId);
}

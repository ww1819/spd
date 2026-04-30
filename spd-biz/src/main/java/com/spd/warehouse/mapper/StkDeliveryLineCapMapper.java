package com.spd.warehouse.mapper;

import com.spd.warehouse.domain.StkDeliveryLineCap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface StkDeliveryLineCapMapper {

    StkDeliveryLineCap selectByUk(@Param("tenantId") String tenantId,
        @Param("deliveryNo") String deliveryNo,
        @Param("lineSign") String lineSign);

    int upsertMaxCap(StkDeliveryLineCap row);
}

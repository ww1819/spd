package com.spd.his.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.his.domain.HisBillingRefundOrderLine;

public interface HisBillingRefundOrderLineMapper
{
    int insertBatch(@Param("list") List<HisBillingRefundOrderLine> list);
}

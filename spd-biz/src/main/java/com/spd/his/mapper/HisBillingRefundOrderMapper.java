package com.spd.his.mapper;

import org.apache.ibatis.annotations.Param;
import com.spd.his.domain.HisBillingRefundOrder;

public interface HisBillingRefundOrderMapper
{
    int insertHisBillingRefundOrder(HisBillingRefundOrder row);

    int updateProcessStatus(@Param("tenantId") String tenantId, @Param("id") String id,
        @Param("processStatus") String processStatus, @Param("failReason") String failReason,
        @Param("updateBy") String updateBy);
}

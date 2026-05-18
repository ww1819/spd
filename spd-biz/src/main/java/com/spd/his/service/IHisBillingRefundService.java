package com.spd.his.service;

import com.spd.his.domain.HisBillingRefundOrder;
import com.spd.his.domain.dto.HisBillingRefundHighBody;
import com.spd.his.domain.dto.HisBillingRefundLowBody;

public interface IHisBillingRefundService
{
    HisBillingRefundOrder processLowValueRefund(HisBillingRefundLowBody body);

    HisBillingRefundOrder processHighValueRefund(HisBillingRefundHighBody body);

    /**
     * 抓取批次内待处理退费镜像行自动返还库存（需租户开关；单行失败不影响其它行）
     */
    void processAutoRefundForFetchBatch(String tenantId, String fetchBatchId, String visitKind);
}

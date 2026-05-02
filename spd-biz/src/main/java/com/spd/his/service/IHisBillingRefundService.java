package com.spd.his.service;

import com.spd.his.domain.HisBillingRefundOrder;
import com.spd.his.domain.dto.HisBillingRefundHighBody;
import com.spd.his.domain.dto.HisBillingRefundLowBody;

public interface IHisBillingRefundService
{
    HisBillingRefundOrder processLowValueRefund(HisBillingRefundLowBody body);

    HisBillingRefundOrder processHighValueRefund(HisBillingRefundHighBody body);
}

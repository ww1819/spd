package com.spd.his.service.refund;

import java.util.List;
import com.spd.his.domain.HisMirrorConsumeLink;

/**
 * 低值退费：计费消耗关联行返还顺序（可多批次时按策略排序）
 */
public interface LvRefundConsumeLinkOrderStrategy
{
    List<HisMirrorConsumeLink> sortForRefund(List<HisMirrorConsumeLink> links);
}

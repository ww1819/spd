package com.spd.his.service.refund.impl;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.spd.his.domain.HisMirrorConsumeLink;
import com.spd.his.service.refund.LvRefundConsumeLinkOrderStrategy;

/**
 * 默认策略：按科室库存有效期快照降序（效期最远/最长优先），空日期排在最后。
 */
@Component
public class LvRefundByEndDateDescStrategy implements LvRefundConsumeLinkOrderStrategy
{
    @Override
    public List<HisMirrorConsumeLink> sortForRefund(List<HisMirrorConsumeLink> links)
    {
        if (links == null || links.isEmpty())
        {
            return links;
        }
        return links.stream()
            .sorted(Comparator
                .comparing(HisMirrorConsumeLink::getStkDepEndDate, Comparator.nullsLast(Date::compareTo))
                .reversed()
                .thenComparing(HisMirrorConsumeLink::getId, Comparator.nullsLast(String::compareTo)))
            .collect(Collectors.toList());
    }
}

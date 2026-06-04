package com.spd.gz.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.spd.common.utils.StringUtils;
import com.spd.gz.mapper.GzOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 备货验收单出库引用状态（shipment_ref_status）重算
 */
@Service
public class GzShipmentRefStatusService
{
    @Autowired
    private GzOrderMapper gzOrderMapper;

    public void recalculate(Long acceptanceOrderId)
    {
        if (acceptanceOrderId == null)
        {
            return;
        }
        long total = gzOrderMapper.countAcceptanceBarcodeLines(acceptanceOrderId);
        long occupied = gzOrderMapper.countShipmentOccupiedBarcodeLines(acceptanceOrderId);
        int status = 0;
        if (total > 0)
        {
            if (occupied <= 0)
            {
                status = 0;
            }
            else if (occupied >= total)
            {
                status = 2;
            }
            else
            {
                status = 1;
            }
        }
        gzOrderMapper.updateShipmentRefStatus(acceptanceOrderId, status);
    }

    public void recalculateBatch(Collection<Long> acceptanceOrderIds)
    {
        if (acceptanceOrderIds == null || acceptanceOrderIds.isEmpty())
        {
            return;
        }
        Set<Long> ids = new HashSet<>();
        for (Long id : acceptanceOrderIds)
        {
            if (id != null)
            {
                ids.add(id);
            }
        }
        for (Long id : ids)
        {
            recalculate(id);
        }
    }

    public void recalculateByAcceptanceIdStr(Collection<String> acceptanceIdStrs)
    {
        if (acceptanceIdStrs == null || acceptanceIdStrs.isEmpty())
        {
            return;
        }
        Set<Long> ids = new HashSet<>();
        for (String s : acceptanceIdStrs)
        {
            if (StringUtils.isEmpty(s))
            {
                continue;
            }
            try
            {
                ids.add(Long.parseLong(s.trim()));
            }
            catch (NumberFormatException ignored)
            {
                // ignore non-numeric legacy ids
            }
        }
        recalculateBatch(ids);
    }
}

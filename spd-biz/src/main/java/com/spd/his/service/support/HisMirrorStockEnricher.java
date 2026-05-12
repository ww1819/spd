package com.spd.his.service.support;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.spd.his.domain.HisPatientChargeMirrorUnified;
import com.spd.his.domain.dto.HisMirrorStockLocItemQty;
import com.spd.his.mapper.HisMirrorStockAggregateMapper;
import com.spd.his.support.HisPatientChargeMirrorUnifiedSupport;

/**
 * 计费镜像列表分页返回后，再按「科室HIS编码|收费项」批量查询高低值库存并回填。
 */
@Component
public class HisMirrorStockEnricher
{
    private static final int PAIR_KEY_CHUNK = 400;

    @Autowired
    private HisMirrorStockAggregateMapper hisMirrorStockAggregateMapper;

    public void enrichUnifiedList(String tenantId, List<HisPatientChargeMirrorUnified> rows)
    {
        if (StringUtils.isEmpty(tenantId) || rows == null || rows.isEmpty())
        {
            return;
        }
        Set<String> keys = new HashSet<>();
        for (HisPatientChargeMirrorUnified u : rows)
        {
            String pk = HisPatientChargeMirrorUnifiedSupport.stockPairKey(u);
            if (StringUtils.isNotEmpty(pk))
            {
                keys.add(pk);
            }
        }
        Map<String, BigDecimal> gz = loadGz(tenantId, keys);
        Map<String, BigDecimal> stk = loadStk(tenantId, keys);
        for (HisPatientChargeMirrorUnified u : rows)
        {
            String pk = HisPatientChargeMirrorUnifiedSupport.stockPairKey(u);
            if (StringUtils.isEmpty(pk))
            {
                u.setHighValueStockQty(BigDecimal.ZERO);
                u.setLowValueStockQty(BigDecimal.ZERO);
                continue;
            }
            u.setHighValueStockQty(nz(gz.get(pk)));
            u.setLowValueStockQty(nz(stk.get(pk)));
        }
    }

    private Map<String, BigDecimal> loadGz(String tenantId, Set<String> keys)
    {
        Map<String, BigDecimal> map = new HashMap<>();
        if (keys.isEmpty())
        {
            return map;
        }
        List<String> all = new ArrayList<>(keys);
        for (int i = 0; i < all.size(); i += PAIR_KEY_CHUNK)
        {
            int end = Math.min(i + PAIR_KEY_CHUNK, all.size());
            List<String> chunk = all.subList(i, end);
            List<HisMirrorStockLocItemQty> rows = hisMirrorStockAggregateMapper.selectGzStockByPairKeys(tenantId, chunk);
            if (rows != null)
            {
                for (HisMirrorStockLocItemQty r : rows)
                {
                    if (r != null && StringUtils.isNotEmpty(r.getPairKey()))
                    {
                        map.put(r.getPairKey(), r.getQty());
                    }
                }
            }
        }
        return map;
    }

    private Map<String, BigDecimal> loadStk(String tenantId, Set<String> keys)
    {
        Map<String, BigDecimal> map = new HashMap<>();
        if (keys.isEmpty())
        {
            return map;
        }
        List<String> all = new ArrayList<>(keys);
        for (int i = 0; i < all.size(); i += PAIR_KEY_CHUNK)
        {
            int end = Math.min(i + PAIR_KEY_CHUNK, all.size());
            List<String> chunk = all.subList(i, end);
            List<HisMirrorStockLocItemQty> rows = hisMirrorStockAggregateMapper.selectStkStockByPairKeys(tenantId, chunk);
            if (rows != null)
            {
                for (HisMirrorStockLocItemQty r : rows)
                {
                    if (r != null && StringUtils.isNotEmpty(r.getPairKey()))
                    {
                        map.put(r.getPairKey(), r.getQty());
                    }
                }
            }
        }
        return map;
    }

    private static BigDecimal nz(BigDecimal v)
    {
        return v == null ? BigDecimal.ZERO : v;
    }
}

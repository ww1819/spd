package com.spd.his.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.his.domain.dto.HisMirrorStockLocItemQty;

/**
 * 按「科室HIS编码|收费项」批量汇总高低值库存（用于列表分页后填充）。
 */
public interface HisMirrorStockAggregateMapper
{
    List<HisMirrorStockLocItemQty> selectGzStockByPairKeys(@Param("tenantId") String tenantId, @Param("pairKeys") List<String> pairKeys);

    List<HisMirrorStockLocItemQty> selectStkStockByPairKeys(@Param("tenantId") String tenantId, @Param("pairKeys") List<String> pairKeys);
}

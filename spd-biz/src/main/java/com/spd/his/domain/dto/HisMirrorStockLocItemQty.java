package com.spd.his.domain.dto;

import java.math.BigDecimal;
import lombok.Data;

/** 科室HIS编码 + 收费项 维度的库存汇总（pairKey = trim(his_id)|trim(charge_item_id)） */
@Data
public class HisMirrorStockLocItemQty
{
    private String pairKey;
    private BigDecimal qty;
}

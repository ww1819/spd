package com.spd.department.availablestock;

import java.util.List;
import java.util.Map;

import com.spd.warehouse.domain.StkInventory;

/**
 * 科室申领「新增明细」可用库存展示策略：与库存明细列表解耦，便于按客户替换计算口径（聚合维度、仓类过滤、计价方式等）。
 * 默认实现按「耗材+仓库」返回行，前端落库 {@code bas_apply_entry.stock_warehouse_id}，审核时仅在该仓内 FIFO 拆分。
 */
public interface DepartmentApplyAvailableStockStrategy {

    /**
     * 分页列表由调用方 {@code PageHelper.startPage} 包裹后执行本方法。
     *
     * @param query 查询条件（tenantId、materialName 等），不含仓库过滤
     * @return 每行 Map 字段需与前端 {@code SelectDepartmentApplyAvailableStock} 约定一致
     */
    List<Map<String, Object>> listAggregated(StkInventory query);
}

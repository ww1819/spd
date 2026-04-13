package com.spd.department.availablestock.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.department.availablestock.DepartmentApplyAvailableStockStrategy;
import com.spd.warehouse.domain.StkInventory;
import com.spd.warehouse.mapper.StkInventoryMapper;

/**
 * 默认策略：按「耗材 + 仓库」聚合 SUM(qty)（单价为金额加权平均），排除「高值」「设备」仓类；科室明细保存仓库 ID 后审核仅在该仓拆分，避免串库。
 * <p>替换方式：新增另一实现类并标注 {@code @Primary}，或改用 Spring 条件装配。</p>
 */
@Component
@Primary
public class DefaultDepartmentApplyAvailableStockStrategy implements DepartmentApplyAvailableStockStrategy {

    @Autowired
    private StkInventoryMapper stkInventoryMapper;

    @Override
    public List<Map<String, Object>> listAggregated(StkInventory query) {
        if (query == null) {
            query = new StkInventory();
        }
        if (StringUtils.isEmpty(query.getTenantId())) {
            query.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        return stkInventoryMapper.selectMaterialAvailableAggForDeptApply(query);
    }
}

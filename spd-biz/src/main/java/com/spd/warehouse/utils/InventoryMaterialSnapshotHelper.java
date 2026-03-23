package com.spd.warehouse.utils;

import com.spd.common.utils.StringUtils;
import com.spd.department.domain.StkDepInventory;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.warehouse.domain.StkIoBillEntry;
import com.spd.warehouse.domain.StkInventory;

/**
 * 仓库/科室库存行上的耗材名称、规格、型号、厂家快照（与明细或档案一致时写入）。
 */
public final class InventoryMaterialSnapshotHelper {

    private InventoryMaterialSnapshotHelper() {
    }

    public static void fillWarehouseRow(StkInventory inv, StkIoBillEntry entry, FdMaterialMapper fdMaterialMapper, String tenantId) {
        if (inv == null) {
            return;
        }
        if (entry != null) {
            if (StringUtils.isNotEmpty(entry.getMaterialName())) {
                inv.setSnapMaterialName(entry.getMaterialName());
            }
            if (StringUtils.isNotEmpty(entry.getMaterialSpeci())) {
                inv.setSnapMaterialSpeci(entry.getMaterialSpeci());
            }
            if (StringUtils.isNotEmpty(entry.getMaterialModel())) {
                inv.setSnapMaterialModel(entry.getMaterialModel());
            }
            if (entry.getMaterialFactoryId() != null) {
                inv.setSnapMaterialFactoryId(entry.getMaterialFactoryId());
            }
        }
        Long mid = inv.getMaterialId();
        if (mid == null || fdMaterialMapper == null) {
            return;
        }
        FdMaterial m = null;
        if (StringUtils.isNotEmpty(tenantId)) {
            m = fdMaterialMapper.selectFdMaterialByIdAndTenant(mid, tenantId);
        }
        if (m == null) {
            m = fdMaterialMapper.selectFdMaterialById(mid);
        }
        if (m == null) {
            return;
        }
        if (StringUtils.isEmpty(inv.getSnapMaterialName())) {
            inv.setSnapMaterialName(m.getName());
        }
        if (StringUtils.isEmpty(inv.getSnapMaterialSpeci())) {
            inv.setSnapMaterialSpeci(m.getSpeci());
        }
        if (StringUtils.isEmpty(inv.getSnapMaterialModel())) {
            inv.setSnapMaterialModel(m.getModel());
        }
        if (inv.getSnapMaterialFactoryId() == null && m.getFactoryId() != null) {
            inv.setSnapMaterialFactoryId(m.getFactoryId());
        }
        if (inv.getFactoryId() == null && m.getFactoryId() != null) {
            inv.setFactoryId(m.getFactoryId());
        }
    }

    /**
     * 科室库存：优先出库明细快照，其次仓库库存行快照，再回落耗材档案。
     */
    public static void fillDepRow(StkDepInventory dep, StkIoBillEntry entry, StkInventory whInv, FdMaterialMapper fdMaterialMapper, String tenantId) {
        if (dep == null) {
            return;
        }
        if (entry != null) {
            if (StringUtils.isNotEmpty(entry.getMaterialName())) {
                dep.setSnapMaterialName(entry.getMaterialName());
            }
            if (StringUtils.isNotEmpty(entry.getMaterialSpeci())) {
                dep.setSnapMaterialSpeci(entry.getMaterialSpeci());
            }
            if (StringUtils.isNotEmpty(entry.getMaterialModel())) {
                dep.setSnapMaterialModel(entry.getMaterialModel());
            }
            if (entry.getMaterialFactoryId() != null) {
                dep.setSnapMaterialFactoryId(entry.getMaterialFactoryId());
            }
        }
        if (whInv != null) {
            if (StringUtils.isEmpty(dep.getSnapMaterialName()) && StringUtils.isNotEmpty(whInv.getSnapMaterialName())) {
                dep.setSnapMaterialName(whInv.getSnapMaterialName());
            }
            if (StringUtils.isEmpty(dep.getSnapMaterialSpeci()) && StringUtils.isNotEmpty(whInv.getSnapMaterialSpeci())) {
                dep.setSnapMaterialSpeci(whInv.getSnapMaterialSpeci());
            }
            if (StringUtils.isEmpty(dep.getSnapMaterialModel()) && StringUtils.isNotEmpty(whInv.getSnapMaterialModel())) {
                dep.setSnapMaterialModel(whInv.getSnapMaterialModel());
            }
            if (dep.getSnapMaterialFactoryId() == null && whInv.getSnapMaterialFactoryId() != null) {
                dep.setSnapMaterialFactoryId(whInv.getSnapMaterialFactoryId());
            }
        }
        Long mid = dep.getMaterialId();
        if (mid == null || fdMaterialMapper == null) {
            return;
        }
        FdMaterial m = null;
        if (StringUtils.isNotEmpty(tenantId)) {
            m = fdMaterialMapper.selectFdMaterialByIdAndTenant(mid, tenantId);
        }
        if (m == null) {
            m = fdMaterialMapper.selectFdMaterialById(mid);
        }
        if (m == null) {
            return;
        }
        if (StringUtils.isEmpty(dep.getSnapMaterialName())) {
            dep.setSnapMaterialName(m.getName());
        }
        if (StringUtils.isEmpty(dep.getSnapMaterialSpeci())) {
            dep.setSnapMaterialSpeci(m.getSpeci());
        }
        if (StringUtils.isEmpty(dep.getSnapMaterialModel())) {
            dep.setSnapMaterialModel(m.getModel());
        }
        if (dep.getSnapMaterialFactoryId() == null && m.getFactoryId() != null) {
            dep.setSnapMaterialFactoryId(m.getFactoryId());
        }
        if (dep.getFactoryId() == null && m.getFactoryId() != null) {
            dep.setFactoryId(m.getFactoryId());
        }
    }
}

package com.spd.foundation.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.spd.common.utils.StringUtils;
import com.spd.foundation.domain.FdFactory;
import com.spd.foundation.domain.FdFinanceCategory;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.domain.FdUnit;
import com.spd.foundation.domain.FdWarehouse;
import com.spd.foundation.domain.FdWarehouseCategory;
import com.spd.foundation.domain.MaterialArchiveView;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.warehouse.domain.StkBatch;

/**
 * 写快照取数收敛：批查耗材档案（已 JOIN 维表），从嵌套对象填批次/流水快照，避免按维表再单查。
 */
public final class MaterialSnapshotAssembler {

    private static final int IN_CHUNK = 200;

    private MaterialSnapshotAssembler() {
    }

    /**
     * 一次（或分片）批查耗材，返回 id → 档案（含 supplier/fdFactory/fdUnit 等嵌套）。
     */
    public static Map<Long, FdMaterial> loadMap(FdMaterialMapper mapper, Collection<Long> ids) {
        if (mapper == null || ids == null || ids.isEmpty()) {
            return new HashMap<>();
        }
        List<Long> distinct = ids.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, FdMaterial> map = new HashMap<>(Math.max(16, distinct.size() * 2));
        for (int i = 0; i < distinct.size(); i += IN_CHUNK) {
            List<Long> chunk = distinct.subList(i, Math.min(i + IN_CHUNK, distinct.size()));
            List<FdMaterial> list = mapper.selectFdMaterialByIds(new ArrayList<>(chunk));
            if (list == null) {
                continue;
            }
            for (FdMaterial m : list) {
                if (m != null && m.getId() != null) {
                    map.put(m.getId(), m);
                }
            }
        }
        return map;
    }

    public static FdMaterial get(Map<Long, FdMaterial> cache, Long materialId) {
        if (cache == null || materialId == null) {
            return null;
        }
        return cache.get(materialId);
    }

    /**
     * 优先缓存；未命中再单查并回填缓存（兼容旧调用）。
     */
    public static FdMaterial resolve(FdMaterialMapper mapper, Long materialId, String tenantId, Map<Long, FdMaterial> cache) {
        if (materialId == null) {
            return null;
        }
        if (cache != null && cache.containsKey(materialId)) {
            return cache.get(materialId);
        }
        FdMaterial m = null;
        if (mapper != null) {
            if (StringUtils.isNotEmpty(tenantId)) {
                m = mapper.selectFdMaterialByIdAndTenant(materialId, tenantId);
            }
            if (m == null) {
                m = mapper.selectFdMaterialById(materialId);
            }
        }
        if (m != null && cache != null) {
            cache.put(materialId, m);
        }
        return m;
    }

    public static MaterialArchiveView viewOf(FdMaterial m) {
        if (m == null) {
            return null;
        }
        MaterialArchiveView v = new MaterialArchiveView();
        v.setId(m.getId());
        v.setCode(m.getCode());
        v.setName(m.getName());
        v.setSpeci(m.getSpeci());
        v.setModel(m.getModel());
        v.setRegisterNo(m.getRegisterNo());
        v.setPermitNo(m.getPermitNo());
        v.setUnitId(m.getUnitId());
        v.setFactoryId(m.getFactoryId());
        v.setSupplierId(m.getSupplierId());
        v.setStoreroomId(m.getStoreroomId());
        v.setFinanceCategoryId(m.getFinanceCategoryId());
        v.setDefaultWarehouseId(m.getDefaultWarehouseId());

        FdUnit unit = m.getFdUnit();
        if (unit != null) {
            v.setUnitId(unit.getUnitId() != null ? unit.getUnitId() : m.getUnitId());
            v.setUnitCode(unit.getUnitCode());
            v.setUnitName(unit.getUnitName());
        }
        FdFactory factory = m.getFdFactory();
        if (factory != null) {
            v.setFactoryId(factory.getFactoryId() != null ? factory.getFactoryId() : m.getFactoryId());
            v.setFactoryCode(factory.getFactoryCode());
            v.setFactoryName(factory.getFactoryName());
        }
        FdSupplier supplier = m.getSupplier();
        if (supplier != null) {
            v.setSupplierId(supplier.getId() != null ? supplier.getId() : m.getSupplierId());
            v.setSupplierCode(supplier.getCode());
            v.setSupplierName(supplier.getName());
        }
        FdWarehouseCategory storeroom = m.getFdWarehouseCategory();
        if (storeroom != null) {
            v.setStoreroomId(storeroom.getWarehouseCategoryId() != null ? storeroom.getWarehouseCategoryId() : m.getStoreroomId());
            v.setStoreroomCode(storeroom.getWarehouseCategoryCode());
            v.setStoreroomName(storeroom.getWarehouseCategoryName());
        }
        FdFinanceCategory finance = m.getFdFinanceCategory();
        if (finance != null) {
            v.setFinanceCategoryId(finance.getFinanceCategoryId() != null ? finance.getFinanceCategoryId() : m.getFinanceCategoryId());
            v.setFinanceCategoryCode(finance.getFinanceCategoryCode());
            v.setFinanceCategoryName(finance.getFinanceCategoryName());
        }
        return v;
    }

    /**
     * 从已 JOIN 的档案嵌套填批次材料维快照（不再 select 单位/厂家/分类）。
     */
    public static void applyMaterialDimsToStkBatch(StkBatch batch, FdMaterial material) {
        if (batch == null || material == null) {
            return;
        }
        MaterialArchiveView v = viewOf(material);
        applyMaterialDimsToStkBatch(batch, v);
    }

    public static void applyMaterialDimsToStkBatch(StkBatch batch, MaterialArchiveView v) {
        if (batch == null || v == null) {
            return;
        }
        batch.setMaterialCode(v.getCode());
        batch.setMaterialName(v.getName());
        batch.setSpeci(v.getSpeci());
        batch.setModel(v.getModel());
        batch.setRegisterNo(v.getRegisterNo());
        batch.setPermitNo(v.getPermitNo());
        if (v.getUnitId() != null) {
            batch.setUnitId(v.getUnitId());
        }
        if (StringUtils.isNotEmpty(v.getUnitName())) {
            batch.setUnitName(v.getUnitName());
        }
        if (v.getFactoryId() != null) {
            batch.setFactoryId(v.getFactoryId());
        }
        if (StringUtils.isNotEmpty(v.getFactoryCode())) {
            batch.setFactoryCode(v.getFactoryCode());
        }
        if (StringUtils.isNotEmpty(v.getFactoryName())) {
            batch.setFactoryName(v.getFactoryName());
        }
        if (v.getStoreroomId() != null) {
            batch.setStoreroomId(v.getStoreroomId());
        }
        if (StringUtils.isNotEmpty(v.getStoreroomCode())) {
            batch.setStoreroomCode(v.getStoreroomCode());
        }
        if (StringUtils.isNotEmpty(v.getStoreroomName())) {
            batch.setStoreroomName(v.getStoreroomName());
        }
        if (v.getFinanceCategoryId() != null) {
            batch.setFinanceCategoryId(v.getFinanceCategoryId());
        }
        if (StringUtils.isNotEmpty(v.getFinanceCategoryCode())) {
            batch.setFinanceCategoryCode(v.getFinanceCategoryCode());
        }
        if (StringUtils.isNotEmpty(v.getFinanceCategoryName())) {
            batch.setFinanceCategoryName(v.getFinanceCategoryName());
        }
    }

    public static void applySupplierToStkBatch(StkBatch batch, FdSupplier supplier) {
        if (batch == null || supplier == null) {
            return;
        }
        batch.setSupplierId(supplier.getId());
        batch.setSupplierCode(supplier.getCode());
        batch.setSupplierName(supplier.getName());
    }

    public static void applyWarehouseToStkBatch(StkBatch batch, FdWarehouse warehouse) {
        if (batch == null || warehouse == null) {
            return;
        }
        if (warehouse.getId() != null) {
            batch.setWarehouseId(warehouse.getId());
        }
        batch.setWarehouseCode(warehouse.getCode());
        batch.setWarehouseName(warehouse.getName());
    }

    public static void applyWarehouseToStkBatch(StkBatch batch, Long warehouseId, FdWarehouse warehouse) {
        if (batch == null) {
            return;
        }
        if (warehouseId != null) {
            batch.setWarehouseId(warehouseId);
        }
        if (warehouse != null) {
            batch.setWarehouseCode(warehouse.getCode());
            batch.setWarehouseName(warehouse.getName());
        }
    }

    public static List<Long> collectMaterialIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return ids.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }
}

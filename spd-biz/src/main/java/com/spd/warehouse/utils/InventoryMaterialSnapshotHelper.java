package com.spd.warehouse.utils;

import com.spd.common.utils.StringUtils;
import com.spd.department.domain.BasApply;
import com.spd.department.domain.BasApplyEntry;
import com.spd.department.domain.DeptBatchConsume;
import com.spd.department.domain.DeptBatchConsumeEntry;
import com.spd.department.domain.HcKsFlow;
import com.spd.department.domain.StkDepInventory;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.warehouse.domain.HcCkFlow;
import com.spd.warehouse.domain.StkInitialImport;
import com.spd.warehouse.domain.StkInitialImportEntry;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.domain.StkIoBillEntry;
import com.spd.warehouse.domain.StkIoProfitLoss;
import com.spd.warehouse.domain.StkIoProfitLossEntry;
import com.spd.warehouse.domain.StkInventory;
import com.spd.warehouse.domain.StkIoStocktaking;
import com.spd.warehouse.domain.StkIoStocktakingEntry;

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

    private static String longStr(Long v) {
        return v == null ? null : String.valueOf(v);
    }

    private static FdMaterial loadFdMaterial(Long materialId, FdMaterialMapper fdMaterialMapper, String tenantId) {
        if (materialId == null || fdMaterialMapper == null) {
            return null;
        }
        FdMaterial m = null;
        if (StringUtils.isNotEmpty(tenantId)) {
            m = fdMaterialMapper.selectFdMaterialByIdAndTenant(materialId, tenantId);
        }
        if (m == null) {
            m = fdMaterialMapper.selectFdMaterialById(materialId);
        }
        return m;
    }

    /** 仓库流水：仓库/供应商/科室 varchar + 科室 bigint（按科室统计） */
    public static void applyHcCkFlowWarehouseSnapshots(HcCkFlow f, Long warehouseId, Long supplierId, Long departmentId) {
        if (f == null) {
            return;
        }
        f.setWarehouseIdStr(longStr(warehouseId));
        f.setSupplierIdStr(longStr(supplierId));
        f.setDepartmentIdStr(longStr(departmentId));
        f.setDepartmentId(departmentId);
    }

    public static void applyHcCkFlowNumericStrMirrors(HcCkFlow f) {
        if (f == null) {
            return;
        }
        f.setBillIdStr(longStr(f.getBillId()));
        f.setEntryIdStr(longStr(f.getEntryId()));
        f.setMaterialIdStr(longStr(f.getMaterialId()));
        f.setKcNoStr(longStr(f.getKcNo()));
        f.setBatchIdStr(longStr(f.getBatchId()));
        f.setFactoryIdStr(longStr(f.getFactoryId()));
    }

    private static void fillHcCkFlowMaterialCodeName(HcCkFlow f, Long materialId, FdMaterial nestedMaterial,
        String nameHint, String codeHint, FdMaterialMapper fdMaterialMapper, String tenantId) {
        if (f == null) {
            return;
        }
        Long mid = materialId != null ? materialId : f.getMaterialId();
        String code = codeHint;
        String name = nameHint;
        if (nestedMaterial != null) {
            if (StringUtils.isEmpty(code)) {
                code = nestedMaterial.getCode();
            }
            if (StringUtils.isEmpty(name)) {
                name = nestedMaterial.getName();
            }
        }
        FdMaterial doc = loadFdMaterial(mid, fdMaterialMapper, tenantId);
        if (doc != null) {
            if (StringUtils.isEmpty(code)) {
                code = doc.getCode();
            }
            if (StringUtils.isEmpty(name)) {
                name = doc.getName();
            }
        }
        f.setMaterialCode(code);
        f.setMaterialName(name);
    }

    /** 出入库审核等：低值仓库流水快照（与高值 gz_wh_flow 维度对齐） */
    public static void enrichHcCkFlowAfterStkIo(HcCkFlow f, StkIoBill bill, StkIoBillEntry entry,
        Long warehouseId, Long supplierId, Long departmentId, FdMaterialMapper fdMaterialMapper) {
        if (f == null) {
            return;
        }
        applyHcCkFlowWarehouseSnapshots(f, warehouseId, supplierId, departmentId);
        if (bill != null && StringUtils.isNotEmpty(bill.getBillNo())) {
            f.setBillNo(bill.getBillNo());
        }
        applyHcCkFlowNumericStrMirrors(f);
        fillHcCkFlowMaterialCodeName(f, f.getMaterialId(), entry != null ? entry.getMaterial() : null,
            entry != null ? entry.getMaterialName() : null, null, fdMaterialMapper, bill != null ? bill.getTenantId() : null);
    }

    public static void applyHcKsFlowDeptWhSnapshots(HcKsFlow f, Long warehouseId, Long departmentId) {
        if (f == null) {
            return;
        }
        f.setWarehouseIdStr(longStr(warehouseId));
        f.setDepartmentIdStr(longStr(departmentId));
    }

    public static void applyHcKsFlowNumericStrMirrors(HcKsFlow f) {
        if (f == null) {
            return;
        }
        f.setBillIdStr(longStr(f.getBillId()));
        f.setEntryIdStr(longStr(f.getEntryId()));
        f.setMaterialIdStr(longStr(f.getMaterialId()));
        f.setKcNoStr(longStr(f.getKcNo()));
        f.setBatchIdStr(longStr(f.getBatchId()));
        f.setFactoryIdStr(longStr(f.getFactoryId()));
    }

    private static void fillHcKsFlowMaterialCodeName(HcKsFlow f, Long materialId, FdMaterial nestedMaterial,
        String nameHint, FdMaterialMapper fdMaterialMapper, String tenantId) {
        if (f == null) {
            return;
        }
        Long mid = materialId != null ? materialId : f.getMaterialId();
        String code = null;
        String name = nameHint;
        if (nestedMaterial != null) {
            code = nestedMaterial.getCode();
            if (StringUtils.isEmpty(name)) {
                name = nestedMaterial.getName();
            }
        }
        FdMaterial doc = loadFdMaterial(mid, fdMaterialMapper, tenantId);
        if (doc != null) {
            if (StringUtils.isEmpty(code)) {
                code = doc.getCode();
            }
            if (StringUtils.isEmpty(name)) {
                name = doc.getName();
            }
        }
        f.setMaterialCode(code);
        f.setMaterialName(name);
    }

    public static void enrichHcKsFlowAfterStkIo(HcKsFlow f, StkIoBill bill, StkIoBillEntry entry,
        Long warehouseId, Long departmentId, FdMaterialMapper fdMaterialMapper) {
        if (f == null) {
            return;
        }
        applyHcKsFlowDeptWhSnapshots(f, warehouseId, departmentId);
        if (bill != null && StringUtils.isNotEmpty(bill.getBillNo())) {
            f.setBillNo(bill.getBillNo());
        }
        applyHcKsFlowNumericStrMirrors(f);
        fillHcKsFlowMaterialCodeName(f, f.getMaterialId(), entry != null ? entry.getMaterial() : null,
            entry != null ? entry.getMaterialName() : null, fdMaterialMapper, bill != null ? bill.getTenantId() : null);
    }

    public static void enrichHcCkFlowAfterInitial(HcCkFlow f, StkInitialImport main, StkInitialImportEntry entry,
        FdMaterialMapper fdMaterialMapper) {
        if (f == null) {
            return;
        }
        applyHcCkFlowWarehouseSnapshots(f, main != null ? main.getWarehouseId() : null,
            entry != null ? entry.getSupplierId() : null, null);
        if (main != null && StringUtils.isNotEmpty(main.getBillNo())) {
            f.setBillNo(main.getBillNo());
        }
        applyHcCkFlowNumericStrMirrors(f);
        String codeHint = entry != null ? entry.getMaterialCode() : null;
        fillHcCkFlowMaterialCodeName(f, entry != null ? entry.getMaterialId() : f.getMaterialId(), null, null, codeHint,
            fdMaterialMapper, main != null ? main.getTenantId() : null);
    }

    public static void enrichHcCkFlowAfterProfitLoss(HcCkFlow f, StkIoProfitLoss bill, StkIoProfitLossEntry entry,
        Long warehouseId, Long supplierId, Long departmentId, FdMaterialMapper fdMaterialMapper) {
        if (f == null) {
            return;
        }
        applyHcCkFlowWarehouseSnapshots(f, warehouseId, supplierId, departmentId);
        if (bill != null && StringUtils.isNotEmpty(bill.getBillNo())) {
            f.setBillNo(bill.getBillNo());
        }
        applyHcCkFlowNumericStrMirrors(f);
        String nameHint = entry != null ? entry.getMaterialNameSnap() : null;
        fillHcCkFlowMaterialCodeName(f, entry != null ? entry.getMaterialId() : f.getMaterialId(),
            entry != null ? entry.getMaterial() : null, nameHint, null, fdMaterialMapper, bill != null ? bill.getTenantId() : null);
    }

    public static void enrichHcKsFlowAfterProfitLoss(HcKsFlow f, StkIoProfitLoss bill, StkIoProfitLossEntry entry,
        Long warehouseId, FdMaterialMapper fdMaterialMapper) {
        if (f == null) {
            return;
        }
        applyHcKsFlowDeptWhSnapshots(f, warehouseId, f.getDepartmentId());
        if (bill != null && StringUtils.isNotEmpty(bill.getBillNo())) {
            f.setBillNo(bill.getBillNo());
        }
        applyHcKsFlowNumericStrMirrors(f);
        fillHcKsFlowMaterialCodeName(f, entry != null ? entry.getMaterialId() : f.getMaterialId(),
            entry != null ? entry.getMaterial() : null, entry != null ? entry.getMaterialNameSnap() : null, fdMaterialMapper,
            bill != null ? bill.getTenantId() : null);
    }

    /** 科室盘点审核直接改科室库存时写入 t_hc_ks_flow 的快照与单号 */
    public static void enrichHcKsFlowAfterDeptStocktaking(HcKsFlow f, StkIoStocktaking bill, StkIoStocktakingEntry entry,
        Long warehouseId, FdMaterialMapper fdMaterialMapper) {
        if (f == null) {
            return;
        }
        applyHcKsFlowDeptWhSnapshots(f, warehouseId, bill != null ? bill.getDepartmentId() : null);
        if (bill != null) {
            String stockNo = bill.getStockNo();
            if (stockNo != null && !stockNo.isEmpty()) {
                f.setBillNo(stockNo);
            }
        }
        applyHcKsFlowNumericStrMirrors(f);
        fillHcKsFlowMaterialCodeName(f, entry != null ? entry.getMaterialId() : f.getMaterialId(),
            entry != null ? entry.getMaterial() : null, null, fdMaterialMapper,
            bill != null ? bill.getTenantId() : null);
    }

    public static void enrichHcCkFlowBasApplyTransfer(HcCkFlow f, BasApply basApply, BasApplyEntry entry,
        Long warehouseId, Long supplierId, Long departmentId, FdMaterialMapper fdMaterialMapper) {
        if (f == null) {
            return;
        }
        applyHcCkFlowWarehouseSnapshots(f, warehouseId, supplierId, departmentId);
        if (basApply != null && StringUtils.isNotEmpty(basApply.getApplyBillNo())) {
            f.setBillNo(basApply.getApplyBillNo());
        }
        applyHcCkFlowNumericStrMirrors(f);
        fillHcCkFlowMaterialCodeName(f, entry != null ? entry.getMaterialId() : f.getMaterialId(),
            entry != null ? entry.getMaterial() : null, null, null, fdMaterialMapper,
            basApply != null ? basApply.getTenantId() : null);
    }

    public static void enrichHcKsFlowBasApply(HcKsFlow f, BasApply basApply, BasApplyEntry entry,
        FdMaterialMapper fdMaterialMapper) {
        if (f == null) {
            return;
        }
        applyHcKsFlowDeptWhSnapshots(f, f.getWarehouseId(), f.getDepartmentId());
        if (basApply != null && StringUtils.isNotEmpty(basApply.getApplyBillNo())) {
            f.setBillNo(basApply.getApplyBillNo());
        }
        applyHcKsFlowNumericStrMirrors(f);
        fillHcKsFlowMaterialCodeName(f, entry != null ? entry.getMaterialId() : f.getMaterialId(),
            entry != null ? entry.getMaterial() : null, null, fdMaterialMapper, basApply != null ? basApply.getTenantId() : null);
    }

    public static void enrichHcKsFlowDeptBatchConsume(HcKsFlow f, DeptBatchConsume bill, DeptBatchConsumeEntry entry,
        FdMaterialMapper fdMaterialMapper) {
        if (f == null) {
            return;
        }
        applyHcKsFlowDeptWhSnapshots(f, f.getWarehouseId(), f.getDepartmentId());
        if (bill != null && StringUtils.isNotEmpty(bill.getConsumeBillNo())) {
            f.setBillNo(bill.getConsumeBillNo());
        }
        applyHcKsFlowNumericStrMirrors(f);
        fillHcKsFlowMaterialCodeName(f, entry != null ? entry.getMaterialId() : f.getMaterialId(),
            entry != null ? entry.getMaterial() : null, entry != null ? entry.getMaterialName() : null, fdMaterialMapper,
            bill != null ? bill.getTenantId() : null);
    }
}

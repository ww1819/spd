package com.spd.hc.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.department.domain.DeptBatchConsume;
import com.spd.department.domain.DeptBatchConsumeEntry;
import com.spd.department.domain.StkDepInventory;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdFactory;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.domain.FdWarehouse;
import com.spd.foundation.mapper.FdDepartmentMapper;
import com.spd.foundation.mapper.FdFactoryMapper;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.foundation.mapper.FdSupplierMapper;
import com.spd.foundation.mapper.FdWarehouseMapper;
import com.spd.gz.domain.GzDepInventory;
import com.spd.gz.domain.GzDepotInventory;
import com.spd.gz.domain.GzDepFlow;
import com.spd.gz.domain.GzOrder;
import com.spd.gz.domain.GzOrderEntry;
import com.spd.gz.domain.GzOrderEntryInhospitalcodeList;
import com.spd.gz.domain.GzRefundGoods;
import com.spd.gz.domain.GzRefundGoodsEntry;
import com.spd.gz.domain.GzShipment;
import com.spd.gz.domain.GzShipmentEntry;
import com.spd.gz.domain.GzWhFlow;
import com.spd.gz.mapper.GzOrderMapper;
import com.spd.gz.mapper.SysSheetIdMapper;
import com.spd.hc.domain.HcBarcodeFlow;
import com.spd.hc.domain.HcBarcodeMaster;
import com.spd.hc.domain.StkLvIoInhospitalBarcode;
import com.spd.hc.mapper.HcBarcodeTraceMapper;
import com.spd.hc.service.IHcBarcodeLifecycleService;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.domain.StkIoBillEntry;
import com.spd.warehouse.domain.StkInventory;
import com.spd.warehouse.mapper.StkInventoryMapper;
import com.spd.warehouse.mapper.StkIoBillMapper;

@Service
public class HcBarcodeLifecycleServiceImpl implements IHcBarcodeLifecycleService {

    private static final Logger log = LoggerFactory.getLogger(HcBarcodeLifecycleServiceImpl.class);

    private static final String LV_SHEET_BT = "低值";
    private static final String LV_SHEET_ST = "lvynm";

    @Autowired
    private HcBarcodeTraceMapper hcBarcodeTraceMapper;
    @Autowired
    private SysSheetIdMapper sysSheetIdMapper;
    @Autowired
    private FdMaterialMapper fdMaterialMapper;
    @Autowired
    private FdSupplierMapper fdSupplierMapper;
    @Autowired
    private FdDepartmentMapper fdDepartmentMapper;
    @Autowired
    private FdFactoryMapper fdFactoryMapper;
    @Autowired
    private StkIoBillMapper stkIoBillMapper;
    @Autowired
    private StkInventoryMapper stkInventoryMapper;
    @Autowired
    private GzOrderMapper gzOrderMapper;
    @Autowired
    private com.spd.department.mapper.StkDepInventoryMapper stkDepInventoryMapper;
    @Autowired
    private FdWarehouseMapper fdWarehouseMapper;

    private String tenantOf(StkIoBill bill) {
        return StringUtils.isNotEmpty(bill.getTenantId()) ? bill.getTenantId() : SecurityUtils.getCustomerId();
    }

    private String tenantOf(GzOrder order) {
        return StringUtils.isNotEmpty(order.getTenantId()) ? order.getTenantId() : SecurityUtils.getCustomerId();
    }

    private String uid() {
        return SecurityUtils.getUserIdStr();
    }

    private boolean genIn(FdWarehouse w) {
        return w != null && w.getLvAuditGenInhospitalIn() != null && w.getLvAuditGenInhospitalIn() == 1;
    }

    private boolean genOut(FdWarehouse w) {
        return w != null && w.getLvAuditGenInhospitalOut() != null && w.getLvAuditGenInhospitalOut() == 1;
    }

    private BigDecimal resolvePackageUnit(FdMaterial m) {
        if (m == null || StringUtils.isEmpty(m.getPackageSpeci())) {
            return BigDecimal.ONE;
        }
        String s = m.getPackageSpeci().trim();
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                digits.append(c);
            } else if (digits.length() > 0) {
                break;
            }
        }
        if (digits.length() == 0) {
            return BigDecimal.ONE;
        }
        try {
            BigDecimal v = new BigDecimal(digits.toString());
            return v.compareTo(BigDecimal.ZERO) > 0 ? v : BigDecimal.ONE;
        } catch (Exception e) {
            return BigDecimal.ONE;
        }
    }

    private int packageCount(BigDecimal totalQty, BigDecimal unit) {
        if (totalQty == null || totalQty.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        BigDecimal u = unit != null && unit.compareTo(BigDecimal.ZERO) > 0 ? unit : BigDecimal.ONE;
        return totalQty.divide(u, 0, RoundingMode.CEILING).intValue();
    }

    private String nextLvInHospitalCode() {
        String bt = LV_SHEET_BT;
        String st = LV_SHEET_ST;
        if (sysSheetIdMapper.countSheetId(bt, st) == 0) {
            sysSheetIdMapper.insertSheetId(bt, st, 0L);
        }
        Long cur = sysSheetIdMapper.selectSheetId(bt, st);
        if (cur == null) {
            cur = 0L;
        }
        long next = cur + 1;
        sysSheetIdMapper.updateSheetId(bt, st, next);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        String dateStr = sdf.format(new Date());
        if (dateStr.length() > 10) {
            dateStr = dateStr.substring(dateStr.length() - 10);
        }
        String seqStr = String.valueOf(next + 1000000);
        if (seqStr.length() > 6) {
            seqStr = seqStr.substring(seqStr.length() - 6);
        }
        return "L" + dateStr + seqStr;
    }

    private String joinCodes(List<String> codes, int maxLen) {
        if (codes == null || codes.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String c : codes) {
            if (StringUtils.isEmpty(c)) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(c);
            if (sb.length() >= maxLen) {
                break;
            }
        }
        String s = sb.toString();
        return s.length() > maxLen ? s.substring(0, maxLen) : s;
    }

    private void insertMasterAndFlow(HcBarcodeMaster master, String eventCode, String eventName,
        String billDomain, String billId, String billNo, String billEntryId,
        String fromWh, String toWh, String fromDept, String toDept, BigDecimal qty) {
        hcBarcodeTraceMapper.insertHcBarcodeMaster(master);
        HcBarcodeFlow flow = new HcBarcodeFlow();
        flow.setId(UUID7.generateUUID7());
        flow.setTenantId(master.getTenantId());
        flow.setHcBarcodeMasterId(master.getId());
        flow.setBarcodeValue(master.getBarcodeValue());
        flow.setValueLevel(master.getValueLevel());
        flow.setSeqNo(1);
        flow.setEventCode(eventCode);
        flow.setEventName(eventName);
        flow.setEventTime(new Date());
        flow.setBillDomain(billDomain);
        flow.setBillId(billId);
        flow.setBillNo(billNo);
        flow.setBillEntryId(billEntryId);
        flow.setFromWarehouseId(fromWh);
        flow.setToWarehouseId(toWh);
        flow.setFromDepartmentId(fromDept);
        flow.setToDepartmentId(toDept);
        flow.setQty(qty);
        flow.setMaterialName(master.getMaterialName());
        flow.setMaterialSpeci(master.getMaterialSpeci());
        flow.setMaterialModel(master.getMaterialModel());
        flow.setOperatorId(uid());
        flow.setOperatorName(uid());
        flow.setRemark(null);
        flow.setDelFlag(0);
        flow.setCreateBy(uid());
        flow.setCreateTime(new Date());
        hcBarcodeTraceMapper.insertHcBarcodeFlow(flow);
    }

    /**
     * 追加流水后回写主档当前态（与 hc_barcode_flow 语义一致）
     */
    private void syncMasterAfterAppendFlow(HcBarcodeMaster master, String eventCode,
        String fromWh, String toWh, String fromDept, String toDept) {
        if (master == null || StringUtils.isEmpty(master.getId()) || StringUtils.isEmpty(master.getTenantId())) {
            return;
        }
        HcBarcodeMaster patch = new HcBarcodeMaster();
        patch.setId(master.getId());
        patch.setTenantId(master.getTenantId());
        patch.setUpdateBy(uid());
        if ("GZ_SHIP_OUT".equals(eventCode)) {
            patch.setCurrentHolderType("DEPT");
            patch.setCurrentDepartmentId(toDept);
            patch.setCurrentWarehouseId(null);
            patch.setStatus("ACTIVE");
        } else if ("GZ_REFUND_TK".equals(eventCode)) {
            patch.setCurrentHolderType("WH");
            patch.setCurrentWarehouseId(StringUtils.isNotEmpty(toWh) ? toWh : null);
            patch.setCurrentDepartmentId(null);
            patch.setStatus("ACTIVE");
        } else if ("GZ_REFUND_TH".equals(eventCode)) {
            patch.setCurrentHolderType("UNKNOWN");
            patch.setCurrentWarehouseId(null);
            patch.setCurrentDepartmentId(null);
            patch.setStatus("CONSUMED");
        } else {
            return;
        }
        hcBarcodeTraceMapper.updateHcBarcodeMasterCurrentState(patch);
    }

    private void appendFlowEvent(HcBarcodeMaster master, String eventCode, String eventName,
        String billDomain, String billId, String billNo, String billEntryId,
        String fromWh, String toWh, String fromDept, String toDept, BigDecimal qty) {
        Integer seq = hcBarcodeTraceMapper.selectNextFlowSeq(master.getTenantId(), master.getId());
        HcBarcodeFlow flow = new HcBarcodeFlow();
        flow.setId(UUID7.generateUUID7());
        flow.setTenantId(master.getTenantId());
        flow.setHcBarcodeMasterId(master.getId());
        flow.setBarcodeValue(master.getBarcodeValue());
        flow.setValueLevel(master.getValueLevel());
        flow.setSeqNo(seq != null ? seq : 1);
        flow.setEventCode(eventCode);
        flow.setEventName(eventName);
        flow.setEventTime(new Date());
        flow.setBillDomain(billDomain);
        flow.setBillId(billId);
        flow.setBillNo(billNo);
        flow.setBillEntryId(billEntryId);
        flow.setFromWarehouseId(fromWh);
        flow.setToWarehouseId(toWh);
        flow.setFromDepartmentId(fromDept);
        flow.setToDepartmentId(toDept);
        flow.setQty(qty);
        flow.setMaterialName(master.getMaterialName());
        flow.setMaterialSpeci(master.getMaterialSpeci());
        flow.setMaterialModel(master.getMaterialModel());
        flow.setOperatorId(uid());
        flow.setOperatorName(uid());
        flow.setDelFlag(0);
        flow.setCreateBy(uid());
        flow.setCreateTime(new Date());
        hcBarcodeTraceMapper.insertHcBarcodeFlow(flow);
        syncMasterAfterAppendFlow(master, eventCode, fromWh, toWh, fromDept, toDept);
    }

    @Override
    public void onLowValueInbound101(StkIoBill bill, StkIoBillEntry entry, StkInventory insertedInventory, FdWarehouse warehouse) {
        if (!genIn(warehouse) || bill == null || entry == null || insertedInventory == null || insertedInventory.getId() == null) {
            return;
        }
        try {
            FdMaterial m = entry.getMaterialId() != null ? fdMaterialMapper.selectFdMaterialById(entry.getMaterialId()) : null;
            BigDecimal unit = resolvePackageUnit(m);
            int n = packageCount(entry.getQty(), unit);
            if (n <= 0) {
                return;
            }
            String tenantId = tenantOf(bill);
            Date auditTime = new Date();
            List<String> codes = new ArrayList<>();
            BigDecimal remaining = entry.getQty() != null ? entry.getQty() : BigDecimal.ZERO;
            for (int i = 1; i <= n; i++) {
                BigDecimal pkgQty = remaining.min(unit);
                remaining = remaining.subtract(pkgQty);
                String code = nextLvInHospitalCode();
                codes.add(code);
                String masterId = UUID7.generateUUID7();
                HcBarcodeMaster master = buildLvMasterInbound(bill, entry, m, tenantId, masterId, code, pkgUnitForMaster(pkgQty), warehouse);
                master.setSourceTable("stk_lv_io_inhospital_barcode");
                insertMasterAndFlow(master, "LV_IN_AUDIT", "低值入库审核生成定数包", "STK_IO_BILL",
                    String.valueOf(bill.getId()), bill.getBillNo(), entry.getId() != null ? String.valueOf(entry.getId()) : null,
                    null, str(warehouse != null ? warehouse.getId() : bill.getWarehouseId()), null, null, pkgQty);
                StkLvIoInhospitalBarcode row = buildLvIoRow(bill, entry, m, tenantId, 1, masterId, code, pkgQty, i, n,
                    String.valueOf(insertedInventory.getId()), null, auditTime, warehouse);
                row.setFixedPackageBarcode(code);
                hcBarcodeTraceMapper.insertStkLvIoInhospitalBarcode(row);
                if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }
            }
            String joined = joinCodes(codes, 200);
            StkIoBillEntry updE = new StkIoBillEntry();
            updE.setId(entry.getId());
            updE.setFixedPackageBarcode(joined);
            stkIoBillMapper.updateStkIoBillEntryById(updE);
            StkInventory invU = new StkInventory();
            invU.setId(insertedInventory.getId());
            invU.setTenantId(tenantId);
            invU.setLvInhospitalPackageCode(codes.isEmpty() ? null : codes.get(0));
            stkInventoryMapper.updateStkInventory(invU);
        } catch (Exception e) {
            log.warn("onLowValueInbound101 barcode lifecycle failed billId={}", bill.getId(), e);
        }
    }

    private BigDecimal pkgUnitForMaster(BigDecimal pkgQty) {
        return pkgQty;
    }

    private HcBarcodeMaster buildLvMasterInbound(StkIoBill bill, StkIoBillEntry entry, FdMaterial m, String tenantId,
        String masterId, String code, BigDecimal fixedPkgQty, FdWarehouse wh) {
        HcBarcodeMaster master = new HcBarcodeMaster();
        master.setId(masterId);
        master.setTenantId(tenantId);
        master.setBarcodeValue(code);
        master.setValueLevel("2");
        master.setBusinessTypeCode("LV_STK_IN_AUDIT");
        master.setBusinessTypeName("低值入库审核定数包");
        master.setBillDomain("STK_IO_BILL");
        master.setBillId(String.valueOf(bill.getId()));
        master.setBillNo(bill.getBillNo());
        master.setBillType(bill.getBillType() != null ? String.valueOf(bill.getBillType()) : null);
        master.setBillEntryId(entry.getId() != null ? String.valueOf(entry.getId()) : null);
        master.setMaterialId(entry.getMaterialId() != null ? String.valueOf(entry.getMaterialId()) : null);
        master.setMaterialCode(m != null ? m.getCode() : null);
        master.setMaterialName(entry.getMaterialName());
        master.setMaterialSpeci(entry.getMaterialSpeci());
        master.setMaterialModel(entry.getMaterialModel());
        master.setMaterialUnitName(m != null && m.getFdUnit() != null ? m.getFdUnit().getUnitName() : null);
        master.setFactoryId(entry.getMaterialFactoryId() != null ? String.valueOf(entry.getMaterialFactoryId()) : null);
        master.setFactoryName(resolveFactoryName(entry.getMaterialFactoryId()));
        master.setSupplierId(bill.getSupplerId() != null ? String.valueOf(bill.getSupplerId()) : null);
        master.setSupplierName(resolveSupplierName(bill.getSupplerId()));
        master.setWarehouseId(bill.getWarehouseId() != null ? String.valueOf(bill.getWarehouseId()) : null);
        master.setWarehouseName(wh != null ? wh.getName() : null);
        master.setDepartmentId(null);
        master.setDepartmentName(null);
        master.setBatchNo(entry.getBatchNo());
        master.setBatchNumber(entry.getBatchNumber());
        master.setBeginTime(entry.getBeginTime());
        master.setEndTime(entry.getEndTime());
        master.setMasterBarcode(entry.getMainBarcode());
        master.setSecondaryBarcode(entry.getSubBarcode());
        master.setFixedPackageQty(fixedPkgQty);
        master.setCurrentHolderType("WH");
        master.setCurrentWarehouseId(bill.getWarehouseId() != null ? String.valueOf(bill.getWarehouseId()) : null);
        master.setCurrentDepartmentId(null);
        master.setStatus("ACTIVE");
        master.setDelFlag(0);
        master.setCreateBy(uid());
        master.setCreateTime(new Date());
        return master;
    }

    private StkLvIoInhospitalBarcode buildLvIoRow(StkIoBill bill, StkIoBillEntry entry, FdMaterial m, String tenantId,
        int ioDir, String masterId, String code, BigDecimal pkgQty, int idx, int total,
        String stkInvId, String depInvId, Date auditTime, FdWarehouse wh) {
        StkLvIoInhospitalBarcode row = new StkLvIoInhospitalBarcode();
        row.setId(UUID7.generateUUID7());
        row.setTenantId(tenantId);
        row.setIoDirection(ioDir);
        row.setStkIoBillId(String.valueOf(bill.getId()));
        row.setStkIoBillNo(bill.getBillNo());
        row.setStkIoBillType(bill.getBillType());
        row.setStkIoBillEntryId(entry.getId() != null ? String.valueOf(entry.getId()) : null);
        row.setWarehouseId(bill.getWarehouseId() != null ? String.valueOf(bill.getWarehouseId()) : null);
        row.setWarehouseCode(wh != null ? wh.getCode() : null);
        row.setWarehouseName(wh != null ? wh.getName() : null);
        row.setDepartmentId(bill.getDepartmentId() != null ? String.valueOf(bill.getDepartmentId()) : null);
        FdDepartment dep = bill.getDepartmentId() != null ? fdDepartmentMapper.selectFdDepartmentById(String.valueOf(bill.getDepartmentId())) : null;
        row.setDepartmentCode(dep != null ? dep.getCode() : null);
        row.setDepartmentName(dep != null ? dep.getName() : null);
        row.setMaterialId(entry.getMaterialId() != null ? String.valueOf(entry.getMaterialId()) : null);
        row.setMaterialCode(m != null ? m.getCode() : null);
        row.setMaterialName(entry.getMaterialName());
        row.setMaterialSpeci(entry.getMaterialSpeci());
        row.setMaterialModel(entry.getMaterialModel());
        row.setMaterialUnitName(m != null && m.getFdUnit() != null ? m.getFdUnit().getUnitName() : null);
        row.setFactoryId(entry.getMaterialFactoryId() != null ? String.valueOf(entry.getMaterialFactoryId()) : null);
        row.setFactoryName(resolveFactoryName(entry.getMaterialFactoryId()));
        row.setSupplierId(bill.getSupplerId() != null ? String.valueOf(bill.getSupplerId()) : null);
        row.setSupplierName(resolveSupplierName(bill.getSupplerId()));
        row.setBatchNo(entry.getBatchNo());
        row.setBatchNumber(entry.getBatchNumber());
        row.setBeginTime(entry.getBeginTime());
        row.setEndTime(entry.getEndTime());
        row.setUnitPrice(entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice());
        row.setQtyInPackage(pkgQty);
        row.setPackageIndex(idx);
        row.setPackageTotal(total);
        row.setInHospitalCode(code);
        row.setFixedPackageBarcode(code);
        row.setStkInventoryId(stkInvId);
        row.setStkDepInventoryId(depInvId);
        row.setHcBarcodeMasterId(masterId);
        row.setAuditBy(uid());
        row.setAuditTime(auditTime);
        row.setDelFlag(0);
        row.setCreateBy(uid());
        row.setCreateTime(new Date());
        return row;
    }

    private String str(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private String resolveSupplierName(Long supplierId) {
        if (supplierId == null) {
            return null;
        }
        FdSupplier s = fdSupplierMapper.selectFdSupplierById(supplierId);
        return s != null ? s.getName() : null;
    }

    private String resolveFactoryName(Long factoryId) {
        if (factoryId == null) {
            return null;
        }
        FdFactory f = fdFactoryMapper.selectFdFactoryByFactoryId(factoryId);
        return f != null ? f.getFactoryName() : null;
    }

    @Override
    public void onLowValueOutbound201(StkIoBill bill, StkIoBillEntry entry, StkInventory warehouseInventory,
        StkDepInventory depInventory, FdWarehouse warehouse) {
        if (!genOut(warehouse) || bill == null || entry == null || depInventory == null || depInventory.getId() == null) {
            return;
        }
        try {
            FdMaterial m = entry.getMaterialId() != null ? fdMaterialMapper.selectFdMaterialById(entry.getMaterialId()) : null;
            BigDecimal unit = resolvePackageUnit(m);
            int n = packageCount(entry.getQty(), unit);
            if (n <= 0) {
                return;
            }
            String tenantId = tenantOf(bill);
            Date auditTime = new Date();
            List<String> codes = new ArrayList<>();
            BigDecimal remaining = entry.getQty() != null ? entry.getQty() : BigDecimal.ZERO;
            for (int i = 1; i <= n; i++) {
                BigDecimal pkgQty = remaining.min(unit);
                remaining = remaining.subtract(pkgQty);
                String code = nextLvInHospitalCode();
                codes.add(code);
                String masterId = UUID7.generateUUID7();
                HcBarcodeMaster master = buildLvMasterOutbound(bill, entry, m, tenantId, masterId, code, pkgQty, warehouse);
                master.setSourceTable("stk_lv_io_inhospital_barcode");
                insertMasterAndFlow(master, "LV_OUT_AUDIT", "低值出库审核生成定数包", "STK_IO_BILL",
                    String.valueOf(bill.getId()), bill.getBillNo(), entry.getId() != null ? String.valueOf(entry.getId()) : null,
                    str(bill.getWarehouseId()), null, null, str(bill.getDepartmentId()), pkgQty);
                StkLvIoInhospitalBarcode row = buildLvIoRow(bill, entry, m, tenantId, 2, masterId, code, pkgQty, i, n,
                    warehouseInventory != null ? String.valueOf(warehouseInventory.getId()) : null,
                    String.valueOf(depInventory.getId()), auditTime, warehouse);
                row.setFixedPackageBarcode(code);
                hcBarcodeTraceMapper.insertStkLvIoInhospitalBarcode(row);
                if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }
            }
            String joined = joinCodes(codes, 200);
            StkIoBillEntry updE = new StkIoBillEntry();
            updE.setId(entry.getId());
            updE.setFixedPackageBarcode(joined);
            stkIoBillMapper.updateStkIoBillEntryById(updE);
            updateDepLvCode(depInventory.getId(), tenantId, codes.isEmpty() ? null : codes.get(0));
        } catch (Exception e) {
            log.warn("onLowValueOutbound201 barcode lifecycle failed billId={}", bill.getId(), e);
        }
    }

    private HcBarcodeMaster buildLvMasterOutbound(StkIoBill bill, StkIoBillEntry entry, FdMaterial m, String tenantId,
        String masterId, String code, BigDecimal fixedPkgQty, FdWarehouse wh) {
        HcBarcodeMaster master = buildLvMasterInbound(bill, entry, m, tenantId, masterId, code, fixedPkgQty, wh);
        master.setBusinessTypeCode("LV_STK_OUT_AUDIT");
        master.setBusinessTypeName("低值出库审核定数包");
        master.setCurrentHolderType("DEPT");
        master.setCurrentWarehouseId(null);
        master.setCurrentDepartmentId(bill.getDepartmentId() != null ? String.valueOf(bill.getDepartmentId()) : null);
        return master;
    }

    private void updateDepLvCode(Long depId, String tenantId, String firstCode) {
        if (depId == null) {
            return;
        }
        StkDepInventory depU = new StkDepInventory();
        depU.setId(depId);
        depU.setTenantId(tenantId);
        depU.setLvInhospitalPackageCode(firstCode);
        stkDepInventoryMapper.updateStkDepInventory(depU);
    }

    @Override
    public void onLowValueReturn301(StkIoBill bill, StkIoBillEntry entry, StkInventory inventory, FdWarehouse warehouse) {
        if (!genOut(warehouse) || bill == null || entry == null) {
            return;
        }
        try {
            FdMaterial m = entry.getMaterialId() != null ? fdMaterialMapper.selectFdMaterialById(entry.getMaterialId()) : null;
            BigDecimal unit = resolvePackageUnit(m);
            int n = packageCount(entry.getQty(), unit);
            if (n <= 0) {
                return;
            }
            String tenantId = tenantOf(bill);
            Date auditTime = new Date();
            List<String> codes = new ArrayList<>();
            BigDecimal remaining = entry.getQty() != null ? entry.getQty() : BigDecimal.ZERO;
            for (int i = 1; i <= n; i++) {
                BigDecimal pkgQty = remaining.min(unit);
                remaining = remaining.subtract(pkgQty);
                String code = nextLvInHospitalCode();
                codes.add(code);
                String masterId = UUID7.generateUUID7();
                HcBarcodeMaster master = buildLvMasterInbound(bill, entry, m, tenantId, masterId, code, pkgQty, warehouse);
                master.setBusinessTypeCode("LV_STK_TH_AUDIT");
                master.setBusinessTypeName("低值退货出库(至供应商)");
                master.setSourceTable("stk_lv_io_inhospital_barcode");
                insertMasterAndFlow(master, "LV_TH_AUDIT", "低值退货审核", "STK_IO_BILL",
                    String.valueOf(bill.getId()), bill.getBillNo(), entry.getId() != null ? String.valueOf(entry.getId()) : null,
                    str(bill.getWarehouseId()), null, null, null, pkgQty);
                StkLvIoInhospitalBarcode row = buildLvIoRow(bill, entry, m, tenantId, 2, masterId, code, pkgQty, i, n,
                    inventory != null ? String.valueOf(inventory.getId()) : null, null, auditTime, warehouse);
                row.setFixedPackageBarcode(code);
                hcBarcodeTraceMapper.insertStkLvIoInhospitalBarcode(row);
                if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }
            }
            StkIoBillEntry updE = new StkIoBillEntry();
            updE.setId(entry.getId());
            updE.setFixedPackageBarcode(joinCodes(codes, 200));
            stkIoBillMapper.updateStkIoBillEntryById(updE);
        } catch (Exception e) {
            log.warn("onLowValueReturn301 barcode lifecycle failed billId={}", bill.getId(), e);
        }
    }

    @Override
    public void onLowValueTk401(StkIoBill bill, StkIoBillEntry entry, StkInventory warehouseInventory,
        StkDepInventory depInventory, FdWarehouse warehouse) {
        if (!genIn(warehouse) || bill == null || entry == null || warehouseInventory == null) {
            return;
        }
        try {
            FdMaterial m = entry.getMaterialId() != null ? fdMaterialMapper.selectFdMaterialById(entry.getMaterialId()) : null;
            BigDecimal unit = resolvePackageUnit(m);
            int n = packageCount(entry.getQty(), unit);
            if (n <= 0) {
                return;
            }
            String tenantId = tenantOf(bill);
            Date auditTime = new Date();
            List<String> codes = new ArrayList<>();
            BigDecimal remaining = entry.getQty() != null ? entry.getQty() : BigDecimal.ZERO;
            for (int i = 1; i <= n; i++) {
                BigDecimal pkgQty = remaining.min(unit);
                remaining = remaining.subtract(pkgQty);
                String code = nextLvInHospitalCode();
                codes.add(code);
                String masterId = UUID7.generateUUID7();
                HcBarcodeMaster master = buildLvMasterInbound(bill, entry, m, tenantId, masterId, code, pkgQty, warehouse);
                master.setBusinessTypeCode("LV_STK_TK_AUDIT");
                master.setBusinessTypeName("低值退库入仓");
                master.setSourceTable("stk_lv_io_inhospital_barcode");
                insertMasterAndFlow(master, "LV_TK_AUDIT", "低值退库审核", "STK_IO_BILL",
                    String.valueOf(bill.getId()), bill.getBillNo(), entry.getId() != null ? String.valueOf(entry.getId()) : null,
                    null, str(entry.getWarehouseId() != null ? entry.getWarehouseId() : bill.getWarehouseId()), null, null, pkgQty);
                StkLvIoInhospitalBarcode row = buildLvIoRow(bill, entry, m, tenantId, 1, masterId, code, pkgQty, i, n,
                    String.valueOf(warehouseInventory.getId()),
                    depInventory != null && depInventory.getId() != null ? String.valueOf(depInventory.getId()) : null,
                    auditTime, warehouse);
                row.setFixedPackageBarcode(code);
                hcBarcodeTraceMapper.insertStkLvIoInhospitalBarcode(row);
                if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }
            }
            StkIoBillEntry updE = new StkIoBillEntry();
            updE.setId(entry.getId());
            updE.setFixedPackageBarcode(joinCodes(codes, 200));
            stkIoBillMapper.updateStkIoBillEntryById(updE);
            StkInventory invU = new StkInventory();
            invU.setId(warehouseInventory.getId());
            invU.setTenantId(tenantId);
            invU.setLvInhospitalPackageCode(codes.isEmpty() ? null : codes.get(0));
            stkInventoryMapper.updateStkInventory(invU);
        } catch (Exception e) {
            log.warn("onLowValueTk401 barcode lifecycle failed billId={}", bill.getId(), e);
        }
    }

    @Override
    public void fillGzOrderInhospitalcodeSnapshots(GzOrder order, GzOrderEntry orderEntry, GzOrderEntryInhospitalcodeList row) {
        if (order == null || orderEntry == null || row == null) {
            return;
        }
        FdMaterial m = orderEntry.getMaterialId() != null ? fdMaterialMapper.selectFdMaterialById(orderEntry.getMaterialId()) : null;
        if (m != null) {
            row.setMaterialName(m.getName());
            row.setMaterialSpeci(m.getSpeci());
            row.setMaterialModel(m.getModel());
            if (m.getFdUnit() != null) {
                row.setMaterialUnitName(m.getFdUnit().getUnitName());
            }
            row.setFactoryId(m.getFactoryId());
            row.setFactoryName(resolveFactoryName(m.getFactoryId()));
            row.setRegisterNo(m.getRegisterNo());
        }
        if (order.getWarehouseId() != null) {
            FdWarehouse w = fdWarehouseMapper.selectFdWarehouseById(String.valueOf(order.getWarehouseId()));
            if (w != null) {
                row.setWarehouseName(w.getName());
            }
        }
        if (order.getSupplerId() != null) {
            row.setSupplierName(resolveSupplierName(order.getSupplerId()));
        }
    }

    @Override
    public void onGzPrepAcceptUnit(GzOrder order, GzOrderEntry orderEntry, GzOrderEntryInhospitalcodeList inhospitalRow, GzDepotInventory depotRow) {
        if (order == null || inhospitalRow == null || StringUtils.isEmpty(inhospitalRow.getInHospitalCode()) || inhospitalRow.getId() == null) {
            return;
        }
        try {
            String tenantId = tenantOf(order);
            String masterId = UUID7.generateUUID7();
            HcBarcodeMaster master = new HcBarcodeMaster();
            master.setId(masterId);
            master.setTenantId(tenantId);
            master.setBarcodeValue(inhospitalRow.getInHospitalCode());
            master.setValueLevel("1");
            master.setBusinessTypeCode("GZ_ORDER_PREP_ACCEPT");
            master.setBusinessTypeName("高值备货验收入库");
            master.setSourceTable("gz_order_entry_inhospitalcode_list");
            master.setSourceRowId(String.valueOf(inhospitalRow.getId()));
            master.setBillDomain("GZ_ORDER");
            master.setBillId(String.valueOf(order.getId()));
            master.setBillNo(order.getOrderNo());
            master.setBillType("GZ_ORDER");
            master.setBillEntryId(orderEntry != null && orderEntry.getId() != null ? String.valueOf(orderEntry.getId()) : null);
            master.setMaterialId(orderEntry != null && orderEntry.getMaterialId() != null ? String.valueOf(orderEntry.getMaterialId()) : null);
            FdMaterial mm = orderEntry != null && orderEntry.getMaterialId() != null ? fdMaterialMapper.selectFdMaterialById(orderEntry.getMaterialId()) : null;
            master.setMaterialCode(mm != null ? mm.getCode() : null);
            master.setMaterialName(inhospitalRow.getMaterialName());
            master.setMaterialSpeci(inhospitalRow.getMaterialSpeci());
            master.setMaterialModel(inhospitalRow.getMaterialModel());
            master.setMaterialUnitName(inhospitalRow.getMaterialUnitName());
            master.setFactoryId(inhospitalRow.getFactoryId() != null ? String.valueOf(inhospitalRow.getFactoryId()) : null);
            master.setFactoryName(inhospitalRow.getFactoryName());
            master.setSupplierId(order.getSupplerId() != null ? String.valueOf(order.getSupplerId()) : null);
            master.setSupplierName(inhospitalRow.getSupplierName());
            master.setWarehouseId(order.getWarehouseId() != null ? String.valueOf(order.getWarehouseId()) : null);
            master.setWarehouseName(inhospitalRow.getWarehouseName());
            master.setBatchNo(inhospitalRow.getBatchNo());
            master.setBatchNumber(inhospitalRow.getBatchNumber());
            master.setBeginTime(orderEntry != null ? orderEntry.getBeginTime() : null);
            master.setEndTime(inhospitalRow.getEndDate());
            master.setMasterBarcode(inhospitalRow.getMasterBarcode());
            master.setSecondaryBarcode(inhospitalRow.getSecondaryBarcode());
            master.setFixedPackageQty(BigDecimal.ONE);
            master.setCurrentHolderType("WH");
            master.setCurrentWarehouseId(order.getWarehouseId() != null ? String.valueOf(order.getWarehouseId()) : null);
            master.setCurrentDepartmentId(null);
            master.setStatus("ACTIVE");
            master.setDelFlag(0);
            master.setCreateBy(uid());
            master.setCreateTime(new Date());
            insertMasterAndFlow(master, "GZ_PREP_IN", "高值备货验收入库", "GZ_ORDER",
                String.valueOf(order.getId()), order.getOrderNo(),
                orderEntry != null && orderEntry.getId() != null ? String.valueOf(orderEntry.getId()) : null,
                null, str(order.getWarehouseId()), null, null, BigDecimal.ONE);

            GzWhFlow wf = new GzWhFlow();
            wf.setId(UUID7.generateUUID7());
            wf.setTenantId(tenantId);
            wf.setBillId(String.valueOf(order.getId()));
            wf.setBillNo(order.getOrderNo());
            wf.setEntryId(orderEntry != null && orderEntry.getId() != null ? String.valueOf(orderEntry.getId()) : null);
            wf.setWarehouseId(order.getWarehouseId() != null ? String.valueOf(order.getWarehouseId()) : null);
            wf.setWarehouseName(inhospitalRow.getWarehouseName());
            wf.setMaterialId(orderEntry != null && orderEntry.getMaterialId() != null ? String.valueOf(orderEntry.getMaterialId()) : null);
            wf.setMaterialName(inhospitalRow.getMaterialName());
            wf.setMaterialSpeci(inhospitalRow.getMaterialSpeci());
            wf.setMaterialModel(inhospitalRow.getMaterialModel());
            wf.setBatchNo(inhospitalRow.getBatchNo());
            wf.setBatchNumber(inhospitalRow.getBatchNumber());
            wf.setQty(BigDecimal.ONE);
            wf.setUnitPrice(inhospitalRow.getPrice());
            wf.setAmt(inhospitalRow.getPrice());
            wf.setBeginTime(orderEntry != null ? orderEntry.getBeginTime() : null);
            wf.setEndTime(inhospitalRow.getEndDate());
            wf.setSupplierId(order.getSupplerId() != null ? String.valueOf(order.getSupplerId()) : null);
            wf.setSupplierName(inhospitalRow.getSupplierName());
            wf.setFactoryId(inhospitalRow.getFactoryId() != null ? String.valueOf(inhospitalRow.getFactoryId()) : null);
            wf.setFactoryName(inhospitalRow.getFactoryName());
            wf.setGzDepotInventoryId(depotRow != null && depotRow.getId() != null ? String.valueOf(depotRow.getId()) : null);
            wf.setInHospitalCode(inhospitalRow.getInHospitalCode());
            wf.setMasterBarcode(inhospitalRow.getMasterBarcode());
            wf.setSecondaryBarcode(inhospitalRow.getSecondaryBarcode());
            wf.setLx("RK");
            wf.setFlowTime(new Date());
            wf.setOriginBusinessType("高值备货验收入库");
            wf.setDelFlag(0);
            wf.setCreateBy(uid());
            wf.setCreateTime(new Date());
            hcBarcodeTraceMapper.insertGzWhFlow(wf);

            GzOrderEntryInhospitalcodeList up = new GzOrderEntryInhospitalcodeList();
            up.setId(inhospitalRow.getId());
            up.setHcBarcodeMasterId(masterId);
            up.setUpdateBy(uid());
            gzOrderMapper.updateGzOrderEntryInhospitalcodeListSnapshots(up);
        } catch (Exception e) {
            log.warn("onGzPrepAcceptUnit failed orderId={}", order.getId(), e);
        }
    }

    @Override
    public void onGzShipmentDepInventoryInserted(GzShipment shipment, GzShipmentEntry shipmentEntry,
        GzDepInventory depInventory, GzDepotInventory depotSource) {
        if (shipment == null || shipmentEntry == null || StringUtils.isEmpty(shipmentEntry.getInHospitalCode())) {
            return;
        }
        try {
            String tenantId = StringUtils.isNotEmpty(shipment.getTenantId()) ? shipment.getTenantId() : SecurityUtils.getCustomerId();
            HcBarcodeMaster master = hcBarcodeTraceMapper.selectHcBarcodeMasterByTenantAndBarcode(
                tenantId, shipmentEntry.getInHospitalCode().trim());
            if (master == null) {
                return;
            }
            appendFlowEvent(master, "GZ_SHIP_OUT", "高值备货出库至科室", "GZ_SHIPMENT",
                shipment.getId() != null ? String.valueOf(shipment.getId()) : null, shipment.getShipmentNo(),
                shipmentEntry.getId() != null ? String.valueOf(shipmentEntry.getId()) : null,
                str(shipment.getWarehouseId()), null, null, str(shipment.getDepartmentId()),
                shipmentEntry.getQty());

            GzDepFlow df = new GzDepFlow();
            df.setId(UUID7.generateUUID7());
            df.setTenantId(tenantId);
            df.setBillId(shipment.getId() != null ? String.valueOf(shipment.getId()) : null);
            df.setBillNo(shipment.getShipmentNo());
            df.setEntryId(shipmentEntry.getId() != null ? String.valueOf(shipmentEntry.getId()) : null);
            df.setDepartmentId(shipment.getDepartmentId() != null ? String.valueOf(shipment.getDepartmentId()) : null);
            df.setDepartmentName(resolveDepartmentName(shipment.getDepartmentId()));
            df.setWarehouseId(shipment.getWarehouseId() != null ? String.valueOf(shipment.getWarehouseId()) : null);
            df.setMaterialId(shipmentEntry.getMaterialId() != null ? String.valueOf(shipmentEntry.getMaterialId()) : null);
            df.setBatchNo(shipmentEntry.getBatchNo());
            df.setBatchNumber(shipmentEntry.getBatchNumber());
            df.setQty(shipmentEntry.getQty());
            df.setUnitPrice(shipmentEntry.getPrice());
            df.setInHospitalCode(shipmentEntry.getInHospitalCode());
            df.setMasterBarcode(shipmentEntry.getMasterBarcode());
            df.setSecondaryBarcode(shipmentEntry.getSecondaryBarcode());
            df.setGzDepInventoryId(depInventory != null && depInventory.getId() != null ? String.valueOf(depInventory.getId()) : null);
            df.setLx("CK");
            df.setFlowTime(new Date());
            df.setOriginBusinessType("高值出库入科室");
            df.setDelFlag(0);
            df.setCreateBy(uid());
            df.setCreateTime(new Date());
            hcBarcodeTraceMapper.insertGzDepFlow(df);
        } catch (Exception e) {
            log.warn("onGzShipmentDepInventoryInserted failed shipmentId={}", shipment.getId(), e);
        }
    }

    private String resolveDepartmentName(Long departmentId) {
        if (departmentId == null) {
            return null;
        }
        FdDepartment d = fdDepartmentMapper.selectFdDepartmentById(String.valueOf(departmentId));
        return d != null ? d.getName() : null;
    }

    @Override
    public void onGzRefundStockLine(GzRefundGoods bill, GzRefundGoodsEntry entry) {
        if (bill == null || entry == null || StringUtils.isEmpty(entry.getInHospitalCode())) {
            return;
        }
        try {
            String tenantId = StringUtils.isNotEmpty(bill.getTenantId()) ? bill.getTenantId() : SecurityUtils.getCustomerId();
            HcBarcodeMaster master = hcBarcodeTraceMapper.selectHcBarcodeMasterByTenantAndBarcode(tenantId, entry.getInHospitalCode().trim());
            if (master == null) {
                return;
            }
            appendFlowEvent(master, "GZ_REFUND_TK", "高值备货退库", "GZ_REFUND_STOCK",
                bill.getId() != null ? String.valueOf(bill.getId()) : null, bill.getGoodsNo(),
                entry.getId() != null ? String.valueOf(entry.getId()) : null,
                str(bill.getWarehouseId()), str(bill.getWarehouseId()), str(bill.getDepartmentId()), null, entry.getQty());
            GzWhFlow wf = new GzWhFlow();
            wf.setId(UUID7.generateUUID7());
            wf.setTenantId(tenantId);
            wf.setBillId(bill.getId() != null ? String.valueOf(bill.getId()) : null);
            wf.setBillNo(bill.getGoodsNo());
            wf.setEntryId(entry.getId() != null ? String.valueOf(entry.getId()) : null);
            wf.setWarehouseId(bill.getWarehouseId() != null ? String.valueOf(bill.getWarehouseId()) : null);
            wf.setMaterialId(entry.getMaterialId() != null ? String.valueOf(entry.getMaterialId()) : null);
            wf.setBatchNo(entry.getBatchNo());
            wf.setQty(entry.getQty());
            wf.setInHospitalCode(entry.getInHospitalCode());
            wf.setLx("TK");
            wf.setFlowTime(new Date());
            wf.setOriginBusinessType("高值备货退库");
            wf.setDelFlag(0);
            wf.setCreateBy(uid());
            wf.setCreateTime(new Date());
            hcBarcodeTraceMapper.insertGzWhFlow(wf);
        } catch (Exception e) {
            log.warn("onGzRefundStockLine failed billId={}", bill.getId(), e);
        }
    }

    @Override
    public void onGzRefundGoodsLine(GzRefundGoods bill, GzRefundGoodsEntry entry) {
        if (bill == null || entry == null) {
            return;
        }
        try {
            String tenantId = StringUtils.isNotEmpty(bill.getTenantId()) ? bill.getTenantId() : SecurityUtils.getCustomerId();
            String code = StringUtils.isNotEmpty(entry.getInHospitalCode()) ? entry.getInHospitalCode().trim() : null;
            HcBarcodeMaster master = code != null ? hcBarcodeTraceMapper.selectHcBarcodeMasterByTenantAndBarcode(tenantId, code) : null;
            if (master != null) {
                appendFlowEvent(master, "GZ_REFUND_TH", "高值备货退货", "GZ_REFUND_GOODS",
                    bill.getId() != null ? String.valueOf(bill.getId()) : null, bill.getGoodsNo(),
                    entry.getId() != null ? String.valueOf(entry.getId()) : null,
                    str(bill.getWarehouseId()), null, null, null, entry.getQty());
            }
            GzWhFlow wf = new GzWhFlow();
            wf.setId(UUID7.generateUUID7());
            wf.setTenantId(tenantId);
            wf.setBillId(bill.getId() != null ? String.valueOf(bill.getId()) : null);
            wf.setBillNo(bill.getGoodsNo());
            wf.setEntryId(entry.getId() != null ? String.valueOf(entry.getId()) : null);
            wf.setWarehouseId(bill.getWarehouseId() != null ? String.valueOf(bill.getWarehouseId()) : null);
            wf.setMaterialId(entry.getMaterialId() != null ? String.valueOf(entry.getMaterialId()) : null);
            wf.setBatchNo(entry.getBatchNo());
            wf.setQty(entry.getQty());
            wf.setInHospitalCode(code);
            wf.setLx("TH");
            wf.setFlowTime(new Date());
            wf.setOriginBusinessType("高值备货退货");
            wf.setDelFlag(0);
            wf.setCreateBy(uid());
            wf.setCreateTime(new Date());
            hcBarcodeTraceMapper.insertGzWhFlow(wf);
        } catch (Exception e) {
            log.warn("onGzRefundGoodsLine failed billId={}", bill.getId(), e);
        }
    }

    private List<String> splitLvBarcodeTokens(String raw) {
        if (StringUtils.isEmpty(raw)) {
            return Collections.emptyList();
        }
        List<String> out = new ArrayList<>();
        for (String p : raw.split(",")) {
            if (p == null) {
                continue;
            }
            String t = p.trim();
            if (StringUtils.isNotEmpty(t)) {
                out.add(t);
            }
        }
        return out;
    }

    private void patchMasterAfterDeptConsume(HcBarcodeMaster master, BigDecimal signedQty, Long departmentId) {
        if (master == null || signedQty == null || signedQty.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        HcBarcodeMaster patch = new HcBarcodeMaster();
        patch.setId(master.getId());
        patch.setTenantId(master.getTenantId());
        patch.setUpdateBy(uid());
        if (signedQty.compareTo(BigDecimal.ZERO) > 0) {
            patch.setCurrentHolderType("UNKNOWN");
            patch.setCurrentWarehouseId(null);
            patch.setCurrentDepartmentId(null);
            patch.setStatus("CONSUMED");
        } else {
            patch.setCurrentHolderType("DEPT");
            patch.setCurrentDepartmentId(departmentId != null ? String.valueOf(departmentId) : null);
            patch.setCurrentWarehouseId(null);
            patch.setStatus("ACTIVE");
        }
        hcBarcodeTraceMapper.updateHcBarcodeMasterCurrentState(patch);
    }

    private void appendDeptBatchConsumeHcFlowAndPatch(HcBarcodeMaster master, DeptBatchConsume bill,
        DeptBatchConsumeEntry entry, BigDecimal signedQty, boolean highValue) {
        if (master == null || signedQty == null || signedQty.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        boolean consume = signedQty.compareTo(BigDecimal.ZERO) > 0;
        String eventCode = consume ? "KS_XH" : "KS_TXH";
        String eventName;
        if (consume) {
            eventName = highValue ? "科室批量消耗(高值)" : "科室批量消耗";
        } else {
            eventName = highValue ? "科室退消耗(高值)" : "科室退消耗";
        }
        String strDept = bill.getDepartmentId() != null ? String.valueOf(bill.getDepartmentId()) : null;
        String fromDept = consume ? strDept : null;
        String toDept = consume ? null : strDept;
        Integer seq = hcBarcodeTraceMapper.selectNextFlowSeq(master.getTenantId(), master.getId());
        HcBarcodeFlow flow = new HcBarcodeFlow();
        flow.setId(UUID7.generateUUID7());
        flow.setTenantId(master.getTenantId());
        flow.setHcBarcodeMasterId(master.getId());
        flow.setBarcodeValue(master.getBarcodeValue());
        flow.setValueLevel(master.getValueLevel());
        flow.setSeqNo(seq != null ? seq : 1);
        flow.setEventCode(eventCode);
        flow.setEventName(eventName);
        flow.setEventTime(new Date());
        flow.setBillDomain("DEPT_BATCH_CONSUME");
        flow.setBillId(bill.getId() != null ? String.valueOf(bill.getId()) : null);
        flow.setBillNo(bill.getConsumeBillNo());
        flow.setBillEntryId(entry != null && entry.getId() != null ? String.valueOf(entry.getId()) : null);
        flow.setFromWarehouseId(null);
        flow.setToWarehouseId(null);
        flow.setFromDepartmentId(fromDept);
        flow.setToDepartmentId(toDept);
        flow.setQty(signedQty.abs());
        flow.setMaterialName(master.getMaterialName());
        flow.setMaterialSpeci(master.getMaterialSpeci());
        flow.setMaterialModel(master.getMaterialModel());
        flow.setOperatorId(uid());
        flow.setOperatorName(uid());
        flow.setDelFlag(0);
        flow.setCreateBy(uid());
        flow.setCreateTime(new Date());
        hcBarcodeTraceMapper.insertHcBarcodeFlow(flow);
        patchMasterAfterDeptConsume(master, signedQty, bill.getDepartmentId());
    }

    @Override
    public void onDeptBatchConsumeGz(DeptBatchConsume bill, DeptBatchConsumeEntry entry, GzDepInventory gzLine) {
        if (bill == null || gzLine == null || StringUtils.isEmpty(gzLine.getInHospitalCode())) {
            return;
        }
        try {
            String tenantId = StringUtils.isNotEmpty(bill.getTenantId()) ? bill.getTenantId() : SecurityUtils.getCustomerId();
            HcBarcodeMaster master = hcBarcodeTraceMapper.selectHcBarcodeMasterByTenantAndBarcode(tenantId, gzLine.getInHospitalCode().trim());
            if (master == null) {
                return;
            }
            BigDecimal signedQty = entry != null && entry.getQty() != null ? entry.getQty() : BigDecimal.ZERO;
            if (signedQty.compareTo(BigDecimal.ZERO) == 0) {
                return;
            }
            appendDeptBatchConsumeHcFlowAndPatch(master, bill, entry, signedQty, true);
        } catch (Exception e) {
            log.warn("onDeptBatchConsumeGz failed billId={}", bill.getId(), e);
        }
    }

    @Override
    public void onDeptBatchConsumeLv(DeptBatchConsume bill, DeptBatchConsumeEntry entry, StkDepInventory depLine) {
        if (bill == null || depLine == null) {
            return;
        }
        try {
            String tenantId = StringUtils.isNotEmpty(bill.getTenantId()) ? bill.getTenantId() : SecurityUtils.getCustomerId();
            List<String> codes = splitLvBarcodeTokens(depLine.getLvInhospitalPackageCode());
            if (codes.isEmpty()) {
                return;
            }
            BigDecimal signedQty = entry != null && entry.getQty() != null ? entry.getQty() : BigDecimal.ZERO;
            if (signedQty.compareTo(BigDecimal.ZERO) == 0) {
                return;
            }
            int n = codes.size();
            if (n == 1) {
                HcBarcodeMaster master = hcBarcodeTraceMapper.selectHcBarcodeMasterByTenantAndBarcode(tenantId, codes.get(0));
                if (master != null) {
                    appendDeptBatchConsumeHcFlowAndPatch(master, bill, entry, signedQty, false);
                }
                return;
            }
            BigDecimal acc = BigDecimal.ZERO;
            BigDecimal base = signedQty.divide(BigDecimal.valueOf(n), 6, RoundingMode.DOWN);
            for (int i = 0; i < n; i++) {
                BigDecimal part = (i == n - 1) ? signedQty.subtract(acc) : base;
                acc = acc.add(part);
                if (part.compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }
                HcBarcodeMaster master = hcBarcodeTraceMapper.selectHcBarcodeMasterByTenantAndBarcode(tenantId, codes.get(i));
                if (master != null) {
                    appendDeptBatchConsumeHcFlowAndPatch(master, bill, entry, part, false);
                }
            }
        } catch (Exception e) {
            log.warn("onDeptBatchConsumeLv failed billId={}", bill != null ? bill.getId() : null, e);
        }
    }
}

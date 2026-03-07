package com.spd.finance.service.impl;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.common.utils.uuid.UUID7;
import com.spd.finance.domain.*;
import com.spd.finance.mapper.SuppSettlementBillMapper;
import com.spd.finance.mapper.WhSettlementBillMapper;
import com.spd.finance.service.IWhSettlementBillService;
import com.spd.foundation.domain.FdWarehouse;
import com.spd.foundation.mapper.FdWarehouseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 仓库结算单Service实现
 */
@Service
public class WhSettlementBillServiceImpl implements IWhSettlementBillService {

    @Autowired
    private WhSettlementBillMapper whSettlementBillMapper;
    @Autowired
    private SuppSettlementBillMapper suppSettlementBillMapper;
    @Autowired
    private FdWarehouseMapper fdWarehouseMapper;

    @Override
    public List<WhSettlementBill> list(WhSettlementBill query) {
        if (query != null && StringUtils.isEmpty(query.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            query.setTenantId(SecurityUtils.getCustomerId());
        }
        return whSettlementBillMapper.selectList(query);
    }

    @Override
    public WhSettlementBill getById(String id) {
        WhSettlementBill bill = whSettlementBillMapper.selectById(id);
        if (bill == null) return null;
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isNotEmpty(customerId) && StringUtils.isNotEmpty(bill.getTenantId()) && !customerId.equals(bill.getTenantId())) {
            return null;
        }
        bill.setEntryList(whSettlementBillMapper.selectEntriesByParenId(id));
        return bill;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(WhSettlementBill row) {
        if (row == null) return 0;
        if (StringUtils.isEmpty(row.getId())) {
            row.setId(UUID7.generateUUID7());
        }
        if (StringUtils.isEmpty(row.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            row.setTenantId(SecurityUtils.getCustomerId());
        }
        FdWarehouse wh = null;
        if (row.getWarehouseId() != null) {
            wh = fdWarehouseMapper.selectFdWarehouseById(String.valueOf(row.getWarehouseId()));
            if (wh != null) {
                row.setWarehouseCode(wh.getCode());
                row.setWarehouseName(wh.getName());
                if (StringUtils.isEmpty(row.getTenantId()) && StringUtils.isNotEmpty(wh.getTenantId())) {
                    row.setTenantId(wh.getTenantId());
                }
            }
        }
        if (StringUtils.isEmpty(row.getBillNo())) {
            String dateStr = FillRuleUtil.getDateNum();
            String prefix = "CKD" + dateStr;
            String maxNo = whSettlementBillMapper.selectMaxBillNo(prefix);
            row.setBillNo(FillRuleUtil.getNumber("CKD", maxNo, dateStr));
        }
        row.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getCreateBy())) {
            row.setCreateBy(SecurityUtils.getUsername());
        }
        row.setAuditStatus(0);
        row.setDelFlag(0);
        int r = whSettlementBillMapper.insert(row);
        if (row.getEntryList() != null && !row.getEntryList().isEmpty()) {
            saveEntries(row.getId(), row.getEntryList());
        }
        return r;
    }

    @Override
    public int update(WhSettlementBill row) {
        if (row == null || StringUtils.isEmpty(row.getId())) return 0;
        WhSettlementBill existing = whSettlementBillMapper.selectById(row.getId());
        if (existing == null) {
            throw new ServiceException("仓库结算单不存在");
        }
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isNotEmpty(customerId) && StringUtils.isNotEmpty(existing.getTenantId()) && !customerId.equals(existing.getTenantId())) {
            throw new ServiceException("无权操作其他租户数据");
        }
        if (existing.getAuditStatus() != null && existing.getAuditStatus() == 1) {
            throw new ServiceException("已审核的仓库结算单不可修改");
        }
        return whSettlementBillMapper.update(row);
    }

    @Override
    public int remove(String id) {
        if (StringUtils.isEmpty(id)) return 0;
        WhSettlementBill existing = whSettlementBillMapper.selectById(id);
        if (existing == null) return 0;
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isNotEmpty(customerId) && StringUtils.isNotEmpty(existing.getTenantId()) && !customerId.equals(existing.getTenantId())) {
            throw new ServiceException("无权操作其他租户数据");
        }
        if (existing.getAuditStatus() != null && existing.getAuditStatus() == 1) {
            throw new ServiceException("已审核的仓库结算单不可删除");
        }
        return whSettlementBillMapper.deleteById(id, SecurityUtils.getUsername());
    }

    @Override
    public List<WhSettlementBillEntry> extractData(Long warehouseId, String settlementMethod, Date beginTime, Date endTime) {
        if (warehouseId == null) {
            throw new ServiceException("请选择仓库");
        }
        if (StringUtils.isEmpty(settlementMethod)) {
            throw new ServiceException("请选择结算方式");
        }
        if (beginTime == null || endTime == null) {
            throw new ServiceException("请选择开始时间和结束时间");
        }
        String tenantId = SecurityUtils.getCustomerId();
        if ("1".equals(settlementMethod)) {
            return whSettlementBillMapper.selectUnsettledInboundEntries(warehouseId, beginTime, endTime, tenantId);
        }
        if ("2".equals(settlementMethod)) {
            return whSettlementBillMapper.selectUnsettledOutboundEntries(warehouseId, beginTime, endTime, tenantId);
        }
        if ("3".equals(settlementMethod)) {
            throw new ServiceException("消耗结算暂不支持提取数据，请使用入库结算或出库结算");
        }
        throw new ServiceException("不支持的结算方式");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveEntries(String billId, List<WhSettlementBillEntry> entries) {
        if (StringUtils.isEmpty(billId)) return 0;
        WhSettlementBill bill = whSettlementBillMapper.selectById(billId);
        if (bill == null) {
            throw new ServiceException("仓库结算单不存在");
        }
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isNotEmpty(customerId) && StringUtils.isNotEmpty(bill.getTenantId()) && !customerId.equals(bill.getTenantId())) {
            throw new ServiceException("无权操作其他租户数据");
        }
        if (bill.getAuditStatus() != null && bill.getAuditStatus() == 1) {
            throw new ServiceException("审核之后不得删除、增加单据明细");
        }
        whSettlementBillMapper.deleteEntriesByParenId(billId, SecurityUtils.getUsername());
        if (entries == null) entries = new ArrayList<>();
        String billNo = bill.getBillNo();
        String tenantId = bill.getTenantId();
        for (int i = 0; i < entries.size(); i++) {
            WhSettlementBillEntry e = entries.get(i);
            if (StringUtils.isEmpty(e.getId())) e.setId(UUID7.generateUUID7());
            e.setParenId(billId);
            e.setBillNo(billNo);
            e.setTenantId(tenantId);
            e.setSortOrder(i);
        }
        if (entries.isEmpty()) {
            return 1;
        }
        whSettlementBillMapper.insertEntryBatch(entries);
        return entries.size();
    }

    @Override
    public int removeEntries(String billId, List<String> entryIds) {
        if (StringUtils.isEmpty(billId) || entryIds == null || entryIds.isEmpty()) return 0;
        WhSettlementBill bill = whSettlementBillMapper.selectById(billId);
        if (bill == null) throw new ServiceException("仓库结算单不存在");
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isNotEmpty(customerId) && StringUtils.isNotEmpty(bill.getTenantId()) && !customerId.equals(bill.getTenantId())) {
            throw new ServiceException("无权操作其他租户数据");
        }
        if (bill.getAuditStatus() != null && bill.getAuditStatus() == 1) {
            throw new ServiceException("审核之后不得删除、增加单据明细");
        }
        return whSettlementBillMapper.deleteEntriesByIds(billId, entryIds, SecurityUtils.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int audit(String id) {
        if (StringUtils.isEmpty(id)) return 0;
        WhSettlementBill bill = whSettlementBillMapper.selectById(id);
        if (bill == null) {
            throw new ServiceException("仓库结算单不存在");
        }
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isNotEmpty(customerId) && StringUtils.isNotEmpty(bill.getTenantId()) && !customerId.equals(bill.getTenantId())) {
            throw new ServiceException("无权操作其他租户数据");
        }
        if (bill.getAuditStatus() != null && bill.getAuditStatus() == 1) {
            throw new ServiceException("已审核，不能重复审核");
        }
        List<WhSettlementBillEntry> entries = whSettlementBillMapper.selectEntriesByParenId(id);
        if (entries == null || entries.isEmpty()) {
            throw new ServiceException("无明细不能审核");
        }
        Date auditTime = DateUtils.getNowDate();
        String auditBy = SecurityUtils.getUsername();
        whSettlementBillMapper.updateAuditStatus(id, auditBy, auditTime);

        // 按供应商分组生成供应商结算单
        Map<Long, List<WhSettlementBillEntry>> bySupplier = entries.stream()
                .filter(e -> e.getSupplierId() != null)
                .collect(Collectors.groupingBy(WhSettlementBillEntry::getSupplierId));

        String dateStr = FillRuleUtil.getDateNum();
        String tenantId = bill.getTenantId();
        for (Map.Entry<Long, List<WhSettlementBillEntry>> en : bySupplier.entrySet()) {
            Long supplierId = en.getKey();
            List<WhSettlementBillEntry> whEntries = en.getValue();
            String supplierName = whEntries.isEmpty() ? null : whEntries.get(0).getSupplierName();

            SuppSettlementBill supp = new SuppSettlementBill();
            supp.setId(UUID7.generateUUID7());
            supp.setTenantId(tenantId);
            String prefix = "GYS" + dateStr;
            String maxNo = suppSettlementBillMapper.selectMaxBillNo(prefix);
            supp.setBillNo(FillRuleUtil.getNumber("GYS", maxNo, dateStr));
            supp.setSupplierId(supplierId);
            supp.setSupplierName(supplierName);
            supp.setWhSettlementId(id);
            supp.setCreateBy(auditBy);
            supp.setCreateTime(auditTime);
            supp.setAuditStatus(1);
            supp.setDelFlag(0);
            suppSettlementBillMapper.insert(supp);

            List<SuppSettlementBillEntry> suppEntries = new ArrayList<>();
            for (int i = 0; i < whEntries.size(); i++) {
                WhSettlementBillEntry we = whEntries.get(i);
                SuppSettlementBillEntry se = new SuppSettlementBillEntry();
                se.setId(UUID7.generateUUID7());
                se.setTenantId(tenantId);
                se.setParenId(supp.getId());
                se.setBillNo(supp.getBillNo());
                se.setWhSettlementId(bill.getId());
                se.setWhSettlementBillNo(bill.getBillNo());
                se.setWhSettlementEntryId(we.getId());
                se.setMaterialId(we.getMaterialId());
                se.setMaterialName(we.getMaterialName());
                se.setSpeci(we.getSpeci());
                se.setModel(we.getModel());
                se.setUnit(we.getUnit());
                se.setUnitPrice(we.getUnitPrice());
                se.setQty(we.getQty());
                se.setAmt(we.getAmt());
                se.setBatchNumber(we.getBatchNumber());
                se.setEndTime(we.getEndTime());
                se.setBatchNo(we.getBatchNo());
                se.setFactoryId(we.getFactoryId());
                se.setFactoryCode(we.getFactoryCode());
                se.setFactoryName(we.getFactoryName());
                se.setSourceBillType(we.getSourceBillType());
                se.setSourceBillId(we.getSourceBillId());
                se.setSourceBillNo(we.getSourceBillNo());
                se.setSourceEntryId(we.getSourceEntryId());
                se.setSupplierId(we.getSupplierId());
                se.setSupplierName(we.getSupplierName());
                se.setSortOrder(i);
                suppEntries.add(se);
            }
            if (!suppEntries.isEmpty()) {
                suppSettlementBillMapper.insertEntryBatch(suppEntries);
            }
        }
        return 1;
    }
}

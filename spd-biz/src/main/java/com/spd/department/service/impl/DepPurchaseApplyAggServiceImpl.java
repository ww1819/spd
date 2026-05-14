package com.spd.department.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.department.domain.DepPurchaseApply;
import com.spd.department.domain.DepPurchaseApplyAgg;
import com.spd.department.domain.DepPurchaseApplyAggEntry;
import com.spd.department.domain.DepPurchaseApplyEntry;
import com.spd.department.mapper.DepPurchaseApplyAggMapper;
import com.spd.department.service.IDepPurchaseApplyAggService;
import com.spd.department.service.IDepPurchaseApplyService;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.system.service.ITenantScopeService;

@Service
public class DepPurchaseApplyAggServiceImpl implements IDepPurchaseApplyAggService {

    @Autowired
    private DepPurchaseApplyAggMapper depPurchaseApplyAggMapper;

    @Autowired
    private IDepPurchaseApplyService depPurchaseApplyService;

    @Autowired
    private ITenantScopeService tenantScopeService;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    private void assertDepartmentInUserScope(Long departmentId) {
        Long userId = SecurityUtils.getUserId();
        String customerId = SecurityUtils.getCustomerId();
        List<Long> deptIds = tenantScopeService.resolveDepartmentScope(userId, customerId);
        if (deptIds == null) {
            return;
        }
        if (departmentId == null || deptIds.isEmpty() || !deptIds.contains(departmentId)) {
            throw new ServiceException("无权操作该科室的汇总申购单");
        }
    }

    private void assertAggDepartmentInUserScope(DepPurchaseApplyAgg a) {
        if (a == null) {
            return;
        }
        assertDepartmentInUserScope(a.getDepartmentId());
    }

    @Override
    public void applyDepartmentScopeToQuery(DepPurchaseApplyAgg query) {
        if (query == null) {
            return;
        }
        tenantScopeService.applyDepartmentScopeQueryParams(
            query.getParams(), SecurityUtils.getUserId(), SecurityUtils.getCustomerId());
    }

    @Override
    public DepPurchaseApplyAgg selectDepPurchaseApplyAggById(String id) {
        DepPurchaseApplyAgg a = depPurchaseApplyAggMapper.selectDepPurchaseApplyAggById(id);
        if (a != null) {
            SecurityUtils.ensureTenantAccess(a.getTenantId());
            assertAggDepartmentInUserScope(a);
        }
        return a;
    }

    @Override
    public List<DepPurchaseApplyAgg> selectDepPurchaseApplyAggList(DepPurchaseApplyAgg query) {
        if (query != null && StringUtils.isEmpty(query.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            query.setTenantId(SecurityUtils.getCustomerId());
        }
        return depPurchaseApplyAggMapper.selectDepPurchaseApplyAggList(query);
    }

    private void validateEntryQty(List<DepPurchaseApplyAggEntry> list) {
        if (list == null) {
            return;
        }
        for (DepPurchaseApplyAggEntry e : list) {
            if (e.getMaterialId() != null && (e.getQty() == null || e.getQty().compareTo(BigDecimal.ZERO) <= 0)) {
                throw new ServiceException("汇总申购明细中数量不能为空且必须大于0，请检查后保存。");
            }
        }
    }

    private String generateAggPurchaseBillNo() {
        String dateStr = DateUtils.dateTimeNow("yyyyMMdd");
        String prefix = "SGH" + dateStr;
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
        return prefix + timestamp;
    }

    private void fillEntryAmt(DepPurchaseApplyAggEntry e) {
        if (e.getAmt() == null && e.getQty() != null && e.getUnitPrice() != null) {
            e.setAmt(e.getQty().multiply(e.getUnitPrice()));
        }
    }

    private BigDecimal sumEntryAmt(List<DepPurchaseApplyAggEntry> entries) {
        BigDecimal t = BigDecimal.ZERO;
        if (entries == null) {
            return t;
        }
        for (DepPurchaseApplyAggEntry e : entries) {
            if (e.getAmt() != null) {
                t = t.add(e.getAmt());
            }
        }
        return t;
    }

    @Transactional
    @Override
    public int insertDepPurchaseApplyAgg(DepPurchaseApplyAgg row) {
        if (row.getDepartmentId() != null) {
            assertDepartmentInUserScope(row.getDepartmentId());
        }
        validateEntryQty(row.getEntryList());
        if (StringUtils.isEmpty(row.getId())) {
            row.setId(UUID7.generateUUID7());
        }
        if (StringUtils.isEmpty(row.getPurchaseBillNo())) {
            row.setPurchaseBillNo(generateAggPurchaseBillNo());
        }
        row.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getCreateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            row.setCreateBy(SecurityUtils.getUserIdStr());
        }
        if (StringUtils.isEmpty(row.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            row.setTenantId(SecurityUtils.getCustomerId());
        }
        if (row.getPurchaseBillStatus() == null) {
            row.setPurchaseBillStatus(1);
        }
        if (row.getSplitStatus() == null) {
            row.setSplitStatus(0);
        }
        if (row.getDelFlag() == null) {
            row.setDelFlag(0);
        }
        List<DepPurchaseApplyAggEntry> rawList = row.getEntryList();
        List<DepPurchaseApplyAggEntry> toSave = new ArrayList<>();
        int line = 1;
        if (rawList != null) {
            for (DepPurchaseApplyAggEntry e : rawList) {
                if (e == null || e.getMaterialId() == null) {
                    continue;
                }
                if (StringUtils.isEmpty(e.getId())) {
                    e.setId(UUID7.generateUUID7());
                }
                e.setParentId(row.getId());
                e.setTenantId(row.getTenantId());
                e.setDelFlag(0);
                e.setCreateTime(DateUtils.getNowDate());
                e.setCreateBy(SecurityUtils.getUserIdStr());
                if (e.getLineNo() == null) {
                    e.setLineNo(line++);
                }
                fillEntryAmt(e);
                toSave.add(e);
            }
        }
        row.setTotalAmount(sumEntryAmt(toSave));
        int n = depPurchaseApplyAggMapper.insertDepPurchaseApplyAgg(row);
        if (!toSave.isEmpty()) {
            depPurchaseApplyAggMapper.batchInsertDepPurchaseApplyAggEntry(toSave);
        }
        return n;
    }

    @Transactional
    @Override
    public int updateDepPurchaseApplyAgg(DepPurchaseApplyAgg row) {
        if (row == null || StringUtils.isEmpty(row.getId())) {
            throw new ServiceException("汇总申购单主键不能为空");
        }
        DepPurchaseApplyAgg existing = depPurchaseApplyAggMapper.selectDepPurchaseApplyAggById(row.getId());
        if (existing != null) {
            SecurityUtils.ensureTenantAccess(existing.getTenantId());
            assertAggDepartmentInUserScope(existing);
        }
        if (existing == null || existing.getDelFlag() != null && existing.getDelFlag() == 1) {
            throw new ServiceException("汇总申购单不存在或已删除");
        }
        if (existing.getPurchaseBillStatus() == null || existing.getPurchaseBillStatus() != 1
            || (existing.getSplitStatus() != null && existing.getSplitStatus() != 0)) {
            throw new ServiceException("仅待审核且未拆分的汇总申购单可修改");
        }
        if (row.getDepartmentId() != null) {
            assertDepartmentInUserScope(row.getDepartmentId());
        }
        validateEntryQty(row.getEntryList());
        row.setUpdateBy(SecurityUtils.getUserIdStr());
        row.setUpdateTime(DateUtils.getNowDate());
        String deleteBy = SecurityUtils.getUserIdStr();
        depPurchaseApplyAggMapper.deleteDepPurchaseApplyAggEntryByParentId(row.getId(), deleteBy);

        List<DepPurchaseApplyAggEntry> rawList = row.getEntryList();
        List<DepPurchaseApplyAggEntry> toSave = new ArrayList<>();
        int line = 1;
        if (rawList != null) {
            for (DepPurchaseApplyAggEntry e : rawList) {
                if (e == null || e.getMaterialId() == null) {
                    continue;
                }
                if (StringUtils.isEmpty(e.getId())) {
                    e.setId(UUID7.generateUUID7());
                }
                e.setParentId(row.getId());
                e.setTenantId(existing.getTenantId());
                e.setDelFlag(0);
                e.setCreateTime(DateUtils.getNowDate());
                e.setCreateBy(SecurityUtils.getUserIdStr());
                if (e.getLineNo() == null) {
                    e.setLineNo(line++);
                }
                fillEntryAmt(e);
                toSave.add(e);
            }
        }
        row.setTotalAmount(sumEntryAmt(toSave));
        if (!toSave.isEmpty()) {
            depPurchaseApplyAggMapper.batchInsertDepPurchaseApplyAggEntry(toSave);
        }
        return depPurchaseApplyAggMapper.updateDepPurchaseApplyAgg(row);
    }

    @Transactional
    @Override
    public int deleteDepPurchaseApplyAggById(String id) {
        DepPurchaseApplyAgg existing = depPurchaseApplyAggMapper.selectDepPurchaseApplyAggById(id);
        if (existing != null) {
            SecurityUtils.ensureTenantAccess(existing.getTenantId());
            assertAggDepartmentInUserScope(existing);
        }
        if (existing == null) {
            throw new ServiceException("汇总申购单不存在");
        }
        if (existing.getPurchaseBillStatus() == null || existing.getPurchaseBillStatus() != 1
            || (existing.getSplitStatus() != null && existing.getSplitStatus() != 0)) {
            throw new ServiceException("仅待审核且未拆分的汇总申购单可删除");
        }
        String deleteBy = SecurityUtils.getUserIdStr();
        depPurchaseApplyAggMapper.deleteDepPurchaseApplyAggEntryByParentId(id, deleteBy);
        return depPurchaseApplyAggMapper.deleteDepPurchaseApplyAggById(id, deleteBy);
    }

    @Transactional
    @Override
    public int deleteDepPurchaseApplyAggByIds(String[] ids) {
        if (ids == null || ids.length == 0) {
            return 0;
        }
        int sum = 0;
        for (String id : ids) {
            if (StringUtils.isEmpty(id)) {
                continue;
            }
            sum += deleteDepPurchaseApplyAggById(id.trim());
        }
        return sum;
    }

    private DepPurchaseApplyEntry toDepEntry(DepPurchaseApplyAggEntry ae) {
        DepPurchaseApplyEntry e = new DepPurchaseApplyEntry();
        e.setMaterialId(ae.getMaterialId());
        e.setMaterialName(ae.getMaterialName());
        e.setMaterialSpec(ae.getMaterialSpec());
        e.setUnit(ae.getUnit());
        e.setUnitPrice(ae.getUnitPrice());
        e.setQty(ae.getQty());
        e.setAmt(ae.getAmt());
        if (e.getAmt() == null && e.getQty() != null && e.getUnitPrice() != null) {
            e.setAmt(e.getQty().multiply(e.getUnitPrice()));
        }
        e.setReason(ae.getReason());
        e.setSupplierName(ae.getSupplierName());
        e.setBrand(ae.getBrand());
        e.setModel(ae.getModel());
        e.setRemark(ae.getRemark());
        e.setSrcAggEntryId(ae.getId());
        return e;
    }

    @Transactional
    @Override
    public int auditDepPurchaseApplyAgg(String id) {
        DepPurchaseApplyAgg agg = depPurchaseApplyAggMapper.selectDepPurchaseApplyAggById(id);
        if (agg == null) {
            throw new ServiceException("汇总申购单不存在");
        }
        SecurityUtils.ensureTenantAccess(agg.getTenantId());
        assertAggDepartmentInUserScope(agg);
        if (agg.getPurchaseBillStatus() == null || agg.getPurchaseBillStatus() != 1) {
            throw new ServiceException("只有待审核状态的汇总申购单可审核，当前状态：" + agg.getPurchaseBillStatus());
        }
        if (agg.getSplitStatus() != null && agg.getSplitStatus() != 0) {
            throw new ServiceException("该汇总申购单已拆分，不能重复审核");
        }
        List<DepPurchaseApplyAggEntry> entries = agg.getEntryList();
        if (entries == null || entries.isEmpty()) {
            throw new ServiceException("汇总申购单无有效明细，无法审核拆分");
        }
        validateEntryQty(entries);

        List<Long> materialIds = entries.stream()
            .map(DepPurchaseApplyAggEntry::getMaterialId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
        List<FdMaterial> materials = materialIds.isEmpty() ? new ArrayList<>() : fdMaterialMapper.selectFdMaterialByIds(materialIds);
        Map<Long, FdMaterial> matMap = new LinkedHashMap<>();
        for (FdMaterial m : materials) {
            if (m != null && m.getId() != null) {
                matMap.put(m.getId(), m);
            }
        }

        Map<Long, List<DepPurchaseApplyAggEntry>> byWh = new LinkedHashMap<>();
        List<String> missing = new ArrayList<>();
        for (DepPurchaseApplyAggEntry e : entries) {
            if (e.getMaterialId() == null) {
                continue;
            }
            FdMaterial m = matMap.get(e.getMaterialId());
            Long whId = m != null ? m.getDefaultWarehouseId() : null;
            if (whId == null || whId <= 0) {
                String label = m != null && StringUtils.isNotEmpty(m.getName()) ? m.getName()
                    : (StringUtils.isNotEmpty(e.getMaterialName()) ? e.getMaterialName() : String.valueOf(e.getMaterialId()));
                missing.add(label);
                continue;
            }
            byWh.computeIfAbsent(whId, k -> new ArrayList<>()).add(e);
        }
        if (!missing.isEmpty()) {
            throw new ServiceException("以下耗材未维护默认仓库，无法按仓拆分，请在产品档案中维护 default_warehouse_id 后再审核："
                + String.join("、", missing));
        }
        if (byWh.isEmpty()) {
            throw new ServiceException("没有可拆分的明细行");
        }

        String auditBy = SecurityUtils.getUserIdStr();
        Date now = new Date();
        for (Map.Entry<Long, List<DepPurchaseApplyAggEntry>> g : byWh.entrySet()) {
            Long warehouseId = g.getKey();
            List<DepPurchaseApplyAggEntry> group = g.getValue();
            List<DepPurchaseApplyEntry> depEntries = new ArrayList<>();
            for (DepPurchaseApplyAggEntry ae : group) {
                fillEntryAmt(ae);
                depEntries.add(toDepEntry(ae));
            }
            DepPurchaseApply bill = new DepPurchaseApply();
            bill.setWarehouseId(warehouseId);
            bill.setDepartmentId(agg.getDepartmentId());
            bill.setUserId(agg.getUserId());
            bill.setPurchaseBillDate(agg.getPurchaseBillDate());
            bill.setUrgencyLevel(agg.getUrgencyLevel());
            bill.setExpectedDeliveryDate(agg.getExpectedDeliveryDate());
            bill.setRemark(agg.getRemark());
            // 汇总单已审，拆分后的科室申购单直接为已审核，沿用原采购/出库逻辑
            bill.setPurchaseBillStatus(2);
            bill.setUpdateBy(auditBy);
            bill.setUpdateTime(now);
            bill.setSrcAggApplyId(agg.getId());
            bill.setSrcAggBillNo(agg.getPurchaseBillNo());
            bill.setTotalAmount(sumEntryAmt(group));
            bill.setDepPurchaseApplyEntryList(depEntries);
            bill.setTenantId(agg.getTenantId());
            depPurchaseApplyService.insertDepPurchaseApply(bill);
        }

        agg.setPurchaseBillStatus(2);
        agg.setSplitStatus(1);
        agg.setUpdateBy(auditBy);
        agg.setUpdateTime(now);
        return depPurchaseApplyAggMapper.updateDepPurchaseApplyAgg(agg);
    }

    @Transactional
    @Override
    public int rejectDepPurchaseApplyAgg(String id, String rejectReason) {
        DepPurchaseApplyAgg agg = depPurchaseApplyAggMapper.selectDepPurchaseApplyAggById(id);
        if (agg == null) {
            throw new ServiceException("汇总申购单不存在");
        }
        SecurityUtils.ensureTenantAccess(agg.getTenantId());
        assertAggDepartmentInUserScope(agg);
        if (agg.getPurchaseBillStatus() == null || agg.getPurchaseBillStatus() != 1) {
            throw new ServiceException("只有待审核状态的汇总申购单可驳回，当前状态：" + agg.getPurchaseBillStatus());
        }
        if (agg.getSplitStatus() != null && agg.getSplitStatus() != 0) {
            throw new ServiceException("已拆分的汇总申购单不可驳回");
        }
        agg.setPurchaseBillStatus(3);
        agg.setRejectReason(rejectReason);
        agg.setUpdateBy(SecurityUtils.getUserIdStr());
        agg.setUpdateTime(new Date());
        return depPurchaseApplyAggMapper.updateDepPurchaseApplyAgg(agg);
    }
}

package com.spd.department.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.common.utils.uuid.UUID7;
import com.spd.department.domain.BasApply;
import com.spd.department.domain.BasApplyEntry;
import com.spd.department.domain.WhWhApplyCkEntryRef;
import com.spd.department.domain.WhWarehouseApply;
import com.spd.department.domain.WhWarehouseApplyEntry;
import com.spd.department.mapper.WhWarehouseApplyMapper;
import com.spd.department.service.IWhWarehouseApplyService;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.domain.StkIoBillEntry;
import com.spd.warehouse.domain.StkInventory;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.warehouse.mapper.StkIoBillMapper;
import com.spd.warehouse.mapper.StkInventoryMapper;

@Service
public class WhWarehouseApplyServiceImpl implements IWhWarehouseApplyService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    @Autowired
    private WhWarehouseApplyMapper whWarehouseApplyMapper;

    @Autowired
    private StkInventoryMapper stkInventoryMapper;

    @Autowired
    private StkIoBillMapper stkIoBillMapper;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateFromDeptApplyAfterAudit(BasApply basApply) {
        if (basApply == null || basApply.getId() == null) {
            return;
        }
        if (basApply.getBillType() == null || basApply.getBillType() != 1) {
            return;
        }
        String tenantId = StringUtils.isNotEmpty(basApply.getTenantId())
            ? basApply.getTenantId() : SecurityUtils.requiredScopedTenantIdForSql();
        String basApplyIdStr = String.valueOf(basApply.getId());
        if (whWarehouseApplyMapper.countActiveByBasApplyId(basApplyIdStr, tenantId) > 0) {
            throw new ServiceException("该科室申领已生成过仓库申请单，不能重复审核拆分");
        }
        List<BasApplyEntry> entries = basApply.getBasApplyEntryList();
        if (entries == null || entries.isEmpty()) {
            return;
        }
        Map<Long, List<AllocLine>> byWarehouse = new LinkedHashMap<>();
        for (BasApplyEntry e : entries) {
            if (e == null || e.getMaterialId() == null) {
                continue;
            }
            if (e.getQty() == null || e.getQty().compareTo(ZERO) <= 0) {
                continue;
            }
            Long stockWh = e.getStockWarehouseId();
            if (stockWh == null) {
                throw new ServiceException(String.format(
                    "审核失败：明细耗材ID %s 未指定可用库存所属仓库，请修改申领单后重新审核", e.getMaterialId()));
            }
            BigDecimal need = e.getQty();
            List<StkInventory> fifo = stkInventoryMapper.selectStkInventoryFifoForDeptApply(tenantId, e.getMaterialId(), stockWh);
            if (fifo == null) {
                fifo = new ArrayList<>();
            }
            for (StkInventory inv : fifo) {
                if (need.compareTo(ZERO) <= 0) {
                    break;
                }
                if (inv == null || inv.getWarehouseId() == null) {
                    continue;
                }
                BigDecimal rowQty = inv.getQty() != null ? inv.getQty() : ZERO;
                if (rowQty.compareTo(ZERO) <= 0) {
                    continue;
                }
                BigDecimal take = need.min(rowQty);
                AllocLine line = new AllocLine();
                line.warehouseId = stockWh;
                line.materialId = e.getMaterialId();
                line.qty = take;
                line.unitPrice = inv.getUnitPrice() != null ? inv.getUnitPrice() : ZERO;
                line.amt = take.multiply(line.unitPrice);
                line.batchNo = inv.getBatchNo();
                line.batchNumber = inv.getBatchNumber();
                line.beginTime = inv.getBeginTime();
                line.endTime = inv.getEndTime();
                line.stkInventoryId = inv.getId();
                line.supplierId = inv.getSupplierId();
                line.factoryId = inv.getFactoryId();
                line.basApplyEntryId = e.getId();
                byWarehouse.computeIfAbsent(line.warehouseId, k -> new ArrayList<>()).add(line);
                need = need.subtract(take);
            }
            if (need.compareTo(ZERO) > 0) {
                throw new ServiceException(String.format(
                    "审核失败：耗材ID %s 在指定仓库（仓库ID %s）内可用库存不足，还差 %s（已按先入先出）",
                    e.getMaterialId(), stockWh, need.stripTrailingZeros().toPlainString()));
            }
        }
        if (byWarehouse.isEmpty()) {
            return;
        }
        String uid = SecurityUtils.getUserIdStr();
        Date now = DateUtils.getNowDate();
        String basBillNo = basApply.getApplyBillNo();
        Date auditDate = basApply.getAuditDate() != null ? basApply.getAuditDate() : now;
        for (Map.Entry<Long, List<AllocLine>> en : byWarehouse.entrySet()) {
            List<AllocLine> lines = en.getValue();
            BigDecimal sumQty = ZERO;
            BigDecimal sumAmt = ZERO;
            for (AllocLine al : lines) {
                sumQty = sumQty.add(al.qty);
                sumAmt = sumAmt.add(al.amt != null ? al.amt : ZERO);
            }
            WhWarehouseApply main = new WhWarehouseApply();
            main.setId(UUID7.generateUUID7());
            main.setTenantId(tenantId);
            main.setApplyBillNo(nextBillNo());
            main.setBasApplyId(basApplyIdStr);
            main.setBasApplyBillNo(basBillNo);
            main.setWarehouseId(en.getKey());
            main.setDepartmentId(basApply.getDepartmentId());
            main.setBillStatus(2);
            main.setVoidWholeFlag(0);
            main.setTotalQty(sumQty);
            main.setTotalAmt(sumAmt);
            main.setSourceAuditDate(auditDate);
            main.setDelFlag(0);
            main.setCreateBy(uid);
            main.setCreateTime(now);
            whWarehouseApplyMapper.insertWhWarehouseApply(main);
            int lineNo = 1;
            for (AllocLine al : lines) {
                WhWarehouseApplyEntry row = new WhWarehouseApplyEntry();
                row.setId(UUID7.generateUUID7());
                row.setParenId(main.getId());
                row.setTenantId(tenantId);
                row.setBasApplyId(basApplyIdStr);
                row.setBasApplyBillNo(basBillNo);
                row.setBasApplyEntryId(String.valueOf(al.basApplyEntryId));
                row.setLineNo(lineNo++);
                row.setMaterialId(al.materialId);
                row.setWarehouseId(al.warehouseId);
                row.setStkInventoryId(al.stkInventoryId);
                row.setUnitPrice(al.unitPrice);
                row.setQty(al.qty);
                row.setPrice(al.unitPrice);
                row.setAmt(al.amt);
                row.setBatchNo(al.batchNo);
                row.setBatchNumber(al.batchNumber);
                row.setBeginTime(al.beginTime);
                row.setEndTime(al.endTime);
                row.setSupplierId(al.supplierId);
                row.setFactoryId(al.factoryId);
                row.setLineVoidStatus(0);
                row.setLineVoidQty(ZERO);
                row.setDelFlag(0);
                row.setCreateBy(uid);
                row.setCreateTime(now);
                whWarehouseApplyMapper.insertWhWarehouseApplyEntry(row);
            }
        }
    }

    private String nextBillNo() {
        String date = FillRuleUtil.getDateNum();
        String maxNum = whWarehouseApplyMapper.selectMaxBillNo(date);
        return FillRuleUtil.getNumber("CKSQ", maxNum, date);
    }

    @Override
    public List<WhWarehouseApply> selectWhWarehouseApplyList(WhWarehouseApply query) {
        if (query != null && StringUtils.isEmpty(query.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            query.setTenantId(SecurityUtils.getCustomerId());
        }
        return whWarehouseApplyMapper.selectWhWarehouseApplyList(query);
    }

    @Override
    public WhWarehouseApply selectWhWarehouseApplyById(String id) {
        WhWarehouseApply m = whWarehouseApplyMapper.selectWhWarehouseApplyById(id);
        if (m != null) {
            SecurityUtils.ensureTenantAccess(m.getTenantId());
            List<WhWarehouseApplyEntry> entries = whWarehouseApplyMapper.selectWhWarehouseApplyEntryListByParenId(id);
            if (entries != null) {
                for (WhWarehouseApplyEntry e : entries) {
                    if (e != null && e.getMaterialId() != null) {
                        FdMaterial mat = fdMaterialMapper.selectFdMaterialById(e.getMaterialId());
                        e.setMaterial(mat);
                    }
                }
            }
            m.setEntryList(entries);
        }
        return m;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveCkEntryRefsAfterOutboundInsert(StkIoBill outboundRequest) {
        if (outboundRequest == null || outboundRequest.getId() == null) {
            return;
        }
        if (StringUtils.isEmpty(outboundRequest.getWhWarehouseApplyId())) {
            return;
        }
        List<StkIoBillEntry> reqEntries = outboundRequest.getStkIoBillEntryList();
        if (reqEntries == null || reqEntries.isEmpty()) {
            return;
        }
        StkIoBill loaded = stkIoBillMapper.selectStkIoBillById(outboundRequest.getId());
        if (loaded == null || loaded.getStkIoBillEntryList() == null) {
            return;
        }
        List<StkIoBillEntry> dbEntries = new ArrayList<>(loaded.getStkIoBillEntryList());
        dbEntries.sort(Comparator.comparing(StkIoBillEntry::getId, Comparator.nullsLast(Long::compareTo)));
        String tenantId = StringUtils.isNotEmpty(loaded.getTenantId()) ? loaded.getTenantId()
            : SecurityUtils.requiredScopedTenantIdForSql();
        String uid = SecurityUtils.getUserIdStr();
        Date now = DateUtils.getNowDate();
        int n = Math.min(reqEntries.size(), dbEntries.size());
        for (int i = 0; i < n; i++) {
            StkIoBillEntry req = reqEntries.get(i);
            StkIoBillEntry db = dbEntries.get(i);
            if (req == null || db == null || db.getId() == null) {
                continue;
            }
            if (StringUtils.isEmpty(req.getWhApplyEntryId())) {
                continue;
            }
            WhWhApplyCkEntryRef row = new WhWhApplyCkEntryRef();
            row.setId(UUID7.generateUUID7());
            row.setTenantId(tenantId);
            row.setWhApplyId(outboundRequest.getWhWarehouseApplyId());
            row.setWhApplyBillNo(outboundRequest.getWhWarehouseApplyBillNo());
            row.setWhApplyEntryId(req.getWhApplyEntryId());
            row.setCkBillId(String.valueOf(loaded.getId()));
            row.setCkBillNo(loaded.getBillNo());
            row.setCkEntryId(String.valueOf(db.getId()));
            row.setRefQty(db.getQty());
            row.setRefAmt(db.getAmt());
            row.setLinkStatus(1);
            row.setDelFlag(0);
            row.setCreateBy(uid);
            row.setCreateTime(now);
            whWarehouseApplyMapper.insertWhWhApplyCkEntryRef(row);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void voidWholeWhWarehouseApply(String id, String reason) {
        if (StringUtils.isEmpty(id)) {
            throw new ServiceException("库房申请单ID不能为空");
        }
        WhWarehouseApply m = whWarehouseApplyMapper.selectWhWarehouseApplyById(id);
        if (m == null) {
            throw new ServiceException("库房申请单不存在");
        }
        SecurityUtils.ensureTenantAccess(m.getTenantId());
        if (Integer.valueOf(1).equals(m.getVoidWholeFlag())) {
            throw new ServiceException("该库房申请单已作废");
        }
        int refCnt = whWarehouseApplyMapper.countActiveCkRefsByWhApplyId(id);
        if (refCnt > 0) {
            throw new ServiceException("已关联出库单，无法整单作废；请先删除或处理相关出库单后重试");
        }
        String uid = SecurityUtils.getUserIdStr();
        int u = whWarehouseApplyMapper.updateWhWarehouseApplyVoidWhole(id, uid, DateUtils.getNowDate(), reason);
        if (u <= 0) {
            throw new ServiceException("整单作废失败，请刷新后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void voidWhWarehouseApplyEntryLine(String whApplyId, String entryId, BigDecimal voidQty, String reason) {
        if (StringUtils.isEmpty(whApplyId) || StringUtils.isEmpty(entryId) || voidQty == null) {
            throw new ServiceException("参数不完整");
        }
        if (voidQty.compareTo(ZERO) <= 0) {
            throw new ServiceException("作废数量必须大于0");
        }
        WhWarehouseApply main = whWarehouseApplyMapper.selectWhWarehouseApplyById(whApplyId);
        if (main == null) {
            throw new ServiceException("库房申请单不存在");
        }
        SecurityUtils.ensureTenantAccess(main.getTenantId());
        if (Integer.valueOf(1).equals(main.getVoidWholeFlag())) {
            throw new ServiceException("主单已整单作废，不能单独作废明细");
        }
        WhWarehouseApplyEntry en = whWarehouseApplyMapper.selectWhWarehouseApplyEntryById(entryId);
        if (en == null || !whApplyId.equals(en.getParenId())) {
            throw new ServiceException("明细不属于该库房申请单");
        }
        BigDecimal qty = en.getQty() != null ? en.getQty() : ZERO;
        BigDecimal linked = whWarehouseApplyMapper.sumLinkedQtyByWhApplyEntryId(entryId);
        if (linked == null) {
            linked = ZERO;
        }
        BigDecimal already = en.getLineVoidQty() != null ? en.getLineVoidQty() : ZERO;
        BigDecimal maxFurther = qty.subtract(already).subtract(linked);
        if (maxFurther.compareTo(ZERO) <= 0) {
            throw new ServiceException("该明细已无可作废数量");
        }
        if (voidQty.compareTo(maxFurther) > 0) {
            throw new ServiceException("作废数量超过可作废上限（申请数量 - 已作废 - 已关联出库）");
        }
        BigDecimal newVoid = already.add(voidQty);
        int newStatus = qty.subtract(linked).compareTo(newVoid) <= 0 ? 1 : 0;
        String uid = SecurityUtils.getUserIdStr();
        int u = whWarehouseApplyMapper.updateWhWarehouseApplyEntryLineVoid(entryId, whApplyId, newStatus, newVoid,
            uid, DateUtils.getNowDate(), reason);
        if (u <= 0) {
            throw new ServiceException("明细作废失败，请刷新后重试");
        }
    }

    private static class AllocLine {
        Long warehouseId;
        Long materialId;
        BigDecimal qty;
        BigDecimal unitPrice;
        BigDecimal amt;
        String batchNo;
        String batchNumber;
        Date beginTime;
        Date endTime;
        Long stkInventoryId;
        Long supplierId;
        Long factoryId;
        Long basApplyEntryId;
    }
}

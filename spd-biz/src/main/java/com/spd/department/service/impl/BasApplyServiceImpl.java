package com.spd.department.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import com.spd.system.service.ITenantScopeService;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.common.utils.SecurityUtils;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.spd.department.domain.BasApplyEntry;
import com.spd.department.domain.HcKsFlow;
import com.spd.department.domain.StkDepInventory;
import com.spd.department.mapper.BasApplyMapper;
import com.spd.department.mapper.HcKsFlowMapper;
import com.spd.department.mapper.StkDepInventoryMapper;
import com.spd.warehouse.domain.HcCkFlow;
import com.spd.warehouse.domain.StkInventory;
import com.spd.warehouse.mapper.HcCkFlowMapper;
import com.spd.warehouse.mapper.StkInventoryMapper;
import com.spd.department.domain.BasApply;
import com.spd.department.service.IBasApplyService;
import com.spd.department.service.IWhWarehouseApplyService;
import com.spd.department.vo.BasApplyOutboundRefVo;

/**
 * 科室申领Service业务层处理
 *
 * @author spd
 * @date 2024-02-26
 */
@Service
public class BasApplyServiceImpl implements IBasApplyService
{
    @Autowired
    private BasApplyMapper basApplyMapper;
    @Autowired
    private FdMaterialMapper fdMaterialMapper;
    @Autowired
    private ITenantScopeService tenantScopeService;

    @Autowired
    private StkDepInventoryMapper stkDepInventoryMapper;

    @Autowired
    private HcKsFlowMapper hcKsFlowMapper;

    @Autowired
    private HcCkFlowMapper hcCkFlowMapper;

    @Autowired
    private StkInventoryMapper stkInventoryMapper;

    @Autowired
    private IWhWarehouseApplyService whWarehouseApplyService;

    /** 非租户管理员：仅能访问已授权科室的申领/转科等 bas_apply 单据 */
    private void assertDepartmentInUserScope(Long departmentId) {
        Long userId = SecurityUtils.getUserId();
        String customerId = SecurityUtils.getCustomerId();
        List<Long> deptIds = tenantScopeService.resolveDepartmentScope(userId, customerId);
        if (deptIds == null) {
            return;
        }
        if (departmentId == null || deptIds.isEmpty() || !deptIds.contains(departmentId)) {
            throw new ServiceException("无权操作该科室的申领单");
        }
    }

    private void assertBasApplyDepartmentInUserScope(BasApply basApply) {
        if (basApply == null) {
            return;
        }
        // 转科：调出 warehouseId、调入 departmentId 均需授权，防串科室
        if (basApply.getBillType() != null && basApply.getBillType() == 3) {
            assertDepartmentInUserScope(basApply.getWarehouseId());
            assertDepartmentInUserScope(basApply.getDepartmentId());
        } else {
            assertDepartmentInUserScope(basApply.getDepartmentId());
        }
    }

    @Override
    public void applyDepartmentScopeToQuery(BasApply basApply) {
        if (basApply == null) {
            return;
        }
        tenantScopeService.applyDepartmentScopeQueryParams(
            basApply.getParams(), SecurityUtils.getUserId(), SecurityUtils.getCustomerId());
    }

    /**
     * 查询科室申领
     *
     * @param id 科室申领主键
     * @return 科室申领
     */
    @Override
    public BasApply selectBasApplyById(Long id)
    {
        BasApply basApply = basApplyMapper.selectBasApplyById(id);
        if (basApply == null) {
            return null;
        }
        SecurityUtils.ensureTenantAccess(basApply.getTenantId());
        assertBasApplyDepartmentInUserScope(basApply);
        List<BasApplyEntry> basApplyEntryList = basApply.getBasApplyEntryList();
        if (basApplyEntryList != null) {
            for (BasApplyEntry basApplyEntry : basApplyEntryList) {
                if (basApplyEntry.getMaterialId() != null) {
                    FdMaterial material = this.fdMaterialMapper.selectFdMaterialById(basApplyEntry.getMaterialId());
                    basApplyEntry.setMaterial(material);
                }
            }
        }
        List<BasApplyOutboundRefVo> outboundRefs = basApplyMapper.selectBasApplyOutboundRefsByBasApplyId(id);
        basApply.setOutboundRefList(outboundRefs != null ? outboundRefs : Collections.emptyList());
        return basApply;
    }

    /**
     * 查询科室申领列表
     *
     * @param basApply 科室申领
     * @return 科室申领
     */
    @Override
    public List<BasApply> selectBasApplyList(BasApply basApply)
    {
        if (basApply != null && StringUtils.isEmpty(basApply.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            basApply.setTenantId(SecurityUtils.getCustomerId());
        }
        return basApplyMapper.selectBasApplyList(basApply);
    }

    /**
     * 校验明细数量；申领单（billType=1）还须填写可用库存所属仓库，供审核按仓拆分、避免串库。
     */
    private void validateEntryQty(BasApply basApply) {
        List<BasApplyEntry> list = basApply == null ? null : basApply.getBasApplyEntryList();
        if (list == null) {
            return;
        }
        for (BasApplyEntry e : list) {
            if (e == null) {
                continue;
            }
            if (e.getMaterialId() != null && (e.getQty() == null || e.getQty().compareTo(BigDecimal.ZERO) <= 0)) {
                throw new ServiceException("科室申领单明细中数量不能为空且必须大于0，请检查后保存。");
            }
            if (deptApplyConsumableRequiresStockWarehouse(basApply)
                && e.getMaterialId() != null
                && e.getStockWarehouseId() == null) {
                throw new ServiceException("科室申领明细须指定可用库存所属仓库（请从按仓库汇总的可选行添加），保存或审核前请补全。");
            }
        }
    }

    /** billType=1：明细绑定 stock_warehouse_id；2 申购、3 转科不要求 */
    private static boolean deptApplyConsumableRequiresStockWarehouse(BasApply basApply) {
        if (basApply == null) {
            return false;
        }
        Integer t = basApply.getBillType();
        return t != null && t == 1;
    }

    /**
     * 转科申请（billType=3）：已存在有效明细时，不允许修改调出科室（warehouseId）、调入科室（departmentId）。
     */
    private void assertTransferDepartmentsImmutableWhenDetailsExist(BasApply incoming, BasApply persisted)
    {
        if (incoming == null || persisted == null)
        {
            return;
        }
        Integer bt = incoming.getBillType() != null ? incoming.getBillType() : persisted.getBillType();
        if (bt == null || bt != 3)
        {
            return;
        }
        List<BasApplyEntry> oldList = persisted.getBasApplyEntryList();
        if (oldList == null || oldList.isEmpty())
        {
            return;
        }
        boolean hasDetail = false;
        for (BasApplyEntry e : oldList)
        {
            if (e != null && e.getMaterialId() != null)
            {
                hasDetail = true;
                break;
            }
        }
        if (!hasDetail)
        {
            return;
        }
        // 本次保存将清空全部明细时，允许同时调整调出/调入科室
        List<BasApplyEntry> incList = incoming.getBasApplyEntryList();
        if (incList == null || incList.isEmpty())
        {
            return;
        }
        boolean incomingKeepsMaterialLine = false;
        for (BasApplyEntry e : incList)
        {
            if (e != null && e.getMaterialId() != null)
            {
                incomingKeepsMaterialLine = true;
                break;
            }
        }
        if (!incomingKeepsMaterialLine)
        {
            return;
        }
        if (!Objects.equals(persisted.getWarehouseId(), incoming.getWarehouseId())
                || !Objects.equals(persisted.getDepartmentId(), incoming.getDepartmentId()))
        {
            throw new ServiceException("转科申请已有明细，不允许修改调出科室或调入科室");
        }
    }

    /**
     * 新增科室申领
     *
     * @param basApply 科室申领
     * @return 结果
     */
    @Transactional
    @Override
    public int insertBasApply(BasApply basApply)
    {
        if (basApply == null) {
            throw new ServiceException("科室申领数据不能为空");
        }
        if (basApply.getDepartmentId() == null) {
            throw new ServiceException("科室不能为空，请先选择科室");
        }
        assertDepartmentInUserScope(basApply.getDepartmentId());
        if (basApply.getBillType() != null && basApply.getBillType() == 3)
        {
            if (basApply.getWarehouseId() == null)
            {
                throw new ServiceException("转出科室不能为空，请先选择调出科室");
            }
            assertDepartmentInUserScope(basApply.getWarehouseId());
        }
        validateEntryQty(basApply);
        if (StringUtils.isEmpty(basApply.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            basApply.setTenantId(SecurityUtils.getCustomerId());
        }
        basApply.setCreateTime(DateUtils.getNowDate());
        if (basApply.getBillType() == null) {
            basApply.setBillType(1);
        }
        if (basApply.getBillType() != null && basApply.getBillType() == 1) {
            basApply.setWarehouseId(null);
            basApply.setCreateBy(SecurityUtils.getUserIdStr());
        } else if (StringUtils.isEmpty(basApply.getCreateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            basApply.setCreateBy(SecurityUtils.getUserIdStr());
        }
        basApply.setApplyBillNo(getNumber(basApply.getBillType()));
        int rows = basApplyMapper.insertBasApply(basApply);
        insertBasApplyEntry(basApply);
        return rows;
    }

    //str：单号前缀
    //date：日期
    //result：最终结果，需要的流水号
    public String getNumber(Integer billType) {
        String str = "SL"; // 默认申领单前缀
        if (billType != null) {
            if (billType == 2) {
                str = "SG"; // 申购单前缀
            } else if (billType == 3) {
                str = "ZK"; // 转科申请单前缀
            }
        }
        String date = FillRuleUtil.getDateNum();
        String maxNum = basApplyMapper.selectMaxBillNo(date);
        String result = FillRuleUtil.getNumber(str,maxNum,date);
        return result;
    }

    /**
     * 修改科室申领
     *
     * @param basApply 科室申领
     * @return 结果
     */
    @Transactional
    @Override
    public int updateBasApply(BasApply basApply)
    {
        if (basApply == null) {
            throw new ServiceException("科室申领数据不能为空");
        }
        if (basApply.getDepartmentId() == null) {
            throw new ServiceException("科室不能为空，请先选择科室");
        }
        BasApply existing = basApplyMapper.selectBasApplyById(basApply.getId());
        if (existing != null) {
            SecurityUtils.ensureTenantAccess(existing.getTenantId());
            assertBasApplyDepartmentInUserScope(existing);
        }
        assertDepartmentInUserScope(basApply.getDepartmentId());
        if (basApply.getBillType() == null && existing != null) {
            basApply.setBillType(existing.getBillType());
        }
        Integer billType = basApply.getBillType() != null ? basApply.getBillType()
            : (existing != null ? existing.getBillType() : null);
        if (billType != null && billType == 3 && basApply.getWarehouseId() != null)
        {
            assertDepartmentInUserScope(basApply.getWarehouseId());
        }
        if (existing != null)
        {
            assertTransferDepartmentsImmutableWhenDetailsExist(basApply, existing);
        }
        validateEntryQty(basApply);
        if (existing != null) {
            basApply.setCreateBy(existing.getCreateBy());
            basApply.setCreateTime(existing.getCreateTime());
        }
        if (billType != null && billType == 1) {
            basApply.setWarehouseId(null);
            basApply.getParams().put("clearDeptApplyHeaderWarehouse", Boolean.TRUE);
        }
        basApply.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(basApply.getUpdateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            basApply.setUpdateBy(SecurityUtils.getUserIdStr());
        }
        String deleteBy = SecurityUtils.getUserIdStr();
        basApplyMapper.deleteBasApplyEntryByParenId(basApply.getId(), deleteBy);
        insertBasApplyEntry(basApply);
        return basApplyMapper.updateBasApply(basApply);
    }

    /**
     * 批量删除科室申领
     *
     * @param ids 需要删除的科室申领主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteBasApplyByIds(Long[] ids)
    {
        for (Long id : ids) {
            BasApply existing = basApplyMapper.selectBasApplyById(id);
            if (existing != null) {
                SecurityUtils.ensureTenantAccess(existing.getTenantId());
                assertBasApplyDepartmentInUserScope(existing);
            }
        }
        String deleteBy = SecurityUtils.getUserIdStr();
        basApplyMapper.deleteBasApplyEntryByParenIds(ids, deleteBy);
        return basApplyMapper.deleteBasApplyByIds(ids, deleteBy);
    }

    /**
     * 删除科室申领信息
     *
     * @param id 科室申领主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteBasApplyById(Long id)
    {
        BasApply existing = basApplyMapper.selectBasApplyById(id);
        if (existing != null) {
            SecurityUtils.ensureTenantAccess(existing.getTenantId());
            assertBasApplyDepartmentInUserScope(existing);
        }
        String deleteBy = SecurityUtils.getUserIdStr();
        basApplyMapper.deleteBasApplyEntryByParenId(id, deleteBy);
        return basApplyMapper.deleteBasApplyById(id, deleteBy);
    }

    /**
     * 审核科室申领
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int auditApply(String id, String auditBy) {
        BasApply basApply = basApplyMapper.selectBasApplyById(Long.parseLong(id));
        if (basApply == null) {
            throw new ServiceException(String.format("科室申领ID：%s，不存在!", id));
        }
        SecurityUtils.ensureTenantAccess(basApply.getTenantId());
        assertBasApplyDepartmentInUserScope(basApply);
        if (basApply.getApplyBillStatus() == null || basApply.getApplyBillStatus() != 1) {
            throw new ServiceException("只有待审核状态(1)的科室申领可审核，当前状态：" + basApply.getApplyBillStatus());
        }
        validateEntryQty(basApply);
        basApply.setApplyBillStatus(2);//已审核状态
        basApply.setAuditBy(auditBy);
        basApply.setAuditDate(new Date());
        int res = basApplyMapper.updateBasApply(basApply);
        int effectiveBillType = basApply.getBillType() != null ? basApply.getBillType() : 1;
        if (res > 0 && basApply.getBillType() != null && basApply.getBillType() == 3) {
            executeDepartmentTransferInventory(basApply);
        }
        if (res > 0 && effectiveBillType == 1) {
            whWarehouseApplyService.generateFromDeptApplyAfterAudit(basApply);
        }
        return res;
    }

    /**
     * 转科审核通过后：调出科室库存扣减、调入科室库存增加，并写入科室流水（科室转出 / 科室转入）。
     */
    private void executeDepartmentTransferInventory(BasApply basApply) {
        Long outDeptId = basApply.getWarehouseId();
        Long inDeptId = basApply.getDepartmentId();
        if (outDeptId == null || inDeptId == null) {
            throw new ServiceException("转科单调出科室或调入科室不能为空");
        }
        if (outDeptId.equals(inDeptId)) {
            throw new ServiceException("调出科室与调入科室不能相同");
        }
        String tenantId = StringUtils.isNotEmpty(basApply.getTenantId())
            ? basApply.getTenantId() : SecurityUtils.requiredScopedTenantIdForSql();
        List<BasApplyEntry> entries = basApply.getBasApplyEntryList();
        if (entries == null || entries.isEmpty()) {
            return;
        }
        String uid = SecurityUtils.getUserIdStr();
        Date now = new Date();
        for (BasApplyEntry e : entries) {
            if (e == null || e.getQty() == null || e.getQty().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            if (StringUtils.isEmpty(e.getBatchNo()) || e.getMaterialId() == null) {
                throw new ServiceException("转科明细批次号、耗材不能为空");
            }
            BigDecimal need = e.getQty();
            StkDepInventory q = new StkDepInventory();
            q.setTenantId(tenantId);
            q.setDepartmentId(outDeptId);
            q.setBatchNo(e.getBatchNo());
            q.setMaterialId(e.getMaterialId());
            q.setReceiptConfirmStatus(1);
            List<StkDepInventory> candidates = stkDepInventoryMapper.selectStkDepInventoryList(q);
            if (candidates == null) {
                candidates = new ArrayList<>();
            }
            StkDepInventory src = null;
            for (StkDepInventory row : candidates) {
                if (row.getQty() != null && row.getQty().compareTo(need) >= 0) {
                    src = row;
                    break;
                }
            }
            if (src == null) {
                throw new ServiceException(String.format(
                    "转科库存不足：批次[%s] 耗材ID[%s] 在调出科室可用量不足 %s",
                    e.getBatchNo(), e.getMaterialId(), need));
            }
            BigDecimal srcQty = src.getQty();
            BigDecimal newSrcQty = srcQty.subtract(need);
            BigDecimal unitPrice = src.getUnitPrice() != null ? src.getUnitPrice() : BigDecimal.ZERO;
            src.setQty(newSrcQty);
            src.setAmt(unitPrice.multiply(newSrcQty));
            src.setUpdateTime(now);
            src.setUpdateBy(uid);
            stkDepInventoryMapper.updateStkDepInventory(src);

            StkDepInventory tq = new StkDepInventory();
            tq.setTenantId(tenantId);
            tq.setDepartmentId(inDeptId);
            tq.setBatchNo(e.getBatchNo());
            tq.setMaterialId(e.getMaterialId());
            List<StkDepInventory> tgtList = stkDepInventoryMapper.selectStkDepInventoryList(tq);
            StkDepInventory tgtExisting = (tgtList != null && !tgtList.isEmpty()) ? tgtList.get(0) : null;

            if (tgtExisting != null) {
                BigDecimal tq0 = tgtExisting.getQty() != null ? tgtExisting.getQty() : BigDecimal.ZERO;
                BigDecimal up = tgtExisting.getUnitPrice() != null ? tgtExisting.getUnitPrice() : unitPrice;
                tgtExisting.setQty(tq0.add(need));
                tgtExisting.setAmt(up.multiply(tgtExisting.getQty()));
                tgtExisting.setUpdateTime(now);
                tgtExisting.setUpdateBy(uid);
                stkDepInventoryMapper.updateStkDepInventory(tgtExisting);
            } else {
                StkDepInventory ins = new StkDepInventory();
                ins.setTenantId(tenantId);
                ins.setMaterialId(src.getMaterialId());
                ins.setDepartmentId(inDeptId);
                ins.setWarehouseId(src.getWarehouseId());
                ins.setQty(need);
                ins.setUnitPrice(unitPrice);
                ins.setAmt(unitPrice.multiply(need));
                ins.setBatchNo(src.getBatchNo());
                ins.setBatchId(src.getBatchId());
                ins.setMaterialNo(src.getMaterialNo());
                ins.setMaterialDate(src.getMaterialDate());
                ins.setWarehouseDate(now);
                ins.setSupplierId(src.getSupplierId());
                ins.setBeginDate(src.getBeginDate());
                ins.setEndDate(src.getEndDate());
                ins.setFactoryId(src.getFactoryId());
                ins.setBatchNumber(src.getBatchNumber());
                ins.setMainBarcode(src.getMainBarcode());
                ins.setSubBarcode(src.getSubBarcode());
                ins.setReceiptConfirmStatus(1);
                ins.setSnapMaterialName(src.getSnapMaterialName());
                ins.setSnapMaterialSpeci(src.getSnapMaterialSpeci());
                ins.setSnapMaterialModel(src.getSnapMaterialModel());
                ins.setSnapMaterialFactoryId(src.getSnapMaterialFactoryId());
                ins.setSettlementType(src.getSettlementType());
                ins.setCreateTime(now);
                ins.setCreateBy(uid);
                stkDepInventoryMapper.insertStkDepInventory(ins);
                tgtExisting = ins;
            }

            insertKsTransferFlow(basApply, e, src, outDeptId, need, unitPrice, "科室转出", "KSZC");
            insertKsTransferFlow(basApply, e, tgtExisting, inDeptId, need, unitPrice, "科室转入", "KSZR");
            insertHcCkTransferPairForWarehouse(basApply, e, src, need, unitPrice);
        }
    }

    /**
     * 按科室库存明细的归属仓库写入仓库流水（KSZC+KSZR 成对），不扣减/增加 stk_inventory。
     * lx 使用 KSZC/KSZR，与仓库间调拨的 ZC/ZR 区分。
     */
    private void insertHcCkTransferPairForWarehouse(BasApply basApply, BasApplyEntry entry,
        StkDepInventory srcRow, BigDecimal qty, BigDecimal deptUnitPrice) {
        if (srcRow == null || srcRow.getWarehouseId() == null) {
            throw new ServiceException("转科明细归属仓库不能为空，无法生成仓库流水");
        }
        Long whId = srcRow.getWarehouseId();
        StkInventory whInv = stkInventoryMapper.selectStkInventoryByBatchNoAndMaterialIdAndWarehouse(
            entry.getBatchNo(), entry.getMaterialId(), whId);
        if (whInv == null) {
            whInv = stkInventoryMapper.selectStkInventoryByBatchNoAndWarehouse(entry.getBatchNo(), whId);
        }
        if (whInv == null) {
            StkInventory one = stkInventoryMapper.selectStkInventoryOne(entry.getBatchNo());
            if (one != null && whId.equals(one.getWarehouseId())) {
                whInv = one;
            }
        }
        BigDecimal flowUnitPrice = deptUnitPrice;
        if (whInv != null && whInv.getUnitPrice() != null) {
            flowUnitPrice = whInv.getUnitPrice();
        }
        BigDecimal amt = flowUnitPrice != null && qty != null ? qty.multiply(flowUnitPrice) : BigDecimal.ZERO;
        Long supplierId = resolveCkSupplierId(srcRow, whInv);
        Long factoryId = resolveCkFactoryId(srcRow, whInv);
        Long batchId = whInv != null && whInv.getBatchId() != null ? whInv.getBatchId() : srcRow.getBatchId();
        Long kcNo = whInv != null ? whInv.getId() : null;
        String mainBc = whInv != null && StringUtils.isNotEmpty(whInv.getMainBarcode())
            ? whInv.getMainBarcode() : srcRow.getMainBarcode();
        String subBc = whInv != null && StringUtils.isNotEmpty(whInv.getSubBarcode())
            ? whInv.getSubBarcode() : srcRow.getSubBarcode();
        Date beginT = whInv != null ? whInv.getBeginTime() : srcRow.getBeginDate();
        Date endT = whInv != null ? whInv.getEndTime() : srcRow.getEndDate();
        String tenantId = StringUtils.isNotEmpty(basApply.getTenantId())
            ? basApply.getTenantId() : SecurityUtils.getCustomerId();
        Date flowTime = new Date();
        String uid = SecurityUtils.getUserIdStr();

        HcCkFlow zc = new HcCkFlow();
        zc.setBillId(basApply.getId());
        zc.setEntryId(entry.getId());
        zc.setWarehouseId(whId);
        zc.setMaterialId(entry.getMaterialId());
        zc.setBatchNo(entry.getBatchNo());
        zc.setBatchNumber(entry.getBatchNumer());
        zc.setQty(qty);
        zc.setUnitPrice(flowUnitPrice);
        zc.setAmt(amt);
        zc.setBeginTime(beginT);
        zc.setEndTime(endT);
        zc.setSupplierId(supplierId);
        zc.setFactoryId(factoryId);
        zc.setMainBarcode(mainBc);
        zc.setSubBarcode(subBc);
        zc.setLx("KSZC");
        zc.setBatchId(batchId);
        zc.setOriginBusinessType("科室转科仓库转出");
        zc.setKcNo(kcNo);
        zc.setFlowTime(flowTime);
        zc.setDelFlag(0);
        zc.setCreateTime(flowTime);
        zc.setCreateBy(uid);
        zc.setTenantId(tenantId);
        hcCkFlowMapper.insertHcCkFlow(zc);

        HcCkFlow zr = new HcCkFlow();
        zr.setBillId(basApply.getId());
        zr.setEntryId(entry.getId());
        zr.setWarehouseId(whId);
        zr.setMaterialId(entry.getMaterialId());
        zr.setBatchNo(entry.getBatchNo());
        zr.setBatchNumber(entry.getBatchNumer());
        zr.setQty(qty);
        zr.setUnitPrice(flowUnitPrice);
        zr.setAmt(amt);
        zr.setBeginTime(beginT);
        zr.setEndTime(endT);
        zr.setSupplierId(supplierId);
        zr.setFactoryId(factoryId);
        zr.setMainBarcode(mainBc);
        zr.setSubBarcode(subBc);
        zr.setLx("KSZR");
        zr.setBatchId(batchId);
        zr.setOriginBusinessType("科室转科仓库转入");
        zr.setKcNo(kcNo);
        zr.setFlowTime(flowTime);
        zr.setDelFlag(0);
        zr.setCreateTime(flowTime);
        zr.setCreateBy(uid);
        zr.setTenantId(tenantId);
        hcCkFlowMapper.insertHcCkFlow(zr);
    }

    private static Long resolveCkSupplierId(StkDepInventory depRow, StkInventory whInv) {
        if (whInv != null && whInv.getSupplierId() != null) {
            return whInv.getSupplierId();
        }
        if (depRow != null && StringUtils.isNotEmpty(depRow.getSupplierId())) {
            try {
                return Long.parseLong(depRow.getSupplierId().trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private static Long resolveCkFactoryId(StkDepInventory depRow, StkInventory whInv) {
        if (whInv != null && whInv.getFactoryId() != null) {
            return whInv.getFactoryId();
        }
        return depRow != null ? depRow.getFactoryId() : null;
    }

    private void insertKsTransferFlow(BasApply basApply, BasApplyEntry entry, StkDepInventory depRow,
        Long departmentId, BigDecimal qty, BigDecimal unitPrice, String originBiz, String lx) {
        HcKsFlow flow = new HcKsFlow();
        flow.setBillId(basApply.getId());
        flow.setEntryId(entry.getId());
        flow.setDepartmentId(departmentId);
        flow.setWarehouseId(depRow != null ? depRow.getWarehouseId() : null);
        flow.setMaterialId(entry.getMaterialId());
        flow.setBatchNo(entry.getBatchNo());
        flow.setBatchNumber(entry.getBatchNumer());
        flow.setBatchId(depRow != null ? depRow.getBatchId() : null);
        flow.setQty(qty);
        flow.setUnitPrice(unitPrice);
        flow.setAmt(unitPrice != null ? qty.multiply(unitPrice) : null);
        flow.setBeginTime(depRow != null ? depRow.getBeginDate() : null);
        flow.setEndTime(depRow != null ? depRow.getEndDate() : null);
        flow.setSupplierId(depRow != null ? depRow.getSupplierId() : null);
        flow.setFactoryId(depRow != null ? depRow.getFactoryId() : null);
        flow.setMainBarcode(depRow != null ? depRow.getMainBarcode() : null);
        flow.setSubBarcode(depRow != null ? depRow.getSubBarcode() : null);
        flow.setKcNo(depRow != null ? depRow.getId() : null);
        flow.setLx(lx);
        flow.setOriginBusinessType(originBiz);
        flow.setFlowTime(new Date());
        flow.setDelFlag(0);
        flow.setCreateTime(new Date());
        flow.setCreateBy(SecurityUtils.getUserIdStr());
        flow.setTenantId(StringUtils.isNotEmpty(basApply.getTenantId()) ? basApply.getTenantId() : SecurityUtils.getCustomerId());
        hcKsFlowMapper.insertHcKsFlow(flow);
    }

    /**
     * 驳回科室申领
     * 
     * @param id 科室申领主键
     * @param rejectReason 驳回原因
     * @return 结果
     */
    @Override
    public int rejectApply(String id, String rejectReason) {
        BasApply basApply = basApplyMapper.selectBasApplyById(Long.parseLong(id));
        if (basApply == null) {
            throw new ServiceException(String.format("科室申领ID：%s，不存在!", id));
        }
        SecurityUtils.ensureTenantAccess(basApply.getTenantId());
        assertBasApplyDepartmentInUserScope(basApply);
        if (basApply.getApplyBillStatus() == null || basApply.getApplyBillStatus() != 1) {
            throw new ServiceException("只有待审核状态(1)的科室申领可驳回，当前状态：" + basApply.getApplyBillStatus());
        }
        basApply.setRejectReason(rejectReason);
        basApply.setUpdateBy(SecurityUtils.getUserIdStr());
        basApply.setAuditBy(String.valueOf(SecurityUtils.getUserId()));
        basApply.setAuditDate(new Date());
        basApply.setUpdateTime(new Date());
        int res = basApplyMapper.updateBasApply(basApply);
        return res;
    }

    /**
     * 新增科室申领明细信息
     *
     * @param basApply 科室申领对象
     */
    public void insertBasApplyEntry(BasApply basApply)
    {
        List<BasApplyEntry> basApplyEntryList = basApply.getBasApplyEntryList();
        Long id = basApply.getId();
        if (id == null) {
            return;
        }
        // 批量写入 mapper 依赖 item.tenantId，必须严格解析且不允许为空
        String tenantId = StringUtils.isNotEmpty(basApply.getTenantId())
            ? basApply.getTenantId()
            : SecurityUtils.requiredScopedTenantIdForSql();
        if (StringUtils.isNotNull(basApplyEntryList) && !basApplyEntryList.isEmpty())
        {
            List<BasApplyEntry> list = new ArrayList<BasApplyEntry>();
            for (BasApplyEntry basApplyEntry : basApplyEntryList)
            {
                if (basApplyEntry.getMaterialId() == null) {
                    continue;
                }
                basApplyEntry.setParenId(id);
                basApplyEntry.setTenantId(tenantId);
                list.add(basApplyEntry);
            }
            if (!list.isEmpty())
            {
                basApplyMapper.batchBasApplyEntry(list);
            }
        }
    }
}

package com.spd.department.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;
import com.spd.system.service.ITenantScopeService;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.MasterDetailValidateUtil;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.spd.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.spd.department.domain.DepPurchaseApplyEntry;
import com.spd.department.domain.DepPurApplyCkEntryRef;
import com.spd.department.mapper.DepPurchaseApplyMapper;
import com.spd.department.domain.DepPurchaseApply;
import com.spd.department.service.IDepPurchaseApplyService;
import com.spd.department.vo.WarehousePurchaseReminderRowVo;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.domain.StkIoBillEntry;
import com.spd.warehouse.mapper.StkIoBillMapper;
import com.spd.caigou.mapper.PurchasePlanEntryDepApplyMapper;

/**
 * 科室申购Service业务层处理
 * 
 * @author spd
 * @date 2025-01-01
 */
@Service
public class DepPurchaseApplyServiceImpl implements IDepPurchaseApplyService 
{
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    @Autowired
    private DepPurchaseApplyMapper depPurchaseApplyMapper;

    @Autowired
    private ITenantScopeService tenantScopeService;

    @Autowired
    private StkIoBillMapper stkIoBillMapper;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    @Autowired
    private PurchasePlanEntryDepApplyMapper purchasePlanEntryDepApplyMapper;

    /** 非租户管理员：仅能访问已授权科室的科室申购单 */
    private void assertDepartmentInUserScope(Long departmentId) {
        Long userId = SecurityUtils.getUserId();
        String customerId = SecurityUtils.getCustomerId();
        List<Long> deptIds = tenantScopeService.resolveDepartmentScope(userId, customerId);
        if (deptIds == null) {
            return;
        }
        if (departmentId == null || deptIds.isEmpty() || !deptIds.contains(departmentId)) {
            throw new ServiceException("无权操作该科室的申购单");
        }
    }

    private void assertDepPurchaseDepartmentInUserScope(DepPurchaseApply a) {
        if (a == null) {
            return;
        }
        assertDepartmentInUserScope(a.getDepartmentId());
    }

    @Override
    public void applyDepartmentScopeToQuery(DepPurchaseApply depPurchaseApply) {
        if (depPurchaseApply == null) {
            return;
        }
        tenantScopeService.applyDepartmentScopeQueryParams(
            depPurchaseApply.getParams(), SecurityUtils.getUserId(), SecurityUtils.getCustomerId());
    }

    /**
     * 查询科室申购
     * 
     * @param id 科室申购主键
     * @return 科室申购
     */
    @Override
    public DepPurchaseApply selectDepPurchaseApplyById(Long id)
    {
        DepPurchaseApply a = depPurchaseApplyMapper.selectDepPurchaseApplyById(id);
        if (a != null) {
            SecurityUtils.ensureTenantAccess(a.getTenantId());
            assertDepPurchaseDepartmentInUserScope(a);
        }
        return a;
    }

    /**
     * 查询科室申购列表
     * 
     * @param depPurchaseApply 科室申购
     * @return 科室申购
     */
    @Override
    public List<DepPurchaseApply> selectDepPurchaseApplyList(DepPurchaseApply depPurchaseApply)
    {
        if (depPurchaseApply != null && StringUtils.isEmpty(depPurchaseApply.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            depPurchaseApply.setTenantId(SecurityUtils.getCustomerId());
        }
        applyExcludePurchaseBillNos(depPurchaseApply);
        return depPurchaseApplyMapper.selectDepPurchaseApplyList(depPurchaseApply);
    }

    /** 解析 excludePurchaseBillNos 为列表供 Mapper 使用 */
    private void applyExcludePurchaseBillNos(DepPurchaseApply depPurchaseApply) {
        if (depPurchaseApply == null || StringUtils.isEmpty(depPurchaseApply.getExcludePurchaseBillNos())) {
            return;
        }
        String[] parts = depPurchaseApply.getExcludePurchaseBillNos().split("[,，]");
        java.util.List<String> list = new java.util.ArrayList<>();
        for (String p : parts) {
            if (StringUtils.isNotEmpty(p) && StringUtils.isNotEmpty(p.trim())) {
                list.add(p.trim());
            }
        }
        if (!list.isEmpty()) {
            depPurchaseApply.setExcludePurchaseBillNoList(list);
        }
    }

    @Override
    public BigDecimal selectDepPurchaseApplyEntryQtySum(DepPurchaseApply depPurchaseApply)
    {
        if (depPurchaseApply != null && StringUtils.isEmpty(depPurchaseApply.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            depPurchaseApply.setTenantId(SecurityUtils.getCustomerId());
        }
        BigDecimal v = depPurchaseApplyMapper.selectDepPurchaseApplyEntryQtySum(depPurchaseApply);
        return v != null ? v : BigDecimal.ZERO;
    }

    @Override
    public long countPendingAuditPurchaseApply()
    {
        DepPurchaseApply depPurchaseApply = new DepPurchaseApply();
        depPurchaseApply.setPurchaseBillStatus(1);
        applyDepartmentScopeToQuery(depPurchaseApply);
        if (StringUtils.isEmpty(depPurchaseApply.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            depPurchaseApply.setTenantId(SecurityUtils.getCustomerId());
        }
        return depPurchaseApplyMapper.selectDepPurchaseApplyBillCount(depPurchaseApply);
    }

    @Override
    public List<WarehousePurchaseReminderRowVo> selectWarehouseReminderPurchaseMonitorList()
    {
        DepPurchaseApply q = new DepPurchaseApply();
        applyDepartmentScopeToQuery(q);
        if (StringUtils.isEmpty(q.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            q.setTenantId(SecurityUtils.getCustomerId());
        }
        return depPurchaseApplyMapper.selectWarehouseReminderPurchaseMonitorList(q);
    }

    /**
     * 新增科室申购
     * 
     * @param depPurchaseApply 科室申购
     * @return 结果
     */
    /** 校验明细数量：有耗材的明细数量不能为空且必须大于0 */
    private void validateEntryQty(List<DepPurchaseApplyEntry> list) {
        if (list == null) return;
        for (DepPurchaseApplyEntry e : list) {
            if (e.getMaterialId() != null && (e.getQty() == null || e.getQty().compareTo(BigDecimal.ZERO) <= 0)) {
                throw new ServiceException("科室申购单明细中数量不能为空且必须大于0，请检查后保存。");
            }
        }
    }

    @Transactional
    @Override
    public int insertDepPurchaseApply(DepPurchaseApply depPurchaseApply)
    {
        if (depPurchaseApply != null && depPurchaseApply.getDepartmentId() != null) {
            assertDepartmentInUserScope(depPurchaseApply.getDepartmentId());
        }
        MasterDetailValidateUtil.assertHasMaterialLine(
            depPurchaseApply.getDepPurchaseApplyEntryList(), DepPurchaseApplyEntry::getMaterialId, "科室申购");
        validateEntryQty(depPurchaseApply.getDepPurchaseApplyEntryList());
        // 生成申购单号
        if (StringUtils.isEmpty(depPurchaseApply.getPurchaseBillNo())) {
            depPurchaseApply.setPurchaseBillNo(generatePurchaseBillNo());
        }
        
        depPurchaseApply.setCreateTime(DateUtils.getNowDate());
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId != null) {
            depPurchaseApply.setUserId(currentUserId);
        }
        if (StringUtils.isEmpty(depPurchaseApply.getCreateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            depPurchaseApply.setCreateBy(SecurityUtils.getUserIdStr());
        }
        if (StringUtils.isEmpty(depPurchaseApply.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            depPurchaseApply.setTenantId(SecurityUtils.getCustomerId());
        }
        int rows = depPurchaseApplyMapper.insertDepPurchaseApply(depPurchaseApply);
        insertDepPurchaseApplyEntry(depPurchaseApply);
        return rows;
    }

    /**
     * 修改科室申购
     * 
     * @param depPurchaseApply 科室申购
     * @return 结果
     */
    @Transactional
    @Override
    public int updateDepPurchaseApply(DepPurchaseApply depPurchaseApply)
    {
        DepPurchaseApply existing = depPurchaseApplyMapper.selectDepPurchaseApplyById(depPurchaseApply.getId());
        if (existing != null) {
            SecurityUtils.ensureTenantAccess(existing.getTenantId());
            assertDepPurchaseDepartmentInUserScope(existing);
        }
        if (depPurchaseApply.getDepartmentId() != null) {
            assertDepartmentInUserScope(depPurchaseApply.getDepartmentId());
        }
        MasterDetailValidateUtil.assertHasMaterialLine(
            depPurchaseApply.getDepPurchaseApplyEntryList(), DepPurchaseApplyEntry::getMaterialId, "科室申购");
        validateEntryQty(depPurchaseApply.getDepPurchaseApplyEntryList());
        depPurchaseApply.setUpdateTime(DateUtils.getNowDate());
        depPurchaseApply.setUpdateBy(SecurityUtils.getUserIdStr());
        if (depPurchaseApply.getUserId() == null && existing != null && existing.getUserId() != null) {
            depPurchaseApply.setUserId(existing.getUserId());
        }
        if (StringUtils.isEmpty(depPurchaseApply.getPurchaseBillNo()) && existing != null) {
            depPurchaseApply.setPurchaseBillNo(existing.getPurchaseBillNo());
        }

        depPurchaseApplyMapper.deleteDepPurchaseApplyEntryByParentId(depPurchaseApply.getId(), com.spd.common.utils.SecurityUtils.getUserIdStr());
        insertDepPurchaseApplyEntry(depPurchaseApply);
        return depPurchaseApplyMapper.updateDepPurchaseApply(depPurchaseApply);
    }

    /**
     * 批量删除科室申购
     * 
     * @param ids 需要删除的科室申购主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteDepPurchaseApplyByIds(Long[] ids)
    {
        for (Long id : ids) {
            DepPurchaseApply existing = depPurchaseApplyMapper.selectDepPurchaseApplyById(id);
            if (existing != null) {
                SecurityUtils.ensureTenantAccess(existing.getTenantId());
                assertDepPurchaseDepartmentInUserScope(existing);
            }
        }
        String deleteBy = com.spd.common.utils.SecurityUtils.getUserIdStr();
        depPurchaseApplyMapper.deleteDepPurchaseApplyEntryByParentIds(ids, deleteBy);
        return depPurchaseApplyMapper.deleteDepPurchaseApplyByIds(ids, deleteBy);
    }

    /**
     * 删除科室申购信息
     * 
     * @param id 科室申购主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteDepPurchaseApplyById(Long id)
    {
        DepPurchaseApply existing = depPurchaseApplyMapper.selectDepPurchaseApplyById(id);
        if (existing != null) {
            SecurityUtils.ensureTenantAccess(existing.getTenantId());
            assertDepPurchaseDepartmentInUserScope(existing);
        }
        String deleteBy = com.spd.common.utils.SecurityUtils.getUserIdStr();
        depPurchaseApplyMapper.deleteDepPurchaseApplyEntryByParentId(id, deleteBy);
        return depPurchaseApplyMapper.deleteDepPurchaseApplyById(id, deleteBy);
    }

    /**
     * 新增科室申购明细信息
     * 
     * @param depPurchaseApply 科室申购对象
     */
    public void insertDepPurchaseApplyEntry(DepPurchaseApply depPurchaseApply)
    {
        List<DepPurchaseApplyEntry> depPurchaseApplyEntryList = depPurchaseApply.getDepPurchaseApplyEntryList();
        Long id = depPurchaseApply.getId();
        if (id == null) {
            return;
        }
        // 批量写入 mapper 依赖 item.tenantId，必须严格解析且不允许为空
        String tenantId = StringUtils.isNotEmpty(depPurchaseApply.getTenantId())
            ? depPurchaseApply.getTenantId()
            : SecurityUtils.requiredScopedTenantIdForSql();
        if (StringUtils.isNotNull(depPurchaseApplyEntryList) && !depPurchaseApplyEntryList.isEmpty())
        {
            List<DepPurchaseApplyEntry> list = new ArrayList<DepPurchaseApplyEntry>();
            for (DepPurchaseApplyEntry depPurchaseApplyEntry : depPurchaseApplyEntryList)
            {
                if (depPurchaseApplyEntry.getMaterialId() == null) {
                    continue;
                }
                depPurchaseApplyEntry.setParentId(id);
                depPurchaseApplyEntry.setTenantId(tenantId);
                if (StringUtils.isEmpty(depPurchaseApplyEntry.getPurchaseBillNo())) {
                    depPurchaseApplyEntry.setPurchaseBillNo(depPurchaseApply.getPurchaseBillNo());
                }
                depPurchaseApplyEntry.setCreateTime(DateUtils.getNowDate());
                depPurchaseApplyEntry.setCreateBy(SecurityUtils.getUserIdStr());
                list.add(depPurchaseApplyEntry);
            }
            if (!list.isEmpty())
            {
                depPurchaseApplyMapper.batchDepPurchaseApplyEntry(list);
            }
        }
    }

    /**
     * 生成申购单号
     * 
     * @return 申购单号
     */
    private String generatePurchaseBillNo() {
        String dateStr = DateUtils.dateTimeNow("yyyyMMdd");
        String prefix = "SG" + dateStr;
        
        // 简单的序号生成，实际项目中可能需要更复杂的逻辑
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
        return prefix + timestamp;
    }

    /**
     * 审核科室申购
     * 
     * @param id 科室申购主键
     * @param auditBy 审核人
     * @return 结果
     */
    @Override
    public int auditPurchaseApply(String id, String auditBy) {
        auditBy = SecurityUtils.getUserIdStr();
        DepPurchaseApply depPurchaseApply = depPurchaseApplyMapper.selectDepPurchaseApplyById(Long.parseLong(id));
        if (depPurchaseApply == null) {
            throw new ServiceException(String.format("科室申购ID：%s，不存在!", id));
        }
        SecurityUtils.ensureTenantAccess(depPurchaseApply.getTenantId());
        assertDepPurchaseDepartmentInUserScope(depPurchaseApply);
        if (depPurchaseApply.getPurchaseBillStatus() == null || depPurchaseApply.getPurchaseBillStatus() != 1) {
            throw new ServiceException("只有待审核状态(1)的科室申购可审核，当前状态：" + depPurchaseApply.getPurchaseBillStatus());
        }
        validateEntryQty(depPurchaseApply.getDepPurchaseApplyEntryList());
        depPurchaseApply.setPurchaseBillStatus(2);//已审核状态
        depPurchaseApply.setAuditBy(auditBy);
        depPurchaseApply.setAuditDate(new Date());
        depPurchaseApply.setUpdateBy(auditBy);
        depPurchaseApply.setUpdateTime(new Date());
        int res = depPurchaseApplyMapper.updateDepPurchaseApply(depPurchaseApply);
        return res;
    }

    /**
     * 驳回科室申购
     * 
     * @param id 科室申购主键
     * @param rejectReason 驳回原因
     * @return 结果
     */
    @Override
    public int rejectPurchaseApply(String id, String rejectReason) {
        DepPurchaseApply depPurchaseApply = depPurchaseApplyMapper.selectDepPurchaseApplyById(Long.parseLong(id));
        if (depPurchaseApply == null) {
            throw new ServiceException(String.format("科室申购ID：%s，不存在!", id));
        }
        SecurityUtils.ensureTenantAccess(depPurchaseApply.getTenantId());
        assertDepPurchaseDepartmentInUserScope(depPurchaseApply);
        if (depPurchaseApply.getPurchaseBillStatus() == null || depPurchaseApply.getPurchaseBillStatus() != 1) {
            throw new ServiceException("只有待审核状态(1)的科室申购可驳回，当前状态：" + depPurchaseApply.getPurchaseBillStatus());
        }
        depPurchaseApply.setPurchasePlanRefStatus(3); // 采购计划引用驳回
        depPurchaseApply.setPlanStatus(2); // 历史字段兼容
        depPurchaseApply.setRejectReason(rejectReason);
        depPurchaseApply.setUpdateBy(SecurityUtils.getUserIdStr());
        depPurchaseApply.setUpdateTime(new Date());
        int res = depPurchaseApplyMapper.updateDepPurchaseApply(depPurchaseApply);
        return res;
    }

    /**
     * 确认收货
     * 
     * @param id 科室申购主键
     * @param confirmBy 确认人
     * @return 结果
     */
    @Override
    public int confirmReceipt(String id, String confirmBy) {
        confirmBy = SecurityUtils.getUserIdStr();
        DepPurchaseApply depPurchaseApply = depPurchaseApplyMapper.selectDepPurchaseApplyById(Long.parseLong(id));
        if(depPurchaseApply == null){
            throw new ServiceException(String.format("科室申购ID：%s，不存在!", id));
        }
        SecurityUtils.ensureTenantAccess(depPurchaseApply.getTenantId());
        assertDepPurchaseDepartmentInUserScope(depPurchaseApply);
        if(depPurchaseApply.getPurchaseBillStatus() != 2){
            throw new ServiceException("只有已审核的申购单才能确认收货!");
        }
        depPurchaseApply.setReceiptStatus(1);
        depPurchaseApply.setUpdateBy(confirmBy);
        depPurchaseApply.setUpdateTime(new Date());
        int res = depPurchaseApplyMapper.updateDepPurchaseApply(depPurchaseApply);
        return res;
    }

    /**
     * 驳回收货
     * 
     * @param id 科室申购主键
     * @param rejectReason 驳回原因
     * @return 结果
     */
    @Override
    public int rejectReceipt(String id, String rejectReason) {
        DepPurchaseApply depPurchaseApply = depPurchaseApplyMapper.selectDepPurchaseApplyById(Long.parseLong(id));
        if(depPurchaseApply == null){
            throw new ServiceException(String.format("科室申购ID：%s，不存在!", id));
        }
        SecurityUtils.ensureTenantAccess(depPurchaseApply.getTenantId());
        assertDepPurchaseDepartmentInUserScope(depPurchaseApply);
        if(depPurchaseApply.getPurchaseBillStatus() != 2){
            throw new ServiceException("只有已审核的申购单才能驳回收货!");
        }
        depPurchaseApply.setReceiptStatus(2);
        depPurchaseApply.setRejectReason(rejectReason);
        depPurchaseApply.setUpdateBy(SecurityUtils.getUserIdStr());
        depPurchaseApply.setUpdateTime(new Date());
        int res = depPurchaseApplyMapper.updateDepPurchaseApply(depPurchaseApply);
        return res;
    }

    @Override
    public List<DepPurchaseApply> selectDepPurchaseApplyListForOutboundCk(DepPurchaseApply query) {
        if (query != null) {
            applyDepartmentScopeToQuery(query);
            if (StringUtils.isEmpty(query.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
                query.setTenantId(SecurityUtils.getCustomerId());
            }
        }
        return depPurchaseApplyMapper.selectDepPurchaseApplyListForOutboundCk(query);
    }

    @Override
    public DepPurchaseApply selectDepPurchaseApplyByIdForOutboundCk(Long id) {
        DepPurchaseApply m = depPurchaseApplyMapper.selectDepPurchaseApplyById(id);
        if (m != null) {
            SecurityUtils.ensureTenantAccess(m.getTenantId());
            assertDepPurchaseDepartmentInUserScope(m);
            List<DepPurchaseApplyEntry> entries = depPurchaseApplyMapper.selectDepPurchaseApplyEntryListByParentIdForCk(id);
            if (entries != null) {
                for (DepPurchaseApplyEntry e : entries) {
                    if (e != null && e.getMaterialId() != null) {
                        FdMaterial mat = fdMaterialMapper.selectFdMaterialById(e.getMaterialId());
                        e.setMaterial(mat);
                    }
                }
            }
            m.setDepPurchaseApplyEntryList(entries);
        }
        return m;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncDepPurApplyCkRefsAfterOutboundSave(Long ckBillId) {
        if (ckBillId == null) {
            return;
        }
        StkIoBill loaded = stkIoBillMapper.selectStkIoBillById(ckBillId);
        if (loaded == null) {
            return;
        }
        String tenantId = StringUtils.isNotEmpty(loaded.getTenantId())
            ? loaded.getTenantId() : SecurityUtils.requiredScopedTenantIdForSql();
        String uid = SecurityUtils.getUserIdStr();
        depPurchaseApplyMapper.softDeleteCkEntryRefsByCkBillId(String.valueOf(ckBillId), tenantId, uid);
        if (loaded.getDepPurchaseApplyId() == null) {
            return;
        }
        Integer bt = loaded.getBillType();
        if (bt == null || bt != 201) {
            return;
        }
        DepPurchaseApply dep = depPurchaseApplyMapper.selectDepPurchaseApplyById(loaded.getDepPurchaseApplyId());
        if (dep == null) {
            throw new ServiceException("出库单引用的科室申购单不存在或已删除");
        }
        SecurityUtils.ensureTenantAccess(dep.getTenantId());
        List<StkIoBillEntry> entries = loaded.getStkIoBillEntryList();
        if (entries == null) {
            return;
        }
        Date now = DateUtils.getNowDate();
        for (StkIoBillEntry db : entries) {
            if (db == null || db.getId() == null || db.getDepPurApplyEntryId() == null) {
                continue;
            }
            DepPurApplyCkEntryRef row = new DepPurApplyCkEntryRef();
            row.setTenantId(tenantId);
            row.setDepPurApplyId(loaded.getDepPurchaseApplyId());
            row.setDepPurApplyBillNo(loaded.getDepPurchaseApplyBillNo());
            row.setDepPurApplyEntryId(db.getDepPurApplyEntryId());
            row.setCkBillId(String.valueOf(loaded.getId()));
            row.setCkBillNo(loaded.getBillNo());
            row.setCkEntryId(String.valueOf(db.getId()));
            row.setRefQty(db.getQty());
            row.setRefAmt(db.getAmt());
            row.setLinkStatus(1);
            row.setDelFlag(0);
            row.setCreateBy(uid);
            row.setCreateTime(now);
            depPurchaseApplyMapper.insertDepPurApplyCkEntryRef(row);
        }
        refreshOutboundRefStatus(loaded.getDepPurchaseApplyId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseDepPurApplyCkRefsForOutboundBill(Long ckBillId, String tenantId) {
        if (ckBillId == null || StringUtils.isEmpty(tenantId)) {
            return;
        }
        StkIoBill loaded = stkIoBillMapper.selectStkIoBillById(ckBillId);
        Long depApplyId = loaded != null ? loaded.getDepPurchaseApplyId() : null;
        depPurchaseApplyMapper.softDeleteCkEntryRefsByCkBillId(String.valueOf(ckBillId), tenantId,
            SecurityUtils.getUserIdStr());
        if (depApplyId != null) {
            refreshOutboundRefStatus(depApplyId);
        }
    }

    @Override
    public void refreshPurchasePlanRefStatus(Long depPurchaseApplyId) {
        if (depPurchaseApplyId == null) {
            return;
        }
        depPurchaseApplyMapper.refreshPurchasePlanRefStatus(depPurchaseApplyId);
    }

    @Override
    public void refreshOutboundRefStatus(Long depPurchaseApplyId) {
        if (depPurchaseApplyId == null) {
            return;
        }
        depPurchaseApplyMapper.refreshOutboundRefStatus(depPurchaseApplyId);
    }

    @Override
    public void refreshPurchasePlanRefStatusByPlanId(Long planId) {
        if (planId == null) {
            return;
        }
        List<Long> applyIds = purchasePlanEntryDepApplyMapper.selectDistinctDepApplyIdsByPlanId(planId);
        if (applyIds == null || applyIds.isEmpty()) {
            return;
        }
        for (Long applyId : applyIds) {
            if (applyId != null) {
                refreshPurchasePlanRefStatus(applyId);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void voidWholeDepPurchaseApply(Long id, String reason) {
        if (id == null) {
            throw new ServiceException("科室申购单ID不能为空");
        }
        DepPurchaseApply m = depPurchaseApplyMapper.selectDepPurchaseApplyById(id);
        if (m == null) {
            throw new ServiceException("科室申购单不存在");
        }
        SecurityUtils.ensureTenantAccess(m.getTenantId());
        assertDepPurchaseDepartmentInUserScope(m);
        if (Integer.valueOf(1).equals(m.getVoidWholeFlag())) {
            throw new ServiceException("该科室申购单已作废");
        }
        int audited = depPurchaseApplyMapper.countLinkedRefsToAuditedCkByDepPurApplyId(id);
        if (audited > 0) {
            throw new ServiceException("已关联已审核出库单，无法整单作废；请先处理相关出库单");
        }
        String uid = SecurityUtils.getUserIdStr();
        int refCnt = depPurchaseApplyMapper.countActiveCkRefsByDepPurApplyId(id);
        if (refCnt > 0) {
            stkIoBillMapper.clearDepPurApplyEntryIdOnDraftOutbillsByDepPurApplyId(id, uid);
            stkIoBillMapper.clearDepPurchaseApplyOnDraftOutbillsByDepPurApplyId(id, uid);
            depPurchaseApplyMapper.softDeleteCkEntryRefsByDepPurApplyId(id, uid);
        }
        int u = depPurchaseApplyMapper.updateDepPurchaseApplyVoidWhole(id, uid, DateUtils.getNowDate(), reason);
        if (u <= 0) {
            throw new ServiceException("整单作废失败，请刷新后重试");
        }
        refreshOutboundRefStatus(id);
    }
}

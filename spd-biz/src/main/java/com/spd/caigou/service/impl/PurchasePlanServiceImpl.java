package com.spd.caigou.service.impl;

import com.spd.caigou.domain.PurchasePlan;
import com.spd.caigou.domain.PurchasePlanEntry;
import com.spd.caigou.domain.PurchasePlanEntryApply;
import com.spd.caigou.domain.PurchasePlanEntryDepApply;
import com.spd.caigou.mapper.PurchasePlanEntryApplyMapper;
import com.spd.caigou.mapper.PurchasePlanEntryDepApplyMapper;
import com.spd.caigou.mapper.PurchasePlanMapper;
import com.spd.caigou.service.IPurchasePlanService;
import com.spd.caigou.service.IPurchaseOrderService;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.warehouse.mapper.StkInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spd.caigou.domain.vo.EntryBillNoVO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 采购计划Service业务层处理
 *
 * @author spd
 * @date 2024-01-15
 */
@Service
public class PurchasePlanServiceImpl implements IPurchasePlanService 
{
    @Autowired
    private PurchasePlanMapper purchasePlanMapper;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    @Autowired
    private StkInventoryMapper stkInventoryMapper;

    @Autowired
    private IPurchaseOrderService purchaseOrderService;

    @Autowired
    private PurchasePlanEntryApplyMapper purchasePlanEntryApplyMapper;
    @Autowired
    private PurchasePlanEntryDepApplyMapper purchasePlanEntryDepApplyMapper;

    /**
     * 查询采购计划
     *
     * @param id 采购计划主键
     * @return 采购计划
     */
    @Override
    public PurchasePlan selectPurchasePlanById(Long id)
    {
        PurchasePlan purchasePlan = purchasePlanMapper.selectPurchasePlanById(id);
        if (purchasePlan == null) {
            return null;
        }
        SecurityUtils.ensureTenantAccess(purchasePlan.getTenantId());
        List<PurchasePlanEntry> purchasePlanEntryList = purchasePlanMapper.selectPurchasePlanEntryByParentId(id);
        if (purchasePlanEntryList != null) {
            Long warehouseId = purchasePlan.getWarehouseId();
            for (PurchasePlanEntry entry : purchasePlanEntryList) {
                if (entry.getMaterialId() != null) {
                    FdMaterial fdMaterial = fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
                    entry.setMaterial(fdMaterial);
                    if (warehouseId != null) {
                        BigDecimal stock = stkInventoryMapper.selectSumQtyByMaterialAndWarehouse(entry.getMaterialId(), warehouseId);
                        entry.setStockQty(stock != null ? stock : BigDecimal.ZERO);
                    }
                }
            }
            List<EntryBillNoVO> entryBillNos = purchasePlanEntryDepApplyMapper.selectEntryBillNosByPlanId(id);
            if (entryBillNos != null && !entryBillNos.isEmpty()) {
                java.util.Map<Long, List<String>> byEntry = entryBillNos.stream()
                    .filter(v -> v.getEntryId() != null && v.getPurchaseBillNo() != null)
                    .collect(Collectors.groupingBy(EntryBillNoVO::getEntryId, Collectors.mapping(EntryBillNoVO::getPurchaseBillNo, Collectors.toList())));
                for (PurchasePlanEntry entry : purchasePlanEntryList) {
                    if (entry.getId() != null && byEntry.containsKey(entry.getId())) {
                        entry.setApplyBillNos(byEntry.get(entry.getId()).stream().distinct().collect(Collectors.joining(",")));
                    }
                }
            }
        }
        purchasePlan.setPurchasePlanEntryList(purchasePlanEntryList);
        return purchasePlan;
    }

    /**
     * 查询采购计划列表
     *
     * @param purchasePlan 采购计划
     * @return 采购计划
     */
    @Override
    public List<PurchasePlan> selectPurchasePlanList(PurchasePlan purchasePlan)
    {
        if (purchasePlan != null && StringUtils.isEmpty(purchasePlan.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            purchasePlan.setTenantId(SecurityUtils.getCustomerId());
        }
        return purchasePlanMapper.selectPurchasePlanList(purchasePlan);
    }

    /**
     * 新增采购计划
     *
     * @param purchasePlan 采购计划
     * @return 结果
     */
    /** 校验计划明细：有耗材的明细必须指定供应商 */
    private void validateEntriesHaveSupplier(List<PurchasePlanEntry> list) {
        if (list == null) return;
        for (PurchasePlanEntry e : list) {
            if (e.getMaterialId() != null && (e.getSupplierId() == null)) {
                throw new ServiceException("请为每条计划明细指定供应商后再保存。存在未选择供应商的明细。");
            }
        }
    }

    /** 校验计划明细：有耗材的明细采购数量不能为空且必须大于0（审核时调用） */
    private void validateEntriesHaveQty(List<PurchasePlanEntry> list) {
        if (list == null) return;
        for (PurchasePlanEntry e : list) {
            if (e.getMaterialId() != null && (e.getQty() == null || e.getQty().compareTo(BigDecimal.ZERO) <= 0)) {
                throw new ServiceException("采购计划明细中采购数量不能为空且必须大于0，请检查后重新审核。");
            }
        }
    }

    @Transactional
    @Override
    public int insertPurchasePlan(PurchasePlan purchasePlan)
    {
        validateEntriesHaveSupplier(purchasePlan.getPurchasePlanEntryList());
        validateEntriesHaveQty(purchasePlan.getPurchasePlanEntryList());
        purchasePlan.setPlanNo(getPlanNumber());
        // 如果前端没有传入状态，则默认为"未提交"（0），否则使用前端传入的状态
        if (purchasePlan.getPlanStatus() == null || purchasePlan.getPlanStatus().isEmpty()) {
            purchasePlan.setPlanStatus("0"); // 未提交状态
        }
        if (StringUtils.isEmpty(purchasePlan.getPlanEntryMode())) {
            purchasePlan.setPlanEntryMode("1"); // 默认按产品档案汇总
        }
        purchasePlan.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(purchasePlan.getCreateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            purchasePlan.setCreateBy(SecurityUtils.getUserIdStr());
        }
        if (StringUtils.isEmpty(purchasePlan.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            purchasePlan.setTenantId(SecurityUtils.getCustomerId());
        }
        int rows = purchasePlanMapper.insertPurchasePlan(purchasePlan);
        insertPurchasePlanEntry(purchasePlan);
        return rows;
    }

    /**
     * 修改采购计划
     *
     * @param purchasePlan 采购计划
     * @return 结果
     */
    @Transactional
    @Override
    public int updatePurchasePlan(PurchasePlan purchasePlan)
    {
        validateEntriesHaveSupplier(purchasePlan.getPurchasePlanEntryList());
        validateEntriesHaveQty(purchasePlan.getPurchasePlanEntryList());
        purchasePlan.setUpdateTime(DateUtils.getNowDate());
        purchasePlan.setUpdateBy(SecurityUtils.getUserIdStr());
        String deleteBy = SecurityUtils.getUserIdStr();
        purchasePlanEntryApplyMapper.logicDeleteByPlanId(purchasePlan.getId(), deleteBy);
        purchasePlanEntryDepApplyMapper.logicDeleteByPlanId(purchasePlan.getId(), deleteBy);
        purchasePlanMapper.deletePurchasePlanEntryByParentId(purchasePlan.getId(), deleteBy);
        insertPurchasePlanEntry(purchasePlan);
        return purchasePlanMapper.updatePurchasePlan(purchasePlan);
    }

    /**
     * 批量删除采购计划
     *
     * @param ids 需要删除的采购计划主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletePurchasePlanByIds(Long[] ids)
    {
        String deleteBy = SecurityUtils.getUserIdStr();
        purchasePlanMapper.deletePurchasePlanEntryByParentIds(ids, deleteBy);
        return purchasePlanMapper.deletePurchasePlanByIds(ids, deleteBy);
    }

    /**
     * 删除采购计划信息
     *
     * @param id 采购计划主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletePurchasePlanById(Long id)
    {
        PurchasePlan purchasePlan = purchasePlanMapper.selectPurchasePlanById(id);
        if(purchasePlan == null){
            throw new ServiceException(String.format("采购计划ID：%s，不存在!", id));
        }
        SecurityUtils.ensureTenantAccess(purchasePlan.getTenantId());
        String deleteBy = SecurityUtils.getUserIdStr();
        purchasePlanMapper.deletePurchasePlanEntryByParentId(id, deleteBy);
        return purchasePlanMapper.deletePurchasePlanById(id, deleteBy);
    }

    /**
     * 审核采购计划
     *
     * @param id 采购计划主键
     * @param auditBy 审核人
     * @param auditOpinion 审核意见
     * @return 结果
     */
    @Transactional
    @Override
    public int auditPurchasePlan(Long id, String auditBy, String auditOpinion)
    {
        PurchasePlan purchasePlan = purchasePlanMapper.selectPurchasePlanById(id);
        if(purchasePlan == null){
            throw new ServiceException(String.format("采购计划ID：%s，不存在!", id));
        }

        // 检查状态是否为"未提交"（1，已提交但未审核）
        if(!"1".equals(purchasePlan.getPlanStatus())){
            throw new ServiceException(String.format("采购计划ID：%s，状态不正确，只能审核未提交状态的计划!", id));
        }
        List<PurchasePlanEntry> entryList = purchasePlanMapper.selectPurchasePlanEntryByParentId(id);
        validateEntriesHaveSupplier(entryList);
        validateEntriesHaveQty(entryList);

        purchasePlan.setPlanStatus("2"); // 已审核状态
        purchasePlan.setAuditBy(auditBy);
        purchasePlan.setAuditDate(new Date());
        purchasePlan.setAuditOpinion(auditOpinion != null ? auditOpinion : "");
        purchasePlan.setUpdateBy(SecurityUtils.getUserIdStr());
        purchasePlan.setUpdateTime(new Date());

        int result = purchasePlanMapper.auditPurchasePlan(purchasePlan);
        
        // 审核通过后，自动按供应商拆分生成订单
        if (result > 0) {
            try {
                purchaseOrderService.generateOrdersFromPlan(id);
                // 订单生成成功，计划状态已在generateOrdersFromPlan中更新为"已执行"
            } catch (Exception e) {
                // 如果生成订单失败，记录日志但不影响审核结果
                // 可以在这里添加日志记录
                throw new ServiceException("审核成功，但生成订单失败：" + e.getMessage());
            }
        }

        return result;
    }

    /**
     * 生成计划单号
     *
     * @return 计划单号
     */
    public String getPlanNumber() {
        String str = "JH";
        String date = FillRuleUtil.getDateNum();
        String maxNum = purchasePlanMapper.selectMaxPlanNo(date);
        String result = FillRuleUtil.getNumber(str, maxNum, date);
        return result;
    }

    /**
     * 新增采购计划明细信息
     *
     * @param purchasePlan 采购计划对象
     */
    public void insertPurchasePlanEntry(PurchasePlan purchasePlan)
    {
        List<PurchasePlanEntry> purchasePlanEntryList = purchasePlan.getPurchasePlanEntryList();
        Long id = purchasePlan.getId();
        if (id == null) {
            return;
        }
        if (StringUtils.isNotNull(purchasePlanEntryList) && !purchasePlanEntryList.isEmpty())
        {
            List<PurchasePlanEntry> list = new ArrayList<PurchasePlanEntry>();
            for (PurchasePlanEntry purchasePlanEntry : purchasePlanEntryList)
            {
                if (purchasePlanEntry.getMaterialId() == null) {
                    continue;
                }
                purchasePlanEntry.setParentId(id);
                if (StringUtils.isEmpty(purchasePlanEntry.getTenantId()) && StringUtils.isNotEmpty(purchasePlan.getTenantId())) {
                    purchasePlanEntry.setTenantId(purchasePlan.getTenantId());
                }
                purchasePlanEntry.setDelFlag("0");
                purchasePlanEntry.setCreateBy(SecurityUtils.getUserIdStr());
                purchasePlanEntry.setCreateTime(new Date());
                list.add(purchasePlanEntry);
            }
            if (list.isEmpty()) {
                return;
            }
            boolean hasBasRefs = list.stream().anyMatch(e -> e.getBasApplyEntryIds() != null && !e.getBasApplyEntryIds().isEmpty());
            boolean hasDepRefs = list.stream().anyMatch(e -> e.getDepApplyEntryIds() != null && !e.getDepApplyEntryIds().isEmpty());
            boolean hasRefs = hasBasRefs || hasDepRefs;
            String createBy = SecurityUtils.getUserIdStr();
            String tenantId = SecurityUtils.getCustomerId();
            Date now = new Date();
            if (hasRefs) {
                for (PurchasePlanEntry e : list) {
                    purchasePlanMapper.insertPurchasePlanEntry(e);
                    if (e.getId() != null) {
                        if (e.getBasApplyEntryIds() != null && !e.getBasApplyEntryIds().isEmpty()) {
                            List<PurchasePlanEntryApply> refs = e.getBasApplyEntryIds().stream()
                                .map(applyEntryId -> {
                                    PurchasePlanEntryApply ref = new PurchasePlanEntryApply();
                                    ref.setPurchasePlanEntryId(e.getId());
                                    ref.setBasApplyEntryId(applyEntryId);
                                    ref.setTenantId(tenantId);
                                    ref.setCreateBy(createBy);
                                    ref.setCreateTime(now);
                                    return ref;
                                }).collect(Collectors.toList());
                            purchasePlanEntryApplyMapper.batchInsert(refs);
                        }
                        if (e.getDepApplyEntryIds() != null && !e.getDepApplyEntryIds().isEmpty()) {
                            List<PurchasePlanEntryDepApply> depRefs = e.getDepApplyEntryIds().stream()
                                .map(depEntryId -> {
                                    PurchasePlanEntryDepApply ref = new PurchasePlanEntryDepApply();
                                    ref.setPurchasePlanEntryId(e.getId());
                                    ref.setDepPurchaseApplyEntryId(depEntryId);
                                    ref.setTenantId(tenantId);
                                    ref.setCreateBy(createBy);
                                    ref.setCreateTime(now);
                                    return ref;
                                }).collect(Collectors.toList());
                            purchasePlanEntryDepApplyMapper.batchInsert(depRefs);
                            purchasePlanEntryDepApplyMapper.updateFillApplyInfo(e.getId());
                        }
                    }
                }
            } else {
                purchasePlanMapper.batchPurchasePlanEntry(list);
            }
        }
    }
}

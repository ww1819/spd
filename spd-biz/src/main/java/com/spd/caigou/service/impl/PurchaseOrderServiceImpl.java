package com.spd.caigou.service.impl;

import com.spd.caigou.domain.PurchaseOrder;
import com.spd.caigou.domain.PurchaseOrderEntry;
import com.spd.caigou.domain.PurchasePlan;
import com.spd.caigou.domain.PurchasePlanEntry;
import com.spd.caigou.mapper.PurchaseOrderMapper;
import com.spd.caigou.mapper.PurchasePlanMapper;
import com.spd.caigou.service.IPurchaseOrderService;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import java.math.BigDecimal;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 采购订单Service业务层处理
 *
 * @author spd
 * @date 2024-01-15
 */
@Service
public class PurchaseOrderServiceImpl implements IPurchaseOrderService 
{
    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    @Autowired
    private PurchasePlanMapper purchasePlanMapper;

    /**
     * 查询采购订单
     *
     * @param id 采购订单主键
     * @return 采购订单
     */
    @Override
    public PurchaseOrder selectPurchaseOrderById(Long id)
    {
        PurchaseOrder purchaseOrder = purchaseOrderMapper.selectPurchaseOrderById(id);
        if (purchaseOrder == null) {
            return null;
        }
        List<PurchaseOrderEntry> purchaseOrderEntryList = purchaseOrderMapper.selectPurchaseOrderEntryByParentId(id);
        List<FdMaterial> materialList = new ArrayList<FdMaterial>();
        for(PurchaseOrderEntry entry : purchaseOrderEntryList){
            Long materialId = entry.getMaterialId();
            FdMaterial fdMaterial = fdMaterialMapper.selectFdMaterialById(materialId);
            materialList.add(fdMaterial);
        }
        purchaseOrder.setPurchaseOrderEntryList(purchaseOrderEntryList);
        return purchaseOrder;
    }

    /**
     * 查询采购订单列表
     *
     * @param purchaseOrder 采购订单
     * @return 采购订单
     */
    @Override
    public List<PurchaseOrder> selectPurchaseOrderList(PurchaseOrder purchaseOrder)
    {
        return purchaseOrderMapper.selectPurchaseOrderList(purchaseOrder);
    }

    /**
     * 新增采购订单
     *
     * @param purchaseOrder 采购订单
     * @return 结果
     */
    @Transactional
    @Override
    public int insertPurchaseOrder(PurchaseOrder purchaseOrder)
    {
        // 生成订单单号
        if (StringUtils.isEmpty(purchaseOrder.getOrderNo())) {
            String orderNo = "PO" + System.currentTimeMillis();
            purchaseOrder.setOrderNo(orderNo);
        }
        
        // 设置默认值
        if (StringUtils.isEmpty(purchaseOrder.getOrderStatus())) {
            purchaseOrder.setOrderStatus("0"); // 待审核
        }
        if (StringUtils.isEmpty(purchaseOrder.getOrderType())) {
            purchaseOrder.setOrderType("1"); // 采购订单
        }
        if (StringUtils.isEmpty(purchaseOrder.getUrgencyLevel())) {
            purchaseOrder.setUrgencyLevel("2"); // 中等紧急程度
        }
        if (purchaseOrder.getOrderDate() == null) {
            purchaseOrder.setOrderDate(new Date());
        }
        if (StringUtils.isEmpty(purchaseOrder.getDelFlag())) {
            purchaseOrder.setDelFlag("0");
        }
        
        // 计算总金额
        if (purchaseOrder.getPurchaseOrderEntryList() != null && !purchaseOrder.getPurchaseOrderEntryList().isEmpty()) {
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (PurchaseOrderEntry entry : purchaseOrder.getPurchaseOrderEntryList()) {
                if (entry.getTotalAmount() != null) {
                    totalAmount = totalAmount.add(entry.getTotalAmount());
                }
            }
            purchaseOrder.setTotalAmount(totalAmount);
            purchaseOrder.setUnpaidAmount(totalAmount);
        }
        
        int result = purchaseOrderMapper.insertPurchaseOrder(purchaseOrder);
        
        // 插入订单明细
        if (result > 0 && purchaseOrder.getPurchaseOrderEntryList() != null && !purchaseOrder.getPurchaseOrderEntryList().isEmpty()) {
            insertPurchaseOrderEntry(purchaseOrder);
        }
        
        return result;
    }

    /**
     * 修改采购订单
     *
     * @param purchaseOrder 采购订单
     * @return 结果
     */
    @Transactional
    @Override
    public int updatePurchaseOrder(PurchaseOrder purchaseOrder)
    {
        // 计算总金额
        if (purchaseOrder.getPurchaseOrderEntryList() != null && !purchaseOrder.getPurchaseOrderEntryList().isEmpty()) {
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (PurchaseOrderEntry entry : purchaseOrder.getPurchaseOrderEntryList()) {
                if (entry.getTotalAmount() != null) {
                    totalAmount = totalAmount.add(entry.getTotalAmount());
                }
            }
            purchaseOrder.setTotalAmount(totalAmount);
            if (purchaseOrder.getPaidAmount() == null) {
                purchaseOrder.setPaidAmount(BigDecimal.ZERO);
            }
            purchaseOrder.setUnpaidAmount(totalAmount.subtract(purchaseOrder.getPaidAmount()));
        }
        
        // 删除原有明细
        purchaseOrderMapper.deletePurchaseOrderEntryByParentId(purchaseOrder.getId());
        
        // 插入新明细
        if (purchaseOrder.getPurchaseOrderEntryList() != null && !purchaseOrder.getPurchaseOrderEntryList().isEmpty()) {
            insertPurchaseOrderEntry(purchaseOrder);
        }
        
        return purchaseOrderMapper.updatePurchaseOrder(purchaseOrder);
    }

    /**
     * 批量删除采购订单
     *
     * @param ids 需要删除的采购订单主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletePurchaseOrderByIds(Long[] ids)
    {
        for (Long id : ids) {
            purchaseOrderMapper.deletePurchaseOrderEntryByParentId(id);
        }
        return purchaseOrderMapper.deletePurchaseOrderByIds(ids);
    }

    /**
     * 删除采购订单信息
     *
     * @param id 采购订单主键
     * @return 结果
     */
    @Override
    public int deletePurchaseOrderById(Long id)
    {
        purchaseOrderMapper.deletePurchaseOrderEntryByParentId(id);
        return purchaseOrderMapper.deletePurchaseOrderById(id);
    }

    /**
     * 审核采购订单
     *
     * @param id 采购订单主键
     * @param auditBy 审核人
     * @param auditOpinion 审核意见
     * @return 结果
     */
    @Override
    public int auditPurchaseOrder(Long id, String auditBy, String auditOpinion)
    {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(id);
        purchaseOrder.setOrderStatus("2"); // 已审核
        purchaseOrder.setAuditBy(auditBy);
        purchaseOrder.setAuditDate(new Date());
        purchaseOrder.setAuditOpinion(auditOpinion);
        purchaseOrder.setUpdateBy(auditBy);
        purchaseOrder.setUpdateTime(new Date());
        
        return purchaseOrderMapper.auditPurchaseOrder(purchaseOrder);
    }

    /**
     * 新增采购订单明细信息
     *
     * @param purchaseOrder 采购订单对象
     */
    public void insertPurchaseOrderEntry(PurchaseOrder purchaseOrder)
    {
        List<PurchaseOrderEntry> purchaseOrderEntryList = purchaseOrder.getPurchaseOrderEntryList();
        Long id = purchaseOrder.getId();
        if (StringUtils.isNotNull(purchaseOrderEntryList))
        {
            List<PurchaseOrderEntry> list = new ArrayList<PurchaseOrderEntry>();
            for (PurchaseOrderEntry purchaseOrderEntry : purchaseOrderEntryList)
            {
                // 验证必填字段
                if (purchaseOrderEntry.getMaterialId() == null) {
                    throw new RuntimeException("耗材ID不能为空");
                }
                if (purchaseOrderEntry.getOrderQty() == null || purchaseOrderEntry.getOrderQty().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new RuntimeException("订单数量不能为空且必须大于0");
                }
                if (purchaseOrderEntry.getUnitPrice() == null || purchaseOrderEntry.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
                    throw new RuntimeException("单价不能为空且不能小于0");
                }
                
                // 设置默认值
                if (StringUtils.isEmpty(purchaseOrderEntry.getDelFlag())) {
                    purchaseOrderEntry.setDelFlag("0");
                }
                if (StringUtils.isEmpty(purchaseOrderEntry.getQualityStatus())) {
                    purchaseOrderEntry.setQualityStatus("0");
                }
                if (purchaseOrderEntry.getReceivedQty() == null) {
                    purchaseOrderEntry.setReceivedQty(BigDecimal.ZERO);
                }
                
                purchaseOrderEntry.setParentId(id);
                list.add(purchaseOrderEntry);
            }
            if (list.size() > 0)
            {
                purchaseOrderMapper.batchPurchaseOrderEntry(list);
            }
        }
    }

    /**
     * 从采购计划生成订单（按供应商拆分）
     * 
     * @param planId 采购计划主键
     * @return 生成的订单数量
     */
    @Transactional
    @Override
    public int generateOrdersFromPlan(Long planId)
    {
        // 查询采购计划
        PurchasePlan purchasePlan = purchasePlanMapper.selectPurchasePlanById(planId);
        if (purchasePlan == null) {
            throw new ServiceException(String.format("采购计划ID：%s，不存在!", planId));
        }

        // 检查计划状态是否为已审核
        if (!"2".equals(purchasePlan.getPlanStatus())) {
            throw new ServiceException(String.format("采购计划ID：%s，状态不正确，只能从已审核的计划生成订单!", planId));
        }

        // 查询计划明细
        List<PurchasePlanEntry> planEntryList = purchasePlanMapper.selectPurchasePlanEntryByParentId(planId);
        if (planEntryList == null || planEntryList.isEmpty()) {
            throw new ServiceException(String.format("采购计划ID：%s，没有明细数据!", planId));
        }

        // 按供应商分组
        Map<Long, List<PurchasePlanEntry>> supplierGroupMap = new HashMap<>();
        for (PurchasePlanEntry entry : planEntryList) {
            // 获取耗材信息
            FdMaterial material = fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
            if (material == null || material.getSupplierId() == null) {
                throw new ServiceException(String.format("耗材ID：%s，不存在或没有关联供应商!", entry.getMaterialId()));
            }
            
            Long supplierId = material.getSupplierId();
            if (!supplierGroupMap.containsKey(supplierId)) {
                supplierGroupMap.put(supplierId, new ArrayList<>());
            }
            supplierGroupMap.get(supplierId).add(entry);
        }

        // 为每个供应商生成一个订单
        int orderCount = 0;
        for (Map.Entry<Long, List<PurchasePlanEntry>> entry : supplierGroupMap.entrySet()) {
            Long supplierId = entry.getKey();
            List<PurchasePlanEntry> supplierEntries = entry.getValue();

            // 创建订单
            PurchaseOrder purchaseOrder = new PurchaseOrder();
            purchaseOrder.setPlanNo(purchasePlan.getPlanNo()); // 关联计划单号
            purchaseOrder.setOrderNo(generateOrderNo()); // 生成订单单号
            purchaseOrder.setOrderDate(new Date());
            purchaseOrder.setSupplierId(supplierId);
            purchaseOrder.setWarehouseId(purchasePlan.getWarehouseId());
            purchaseOrder.setDepartmentId(purchasePlan.getDepartmentId());
            purchaseOrder.setOrderStatus("0"); // 待审核
            purchaseOrder.setOrderType("1"); // 采购订单
            purchaseOrder.setUrgencyLevel("2"); // 中等紧急程度
            purchaseOrder.setDelFlag("0");
            purchaseOrder.setCreateBy(SecurityUtils.getLoginUser().getUsername());
            purchaseOrder.setCreateTime(new Date());
            purchaseOrder.setRemark("从采购计划" + purchasePlan.getPlanNo() + "生成");

            // 创建订单明细
            List<PurchaseOrderEntry> orderEntryList = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;
            
            for (PurchasePlanEntry planEntry : supplierEntries) {
                FdMaterial material = fdMaterialMapper.selectFdMaterialById(planEntry.getMaterialId());
                
                PurchaseOrderEntry orderEntry = new PurchaseOrderEntry();
                orderEntry.setMaterialId(planEntry.getMaterialId());
                orderEntry.setMaterialCode(material != null ? material.getCode() : "");
                orderEntry.setMaterialName(material != null ? material.getName() : "");
                orderEntry.setMaterialSpec(material != null ? material.getSpeci() : planEntry.getSpeci());
                orderEntry.setMaterialUnit(material != null && material.getFdUnit() != null ? material.getFdUnit().getUnitName() : "");
                orderEntry.setOrderQty(planEntry.getQty());
                orderEntry.setUnitPrice(planEntry.getPrice());
                orderEntry.setTotalAmount(planEntry.getAmt());
                orderEntry.setQualityStatus("0"); // 待检验
                orderEntry.setReceivedQty(BigDecimal.ZERO);
                orderEntry.setDelFlag("0");
                orderEntry.setCreateBy(SecurityUtils.getLoginUser().getUsername());
                orderEntry.setCreateTime(new Date());
                
                orderEntryList.add(orderEntry);
                if (planEntry.getAmt() != null) {
                    totalAmount = totalAmount.add(planEntry.getAmt());
                }
            }

            purchaseOrder.setTotalAmount(totalAmount);
            purchaseOrder.setUnpaidAmount(totalAmount);
            purchaseOrder.setPaidAmount(BigDecimal.ZERO);
            purchaseOrder.setPurchaseOrderEntryList(orderEntryList);

            // 保存订单
            int result = purchaseOrderMapper.insertPurchaseOrder(purchaseOrder);
            if (result > 0) {
                insertPurchaseOrderEntry(purchaseOrder);
                orderCount++;
            }
        }

        // 更新计划状态为已执行
        purchasePlan.setPlanStatus("3"); // 已执行
        purchasePlan.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        purchasePlan.setUpdateTime(new Date());
        purchasePlanMapper.updatePurchasePlan(purchasePlan);

        return orderCount;
    }

    /**
     * 生成订单单号
     * 
     * @return 订单单号
     */
    private String generateOrderNo() {
        String str = "PO";
        String date = FillRuleUtil.getDateNum();
        String maxNum = purchaseOrderMapper.selectMaxOrderNo(date);
        String result = FillRuleUtil.getNumber(str, maxNum, date);
        return result;
    }
}

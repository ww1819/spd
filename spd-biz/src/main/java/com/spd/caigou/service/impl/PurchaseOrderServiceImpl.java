package com.spd.caigou.service.impl;

import com.spd.caigou.domain.PurchaseOrder;
import com.spd.caigou.domain.PurchaseOrderEntry;
import com.spd.caigou.mapper.PurchaseOrderMapper;
import com.spd.caigou.service.IPurchaseOrderService;
import com.spd.common.utils.StringUtils;
import java.math.BigDecimal;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        purchaseOrder.setOrderStatus("1"); // 已审核
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
}

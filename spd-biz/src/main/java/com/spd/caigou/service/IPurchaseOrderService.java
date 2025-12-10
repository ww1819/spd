package com.spd.caigou.service;

import com.spd.caigou.domain.PurchaseOrder;

import java.util.List;

/**
 * 采购订单Service接口
 *
 * @author spd
 * @date 2024-01-15
 */
public interface IPurchaseOrderService 
{
    /**
     * 查询采购订单
     *
     * @param id 采购订单主键
     * @return 采购订单
     */
    public PurchaseOrder selectPurchaseOrderById(Long id);

    /**
     * 查询采购订单列表
     *
     * @param purchaseOrder 采购订单
     * @return 采购订单集合
     */
    public List<PurchaseOrder> selectPurchaseOrderList(PurchaseOrder purchaseOrder);

    /**
     * 新增采购订单
     *
     * @param purchaseOrder 采购订单
     * @return 结果
     */
    public int insertPurchaseOrder(PurchaseOrder purchaseOrder);

    /**
     * 修改采购订单
     *
     * @param purchaseOrder 采购订单
     * @return 结果
     */
    public int updatePurchaseOrder(PurchaseOrder purchaseOrder);

    /**
     * 批量删除采购订单
     *
     * @param ids 需要删除的采购订单主键集合
     * @return 结果
     */
    public int deletePurchaseOrderByIds(Long[] ids);

    /**
     * 删除采购订单信息
     * 
     * @param id 采购订单主键
     * @return 结果
     */
    public int deletePurchaseOrderById(Long id);

    /**
     * 审核采购订单
     * 
     * @param id 采购订单主键
     * @param auditBy 审核人
     * @param auditOpinion 审核意见
     * @return 结果
     */
    public int auditPurchaseOrder(Long id, String auditBy, String auditOpinion);

    /**
     * 从采购计划生成订单（按供应商拆分）
     * 
     * @param planId 采购计划主键
     * @return 生成的订单数量
     */
    public int generateOrdersFromPlan(Long planId);

}

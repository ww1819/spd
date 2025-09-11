package com.spd.caigou.mapper;

import com.spd.caigou.domain.PurchaseOrder;
import com.spd.caigou.domain.PurchaseOrderEntry;

import java.util.List;

/**
 * 采购订单Mapper接口
 *
 * @author spd
 * @date 2024-01-15
 */
public interface PurchaseOrderMapper 
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
     * 删除采购订单
     *
     * @param id 采购订单主键
     * @return 结果
     */
    public int deletePurchaseOrderById(Long id);

    /**
     * 审核采购订单
     * 
     * @param purchaseOrder 采购订单
     * @return 结果
     */
    public int auditPurchaseOrder(PurchaseOrder purchaseOrder);

    /**
     * 批量删除采购订单
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePurchaseOrderByIds(Long[] ids);

    /**
     * 查询采购订单明细
     *
     * @param parentId 主表ID
     * @return 采购订单明细集合
     */
    public List<PurchaseOrderEntry> selectPurchaseOrderEntryByParentId(Long parentId);

    /**
     * 新增采购订单明细
     *
     * @param purchaseOrderEntry 采购订单明细
     * @return 结果
     */
    public int insertPurchaseOrderEntry(PurchaseOrderEntry purchaseOrderEntry);

    /**
     * 批量新增采购订单明细
     *
     * @param purchaseOrderEntryList 采购订单明细列表
     * @return 结果
     */
    public int batchPurchaseOrderEntry(List<PurchaseOrderEntry> purchaseOrderEntryList);

    /**
     * 删除采购订单明细
     *
     * @param parentId 主表ID
     * @return 结果
     */
    public int deletePurchaseOrderEntryByParentId(Long parentId);

}

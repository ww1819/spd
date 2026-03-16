package com.spd.caigou.mapper;

import com.spd.caigou.domain.PurchaseOrder;
import com.spd.caigou.domain.PurchaseOrderEntry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 采购订单Mapper接口
 *
 * @author spd
 * @date 2024-01-15
 */
@Mapper
@Repository
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
     * 逻辑删除采购订单（设置 del_flag、delete_by、delete_time）
     */
    public int deletePurchaseOrderById(@Param("id") Long id, @Param("deleteBy") String deleteBy);

    /**
     * 审核采购订单
     * 
     * @param purchaseOrder 采购订单
     * @return 结果
     */
    public int auditPurchaseOrder(PurchaseOrder purchaseOrder);

    /**
     * 批量逻辑删除采购订单
     */
    public int deletePurchaseOrderByIds(@Param("ids") Long[] ids, @Param("deleteBy") String deleteBy);

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
     * 逻辑删除采购订单明细
     */
    public int deletePurchaseOrderEntryByParentId(@Param("parentId") Long parentId, @Param("deleteBy") String deleteBy);

    /**
     * 查询最大订单单号
     *
     * @param date 日期字符串（格式：yymmdd）
     * @return 最大订单单号
     */
    public String selectMaxOrderNo(String date);

}

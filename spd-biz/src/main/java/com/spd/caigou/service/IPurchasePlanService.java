package com.spd.caigou.service;

import com.spd.caigou.domain.PurchasePlan;

import java.util.List;

/**
 * 采购计划Service接口
 *
 * @author spd
 * @date 2024-01-15
 */
public interface IPurchasePlanService 
{
    /**
     * 查询采购计划
     *
     * @param id 采购计划主键
     * @return 采购计划
     */
    public PurchasePlan selectPurchasePlanById(Long id);

    /**
     * 查询采购计划列表
     *
     * @param purchasePlan 采购计划
     * @return 采购计划集合
     */
    public List<PurchasePlan> selectPurchasePlanList(PurchasePlan purchasePlan);

    /**
     * 新增采购计划
     *
     * @param purchasePlan 采购计划
     * @return 结果
     */
    public int insertPurchasePlan(PurchasePlan purchasePlan);

    /**
     * 修改采购计划
     *
     * @param purchasePlan 采购计划
     * @return 结果
     */
    public int updatePurchasePlan(PurchasePlan purchasePlan);

    /**
     * 批量删除采购计划
     *
     * @param ids 需要删除的采购计划主键集合
     * @return 结果
     */
    public int deletePurchasePlanByIds(Long[] ids);

    /**
     * 删除采购计划信息
     *
     * @param id 采购计划主键
     * @return 结果
     */
    public int deletePurchasePlanById(Long id);

    /**
     * 审核采购计划
     *
     * @param id 采购计划主键
     * @param auditBy 审核人
     * @param auditOpinion 审核意见
     * @return 结果
     */
    public int auditPurchasePlan(Long id, String auditBy, String auditOpinion);
}

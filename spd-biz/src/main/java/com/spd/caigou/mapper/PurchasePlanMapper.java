package com.spd.caigou.mapper;

import com.spd.caigou.domain.PurchasePlan;
import com.spd.caigou.domain.PurchasePlanEntry;

import java.util.List;

/**
 * 采购计划Mapper接口
 *
 * @author spd
 * @date 2024-01-15
 */
public interface PurchasePlanMapper 
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
     * 删除采购计划
     *
     * @param id 采购计划主键
     * @return 结果
     */
    public int deletePurchasePlanById(Long id);

    /**
     * 批量删除采购计划
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePurchasePlanByIds(Long[] ids);

    /**
     * 查询采购计划明细
     *
     * @param parentId 主表ID
     * @return 采购计划明细集合
     */
    public List<PurchasePlanEntry> selectPurchasePlanEntryByParentId(Long parentId);

    /**
     * 新增采购计划明细
     *
     * @param purchasePlanEntry 采购计划明细
     * @return 结果
     */
    public int insertPurchasePlanEntry(PurchasePlanEntry purchasePlanEntry);

    /**
     * 批量新增采购计划明细
     *
     * @param purchasePlanEntryList 采购计划明细列表
     * @return 结果
     */
    public int batchPurchasePlanEntry(List<PurchasePlanEntry> purchasePlanEntryList);

    /**
     * 修改采购计划明细
     *
     * @param purchasePlanEntry 采购计划明细
     * @return 结果
     */
    public int updatePurchasePlanEntry(PurchasePlanEntry purchasePlanEntry);

    /**
     * 删除采购计划明细
     *
     * @param parentId 主表ID
     * @return 结果
     */
    public int deletePurchasePlanEntryByParentId(Long parentId);

    /**
     * 批量删除采购计划明细
     *
     * @param parentIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePurchasePlanEntryByParentIds(Long[] parentIds);

    /**
     * 查询最大计划单号
     *
     * @param date 日期
     * @return 最大计划单号
     */
    public String selectMaxPlanNo(String date);

    /**
     * 审核采购计划
     *
     * @param purchasePlan 采购计划
     * @return 结果
     */
    public int auditPurchasePlan(PurchasePlan purchasePlan);
}

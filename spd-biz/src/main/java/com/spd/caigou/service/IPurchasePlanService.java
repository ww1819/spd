package com.spd.caigou.service;

import com.spd.caigou.domain.PurchasePlan;
import com.spd.caigou.domain.vo.PurchasePlanEntrySupplierExportVO;
import com.spd.caigou.domain.vo.PurchaseRecordExportVO;

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

    /**
     * 查询已审核计划在指定日期范围内的采购记录（按物资+供货单位汇总数量，用于导出「年份月份耗材采购记录」）
     *
     * @param beginDate 开始日期 yyyy-MM-dd
     * @param endDate   结束日期 yyyy-MM-dd
     * @return 导出行列表：物资名称、物资规格、数量、单位、供货单位、收货人(空)、收货日期(空)
     */
    List<PurchaseRecordExportVO> listPurchaseRecordForExport(String beginDate, String endDate);

    /**
     * 根据选中的计划单ID列表汇总采购记录（按物资+供货单位汇总数量，用于导出）
     *
     * @param planIds 计划主键数组
     * @return 导出行列表
     */
    List<PurchaseRecordExportVO> listPurchaseRecordForExportByIds(Long[] planIds);

    /**
     * 采购计划明细导出（与列表筛选一致，一行一条明细）
     */
    List<PurchasePlanEntrySupplierExportVO> listPurchasePlanEntrySupplierExport(PurchasePlan query);

    /**
     * 仅导出指定计划下的明细
     */
    List<PurchasePlanEntrySupplierExportVO> listPurchasePlanEntrySupplierExportByPlanIds(Long[] planIds);
}

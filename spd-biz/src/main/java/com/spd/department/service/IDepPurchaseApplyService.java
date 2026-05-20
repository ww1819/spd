package com.spd.department.service;

import java.math.BigDecimal;
import java.util.List;
import com.spd.department.domain.DepPurchaseApply;
import com.spd.department.vo.WarehousePurchaseReminderRowVo;

/**
 * 科室申购Service接口
 * 
 * @author spd
 * @date 2025-01-01
 */
public interface IDepPurchaseApplyService 
{
    /** 非租户管理员：列表/导出等查询按科室数据权限过滤（params.scopeDeptUserId，见 Mapper） */
    void applyDepartmentScopeToQuery(DepPurchaseApply depPurchaseApply);

    /**
     * 查询科室申购
     * 
     * @param id 科室申购主键
     * @return 科室申购
     */
    public DepPurchaseApply selectDepPurchaseApplyById(Long id);

    /**
     * 查询科室申购列表
     * 
     * @param depPurchaseApply 科室申购
     * @return 科室申购集合
     */
    public List<DepPurchaseApply> selectDepPurchaseApplyList(DepPurchaseApply depPurchaseApply);

    /**
     * 按条件汇总申购明细数量（首页今日统计等，一条 SQL）
     */
    BigDecimal selectDepPurchaseApplyEntryQtySum(DepPurchaseApply depPurchaseApply);

    /**
     * 待审核申购单数量：与申购列表/审核列表同一科室数据范围与租户口径。
     */
    long countPendingAuditPurchaseApply();

    /**
     * 消息提醒：科室申购监控列表（与 {@link #countPendingAuditPurchaseApply()} 同一科室数据范围）
     */
    List<WarehousePurchaseReminderRowVo> selectWarehouseReminderPurchaseMonitorList();

    /**
     * 新增科室申购
     * 
     * @param depPurchaseApply 科室申购
     * @return 结果
     */
    public int insertDepPurchaseApply(DepPurchaseApply depPurchaseApply);

    /**
     * 修改科室申购
     * 
     * @param depPurchaseApply 科室申购
     * @return 结果
     */
    public int updateDepPurchaseApply(DepPurchaseApply depPurchaseApply);

    /**
     * 批量删除科室申购
     * 
     * @param ids 需要删除的科室申购主键集合
     * @return 结果
     */
    public int deleteDepPurchaseApplyByIds(Long[] ids);

    /**
     * 删除科室申购信息
     * 
     * @param id 科室申购主键
     * @return 结果
     */
    public int deleteDepPurchaseApplyById(Long id);

    /**
     * 审核科室申购
     * 
     * @param id 科室申购主键
     * @param auditBy 审核人
     * @return 结果
     */
    public int auditPurchaseApply(String id, String auditBy);

    /**
     * 驳回科室申购
     * 
     * @param id 科室申购主键
     * @param rejectReason 驳回原因
     * @return 结果
     */
    public int rejectPurchaseApply(String id, String rejectReason);

    /**
     * 确认收货
     * 
     * @param id 科室申购主键
     * @param confirmBy 确认人
     * @return 结果
     */
    public int confirmReceipt(String id, String confirmBy);

    /**
     * 驳回收货
     * 
     * @param id 科室申购主键
     * @param rejectReason 驳回原因
     * @return 结果
     */
    public int rejectReceipt(String id, String rejectReason);

    List<DepPurchaseApply> selectDepPurchaseApplyListForOutboundCk(DepPurchaseApply query);

    DepPurchaseApply selectDepPurchaseApplyByIdForOutboundCk(Long id);

    void syncDepPurApplyCkRefsAfterOutboundSave(Long ckBillId);

    void releaseDepPurApplyCkRefsForOutboundBill(Long ckBillId, String tenantId);

    /** 重算采购计划引用状态（0未引用 1部分 2全部；3计划驳回不覆盖） */
    void refreshPurchasePlanRefStatus(Long depPurchaseApplyId);

    /** 重算出库引用状态（0未引用 1部分 2全部） */
    void refreshOutboundRefStatus(Long depPurchaseApplyId);

    /** 采购计划保存/变更后，批量刷新关联申购单的采购计划引用状态 */
    void refreshPurchasePlanRefStatusByPlanId(Long planId);

    /** 整单作废：无已审核出库关联时允许；若仅有未审核出库关联则先解除关联再作废 */
    void voidWholeDepPurchaseApply(Long id, String reason);
}

package com.spd.department.mapper;

import java.math.BigDecimal;
import java.util.List;
import com.spd.department.domain.DepPurchaseApply;
import com.spd.department.domain.DepPurchaseApplyEntry;
import com.spd.department.domain.DepPurApplyCkEntryRef;
import com.spd.department.vo.WarehousePurchaseReminderRowVo;
import java.util.Date;
import org.apache.ibatis.annotations.Param;

/**
 * 科室申购Mapper接口
 * 
 * @author spd
 * @date 2025-01-01
 */
public interface DepPurchaseApplyMapper 
{
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

    /** 逻辑删除（设置 del_flag、delete_by、delete_time） */
    public int deleteDepPurchaseApplyById(@Param("id") Long id, @Param("deleteBy") String deleteBy);

    /** 批量逻辑删除 */
    public int deleteDepPurchaseApplyByIds(@Param("ids") Long[] ids, @Param("deleteBy") String deleteBy);

    /** 批量逻辑删除科室申购明细 */
    public int deleteDepPurchaseApplyEntryByParentIds(@Param("ids") Long[] ids, @Param("deleteBy") String deleteBy);
    
    /**
     * 批量新增科室申购明细
     * 
     * @param depPurchaseApplyEntryList 科室申购明细列表
     * @return 结果
     */
    public int batchDepPurchaseApplyEntry(List<DepPurchaseApplyEntry> depPurchaseApplyEntryList);
    

    /**
     * 通过科室申购主键删除科室申购明细信息
     * 
     * @param id 科室申购ID
     * @return 结果
     */
    public int deleteDepPurchaseApplyEntryByParentId(@Param("id") Long id, @Param("deleteBy") String deleteBy);

    /**
     * 按条件汇总申购明细数量（首页今日统计等）
     */
    BigDecimal selectDepPurchaseApplyEntryQtySum(DepPurchaseApply depPurchaseApply);

    /**
     * 按条件统计申购主表单据数（与列表筛选、科室范围一致）
     */
    int selectDepPurchaseApplyBillCount(DepPurchaseApply depPurchaseApply);

    /**
     * 消息提醒：科室申购监控列表（待审核/已审核，排除驳回；科室范围与列表一致）
     */
    List<WarehousePurchaseReminderRowVo> selectWarehouseReminderPurchaseMonitorList(DepPurchaseApply depPurchaseApply);

    List<DepPurchaseApply> selectDepPurchaseApplyListForOutboundCk(DepPurchaseApply query);

    List<DepPurchaseApplyEntry> selectDepPurchaseApplyEntryListByParentIdForCk(@Param("parentId") Long parentId);

    int updateDepPurchaseApplyVoidWhole(@Param("id") Long id,
        @Param("voidWholeBy") String voidWholeBy,
        @Param("voidWholeTime") Date voidWholeTime,
        @Param("voidWholeReason") String voidWholeReason);

    int softDeleteCkEntryRefsByCkBillId(@Param("ckBillId") String ckBillId,
        @Param("tenantId") String tenantId, @Param("updateBy") String updateBy);

    int softDeleteCkEntryRefsByDepPurApplyId(@Param("depPurApplyId") Long depPurApplyId,
        @Param("updateBy") String updateBy);

    int countLinkedRefsToAuditedCkByDepPurApplyId(@Param("depPurApplyId") Long depPurApplyId);

    int countActiveCkRefsByDepPurApplyId(@Param("depPurApplyId") Long depPurApplyId);

    int insertDepPurApplyCkEntryRef(DepPurApplyCkEntryRef row);
}

package com.spd.department.service;

import java.util.List;
import com.spd.department.domain.DepPurchaseApply;

/**
 * 科室申购Service接口
 * 
 * @author spd
 * @date 2025-01-01
 */
public interface IDepPurchaseApplyService 
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
}

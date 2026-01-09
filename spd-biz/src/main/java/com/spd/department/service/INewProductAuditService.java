package com.spd.department.service;

import java.util.List;
import com.spd.department.domain.NewProductApply;

/**
 * 新品申购审批Service接口
 * 
 * @author spd
 * @date 2025-01-01
 */
public interface INewProductAuditService 
{
    /**
     * 查询新品申购审批
     * 
     * @param id 新品申购审批主键
     * @return 新品申购审批
     */
    public NewProductApply selectNewProductAuditById(Long id);

    /**
     * 查询新品申购审批列表
     * 
     * @param newProductApply 新品申购审批
     * @return 新品申购审批集合
     */
    public List<NewProductApply> selectNewProductAuditList(NewProductApply newProductApply);

    /**
     * 审核通过
     * 
     * @param id 新品申购申请主键
     * @return 结果
     */
    public int auditNewProductApply(Long id);

    /**
     * 驳回
     * 
     * @param newProductApply 新品申购申请
     * @return 结果
     */
    public int rejectNewProductApply(NewProductApply newProductApply);
}

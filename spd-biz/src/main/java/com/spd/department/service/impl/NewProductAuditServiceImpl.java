package com.spd.department.service.impl;

import java.util.List;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spd.department.mapper.NewProductApplyMapper;
import com.spd.department.domain.NewProductApply;
import com.spd.department.service.INewProductAuditService;

/**
 * 新品申购审批Service业务层处理
 * 
 * @author spd
 * @date 2025-01-01
 */
@Service
public class NewProductAuditServiceImpl implements INewProductAuditService 
{
    @Autowired
    private NewProductApplyMapper newProductApplyMapper;

    /**
     * 查询新品申购审批
     * 
     * @param id 新品申购审批主键
     * @return 新品申购审批
     */
    @Override
    public NewProductApply selectNewProductAuditById(Long id)
    {
        return newProductApplyMapper.selectNewProductApplyById(id);
    }

    /**
     * 查询新品申购审批列表
     * 
     * @param newProductApply 新品申购审批
     * @return 新品申购审批
     */
    @Override
    public List<NewProductApply> selectNewProductAuditList(NewProductApply newProductApply)
    {
        return newProductApplyMapper.selectNewProductApplyList(newProductApply);
    }

    /**
     * 审核通过
     * 
     * @param id 新品申购申请主键
     * @return 结果
     */
    @Transactional
    @Override
    public int auditNewProductApply(Long id)
    {
        NewProductApply newProductApply = new NewProductApply();
        newProductApply.setId(id);
        newProductApply.setApplyStatus(2); // 2-已审核
        newProductApply.setAuditDate(DateUtils.getNowDate());
        newProductApply.setUpdateTime(DateUtils.getNowDate());
        newProductApply.setUpdateBy(SecurityUtils.getUsername());
        return newProductApplyMapper.updateNewProductApply(newProductApply);
    }

    /**
     * 驳回
     * 
     * @param newProductApply 新品申购申请
     * @return 结果
     */
    @Transactional
    @Override
    public int rejectNewProductApply(NewProductApply newProductApply)
    {
        // 根据SQL表结构：0-待审核,1-已审核,2-已拒绝
        // 驳回原因存储在remark字段中
        newProductApply.setApplyStatus(2); // 2-已拒绝
        newProductApply.setUpdateTime(DateUtils.getNowDate());
        newProductApply.setUpdateBy(SecurityUtils.getUsername());
        // 驳回原因通过前端传入的rejectReason字段，设置到remark中
        // 前端传入的数据结构：{ id: xxx, rejectReason: "驳回原因" }
        // 这里rejectReason会映射到remark字段
        return newProductApplyMapper.updateNewProductApply(newProductApply);
    }
}

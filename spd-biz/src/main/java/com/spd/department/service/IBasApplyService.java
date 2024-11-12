package com.spd.department.service;

import java.util.List;
import com.spd.department.domain.BasApply;

/**
 * 科室申领Service接口
 * 
 * @author spd
 * @date 2024-02-26
 */
public interface IBasApplyService 
{
    /**
     * 查询科室申领
     * 
     * @param id 科室申领主键
     * @return 科室申领
     */
    public BasApply selectBasApplyById(Long id);

    /**
     * 查询科室申领列表
     * 
     * @param basApply 科室申领
     * @return 科室申领集合
     */
    public List<BasApply> selectBasApplyList(BasApply basApply);

    /**
     * 新增科室申领
     * 
     * @param basApply 科室申领
     * @return 结果
     */
    public int insertBasApply(BasApply basApply);

    /**
     * 修改科室申领
     * 
     * @param basApply 科室申领
     * @return 结果
     */
    public int updateBasApply(BasApply basApply);

    /**
     * 批量删除科室申领
     * 
     * @param ids 需要删除的科室申领主键集合
     * @return 结果
     */
    public int deleteBasApplyByIds(Long[] ids);

    /**
     * 删除科室申领信息
     * 
     * @param id 科室申领主键
     * @return 结果
     */
    public int deleteBasApplyById(Long id);

    /**
     * 审核科室申领
     * @param id
     * @return
     */
    int auditApply(String id);
}

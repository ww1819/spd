package com.spd.department.service;

import java.util.List;
import com.spd.department.domain.NewProductApply;

/**
 * 新品申购申请Service接口
 * 
 * @author spd
 * @date 2025-01-01
 */
public interface INewProductApplyService 
{
    /**
     * 查询新品申购申请
     * 
     * @param id 新品申购申请主键
     * @return 新品申购申请
     */
    public NewProductApply selectNewProductApplyById(Long id);

    /**
     * 查询新品申购申请列表
     * 
     * @param newProductApply 新品申购申请
     * @return 新品申购申请集合
     */
    public List<NewProductApply> selectNewProductApplyList(NewProductApply newProductApply);

    /**
     * 新增新品申购申请
     * 
     * @param newProductApply 新品申购申请
     * @return 结果
     */
    public int insertNewProductApply(NewProductApply newProductApply);

    /**
     * 修改新品申购申请
     * 
     * @param newProductApply 新品申购申请
     * @return 结果
     */
    public int updateNewProductApply(NewProductApply newProductApply);

    /**
     * 批量删除新品申购申请
     * 
     * @param ids 需要删除的新品申购申请主键集合
     * @return 结果
     */
    public int deleteNewProductApplyByIds(Long[] ids);

    /**
     * 删除新品申购申请信息
     * 
     * @param id 新品申购申请主键
     * @return 结果
     */
    public int deleteNewProductApplyById(Long id);
}


package com.spd.department.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import com.spd.department.domain.NewProductApply;
import com.spd.department.domain.NewProductApplyEntry;
import com.spd.department.domain.NewProductApplyDetail;

/**
 * 新品申购申请Mapper接口
 * 
 * @author spd
 * @date 2025-01-01
 */
@Mapper
@Repository
public interface NewProductApplyMapper 
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
     * 删除新品申购申请
     * 
     * @param id 新品申购申请主键
     * @return 结果
     */
    public int deleteNewProductApplyById(Long id);

    /**
     * 批量删除新品申购申请
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteNewProductApplyByIds(Long[] ids);

    /**
     * 批量删除新品申购申请明细
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteNewProductApplyEntryByParentIds(Long[] ids);
    
    /**
     * 批量新增新品申购申请明细
     * 
     * @param newProductApplyEntryList 新品申购申请明细列表
     * @return 结果
     */
    public int batchNewProductApplyEntry(List<NewProductApplyEntry> newProductApplyEntryList);
    

    /**
     * 通过新品申购申请主键删除新品申购申请明细信息
     * 
     * @param id 新品申购申请ID
     * @return 结果
     */
    public int deleteNewProductApplyEntryByParentId(Long id);

    /**
     * 批量新增院内同类产品信息
     * 
     * @param newProductApplyDetailList 院内同类产品信息列表
     * @return 结果
     */
    public int batchNewProductApplyDetail(List<NewProductApplyDetail> newProductApplyDetailList);
    
    /**
     * 通过新品申购申请主键删除院内同类产品信息
     * 
     * @param id 新品申购申请ID
     * @return 结果
     */
    public int deleteNewProductApplyDetailByParentId(Long id);
}


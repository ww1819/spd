package com.spd.department.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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

    /** 逻辑删除（设置 del_flag、delete_by、delete_time） */
    public int deleteNewProductApplyById(@Param("id") Long id, @Param("deleteBy") String deleteBy);

    /** 批量逻辑删除 */
    public int deleteNewProductApplyByIds(@Param("ids") Long[] ids, @Param("deleteBy") String deleteBy);

    /** 批量逻辑删除新品申购申请明细 */
    public int deleteNewProductApplyEntryByParentIds(@Param("ids") Long[] ids, @Param("deleteBy") String deleteBy);
    
    /**
     * 批量新增新品申购申请明细
     * 
     * @param newProductApplyEntryList 新品申购申请明细列表
     * @return 结果
     */
    public int batchNewProductApplyEntry(List<NewProductApplyEntry> newProductApplyEntryList);
    

    /**
     * 批量新增院内同类产品信息
     * 
     * @param newProductApplyDetailList 院内同类产品信息列表
     * @return 结果
     */
    public int batchNewProductApplyDetail(List<NewProductApplyDetail> newProductApplyDetailList);
    
    /** 逻辑删除院内同类产品信息 */
    public int deleteNewProductApplyDetailByParentId(@Param("parentId") Long parentId, @Param("deleteBy") String deleteBy);

    /** 逻辑删除新品申购申请明细 */
    public int deleteNewProductApplyEntryByParentId(@Param("id") Long id, @Param("deleteBy") String deleteBy);
}


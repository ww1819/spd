package com.spd.department.mapper;

import java.util.List;
import com.spd.department.domain.BasApply;
import com.spd.department.domain.BasApplyEntry;
import com.spd.department.vo.BasApplyOutboundRefVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 科室申领Mapper接口
 * 
 * @author spd
 * @date 2024-02-26
 */
@Mapper
@Repository
public interface BasApplyMapper 
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
     * 逻辑删除科室申领（设置 del_flag、del_by、del_time）
     * @param id 科室申领主键
     * @param deleteBy 删除人
     */
    public int deleteBasApplyById(@Param("id") Long id, @Param("deleteBy") String deleteBy);

    /**
     * 批量逻辑删除科室申领
     */
    public int deleteBasApplyByIds(@Param("ids") Long[] ids, @Param("deleteBy") String deleteBy);

    /**
     * 批量逻辑删除科室申领明细（设置 del_flag、delete_by、delete_time）
     */
    public int deleteBasApplyEntryByParenIds(@Param("ids") Long[] ids, @Param("deleteBy") String deleteBy);
    
    /**
     * 批量新增科室申领明细
     * 
     * @param basApplyEntryList 科室申领明细列表
     * @return 结果
     */
    public int batchBasApplyEntry(List<BasApplyEntry> basApplyEntryList);
    

    /**
     * 通过科室申领主键逻辑删除科室申领明细（设置 del_flag、delete_by、delete_time）
     */
    public int deleteBasApplyEntryByParenId(@Param("id") Long id, @Param("deleteBy") String deleteBy);

    String selectMaxBillNo(String date);

    /**
     * 科室申领单关联的出库单明细（经 wh_wh_apply_ck_entry_ref）
     */
    List<BasApplyOutboundRefVo> selectBasApplyOutboundRefsByBasApplyId(@Param("basApplyId") Long basApplyId);
}

package com.spd.gz.mapper;

import java.util.List;
import com.spd.gz.domain.GzDepApply;
import com.spd.gz.domain.GzDepApplyEntry;
import org.apache.ibatis.annotations.Param;

/**
 * 高值科室申领Mapper接口
 *
 * @author spd
 * @date 2024-06-22
 */
public interface GzDepApplyMapper
{
    /**
     * 查询高值科室申领
     *
     * @param id 高值科室申领主键
     * @return 高值科室申领
     */
    public GzDepApply selectGzDepApplyById(Long id);

    /**
     * 查询高值科室申领列表
     *
     * @param gzDepApply 高值科室申领
     * @return 高值科室申领集合
     */
    public List<GzDepApply> selectGzDepApplyList(GzDepApply gzDepApply);

    /**
     * 新增高值科室申领
     *
     * @param gzDepApply 高值科室申领
     * @return 结果
     */
    public int insertGzDepApply(GzDepApply gzDepApply);

    /**
     * 修改高值科室申领
     *
     * @param gzDepApply 高值科室申领
     * @return 结果
     */
    public int updateGzDepApply(GzDepApply gzDepApply);

    /** 逻辑删除（设置 del_flag、delete_by、delete_time） */
    public int deleteGzDepApplyById(@Param("id") Long id, @Param("deleteBy") String deleteBy);

    /** 批量逻辑删除 */
    public int deleteGzDepApplyByIds(@Param("ids") Long[] ids, @Param("deleteBy") String deleteBy);

    /** 批量逻辑删除高值科室申领明细 */
    public int deleteGzDepApplyEntryByParenIds(@Param("ids") Long[] ids, @Param("deleteBy") String deleteBy);

    /**
     * 批量新增高值科室申领明细
     *
     * @param gzDepApplyEntryList 高值科室申领明细列表
     * @return 结果
     */
    public int batchGzDepApplyEntry(List<GzDepApplyEntry> gzDepApplyEntryList);


    /** 逻辑删除高值科室申领明细 */
    public int deleteGzDepApplyEntryByParenId(@Param("id") Long id, @Param("deleteBy") String deleteBy);

    /**
     * 查询当天最大流水号
     * @param date
     * @return
     */
    String selectMaxBillNo(String date);
}

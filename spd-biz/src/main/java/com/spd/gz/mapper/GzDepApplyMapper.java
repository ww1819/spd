package com.spd.gz.mapper;

import java.util.List;
import com.spd.gz.domain.GzDepApply;
import com.spd.gz.domain.GzDepApplyEntry;

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

    /**
     * 删除高值科室申领
     *
     * @param id 高值科室申领主键
     * @return 结果
     */
    public int deleteGzDepApplyById(Long id);

    /**
     * 批量删除高值科室申领
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGzDepApplyByIds(Long[] ids);

    /**
     * 批量删除高值科室申领明细
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGzDepApplyEntryByParenIds(Long[] ids);

    /**
     * 批量新增高值科室申领明细
     *
     * @param gzDepApplyEntryList 高值科室申领明细列表
     * @return 结果
     */
    public int batchGzDepApplyEntry(List<GzDepApplyEntry> gzDepApplyEntryList);


    /**
     * 通过高值科室申领主键删除高值科室申领明细信息
     *
     * @param id 高值科室申领ID
     * @return 结果
     */
    public int deleteGzDepApplyEntryByParenId(Long id);

    /**
     * 查询当天最大流水号
     * @param date
     * @return
     */
    String selectMaxBillNo(String date);
}

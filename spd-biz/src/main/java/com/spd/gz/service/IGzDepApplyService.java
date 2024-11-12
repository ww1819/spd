package com.spd.gz.service;

import java.util.List;
import com.spd.gz.domain.GzDepApply;

/**
 * 高值科室申领Service接口
 *
 * @author spd
 * @date 2024-06-22
 */
public interface IGzDepApplyService
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
     * 批量删除高值科室申领
     *
     * @param ids 需要删除的高值科室申领主键集合
     * @return 结果
     */
    public int deleteGzDepApplyByIds(Long[] ids);

    /**
     * 删除高值科室申领信息
     *
     * @param id 高值科室申领主键
     * @return 结果
     */
    public int deleteGzDepApplyById(Long id);

    /**
     * 审核高职科室信息
     * @param id
     * @return
     */
    int auditApply(String id);
}

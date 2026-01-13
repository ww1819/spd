package com.spd.gz.service;

import java.util.List;
import com.spd.gz.domain.GzTraceability;

/**
 * 高值追溯单Service接口
 *
 * @author spd
 * @date 2025-01-01
 */
public interface IGzTraceabilityService
{
    /**
     * 查询高值追溯单
     *
     * @param id 高值追溯单主键
     * @return 高值追溯单
     */
    public GzTraceability selectGzTraceabilityById(Long id);

    /**
     * 查询高值追溯单列表
     *
     * @param gzTraceability 高值追溯单
     * @return 高值追溯单集合
     */
    public List<GzTraceability> selectGzTraceabilityList(GzTraceability gzTraceability);

    /**
     * 新增高值追溯单
     *
     * @param gzTraceability 高值追溯单
     * @return 结果
     */
    public int insertGzTraceability(GzTraceability gzTraceability);

    /**
     * 修改高值追溯单
     *
     * @param gzTraceability 高值追溯单
     * @return 结果
     */
    public int updateGzTraceability(GzTraceability gzTraceability);

    /**
     * 批量删除高值追溯单
     *
     * @param ids 需要删除的高值追溯单主键集合
     * @return 结果
     */
    public int deleteGzTraceabilityByIds(Long[] ids);

    /**
     * 删除高值追溯单信息
     *
     * @param id 高值追溯单主键
     * @return 结果
     */
    public int deleteGzTraceabilityById(Long id);

    /**
     * 审核高值追溯单
     *
     * @param id 高值追溯单主键
     * @return 结果
     */
    public int auditGzTraceability(Long id);

    /**
     * 反审核高值追溯单
     *
     * @param id 高值追溯单主键
     * @return 结果
     */
    public int unauditGzTraceability(Long id);

    /**
     * 查询追溯单明细列表（用于使用追溯明细表）
     *
     * @param gzTraceability 查询条件
     * @return 追溯单明细集合
     */
    public List<com.spd.gz.domain.GzTraceabilityEntry> selectTraceabilityEntryList(GzTraceability gzTraceability);
}

package com.spd.gz.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.gz.domain.GzTraceability;

/**
 * 高值追溯单Mapper接口
 *
 * @author spd
 * @date 2025-01-01
 */
public interface GzTraceabilityMapper
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
     * 删除高值追溯单
     *
     * @param id 高值追溯单主键
     * @return 结果
     */
    public int deleteGzTraceabilityById(Long id);

    /**
     * 批量删除高值追溯单
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGzTraceabilityByIds(Long[] ids);

    /**
     * 查询当天最大的单号
     * @param prefix 单号前缀（GZ-01）
     * @param date 日期（yyyyMMd格式，如2026011）
     * @return 最大单号
     */
    String selectMaxTraceNo(@Param("prefix") String prefix, @Param("date") String date);

    /**
     * 批量新增追溯单明细
     *
     * @param entryList 追溯单明细列表
     * @return 结果
     */
    public int batchGzTraceabilityEntry(List<com.spd.gz.domain.GzTraceabilityEntry> entryList);

    /**
     * 通过追溯单主键删除追溯单明细信息
     *
     * @param id 追溯单ID
     * @return 结果
     */
    public int deleteGzTraceabilityEntryByParentId(Long id);

    /**
     * 查询追溯单明细列表（用于使用追溯明细表）
     *
     * @param gzTraceability 查询条件（包含追溯单的查询条件）
     * @return 追溯单明细集合
     */
    public List<com.spd.gz.domain.GzTraceabilityEntry> selectTraceabilityEntryList(GzTraceability gzTraceability);
}

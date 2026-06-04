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
     * 写入已审核的高值消耗追溯记录（不重复扣减科室库存，供 HIS 镜像高值核销等场景使用）
     *
     * @param gzTraceability 高值追溯单（含明细）
     * @return 结果
     */
    public int insertConsumedTraceability(GzTraceability gzTraceability);

    /**
     * HIS 镜像高值扫码核销：写入已审核高值计费单、扣减科室库存并写 gz_dep_flow
     *
     * @param gzTraceability 含 visitKind/mirrorRowId/traceSource=HIS_MIRROR_HIGH 及明细
     * @return 持久化后的计费单（含明细 ID）
     */
    GzTraceability insertAuditedMirrorHighBill(GzTraceability gzTraceability);

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

    /**
     * 高值耗材使用情况报表
     */
    public List<com.spd.gz.domain.GzMaterialUsageReportVo> selectMaterialUsageReportList(
        com.spd.gz.domain.GzMaterialUsageReportQuery query);
}

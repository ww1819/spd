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
     * 高值冲销：单独生成反向计费单（不回补/不扣库存、不触即入即出）。
     * 明细数量为负的冲销回补量；单据 {@code reverse_flag=1}，来源 {@code HIS_MIRROR_HIGH_REVERSE}；原单标记已冲销。
     *
     * @param original 原正向计费单（须含明细）
     * @param entryReturnQty 原明细ID → 本次冲销数量（正数）
     * @param entryLinkIds 原明细ID → 原消耗关联ID（反向明细据此继承确认/即入即出状态）
     * @param remark 备注
     * @return 反向计费单
     */
    GzTraceability insertReverseWriteOffBill(GzTraceability original,
        java.util.Map<Long, java.math.BigDecimal> entryReturnQty,
        java.util.Map<Long, String> entryLinkIds, String remark);

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

    /**
     * 使用追溯汇总：按执行科室+耗材汇总
     */
    public List<com.spd.gz.domain.GzTraceabilitySummaryVo> selectTraceabilitySummaryByExecDept(
        GzTraceability gzTraceability);

    /**
     * 使用追溯汇总：按开单科室+耗材汇总
     */
    public List<com.spd.gz.domain.GzTraceabilitySummaryVo> selectTraceabilitySummaryByApplyDept(
        GzTraceability gzTraceability);

    /**
     * 使用追溯汇总：按供应商+耗材汇总
     */
    public List<com.spd.gz.domain.GzTraceabilitySummaryVo> selectTraceabilitySummaryBySupplier(
        GzTraceability gzTraceability);

    com.spd.common.core.page.TotalInfo selectTraceabilityEntryListTotal(GzTraceability gzTraceability);

    com.spd.common.core.page.TotalInfo selectTraceabilitySummaryByExecDeptTotal(GzTraceability gzTraceability);

    com.spd.common.core.page.TotalInfo selectTraceabilitySummaryByApplyDeptTotal(GzTraceability gzTraceability);

    com.spd.common.core.page.TotalInfo selectTraceabilitySummaryBySupplierTotal(GzTraceability gzTraceability);
}

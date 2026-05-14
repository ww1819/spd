package com.spd.department.service;

import java.util.List;
import com.spd.department.dto.StocktakingQtyAdjustDto;
import com.spd.department.vo.DeptStocktakingExportRow;
import com.spd.department.vo.StocktakingQtyMismatchVo;
import com.spd.warehouse.domain.StkIoStocktaking;
import com.spd.warehouse.domain.StkIoStocktakingEntry;

/**
 * 科室盘点Service接口
 *
 * @author spd
 * @date 2025-01-28
 */
public interface IDeptStocktakingService
{
    /**
     * 查询科室盘点
     *
     * @param id 科室盘点主键
     * @return 科室盘点
     */
    public StkIoStocktaking selectDeptStocktakingById(Long id);

    /**
     * 查询科室盘点列表
     *
     * @param stkIoStocktaking 科室盘点
     * @return 科室盘点集合
     */
    public List<StkIoStocktaking> selectDeptStocktakingList(StkIoStocktaking stkIoStocktaking);

    /**
     * 科室盘点导出数据（含明细行）
     */
    List<DeptStocktakingExportRow> selectDeptStocktakingExportList(StkIoStocktaking stkIoStocktaking);

    /**
     * 新增科室盘点
     *
     * @param stkIoStocktaking 科室盘点
     * @return 结果
     */
    public int insertDeptStocktaking(StkIoStocktaking stkIoStocktaking);

    /**
     * 修改科室盘点
     *
     * @param stkIoStocktaking 科室盘点
     * @return 结果
     */
    public int updateDeptStocktaking(StkIoStocktaking stkIoStocktaking);

    /**
     * 批量删除科室盘点
     *
     * @param ids 需要删除的科室盘点主键集合
     * @return 结果
     */
    public int deleteDeptStocktakingByIds(Long[] ids);

    /**
     * 删除科室盘点信息
     *
     * @param id 科室盘点主键
     * @return 结果
     */
    public int deleteDeptStocktakingById(Long id);

    /**
     * 审核科室盘点信息
     * @param id
     * @return
     */
    int auditDeptStocktaking(String id);

    /** 审核前校验：盘点明细库存数量 vs 当前科室账面库存 */
    List<StocktakingQtyMismatchVo> checkStocktakingQtyMismatch(String id);

    /** 审核（含库存不一致时用户确认后的数量调整） */
    int auditDeptStocktaking(String id, List<StocktakingQtyAdjustDto> adjustList);

    /**
     * 驳回科室盘点信息
     * @param id 盘点ID
     * @param rejectReason 驳回原因
     * @return
     */
    int rejectDeptStocktaking(String id, String rejectReason);

    /**
     * 更新科室盘点明细「是否已盘」（主单须未审核且 stock_type=502）
     */
    int updateDeptStocktakingEntryCountedFlag(Long entryId, Integer countedFlag);

    /**
     * 未审核科室盘点：仅追加新明细（不落全量 deleteExcept），用于前端分批落库减轻整单 PUT 压力。
     *
     * @param billId 主单 id
     * @param newEntries 无明细 id 的新行（可多条）
     * @return 实际插入条数
     */
    int appendDeptStocktakingEntries(Long billId, List<StkIoStocktakingEntry> newEntries);

    /**
     * 盘点初始化：在服务端按科室「已收货确认」库存生成主单+明细并落库；成功返回完整单据；失败整单回滚不落库。
     *
     * @param headPatch 主单草稿字段（id 为空则新建；非空则须为无明细的未审核科室盘点单）
     */
    StkIoStocktaking initDeptStocktakingFromDepInventory(StkIoStocktaking headPatch);
}

package com.spd.department.service;

import java.util.List;
import com.spd.department.dto.StocktakingQtyAdjustDto;
import com.spd.department.vo.DeptStocktakingExportRow;
import com.spd.department.vo.StocktakingQtyMismatchVo;
import com.spd.warehouse.domain.StkIoStocktaking;

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
}

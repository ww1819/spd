package com.spd.department.service;

import java.util.Date;
import java.util.List;
import com.spd.department.dto.StocktakingEntryCountedDto;
import com.spd.department.dto.StocktakingPatchSaveDto;
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
     * 未审核科室盘点精简保存：主表 + 变更明细实盘/账面/已盘（不整包 replace 明细）。
     */
    StkIoStocktaking patchSaveDeptStocktaking(StocktakingPatchSaveDto save);

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

    /** 审核前校验：盘点明细库存数量 vs 当前科室账面库存 */
    List<StocktakingQtyMismatchVo> checkStocktakingQtyMismatch(String id);

    /**
     * 审核科室盘点信息（含库存不一致时用户确认后的数量调整）
     * @param expectedUpdateTime 客户端打开/上次保存后主表更新时间，用于并发控制
     */
    int auditDeptStocktaking(String id, List<StocktakingQtyAdjustDto> adjustList, Date expectedUpdateTime);

    /**
     * 驳回科室盘点信息
     * @param expectedUpdateTime 主表更新时间，用于并发控制
     */
    int rejectDeptStocktaking(String id, String rejectReason, Date expectedUpdateTime);

    /**
     * 更新科室盘点明细「是否已盘」，可选同时写入实盘数量（与前端盘点数量一致）
     */
    int updateDeptStocktakingEntryCounted(StocktakingEntryCountedDto dto);

    /**
     * 未审核科室盘点：仅追加新明细（不落全量 deleteExcept），用于前端分批落库减轻整单 PUT 压力。
     *
     * @param billId 主单 id
     * @param newEntries 无明细 id 的新行（可多条）
     * @param expectedUpdateTime 主表更新时间，用于并发控制
     * @return 实际插入条数
     */
    int appendDeptStocktakingEntries(Long billId, List<StkIoStocktakingEntry> newEntries, Date expectedUpdateTime);

    /**
     * 盘点初始化：在服务端按科室「已收货确认」库存生成主单+明细并落库；成功返回完整单据；失败整单回滚不落库。
     *
     * @param headPatch 主单草稿字段（id 为空则新建；非空则须为无明细的未审核科室盘点单）
     */
    StkIoStocktaking initDeptStocktakingFromDepInventory(StkIoStocktaking headPatch);
}

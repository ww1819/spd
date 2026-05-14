package com.spd.warehouse.service;

import java.util.List;

import com.spd.department.dto.StocktakingQtyAdjustDto;
import com.spd.department.vo.StocktakingQtyMismatchVo;
import com.spd.warehouse.domain.StkIoStocktaking;
import com.spd.warehouse.domain.StkIoStocktakingEntry;

/**
 * 盘点Service接口
 *
 * @author spd
 * @date 2024-06-27
 */
public interface IStkIoStocktakingService
{
    /**
     * 查询盘点
     *
     * @param id 盘点主键
     * @return 盘点
     */
    public StkIoStocktaking selectStkIoStocktakingById(Long id);

    /**
     * 查询盘点列表
     *
     * @param stkIoStocktaking 盘点
     * @return 盘点集合
     */
    public List<StkIoStocktaking> selectStkIoStocktakingList(StkIoStocktaking stkIoStocktaking);

    /**
     * 新增盘点
     *
     * @param stkIoStocktaking 盘点
     * @return 结果
     */
    public int insertStkIoStocktaking(StkIoStocktaking stkIoStocktaking);

    /**
     * 修改盘点
     *
     * @param stkIoStocktaking 盘点
     * @return 结果
     */
    public int updateStkIoStocktaking(StkIoStocktaking stkIoStocktaking);

    /**
     * 批量删除盘点
     *
     * @param ids 需要删除的盘点主键集合
     * @return 结果
     */
    public int deleteStkIoStocktakingByIds(Long[] ids);

    /**
     * 删除盘点信息
     *
     * @param id 盘点主键
     * @return 结果
     */
    public int deleteStkIoStocktakingById(Long id);

    /**
     * 审核盘点信息（可选：逐条确认后传入 qtyAdjustList 同步账面库存并回写盘点数量）
     * @param id 盘点主键
     * @param adjustList 与 stk_inventory 不一致时的调整项，可为 null
     */
    int auditStkIoBill(String id, List<StocktakingQtyAdjustDto> adjustList);

    /**
     * 仓库盘点（stock_type=501）审核前：明细库存数量与当前仓库库存是否一致
     */
    List<StocktakingQtyMismatchVo> checkWhStocktakingQtyMismatch(String id);

    /**
     * 月结处理
     * @param beginDate
     * @param endDate
     * @return
     */
    List<StkIoStocktaking> getMonthHandleDataList(String beginDate, String endDate);

    /**
     * 更新仓库盘点明细「是否已盘」（主单须未审核且 stock_type=501）
     */
    int updateStocktakingEntryCountedFlag(Long entryId, Integer countedFlag);

    /**
     * 向已存在的仓库盘点单（stock_type=501、未审核）追加明细；新行不得带明细 id。
     */
    int appendWarehouseStocktakingEntries(Long billId, List<StkIoStocktakingEntry> newEntries);

    /**
     * 仓库盘点初始化：在服务端按仓库库存生成主单+明细并落库；成功返回完整单据；失败整单回滚不落库。
     */
    StkIoStocktaking initWarehouseStocktakingFromInventory(StkIoStocktaking headPatch);
}

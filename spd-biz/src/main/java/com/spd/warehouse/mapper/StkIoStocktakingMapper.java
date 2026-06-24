package com.spd.warehouse.mapper;

import java.util.List;
import com.spd.warehouse.domain.StkIoStocktaking;
import com.spd.warehouse.domain.StkIoStocktakingEntry;
import org.apache.ibatis.annotations.Param;

/**
 * 盘点Mapper接口
 *
 * @author spd
 * @date 2024-06-27
 */
public interface StkIoStocktakingMapper
{
    /**
     * 查询盘点
     *
     * @param id 盘点主键
     * @return 盘点
     */
    public StkIoStocktaking selectStkIoStocktakingById(Long id);

    /** 盘点主单（不含明细，编辑弹窗首屏更快） */
    StkIoStocktaking selectStkIoStocktakingHeadById(Long id);

    /** 盘点明细列表（仅关联耗材与单位，不含厂家/耗材默认供应商） */
    List<StkIoStocktakingEntry> selectStkIoStocktakingEntryListByParenId(Long parenId);

    /**
     * 事务内锁定盘点主表行（FOR UPDATE），用于与并发校验配合。
     */
    StkIoStocktaking lockStkIoStocktakingHeadById(Long id);

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
     * 删除盘点
     *
     * @param id 盘点主键
     * @return 结果
     */
    public int deleteStkIoStocktakingById(@Param("id") Long id, @Param("deleteBy") String deleteBy);

    /**
     * 批量删除盘点
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStkIoStocktakingByIds(@Param("ids") Long[] ids, @Param("deleteBy") String deleteBy);

    /** 批量逻辑删除盘点明细（设置 delete_by、delete_time） */
    public int deleteStkIoStocktakingEntryByParenIds(@Param("ids") Long[] ids, @Param("deleteBy") String deleteBy);

    /**
     * 批量新增盘点明细
     *
     * @param stkIoStocktakingEntryList 盘点明细列表
     * @return 结果
     */
    public int batchStkIoStocktakingEntry(List<StkIoStocktakingEntry> stkIoStocktakingEntryList);


    /** 逻辑删除盘点明细 */
    public int deleteStkIoStocktakingEntryByParenId(@Param("parenId") Long parenId, @Param("deleteBy") String deleteBy);

    /**
     * 修改盘点明细（按主键）
     */
    int updateStkIoStocktakingEntry(StkIoStocktakingEntry entry);

    /**
     * 单条新增盘点明细（id 自增）
     */
    int insertStkIoStocktakingEntrySingle(StkIoStocktakingEntry entry);

    /**
     * 软删盘点明细：同一父单下、id 不在保留列表中的明细
     */
    int deleteStkIoStocktakingEntryByParenIdExceptIds(@Param("parenId") Long parenId, @Param("keepIds") List<Long> keepIds, @Param("deleteBy") String deleteBy);

    /**
     * 查询当天最大流水号
     * @param date
     * @return
     */
    String selectMaxBillNo(String date);

    /**
     * 月结处理
     * @param beginDate
     * @param endDate
     * @return
     */
    List<StkIoStocktaking> getMonthHandleDataList(@Param("beginDate")String beginDate,@Param("endDate") String endDate);

    /**
     * 审核落账后仅回写明细上的仓库库存 id / 科室库存 id（避免整行 update 将未传字段置空）
     */
    int updateStocktakingEntryPostingInventoryRef(@Param("entryId") Long entryId,
        @Param("kcNo") Long kcNo,
        @Param("kcNoStr") String kcNoStr,
        @Param("depInventoryId") String depInventoryId,
        @Param("updateBy") String updateBy);

    /**
     * 仅更新明细「是否已盘」，且校验主单为指定 stock_type、未审核。
     * stockQty 非空时同时更新实盘数量、金额、盈亏标志等（由 Service 计算后传入）。
     */
    int updateStocktakingEntryCountedFlag(@Param("entryId") Long entryId,
        @Param("countedFlag") Integer countedFlag,
        @Param("stockType") Integer stockType,
        @Param("updateBy") String updateBy,
        @Param("stockQty") java.math.BigDecimal stockQty,
        @Param("amt") java.math.BigDecimal amt,
        @Param("profitLossFlag") String profitLossFlag,
        @Param("profitQty") java.math.BigDecimal profitQty,
        @Param("stockAmount") java.math.BigDecimal stockAmount,
        @Param("profitAmount") java.math.BigDecimal profitAmount);

    /** 根据明细 id 查主单 id（租户隔离） */
    Long selectParenIdByStocktakingEntryId(@Param("entryId") Long entryId);

    /**
     * 未审核盘点单：按主键仅更新明细账面/实盘数量及盈亏金额字段（不触碰批号、日期等）。
     */
    int updateStocktakingEntryQtyPatch(@Param("entryId") Long entryId,
        @Param("stockType") Integer stockType,
        @Param("updateBy") String updateBy,
        @Param("qty") java.math.BigDecimal qty,
        @Param("stockQty") java.math.BigDecimal stockQty,
        @Param("amt") java.math.BigDecimal amt,
        @Param("profitLossFlag") String profitLossFlag,
        @Param("profitQty") java.math.BigDecimal profitQty,
        @Param("stockAmount") java.math.BigDecimal stockAmount,
        @Param("profitAmount") java.math.BigDecimal profitAmount,
        @Param("countedFlag") Integer countedFlag,
        @Param("batchNumber") String batchNumber,
        @Param("remark") String remark);

    /** 同仓库下除 excludeId 外未审核仓库盘点单（stock_type=501）业务单号，按 id 升序 */
    List<String> selectPendingWhStocktakingStockNos(@Param("warehouseId") Long warehouseId,
        @Param("excludeId") Long excludeId);
}

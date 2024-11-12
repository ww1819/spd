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
    public int deleteStkIoStocktakingById(Long id);

    /**
     * 批量删除盘点
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStkIoStocktakingByIds(Long[] ids);

    /**
     * 批量删除盘点明细
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStkIoStocktakingEntryByParenIds(Long[] ids);

    /**
     * 批量新增盘点明细
     *
     * @param stkIoStocktakingEntryList 盘点明细列表
     * @return 结果
     */
    public int batchStkIoStocktakingEntry(List<StkIoStocktakingEntry> stkIoStocktakingEntryList);


    /**
     * 通过盘点主键删除盘点明细信息
     *
     * @param id 盘点ID
     * @return 结果
     */
    public int deleteStkIoStocktakingEntryByParenId(Long id);

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
}

package com.spd.warehouse.service;

import java.util.List;
import com.spd.warehouse.domain.StkIoStocktaking;

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
     * 审核盘点信息
     * @param id
     * @return
     */
    int auditStkIoBill(String id);

    /**
     * 月结处理
     * @param beginDate
     * @param endDate
     * @return
     */
    List<StkIoStocktaking> getMonthHandleDataList(String beginDate, String endDate);
}

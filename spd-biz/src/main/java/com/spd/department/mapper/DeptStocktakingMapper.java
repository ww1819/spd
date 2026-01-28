package com.spd.department.mapper;

import java.util.List;
import com.spd.warehouse.domain.StkIoStocktaking;
import com.spd.warehouse.domain.StkIoStocktakingEntry;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 科室盘点Mapper接口
 *
 * @author spd
 * @date 2025-01-28
 */
@Mapper
@Repository
public interface DeptStocktakingMapper
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
     * 只查询科室盘点（departmentId不为空，warehouseId为空）
     *
     * @param stkIoStocktaking 科室盘点
     * @return 科室盘点集合
     */
    public List<StkIoStocktaking> selectDeptStocktakingList(StkIoStocktaking stkIoStocktaking);

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
     * 删除科室盘点
     *
     * @param id 科室盘点主键
     * @return 结果
     */
    public int deleteDeptStocktakingById(Long id);

    /**
     * 批量删除科室盘点
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDeptStocktakingByIds(Long[] ids);

    /**
     * 批量删除科室盘点明细
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDeptStocktakingEntryByParenIds(Long[] ids);

    /**
     * 批量新增科室盘点明细
     *
     * @param stkIoStocktakingEntryList 科室盘点明细列表
     * @return 结果
     */
    public int batchDeptStocktakingEntry(List<StkIoStocktakingEntry> stkIoStocktakingEntryList);

    /**
     * 通过科室盘点主键删除科室盘点明细信息
     *
     * @param id 科室盘点ID
     * @return 结果
     */
    public int deleteDeptStocktakingEntryByParenId(Long id);

    /**
     * 查询当天最大流水号
     * @param date
     * @return
     */
    String selectMaxBillNo(String date);
}

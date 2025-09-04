package com.spd.department.mapper;

import java.math.BigDecimal;
import java.util.List;
import com.spd.department.domain.StkDepInventory;
import com.spd.department.vo.InventorySummaryVo;
import com.spd.department.vo.DepartmentInOutDetailVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 科室库存Mapper接口
 *
 * @author spd
 * @date 2024-03-04
 */
@Mapper
@Repository
public interface StkDepInventoryMapper
{
    /**
     * 查询科室库存
     *
     * @param id 科室库存主键
     * @return 科室库存
     */
    public StkDepInventory selectStkDepInventoryById(Long id);

    /**
     * 查询科室库存列表
     *
     * @param stkDepInventory 科室库存
     * @return 科室库存集合
     */
    public List<StkDepInventory> selectStkDepInventoryList(StkDepInventory stkDepInventory);

    /**
     * 新增科室库存
     *
     * @param stkDepInventory 科室库存
     * @return 结果
     */
    public int insertStkDepInventory(StkDepInventory stkDepInventory);

    /**
     * 修改科室库存
     *
     * @param stkDepInventory 科室库存
     * @return 结果
     */
    public int updateStkDepInventory(StkDepInventory stkDepInventory);

    /**
     * 删除科室库存
     *
     * @param id 科室库存主键
     * @return 结果
     */
    public int deleteStkDepInventoryById(Long id);

    /**
     * 批量删除科室库存
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStkDepInventoryByIds(Long[] ids);

    /**
     * 按批次号查询科室库存
     * @param batchNo 批次号
     * @return 科室库存
     */
    StkDepInventory selectStkDepInventoryOne(String batchNo);

    /**
     * 根据批次号查询科室库存实际数量
     * @param batchNo
     * @return
     */
    BigDecimal selectTKStkInvntoryByBatchNo(String batchNo);

    /**
     * 查询库存汇总列表
     *
     * @param stkDepInventory 查询条件
     * @return 库存汇总集合
     */
    public List<InventorySummaryVo> selectInventorySummaryList(StkDepInventory stkDepInventory);

    /**
     * 查询科室进销存明细列表
     *
     * @param stkDepInventory 查询条件
     * @return 进销存明细集合
     */
    public List<DepartmentInOutDetailVo> selectDepartmentInOutDetailList(StkDepInventory stkDepInventory);
}

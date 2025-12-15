package com.spd.warehouse.mapper;

import java.util.List;
import java.util.Map;

import com.spd.common.core.page.TotalInfo;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.domain.StkIoBillEntry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 出入库Mapper接口
 *
 * @author spd
 * @date 2023-12-17
 */
@Mapper
@Repository
public interface StkIoBillMapper
{
    /**
     * 查询出入库
     *
     * @param id 出入库主键
     * @return 出入库
     */
    public StkIoBill selectStkIoBillById(Long id);

    /**
     * 查询出入库列表
     *
     * @param stkIoBill 出入库
     * @return 出入库集合
     */
    public List<StkIoBill> selectStkIoBillList(StkIoBill stkIoBill);


    /**
     * 查询出入库汇总
     *
     * @param stkIoBill 出入库
     * @return 出入库集合
     */
    public TotalInfo selectStkIoBillTotal(StkIoBill stkIoBill);

    /**
     * 新增出入库
     *
     * @param stkIoBill 出入库
     * @return 结果
     */
    public int insertStkIoBill(StkIoBill stkIoBill);

    /**
     * 修改出入库
     *
     * @param stkIoBill 出入库
     * @return 结果
     */
    public int updateStkIoBill(StkIoBill stkIoBill);

//    /**
//     * 删除出入库
//     *
//     * @param id 出入库主键
//     * @return 结果
//     */
//    public int deleteStkIoBillById(Long id);
//
//    /**
//     * 批量删除出入库
//     *
//     * @param ids 需要删除的数据主键集合
//     * @return 结果
//     */
//    public int deleteStkIoBillByIds(Long[] ids);

    /**
     * 批量删除出入库明细
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStkIoBillEntryByParenIds(Long[] ids);

    /**
     * 批量新增出入库明细
     *
     * @param stkIoBillEntryList 出入库明细列表
     * @return 结果
     */
    public int batchStkIoBillEntry(List<StkIoBillEntry> stkIoBillEntryList);


    /**
     * 通过出入库主键删除出入库明细信息
     *
     * @param id 出入库ID
     * @return 结果
     */
    public int deleteStkIoBillEntryByParenId(Long id);

    /**
     * 查询出入库表当天最大的单号
     * @param date 当天日期
     * @return
     */
    String selectMaxBillNo(String date);

    String selectOutMaxBillNo(String date);

    /**
     * 逻辑删除
     * @param stkIoBillEntry
     * @return
     */
    int updatestkIobillEntry(StkIoBillEntry stkIoBillEntry);

    /**
     * 查询出入库明细表耗材是否存在
     * @param id 耗材ID
     * @return
     */
    int selectStkIobillEntryMaterialIsExist(Long id);

    /**
     * 查询出入库表退货当天最大的单号
     * @param date 当天日期
     * @return
     */
    String selectTHMaxBillNo(String date);

    /**
     * 查询出入库表退库当天最大的单号
     * @param date 当天日期
     * @return
     */
    String selectTKMaxBillNo(String date);

    /**
     * 查询出入库表结算当天最大的单号
     * @param date 当天日期
     * @return
     */
    String selectJSMaxBillNo(String date);

    /**
     * 查询入退货列表
     * @param stkIoBill
     * @return
     */
    List<Map<String, Object>> selectRTHStkIoBillList(StkIoBill stkIoBill);

    /**
     * 查询出退库列表
     * @param stkIoBill
     * @return
     */
    List<Map<String, Object>> selectCTKStkIoBillList(StkIoBill stkIoBill);
    /**
     * 查询入退货汇总列表
     * @param stkIoBill
     * @return
     */
    List<Map<String, Object>> selectRTHStkIoBillSummaryList(StkIoBill stkIoBill);

    /**
     * 查询出退库汇总列表
     * @param stkIoBill
     * @return
     */
    List<Map<String, Object>> selectCTKStkIoBillListSummary(StkIoBill stkIoBill);

    /**
     * 查询历史库存
     * @param previousDateString
     * @return
     */
    List<Map<String, Object>> selectHistoryInventory(String previousDateString);

    /**
     * 查询进销存明细列表
     * @param stkIoBill
     * @return
     */
    List<Map<String, Object>> selectListPurInventory(StkIoBill stkIoBill);

    /**
     * 查询月结初始化列表
     * @return
     */
    List<Map<String, Object>> selectMonthInitDataList(@Param("beginDate") String beginDate,@Param("endDate") String endDate,
                                                      @Param("toStatDate") String toStatDate,@Param("toEndDate") String toEndDate);

    /**
     * 月结处理
     * @param beginDate
     * @param endDate
     * @return
     */
    List<StkIoBill> getMonthHandleDataList(@Param("beginDate") String beginDate,@Param("endDate") String endDate);

    /**
     * 查询结算明细：根据供应商、日期范围、仓库结算类型查询出库明细
     * @param stkIoBill 查询条件
     * @return 结算明细列表
     */
    List<StkIoBillEntry> selectSettlementDetails(StkIoBill stkIoBill);
}

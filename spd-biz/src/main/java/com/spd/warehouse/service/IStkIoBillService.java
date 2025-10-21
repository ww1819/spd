package com.spd.warehouse.service;

import java.util.List;
import java.util.Map;

import com.spd.warehouse.domain.StkIoBill;

/**
 * 出入库Service接口
 *
 * @author spd
 * @date 2023-12-17
 */
public interface IStkIoBillService
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
//     * 批量删除出入库
//     *
//     * @param ids 需要删除的出入库主键集合
//     * @return 结果
//     */
//    public int deleteStkIoBillByIds(Long[] ids);

    /**
     * 删除出入库信息
     *
     * @param id 出入库主键
     * @return 结果
     */
    public int deleteStkIoBillById(Long id);

    /**
     * 审核入库信息
     *
     * @param id
     * @return 结果
     */
    int auditStkIoBill(String id,String auditBy);

    /**
     * 新增出库
     *
     * @param stkIoBill 出库
     * @return 结果
     */
    public int insertOutStkIoBill(StkIoBill stkIoBill);

    /**
     * 修改出库
     *
     * @param stkIoBill 出库
     * @return 结果
     */
    public int updateOutStkIoBill(StkIoBill stkIoBill);

    /**
     * 新增退库
     * @param stkIoBill
     * @return
     */
    public int insertTkStkIoBill(StkIoBill stkIoBill);

    /**
     * 新增退货
     * @param stkIoBill
     * @return
     */
    public int insertTHStkIoBill(StkIoBill stkIoBill);

    /**
     * 修改退货
     * @param stkIoBill
     * @return
     */
    int updateTKStkIoBill(StkIoBill stkIoBill);

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
    List<Map<String, Object>> selectMonthInitDataList(String beginDate,String endDate,String toStatDate,String toEndDate);

    /**
     * 月结处理
     * @param beginDate
     * @param endDate
     * @return
     */
    List<StkIoBill> getMonthHandleDataList(String beginDate, String endDate);


    public StkIoBill createEntriesByDApply(String dApplyId);

    public StkIoBill createEntriesByRkApply(String rkApplyId);


}

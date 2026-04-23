package com.spd.warehouse.mapper;

import java.util.List;
import java.util.Map;

import com.spd.common.core.page.TotalInfo;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.domain.StkIoBillEntry;
import com.spd.warehouse.domain.vo.StkOutBillExportFlatRow;
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
     * 出库单按单导出：扁平行（bill_type=201）
     * @param billIds 非空时仅导出这些主键；空时按 q 条件筛选
     */
    List<StkOutBillExportFlatRow> selectOutBillGroupedExportRows(@Param("q") StkIoBill q, @Param("billIds") List<Long> billIds);

    /**
     * 查询出入库汇总
     *
     * @param stkIoBill 出入库
     * @return 出入库集合
     */
    public TotalInfo selectStkIoBillTotal(StkIoBill stkIoBill);

    /**
     * 按科室汇总出库总金额（出库单 bill_type=201 已审核 bill_status=2）
     *
     * @return 列表项：departmentId, departmentName, outboundAmount
     */
    List<Map<String, Object>> selectOutboundSummaryByDepartment();

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
     * 批量逻辑删除出入库明细（设置 del_flag=1, delete_by, delete_time）
     *
     * @param parenIds 主表ID集合
     * @param deleteBy 删除者
     * @param deleteTime 删除时间
     * @return 结果
     */
    public int deleteStkIoBillEntryByParenIds(@Param("parenIds") Long[] parenIds, @Param("deleteBy") String deleteBy, @Param("deleteTime") java.util.Date deleteTime);

    /**
     * 批量新增出入库明细
     *
     * @param stkIoBillEntryList 出入库明细列表
     * @return 结果
     */
    public int batchStkIoBillEntry(List<StkIoBillEntry> stkIoBillEntryList);


    /**
     * 通过出入库主键逻辑删除出入库明细信息（设置 del_flag=1, delete_by, delete_time）
     *
     * @param parenId 出入库ID
     * @param deleteBy 删除者
     * @param deleteTime 删除时间
     * @return 结果
     */
    public int deleteStkIoBillEntryByParenId(@Param("parenId") Long parenId, @Param("deleteBy") String deleteBy, @Param("deleteTime") java.util.Date deleteTime);

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

    /** 入库审核：反写仓库库存主键，kc_no 同步为兼容镜像 */
    int updateStkIoBillEntryInboundWhRef(@Param("id") Long id, @Param("stkInventoryId") Long stkInventoryId);

    /** 出库审核：反写来源仓库存 + 科室库存主键；kc_no 与 dep_inventory_id 同步（兼容旧接口） */
    int updateStkIoBillEntryOutboundAuditRefs(@Param("id") Long id, @Param("stkInventoryId") Long stkInventoryId, @Param("depInventoryId") Long depInventoryId);

    /** 收货确认等：仅补写科室库存主键 */
    int updateStkIoBillEntryDepInventoryRef(@Param("id") Long id, @Param("depInventoryId") Long depInventoryId);

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
     * 查询入退货列表合计
     * @param stkIoBill
     * @return
     */
    TotalInfo selectRTHStkIoBillListTotal(StkIoBill stkIoBill);

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
     * 查询出退库列表合计
     * @param stkIoBill
     * @return
     */
    TotalInfo selectCTKStkIoBillListTotal(StkIoBill stkIoBill);

    /**
     * 查询出退库汇总列表合计
     * @param stkIoBill
     * @return
     */
    TotalInfo selectCTKStkIoBillListSummaryTotal(StkIoBill stkIoBill);

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

    /**
     * 查询领用明细列表
     * @param stkIoBill 查询条件
     * @return 领用明细列表
     */
    List<Map<String, Object>> selectConsumeDetailList(StkIoBill stkIoBill);

    /**
     * 领用明细：当前筛选下数量、金额合计
     */
    TotalInfo selectConsumeDetailListTotal(StkIoBill stkIoBill);

    /**
     * 查询领用汇总列表（按耗材汇总）
     * @param stkIoBill 查询条件
     * @return 领用汇总列表
     */
    List<Map<String, Object>> selectConsumeSummaryList(StkIoBill stkIoBill);

    /**
     * 领用汇总：当前筛选下数量、金额合计
     */
    TotalInfo selectConsumeSummaryListTotal(StkIoBill stkIoBill);

    /**
     * 查询领用排名列表（按金额降序）
     * @param stkIoBill 查询条件
     * @return 领用排名列表
     */
    List<Map<String, Object>> selectConsumeRankingList(StkIoBill stkIoBill);

    /**
     * 领用排名：当前筛选下数量、金额合计
     */
    TotalInfo selectConsumeRankingListTotal(StkIoBill stkIoBill);

    /**
     * 查询仓库进销存
     * @param stkIoBill
     * @return
     */
    List<Map<String, Object>> selectWarehousePsiReport(StkIoBill stkIoBill);

    /**
     * 采购汇总报表（按供应商）
     */
    List<Map<String, Object>> selectPurchaseSummaryBySupplier(StkIoBill stkIoBill);

    /**
     * 查询产品档案入库供应商列表（仅已审核入库单 bill_type=101）
     */
    List<Map<String, Object>> selectMaterialInboundSuppliers(@Param("materialId") Long materialId);

    /**
     * 查询产品档案入库记录（仅已审核入库单 bill_type=101）
     */
    List<Map<String, Object>> selectMaterialInboundRecords(@Param("materialId") Long materialId,
                                                           @Param("supplierId") Long supplierId,
                                                           @Param("warehouseId") Long warehouseId,
                                                           @Param("auditBeginTime") String auditBeginTime,
                                                           @Param("auditEndTime") String auditEndTime,
                                                           @Param("orderMode") String orderMode);

    /**
     * 财务结算汇总：按材料/试剂 + 供货单位聚合批发金额（出退库 201/401，单价×数量）
     */
    List<Map<String, Object>> selectFinanceSettlementSupplierSummary(StkIoBill stkIoBill);
}

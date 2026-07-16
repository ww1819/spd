package com.spd.warehouse.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.spd.common.core.page.TotalInfo;
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
     * 按科室汇总当月出退库金额与数量（bill_type 201/401，已审核；大屏科室排名等）
     * @return 列表项：departmentId, departmentName, outboundAmount, outboundQuantity
     */
    List<Map<String, Object>> selectOutboundSummaryByDepartment();

    /** 大屏：本月送货入库前十供应商（已审核入退货按供应商汇总金额降序） */
    List<Map<String, Object>> selectBiScreenInboundSupplierTop10();

    /** 大屏：近 20 天入退货按日汇总金额（高值 is_gz=1 / 低值其余） */
    List<Map<String, Object>> selectBiScreenInboundDailyHighLowValue();

    /** 大屏：当月出退库按单个耗材汇总金额 TOP20（降序） */
    List<Map<String, Object>> selectBiScreenOutboundMaterialMonthTop();

    /** 大屏：当月入退货按财务分类汇总入库金额（降序） */
    List<Map<String, Object>> selectBiScreenInboundFinanceCategoryMonth();

    /** 首页/大屏：当月出退库按财务分类汇总出库金额（降序） */
    List<Map<String, Object>> selectBiScreenOutboundFinanceCategoryMonth();

    /** 大屏：今日已审核出库单笔数、今日已审核入库单(101)笔数 */
    Map<String, Object> selectBiScreenTodayInboundOutboundBillCount();

    /** 大屏：当年按自然月汇总入库金额(101)、退货入库金额(301) */
    List<Map<String, Object>> selectBiScreenYearInboundReturnByMonth();

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
     * 查询进销存明细合计
     * @param stkIoBill
     * @return
     */
    TotalInfo selectListPurInventoryTotal(StkIoBill stkIoBill);

    /**
     * 采购汇总报表（按供应商）
     */
    List<Map<String, Object>> selectPurchaseSummaryBySupplier(StkIoBill stkIoBill);

    /**
     * 耗材使用排名（出/退库净出库按耗材汇总）
     */
    List<com.spd.warehouse.domain.StkMaterialUsageRankVo> selectMaterialUsageRank(StkIoBill stkIoBill);

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

    /**
     * 引用科室申请但生成出库明细
     * @param dApplyId 科室申请单id
     * @return
     */

    public StkIoBill createCkEntriesByDApply(String dApplyId);

    /**
     * 引用仓库申请单（科室申领审核按仓拆分）生成出库明细
     * @param whWarehouseApplyId 仓库申请单主键 UUID7
     */
    StkIoBill createCkEntriesByWhApply(String whWarehouseApplyId);

    /**
     * 引用科室申购单生成出库明细草稿
     * @param depPurchaseApplyId 科室申购主表 ID
     */
    StkIoBill createCkEntriesByDepPurchaseApply(Long depPurchaseApplyId);

    /**
     * 引用入库单生成出库明细
     * @param rkApplyId 入库单id
     * @return
     */
    public StkIoBill createCkEntriesByRkApply(String rkApplyId);

    /**
     * 引用订单生成入库单明细
     * @param dingdanId 订单id
     * @return
     */
    public StkIoBill createRkEntriesByDingdan(String dingdanId);

    /**
     * 引用配送单生成入库单明细
     * @param deliveryNo 配送单号
     * @return
     */
    StkIoBill createRkEntriesByDeliveryNo(String deliveryNo);

    /**
     * 引用入库单生成退货单明细
     * @param rkApplyId 入库单id
     * @return
     */
    public StkIoBill createThEntriesByRkApply(String rkApplyId);

    /**
     * 引用出库单生成退库单明细
     * @param ckApplyId 出库单id
     * @return
     */
    public StkIoBill createTkEntriesByCkApply(String ckApplyId);

    /**
     * 引用科室退库单生成退货明细
     * @param tkApplyId
     * @return
     */
    public StkIoBill createThEntriesByTkApply(String tkApplyId);

    /**
     * 查询结算明细：根据供应商、日期范围、仓库结算类型查询出库明细
     * @param stkIoBill 查询条件
     * @return 结算明细列表
     */
    public List<com.spd.warehouse.domain.StkIoBillEntry> selectSettlementDetails(StkIoBill stkIoBill);

    /**
     * 批量确认收货
     * @param ids 出库单ID列表（逗号分隔）
     * @param confirmBy 确认人
     * @return 结果
     */
    int confirmReceipt(String ids, String confirmBy);

    /**
     * 出库单导出：按单据隔离，每单标题（单据号、科室名称）+ 明细（名称、规格、型号、单位、数量、批号、有效期）
     */
    void exportOutWarehouseGroupedByBill(StkIoBill stkIoBill, HttpServletResponse response) throws IOException;

    /** 出退库查询：非机构管理员按科室数据权限过滤（params.scopeDeptUserId，见 Mapper stkIoBillDepartmentScopeFilter） */
    void applyCtkDepartmentScopeToQuery(StkIoBill stkIoBill);

    /**
     * 出退库明细整体导出：单工作表，首行标题「出退库明细_统计时间…」，列与枣强出退库明细表样式一致（非按供应商拆文件）
     */
    void exportCTKOverallDetailXlsx(StkIoBill stkIoBill, HttpServletResponse response) throws IOException;

    /**
     * 消息提醒：已审核出库至科室、收货未确认（与科室收货确认列表口径一致，最多 500 条）
     */
    List<StkIoBill> selectDepartmentUnreceivedReceiptReminderList();

    long countDepartmentUnreceivedReceiptReminder();

    /**
     * 首页仓库采购图：单月、多仓库聚合（入退货 + 出退库）
     */
    List<Map<String, Object>> selectHomeWarehousePurchaseMonthAgg(Date beginDate, Date endDate, List<Long> warehouseIds);

    /**
     * 首页科室使用图：按审核月聚合全年出退库（租户级，不按当前用户科室数据范围过滤）
     */
    List<Map<String, Object>> selectHomeDepartmentReceiveYearMonthAgg(Date beginDate, Date endDate);

    /**
     * 高值即入即出结算流水：写入已审核 101/201/301/401 + hc_ck_flow，不写 stk_inventory / 科室库存。
     */
    Long insertHighValueSettlementBill(StkIoBill bill, String billNoPrefix);

    /**
     * 记录单据打印（更新打印时间、打印人）
     *
     * @param id 出入库单主键
     * @return 结果
     */
    int recordStkIoBillPrint(Long id);
}

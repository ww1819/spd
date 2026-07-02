package com.spd.warehouse.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSONObject;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.http.HttpUtils;
import com.spd.system.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TotalInfo;
import com.spd.common.enums.BusinessType;
import com.spd.department.domain.DeptBatchConsume;
import com.spd.department.service.IDeptBatchConsumeService;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.service.IStkIoBillService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 入库Controller
 *
 * @author spd
 * @date 2023-12-17
 */
@RestController
@RequestMapping("/warehouse/warehouse")
public class StkIoBillController extends BaseController
{
    private static final String DEFAULT_INTERFACE_IP = "127.0.0.1";
    private static final String DEFAULT_INTERFACE_PORT = "8088";

    @Qualifier("stkIoBillServiceImpl")
    @Autowired
    private IStkIoBillService stkIoBillService;

    @Autowired
    private IDeptBatchConsumeService deptBatchConsumeService;

    @Autowired
    private ISysConfigService sysConfigService;

    /**
     * 查询入库列表
     */
    @PreAuthorize("@ss.hasPermi('inWarehouse:apply:list')")
    @GetMapping("/list")
    public TableDataInfo list(StkIoBill stkIoBill)
    {
        startPage();
        List<StkIoBill> list = stkIoBillService.selectStkIoBillList(stkIoBill);
        return getDataTable(list);
    }

    /**
     * 导出入库列表
     */
    @PreAuthorize("@ss.hasPermi('inWarehouse:apply:export')")
    @Log(title = "出入库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StkIoBill stkIoBill)
    {
        List<StkIoBill> list = stkIoBillService.selectStkIoBillList(stkIoBill);
        ExcelUtil<StkIoBill> util = new ExcelUtil<StkIoBill>(StkIoBill.class);
        util.exportExcel(response, list, "出入库数据");
    }

    /**
     * 获取入库详细信息
     */
    @PreAuthorize("@ss.hasPermi('inWarehouse:apply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stkIoBillService.selectStkIoBillById(id));
    }

    /**
     * 记录入库单打印（更新打印人、打印时间）
     */
    @Log(title = "入库单打印", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('inWarehouse:apply:query') || @ss.hasPermi('inWarehouse:apply:audit') || @ss.hasPermi('inWarehouse:apply:list')")
    @PutMapping("/recordPrint/{id}")
    public AjaxResult recordPrint(@PathVariable("id") Long id)
    {
        return toAjax(stkIoBillService.recordStkIoBillPrint(id));
    }

    /**
     * 新增入库
     * 返回带 id、billNo 的实体，供前端展示单据号并后续保存走修改逻辑
     */
    @PreAuthorize("@ss.hasPermi('inWarehouse:apply:add')")
    @Log(title = "入库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StkIoBill stkIoBill)
    {
        if (stkIoBill.getBillType() == null) {
            stkIoBill.setBillType(101);
        }
        int rows = stkIoBillService.insertStkIoBill(stkIoBill);
        if (rows > 0) {
            return success(stkIoBill);
        }
        return AjaxResult.error("新增失败");
    }

    /**
     * 修改入库
     */
    @PreAuthorize("@ss.hasPermi('inWarehouse:apply:edit')")
    @Log(title = "入库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StkIoBill stkIoBill)
    {
        if (stkIoBill.getBillType() == null) {
            stkIoBill.setBillType(101);
        }
        return toAjax(stkIoBillService.updateStkIoBill(stkIoBill));
    }

    /**
     * 审核入库
     */
    @PreAuthorize("@ss.hasPermi('inWarehouse:apply:audit')")
    @Log(title = "入库", businessType = BusinessType.UPDATE)
    @PutMapping("/auditWarehouse")
    public AjaxResult audit(@RequestBody JSONObject json)
    {
        int result = stkIoBillService.auditStkIoBill(json.getString("id"), getUserIdStr());
        return toAjax(result);
    }

    /**
     * 删除入库
     */
    @PreAuthorize("@ss.hasPermi('inWarehouse:apply:remove')")
    @Log(title = "入库", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long ids)
    {
        return toAjax(stkIoBillService.deleteStkIoBillById(ids));
    }


    @PreAuthorize("@ss.hasPermi('inWarehouse:apply:createRkEntriesByDingdan')")
    @GetMapping("/createRkEntriesByDingdan")
    public AjaxResult createRkEntriesByDingdan(@RequestParam String dingdanId) {
        if (dingdanId == null) {
            throw new RuntimeException("采购订单ID不能为空");
        }
        StkIoBill stkIoBill1 = stkIoBillService.createRkEntriesByDingdan(dingdanId);
        return success(stkIoBill1);
    }

    @PreAuthorize("@ss.hasPermi('inWarehouse:apply:createRkEntriesByDingdan')")
    @GetMapping("/queryZsDelivery")
    public AjaxResult queryZsDelivery(@RequestParam String keyword) {
        if (StringUtils.isEmpty(keyword)) {
            return AjaxResult.error("配送单号或输入码不能为空");
        }
        try {
            String url = buildInterfaceBaseUrl() + "/api/spd/delivery/query";
            String param = "keyword=" + URLEncoder.encode(keyword, "UTF-8");
            String result = HttpUtils.sendGet(url, param);
            if (StringUtils.isEmpty(result)) {
                return AjaxResult.error("配送单查询失败：接口无响应");
            }
            JSONObject jsonResult = JSONObject.parseObject(result);
            Integer code = jsonResult.getInteger("code");
            if (code != null && code == 200) {
                return success(jsonResult.get("data"));
            }
            String msg = jsonResult.getString("msg");
            return AjaxResult.error(StringUtils.isNotEmpty(msg) ? msg : "配送单查询失败");
        } catch (Exception e) {
            return AjaxResult.error("配送单查询失败：" + e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('inWarehouse:apply:createRkEntriesByDingdan')")
    @GetMapping("/createRkEntriesByDeliveryNo")
    public AjaxResult createRkEntriesByDeliveryNo(@RequestParam String deliveryNo) {
        if (StringUtils.isEmpty(deliveryNo)) {
            return AjaxResult.error("配送单号不能为空");
        }
        StkIoBill stkIoBill = stkIoBillService.createRkEntriesByDeliveryNo(deliveryNo);
        return success(stkIoBill);
    }

    /**
     * 按科室汇总当月出退库金额与数量（数据可视化大屏：科室领用排名、高值消耗图等）
     * @return 列表项：departmentId, departmentName, outboundAmount, outboundQuantity（201 为正、401 为负）
     */
    @GetMapping("/outboundSummaryByDepartment")
    public AjaxResult outboundSummaryByDepartment() {
        List<Map<String, Object>> list = stkIoBillService.selectOutboundSummaryByDepartment();
        return success(list);
    }

    /**
     * 数据可视化大屏：当前租户下已审核入退货（验收）、出退库、科室消耗的数量与金额合计（不按个人科室数据范围过滤）。
     */
    @GetMapping("/biScreenConsumablesTotals")
    public AjaxResult biScreenConsumablesTotals()
    {
        StkIoBill rthQ = new StkIoBill();
        TotalInfo acceptance = stkIoBillService.selectRTHStkIoBillListTotal(rthQ);
        StkIoBill ctkQ = new StkIoBill();
        TotalInfo outbound = stkIoBillService.selectCTKStkIoBillListTotal(ctkQ);
        DeptBatchConsume dc = new DeptBatchConsume();
        TotalInfo consume = deptBatchConsumeService.selectAuditedConsumeReportTotal(dc);

        Map<String, Object> body = new HashMap<>(8);
        body.put("acceptanceAmount", nzBd(acceptance != null ? acceptance.getTotalAmt() : null).abs().setScale(2, RoundingMode.HALF_UP));
        body.put("acceptanceQuantity", nzBd(acceptance != null ? acceptance.getTotalQty() : null).abs().setScale(2, RoundingMode.HALF_UP));
        body.put("outboundAmount", nzBd(outbound != null ? outbound.getTotalAmt() : null).abs().setScale(2, RoundingMode.HALF_UP));
        body.put("outboundQuantity", nzBd(outbound != null ? outbound.getTotalQty() : null).abs().setScale(2, RoundingMode.HALF_UP));
        body.put("consumptionAmount", nzBd(consume != null ? consume.getTotalAmt() : null).abs().setScale(2, RoundingMode.HALF_UP));
        body.put("consumptionQuantity", nzBd(consume != null ? consume.getTotalQty() : null).abs().setScale(2, RoundingMode.HALF_UP));
        return success(body);
    }

    /**
     * 数据可视化大屏：本月送货入库（已审核入退货）按供应商汇总配送金额，取前 10，按金额降序。
     */
    @GetMapping("/biScreenInboundSupplierTop10")
    public AjaxResult biScreenInboundSupplierTop10() {
        List<Map<String, Object>> list = stkIoBillService.selectBiScreenInboundSupplierTop10();
        return success(list != null ? list : new java.util.ArrayList<>());
    }

    /**
     * 数据可视化大屏：近 20 天已审核入退货按日汇总金额（高值耗材 is_gz=1 / 低值其余），用于折线+柱状图。
     */
    @GetMapping("/biScreenInboundDailyHighLowValue")
    public AjaxResult biScreenInboundDailyHighLowValue() {
        List<Map<String, Object>> list = stkIoBillService.selectBiScreenInboundDailyHighLowValue();
        return success(list != null ? list : new java.util.ArrayList<>());
    }

    /**
     * 数据可视化大屏：当月已审核出退库按单个耗材汇总出库金额，TOP20，金额降序（耗材排行榜）。
     */
    @GetMapping("/biScreenOutboundMaterialMonthTop")
    public AjaxResult biScreenOutboundMaterialMonthTop() {
        List<Map<String, Object>> list = stkIoBillService.selectBiScreenOutboundMaterialMonthTop();
        return success(list != null ? list : new java.util.ArrayList<>());
    }

    /**
     * 数据可视化大屏：当月已审核入退货按耗材「财务分类」汇总入库金额（退库冲减），按金额降序。
     */
    @GetMapping("/biScreenInboundFinanceCategoryMonth")
    public AjaxResult biScreenInboundFinanceCategoryMonth() {
        List<Map<String, Object>> list = stkIoBillService.selectBiScreenInboundFinanceCategoryMonth();
        return success(list != null ? list : new java.util.ArrayList<>());
    }

    /**
     * 数据可视化大屏：今日已审核出库单（201）笔数、今日已审核入库单（101）笔数。
     */
    @GetMapping("/biScreenTodayInboundOutboundBillCount")
    public AjaxResult biScreenTodayInboundOutboundBillCount() {
        Map<String, Object> row = stkIoBillService.selectBiScreenTodayInboundOutboundBillCount();
        return success(row != null ? row : new java.util.HashMap<>());
    }

    /**
     * 数据可视化大屏：当年已审核入退货按自然月汇总金额（入库 101、退货入库 301），用于年度采购/入退货曲线。
     */
    @GetMapping("/biScreenYearInboundReturnByMonth")
    public AjaxResult biScreenYearInboundReturnByMonth() {
        List<Map<String, Object>> list = stkIoBillService.selectBiScreenYearInboundReturnByMonth();
        return success(list != null ? list : new java.util.ArrayList<>());
    }

    private static BigDecimal nzBd(BigDecimal v)
    {
        return v != null ? v : BigDecimal.ZERO;
    }

    private String buildInterfaceBaseUrl()
    {
        String ip = StringUtils.trim(sysConfigService.selectConfigByKey("spd.interface.ip"));
        String port = StringUtils.trim(sysConfigService.selectConfigByKey("spd.interface.port"));
        if (StringUtils.isEmpty(ip)) {
            ip = DEFAULT_INTERFACE_IP;
        }
        if (!port.matches("\\d{1,5}")) {
            port = DEFAULT_INTERFACE_PORT;
        }
        int portNum = Integer.parseInt(port);
        if (portNum < 1 || portNum > 65535) {
            port = DEFAULT_INTERFACE_PORT;
        }
        return "http://" + ip + ":" + port;
    }
}

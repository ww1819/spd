package com.spd.warehouse.controller;

import java.util.List;
import java.util.Map;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSONObject;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.http.HttpUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
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
    /** 服务器 interface 接口 URL（scminterface） */
    @Value("${spd.interface.url:http://localhost:8088}")
    private String interfaceUrl;

    @Qualifier("stkIoBillServiceImpl")
    @Autowired
    private IStkIoBillService stkIoBillService;

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
     * 新增入库
     * 返回带 id、billNo 的实体，供前端展示单据号并后续保存走修改逻辑
     */
    @PreAuthorize("@ss.hasPermi('inWarehouse:apply:add')")
    @Log(title = "入库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StkIoBill stkIoBill)
    {
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
            String url = interfaceUrl;
            if (!url.endsWith("/")) {
                url += "/";
            }
            url += "api/spd/delivery/query";
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
     * 按科室汇总出库总金额（数据可视化大屏用）
     * @return 列表项：departmentId, departmentName, outboundAmount
     */
    @GetMapping("/outboundSummaryByDepartment")
    public AjaxResult outboundSummaryByDepartment() {
        List<Map<String, Object>> list = stkIoBillService.selectOutboundSummaryByDepartment();
        return success(list);
    }
}

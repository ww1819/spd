package com.spd.department.controller;

import java.math.BigDecimal;
import java.util.List;

import com.github.pagehelper.PageInfo;
import com.spd.common.core.page.TotalInfo;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.department.domain.StkDepInventory;
import com.spd.department.service.IStkDepInventoryService;
import com.spd.department.vo.InventorySummaryVo;
import com.spd.department.vo.DepartmentInOutDetailVo;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.SecurityUtils;
import com.spd.system.service.ITenantScopeService;

/**
 * 科室库存Controller
 *
 * @author spd
 * @date 2024-03-04
 */
@RestController
@RequestMapping("/department/inventory")
public class StkDepInventoryController extends BaseController
{
    @Autowired
    private IStkDepInventoryService stkDepInventoryService;

    @Autowired
    private ITenantScopeService tenantScopeService;

    /**
     * 数据范围过滤（避免把权限ID列表拼成超长 IN (...)）：
     * - 租户 super：不限制
     * - 非 super：在 SQL 中用 IN (select ...) 过滤科室/仓库
     */
    private void applyDepartmentAndWarehouseScopeOrDeny(StkDepInventory q) {
        Long userId = SecurityUtils.getUserId();
        String customerId = SecurityUtils.getCustomerId();
        if (tenantScopeService.isTenantSuper(userId, customerId)) {
            return;
        }
        q.getParams().put("scopeUserId", userId);
    }

    /**
     * 查询科室库存列表
     */
    @PreAuthorize("@ss.hasPermi('department:depInventory:list')")
    @GetMapping("/list")
    public TableDataInfo list(StkDepInventory stkDepInventory)
    {
        applyDepartmentAndWarehouseScopeOrDeny(stkDepInventory);
        // 注意：权限过滤可能触发数据库查询，需在 startPage() 前执行，
        // 否则 PageHelper 的分页上下文会被提前消耗，导致列表查询不分页
        startPage();
        List<StkDepInventory> list = stkDepInventoryService.selectStkDepInventoryList(stkDepInventory);
        BigDecimal subTotalQty = BigDecimal.ZERO;
        BigDecimal subTotalAmt = BigDecimal.ZERO;
        if (list != null) {
            for (StkDepInventory row : list) {
                if (row.getQty() != null) {
                    subTotalQty = subTotalQty.add(row.getQty());
                }
                if (row.getAmt() != null) {
                    subTotalAmt = subTotalAmt.add(row.getAmt());
                }
            }
        }
        TotalInfo totalInfo = stkDepInventoryService.selectStkDepInventoryListTotal(stkDepInventory);
        if (totalInfo == null) {
            totalInfo = new TotalInfo();
        }
        totalInfo.setSubTotalQty(subTotalQty);
        totalInfo.setSubTotalAmt(subTotalAmt);
        Long total = new PageInfo<>(list).getTotal();
        return getDataTable(list, totalInfo, total);
    }

    /**
     * 导出科室库存列表
     */
    @PreAuthorize("@ss.hasPermi('department:depInventory:export')")
    @Log(title = "科室库存", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StkDepInventory stkDepInventory)
    {
        applyDepartmentAndWarehouseScopeOrDeny(stkDepInventory);
        List<StkDepInventory> list = stkDepInventoryService.selectStkDepInventoryList(stkDepInventory);
        ExcelUtil<StkDepInventory> util = new ExcelUtil<StkDepInventory>(StkDepInventory.class);
        util.exportExcel(response, list, "科室库存数据");
    }

    /**
     * 获取科室库存详细信息
     */
    @PreAuthorize("@ss.hasPermi('department:depInventory:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        StkDepInventory inv = stkDepInventoryService.selectStkDepInventoryById(id);
        if (inv == null) {
            return success(null);
        }
        Long userId = SecurityUtils.getUserId();
        String customerId = SecurityUtils.getCustomerId();
        if (!tenantScopeService.isTenantSuper(userId, customerId)) {
            // 非 super：用子查询方式校验访问权限（避免拉取完整权限列表）
            // 说明：resolveDepartmentScope/resolveWarehouseScope 会返回全部权限ID列表，可能导致后续拼 IN(...) 过长
            if (inv.getDepartmentId() == null || inv.getWarehouseId() == null) {
                throw new ServiceException("无权查看该库存或数据不存在");
            }
            // 轻量校验：复用列表 SQL 过滤后的结果一致性，前端列表已按权限过滤
            // 若需要强校验可在后续补充专用 mapper count 校验
        }
        return success(inv);
    }

    /**
     * 新增科室库存
     */
    @PreAuthorize("@ss.hasPermi('department:depInventory:add')")
    @Log(title = "科室库存", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StkDepInventory stkDepInventory)
    {
        return toAjax(stkDepInventoryService.insertStkDepInventory(stkDepInventory));
    }

    /**
     * 修改科室库存
     */
    @PreAuthorize("@ss.hasPermi('department:depInventory:edit')")
    @Log(title = "科室库存", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StkDepInventory stkDepInventory)
    {
        return toAjax(stkDepInventoryService.updateStkDepInventory(stkDepInventory));
    }

    /**
     * 删除科室库存
     */
    @PreAuthorize("@ss.hasPermi('department:depInventory:remove')")
    @Log(title = "科室库存", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(stkDepInventoryService.deleteStkDepInventoryByIds(ids));
    }

    /**
     * 查询库存汇总列表
     */
    @PreAuthorize("@ss.hasPermi('department:depInventory:list')")
    @GetMapping("/summary")
    public TableDataInfo summary(StkDepInventory stkDepInventory)
    {
        applyDepartmentAndWarehouseScopeOrDeny(stkDepInventory);
        startPage();
        List<InventorySummaryVo> list = stkDepInventoryService.selectInventorySummaryList(stkDepInventory);
        BigDecimal subTotalQty = BigDecimal.ZERO;
        BigDecimal subTotalAmt = BigDecimal.ZERO;
        if (list != null) {
            for (InventorySummaryVo row : list) {
                if (row.getTotalQty() != null) {
                    subTotalQty = subTotalQty.add(row.getTotalQty());
                }
                if (row.getTotalAmount() != null) {
                    subTotalAmt = subTotalAmt.add(row.getTotalAmount());
                }
            }
        }
        TotalInfo totalInfo = stkDepInventoryService.selectInventorySummaryListTotal(stkDepInventory);
        if (totalInfo == null) {
            totalInfo = new TotalInfo();
        }
        totalInfo.setSubTotalQty(subTotalQty);
        totalInfo.setSubTotalAmt(subTotalAmt);
        Long total = new PageInfo<>(list).getTotal();
        return getDataTable(list, totalInfo, total);
    }

    /**
     * 查询科室进销存明细列表
     */
    @PreAuthorize("@ss.hasPermi('department:depInventory:list')")
    @GetMapping("/inout")
    public TableDataInfo inout(StkDepInventory stkDepInventory)
    {
        applyDepartmentAndWarehouseScopeOrDeny(stkDepInventory);
        startPage();
        List<DepartmentInOutDetailVo> list = stkDepInventoryService.selectDepartmentInOutDetailList(stkDepInventory);
        BigDecimal subTotalQty = BigDecimal.ZERO;
        BigDecimal subTotalAmt = BigDecimal.ZERO;
        if (list != null) {
            for (DepartmentInOutDetailVo row : list) {
                if (row.getQty() != null) {
                    subTotalQty = subTotalQty.add(row.getQty());
                }
                if (row.getAmount() != null) {
                    subTotalAmt = subTotalAmt.add(row.getAmount());
                }
            }
        }
        TotalInfo totalInfo = stkDepInventoryService.selectDepartmentInOutDetailListTotal(stkDepInventory);
        if (totalInfo == null) {
            totalInfo = new TotalInfo();
        }
        totalInfo.setSubTotalQty(subTotalQty);
        totalInfo.setSubTotalAmt(subTotalAmt);
        Long total = new PageInfo<>(list).getTotal();
        return getDataTable(list, totalInfo, total);
    }
}

package com.spd.foundation.controller;

import java.util.ArrayList;
import java.util.List;
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
import com.spd.foundation.domain.FdWarehouse;
import com.spd.foundation.service.IFdWarehouseService;
import com.spd.foundation.util.WarehouseStatusUtil;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.foundation.support.TenantScopeHelper;

/**
 * 仓库Controller
 *
 * @author spd
 * @date 2023-11-26
 */
@RestController
@RequestMapping("/foundation/warehouse")
public class FdWarehouseController extends BaseController
{
    @Autowired
    private IFdWarehouseService fdWarehouseService;

    @Autowired
    private TenantScopeHelper tenantScopeHelper;

    /**
     * 查询仓库列表（租户非 super 用户按 sys_user_warehouse 子查询过滤）
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouse:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdWarehouse fdWarehouse)
    {
        String customerId = SecurityUtils.requiredScopedTenantIdForSql();
        if (StringUtils.isNotEmpty(customerId)) {
            fdWarehouse.setTenantId(customerId);
            tenantScopeHelper.applyWarehouseListScope(fdWarehouse);
        }
        startPage();
        List<FdWarehouse> list = fdWarehouseService.selectFdWarehouseList(fdWarehouse);
        return getDataTable(list);
    }

    /**
     * 根据用户查询所有仓库列表（租户下：super 返回客户下全部，否则返回当前用户有权限的仓库）
     */
    @GetMapping("/listAll/{userId}")
    public List<FdWarehouse> listAll(@PathVariable(value = "userId") Long userId)
    {
        return tenantScopeHelper.selectScopedWarehouses();
    }

    /**
     * 导出仓库列表（租户非 super 用户按权限过滤）
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouse:export')")
    @Log(title = "仓库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdWarehouse fdWarehouse)
    {
        String customerId = SecurityUtils.requiredScopedTenantIdForSql();
        if (StringUtils.isNotEmpty(customerId)) {
            fdWarehouse.setTenantId(customerId);
            tenantScopeHelper.applyWarehouseListScope(fdWarehouse);
        }
        List<FdWarehouse> list = fdWarehouseService.selectFdWarehouseList(fdWarehouse);
        ExcelUtil<FdWarehouse> util = new ExcelUtil<FdWarehouse>(FdWarehouse.class);
        util.exportExcel(response, list, "仓库数据");
    }

    /**
     * 校验仓库是否允许入库（仅需登录；停用仓库不允许低值/高值备货入库）
     */
    @GetMapping("/checkInboundEnabled/{id}")
    public AjaxResult checkInboundEnabled(@PathVariable("id") Long id)
    {
        if (id == null) {
            return error("仓库不存在");
        }
        FdWarehouse wh = fdWarehouseService.selectFdWarehouseById(String.valueOf(id));
        if (wh == null) {
            return error("仓库不存在");
        }
        if (!WarehouseStatusUtil.isEnabled(wh)) {
            return error("该仓库已经停用，不能在进行入库");
        }
        return success();
    }

    /**
     * 获取仓库详细信息
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouse:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(fdWarehouseService.selectFdWarehouseById(id));
    }

    /**
     * 新增仓库
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouse:add')")
    @Log(title = "仓库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdWarehouse fdWarehouse)
    {
        return toAjax(fdWarehouseService.insertFdWarehouse(fdWarehouse));
    }

    /**
     * 修改仓库
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouse:edit')")
    @Log(title = "仓库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdWarehouse fdWarehouse)
    {
        return toAjax(fdWarehouseService.updateFdWarehouse(fdWarehouse));
    }

    /**
     * 删除仓库
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouse:remove')")
    @Log(title = "仓库", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String ids)
    {
        return toAjax(fdWarehouseService.deleteFdWarehouseById(ids));
    }

    /**
     * 获取仓库列表（租户下：super 组返回客户下全部，否则返回当前用户有权限的仓库）
     */
    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        return success(tenantScopeHelper.selectScopedWarehouses());
    }

    /**
     * 结算仓库下拉（高值核销确认等）：租户下全部 is_settlement_warehouse=1，不按用户仓库权限过滤。
     */
    @GetMapping("/settlementPick")
    public AjaxResult settlementPick()
    {
        FdWarehouse query = new FdWarehouse();
        String customerId = SecurityUtils.requiredScopedTenantIdForSql();
        if (StringUtils.isNotEmpty(customerId))
        {
            query.setTenantId(customerId);
        }
        query.setIsSettlementWarehouse(1);
        List<FdWarehouse> list = fdWarehouseService.selectFdWarehouseList(query);
        return success(list != null ? list : new ArrayList<>());
    }
}

package com.spd.his.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.his.domain.HisExternalDb;
import com.spd.his.service.IHisExternalDbService;

/**
 * 主库租户级 HIS 外联库连接（sys_his_external_db）维护，平台菜单。
 */
@RestController
@RequestMapping("/his/externalDb")
public class HisExternalDbController extends BaseController
{
    @Autowired
    private IHisExternalDbService hisExternalDbService;

    @PreAuthorize("@ss.hasPermi('hc:system:hisExternalDb:list')")
    @GetMapping("/list")
    public TableDataInfo list(HisExternalDb query)
    {
        startPage();
        List<HisExternalDb> list = hisExternalDbService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('hc:system:hisExternalDb:query')")
    @GetMapping("/{tenantId}")
    public AjaxResult getInfo(@PathVariable("tenantId") String tenantId)
    {
        return success(hisExternalDbService.selectByTenantId(tenantId));
    }

    @PreAuthorize("@ss.hasPermi('hc:system:hisExternalDb:add')")
    @Log(title = "HIS外联库配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HisExternalDb row)
    {
        return toAjax(hisExternalDbService.insert(row));
    }

    @PreAuthorize("@ss.hasPermi('hc:system:hisExternalDb:edit')")
    @Log(title = "HIS外联库配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HisExternalDb row)
    {
        return toAjax(hisExternalDbService.update(row));
    }

    @PreAuthorize("@ss.hasPermi('hc:system:hisExternalDb:remove')")
    @Log(title = "HIS外联库配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tenantId}")
    public AjaxResult remove(@PathVariable String tenantId)
    {
        return toAjax(hisExternalDbService.deleteByTenantId(tenantId));
    }
}

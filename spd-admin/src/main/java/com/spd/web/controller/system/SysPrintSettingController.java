package com.spd.web.controller.system;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.SecurityUtils;
import com.spd.system.domain.SysPrintSetting;
import com.spd.system.service.ISysPrintSettingService;

/**
 * 打印设置 信息操作处理
 * 
 * @author spd
 */
@RestController
@RequestMapping("/system/printSetting")
public class SysPrintSettingController extends BaseController
{
    @Autowired
    private ISysPrintSettingService sysPrintSettingService;

    /**
     * 查询打印设置列表
     */
    @PreAuthorize("@ss.hasPermi('system:printSetting:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysPrintSetting sysPrintSetting)
    {
        startPage();
        List<SysPrintSetting> list = sysPrintSettingService.selectSysPrintSettingList(sysPrintSetting);
        return getDataTable(list);
    }

    /**
     * 根据打印设置编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:printSetting:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sysPrintSettingService.selectSysPrintSettingById(id));
    }

    /**
     * 根据单据类型获取生效的默认模板（先租户专属，再全库默认）。
     * 可选 tenantId 覆盖（如运维指定客户）；否则使用请求头/登录态解析的租户。
     */
    @GetMapping(value = "/getDefault/{billType}")
    public AjaxResult getDefault(@PathVariable("billType") Integer billType,
            @RequestParam(value = "tenantId", required = false) String tenantId)
    {
        String tid = StringUtils.isNotEmpty(tenantId) ? tenantId.trim() : SecurityUtils.resolveEffectiveTenantId(null);
        SysPrintSetting setting = sysPrintSettingService.selectEffectiveDefault(billType, tid);
        return success(setting);
    }

    /**
     * 新增打印设置
     */
    @PreAuthorize("@ss.hasPermi('system:printSetting:add')")
    @Log(title = "打印设置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysPrintSetting sysPrintSetting)
    {
        sysPrintSetting.setCreateBy(getUserIdStr());
        return toAjax(sysPrintSettingService.insertSysPrintSetting(sysPrintSetting));
    }

    /**
     * 修改打印设置
     */
    @PreAuthorize("@ss.hasPermi('system:printSetting:edit')")
    @Log(title = "打印设置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysPrintSetting sysPrintSetting)
    {
        sysPrintSetting.setUpdateBy(getUserIdStr());
        return toAjax(sysPrintSettingService.updateSysPrintSetting(sysPrintSetting));
    }

    /**
     * 删除打印设置
     */
    @PreAuthorize("@ss.hasPermi('system:printSetting:remove')")
    @Log(title = "打印设置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysPrintSettingService.deleteSysPrintSettingByIds(ids));
    }

    /**
     * 设置默认模板
     */
    @PreAuthorize("@ss.hasPermi('system:printSetting:edit')")
    @Log(title = "打印设置", businessType = BusinessType.UPDATE)
    @PutMapping("/setDefault")
    public AjaxResult setDefault(@RequestBody SysPrintSetting sysPrintSetting)
    {
        sysPrintSetting.setUpdateBy(getUserIdStr());
        return toAjax(sysPrintSettingService.setDefaultTemplate(sysPrintSetting));
    }
}

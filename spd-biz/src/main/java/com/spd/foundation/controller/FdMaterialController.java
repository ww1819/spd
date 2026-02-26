package com.spd.foundation.controller;

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
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.service.IFdMaterialService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.utils.file.FileUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.util.Map;

/**
 * 耗材产品Controller
 *
 * @author spd
 * @date 2023-12-23
 */
@RestController
@RequestMapping("/foundation/material")
public class FdMaterialController extends BaseController
{
    @Autowired
    private IFdMaterialService fdMaterialService;

    /** 服务器interface接口URL */
    @Value("${spd.interface.url:http://localhost:8081}")
    private String interfaceUrl;

    /**
     * 查询耗材产品列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdMaterial fdMaterial)
    {
        startPage();
        List<FdMaterial> list = fdMaterialService.selectFdMaterialList(fdMaterial);
        return getDataTable(list);
    }

    /**
     * 查询所有耗材产品列表
     */
    @GetMapping("/listAll")
    public List<FdMaterial> listAll(FdMaterial fdMaterial)
    {
        List<FdMaterial> list = fdMaterialService.selectFdMaterialList(fdMaterial);
        return list;
    }

    /**
     * 查询所有耗材产品列表（支持分页，避免一次返回过多数据）
     *
     * 说明：
     * - 前端可通过 pageNum、pageSize 传参控制分页；
     * - 返回结果结构与 /list 一致：{ rows: [...], total: n }。
     * - 不改动原有 /listAll 接口，避免影响已有依赖。
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:list')")
    @GetMapping("/listAllPage")
    public TableDataInfo listAllPage(FdMaterial fdMaterial)
    {
        // 读取前端传入的 pageNum / pageSize，并开启分页
        startPage();
        List<FdMaterial> list = fdMaterialService.selectFdMaterialList(fdMaterial);
        // 封装为带 total 的分页对象返回
        return getDataTable(list);
    }

    /**
     * 导出耗材产品列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:export')")
    @Log(title = "耗材产品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdMaterial fdMaterial)
    {
        List<FdMaterial> list = fdMaterialService.selectFdMaterialList(fdMaterial);
        ExcelUtil<FdMaterial> util = new ExcelUtil<FdMaterial>(FdMaterial.class);
        util.exportExcel(response, list, "耗材产品数据");
    }

    /**
     * 获取耗材产品详细信息
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(fdMaterialService.selectFdMaterialById(id));
    }

    /**
     * 新增耗材产品
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:add')")
    @Log(title = "耗材产品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdMaterial fdMaterial)
    {
        return toAjax(fdMaterialService.insertFdMaterial(fdMaterial));
    }

    /**
     * 修改耗材产品
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:edit')")
    @Log(title = "耗材产品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdMaterial fdMaterial)
    {
        return toAjax(fdMaterialService.updateFdMaterial(fdMaterial));
    }

    /**
     * 删除耗材产品
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:remove')")
    @Log(title = "耗材产品", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long ids)
    {
        return toAjax(fdMaterialService.deleteFdMaterialByIds(ids));
    }

    @Log(title = "耗材产品导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:material:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<FdMaterial> util = new ExcelUtil<FdMaterial>(FdMaterial.class);
        List<FdMaterial> fdmaterialList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = fdMaterialService.importFdMaterial(fdmaterialList, updateSupport, operName);
        return success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws Exception
    {
        ExcelUtil<FdMaterial> util = new ExcelUtil<FdMaterial>(FdMaterial.class);
        // 设置文件名响应头
        FileUtils.setAttachmentResponseHeader(response, "耗材产品档案基础字典导入.xlsx");
        util.importTemplateExcel(response, "耗材数据");
    }

    /**
     * 批量更新耗材产品名称简码
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:updateReferred')")
    @Log(title = "耗材产品", businessType = BusinessType.UPDATE)
    @PostMapping("/updateReferred")
    public AjaxResult updateReferred(@RequestBody Map<String, List<Long>> body)
    {
        List<Long> ids = body.get("ids");
        if (ids == null || ids.isEmpty())
        {
            return error("ID 列表不能为空");
        }
        fdMaterialService.updateReferred(ids);
        return success("更新简码成功");
    }

    /**
     * 产品档案停用（记录停用时间、停用人、停用原因）
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:edit')")
    @Log(title = "耗材产品停用", businessType = BusinessType.UPDATE)
    @PutMapping("/disable")
    public AjaxResult disable(@RequestBody Map<String, Object> params) {
        Long id = params.get("id") != null ? Long.valueOf(params.get("id").toString()) : null;
        String reason = params.get("reason") != null ? params.get("reason").toString() : null;
        if (id == null) {
            return error("产品档案ID不能为空");
        }
        if (reason == null || reason.trim().isEmpty()) {
            return error("请填写停用原因");
        }
        fdMaterialService.disableMaterial(id, reason.trim());
        return success();
    }

    /**
     * 产品档案启用（记录启用时间、启用人、启用原因）
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:edit')")
    @Log(title = "耗材产品启用", businessType = BusinessType.UPDATE)
    @PutMapping("/enable")
    public AjaxResult enable(@RequestBody Map<String, Object> params) {
        Long id = params.get("id") != null ? Long.valueOf(params.get("id").toString()) : null;
        String reason = params.get("reason") != null ? params.get("reason").toString() : null;
        if (id == null) {
            return error("产品档案ID不能为空");
        }
        if (reason == null || reason.trim().isEmpty()) {
            return error("请填写启用原因");
        }
        fdMaterialService.enableMaterial(id, reason.trim());
        return success();
    }

    /**
     * 查询产品档案启用停用记录列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:query')")
    @GetMapping("/{id}/statusLog")
    public AjaxResult listStatusLog(@PathVariable("id") Long id) {
        return success(fdMaterialService.listStatusLogByMaterialId(id));
    }

    /**
     * 查询产品档案变更记录列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:query')")
    @GetMapping("/{id}/changeLog")
    public AjaxResult listChangeLog(@PathVariable("id") Long id) {
        return success(fdMaterialService.listChangeLogByMaterialId(id));
    }

    /**
     * 查询产品档案时间轴（合并启用停用与变更记录，按时间倒序）
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:query')")
    @GetMapping("/{id}/timeline")
    public AjaxResult getTimeline(@PathVariable("id") Long id) {
        return success(fdMaterialService.getMaterialTimeline(id));
    }

    /**
     * 推送档案
     * 调用服务器interface接口推送供应商档案
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:push')")
    @Log(title = "推送档案", businessType = BusinessType.OTHER)
    @PostMapping("/pushArchive")
    public AjaxResult pushArchive(@RequestBody Map<String, Object> params)
    {
        try
        {
            Long supplierId = null;
            if (params.get("supplierId") instanceof Number)
            {
                supplierId = ((Number) params.get("supplierId")).longValue();
            }
            else if (params.get("supplierId") != null)
            {
                supplierId = Long.parseLong(params.get("supplierId").toString());
            }

            if (supplierId == null)
            {
                return error("供应商ID不能为空");
            }

            // 调用服务器interface接口
            String url = interfaceUrl;
            if (!url.endsWith("/"))
            {
                url += "/";
            }
            url += "api/spd/pushSupplier";
            
            Map<String, Object> requestData = new java.util.HashMap<>();
            requestData.put("supplierId", supplierId);
            
            String jsonData = com.alibaba.fastjson2.JSON.toJSONString(requestData);
            String result = com.spd.common.utils.http.HttpUtils.sendPost(url, jsonData, "application/json;charset=UTF-8");
            
            // 解析返回结果
            com.alibaba.fastjson2.JSONObject jsonResult = com.alibaba.fastjson2.JSON.parseObject(result);
            Integer code = jsonResult.getInteger("code");
            String msg = jsonResult.getString("msg");
            
            if (code != null && code == 200)
            {
                return success(jsonResult.get("data"));
            }
            else
            {
                return error(msg != null ? msg : "推送失败");
            }
        }
        catch (Exception e)
        {
            return error("推送档案失败: " + e.getMessage());
        }
    }


}

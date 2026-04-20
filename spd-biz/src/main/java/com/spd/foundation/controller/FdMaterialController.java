package com.spd.foundation.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.constant.HttpStatus;
import com.spd.common.enums.BusinessType;
import com.github.pagehelper.PageHelper;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdMaterialListRequest;
import com.spd.foundation.dto.MaterialImportAddDto;
import com.spd.foundation.dto.MaterialImportUpdateDto;
import com.spd.foundation.service.IFdMaterialService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.utils.file.FileUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.util.Map;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.his.config.HisTenantDbHandle;
import com.spd.his.config.HisTenantJdbcAccess;

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
    private static final String HS_THIRD_TENANT_ID = "hengsui-third-001";

    @Autowired
    private IFdMaterialService fdMaterialService;

    @Autowired
    private HisTenantJdbcAccess hisTenantJdbcAccess;

    /** 服务器interface接口URL */
    @Value("${spd.interface.url:http://localhost:8081}")
    private String interfaceUrl;

    /**
     * 查询耗材产品列表（GET）
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
     * 查询耗材产品列表（POST，请求体传参，避免 excludeMaterialIds 等过长导致 400/414）
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:list')")
    @PostMapping("/list")
    public TableDataInfo listPost(@RequestBody FdMaterialListRequest request)
    {
        Integer pageNum = request.getPageNum() != null ? request.getPageNum() : 1;
        Integer pageSize = request.getPageSize() != null ? request.getPageSize() : 10;
        FdMaterial query = request.getQuery() != null ? request.getQuery() : new FdMaterial();
        PageHelper.startPage(pageNum, pageSize);
        List<FdMaterial> list = fdMaterialService.selectFdMaterialList(query);
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
     * 科室/临床模块专用产品档案低敏列表（仅必要字段，避免返回完整产品档案）
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/listDeptSafe")
    public List<Map<String, Object>> listDeptSafe(@RequestParam(value = "name", required = false) String name)
    {
        FdMaterial query = new FdMaterial();
        if (StringUtils.isNotEmpty(name))
        {
            query.setName(name.trim());
        }
        List<FdMaterial> list = fdMaterialService.selectFdMaterialList(query);
        List<Map<String, Object>> safeList = new ArrayList<>();
        for (FdMaterial material : list)
        {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", material.getId());
            item.put("name", material.getName());
            item.put("code", material.getCode());
            item.put("supplierId", material.getSupplierId());
            item.put("referredName", material.getReferredName());
            item.put("speci", material.getSpeci());
            item.put("model", material.getModel());
            item.put("brand", material.getBrand());
            safeList.add(item);
        }
        return safeList;
    }

    /**
     * HIS 收费项目列表（仅衡水三院租户，来源：v_charge_item）
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:query')")
    @GetMapping("/hisChargeItem/list")
    public TableDataInfo listHisChargeItem(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "speci", required = false) String speci,
        @RequestParam(value = "pageNum", required = false) Integer pageNum,
        @RequestParam(value = "pageSize", required = false) Integer pageSize)
    {
        String tenantId = SecurityUtils.getCustomerId();
        if (!HS_THIRD_TENANT_ID.equals(tenantId))
        {
            return getDataTable(new ArrayList<>());
        }

        if (pageNum == null || pageNum < 1)
        {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1)
        {
            pageSize = 10;
        }
        int offset = (pageNum - 1) * pageSize;
        String nameLike = StringUtils.isNotEmpty(name) ? "%" + name.trim() + "%" : null;
        String speciLike = StringUtils.isNotEmpty(speci) ? "%" + speci.trim() + "%" : null;

        HisTenantDbHandle hisDb = hisTenantJdbcAccess.obtainHandle(tenantId);
        String dbType = hisDb.getDbTypeNormalized();
        String baseWhere = " from v_charge_item where 1=1 "
            + " and (? is null or item_name like ?) "
            + " and (? is null or spec_model like ?) ";
        String countSql = "select count(1) " + baseWhere;
        Long total = hisDb.getJdbcTemplate().queryForObject(
            countSql,
            new Object[]{nameLike, nameLike, speciLike, speciLike},
            Long.class);

        String listSql;
        Object[] listArgs;
        if ("MYSQL".equalsIgnoreCase(dbType))
        {
            listSql = "select charge_item_id, item_code, item_name, spec_model, price " + baseWhere
                + " order by charge_item_id limit ? offset ?";
            listArgs = new Object[]{nameLike, nameLike, speciLike, speciLike, pageSize, offset};
        }
        else
        {
            listSql = "select charge_item_id, item_code, item_name, spec_model, price " + baseWhere
                + " order by charge_item_id offset ? rows fetch next ? rows only";
            listArgs = new Object[]{nameLike, nameLike, speciLike, speciLike, offset, pageSize};
        }

        List<Map<String, Object>> rows = hisDb.getJdbcTemplate().query(listSql, listArgs, (rs, i) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("chargeItemId", rs.getString("charge_item_id"));
            item.put("chargeCode", rs.getString("item_code"));
            item.put("chargeName", rs.getString("item_name"));
            item.put("chargeSpeci", rs.getString("spec_model"));
            item.put("chargeModel", rs.getString("spec_model"));
            item.put("chargePrice", rs.getBigDecimal("price"));
            return item;
        });

        TableDataInfo rsp = new TableDataInfo();
        rsp.setCode(HttpStatus.SUCCESS);
        rsp.setRows(rows);
        rsp.setTotal(total == null ? 0L : total);
        return rsp;
    }

    /**
     * 绑定耗材与 HIS 收费项目（fd_material.his_id = v_charge_item.charge_item_id）
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:edit')")
    @PostMapping("/bindHisChargeItem")
    public AjaxResult bindHisChargeItem(@RequestBody Map<String, Object> body)
    {
        String tenantId = SecurityUtils.getCustomerId();
        if (!HS_THIRD_TENANT_ID.equals(tenantId))
        {
            return error("当前租户未启用 HIS 收费项目对照");
        }
        if (body == null || body.get("materialId") == null || body.get("chargeItemId") == null)
        {
            return error("materialId 和 chargeItemId 不能为空");
        }

        Long materialId = Long.valueOf(String.valueOf(body.get("materialId")));
        String chargeItemId = String.valueOf(body.get("chargeItemId")).trim();
        if (StringUtils.isEmpty(chargeItemId))
        {
            return error("chargeItemId 不能为空");
        }

        FdMaterial db = fdMaterialService.selectFdMaterialById(materialId);
        if (db == null)
        {
            return error("耗材不存在");
        }
        db.setHisId(chargeItemId);
        db.setUpdateBy(getUsername());
        return toAjax(fdMaterialService.updateFdMaterial(db));
    }

    /**
     * 解绑耗材与 HIS 收费项目（清空 fd_material.his_id）
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:edit')")
    @PostMapping("/unbindHisChargeItem")
    public AjaxResult unbindHisChargeItem(@RequestBody Map<String, Object> body)
    {
        String tenantId = SecurityUtils.getCustomerId();
        if (!HS_THIRD_TENANT_ID.equals(tenantId))
        {
            return error("当前租户未启用 HIS 收费项目对照");
        }
        if (body == null || body.get("materialId") == null)
        {
            return error("materialId 不能为空");
        }
        Long materialId = Long.valueOf(String.valueOf(body.get("materialId")));
        FdMaterial db = fdMaterialService.selectFdMaterialById(materialId);
        if (db == null)
        {
            return error("耗材不存在");
        }
        db.setHisId(null);
        db.setUpdateBy(getUsername());
        return toAjax(fdMaterialService.updateFdMaterial(db));
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
     * 根据主条码(udi_no)或耗材编码查询产品档案（用于入库单扫码带出产品）
     */
    @GetMapping("/getByMainBarcode")
    public AjaxResult getByMainBarcode(@RequestParam String mainBarcode)
    {
        return success(fdMaterialService.getByMainBarcode(mainBarcode));
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

    /** 耗材档案新增导入模板（不含 SPD 主键；含数据校验结果列供回填） */
    @PreAuthorize("@ss.hasPermi('foundation:material:import')")
    @PostMapping("/importAddTemplate")
    public void importAddTemplate(HttpServletResponse response) throws Exception
    {
        ExcelUtil<MaterialImportAddDto> util = new ExcelUtil<>(MaterialImportAddDto.class);
        FileUtils.setAttachmentResponseHeader(response, "耗材档案新增导入模板.xlsx");
        util.importTemplateExcel(response, "耗材档案新增导入");
    }

    /** 耗材档案更新导入模板 */
    @PreAuthorize("@ss.hasPermi('foundation:material:import')")
    @PostMapping("/importUpdateTemplate")
    public void importUpdateTemplate(HttpServletResponse response) throws Exception
    {
        ExcelUtil<MaterialImportUpdateDto> util = new ExcelUtil<>(MaterialImportUpdateDto.class);
        FileUtils.setAttachmentResponseHeader(response, "耗材档案更新导入模板.xlsx");
        util.importTemplateExcel(response, "耗材档案更新导入");
    }

    /** 耗材档案新增导入：仅校验 */
    @PreAuthorize("@ss.hasPermi('foundation:material:import')")
    @PostMapping("/importAddValidate")
    public AjaxResult importAddValidate(MultipartFile file) throws Exception
    {
        ExcelUtil<MaterialImportAddDto> util = new ExcelUtil<>(MaterialImportAddDto.class);
        List<MaterialImportAddDto> list = util.importExcel(file.getInputStream());
        Map<String, Object> data = fdMaterialService.validateMaterialImportAdd(list);
        if (!Boolean.TRUE.equals(data.get("valid")))
        {
            return AjaxResult.success("校验未通过", data);
        }
        return AjaxResult.success("校验通过，请确认后导入", data);
    }

    /** 耗材档案更新导入：仅校验 */
    @PreAuthorize("@ss.hasPermi('foundation:material:import')")
    @PostMapping("/importUpdateValidate")
    public AjaxResult importUpdateValidate(MultipartFile file) throws Exception
    {
        ExcelUtil<MaterialImportUpdateDto> util = new ExcelUtil<>(MaterialImportUpdateDto.class);
        List<MaterialImportUpdateDto> list = util.importExcel(file.getInputStream());
        Map<String, Object> data = fdMaterialService.validateMaterialImportUpdate(list);
        if (!Boolean.TRUE.equals(data.get("valid")))
        {
            return AjaxResult.success("校验未通过", data);
        }
        return AjaxResult.success("校验通过，请确认后导入", data);
    }

    @Log(title = "耗材档案新增导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:material:import')")
    @PostMapping("/importAddData")
    public AjaxResult importAddData(MultipartFile file,
        @RequestParam(value = "confirm", defaultValue = "false") boolean confirm) throws Exception
    {
        ExcelUtil<MaterialImportAddDto> util = new ExcelUtil<>(MaterialImportAddDto.class);
        List<MaterialImportAddDto> list = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = fdMaterialService.importMaterialImportAdd(list, operName, confirm);
        return AjaxResult.success(message, ExcelUtil.buildImportCommitSummaryMap(list != null ? list.size() : 0));
    }

    @Log(title = "耗材档案更新导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:material:import')")
    @PostMapping("/importUpdateData")
    public AjaxResult importUpdateData(MultipartFile file,
        @RequestParam(value = "confirm", defaultValue = "false") boolean confirm) throws Exception
    {
        ExcelUtil<MaterialImportUpdateDto> util = new ExcelUtil<>(MaterialImportUpdateDto.class);
        List<MaterialImportUpdateDto> list = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = fdMaterialService.importMaterialImportUpdate(list, operName, confirm);
        return AjaxResult.success(message, ExcelUtil.buildImportCommitSummaryMap(list != null ? list.size() : 0));
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
     * 查询产品档案入库记录-供应商树
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:query')")
    @GetMapping("/{id}/inboundSuppliers")
    public AjaxResult listInboundSuppliers(@PathVariable("id") Long id) {
        return success(fdMaterialService.listInboundSuppliers(id));
    }

    /**
     * 查询产品档案入库记录
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:query')")
    @GetMapping("/{id}/inboundRecords")
    public AjaxResult listInboundRecords(@PathVariable("id") Long id,
                                         @RequestParam(value = "supplierId", required = false) Long supplierId,
                                         @RequestParam(value = "orderMode", required = false) String orderMode) {
        return success(fdMaterialService.listInboundRecords(id, supplierId, orderMode));
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

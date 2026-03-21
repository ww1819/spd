package com.spd.foundation.controller;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
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
import org.springframework.web.multipart.MultipartFile;
import com.spd.common.annotation.Log;
import com.spd.common.enums.TenantEnum;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.domain.entity.SysUser;
import com.spd.common.enums.BusinessType;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdDepartmentChangeLog;
import com.spd.foundation.domain.vo.FdDepartmentTreeNode;
import com.spd.foundation.dto.DepartmentImportUpdateDto;
import com.spd.foundation.service.IFdDepartmentService;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.PinyinUtils;
import com.spd.system.service.ITenantScopeService;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.utils.poi.ImportRowErrorCollector;
import com.spd.common.core.page.TableDataInfo;

/**
 * 科室Controller
 *
 * @author spd
 * @date 2023-11-26
 */
@RestController
@RequestMapping("/foundation/depart")
public class FdDepartmentController extends BaseController
{
    @Autowired
    private IFdDepartmentService fdDepartmentService;

    @Autowired
    private ITenantScopeService tenantScopeService;

    /** 租户与科室数据权限（与列表一致） */
    private void applyTenantDepartmentListScope(FdDepartment fdDepartment)
    {
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isNotEmpty(customerId))
        {
            fdDepartment.setTenantId(customerId);
        }
        if (StringUtils.isNotEmpty(customerId) && !tenantScopeService.isTenantSuper(SecurityUtils.getUserId(), customerId))
        {
            List<Long> allowedIds = tenantScopeService.resolveDepartmentScope(SecurityUtils.getUserId(), customerId);
            if (allowedIds != null && !allowedIds.isEmpty())
            {
                fdDepartment.getParams().put("allowedDeptIds", allowedIds);
            }
            else
            {
                fdDepartment.getParams().put("allowedDeptIds", new ArrayList<Long>());
            }
        }
    }

    /**
     * 查询科室列表（租户非 super：耗材端按 sys_user_department，设备端按 sb_user_permission_dept，见 {@link ITenantScopeService#resolveDepartmentScope}）；
     * 可选 treeParentId：仅查该上级下的直接子科室；不传则与点击客户根节点一致，为当前用户可见的全部科室。
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdDepartment fdDepartment)
    {
        applyTenantDepartmentListScope(fdDepartment);
        startPage();
        List<FdDepartment> list = fdDepartmentService.selectFdDepartmentList(fdDepartment);
        return getDataTable(list);
    }

    /**
     * 科室维护左侧树：根节点为客户名称（无租户时为「全部科室」），子树为当前用户有权限的科室层级。
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:list')")
    @GetMapping("/tree")
    public AjaxResult departmentTree()
    {
        FdDepartment q = new FdDepartment();
        applyTenantDepartmentListScope(q);
        List<FdDepartment> flat = fdDepartmentService.selectFdDepartmentList(q);
        List<FdDepartmentTreeNode> tree = fdDepartmentService.buildDepartmentTreeWithCustomerRoot(flat);
        return success(tree);
    }

    /**
     * 查询所有科室列表（租户下：super 组返回客户下全部，否则返回当前用户有权限的科室）
     */
    @GetMapping("/listAll/{userId}")
    public List<FdDepartment> listAll(@PathVariable(value = "userId") Long userId)
    {
        String customerId = SecurityUtils.getCustomerId();
        List<FdDepartment> list;
        if (StringUtils.isNotEmpty(customerId)) {
            list = fdDepartmentService.selectdepartmenAll();
            if (list != null && !tenantScopeService.isTenantSuper(SecurityUtils.getUserId(), customerId)) {
                List<Long> allowedIds = tenantScopeService.resolveDepartmentScope(SecurityUtils.getUserId(), customerId);
                if (allowedIds == null || allowedIds.isEmpty()) list = new ArrayList<>();
                else list = list.stream().filter(d -> d.getId() != null && allowedIds.contains(d.getId())).collect(Collectors.toList());
            }
        } else {
            if (SysUser.isAdmin(userId)) list = fdDepartmentService.selectdepartmenAll();
            else list = fdDepartmentService.selectUserDepartmenAll(userId);
        }
        return list != null ? list : new ArrayList<>();
    }

    /**
     * 导出科室列表（租户非 super 组用户按权限过滤）
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:export')")
    @Log(title = "科室", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdDepartment fdDepartment)
    {
        applyTenantDepartmentListScope(fdDepartment);
        List<FdDepartment> list = fdDepartmentService.selectFdDepartmentList(fdDepartment);
        ExcelUtil<FdDepartment> util = new ExcelUtil<FdDepartment>(FdDepartment.class);
        util.exportExcel(response, list, "科室数据");
    }

    /**
     * 获取科室详细信息（仅本客户可查）
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        FdDepartment dept = fdDepartmentService.selectFdDepartmentById(id);
        if (dept == null) {
            return error("科室不存在");
        }
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isNotEmpty(customerId) && !customerId.equals(dept.getTenantId())) {
            return error("无权查看非本客户的科室");
        }
        if (StringUtils.isNotEmpty(customerId) && dept.getId() != null
            && !tenantScopeService.isTenantSuper(SecurityUtils.getUserId(), customerId)) {
            List<Long> allowedIds = tenantScopeService.resolveDepartmentScope(SecurityUtils.getUserId(), customerId);
            if (allowedIds == null || allowedIds.isEmpty() || !allowedIds.contains(dept.getId())) {
                return error("无权查看该科室");
            }
        }
        return success(dept);
    }

    /**
     * 新增科室
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:add')")
    @Log(title = "科室", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdDepartment fdDepartment)
    {
        // 非衡水租户：手工新增不接收第三方科室 ID（仅导入等场景由服务层写入）；衡水市第三人民医院手工新增须填 HIS/第三方科室 ID
        if (TenantEnum.HS_003 != TenantEnum.fromCustomerId(SecurityUtils.getCustomerId())) {
            fdDepartment.setHisId(null);
        }
        return toAjax(fdDepartmentService.insertFdDepartment(fdDepartment));
    }

    /**
     * 修改科室
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:edit')")
    @Log(title = "科室", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdDepartment fdDepartment)
    {
        return toAjax(fdDepartmentService.updateFdDepartment(fdDepartment));
    }

    /**
     * 删除科室（支持单个或逗号分隔多个 id）
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:remove')")
    @Log(title = "科室", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String ids)
    {
        if (StringUtils.isEmpty(ids)) {
            return error("科室 id 不能为空");
        }
        String[] idArr = ids.split(",");
        int n = 0;
        for (String id : idArr) {
            if (StringUtils.isEmpty(id)) {
                continue;
            }
            n += fdDepartmentService.deleteFdDepartmentById(id.trim());
        }
        return toAjax(n);
    }

    /**
     * 获取科室列表（租户下：super 组返回客户下全部，否则返回当前用户有权限的科室）
     */
    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        List<FdDepartment> fdDepartmentList = fdDepartmentService.selectdepartmenAll();
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isNotEmpty(customerId) && fdDepartmentList != null && !tenantScopeService.isTenantSuper(SecurityUtils.getUserId(), customerId)) {
            List<Long> allowedIds = tenantScopeService.resolveDepartmentScope(SecurityUtils.getUserId(), customerId);
            if (allowedIds == null || allowedIds.isEmpty()) fdDepartmentList = new ArrayList<>();
            else fdDepartmentList = fdDepartmentList.stream().filter(d -> d.getId() != null && allowedIds.contains(d.getId())).collect(Collectors.toList());
        }
        return success(fdDepartmentList != null ? fdDepartmentList : new ArrayList<>());
    }

    /**
     * 科室变更记录（字段级）
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:list')")
    @GetMapping("/changeLog/{deptId}")
    public AjaxResult changeLog(@PathVariable("deptId") Long deptId)
    {
        if (deptId == null) {
            return error("科室 id 无效");
        }
        FdDepartment dept = fdDepartmentService.selectFdDepartmentById(String.valueOf(deptId));
        if (dept == null) {
            return error("科室不存在");
        }
        String customerId = SecurityUtils.getCustomerId();
        if (StringUtils.isNotEmpty(customerId) && !customerId.equals(dept.getTenantId())) {
            return error("无权查看非本客户的科室");
        }
        if (StringUtils.isNotEmpty(customerId) && !tenantScopeService.isTenantSuper(SecurityUtils.getUserId(), customerId)) {
            List<Long> allowedIds = tenantScopeService.resolveDepartmentScope(SecurityUtils.getUserId(), customerId);
            if (allowedIds == null || allowedIds.isEmpty() || !allowedIds.contains(deptId)) {
                return error("无权查看该科室的变更记录");
            }
        }
        List<FdDepartmentChangeLog> logs = fdDepartmentService.selectDepartmentChangeLog(deptId);
        return success(logs);
    }

    /**
     * 批量更新科室名称简码
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:edit') || @ss.hasPermi('foundation:depart:updateReferred')")
    @Log(title = "科室", businessType = BusinessType.UPDATE)
    @PostMapping("/updateReferred")
    public AjaxResult updateReferred(@RequestBody java.util.Map<String, java.util.List<Long>> body)
    {
        java.util.List<Long> ids = body.get("ids");
        if (ids == null || ids.isEmpty())
        {
            return error("ID 列表不能为空");
        }
        fdDepartmentService.updateReferred(ids);
        return success("更新简码成功");
    }

    /**
     * 科室导入：仅校验不落库（须全部通过后再由用户确认调用 importData?confirm=true）
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:import')")
    @PostMapping("/importValidate")
    public AjaxResult importValidate(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<FdDepartment> util = new ExcelUtil<FdDepartment>(FdDepartment.class);
        List<FdDepartment> list = util.importExcel(file.getInputStream());
        Map<String, Object> data = fdDepartmentService.validateFdDepartmentImport(list, updateSupport);
        if (!Boolean.TRUE.equals(data.get("valid")))
        {
            return AjaxResult.success("校验未通过", data);
        }
        return AjaxResult.success("校验通过，请确认后导入", data);
    }

    /**
     * 科室 Excel 导入（须先 importValidate 通过，且 confirm=true）
     */
    @Log(title = "科室导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:depart:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport,
        @RequestParam(value = "confirm", defaultValue = "false") boolean confirm) throws Exception
    {
        ExcelUtil<FdDepartment> util = new ExcelUtil<FdDepartment>(FdDepartment.class);
        List<FdDepartment> list = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = fdDepartmentService.importFdDepartment(list, updateSupport, operName, confirm);
        java.util.Map<String, Object> preview = new LinkedHashMap<>();
        preview.put("previewRows", ExcelUtil.buildImportPreviewMaps(FdDepartment.class, list));
        return AjaxResult.success(message, preview);
    }

    /**
     * 科室导入模板下载
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws Exception
    {
        ExcelUtil<FdDepartment> util = new ExcelUtil<FdDepartment>(FdDepartment.class);
        util.importTemplateExcel(response, "科室数据");
    }

    @PreAuthorize("@ss.hasPermi('foundation:depart:import')")
    @PostMapping("/importAddValidate")
    public AjaxResult importAddValidate(MultipartFile file) throws Exception
    {
        return importValidate(file, false);
    }

    @Log(title = "科室新增导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:depart:import')")
    @PostMapping("/importAddData")
    public AjaxResult importAddData(MultipartFile file,
        @RequestParam(value = "confirm", defaultValue = "false") boolean confirm) throws Exception
    {
        return importData(file, false, confirm);
    }

    @PostMapping("/importAddTemplate")
    public void importAddTemplate(HttpServletResponse response) throws Exception
    {
        importTemplate(response);
    }

    @PreAuthorize("@ss.hasPermi('foundation:depart:import')")
    @PostMapping("/importUpdateValidate")
    public AjaxResult importUpdateValidate(MultipartFile file) throws Exception
    {
        ExcelUtil<DepartmentImportUpdateDto> util = new ExcelUtil<DepartmentImportUpdateDto>(DepartmentImportUpdateDto.class);
        List<DepartmentImportUpdateDto> list = util.importExcel(file.getInputStream());
        Map<String, Object> data = validateDepartmentUpdateRows(list);
        if (!Boolean.TRUE.equals(data.get("valid")))
        {
            return AjaxResult.success("校验未通过", data);
        }
        return AjaxResult.success("校验通过，请确认后导入", data);
    }

    @Log(title = "科室更新导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:depart:import')")
    @PostMapping("/importUpdateData")
    public AjaxResult importUpdateData(MultipartFile file,
        @RequestParam(value = "confirm", defaultValue = "false") boolean confirm) throws Exception
    {
        if (!confirm)
        {
            return AjaxResult.error("请先完成校验并在确认后再导入");
        }
        ExcelUtil<DepartmentImportUpdateDto> util = new ExcelUtil<DepartmentImportUpdateDto>(DepartmentImportUpdateDto.class);
        List<DepartmentImportUpdateDto> list = util.importExcel(file.getInputStream());
        Map<String, Object> data = validateDepartmentUpdateRows(list);
        if (!Boolean.TRUE.equals(data.get("valid")))
        {
            return AjaxResult.error("数据校验未通过：" + String.valueOf(data.get("errors")));
        }
        int successNum = 0;
        StringBuilder msg = new StringBuilder();
        for (DepartmentImportUpdateDto row : list)
        {
            if (row == null || row.getId() == null)
            {
                continue;
            }
            FdDepartment existing = fdDepartmentService.selectFdDepartmentById(String.valueOf(row.getId()));
            existing.setName(row.getName().trim());
            existing.setReferredName(PinyinUtils.getPinyinInitials(existing.getName()));
            existing.setUpdateBy(getUsername());
            fdDepartmentService.updateFdDepartment(existing);
            successNum++;
            msg.append("<br/>").append(successNum).append("、科室 ").append(existing.getName()).append(" 更新成功");
        }
        msg.insert(0, "更新导入完成。共处理 " + successNum + " 条，明细如下：");
        for (DepartmentImportUpdateDto row : list)
        {
            if (row != null && row.getId() != null)
            {
                row.setValidationResult("更新成功");
            }
        }
        java.util.Map<String, Object> preview = new LinkedHashMap<>();
        preview.put("previewRows", ExcelUtil.buildImportPreviewMaps(DepartmentImportUpdateDto.class, list));
        return AjaxResult.success(msg.toString(), preview);
    }

    @PostMapping("/importUpdateTemplate")
    public void importUpdateTemplate(HttpServletResponse response) throws Exception
    {
        ExcelUtil<DepartmentImportUpdateDto> util = new ExcelUtil<DepartmentImportUpdateDto>(DepartmentImportUpdateDto.class);
        util.importTemplateExcel(response, "科室更新导入模板");
    }

    private Map<String, Object> validateDepartmentUpdateRows(List<DepartmentImportUpdateDto> list)
    {
        clearDepartmentUpdateDtoValidation(list);
        Map<String, Object> result = new LinkedHashMap<>();
        ImportRowErrorCollector c = new ImportRowErrorCollector();
        if (list == null || list.isEmpty())
        {
            c.addGlobal("导入数据不能为空");
        }
        else
        {
            String customerId = SecurityUtils.getCustomerId();
            for (int i = 0; i < list.size(); i++)
            {
                DepartmentImportUpdateDto row = list.get(i);
                int excelRow = i + 2;
                if (row == null || row.getId() == null)
                {
                    c.addRow(excelRow, "主键科室ID不能为空");
                    continue;
                }
                FdDepartment existing = fdDepartmentService.selectFdDepartmentById(String.valueOf(row.getId()));
                if (existing == null || (StringUtils.isNotEmpty(customerId) && !customerId.equals(existing.getTenantId())))
                {
                    c.addRow(excelRow, "主键科室ID=" + row.getId() + " 在当前租户下不存在");
                    continue;
                }
                if (StringUtils.isEmpty(row.getName()) || StringUtils.isEmpty(row.getName().trim()))
                {
                    c.addRow(excelRow, "科室名称不能为空");
                }
            }
        }
        List<String> errors = c.getAllErrors();
        boolean valid = errors.isEmpty();
        result.put("valid", valid);
        result.put("errors", errors);
        result.put("totalRows", list == null ? 0 : list.size());
        fillDepartmentUpdateValidationTexts(list, c, valid);
        result.put("previewRows", ExcelUtil.buildImportPreviewMaps(DepartmentImportUpdateDto.class, list));
        return result;
    }

    private void clearDepartmentUpdateDtoValidation(List<DepartmentImportUpdateDto> list)
    {
        if (list == null)
        {
            return;
        }
        for (DepartmentImportUpdateDto row : list)
        {
            if (row != null)
            {
                row.setValidationResult(null);
            }
        }
    }

    private void fillDepartmentUpdateValidationTexts(List<DepartmentImportUpdateDto> list, ImportRowErrorCollector c, boolean fileValid)
    {
        if (list == null)
        {
            return;
        }
        for (int i = 0; i < list.size(); i++)
        {
            int excelRow = i + 2;
            DepartmentImportUpdateDto row = list.get(i);
            if (row == null)
            {
                continue;
            }
            if (row.getId() == null && StringUtils.isEmpty(row.getName()))
            {
                row.setValidationResult("空行（已跳过）");
                continue;
            }
            java.util.List<String> msgs = c.getRowMessages(excelRow);
            if (!msgs.isEmpty())
            {
                row.setValidationResult(String.join("；", msgs));
            }
            else if (fileValid)
            {
                row.setValidationResult("校验通过");
            }
            else
            {
                row.setValidationResult("本行未单独报错；文件因其他数据未通过校验");
            }
        }
    }
}

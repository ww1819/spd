package com.spd.web.controller.system;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.spd.foundation.service.IFdDepartmentService;
import com.spd.foundation.service.IFdWarehouseService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.domain.entity.SysDept;
import com.spd.common.core.domain.entity.SysRole;
import com.spd.common.core.domain.entity.SysUser;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.PinyinUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.utils.poi.ImportRowErrorCollector;
import com.spd.system.dto.UserImportUpdateDto;
import com.spd.system.service.ISbUserPermissionService;
import com.spd.system.service.ISbWorkGroupService;
import com.spd.system.service.ITenantScopeService;
import com.spd.system.service.ISysDeptService;
import com.spd.system.service.ISysPostService;
import com.spd.system.service.ISysRoleService;
import com.spd.system.service.ISysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户信息
 *
 * @author spd
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(SysUserController.class);
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ISysPostService postService;

    @Autowired
    private IFdWarehouseService fdWarehouseService;

    @Autowired
    private IFdDepartmentService fdDepartmentService;

    @Autowired
    private ITenantScopeService tenantScopeService;

    @Autowired
    private ISbWorkGroupService sbWorkGroupService;

    @Autowired
    private ISbUserPermissionService sbUserPermissionService;

    /**
     * 获取用户列表（workgroupPostId：设备工作组 sb_work_group_user；sysPostId：耗材工作组 sys_user_post）
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUser user,
        @RequestParam(value = "workgroupPostId", required = false) String workgroupPostId,
        @RequestParam(value = "sysPostId", required = false) Long sysPostId)
    {
        if (StringUtils.isNotEmpty(workgroupPostId)) {
            user.setWorkgroupPostId(workgroupPostId);
        }
        if (sysPostId != null) {
            user.setSysPostId(sysPostId);
        }
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    /**
     * 获取所有用户列表
     */
    @GetMapping("/listAll")
    public List<SysUser> listAll(SysUser user)
    {
        List<SysUser> list = userService.selectUserList(user);
        return list;
    }

    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUser user)
    {
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String operName = getUserIdStr();
        String message = userService.importUser(userList, updateSupport, operName);
        return success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.importTemplateExcel(response, "用户数据");
    }

    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importAddValidate")
    public AjaxResult importAddValidate(MultipartFile file) throws Exception
    {
        return AjaxResult.success("用户新增导入无需单独校验接口，请直接确认导入");
    }

    @Log(title = "用户新增导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importAddData")
    public AjaxResult importAddData(MultipartFile file) throws Exception
    {
        return importData(file, false);
    }

    @PostMapping("/importAddTemplate")
    public void importAddTemplate(HttpServletResponse response)
    {
        importTemplate(response);
    }

    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importUpdateValidate")
    public AjaxResult importUpdateValidate(MultipartFile file) throws Exception
    {
        ExcelUtil<UserImportUpdateDto> util = new ExcelUtil<UserImportUpdateDto>(UserImportUpdateDto.class);
        List<UserImportUpdateDto> list = util.importExcel(file.getInputStream());
        java.util.Map<String, Object> data = validateUserImportUpdateRows(list);
        if (!Boolean.TRUE.equals(data.get("valid")))
        {
            return AjaxResult.success("校验未通过", data);
        }
        return AjaxResult.success("校验通过，请确认后导入", data);
    }

    @Log(title = "用户更新导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importUpdateData")
    public AjaxResult importUpdateData(MultipartFile file) throws Exception
    {
        ExcelUtil<UserImportUpdateDto> util = new ExcelUtil<UserImportUpdateDto>(UserImportUpdateDto.class);
        List<UserImportUpdateDto> list = util.importExcel(file.getInputStream());
        java.util.Map<String, Object> data = validateUserImportUpdateRows(list);
        if (!Boolean.TRUE.equals(data.get("valid")))
        {
            return AjaxResult.error("数据校验未通过：" + String.valueOf(data.get("errors")));
        }
        int successNum = 0;
        StringBuilder msg = new StringBuilder();
        for (UserImportUpdateDto row : list)
        {
            if (row == null || row.getUserId() == null)
            {
                continue;
            }
            SysUser existing = userService.selectUserById(row.getUserId());
            if (StringUtils.isNotEmpty(row.getNickName()))
            {
                existing.setNickName(row.getNickName().trim());
            }
            String name = StringUtils.isNotEmpty(existing.getNickName()) ? existing.getNickName() : existing.getUserName();
            if (StringUtils.isNotEmpty(name))
            {
                existing.setReferredName(PinyinUtils.getPinyinInitials(name));
            }
            existing.setUpdateBy(getUserIdStr());
            userService.updateUser(existing);
            successNum++;
            msg.append("<br/>").append(successNum).append("、用户 ").append(existing.getUserName()).append(" 更新成功");
        }
        msg.insert(0, "更新导入完成。共处理 " + successNum + " 条，明细如下：");
        for (UserImportUpdateDto row : list)
        {
            if (row != null && row.getUserId() != null)
            {
                row.setValidationResult("更新成功");
            }
        }
        java.util.Map<String, Object> preview = new LinkedHashMap<>();
        preview.put("previewRows", ExcelUtil.buildImportPreviewMaps(UserImportUpdateDto.class, list));
        return AjaxResult.success(msg.toString(), preview);
    }

    @PostMapping("/importUpdateTemplate")
    public void importUpdateTemplate(HttpServletResponse response)
    {
        ExcelUtil<UserImportUpdateDto> util = new ExcelUtil<UserImportUpdateDto>(UserImportUpdateDto.class);
        util.importTemplateExcel(response, "用户更新导入模板");
    }

    private java.util.Map<String, Object> validateUserImportUpdateRows(List<UserImportUpdateDto> list)
    {
        clearUserImportUpdateDtoValidation(list);
        java.util.Map<String, Object> result = new LinkedHashMap<>();
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
                UserImportUpdateDto row = list.get(i);
                int excelRow = i + 2;
                if (row == null || row.getUserId() == null)
                {
                    c.addRow(excelRow, "主键用户ID不能为空");
                    continue;
                }
                SysUser existing = userService.selectUserById(row.getUserId());
                if (existing == null || (StringUtils.isNotEmpty(customerId) && !customerId.equals(existing.getCustomerId())))
                {
                    c.addRow(excelRow, "主键用户ID=" + row.getUserId() + " 在当前租户下不存在");
                    continue;
                }
                if (StringUtils.isEmpty(row.getNickName()) || StringUtils.isEmpty(row.getNickName().trim()))
                {
                    c.addRow(excelRow, "用户姓名不能为空");
                }
            }
        }
        List<String> errors = c.getAllErrors();
        boolean valid = errors.isEmpty();
        result.put("valid", valid);
        result.put("errors", errors);
        result.put("totalRows", list == null ? 0 : list.size());
        fillUserImportUpdateValidationTexts(list, c, valid);
        result.put("previewRows", ExcelUtil.buildImportPreviewMaps(UserImportUpdateDto.class, list));
        return result;
    }

    private void clearUserImportUpdateDtoValidation(List<UserImportUpdateDto> list)
    {
        if (list == null)
        {
            return;
        }
        for (UserImportUpdateDto row : list)
        {
            if (row != null)
            {
                row.setValidationResult(null);
            }
        }
    }

    private void fillUserImportUpdateValidationTexts(List<UserImportUpdateDto> list, ImportRowErrorCollector c, boolean fileValid)
    {
        if (list == null)
        {
            return;
        }
        for (int i = 0; i < list.size(); i++)
        {
            int excelRow = i + 2;
            UserImportUpdateDto row = list.get(i);
            if (row == null)
            {
                continue;
            }
            if (row.getUserId() == null && StringUtils.isEmpty(row.getNickName()))
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

    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = { "/", "/{userId}" })
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId,
        @RequestParam(value = "systemType", required = false) String systemType)
    {
        userService.checkUserDataScope(userId);
        AjaxResult ajax = AjaxResult.success();
        List<SysRole> roles = roleService.selectRoleAll();
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        ajax.put("posts", postService.selectPostAll());
        // 科室/仓库：客户名下；非超级管理员仅见本人权限（设备 sb_user_permission_* 与耗材 sys_user_* 并集，见 TenantScopeService）
        String customerId = SecurityUtils.getCustomerId();
        List<com.spd.foundation.domain.FdDepartment> allDepts = fdDepartmentService.selectdepartmenAll();
        List<com.spd.foundation.domain.FdWarehouse> allWarehouses = fdWarehouseService.selectwarehouseAll();
        if (StringUtils.isNotEmpty(customerId)) {
            Long currentUserId = SecurityUtils.getUserId();
            if (!tenantScopeService.isTenantSuper(currentUserId, customerId)) {
                List<Long> allowedDeptIds = tenantScopeService.resolveDepartmentScope(currentUserId, customerId);
                List<Long> allowedWarehouseIds = tenantScopeService.resolveWarehouseScope(currentUserId, customerId);
                if (allowedDeptIds != null && !allowedDeptIds.isEmpty() && allDepts != null)
                    allDepts = allDepts.stream().filter(d -> d.getId() != null && allowedDeptIds.contains(d.getId())).collect(Collectors.toList());
                else if (allowedDeptIds == null || allowedDeptIds.isEmpty())
                    allDepts = new ArrayList<>();
                if (allowedWarehouseIds != null && !allowedWarehouseIds.isEmpty() && allWarehouses != null)
                    allWarehouses = allWarehouses.stream().filter(w -> w.getId() != null && allowedWarehouseIds.contains(w.getId())).collect(Collectors.toList());
                else if (allowedWarehouseIds == null || allowedWarehouseIds.isEmpty())
                    allWarehouses = new ArrayList<>();
            }
        }
        ajax.put("warehouses", allWarehouses);
        ajax.put("departments", allDepts);
        if (StringUtils.isNotNull(userId))
        {
            SysUser sysUser = userService.selectUserById(userId);
            ajax.put(AjaxResult.DATA_TAG, sysUser);
            ajax.put("postIds", postService.selectPostListByUserId(userId));
            ajax.put("roleIds", sysUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList()));
            // 租户：耗材端 systemType=hc 读 sys_user_*；设备端读 sb_user_permission_* 与工作组
            String userCustomerId = sysUser.getCustomerId();
            if (StringUtils.isNotEmpty(userCustomerId)) {
                if ("hc".equalsIgnoreCase(systemType)) {
                    ajax.put("warehouseIds", fdWarehouseService.selectWarehouseListByUserId(userId));
                    ajax.put("departmentIds", fdDepartmentService.selectDepartmenListByUserId(userId));
                    ajax.put("workGroupIds", new ArrayList<>());
                    List<Long> midLongs = userService.selectMenuListByUserId(userId);
                    List<String> menuStr = new ArrayList<>();
                    if (midLongs != null) {
                        for (Long m : midLongs) {
                            if (m != null) {
                                menuStr.add(m.toString());
                            }
                        }
                    }
                    ajax.put("menuIds", menuStr);
                } else {
                    List<Long> whIds = sbUserPermissionService.selectWarehouseIdsByUserId(userId, userCustomerId);
                    List<Long> deptIds = sbUserPermissionService.selectDeptIdsByUserId(userId, userCustomerId);
                    ajax.put("warehouseIds", whIds != null ? whIds : new ArrayList<>());
                    ajax.put("departmentIds", deptIds != null ? deptIds : new ArrayList<>());
                    List<String> wgIds = sbWorkGroupService.selectGroupIdsByUserId(userId, userCustomerId);
                    ajax.put("workGroupIds", wgIds != null ? wgIds : new ArrayList<>());
                    List<String> menuIds = sbUserPermissionService.selectMenuIdsByUserId(userId, userCustomerId);
                    ajax.put("menuIds", menuIds != null ? menuIds : new ArrayList<>());
                }
            } else {
                ajax.put("warehouseIds", fdWarehouseService.selectWarehouseListByUserId(userId));
                ajax.put("departmentIds", fdDepartmentService.selectDepartmenListByUserId(userId));
                List<Long> menuIdLongs = userService.selectMenuListByUserId(userId);
                List<String> menuIds = menuIdLongs != null ? menuIdLongs.stream().map(Object::toString).collect(Collectors.toList()) : new ArrayList<>();
                ajax.put("menuIds", menuIds);
            }
        }
        return ajax;
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysUser user)
    {
        if (!userService.checkUserNameUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user))
        {
            return error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(getUserIdStr());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        // 租户用户创建的用户继承当前租户ID（前端通常不传 customerId）
        if (StringUtils.isEmpty(user.getCustomerId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            user.setCustomerId(SecurityUtils.getCustomerId());
        }
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysUser user)
    {
        logger.info("接收更新用户请求 - userId: {}, menuIds: {}", user.getUserId(), 
            user.getMenuIds() != null ? java.util.Arrays.toString(user.getMenuIds()) : "null");
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        if (!userService.checkUserNameUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user))
        {
            return error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(getUserIdStr());
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        if (ArrayUtils.contains(userIds, getUserId()))
        {
            return error("当前用户不能删除");
        }
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(getUserIdStr());
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setUpdateBy(getUserIdStr());
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * 根据用户编号获取授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/authRole/{userId}")
    public AjaxResult authRole(@PathVariable("userId") Long userId)
    {
        AjaxResult ajax = AjaxResult.success();
        SysUser user = userService.selectUserById(userId);
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        ajax.put("user", user);
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        return ajax;
    }

    /**
     * 用户授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public AjaxResult insertAuthRole(Long userId, Long[] roleIds)
    {
        userService.checkUserDataScope(userId);
        userService.insertUserAuth(userId, roleIds);
        return success();
    }

    /**
     * 获取部门树列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/deptTree")
    public AjaxResult deptTree(SysDept dept)
    {
        return success(deptService.selectDeptTreeList(dept));
    }

    /**
     * 批量更新用户名称简码（根据用户名称生成拼音首字母）
     */
    @PreAuthorize("@ss.hasPermi('system:user:updateReferred')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/updateReferred")
    public AjaxResult updateReferred(@RequestBody java.util.Map<String, java.util.List<Long>> body)
    {
        java.util.List<Long> ids = body.get("ids");
        if (ids == null || ids.isEmpty())
        {
            return error("ID 列表不能为空");
        }
        userService.updateReferred(ids);
        return success("更新简码成功");
    }
}

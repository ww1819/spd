package com.spd.web.controller.system;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
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
import com.spd.common.annotation.Log;
import com.spd.common.constant.UserConstants;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.domain.entity.SysMenu;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.StringUtils;
import com.spd.system.domain.SbCustomer;
import com.spd.system.domain.dto.MenuBatchGrantBody;
import com.spd.system.mapper.SbCustomerMapper;
import com.spd.system.service.ISysMenuService;

/**
 * 菜单信息
 * 
 * @author spd
 */
@RestController
@RequestMapping("/system/menu")
public class SysMenuController extends BaseController
{
    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SbCustomerMapper sbCustomerMapper;

    /**
     * 获取菜单列表
     */
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/list")
    public AjaxResult list(SysMenu menu)
    {
        List<SysMenu> menus = menuService.selectMenuList(menu, getUserId());
        return success(menus);
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping(value = "/{menuId}")
    public AjaxResult getInfo(@PathVariable Long menuId)
    {
        return success(menuService.selectMenuById(menuId));
    }

    /**
     * 耗材菜单树（含默认对客户开放），用于批量设置
     */
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping("/defaultOpen/tree")
    public AjaxResult defaultOpenTree()
    {
        return success(menuService.selectMenuTreeForDefaultOpenBatch());
    }

    /**
     * 批量设置耗材菜单「默认对客户开放」
     */
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping("/defaultOpen/batch")
    public AjaxResult batchDefaultOpen(@RequestBody List<Long> menuIds)
    {
        menuService.batchSetDefaultOpenToCustomer(menuIds);
        return success();
    }

    /**
     * 批量赋权：在库耗材租户检索（名称、编码/拼音简码、租户ID 模糊）
     */
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @GetMapping("/batchGrant/tenants")
    public TableDataInfo batchGrantTenants(@RequestParam(required = false) String keyword)
    {
        startPage();
        List<SbCustomer> list = sbCustomerMapper.selectActiveHcCustomersForMenuGrant(keyword);
        return getDataTable(list);
    }

    /**
     * 批量赋权：所选租户已有租户菜单权限（全部/部分拥有）
     */
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @GetMapping("/batchGrant/existingMenuIds")
    public AjaxResult batchGrantExistingMenuIds(@RequestParam(required = false) String customerIds)
    {
        if (StringUtils.isEmpty(customerIds))
        {
            return success(menuService.resolveBatchGrantExistingTenantMenuIds(Collections.emptyList()));
        }
        List<String> ids = Arrays.stream(customerIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        return success(menuService.resolveBatchGrantExistingTenantMenuIds(ids));
    }

    /**
     * 批量赋权：精确合并授予租户/其全部工作组/其全部用户
     */
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @Log(title = "菜单管理", businessType = BusinessType.GRANT)
    @PostMapping("/batchGrant")
    public AjaxResult batchGrant(@RequestBody MenuBatchGrantBody body)
    {
        menuService.batchGrantMenusToTenants(body);
        return success();
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    public AjaxResult treeselect(SysMenu menu)
    {
        List<SysMenu> menus = menuService.selectMenuList(menu, getUserId());
        return success(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public AjaxResult roleMenuTreeselect(@PathVariable("roleId") Long roleId)
    {
        List<SysMenu> menus = menuService.selectMenuList(getUserId());
        AjaxResult ajax = AjaxResult.success();
        ajax.put("checkedKeys", menuService.selectMenuListByRoleId(roleId));
        ajax.put("menus", menuService.buildMenuTreeSelect(menus));
        return ajax;
    }

    /**
     * 新增菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysMenu menu)
    {
        if (!menuService.checkMenuNameUnique(menu))
        {
            return error("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath()))
        {
            return error("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        menu.setCreateBy(getUserIdStr());
        return toAjax(menuService.insertMenu(menu));
    }

    /**
     * 修改菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysMenu menu)
    {
        if (!menuService.checkMenuNameUnique(menu))
        {
            return error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath()))
        {
            return error("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        else if (menu.getMenuId().equals(menu.getParentId()))
        {
            return error("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        menu.setUpdateBy(getUserIdStr());
        return toAjax(menuService.updateMenu(menu));
    }

    /**
     * 删除菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public AjaxResult remove(@PathVariable("menuId") Long menuId)
    {
        if (menuService.hasChildByMenuId(menuId))
        {
            return warn("存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId))
        {
            return warn("菜单已分配,不允许删除");
        }
        return toAjax(menuService.deleteMenuById(menuId));
    }
}
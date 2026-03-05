package com.spd.web.controller.system;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.spd.common.annotation.Log;
import com.spd.common.constant.UserConstants;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.system.domain.SbMenu;
import com.spd.system.service.ISbMenuService;

/**
 * 设备菜单管理
 *
 * 路由前缀：/equipment/system/menu
 */
@RestController
@RequestMapping("/equipment/system/menu")
public class SbMenuController extends BaseController {

  @Autowired
  private ISbMenuService sbMenuService;

    /**
     * 获取设备菜单列表
     */
    @PreAuthorize("@ss.hasPermi('sb:system:menu:list')")
    @GetMapping("/list")
    public AjaxResult list(SbMenu menu)
    {
        List<SbMenu> list = sbMenuService.selectSbMenuList(menu);
        return success(list);
    }

  /**
   * 根据用户获取设备菜单树（menuId/parentId 为 UUID7 字符串）
   */
    @GetMapping("/treeselect")
    public AjaxResult treeselect(SbMenu menu)
    {
        Long userId = SecurityUtils.getUserId();
        List<SbMenu> sbMenus = sbMenuService.selectSbMenuTreeByUserId(userId);
        return success(sbMenus);
    }

  /**
   * 客户菜单权限分配用菜单树（仅平台用户可调，且树中不包含客户管理，租户不可被分配客户管理）
   */
    @PreAuthorize("@ss.isPlatformUser() and @ss.hasPermi('sb:system:customer:query')")
    @GetMapping("/treeselectForCustomerAssign")
    public AjaxResult treeselectForCustomerAssign()
    {
        List<SbMenu> sbMenus = sbMenuService.selectSbMenuTreeForCustomerAssign();
        return success(sbMenus);
    }

  /**
   * 新增设备菜单
   */
    @PreAuthorize("@ss.hasPermi('sb:system:menu:add')")
    @Log(title = "设备菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SbMenu menu)
    {
        if (!sbMenuService.checkSbMenuNameUnique(menu))
        {
            return error("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame())
                && !StringUtils.startsWithAny(menu.getPath(), "http://", "https://"))
        {
            return error("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        menu.setCreateBy(SecurityUtils.getUsername());
        return toAjax(sbMenuService.insertSbMenu(menu));
    }

  /**
   * 修改设备菜单
   */
    @PreAuthorize("@ss.hasPermi('sb:system:menu:edit')")
    @Log(title = "设备菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SbMenu menu)
    {
        if (!sbMenuService.checkSbMenuNameUnique(menu))
        {
            return error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame())
                && !StringUtils.startsWithAny(menu.getPath(), "http://", "https://"))
        {
            return error("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        menu.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(sbMenuService.updateSbMenu(menu));
    }

  /**
   * 删除设备菜单
   */
    @PreAuthorize("@ss.hasPermi('sb:system:menu:remove')")
    @Log(title = "设备菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public AjaxResult remove(@PathVariable String menuId)
    {
        return toAjax(sbMenuService.deleteSbMenuById(menuId));
    }
}


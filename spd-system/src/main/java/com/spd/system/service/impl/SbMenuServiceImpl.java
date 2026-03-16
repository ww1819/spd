package com.spd.system.service.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spd.common.constant.UserConstants;
import com.spd.common.core.domain.entity.SysMenu;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.system.domain.SbMenu;
import com.spd.system.domain.SbRoleMenu;
import com.spd.system.domain.SbUserPermissionMenu;
import com.spd.system.domain.vo.MetaVo;
import com.spd.system.domain.vo.RouterVo;
import com.spd.system.mapper.SbMenuMapper;
import com.spd.system.mapper.SbRoleMenuMapper;
import com.spd.system.mapper.SbUserPermissionMenuMapper;
import com.spd.system.service.ISbMenuService;

/**
 * 设备菜单业务实现（基于 sb_menu）
 */
@Service
public class SbMenuServiceImpl implements ISbMenuService {

  /** 平台设备管理员角色ID（新增菜单时自动赋权） */
  private static final String PLATFORM_ADMIN_ROLE_ID = "01900000-0000-7000-8000-000000000001";
  /** 平台 admin 用户ID（假定为 1，新增菜单时自动赋权） */
  private static final Long PLATFORM_ADMIN_USER_ID = 1L;

  @Autowired
  private SbMenuMapper sbMenuMapper;
  @Autowired
  private SbRoleMenuMapper sbRoleMenuMapper;
  @Autowired
  private SbUserPermissionMenuMapper sbUserPermissionMenuMapper;

  @Autowired
  private SysMenuServiceImpl sysMenuService; // 复用构建路由的逻辑

  @Override
  public List<SbMenu> selectSbMenuList(SbMenu menu) {
    return sbMenuMapper.selectSbMenuList(menu);
  }

  @Override
  public SbMenu selectSbMenuById(String menuId) {
    return sbMenuMapper.selectSbMenuById(menuId);
  }

  @Override
  public List<SbMenu> selectSbMenuTreeByUserId(Long userId) {
    List<SbMenu> menus = sbMenuMapper.selectSbMenuTreeByUserId(userId);
    return getChildPerms(menus, "0");
  }

  @Override
  public List<SbMenu> selectSbMenuTreeForCustomerAssign() {
    List<SbMenu> all = sbMenuMapper.selectSbMenuTreeAll();
    if (all == null) return new ArrayList<>();
    List<SbMenu> filtered = new ArrayList<>();
    for (SbMenu m : all) {
      if ("1".equals(m.getIsPlatformOnly())) {
        continue;
      }
      filtered.add(m);
    }
    return getChildPerms(filtered, "0");
  }

  @Override
  public List<SbMenu> selectSbMenuTreeByCustomerIdEnabling(String customerId) {
    if (StringUtils.isEmpty(customerId)) return new ArrayList<>();
    List<SbMenu> list = sbMenuMapper.selectSbMenuTreeByCustomerIdEnabling(customerId);
    return list == null ? new ArrayList<>() : getChildPerms(list, "0");
  }

  @Override
  public int insertSbMenu(SbMenu menu) {
    if (StringUtils.isEmpty(menu.getMenuId())) {
      menu.setMenuId(UUID7.generateUUID7());
    }
    if (menu.getParentId() == null) {
      menu.setParentId("0");
    }
    int rows = sbMenuMapper.insertSbMenu(menu);
    if (rows > 0) {
      grantNewMenuToPlatformAdmin(menu.getMenuId(), menu.getCreateBy());
    }
    return rows;
  }

  /**
   * 新增菜单后自动赋给平台 admin 角色与 admin 用户（用户权限表作为菜单数据源）
   */
  private void grantNewMenuToPlatformAdmin(String menuId, String createBy) {
    if (StringUtils.isEmpty(menuId)) return;
    String by = StringUtils.isNotEmpty(createBy) ? createBy : "admin";
    SbRoleMenu rm = new SbRoleMenu();
    rm.setRoleId(PLATFORM_ADMIN_ROLE_ID);
    rm.setMenuId(menuId);
    rm.setCustomerId(null);
    sbRoleMenuMapper.batchSbRoleMenu(java.util.Collections.singletonList(rm));
    SbUserPermissionMenu upm = new SbUserPermissionMenu();
    upm.setId(UUID7.generateUUID7());
    upm.setUserId(PLATFORM_ADMIN_USER_ID);
    upm.setCustomerId("");
    upm.setMenuId(menuId);
    upm.setCreateBy(by);
    sbUserPermissionMenuMapper.batchInsert(java.util.Collections.singletonList(upm));
  }

  @Override
  public int updateSbMenu(SbMenu menu) {
    return sbMenuMapper.updateSbMenu(menu);
  }

  @Override
  public int deleteSbMenuById(String menuId) {
    return sbMenuMapper.deleteSbMenuById(menuId, SecurityUtils.getUserIdStr());
  }

  @Override
  public boolean checkSbMenuNameUnique(SbMenu menu) {
    String menuId = StringUtils.isNull(menu.getMenuId()) ? "" : menu.getMenuId();
    SbMenu info = sbMenuMapper.checkSbMenuNameUnique(menu.getMenuName(), menu.getParentId());
    if (StringUtils.isNotNull(info) && !info.getMenuId().equals(menuId)) {
      return UserConstants.NOT_UNIQUE;
    }
    return UserConstants.UNIQUE;
  }

  @Override
  public List<RouterVo> buildMenus(List<SysMenu> menus) {
    // 直接复用系统菜单的 buildMenus 逻辑
    return sysMenuService.buildMenus(menus);
  }

  @Override
  public List<SysMenu> convertToSysMenus(List<SbMenu> sbMenus) {
    List<SysMenu> list = new ArrayList<>();
    if (StringUtils.isEmpty(sbMenus)) {
      return list;
    }
    for (SbMenu m : sbMenus) {
      SysMenu s = new SysMenu();
      s.setMenuId(null);
      s.setParentId(null);
      s.setMenuName(m.getMenuName());
      s.setOrderNum(m.getOrderNum());
      s.setPath(m.getPath());
      s.setComponent(m.getComponent());
      s.setIsFrame(m.getIsFrame());
      s.setIsCache(m.getIsCache());
      s.setMenuType(m.getMenuType());
      s.setVisible(m.getVisible());
      s.setStatus(m.getStatus());
      s.setPerms(m.getPerms());
      s.setIcon(m.getIcon());
      s.setCreateBy(m.getCreateBy());
      s.setCreateTime(m.getCreateTime());
      s.setUpdateBy(m.getUpdateBy());
      s.setUpdateTime(m.getUpdateTime());
      if (StringUtils.isNotEmpty(m.getChildren())) {
        s.setChildren(convertToSysMenus(m.getChildren()));
      }
      list.add(s);
    }
    return list;
  }

  @Override
  public java.util.Set<String> selectSbMenuPermsByUserId(Long userId) {
    return selectSbMenuPermsByUserIdAndCustomer(userId, null);
  }

  @Override
  public java.util.Set<String> selectSbMenuPermsByUserIdAndCustomer(Long userId, String customerId) {
    java.util.List<String> perms = sbMenuMapper.selectSbMenuPermsByUserIdAndCustomer(userId, customerId);
    java.util.Set<String> permsSet = new java.util.HashSet<>();
    for (String perm : perms) {
      if (StringUtils.isNotEmpty(perm)) {
        for (String p : perm.trim().split(",")) {
          if (StringUtils.isNotEmpty(p)) {
            permsSet.add(p);
          }
        }
      }
    }
    return permsSet;
  }

  private List<SbMenu> getChildPerms(List<SbMenu> list, String parentId) {
    List<SbMenu> returnList = new ArrayList<>();
    if (list == null) {
      return returnList;
    }
    for (SbMenu t : list) {
      if (parentId != null && parentId.equals(t.getParentId())) {
        recursionFn(list, t);
        returnList.add(t);
      }
    }
    return returnList;
  }

  private void recursionFn(List<SbMenu> list, SbMenu t) {
    List<SbMenu> childList = getChildList(list, t);
    t.setChildren(childList);
    for (SbMenu tChild : childList) {
      if (hasChild(list, tChild)) {
        recursionFn(list, tChild);
      }
    }
  }

  private List<SbMenu> getChildList(List<SbMenu> list, SbMenu t) {
    List<SbMenu> tlist = new ArrayList<>();
    for (SbMenu n : list) {
      if (t.getMenuId() != null && t.getMenuId().equals(n.getParentId())) {
        tlist.add(n);
      }
    }
    return tlist;
  }

  @Override
  public List<RouterVo> buildMenusFromSb(List<SbMenu> menus) {
    List<RouterVo> routers = new LinkedList<>();
    if (menus == null) {
      return routers;
    }
    for (SbMenu menu : menus) {
      RouterVo router = new RouterVo();
      router.setHidden("1".equals(menu.getVisible()));
      router.setName(getRouteNameSb(menu));
      router.setPath(getRouterPathSb(menu));
      router.setComponent(getComponentSb(menu));
      router.setQuery(null);
      router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()), menu.getPath()));
      List<SbMenu> cMenus = menu.getChildren();
      if (StringUtils.isNotEmpty(cMenus) && UserConstants.TYPE_DIR.equals(menu.getMenuType())) {
        router.setAlwaysShow(true);
        router.setRedirect("noRedirect");
        router.setChildren(buildMenusFromSb(cMenus));
      } else if (isMenuFrameSb(menu)) {
        router.setMeta(null);
        List<RouterVo> childrenList = new ArrayList<>();
        RouterVo children = new RouterVo();
        children.setPath(menu.getPath());
        children.setComponent(menu.getComponent());
        children.setName(StringUtils.capitalize(menu.getPath()));
        children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()), menu.getPath()));
        children.setQuery(null);
        childrenList.add(children);
        router.setChildren(childrenList);
      } else if ("0".equals(menu.getParentId()) && isInnerLinkSb(menu)) {
        router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
        router.setPath("/");
        List<RouterVo> childrenList = new ArrayList<>();
        RouterVo children = new RouterVo();
        String routerPath = menu.getPath() != null ? menu.getPath().replaceAll("http(s)?://[^/]+", "") : "";
        children.setPath(routerPath);
        children.setComponent(UserConstants.INNER_LINK);
        children.setName(StringUtils.capitalize(routerPath));
        children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
        childrenList.add(children);
        router.setChildren(childrenList);
      }
      routers.add(router);
    }
    return routers;
  }

  private String getRouteNameSb(SbMenu menu) {
    String routerName = StringUtils.capitalize(menu.getPath());
    if (isMenuFrameSb(menu)) {
      routerName = StringUtils.EMPTY;
    }
    return routerName;
  }

  private String getRouterPathSb(SbMenu menu) {
    String routerPath = menu.getPath();
    if (!"0".equals(menu.getParentId()) && isInnerLinkSb(menu)) {
      routerPath = menu.getPath() != null ? menu.getPath().replaceAll("http(s)?://[^/]+", "") : menu.getPath();
    }
    if ("0".equals(menu.getParentId()) && UserConstants.TYPE_DIR.equals(menu.getMenuType())
        && UserConstants.NO_FRAME.equals(menu.getIsFrame())) {
      routerPath = "/" + menu.getPath();
    } else if (isMenuFrameSb(menu)) {
      routerPath = "/";
    }
    return routerPath;
  }

  private String getComponentSb(SbMenu menu) {
    String component = UserConstants.LAYOUT;
    if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrameSb(menu)) {
      component = menu.getComponent();
    } else if (StringUtils.isEmpty(menu.getComponent()) && !"0".equals(menu.getParentId()) && isInnerLinkSb(menu)) {
      component = UserConstants.INNER_LINK;
    } else if (StringUtils.isEmpty(menu.getComponent()) && isParentViewSb(menu)) {
      component = UserConstants.PARENT_VIEW;
    }
    return component;
  }

  private boolean isMenuFrameSb(SbMenu menu) {
    return "0".equals(menu.getParentId()) && UserConstants.TYPE_MENU.equals(menu.getMenuType())
        && UserConstants.NO_FRAME.equals(menu.getIsFrame());
  }

  private boolean isInnerLinkSb(SbMenu menu) {
    return menu.getIsFrame() != null && menu.getIsFrame().equals(UserConstants.NO_FRAME)
        && StringUtils.ishttp(menu.getPath());
  }

  private boolean isParentViewSb(SbMenu menu) {
    return !"0".equals(menu.getParentId()) && UserConstants.TYPE_DIR.equals(menu.getMenuType());
  }

  private boolean hasChild(List<SbMenu> list, SbMenu t) {
    return getChildList(list, t).size() > 0;
  }
}


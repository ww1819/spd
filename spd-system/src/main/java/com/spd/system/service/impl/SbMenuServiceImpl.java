package com.spd.system.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spd.common.constant.UserConstants;
import com.spd.common.core.domain.entity.SysMenu;
import com.spd.common.utils.StringUtils;
import com.spd.system.domain.SbMenu;
import com.spd.system.domain.vo.RouterVo;
import com.spd.system.mapper.SbMenuMapper;
import com.spd.system.service.ISbMenuService;

/**
 * 设备菜单业务实现（基于 sb_menu）
 */
@Service
public class SbMenuServiceImpl implements ISbMenuService {

  @Autowired
  private SbMenuMapper sbMenuMapper;

  @Autowired
  private SysMenuServiceImpl sysMenuService; // 复用构建路由的逻辑

  @Override
  public List<SbMenu> selectSbMenuList(SbMenu menu) {
    return sbMenuMapper.selectSbMenuList(menu);
  }

  @Override
  public List<SbMenu> selectSbMenuTreeByUserId(Long userId) {
    List<SbMenu> menus = sbMenuMapper.selectSbMenuTreeByUserId(userId);
    return getChildPerms(menus, 0);
  }

  @Override
  public int insertSbMenu(SbMenu menu) {
    return sbMenuMapper.insertSbMenu(menu);
  }

  @Override
  public int updateSbMenu(SbMenu menu) {
    return sbMenuMapper.updateSbMenu(menu);
  }

  @Override
  public int deleteSbMenuById(Long menuId) {
    return sbMenuMapper.deleteSbMenuById(menuId);
  }

  @Override
  public boolean checkSbMenuNameUnique(SbMenu menu) {
    Long menuId = StringUtils.isNull(menu.getMenuId()) ? -1L : menu.getMenuId();
    SbMenu info = sbMenuMapper.checkSbMenuNameUnique(menu.getMenuName(), menu.getParentId());
    if (StringUtils.isNotNull(info) && info.getMenuId().longValue() != menuId.longValue()) {
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
      s.setMenuId(m.getMenuId());
      s.setMenuName(m.getMenuName());
      s.setParentId(m.getParentId());
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
    java.util.List<String> perms = sbMenuMapper.selectSbMenuPermsByUserId(userId);
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

  private List<SbMenu> getChildPerms(List<SbMenu> list, int parentId) {
    List<SbMenu> returnList = new ArrayList<>();
    if (list == null) {
      return returnList;
    }
    for (SbMenu t : list) {
      if (t.getParentId() != null && t.getParentId().intValue() == parentId) {
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
      if (n.getParentId() != null && n.getParentId().longValue() == t.getMenuId().longValue()) {
        tlist.add(n);
      }
    }
    return tlist;
  }

  private boolean hasChild(List<SbMenu> list, SbMenu t) {
    return getChildList(list, t).size() > 0;
  }
}


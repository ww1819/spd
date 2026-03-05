package com.spd.system.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.system.domain.SbCustomerMenu;
import com.spd.system.mapper.SbCustomerMenuMapper;
import com.spd.system.service.ISbCustomerMenuService;

/**
 * 设备系统客户菜单权限 服务实现
 * 客户管理分配功能：勾选则 is_enabled=1，取消勾选则 is_enabled=0（不删行）。
 */
@Service
public class SbCustomerMenuServiceImpl implements ISbCustomerMenuService {

  @Autowired
  private SbCustomerMenuMapper sbCustomerMenuMapper;

  @Override
  public List<String> selectMenuIdsByCustomerId(String customerId) {
    return sbCustomerMenuMapper.selectMenuIdsByCustomerId(customerId);
  }

  @Override
  @Transactional
  public int saveCustomerMenus(String customerId, String[] menuIds) {
    List<SbCustomerMenu> existing = sbCustomerMenuMapper.selectSbCustomerMenuListByCustomerId(customerId);
    Set<String> selected = new HashSet<>();
    if (menuIds != null) {
      for (String id : menuIds) {
        if (StringUtils.isNotEmpty(id)) {
          selected.add(id);
        }
      }
    }
    String createBy = SecurityUtils.getUsername();
    for (SbCustomerMenu row : existing) {
      String en = selected.contains(row.getMenuId()) ? "1" : "0";
      sbCustomerMenuMapper.updateIsEnabled(customerId, row.getMenuId(), en);
    }
    for (String menuId : selected) {
      boolean found = false;
      for (SbCustomerMenu row : existing) {
        if (row.getMenuId().equals(menuId)) {
          found = true;
          break;
        }
      }
      if (!found) {
        SbCustomerMenu cm = new SbCustomerMenu();
        cm.setCustomerId(customerId);
        cm.setMenuId(menuId);
        cm.setStatus("0");
        cm.setIsEnabled("1");
        cm.setCreateBy(createBy);
        sbCustomerMenuMapper.insertSbCustomerMenu(cm);
      }
    }
    return selected.size();
  }
}

package com.spd.system.service.impl;

import java.util.ArrayList;
import java.util.List;

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
    sbCustomerMenuMapper.deleteSbCustomerMenuByCustomerId(customerId);
    if (StringUtils.isEmpty(menuIds) || menuIds.length == 0) {
      return 0;
    }
    List<SbCustomerMenu> list = new ArrayList<>();
    String createBy = SecurityUtils.getUsername();
    for (String menuId : menuIds) {
      if (StringUtils.isEmpty(menuId)) {
        continue;
      }
      SbCustomerMenu cm = new SbCustomerMenu();
      cm.setCustomerId(customerId);
      cm.setMenuId(menuId);
      cm.setCreateBy(createBy);
      list.add(cm);
    }
    if (list.isEmpty()) {
      return 0;
    }
    return sbCustomerMenuMapper.batchSbCustomerMenu(list);
  }
}

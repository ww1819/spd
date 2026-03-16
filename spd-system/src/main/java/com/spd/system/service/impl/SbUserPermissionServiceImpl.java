package com.spd.system.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.system.domain.SbUserPermissionDept;
import com.spd.system.domain.SbUserPermissionMenu;
import com.spd.system.domain.SbUserPermissionWarehouse;
import com.spd.system.mapper.SbUserPermissionDeptMapper;
import com.spd.system.mapper.SbUserPermissionMenuMapper;
import com.spd.system.mapper.SbUserPermissionWarehouseMapper;
import com.spd.system.service.ISbUserPermissionService;

/**
 * 设备用户权限（菜单/仓库/科室）服务实现
 */
@Service
public class SbUserPermissionServiceImpl implements ISbUserPermissionService {

  @Autowired
  private SbUserPermissionMenuMapper sbUserPermissionMenuMapper;
  @Autowired
  private SbUserPermissionWarehouseMapper sbUserPermissionWarehouseMapper;
  @Autowired
  private SbUserPermissionDeptMapper sbUserPermissionDeptMapper;

  @Override
  public List<String> selectMenuIdsByUserId(Long userId, String customerId) {
    return sbUserPermissionMenuMapper.selectMenuIdsByUserId(userId, customerId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int saveUserMenus(Long userId, String customerId, String[] menuIds) {
    String deleteBy = SecurityUtils.getUserIdStr();
    sbUserPermissionMenuMapper.deleteByUserId(userId, deleteBy);
    if (menuIds == null || menuIds.length == 0) return 0;
    List<SbUserPermissionMenu> list = new ArrayList<>();
    String createBy = deleteBy;
    for (String menuId : menuIds) {
      if (StringUtils.isEmpty(menuId)) continue;
      SbUserPermissionMenu m = new SbUserPermissionMenu();
      m.setId(UUID7.generateUUID7());
      m.setUserId(userId);
      m.setCustomerId(customerId);
      m.setMenuId(menuId);
      m.setCreateBy(createBy);
      list.add(m);
    }
    if (list.isEmpty()) return 0;
    return sbUserPermissionMenuMapper.batchInsert(list);
  }

  @Override
  public List<Long> selectWarehouseIdsByUserId(Long userId, String customerId) {
    return sbUserPermissionWarehouseMapper.selectWarehouseIdsByUserId(userId, customerId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int saveUserWarehouses(Long userId, String customerId, Long[] warehouseIds) {
    String deleteBy = SecurityUtils.getUserIdStr();
    sbUserPermissionWarehouseMapper.deleteByUserId(userId, deleteBy);
    if (warehouseIds == null || warehouseIds.length == 0) return 0;
    List<SbUserPermissionWarehouse> list = new ArrayList<>();
    String createBy = deleteBy;
    for (Long warehouseId : warehouseIds) {
      if (warehouseId == null) continue;
      SbUserPermissionWarehouse w = new SbUserPermissionWarehouse();
      w.setId(UUID7.generateUUID7());
      w.setUserId(userId);
      w.setCustomerId(customerId);
      w.setWarehouseId(warehouseId);
      w.setCreateBy(createBy);
      list.add(w);
    }
    if (list.isEmpty()) return 0;
    return sbUserPermissionWarehouseMapper.batchInsert(list);
  }

  @Override
  public List<Long> selectDeptIdsByUserId(Long userId, String customerId) {
    return sbUserPermissionDeptMapper.selectDeptIdsByUserId(userId, customerId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int saveUserDepts(Long userId, String customerId, Long[] deptIds) {
    String deleteBy = SecurityUtils.getUserIdStr();
    sbUserPermissionDeptMapper.deleteByUserId(userId, deleteBy);
    if (deptIds == null || deptIds.length == 0) return 0;
    List<SbUserPermissionDept> list = new ArrayList<>();
    String createBy = deleteBy;
    for (Long deptId : deptIds) {
      if (deptId == null) continue;
      SbUserPermissionDept d = new SbUserPermissionDept();
      d.setId(UUID7.generateUUID7());
      d.setUserId(userId);
      d.setCustomerId(customerId);
      d.setDeptId(deptId);
      d.setCreateBy(createBy);
      list.add(d);
    }
    if (list.isEmpty()) return 0;
    return sbUserPermissionDeptMapper.batchInsert(list);
  }
}

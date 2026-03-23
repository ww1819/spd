package com.spd.system.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spd.common.core.domain.entity.SysUser;
import com.spd.common.utils.StringUtils;
import com.spd.system.domain.SysPost;
import com.spd.system.domain.SysPostDepartment;
import com.spd.system.domain.SysPostWarehouse;
import com.spd.system.domain.SysUserDepartment;
import com.spd.system.domain.SysUserWarehouse;
import com.spd.system.mapper.SysPostDepartmentMapper;
import com.spd.system.mapper.SysPostMapper;
import com.spd.system.mapper.SysPostWarehouseMapper;
import com.spd.system.mapper.SysUserDepartmentMapper;
import com.spd.system.mapper.SysUserMapper;
import com.spd.system.mapper.SysUserWarehouseMapper;
import com.spd.system.service.ITenantFoundationAutoGrantService;

/**
 * 新科室/仓库自动挂到 super_01 与 super 岗位，避免关联缺失。
 */
@Service
public class TenantFoundationAutoGrantServiceImpl implements ITenantFoundationAutoGrantService {

  private static final String DEFAULT_TENANT_ADMIN = "super_01";
  private static final String POST_CODE_SUPER = "super";

  @Autowired
  private SysUserMapper sysUserMapper;
  @Autowired
  private SysPostMapper sysPostMapper;
  @Autowired
  private SysUserDepartmentMapper sysUserDepartmentMapper;
  @Autowired
  private SysUserWarehouseMapper sysUserWarehouseMapper;
  @Autowired
  private SysPostDepartmentMapper sysPostDepartmentMapper;
  @Autowired
  private SysPostWarehouseMapper sysPostWarehouseMapper;

  @Override
  public void grantDepartmentToTenantAdmins(String tenantId, Long departmentId) {
    if (StringUtils.isEmpty(tenantId) || departmentId == null) {
      return;
    }
    SysUser super01 = sysUserMapper.selectUserByUserNameAndCustomerId(DEFAULT_TENANT_ADMIN, tenantId);
    if (super01 != null && super01.getUserId() != null) {
      List<Long> existing = sysUserDepartmentMapper.selectDepartmentIdsByUserId(super01.getUserId());
      if (existing == null || !existing.contains(departmentId)) {
        SysUserDepartment ud = new SysUserDepartment();
        ud.setUserId(super01.getUserId());
        ud.setDepartmentId(departmentId);
        ud.setStatus(0);
        sysUserDepartmentMapper.batchUserDepartment(Collections.singletonList(ud));
      }
    }
    Long superPostId = resolveSuperPostId(tenantId);
    if (superPostId != null) {
      List<Long> pd = sysPostDepartmentMapper.selectDepartmentListByPostId(superPostId);
      if (pd == null || !pd.contains(departmentId)) {
        SysPostDepartment row = new SysPostDepartment();
        row.setPostId(superPostId);
        row.setDepartmentId(departmentId);
        row.setTenantId(tenantId);
        sysPostDepartmentMapper.batchPostDepartment(Collections.singletonList(row));
      }
    }
  }

  @Override
  public void grantWarehouseToTenantAdmins(String tenantId, Long warehouseId) {
    if (StringUtils.isEmpty(tenantId) || warehouseId == null) {
      return;
    }
    SysUser super01 = sysUserMapper.selectUserByUserNameAndCustomerId(DEFAULT_TENANT_ADMIN, tenantId);
    if (super01 != null && super01.getUserId() != null) {
      List<Long> existing = sysUserWarehouseMapper.selectWarehouseIdsByUserId(super01.getUserId());
      if (existing == null || !existing.contains(warehouseId)) {
        SysUserWarehouse uw = new SysUserWarehouse();
        uw.setUserId(super01.getUserId());
        uw.setWarehouseId(warehouseId);
        uw.setStatus(0);
        sysUserWarehouseMapper.batchUserWarehouse(Collections.singletonList(uw));
      }
    }
    Long superPostId = resolveSuperPostId(tenantId);
    if (superPostId != null) {
      List<Long> pw = sysPostWarehouseMapper.selectWarehouseListByPostId(superPostId);
      if (pw == null || !pw.contains(warehouseId)) {
        SysPostWarehouse row = new SysPostWarehouse();
        row.setPostId(superPostId);
        row.setWarehouseId(warehouseId);
        row.setTenantId(tenantId);
        sysPostWarehouseMapper.batchPostWarehouse(Collections.singletonList(row));
      }
    }
  }

  private Long resolveSuperPostId(String tenantId) {
    SysPost q = new SysPost();
    q.setTenantId(tenantId);
    q.setPostCode(POST_CODE_SUPER);
    List<SysPost> posts = sysPostMapper.selectPostList(q);
    if (posts != null && !posts.isEmpty()) {
      return posts.get(0).getPostId();
    }
    SysPost linked = sysPostMapper.selectHcSuperPostLinkedToCustomer(tenantId);
    return linked != null ? linked.getPostId() : null;
  }
}

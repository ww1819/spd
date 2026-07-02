package com.spd.system.service;

import java.util.List;
import java.util.Map;

/**
 * 租户下仓库/科室数据范围（耗材 sys_user_department / sys_user_warehouse）。
 */
public interface ITenantScopeService {

  /**
   * 是否为机构超级管理员：super_01 或租户内 super 岗位。
   */
  boolean isTenantSuper(Long userId, String customerId);

  /**
   * 非 super 时返回当前用户可访问的仓库 ID；super 返回 null 表示不限制。
   */
  List<Long> resolveWarehouseScope(Long userId, String customerId);

  /**
   * 非 super 时返回当前用户可访问的科室 ID；super 返回 null 表示不限制。
   */
  List<Long> resolveDepartmentScope(Long userId, String customerId);

  /**
   * 将科室数据范围写入 MyBatis {@code params}，供 SQL 子查询过滤（避免 {@code IN (大量id)} 过长）。
   * 非机构管理员写入 {@code scopeDeptUserId}；机构管理员不写，表示不限制。
   */
  void applyDepartmentScopeQueryParams(Map<String, Object> params, Long userId, String customerId);

  /**
   * 将仓库数据范围写入 MyBatis {@code params}，供 SQL 子查询过滤。
   * 非机构管理员写入 {@code scopeWarehouseUserId}；机构管理员不写，表示不限制。
   */
  void applyWarehouseScopeQueryParams(Map<String, Object> params, Long userId, String customerId);
}

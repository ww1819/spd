package com.spd.system.service;

import java.util.List;
import java.util.Map;

/**
 * 租户下仓库/科室数据范围：按登录渠道隔离——耗材端仅 sys_user_*，设备端仅 sb_user_permission_*；
 * 未识别渠道时（旧 Token）保持双表并集以兼容。
 */
public interface ITenantScopeService {

  /**
   * 是否为当前渠道下的租户超级管理员：耗材端仅认 super 岗位，设备端仅认管理员组，未识别渠道时二者满足其一即可。
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
   * 将科室数据范围写入 MyBatis {@code params}，供 SQL 使用子查询过滤（避免 {@code IN (大量id)} 过长）。
   * 非租户管理员写入 {@code scopeDeptUserId}；若 {@code customerId} 非空则写入 {@code scopeDeptCustomerId}（设备端 sb_user_permission_dept 按客户过滤）。
   * 租户管理员不写上述键，表示不限制。
   */
  void applyDepartmentScopeQueryParams(Map<String, Object> params, Long userId, String customerId);
}

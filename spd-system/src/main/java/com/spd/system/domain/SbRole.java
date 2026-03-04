package com.spd.system.domain;

import java.util.Date;
import java.util.Set;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 设备角色表 sb_role
 *
 * 结构参考 sys_role
 */
public class SbRole extends BaseEntity {

  private static final long serialVersionUID = 1L;

  /** 角色ID（UUID7） */
  @Excel(name = "角色序号")
  private String roleId;

  /** 客户ID（UUID7），归属客户/租户 */
  private String customerId;

  /** 角色名称 */
  @Excel(name = "角色名称")
  private String roleName;

  /** 角色权限标识 */
  @Excel(name = "角色权限")
  private String roleKey;

  /** 角色排序 */
  @Excel(name = "角色排序")
  private Integer roleSort;

  /** 数据范围（1：所有数据权限；2：自定义数据权限...） */
  @Excel(name = "数据范围", readConverterExp = "1=所有数据权限,2=自定义数据权限,3=本部门数据权限,4=本部门及以下数据权限,5=仅本人数据权限")
  private String dataScope;

  /** 菜单树选择项是否关联显示 */
  private boolean menuCheckStrictly;

  /** 部门树选择项是否关联显示 */
  private boolean deptCheckStrictly;

  /** 角色状态（0正常 1停用） */
  @Excel(name = "角色状态", readConverterExp = "0=正常,1=停用")
  private String status;

  /** 删除标志（0代表存在 2代表删除） */
  private String delFlag;

  /** 删除者 */
  private String deleteBy;

  /** 删除时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date deleteTime;

  /** 用户是否存在此角色标识 默认不存在 */
  private boolean flag = false;

  /** 菜单组 */
  private String[] menuIds;

  /** 角色菜单权限（缓存用） */
  private Set<String> permissions;

  public String getRoleId() {
    return roleId;
  }

  public void setRoleId(String roleId) {
    this.roleId = roleId;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  @NotBlank(message = "角色名称不能为空")
  @Size(min = 0, max = 30, message = "角色名称长度不能超过30个字符")
  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  @NotBlank(message = "权限字符不能为空")
  @Size(min = 0, max = 100, message = "权限字符长度不能超过100个字符")
  public String getRoleKey() {
    return roleKey;
  }

  public void setRoleKey(String roleKey) {
    this.roleKey = roleKey;
  }

  @NotNull(message = "显示顺序不能为空")
  public Integer getRoleSort() {
    return roleSort;
  }

  public void setRoleSort(Integer roleSort) {
    this.roleSort = roleSort;
  }

  public String getDataScope() {
    return dataScope;
  }

  public void setDataScope(String dataScope) {
    this.dataScope = dataScope;
  }

  public boolean isMenuCheckStrictly() {
    return menuCheckStrictly;
  }

  public void setMenuCheckStrictly(boolean menuCheckStrictly) {
    this.menuCheckStrictly = menuCheckStrictly;
  }

  public boolean isDeptCheckStrictly() {
    return deptCheckStrictly;
  }

  public void setDeptCheckStrictly(boolean deptCheckStrictly) {
    this.deptCheckStrictly = deptCheckStrictly;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getDelFlag() {
    return delFlag;
  }

  public void setDelFlag(String delFlag) {
    this.delFlag = delFlag;
  }

  public String getDeleteBy() {
    return deleteBy;
  }

  public void setDeleteBy(String deleteBy) {
    this.deleteBy = deleteBy;
  }

  public Date getDeleteTime() {
    return deleteTime;
  }

  public void setDeleteTime(Date deleteTime) {
    this.deleteTime = deleteTime;
  }

  public boolean isFlag() {
    return flag;
  }

  public void setFlag(boolean flag) {
    this.flag = flag;
  }

  public String[] getMenuIds() {
    return menuIds;
  }

  public void setMenuIds(String[] menuIds) {
    this.menuIds = menuIds;
  }

  public Set<String> getPermissions() {
    return permissions;
  }

  public void setPermissions(Set<String> permissions) {
    this.permissions = permissions;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
        .append("roleId", getRoleId())
        .append("roleName", getRoleName())
        .append("roleKey", getRoleKey())
        .append("roleSort", getRoleSort())
        .append("dataScope", getDataScope())
        .append("menuCheckStrictly", isMenuCheckStrictly())
        .append("deptCheckStrictly", isDeptCheckStrictly())
        .append("status", getStatus())
        .append("delFlag", getDelFlag())
        .append("deleteBy", getDeleteBy())
        .append("deleteTime", getDeleteTime())
        .append("createBy", getCreateBy())
        .append("createTime", getCreateTime())
        .append("updateBy", getUpdateBy())
        .append("updateTime", getUpdateTime())
        .append("remark", getRemark())
        .toString();
  }
}


package com.spd.system.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.spd.common.core.domain.BaseEntity;

/**
 * 设备菜单权限表 sb_menu
 *
 * 结构参考 SysMenu，但独立出来方便与耗材菜单隔离。
 */
public class SbMenu extends BaseEntity {

  private static final long serialVersionUID = 1L;

  /** 菜单ID（UUID7） */
  private String menuId;

  /** 菜单名称 */
  private String menuName;

  /** 父菜单名称（仅展示用） */
  private String parentName;

  /** 父菜单ID（UUID7，根为0） */
  private String parentId;

  /** 显示顺序 */
  private Integer orderNum;

  /** 路由地址 */
  private String path;

  /** 组件路径 */
  private String component;

  /** 是否为外链（0是 1否） */
  private String isFrame;

  /** 是否缓存（0缓存 1不缓存） */
  private String isCache;

  /** 类型（M目录 C菜单 F按钮） */
  private String menuType;

  /** 显示状态（0显示 1隐藏） */
  private String visible;

  /** 菜单状态（0正常 1停用） */
  private String status;

  /** 权限字符串 */
  private String perms;

  /** 菜单图标 */
  private String icon;

  /** 是否仅平台管理功能（1是，客户分配/工作组/用户权限中不展示） */
  private String isPlatformOnly;

  /** 是否默认对客户开放（1是，设备功能重置时授权给客户、管理员组、管理员用户） */
  private String defaultOpenToCustomer;

  /** 删除者 */
  private String deleteBy;

  /** 删除时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date deleteTime;

  /** 删除标志（0正常 1删除） */
  private String delFlag;

  /** 客户菜单暂停状态（仅租户菜单树返回：0正常 1暂停） */
  private String customerMenuStatus;

  /** 子菜单 */
  private List<SbMenu> children = new ArrayList<>();

  public String getIsPlatformOnly() {
    return isPlatformOnly;
  }

  public void setIsPlatformOnly(String isPlatformOnly) {
    this.isPlatformOnly = isPlatformOnly;
  }

  public String getDefaultOpenToCustomer() {
    return defaultOpenToCustomer;
  }

  public void setDefaultOpenToCustomer(String defaultOpenToCustomer) {
    this.defaultOpenToCustomer = defaultOpenToCustomer;
  }

  public String getCustomerMenuStatus() {
    return customerMenuStatus;
  }

  public void setCustomerMenuStatus(String customerMenuStatus) {
    this.customerMenuStatus = customerMenuStatus;
  }

  public String getMenuId() {
    return menuId;
  }

  public void setMenuId(String menuId) {
    this.menuId = menuId;
  }

  @NotBlank(message = "菜单名称不能为空")
  @Size(min = 0, max = 50, message = "菜单名称长度不能超过50个字符")
  public String getMenuName() {
    return menuName;
  }

  public void setMenuName(String menuName) {
    this.menuName = menuName;
  }

  public String getParentName() {
    return parentName;
  }

  public void setParentName(String parentName) {
    this.parentName = parentName;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  @NotNull(message = "显示顺序不能为空")
  public Integer getOrderNum() {
    return orderNum;
  }

  public void setOrderNum(Integer orderNum) {
    this.orderNum = orderNum;
  }

  @Size(min = 0, max = 200, message = "路由地址不能超过200个字符")
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  @Size(min = 0, max = 255, message = "组件路径不能超过255个字符")
  public String getComponent() {
    return component;
  }

  public void setComponent(String component) {
    this.component = component;
  }

  public String getIsFrame() {
    return isFrame;
  }

  public void setIsFrame(String isFrame) {
    this.isFrame = isFrame;
  }

  public String getIsCache() {
    return isCache;
  }

  public void setIsCache(String isCache) {
    this.isCache = isCache;
  }

  @NotBlank(message = "菜单类型不能为空")
  public String getMenuType() {
    return menuType;
  }

  public void setMenuType(String menuType) {
    this.menuType = menuType;
  }

  public String getVisible() {
    return visible;
  }

  public void setVisible(String visible) {
    this.visible = visible;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Size(min = 0, max = 100, message = "权限标识长度不能超过100个字符")
  public String getPerms() {
    return perms;
  }

  public void setPerms(String perms) {
    this.perms = perms;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
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

  public String getDelFlag() {
    return delFlag;
  }

  public void setDelFlag(String delFlag) {
    this.delFlag = delFlag;
  }

  public List<SbMenu> getChildren() {
    return children;
  }

  public void setChildren(List<SbMenu> children) {
    this.children = children;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
        .append("menuId", getMenuId())
        .append("menuName", getMenuName())
        .append("parentId", getParentId())
        .append("orderNum", getOrderNum())
        .append("path", getPath())
        .append("component", getComponent())
        .append("isFrame", getIsFrame())
        .append("isCache", getIsCache())
        .append("menuType", getMenuType())
        .append("visible", getVisible())
        .append("status", getStatus())
        .append("perms", getPerms())
        .append("icon", getIcon())
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


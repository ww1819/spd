package com.spd.system.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 工作组科室权限 sb_work_group_dept
 */
public class SbWorkGroupDept {

  private String id;
  private String groupId;
  private Long deptId;
  private String customerId;
  private String createBy;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  private String deleteBy;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date deleteTime;

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getGroupId() { return groupId; }
  public void setGroupId(String groupId) { this.groupId = groupId; }
  public Long getDeptId() { return deptId; }
  public void setDeptId(Long deptId) { this.deptId = deptId; }
  public String getCustomerId() { return customerId; }
  public void setCustomerId(String customerId) { this.customerId = customerId; }
  public String getCreateBy() { return createBy; }
  public void setCreateBy(String createBy) { this.createBy = createBy; }
  public Date getCreateTime() { return createTime; }
  public void setCreateTime(Date createTime) { this.createTime = createTime; }
  public String getDeleteBy() { return deleteBy; }
  public void setDeleteBy(String deleteBy) { this.deleteBy = deleteBy; }
  public Date getDeleteTime() { return deleteTime; }
  public void setDeleteTime(Date deleteTime) { this.deleteTime = deleteTime; }
}

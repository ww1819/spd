package com.spd.department.domain;

import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.entity.SysUser;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdWarehouse;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 科室批量消耗对象 t_hc_ks_xh
 * 
 * @author spd
 * @date 2025-01-15
 */
public class DeptBatchConsume extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 消耗单号 */
    @Excel(name = "消耗单号")
    private String consumeBillNo;

    /** 消耗日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "消耗日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date consumeBillDate;

    /** 仓库ID */
    @Excel(name = "仓库ID")
    private Long warehouseId;

    /** 科室ID */
    @Excel(name = "科室ID")
    private Long departmentId;

    /** 操作人ID */
    @Excel(name = "操作人ID")
    private Long userId;

    /** 单据状态 */
    @Excel(name = "单据状态")
    private Integer consumeBillStatus;

    /** 总金额 */
    private java.math.BigDecimal totalAmount;

    /** 审核人姓名 */
    @Excel(name = "审核人")
    private String auditPersonName;

    /** 制单人姓名 */
    @Excel(name = "制单人")
    private String createrName;

    /** 审核人 */
    private String auditBy;

    /** 审核日期 */
    private Date auditDate;

    /** 驳回原因 */
    private String rejectReason;

    /** 删除标识 */
    private Integer delFlag;

    /** 删除者 */
    private String deleteBy;

    /** 删除时间 */
    private Date deleteTime;

    /** 租户ID(同sb_customer.customer_id) */
    private String tenantId;

    /** 是否反消耗单（0否 1是） */
    private Integer reverseFlag;

    /** 反消耗来源主单ID */
    private Long reverseOfConsumeId;

    /** 反消耗来源主单号 */
    private String reverseOfBillNo;

    /**
     * 为 true 时保存明细跳过「同科室库存/同批次」去重（HIS 按批次自动生成等多条镜像指向同一库存行时需要）。
     * 不落库，仅请求参数使用。
     */
    private transient Boolean disableEntryDedup;

    /** 单据来源（如 MANUAL、HIS_MIRROR_BATCH） */
    private String billSource;

    /** 1 时禁止手工退消耗（反消耗接口拒绝） */
    private Integer disallowReverse;

    /** HIS 计费抓取批次 ID */
    private String hisFetchBatchId;

    /** 保存时后台自动去重过滤条数（不落库） */
    private Integer dedupFilteredCount;

    /** 科室批量消耗明细信息 */
    private List<DeptBatchConsumeEntry> deptBatchConsumeEntryList;

    /** 仓库对象 */
    private FdWarehouse warehouse;

    /** 操作人对象 */
    private SysUser user;

    /** 科室对象 */
    private FdDepartment department;

    /** 审核人对象 */
    private SysUser auditPerson;

    /** 制单人对象 */
    private SysUser creater;

    /** 查询参数：起始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date beginDate;

    /** 查询参数：截止日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    /** 查询参数：耗材ID */
    private Long materialId;

    /** 查询参数：耗材名称 */
    private String materialName;

    /** 查询参数：规格 */
    private String specification;

    /** 查询参数：型号 */
    private String model;

    /** 查询参数：HIS收费编码 */
    private String hisChargeCode;

    /** 查询参数：患者住院号/门诊号 */
    private String patientId;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setConsumeBillNo(String consumeBillNo) 
    {
        this.consumeBillNo = consumeBillNo;
    }

    public String getConsumeBillNo() 
    {
        return consumeBillNo;
    }

    public void setConsumeBillDate(Date consumeBillDate) 
    {
        this.consumeBillDate = consumeBillDate;
    }

    public Date getConsumeBillDate() 
    {
        return consumeBillDate;
    }

    public void setWarehouseId(Long warehouseId) 
    {
        this.warehouseId = warehouseId;
    }

    public Long getWarehouseId() 
    {
        return warehouseId;
    }

    public void setDepartmentId(Long departmentId) 
    {
        this.departmentId = departmentId;
    }

    public Long getDepartmentId() 
    {
        return departmentId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setConsumeBillStatus(Integer consumeBillStatus) 
    {
        this.consumeBillStatus = consumeBillStatus;
    }

    public Integer getConsumeBillStatus() 
    {
        return consumeBillStatus;
    }

    public void setDelFlag(Integer delFlag) 
    {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag() 
    {
        return delFlag;
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

    public List<DeptBatchConsumeEntry> getDeptBatchConsumeEntryList()
    {
        return deptBatchConsumeEntryList;
    }

    public void setDeptBatchConsumeEntryList(List<DeptBatchConsumeEntry> deptBatchConsumeEntryList)
    {
        this.deptBatchConsumeEntryList = deptBatchConsumeEntryList;
    }

    public FdWarehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(FdWarehouse warehouse) {
        this.warehouse = warehouse;
    }

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    public FdDepartment getDepartment() {
        return department;
    }

    public void setDepartment(FdDepartment department) {
        this.department = department;
    }

    public String getAuditPersonName() {
        return auditPersonName;
    }

    public void setAuditPersonName(String auditPersonName) {
        this.auditPersonName = auditPersonName;
    }

    public SysUser getAuditPerson() {
        return auditPerson;
    }

    public void setAuditPerson(SysUser auditPerson) {
        this.auditPerson = auditPerson;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public SysUser getCreater() {
        return creater;
    }

    public void setCreater(SysUser creater) {
        this.creater = creater;
    }

    public java.math.BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(java.math.BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getAuditBy() {
        return auditBy;
    }

    public void setAuditBy(String auditBy) {
        this.auditBy = auditBy;
    }

    public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getHisChargeCode() {
        return hisChargeCode;
    }

    public void setHisChargeCode(String hisChargeCode) {
        this.hisChargeCode = hisChargeCode;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public Integer getReverseFlag() { return reverseFlag; }
    public void setReverseFlag(Integer reverseFlag) { this.reverseFlag = reverseFlag; }
    public Long getReverseOfConsumeId() { return reverseOfConsumeId; }
    public void setReverseOfConsumeId(Long reverseOfConsumeId) { this.reverseOfConsumeId = reverseOfConsumeId; }
    public String getReverseOfBillNo() { return reverseOfBillNo; }
    public void setReverseOfBillNo(String reverseOfBillNo) { this.reverseOfBillNo = reverseOfBillNo; }
    public Boolean getDisableEntryDedup() { return disableEntryDedup; }
    public void setDisableEntryDedup(Boolean disableEntryDedup) { this.disableEntryDedup = disableEntryDedup; }
    public String getBillSource() { return billSource; }
    public void setBillSource(String billSource) { this.billSource = billSource; }
    public Integer getDisallowReverse() { return disallowReverse; }
    public void setDisallowReverse(Integer disallowReverse) { this.disallowReverse = disallowReverse; }
    public String getHisFetchBatchId() { return hisFetchBatchId; }
    public void setHisFetchBatchId(String hisFetchBatchId) { this.hisFetchBatchId = hisFetchBatchId; }
    public Integer getDedupFilteredCount() { return dedupFilteredCount; }
    public void setDedupFilteredCount(Integer dedupFilteredCount) { this.dedupFilteredCount = dedupFilteredCount; }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("consumeBillNo", getConsumeBillNo())
            .append("consumeBillDate", getConsumeBillDate())
            .append("warehouseId", getWarehouseId())
            .append("departmentId", getDepartmentId())
            .append("userId", getUserId())
            .append("consumeBillStatus", getConsumeBillStatus())
            .append("delFlag", getDelFlag())
            .append("deleteBy", getDeleteBy())
            .append("deleteTime", getDeleteTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("totalAmount", getTotalAmount())
            .append("auditBy", getAuditBy())
            .append("auditDate", getAuditDate())
            .append("auditPersonName", getAuditPersonName())
            .append("createrName", getCreaterName())
            .append("rejectReason", getRejectReason())
            .append("reverseFlag", getReverseFlag())
            .append("reverseOfConsumeId", getReverseOfConsumeId())
            .append("reverseOfBillNo", getReverseOfBillNo())
            .append("billSource", getBillSource())
            .append("disallowReverse", getDisallowReverse())
            .append("hisFetchBatchId", getHisFetchBatchId())
            .append("deptBatchConsumeEntryList", getDeptBatchConsumeEntryList())
            .append("warehouse", getWarehouse())
            .append("user", getUser())
            .append("department", getDepartment())
            .append("auditPerson", getAuditPerson())
            .append("creater", getCreater())
            .append("dedupFilteredCount", getDedupFilteredCount())
            .toString();
    }
}

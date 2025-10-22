package com.spd.warehouse.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.entity.SysUser;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.domain.FdWarehouse;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 出入库对象 stk_io_bill
 *
 * @author spd
 * @date 2023-12-17
 */
public class StkIoBill extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 出入库单号 */
    @Excel(name = "出入库单号")
    private String billNo;

    /** 引用单号 */
    @Excel(name = "引用单号")
    private String refBillNo;

    /** 供应商ID */
    @Excel(name = "供应商ID")
    private Long supplerId;

    /** 出入库日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出入库日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date billDate;

    /** 仓库ID */
    @Excel(name = "仓库ID")
    private Long warehouseId;

    /** 科室ID */
    @Excel(name = "科室ID")
    private Long departmentId;

    /** 单据状态 */
    @Excel(name = "单据状态")
    private Integer billStatus;

    /** 操作人 */
    @Excel(name = "操作人")
    private Long userId;

    /** 出入库类型 */
    @Excel(name = "出入库类型")
    private Integer billType;

    private String materialId;

    /** 删除标识 */
    private Integer delFlag;

    /** 出入库明细信息 */
    private List<StkIoBillEntry> stkIoBillEntryList;

    /** 供应商对象 */
    private FdSupplier supplier;

    /** 科室对象 */
    private FdDepartment department;

    /** 仓库对象 */
    private FdWarehouse warehouse;

//    /** 耗材对象 */
//    private FdMaterial material;

    /** 操作人对象 */
    private SysUser user;

    /** 审核日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审核日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date auditDate;

    /** 配送员 */
    @Excel(name = "配送员")
    private String delPerson;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String telephone;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal totalAmount;

    /** 发票号 */
    @Excel(name = "发票号")
    private String invoiceNumber;

    /** 发票金额 */
    @Excel(name = "发票金额")
    private String invoiceAmount;

    /** 发票时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发票时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date invoiceTime;

    /** 采购员 */
    @Excel(name = "采购员")
    private Long proPerson;


    /** 审核人 */
    private SysUser auditPerson;


    /** 审核人姓名 */
    @Excel(name = "审核人")
    private String auditPersonName;

    /** 制单人 */
    private SysUser creater;


    /** 制单人姓名 */
    @Excel(name = "制单人")
    private String createrName;


    /** 是否月结 */
    private Integer isMonthInit;

    /** 查询参数：起始日期 */
    private Date beginDate;

    /** 查询参数：截止日期 */
    private Date endDate;

    /** 查询参数：耗材 */
    private String materialName;

    /** 查询参数：仓库 */
    private String warehouseName;



    private List<FdMaterial> materialList;

    /**
     * 科室申请单id
     */
    private String dApplyId;

    /**
     * 订单ID
     */
    private String dingdanId;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setBillNo(String billNo)
    {
        this.billNo = billNo;
    }

    public String getBillNo()
    {
        return billNo;
    }
    public void setRefBillNo(String refBillNo)
    {
        this.refBillNo = refBillNo;
    }

    public String getRefBillNo()
    {
        return refBillNo;
    }
    public void setSupplerId(Long supplerId)
    {
        this.supplerId = supplerId;
    }

    public Long getSupplerId()
    {
        return supplerId;
    }
    public void setBillDate(Date billDate)
    {
        this.billDate = billDate;
    }

    public Date getBillDate()
    {
        return billDate;
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

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public Long getDepartmentId()
    {
        return departmentId;
    }
    public void setBillStatus(Integer billStatus)
    {
        this.billStatus = billStatus;
    }

    public Integer getBillStatus()
    {
        return billStatus;
    }
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }
    public void setBillType(Integer billType)
    {
        this.billType = billType;
    }

    public Integer getBillType()
    {
        return billType;
    }
    public void setDelFlag(Integer delFlag)
    {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag()
    {
        return delFlag;
    }

    public List<StkIoBillEntry> getStkIoBillEntryList()
    {
        return stkIoBillEntryList;
    }

    public void setStkIoBillEntryList(List<StkIoBillEntry> stkIoBillEntryList)
    {
        this.stkIoBillEntryList = stkIoBillEntryList;
    }

    public FdSupplier getSupplier() {
        return supplier;
    }

    public void setSupplier(FdSupplier supplier) {
        this.supplier = supplier;
    }

    public FdDepartment getDepartment() {
        return department;
    }

    public void setDepartment(FdDepartment department) {
        this.department = department;
    }

    public FdWarehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(FdWarehouse warehouse) {
        this.warehouse = warehouse;
    }

//    public FdMaterial getMaterial() {
//        return material;
//    }
//
//    public void setMaterial(FdMaterial material) {
//        this.material = material;
//    }

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
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

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public List<FdMaterial> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(List<FdMaterial> materialList) {
        this.materialList = materialList;
    }

    public String getDelPerson() {
        return delPerson;
    }

    public void setDelPerson(String delPerson) {
        this.delPerson = delPerson;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public Date getInvoiceTime() {
        return invoiceTime;
    }

    public void setInvoiceTime(Date invoiceTime) {
        this.invoiceTime = invoiceTime;
    }

    public Long getProPerson() {
        return proPerson;
    }

    public void setProPerson(Long proPerson) {
        this.proPerson = proPerson;
    }

    public Integer getIsMonthInit() {
        return isMonthInit;
    }

    public void setIsMonthInit(Integer isMonthInit) {
        this.isMonthInit = isMonthInit;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("billNo", getBillNo())
            .append("refBillNo", getRefBillNo())
            .append("supplerId", getSupplerId())
            .append("billDate", getBillDate())
            .append("warehouseId", getWarehouseId())
            .append("departmentId", getDepartmentId())
            .append("billStatus", getBillStatus())
            .append("userId", getUserId())
            .append("billType", getBillType())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("auditBy", getAuditBy())
                .append("auditDate", getAuditDate())
                .append("remark", getRemark())
            .append("stkIoBillEntryList", getStkIoBillEntryList())
            .append("supplier", getSupplier())
//            .append("material", getMaterial())
            .append("warehouse", getWarehouse())
            .append("department", getDepartment())
            .append("user", getUser())
            .append("auditDate", getAuditDate())
            .append("beginDate", getBeginDate())
            .append("ennDate", getEndDate())
            .append("materialName", getMaterialName())
            .append("warehouseName", getWarehouseName())
            .append("materialList", getMaterialList())
            .append("delPerson", getDelPerson())
            .append("telephone", getTelephone())
            .append("totalAmount", getTotalAmount())
            .append("invoiceNumber", getInvoiceNumber())
            .append("invoiceAmount", getInvoiceAmount())
            .append("invoiceTime", getInvoiceTime())
            .append("proPerson", getProPerson())
            .append("isMonthInit", getIsMonthInit())
                .append("createrName", getCreaterName())
                .append("auditPersonName", getAuditPersonName())
                .append("creater", getCreater())
                .append("auditPerson", getAuditPerson())
            .toString();
    }

    public SysUser getAuditPerson() {
        return auditPerson;
    }

    public void setAuditPerson(SysUser auditPerson) {
        this.auditPerson = auditPerson;
    }


    public String getAuditPersonName() {
        return auditPersonName;
    }

    public void setAuditPersonName(String auditPersonName) {
        this.auditPersonName = auditPersonName;
    }

    public SysUser getCreater() {
        return creater;
    }

    public void setCreater(SysUser creater) {
        this.creater = creater;
    }


    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public String getDApplyId() {
        return dApplyId;
    }

    public void setDApplyId(String dApplyId) {
        this.dApplyId = dApplyId;
    }

    public String getDingdanId() {
        return dingdanId;
    }

    public void setDingdanId(String dingdanId) {
        this.dingdanId = dingdanId;
    }
}

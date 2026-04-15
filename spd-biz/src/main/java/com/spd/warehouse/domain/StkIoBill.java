package com.spd.warehouse.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.entity.SysUser;
import com.spd.foundation.domain.*;
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

    @Excel(name = "产品档案ID")
    private String materialId;

    /** 产品名称/编码/拼音简码 模糊查询（出退库查询） */
    private String materialNameLike;
    /** 规格 模糊查询（出退库查询） */
    private String materialSpeciLike;
    /** 型号 模糊查询（出退库查询） */
    private String materialModelLike;

    @Excel(name = "库房分类ID")
    private String warehouseCategoryId;

    /** 删除标识 */
    private Integer delFlag;

    /** 租户ID(同sb_customer.customer_id) */
    private String tenantId;

    /** 删除者 */
    private String deleteBy;
    /** 删除时间 */
    private Date deleteTime;

    /** 结算方式（来自仓库：1入库结算 2出库结算 3消耗结算） */
    private String settlementType;

    /** 出入库明细信息 */
    private List<StkIoBillEntry> stkIoBillEntryList;

    /** 保存时可传：单据引用关联（不入库 stk_io_bill，写入 hc_doc_bill_ref） */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<HcDocBillRef> docRefList;

    /** 作为源单被下游引用状态：NONE 未引用 / PARTIAL 部分引用 / FULL 全部引用（列表查询计算，不落库） */
    private String docRefStatus;

    /** 供应商对象 */
    private FdSupplier supplier;

    /** 科室对象 */
    private FdDepartment department;

    /** 仓库对象 */
    private FdWarehouse warehouse;

    /** 调入仓库对象（用于调拨单） */
    private FdWarehouse toWarehouse;

    /** 库房分类对象 */
    private FdWarehouseCategory warehouseCategory;


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

    /** 退货原因 */
    @Excel(name = "退货原因")
    private String returnReason;

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

    /** 更新人姓名 */
    private String updateByUserName;

    /** 更新人昵称 */
    private String updateByNickName;

    /** 是否月结 */
    private Integer isMonthInit;

    /** 收货确认状态（0=未确认，1=已确认） */
    private Integer receiptConfirmStatus;

    /** 查询参数：起始日期 */
    private Date beginDate;

    /** 查询参数：截止日期 */
    private Date endDate;

    /** 查询参数：审核日期起（出库日期） */
    private Date auditBeginDate;

    /** 查询参数：审核日期止（出库日期） */
    private Date auditEndDate;

    /** 查询参数：耗材 */
    private String materialName;

    /** 查询参数：仓库 */
    private String warehouseName;

    /** 查询参数：供应商关键字（名称/编码/简码） */
    private String supplierKeyword;

    /** 查询参数：是否过滤“上期=0且本期入出=0”的供应商（1=过滤） */
    private Integer excludeZeroNoBiz;

    /** 查询参数：批号 */
    private String batchNo;

    /** 列表排序场景：apply=申请页，audit=审核页 */
    private String sortScene;

    /**
     * 列表日期条件维度：bill=按制单日期(bill_date)，audit=按审核日期(audit_date)；为空时与 audit 相同（兼容旧请求）
     */
    private String dateQueryType;

    /** 出库按单导出：勾选的主键 id，逗号分隔；不传则按其它条件导出全部匹配单据 */
    private String exportBillIds;

    private List<FdMaterial> materialList;

    /**
     * 科室申请单id
     */
    private String dApplyId;

    /** 库房申请单主键 UUID7（出库单由库房申请单生成时携带，不落 stk_io_bill 表，仅用于保存后写关联） */
    private String whWarehouseApplyId;

    /** 库房申请单号（冗余，写关联表） */
    private String whWarehouseApplyBillNo;

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

    public String getMaterialNameLike() {
        return materialNameLike;
    }

    public void setMaterialNameLike(String materialNameLike) {
        this.materialNameLike = materialNameLike;
    }

    public String getMaterialSpeciLike() {
        return materialSpeciLike;
    }

    public void setMaterialSpeciLike(String materialSpeciLike) {
        this.materialSpeciLike = materialSpeciLike;
    }

    public String getMaterialModelLike() {
        return materialModelLike;
    }

    public void setMaterialModelLike(String materialModelLike) {
        this.materialModelLike = materialModelLike;
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

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    public String getDeleteBy() { return deleteBy; }
    public void setDeleteBy(String deleteBy) { this.deleteBy = deleteBy; }
    public Date getDeleteTime() { return deleteTime; }
    public void setDeleteTime(Date deleteTime) { this.deleteTime = deleteTime; }

    public String getSettlementType() {
        return settlementType;
    }

    public void setSettlementType(String settlementType) {
        this.settlementType = settlementType;
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

    public FdWarehouse getToWarehouse() {
        return toWarehouse;
    }

    public void setToWarehouse(FdWarehouse toWarehouse) {
        this.toWarehouse = toWarehouse;
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

    public Date getAuditBeginDate() {
        return auditBeginDate;
    }

    public void setAuditBeginDate(Date auditBeginDate) {
        this.auditBeginDate = auditBeginDate;
    }

    public Date getAuditEndDate() {
        return auditEndDate;
    }

    public void setAuditEndDate(Date auditEndDate) {
        this.auditEndDate = auditEndDate;
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

    public String getSupplierKeyword() {
        return supplierKeyword;
    }

    public void setSupplierKeyword(String supplierKeyword) {
        this.supplierKeyword = supplierKeyword;
    }

    public Integer getExcludeZeroNoBiz() {
        return excludeZeroNoBiz;
    }

    public void setExcludeZeroNoBiz(Integer excludeZeroNoBiz) {
        this.excludeZeroNoBiz = excludeZeroNoBiz;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getSortScene() {
        return sortScene;
    }

    public void setSortScene(String sortScene) {
        this.sortScene = sortScene;
    }

    public String getDateQueryType() {
        return dateQueryType;
    }

    public void setDateQueryType(String dateQueryType) {
        this.dateQueryType = dateQueryType;
    }

    public String getExportBillIds() {
        return exportBillIds;
    }

    public void setExportBillIds(String exportBillIds) {
        this.exportBillIds = exportBillIds;
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

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    public Integer getIsMonthInit() {
        return isMonthInit;
    }

    public void setIsMonthInit(Integer isMonthInit) {
        this.isMonthInit = isMonthInit;
    }

    public Integer getReceiptConfirmStatus() {
        return receiptConfirmStatus;
    }

    public void setReceiptConfirmStatus(Integer receiptConfirmStatus) {
        this.receiptConfirmStatus = receiptConfirmStatus;
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
            .append("tenantId", getTenantId())
            .append("settlementType", getSettlementType())
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
            .append("supplierKeyword", getSupplierKeyword())
            .append("excludeZeroNoBiz", getExcludeZeroNoBiz())
            .append("sortScene", getSortScene())
            .append("dateQueryType", getDateQueryType())
            .append("materialList", getMaterialList())
            .append("delPerson", getDelPerson())
            .append("telephone", getTelephone())
            .append("totalAmount", getTotalAmount())
            .append("invoiceNumber", getInvoiceNumber())
            .append("invoiceAmount", getInvoiceAmount())
            .append("invoiceTime", getInvoiceTime())
                .append("proPerson", getProPerson())
                .append("returnReason", getReturnReason())
                .append("isMonthInit", getIsMonthInit())
                .append("receiptConfirmStatus", getReceiptConfirmStatus())
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

    public String getUpdateByUserName() {
        return updateByUserName;
    }

    public void setUpdateByUserName(String updateByUserName) {
        this.updateByUserName = updateByUserName;
    }

    public String getUpdateByNickName() {
        return updateByNickName;
    }

    public void setUpdateByNickName(String updateByNickName) {
        this.updateByNickName = updateByNickName;
    }

    public String getDApplyId() {
        return dApplyId;
    }

    public void setDApplyId(String dApplyId) {
        this.dApplyId = dApplyId;
    }

    public String getWhWarehouseApplyId() {
        return whWarehouseApplyId;
    }

    public void setWhWarehouseApplyId(String whWarehouseApplyId) {
        this.whWarehouseApplyId = whWarehouseApplyId;
    }

    public String getWhWarehouseApplyBillNo() {
        return whWarehouseApplyBillNo;
    }

    public void setWhWarehouseApplyBillNo(String whWarehouseApplyBillNo) {
        this.whWarehouseApplyBillNo = whWarehouseApplyBillNo;
    }

    public String getDingdanId() {
        return dingdanId;
    }

    public void setDingdanId(String dingdanId) {
        this.dingdanId = dingdanId;
    }

    public String getWarehouseCategoryId() {
        return warehouseCategoryId;
    }

    public void setWarehouseCategoryId(String warehouseCategoryId) {
        this.warehouseCategoryId = warehouseCategoryId;
    }

    public FdWarehouseCategory getWarehouseCategory() {
        return warehouseCategory;
    }

    public void setWarehouseCategory(FdWarehouseCategory warehouseCategory) {
        this.warehouseCategory = warehouseCategory;
    }

    public List<HcDocBillRef> getDocRefList() {
        return docRefList;
    }

    public void setDocRefList(List<HcDocBillRef> docRefList) {
        this.docRefList = docRefList;
    }

    public String getDocRefStatus() {
        return docRefStatus;
    }

    public void setDocRefStatus(String docRefStatus) {
        this.docRefStatus = docRefStatus;
    }
}

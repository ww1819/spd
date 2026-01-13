package com.spd.gz.domain;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.foundation.domain.FdDepartment;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 高值追溯单对象 gz_traceability
 *
 * @author spd
 * @date 2025-01-01
 */
public class GzTraceability extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 追溯单号（唯一） */
    @Excel(name = "追溯单号")
    private String traceNo;

    /** 病历号 */
    @Excel(name = "病历号")
    private String medicalRecordNo;

    /** 患者姓名 */
    @Excel(name = "患者姓名")
    private String patientName;

    /** 患者性别 */
    @Excel(name = "患者性别")
    private String patientSex;

    /** 患者年龄 */
    @Excel(name = "患者年龄")
    private Integer patientAge;

    /** 住院号 */
    @Excel(name = "住院号")
    private String hospitalNumber;

    /** 病区 */
    @Excel(name = "病区")
    private String ward;

    /** 病房号 */
    @Excel(name = "病房号")
    private String wardNo;

    /** 病床号 */
    @Excel(name = "病床号")
    private String bedNo;

    /** 申请科室ID */
    private Long applyDeptId;

    /** 执行科室ID */
    private Long execDeptId;

    /** 住院日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "住院日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date hospitalDate;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String contactPhone;

    /** 联系地址 */
    @Excel(name = "联系地址")
    private String contactAddress;

    /** 主刀医生 */
    @Excel(name = "主刀医生")
    private String chiefSurgeon;

    /** 手术日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "手术日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date surgeryDate;

    /** 手术名称 */
    @Excel(name = "手术名称")
    private String surgeryName;

    /** 入院诊断 */
    @Excel(name = "入院诊断")
    private String admissionDiagnosis;

    /** 手术ID */
    @Excel(name = "手术ID")
    private String surgeryId;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;

    /** 单据状态（1=未审核，2=已审核） */
    @Excel(name = "单据状态", readConverterExp = "1=未审核,2=已审核")
    private Integer orderStatus;

    /** 审核日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date auditDate;

    /** 审核人 */
    @Excel(name = "审核人")
    private String auditBy;

    /** 删除标识 */
    private String delFlag;

    /** 申请科室对象 */
    private FdDepartment applyDept;

    /** 执行科室对象 */
    private FdDepartment execDept;

    /** 追溯单明细列表 */
    private List<GzTraceabilityEntry> traceabilityEntryList;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setTraceNo(String traceNo)
    {
        this.traceNo = traceNo;
    }

    public String getTraceNo()
    {
        return traceNo;
    }

    public void setMedicalRecordNo(String medicalRecordNo)
    {
        this.medicalRecordNo = medicalRecordNo;
    }

    public String getMedicalRecordNo()
    {
        return medicalRecordNo;
    }

    public void setPatientName(String patientName)
    {
        this.patientName = patientName;
    }

    public String getPatientName()
    {
        return patientName;
    }

    public void setPatientSex(String patientSex)
    {
        this.patientSex = patientSex;
    }

    public String getPatientSex()
    {
        return patientSex;
    }

    public void setPatientAge(Integer patientAge)
    {
        this.patientAge = patientAge;
    }

    public Integer getPatientAge()
    {
        return patientAge;
    }

    public void setHospitalNumber(String hospitalNumber)
    {
        this.hospitalNumber = hospitalNumber;
    }

    public String getHospitalNumber()
    {
        return hospitalNumber;
    }

    public void setWard(String ward)
    {
        this.ward = ward;
    }

    public String getWard()
    {
        return ward;
    }

    public void setWardNo(String wardNo)
    {
        this.wardNo = wardNo;
    }

    public String getWardNo()
    {
        return wardNo;
    }

    public void setBedNo(String bedNo)
    {
        this.bedNo = bedNo;
    }

    public String getBedNo()
    {
        return bedNo;
    }

    public void setApplyDeptId(Long applyDeptId)
    {
        this.applyDeptId = applyDeptId;
    }

    public Long getApplyDeptId()
    {
        return applyDeptId;
    }

    public void setExecDeptId(Long execDeptId)
    {
        this.execDeptId = execDeptId;
    }

    public Long getExecDeptId()
    {
        return execDeptId;
    }

    public void setHospitalDate(Date hospitalDate)
    {
        this.hospitalDate = hospitalDate;
    }

    public Date getHospitalDate()
    {
        return hospitalDate;
    }

    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    public String getContactPhone()
    {
        return contactPhone;
    }

    public void setContactAddress(String contactAddress)
    {
        this.contactAddress = contactAddress;
    }

    public String getContactAddress()
    {
        return contactAddress;
    }

    public void setChiefSurgeon(String chiefSurgeon)
    {
        this.chiefSurgeon = chiefSurgeon;
    }

    public String getChiefSurgeon()
    {
        return chiefSurgeon;
    }

    public void setSurgeryDate(Date surgeryDate)
    {
        this.surgeryDate = surgeryDate;
    }

    public Date getSurgeryDate()
    {
        return surgeryDate;
    }

    public void setSurgeryName(String surgeryName)
    {
        this.surgeryName = surgeryName;
    }

    public String getSurgeryName()
    {
        return surgeryName;
    }

    public void setAdmissionDiagnosis(String admissionDiagnosis)
    {
        this.admissionDiagnosis = admissionDiagnosis;
    }

    public String getAdmissionDiagnosis()
    {
        return admissionDiagnosis;
    }

    public void setSurgeryId(String surgeryId)
    {
        this.surgeryId = surgeryId;
    }

    public String getSurgeryId()
    {
        return surgeryId;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setOrderStatus(Integer orderStatus)
    {
        this.orderStatus = orderStatus;
    }

    public Integer getOrderStatus()
    {
        return orderStatus;
    }

    public void setAuditDate(Date auditDate)
    {
        this.auditDate = auditDate;
    }

    public Date getAuditDate()
    {
        return auditDate;
    }

    public void setAuditBy(String auditBy)
    {
        this.auditBy = auditBy;
    }

    public String getAuditBy()
    {
        return auditBy;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public FdDepartment getApplyDept() {
        return applyDept;
    }

    public void setApplyDept(FdDepartment applyDept) {
        this.applyDept = applyDept;
    }

    public FdDepartment getExecDept() {
        return execDept;
    }

    public void setExecDept(FdDepartment execDept) {
        this.execDept = execDept;
    }

    public List<GzTraceabilityEntry> getTraceabilityEntryList() {
        return traceabilityEntryList;
    }

    public void setTraceabilityEntryList(List<GzTraceabilityEntry> traceabilityEntryList) {
        this.traceabilityEntryList = traceabilityEntryList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("traceNo", getTraceNo())
            .append("medicalRecordNo", getMedicalRecordNo())
            .append("patientName", getPatientName())
            .append("patientSex", getPatientSex())
            .append("patientAge", getPatientAge())
            .append("hospitalNumber", getHospitalNumber())
            .append("ward", getWard())
            .append("wardNo", getWardNo())
            .append("bedNo", getBedNo())
            .append("applyDeptId", getApplyDeptId())
            .append("execDeptId", getExecDeptId())
            .append("hospitalDate", getHospitalDate())
            .append("contactPhone", getContactPhone())
            .append("contactAddress", getContactAddress())
            .append("chiefSurgeon", getChiefSurgeon())
            .append("surgeryDate", getSurgeryDate())
            .append("surgeryName", getSurgeryName())
            .append("admissionDiagnosis", getAdmissionDiagnosis())
            .append("surgeryId", getSurgeryId())
            .append("remark", getRemark())
            .append("orderStatus", getOrderStatus())
            .append("auditDate", getAuditDate())
            .append("auditBy", getAuditBy())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

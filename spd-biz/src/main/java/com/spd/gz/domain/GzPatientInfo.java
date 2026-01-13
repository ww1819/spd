package com.spd.gz.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.foundation.domain.FdDepartment;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 患者信息对象 gz_patient_info
 *
 * @author spd
 * @date 2025-01-01
 */
public class GzPatientInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 病历号（唯一） */
    @Excel(name = "病历号")
    private String medicalRecordNo;

    /** 姓名 */
    @Excel(name = "姓名")
    private String name;

    /** 性别 */
    @Excel(name = "性别")
    private String sex;

    /** 年龄 */
    @Excel(name = "年龄")
    private Integer age;

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

    /** 删除标识 */
    private String delFlag;

    /** 申请科室对象 */
    private FdDepartment applyDept;

    /** 执行科室对象 */
    private FdDepartment execDept;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setMedicalRecordNo(String medicalRecordNo)
    {
        this.medicalRecordNo = medicalRecordNo;
    }

    public String getMedicalRecordNo()
    {
        return medicalRecordNo;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getSex()
    {
        return sex;
    }

    public void setAge(Integer age)
    {
        this.age = age;
    }

    public Integer getAge()
    {
        return age;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("medicalRecordNo", getMedicalRecordNo())
            .append("name", getName())
            .append("sex", getSex())
            .append("age", getAge())
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
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

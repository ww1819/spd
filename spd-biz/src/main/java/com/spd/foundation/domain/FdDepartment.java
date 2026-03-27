package com.spd.foundation.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 科室对象 fd_department
 *
 * @author spd
 * @date 2023-11-26
 */
public class FdDepartment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID（仅导出，导入模板不含此列） */
    @Excel(name = "科室序号", cellType = Excel.ColumnType.NUMERIC, type = Excel.Type.EXPORT)
    private Long id;

    /** 科室编码 */
    @Excel(name = "科室编码")
    private String code;

    /** 科室名称 */
    @Excel(name = "科室名称")
    private String name;

    /** 拼音简码 */
    @Excel(name = "简码")
    private String referredName;

    /** HIS 系统科室 ID（库字段 his_id；与 HIS 等对接） */
    @Excel(name = "HIS系统ID", nameAliases = {"HIS科室ID", "其他第三方系统科室ID"}, width = 22, prompt = "对接 HIS 等系统时填写科室标识；部分租户导入时必填")
    private String hisId;

    /** 删除标识 */
    private Integer delFlag;

    /** 租户ID(同sb_customer.customer_id)，耗材与租户关联 */
    private String tenantId;

    /** 上级科室ID（NULL 表示客户下顶级） */
    private Long parentId;

    /** 列表/导出：按直接上级筛选（非表字段，请求参数） */
    private Long treeParentId;

    /** 备注（表字段 fd_department.remark，与基类 remark 区分，避免全局基类加 Excel 注解） */
    @Excel(name = "备注")
    private String deptRemark;

    @Excel(name = "数据校验结果", width = 40, sort = 99999)
    private String validationResult;

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public Long getTreeParentId()
    {
        return treeParentId;
    }

    public void setTreeParentId(Long treeParentId)
    {
        this.treeParentId = treeParentId;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String getReferredName() {
        return referredName;
    }

    public void setReferredName(String referredName) {
        this.referredName = referredName;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public String getDeptRemark() {
        return deptRemark;
    }

    public void setDeptRemark(String deptRemark) {
        this.deptRemark = deptRemark;
    }

    public String getHisId() {
        return hisId;
    }

    public void setHisId(String hisId) {
        this.hisId = hisId;
    }

    public String getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(String validationResult) {
        this.validationResult = validationResult;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("name", getName())
            .append("referredName", getReferredName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .append("tenantId", getTenantId())
            .append("parentId", getParentId())
            .append("deptRemark", getDeptRemark())
            .append("hisId", getHisId())
            .append("validationResult", getValidationResult())
            .toString();
    }
}

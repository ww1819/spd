package com.spd.foundation.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 客户68分类对象 sb_customer_category68（以 fd_category68 为蓝本，主键 UUID7，含删除标志/删除者/删除时间）
 *
 * @author spd
 */
public class SbCustomerCategory68 extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键 UUID7 */
    private String id;

    /** 客户ID(UUID7) */
    private String customerId;

    /** 对应标准68分类ID(fd_category68.category68_id) */
    private Long refCategory68Id;

    /** 父分类ID(本表主键id，对应父记录) */
    private String parentId;

    /** 68分类编码 */
    @Excel(name = "68分类编码")
    private String category68Code;

    /** 68分类名称 */
    @Excel(name = "68分类名称")
    private String category68Name;

    /** 删除标志(0正常 1已删除) */
    private Integer delFlag;

    /** 删除者 */
    private String delBy;

    /** 删除时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date delTime;

    /** 子分类列表（树形） */
    private List<SbCustomerCategory68> children;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public Long getRefCategory68Id() { return refCategory68Id; }
    public void setRefCategory68Id(Long refCategory68Id) { this.refCategory68Id = refCategory68Id; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public String getCategory68Code() { return category68Code; }
    public void setCategory68Code(String category68Code) { this.category68Code = category68Code; }

    public String getCategory68Name() { return category68Name; }
    public void setCategory68Name(String category68Name) { this.category68Name = category68Name; }

    public Integer getDelFlag() { return delFlag; }
    public void setDelFlag(Integer delFlag) { this.delFlag = delFlag; }

    public String getDelBy() { return delBy; }
    public void setDelBy(String delBy) { this.delBy = delBy; }

    public Date getDelTime() { return delTime; }
    public void setDelTime(Date delTime) { this.delTime = delTime; }

    public List<SbCustomerCategory68> getChildren() { return children; }
    public void setChildren(List<SbCustomerCategory68> children) { this.children = children; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", id)
            .append("customerId", customerId)
            .append("refCategory68Id", refCategory68Id)
            .append("parentId", parentId)
            .append("category68Code", category68Code)
            .append("category68Name", category68Name)
            .append("delFlag", delFlag)
            .append("delBy", delBy)
            .append("delTime", delTime)
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

package com.spd.foundation.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 68分类对象 fd_category68
 * 
 * @author spd
 * @date 2024-04-12
 */
public class FdCategory68 extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long category68Id;

    /** 父分类ID */
    private Long parentId;

    /** 子分类列表 */
    private java.util.List<FdCategory68> children;

    /** 68分类编码 */
    @Excel(name = "68分类编码")
    private String category68Code;

    /** 68分类名称 */
    @Excel(name = "68分类名称")
    private String category68Name;

    /** 删除标识 */
    private Integer delFlag;

    public void setCategory68Id(Long category68Id) 
    {
        this.category68Id = category68Id;
    }

    public Long getCategory68Id() 
    {
        return category68Id;
    }
    public void setCategory68Code(String category68Code) 
    {
        this.category68Code = category68Code;
    }

    public String getCategory68Code() 
    {
        return category68Code;
    }
    public void setCategory68Name(String category68Name) 
    {
        this.category68Name = category68Name;
    }

    public String getCategory68Name() 
    {
        return category68Name;
    }
    public void setDelFlag(Integer delFlag) 
    {
        this.delFlag = delFlag;
    }

    public Integer getDelFlag() 
    {
        return delFlag;
    }
    public void setParentId(Long parentId) 
    {
        this.parentId = parentId;
    }

    public Long getParentId() 
    {
        return parentId;
    }
    public void setChildren(java.util.List<FdCategory68> children) 
    {
        this.children = children;
    }

    public java.util.List<FdCategory68> getChildren() 
    {
        return children;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("category68Id", getCategory68Id())
            .append("parentId", getParentId())
            .append("category68Code", getCategory68Code())
            .append("category68Name", getCategory68Name())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}


package com.spd.system.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.core.domain.BaseEntity;

/**
 * 按单据类型的打印每页行数 sys_print_doc_rows
 *
 * @author spd
 */
public class SysPrintDocRows extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    /** INBOUND / OUTBOUND / REFUND_DEPOT / REFUND_GOODS */
    @NotBlank(message = "docKind不能为空")
    private String docKind;

    @NotNull(message = "rowsPerPage不能为空")
    private Integer rowsPerPage;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getDocKind()
    {
        return docKind;
    }

    public void setDocKind(String docKind)
    {
        this.docKind = docKind;
    }

    public Integer getRowsPerPage()
    {
        return rowsPerPage;
    }

    public void setRowsPerPage(Integer rowsPerPage)
    {
        this.rowsPerPage = rowsPerPage;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("docKind", getDocKind())
            .append("rowsPerPage", getRowsPerPage())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

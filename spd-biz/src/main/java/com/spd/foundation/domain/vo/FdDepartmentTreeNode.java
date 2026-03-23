package com.spd.foundation.domain.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 科室维护左侧树节点（含虚拟客户根节点：deptId 为 null、nodeKey 为 root）
 */
public class FdDepartmentTreeNode implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String nodeKey;
    private Long deptId;
    private String label;
    private List<FdDepartmentTreeNode> children = new ArrayList<>();

    public String getNodeKey()
    {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey)
    {
        this.nodeKey = nodeKey;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public List<FdDepartmentTreeNode> getChildren()
    {
        return children;
    }

    public void setChildren(List<FdDepartmentTreeNode> children)
    {
        this.children = children != null ? children : new ArrayList<>();
    }
}

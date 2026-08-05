package com.spd.foundation.domain;

import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 18类重点耗材维护 fd_focus18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FdFocus18 extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    @Excel(name = "耗材类别")
    private String category;

    @Excel(name = "耗材分类代码")
    private String classCode;

    @Excel(name = "一级分类(学科/品类)")
    private String level1;

    @Excel(name = "二级分类(用途/品目)")
    private String level2;

    @Excel(name = "三级分类(部位/功能/品种)")
    private String level3;

    @Excel(name = "通用名代码")
    private String genericCode;

    @Excel(name = "医保通用名")
    private String medicalGenericName;

    @Excel(name = "材质代码")
    private String materialCode;

    @Excel(name = "材质")
    private String material;

    @Excel(name = "特征代码")
    private String featureCode;

    @Excel(name = "特征参数")
    private String featureParam;

    /** 查询：仅查耗材类别为空（左侧树「未分类」） */
    private Boolean emptyCategory;

    private Integer delFlag;

    private String tenantId;
}

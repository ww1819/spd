package com.spd.department.service;

import java.util.List;
import com.spd.department.domain.BasApplyTemplate;

/**
 * 科室申领制单模板Service接口
 *
 * @author spd
 */
public interface IBasApplyTemplateService {

    /**
     * 查询制单模板列表（按模板名称模糊）
     *
     * @param template 查询条件
     * @return 制单模板集合
     */
    List<BasApplyTemplate> selectBasApplyTemplateList(BasApplyTemplate template);

    /**
     * 根据ID查询制单模板（含明细）
     *
     * @param id 主键
     * @return 制单模板
     */
    BasApplyTemplate selectBasApplyTemplateById(Long id);

    /**
     * 新增制单模板（含明细）
     *
     * @param template 制单模板
     * @return 结果
     */
    int insertBasApplyTemplate(BasApplyTemplate template);

    /**
     * 删除制单模板（及明细）
     *
     * @param id 主键
     * @return 结果
     */
    int deleteBasApplyTemplateById(Long id);

    /**
     * 修改制单模板（含明细：先删后插）
     *
     * @param template 制单模板
     * @return 结果
     */
    int updateBasApplyTemplate(BasApplyTemplate template);
}

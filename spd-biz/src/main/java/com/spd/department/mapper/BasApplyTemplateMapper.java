package com.spd.department.mapper;

import java.util.List;
import com.spd.department.domain.BasApplyTemplate;
import com.spd.department.domain.BasApplyTemplateEntry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 科室申领制单模板Mapper接口
 *
 * @author spd
 */
@Mapper
public interface BasApplyTemplateMapper {

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
     * 新增制单模板
     *
     * @param template 制单模板
     * @return 结果
     */
    int insertBasApplyTemplate(BasApplyTemplate template);

    /**
     * 修改制单模板
     *
     * @param template 制单模板
     * @return 结果
     */
    int updateBasApplyTemplate(BasApplyTemplate template);

    /**
     * 删除制单模板
     *
     * @param id 主键
     * @return 结果
     */
    int deleteBasApplyTemplateById(Long id);

    /**
     * 删除制单模板明细
     *
     * @param parenId 模板主表ID
     * @return 结果
     */
    int deleteEntryByParenId(@Param("parenId") Long parenId);

    /**
     * 批量新增制单模板明细
     *
     * @param list 明细列表
     * @return 结果
     */
    int batchInsertEntry(List<BasApplyTemplateEntry> list);
}

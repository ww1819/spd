package com.spd.department.mapper;

import java.util.List;
import com.spd.department.domain.DepInventoryWarning;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 科室库存预警设置Mapper接口
 *
 * @author spd
 * @date 2026-01-03
 */
@Mapper
@Repository
public interface DepInventoryWarningMapper
{
    /**
     * 查询科室库存预警设置
     *
     * @param id 科室库存预警设置主键
     * @return 科室库存预警设置
     */
    public DepInventoryWarning selectDepInventoryWarningById(Long id);

    /**
     * 查询科室库存预警设置列表
     *
     * @param depInventoryWarning 科室库存预警设置
     * @return 科室库存预警设置集合
     */
    public List<DepInventoryWarning> selectDepInventoryWarningList(DepInventoryWarning depInventoryWarning);

    /**
     * 新增科室库存预警设置
     *
     * @param depInventoryWarning 科室库存预警设置
     * @return 结果
     */
    public int insertDepInventoryWarning(DepInventoryWarning depInventoryWarning);

    /**
     * 修改科室库存预警设置
     *
     * @param depInventoryWarning 科室库存预警设置
     * @return 结果
     */
    public int updateDepInventoryWarning(DepInventoryWarning depInventoryWarning);

    /**
     * 删除科室库存预警设置
     *
     * @param id 科室库存预警设置主键
     * @return 结果
     */
    public int deleteDepInventoryWarningById(Long id);

    /**
     * 批量删除科室库存预警设置
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDepInventoryWarningByIds(Long[] ids);
}


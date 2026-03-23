package com.spd.department.mapper;

import java.util.List;
import com.spd.department.domain.DepInventoryWarning;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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

    /** 逻辑删除（设置 del_flag、delete_by、delete_time） */
    public int deleteDepInventoryWarningById(@Param("id") Long id, @Param("deleteBy") String deleteBy);

    /** 批量逻辑删除 */
    public int deleteDepInventoryWarningByIds(@Param("ids") Long[] ids, @Param("deleteBy") String deleteBy);
}


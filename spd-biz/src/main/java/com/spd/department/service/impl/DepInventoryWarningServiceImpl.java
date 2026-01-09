package com.spd.department.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.department.mapper.DepInventoryWarningMapper;
import com.spd.department.domain.DepInventoryWarning;
import com.spd.department.service.IDepInventoryWarningService;
import com.spd.common.utils.DateUtils;

/**
 * 科室库存预警设置Service业务层处理
 *
 * @author spd
 * @date 2026-01-03
 */
@Service
public class DepInventoryWarningServiceImpl implements IDepInventoryWarningService
{
    @Autowired
    private DepInventoryWarningMapper depInventoryWarningMapper;

    /**
     * 查询科室库存预警设置
     *
     * @param id 科室库存预警设置主键
     * @return 科室库存预警设置
     */
    @Override
    public DepInventoryWarning selectDepInventoryWarningById(Long id)
    {
        return depInventoryWarningMapper.selectDepInventoryWarningById(id);
    }

    /**
     * 查询科室库存预警设置列表
     *
     * @param depInventoryWarning 科室库存预警设置
     * @return 科室库存预警设置
     */
    @Override
    public List<DepInventoryWarning> selectDepInventoryWarningList(DepInventoryWarning depInventoryWarning)
    {
        return depInventoryWarningMapper.selectDepInventoryWarningList(depInventoryWarning);
    }

    /**
     * 新增科室库存预警设置
     *
     * @param depInventoryWarning 科室库存预警设置
     * @return 结果
     */
    @Override
    public int insertDepInventoryWarning(DepInventoryWarning depInventoryWarning)
    {
        depInventoryWarning.setCreateTime(DateUtils.getNowDate());
        depInventoryWarning.setDelFlag(0);
        return depInventoryWarningMapper.insertDepInventoryWarning(depInventoryWarning);
    }

    /**
     * 修改科室库存预警设置
     *
     * @param depInventoryWarning 科室库存预警设置
     * @return 结果
     */
    @Override
    public int updateDepInventoryWarning(DepInventoryWarning depInventoryWarning)
    {
        depInventoryWarning.setUpdateTime(DateUtils.getNowDate());
        return depInventoryWarningMapper.updateDepInventoryWarning(depInventoryWarning);
    }

    /**
     * 批量删除科室库存预警设置
     *
     * @param ids 需要删除的科室库存预警设置主键
     * @return 结果
     */
    @Override
    public int deleteDepInventoryWarningByIds(Long[] ids)
    {
        return depInventoryWarningMapper.deleteDepInventoryWarningByIds(ids);
    }

    /**
     * 删除科室库存预警设置信息
     *
     * @param id 科室库存预警设置主键
     * @return 结果
     */
    @Override
    public int deleteDepInventoryWarningById(Long id)
    {
        return depInventoryWarningMapper.deleteDepInventoryWarningById(id);
    }
}


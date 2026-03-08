package com.spd.department.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.department.mapper.DepInventoryWarningMapper;
import com.spd.department.domain.DepInventoryWarning;
import com.spd.department.service.IDepInventoryWarningService;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;

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
        DepInventoryWarning w = depInventoryWarningMapper.selectDepInventoryWarningById(id);
        if (w != null) {
            SecurityUtils.ensureTenantAccess(w.getTenantId());
        }
        return w;
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
        if (depInventoryWarning != null && StringUtils.isEmpty(depInventoryWarning.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            depInventoryWarning.setTenantId(SecurityUtils.getCustomerId());
        }
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
        if (StringUtils.isEmpty(depInventoryWarning.getCreateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            depInventoryWarning.setCreateBy(SecurityUtils.getUserIdStr());
        }
        if (StringUtils.isEmpty(depInventoryWarning.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            depInventoryWarning.setTenantId(SecurityUtils.getCustomerId());
        }
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
        if (StringUtils.isEmpty(depInventoryWarning.getUpdateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            depInventoryWarning.setUpdateBy(SecurityUtils.getUserIdStr());
        }
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
        for (Long id : ids) {
            DepInventoryWarning existing = depInventoryWarningMapper.selectDepInventoryWarningById(id);
            if (existing != null) {
                SecurityUtils.ensureTenantAccess(existing.getTenantId());
            }
        }
        return depInventoryWarningMapper.deleteDepInventoryWarningByIds(ids, SecurityUtils.getUserIdStr());
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
        DepInventoryWarning existing = depInventoryWarningMapper.selectDepInventoryWarningById(id);
        if (existing != null) {
            SecurityUtils.ensureTenantAccess(existing.getTenantId());
        }
        return depInventoryWarningMapper.deleteDepInventoryWarningById(id, SecurityUtils.getUserIdStr());
    }
}


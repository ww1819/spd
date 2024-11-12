package com.spd.foundation.service.impl;

import java.util.Date;
import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.foundation.mapper.FdUnitMapper;
import com.spd.foundation.domain.FdUnit;
import com.spd.foundation.service.IFdUnitService;

/**
 * 单位明细Service业务层处理
 *
 * @author spd
 * @date 2024-04-07
 */
@Service
public class FdUnitServiceImpl implements IFdUnitService
{
    @Autowired
    private FdUnitMapper fdUnitMapper;

    /**
     * 查询单位明细
     *
     * @param unitId 单位明细主键
     * @return 单位明细
     */
    @Override
    public FdUnit selectFdUnitByUnitId(Long unitId)
    {
        return fdUnitMapper.selectFdUnitByUnitId(unitId);
    }

    /**
     * 查询单位明细列表
     *
     * @param fdUnit 单位明细
     * @return 单位明细
     */
    @Override
    public List<FdUnit> selectFdUnitList(FdUnit fdUnit)
    {
        return fdUnitMapper.selectFdUnitList(fdUnit);
    }

    /**
     * 新增单位明细
     *
     * @param fdUnit 单位明细
     * @return 结果
     */
    @Override
    public int insertFdUnit(FdUnit fdUnit)
    {
        fdUnit.setCreateTime(DateUtils.getNowDate());
        return fdUnitMapper.insertFdUnit(fdUnit);
    }

    /**
     * 修改单位明细
     *
     * @param fdUnit 单位明细
     * @return 结果
     */
    @Override
    public int updateFdUnit(FdUnit fdUnit)
    {
        fdUnit.setUpdateTime(DateUtils.getNowDate());
        return fdUnitMapper.updateFdUnit(fdUnit);
    }

    /**
     * 批量删除单位明细
     *
     * @param unitIds 需要删除的单位明细主键
     * @return 结果
     */
    @Override
    public int deleteFdUnitByUnitIds(Long unitIds)
    {
        FdUnit fdUnit = fdUnitMapper.selectFdUnitByUnitId(unitIds);
        if(fdUnit == null){
            throw new ServiceException(String.format("单位明细：%s，不存在!", unitIds));
        }
        fdUnit.setDelFlag(1);
        fdUnit.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        fdUnit.setUpdateTime(new Date());
        return fdUnitMapper.updateFdUnit(fdUnit);
    }

//    /**
//     * 删除单位明细信息
//     *
//     * @param unitId 单位明细主键
//     * @return 结果
//     */
//    @Override
//    public int deleteFdUnitByUnitId(Long unitId)
//    {
//        return fdUnitMapper.deleteFdUnitByUnitId(unitId);
//    }
}

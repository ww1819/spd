package com.spd.foundation.service.impl;

import java.util.Date;
import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.foundation.mapper.FdDepartmentMapper;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.service.IFdDepartmentService;

/**
 * 科室Service业务层处理
 *
 * @author spd
 * @date 2023-11-26
 */
@Service
public class FdDepartmentServiceImpl implements IFdDepartmentService
{
    @Autowired
    private FdDepartmentMapper fdDepartmentMapper;

    /**
     * 查询科室
     *
     * @param id 科室主键
     * @return 科室
     */
    @Override
    public FdDepartment selectFdDepartmentById(String id)
    {
        return fdDepartmentMapper.selectFdDepartmentById(id);
    }

    /**
     * 查询科室列表
     *
     * @param fdDepartment 科室
     * @return 科室
     */
    @Override
    public List<FdDepartment> selectFdDepartmentList(FdDepartment fdDepartment)
    {
        return fdDepartmentMapper.selectFdDepartmentList(fdDepartment);
    }

    /**
     * 新增科室
     *
     * @param fdDepartment 科室
     * @return 结果
     */
    @Override
    public int insertFdDepartment(FdDepartment fdDepartment)
    {
        fdDepartment.setCreateTime(DateUtils.getNowDate());
        return fdDepartmentMapper.insertFdDepartment(fdDepartment);
    }

    /**
     * 修改科室
     *
     * @param fdDepartment 科室
     * @return 结果
     */
    @Override
    public int updateFdDepartment(FdDepartment fdDepartment)
    {
        fdDepartment.setUpdateTime(DateUtils.getNowDate());
        return fdDepartmentMapper.updateFdDepartment(fdDepartment);
    }

    /**
     * 删除科室信息
     *
     * @param id 科室主键
     * @return 结果
     */
    @Override
    public int deleteFdDepartmentById(String id)
    {
        FdDepartment fdDepartment = fdDepartmentMapper.selectFdDepartmentById(id);
        if(fdDepartment == null){
            throw new ServiceException(String.format("科室：%s，不存在!", id));
        }
        fdDepartment.setDelFlag(1);
        fdDepartment.setUpdateTime(DateUtils.getNowDate());
        fdDepartment.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        return fdDepartmentMapper.updateFdDepartment(fdDepartment);
    }

    @Override
    public List<FdDepartment> selectdepartmenAll() {
        return fdDepartmentMapper.selectdepartmenAll();
    }

    @Override
    public List<Long> selectDepartmenListByUserId(Long userId) {
        return fdDepartmentMapper.selectDepartmenListByUserId(userId);
    }

    @Override
    public List<FdDepartment> selectUserDepartmenAll(Long userId) {
        return fdDepartmentMapper.selectUserDepartmenAll(userId);
    }

//    /**
//     * 批量删除科室
//     *
//     * @param ids 需要删除的科室主键
//     * @return 结果
//     */
//    @Override
//    public int deleteFdDepartmentByIds(String[] ids)
//    {
//        return fdDepartmentMapper.deleteFdDepartmentByIds(ids);
//    }
//
//    /**
//     * 删除科室信息
//     *
//     * @param id 科室主键
//     * @return 结果
//     */
//    @Override
//    public int deleteFdDepartmentById(String id)
//    {
//        return fdDepartmentMapper.deleteFdDepartmentById(id);
//    }
}

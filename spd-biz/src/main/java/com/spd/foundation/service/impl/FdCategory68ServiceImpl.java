package com.spd.foundation.service.impl;

import java.util.Date;
import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.foundation.mapper.FdCategory68Mapper;
import com.spd.foundation.domain.FdCategory68;
import com.spd.foundation.service.IFdCategory68Service;

/**
 * 68分类Service业务层处理
 *
 * @author spd
 */
@Service
public class FdCategory68ServiceImpl implements IFdCategory68Service
{
    @Autowired
    private FdCategory68Mapper fdCategory68Mapper;

    /**
     * 查询68分类
     *
     * @param category68Id 68分类主键
     * @return 68分类
     */
    @Override
    public FdCategory68 selectFdCategory68ByCategory68Id(Long category68Id)
    {
        return fdCategory68Mapper.selectFdCategory68ByCategory68Id(category68Id);
    }

    /**
     * 查询68分类列表
     *
     * @param fdCategory68 68分类
     * @return 68分类
     */
    @Override
    public List<FdCategory68> selectFdCategory68List(FdCategory68 fdCategory68)
    {
        return fdCategory68Mapper.selectFdCategory68List(fdCategory68);
    }

    /**
     * 查询68分类树形列表
     *
     * @return 68分类集合
     */
    @Override
    public List<FdCategory68> selectFdCategory68Tree()
    {
        return fdCategory68Mapper.selectFdCategory68Tree();
    }

    /**
     * 新增68分类
     *
     * @param fdCategory68 68分类
     * @return 结果
     */
    @Override
    public int insertFdCategory68(FdCategory68 fdCategory68)
    {
        fdCategory68.setCreateTime(DateUtils.getNowDate());
        return fdCategory68Mapper.insertFdCategory68(fdCategory68);
    }

    /**
     * 修改68分类
     *
     * @param fdCategory68 68分类
     * @return 结果
     */
    @Override
    public int updateFdCategory68(FdCategory68 fdCategory68)
    {
        fdCategory68.setUpdateTime(DateUtils.getNowDate());
        return fdCategory68Mapper.updateFdCategory68(fdCategory68);
    }

    /**
     * 批量删除68分类
     *
     * @param category68Ids 需要删除的68分类主键
     * @return 结果
     */
    @Override
    public int deleteFdCategory68ByCategory68Ids(Long category68Ids)
    {
        FdCategory68 fdCategory68 = fdCategory68Mapper.selectFdCategory68ByCategory68Id(category68Ids);
        if(fdCategory68 == null){
            throw new ServiceException(String.format("68分类：%s，不存在!", category68Ids));
        }
        fdCategory68.setDelFlag(1);
        fdCategory68.setUpdateTime(new Date());
        fdCategory68.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        return fdCategory68Mapper.updateFdCategory68(fdCategory68);
    }
}


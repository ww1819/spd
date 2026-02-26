package com.spd.foundation.service.impl;

import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.PinyinUtils;
import com.spd.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.foundation.mapper.FdFactoryMapper;
import com.spd.foundation.domain.FdFactory;
import com.spd.foundation.service.IFdFactoryService;

/**
 * 厂家维护Service业务层处理
 *
 * @author spd
 * @date 2024-03-04
 */
@Service
public class FdFactoryServiceImpl implements IFdFactoryService
{
    @Autowired
    private FdFactoryMapper fdFactoryMapper;

    /**
     * 查询厂家维护
     *
     * @param factoryId 厂家维护主键
     * @return 厂家维护
     */
    @Override
    public FdFactory selectFdFactoryByFactoryId(Long factoryId)
    {
        return fdFactoryMapper.selectFdFactoryByFactoryId(factoryId);
    }

    /**
     * 查询厂家维护列表
     *
     * @param fdFactory 厂家维护
     * @return 厂家维护
     */
    @Override
    public List<FdFactory> selectFdFactoryList(FdFactory fdFactory)
    {
        return fdFactoryMapper.selectFdFactoryList(fdFactory);
    }

    /**
     * 新增厂家维护
     *
     * @param fdFactory 厂家维护
     * @return 结果
     */
    @Override
    public int insertFdFactory(FdFactory fdFactory)
    {
        fdFactory.setCreateTime(DateUtils.getNowDate());
        return fdFactoryMapper.insertFdFactory(fdFactory);
    }

    /**
     * 修改厂家维护
     *
     * @param fdFactory 厂家维护
     * @return 结果
     */
    @Override
    public int updateFdFactory(FdFactory fdFactory)
    {
        fdFactory.setUpdateTime(DateUtils.getNowDate());
        return fdFactoryMapper.updateFdFactory(fdFactory);
    }

//    /**
//     * 批量删除厂家维护
//     *
//     * @param factoryIds 需要删除的厂家维护主键
//     * @return 结果
//     */
//    @Override
//    public int deleteFdFactoryByFactoryIds(Long[] factoryIds)
//    {
//        return fdFactoryMapper.deleteFdFactoryByFactoryIds(factoryIds);
//    }

    /**
     * 删除厂家维护信息
     *
     * @param factoryId 厂家维护主键
     * @return 结果
     */
    @Override
    public int deleteFdFactoryByFactoryId(Long factoryId)
    {
//        checkFdFactoryIsExist(factoryId);
        FdFactory fdFactory = fdFactoryMapper.selectFdFactoryByFactoryId(factoryId);
        if(fdFactory == null){
            throw new ServiceException(String.format("厂家：%s，不存在!", factoryId));
        }
        fdFactory.setDelFlag(1);
        fdFactory.setUpdateTime(DateUtils.getNowDate());
        fdFactory.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        return fdFactoryMapper.updateFdFactory(fdFactory);
    }

    /**
     * 批量更新厂家简码（factoryReferredCode）
     */
    @Override
    public void updateReferred(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            FdFactory factory = fdFactoryMapper.selectFdFactoryByFactoryId(id);
            if (factory == null) {
                continue;
            }
            String name = factory.getFactoryName();
            if (StringUtils.isEmpty(name)) {
                continue;
            }
            factory.setFactoryReferredCode(PinyinUtils.getPinyinInitials(name));
            fdFactoryMapper.updateFdFactory(factory);
        }
    }
}

package com.spd.foundation.service.impl;

import java.util.Date;
import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.foundation.mapper.FdLocationMapper;
import com.spd.foundation.domain.FdLocation;
import com.spd.foundation.service.IFdLocationService;

/**
 * 货位Service业务层处理
 *
 * @author spd
 * @date 2024-12-13
 */
@Service
public class FdLocationServiceImpl implements IFdLocationService
{
    @Autowired
    private FdLocationMapper fdLocationMapper;

    /**
     * 查询货位
     *
     * @param locationId 货位主键
     * @return 货位
     */
    @Override
    public FdLocation selectFdLocationByLocationId(Long locationId)
    {
        return fdLocationMapper.selectFdLocationByLocationId(locationId);
    }

    /**
     * 查询货位列表
     *
     * @param fdLocation 货位
     * @return 货位
     */
    @Override
    public List<FdLocation> selectFdLocationList(FdLocation fdLocation)
    {
        return fdLocationMapper.selectFdLocationList(fdLocation);
    }

    /**
     * 查询货位树形列表
     *
     * @return 货位
     */
    @Override
    public List<FdLocation> selectFdLocationTree()
    {
        return fdLocationMapper.selectFdLocationTree();
    }

    /**
     * 新增货位
     *
     * @param fdLocation 货位
     * @return 结果
     */
    @Override
    public int insertFdLocation(FdLocation fdLocation)
    {
        fdLocation.setCreateTime(new Date());
        if (fdLocation.getParentId() == null) {
            fdLocation.setParentId(0L);
        }
        if (fdLocation.getDelFlag() == null) {
            fdLocation.setDelFlag(0);
        }
        return fdLocationMapper.insertFdLocation(fdLocation);
    }

    /**
     * 修改货位
     *
     * @param fdLocation 货位
     * @return 结果
     */
    @Override
    public int updateFdLocation(FdLocation fdLocation)
    {
        fdLocation.setUpdateTime(new Date());
        return fdLocationMapper.updateFdLocation(fdLocation);
    }

    /**
     * 批量删除货位
     *
     * @param locationIds 需要删除的货位主键
     * @return 结果
     */
    @Override
    public int deleteFdLocationByLocationIds(Long locationIds)
    {
        FdLocation fdLocation = fdLocationMapper.selectFdLocationByLocationId(locationIds);
        if(fdLocation == null){
            throw new ServiceException(String.format("货位：%s，不存在!", locationIds));
        }
        fdLocation.setDelFlag(1);
        fdLocation.setUpdateTime(new Date());
        fdLocation.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        return fdLocationMapper.updateFdLocation(fdLocation);
    }
}


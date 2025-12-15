package com.spd.foundation.service;

import java.util.List;
import com.spd.foundation.domain.FdLocation;

/**
 * 货位Service接口
 *
 * @author spd
 * @date 2024-12-13
 */
public interface IFdLocationService
{
    /**
     * 查询货位
     *
     * @param locationId 货位主键
     * @return 货位
     */
    public FdLocation selectFdLocationByLocationId(Long locationId);

    /**
     * 查询货位列表
     *
     * @param fdLocation 货位
     * @return 货位集合
     */
    public List<FdLocation> selectFdLocationList(FdLocation fdLocation);

    /**
     * 查询货位树形列表
     *
     * @return 货位集合
     */
    public List<FdLocation> selectFdLocationTree();

    /**
     * 新增货位
     *
     * @param fdLocation 货位
     * @return 结果
     */
    public int insertFdLocation(FdLocation fdLocation);

    /**
     * 修改货位
     *
     * @param fdLocation 货位
     * @return 结果
     */
    public int updateFdLocation(FdLocation fdLocation);

    /**
     * 批量删除货位
     *
     * @param locationIds 需要删除的货位主键集合
     * @return 结果
     */
    public int deleteFdLocationByLocationIds(Long locationIds);
}


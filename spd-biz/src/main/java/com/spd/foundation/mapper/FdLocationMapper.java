package com.spd.foundation.mapper;

import java.util.List;
import com.spd.foundation.domain.FdLocation;

/**
 * 货位Mapper接口
 *
 * @author spd
 * @date 2024-12-13
 */
public interface FdLocationMapper
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
     * 删除货位
     *
     * @param locationId 货位主键
     * @return 结果
     */
    public int deleteFdLocationByLocationId(Long locationId);
}


package com.spd.foundation.service;

import java.util.List;
import com.spd.foundation.domain.FdFactory;

/**
 * 厂家维护Service接口
 *
 * @author spd
 * @date 2024-03-04
 */
public interface IFdFactoryService
{
    /**
     * 查询厂家维护
     *
     * @param factoryId 厂家维护主键
     * @return 厂家维护
     */
    public FdFactory selectFdFactoryByFactoryId(Long factoryId);

    /**
     * 查询厂家维护列表
     *
     * @param fdFactory 厂家维护
     * @return 厂家维护集合
     */
    public List<FdFactory> selectFdFactoryList(FdFactory fdFactory);

    /**
     * 新增厂家维护
     *
     * @param fdFactory 厂家维护
     * @return 结果
     */
    public int insertFdFactory(FdFactory fdFactory);

    /**
     * 修改厂家维护
     *
     * @param fdFactory 厂家维护
     * @return 结果
     */
    public int updateFdFactory(FdFactory fdFactory);

//    /**
//     * 批量删除厂家维护
//     *
//     * @param factoryIds 需要删除的厂家维护主键集合
//     * @return 结果
//     */
//    public int deleteFdFactoryByFactoryIds(Long[] factoryIds);

    /**
     * 删除厂家维护信息
     *
     * @param factoryId 厂家维护主键
     * @return 结果
     */
    public int deleteFdFactoryByFactoryId(Long factoryId);
}

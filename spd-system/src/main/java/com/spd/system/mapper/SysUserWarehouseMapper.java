package com.spd.system.mapper;

import com.spd.system.domain.SysUserWarehouse;

import java.util.List;

/**
 * 用户与仓库关联表 数据层
 *
 * @author spd
 */
public interface SysUserWarehouseMapper
{
    /**
     * 通过用户ID删除用户和仓库关联
     *
     * @param userId 用户ID
     * @return 结果
     */
    public int deleteUserWarehouseByUserId(Long userId);

    /**
     * 通过仓库ID查询仓库使用数量
     *
     * @param postId 仓库ID
     * @return 结果
     */
    public int countUserWarehouseById(Long postId);

    /**
     * 批量删除用户和仓库关联
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserWarehouse(Long[] ids);

    /**
     * 批量新增用户仓库信息
     *
     * @param userWarehouseList
     * @return 结果
     */
    public int batchUserWarehouse(List<SysUserWarehouse> userWarehouseList);
}

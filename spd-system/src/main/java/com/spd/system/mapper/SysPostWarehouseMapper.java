package com.spd.system.mapper;

import com.spd.system.domain.SysPostWarehouse;

import java.util.List;

/**
 * 工作组与仓库关联表 数据层
 *
 * @author spd
 */
public interface SysPostWarehouseMapper
{
    /**
     * 通过工作组ID删除工作组和仓库关联
     *
     * @param postId 工作组ID
     * @return 结果
     */
    public int deletePostWarehouseByPostId(Long postId);

    /**
     * 批量新增工作组仓库信息
     *
     * @param postWarehouseList
     * @return 结果
     */
    public int batchPostWarehouse(List<SysPostWarehouse> postWarehouseList);

    /**
     * 通过工作组ID查询仓库ID列表
     *
     * @param postId 工作组ID
     * @return 仓库ID列表
     */
    public List<Long> selectWarehouseListByPostId(Long postId);
}


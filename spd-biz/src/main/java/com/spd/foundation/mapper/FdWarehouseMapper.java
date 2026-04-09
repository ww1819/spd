package com.spd.foundation.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.spd.foundation.domain.FdWarehouse;

/**
 * 仓库Mapper接口
 *
 * @author spd
 * @date 2023-11-26
 */
public interface FdWarehouseMapper
{
    /**
     * 查询仓库
     *
     * @param id 仓库主键
     * @return 仓库
     */
    public FdWarehouse selectFdWarehouseById(String id);

    /**
     * 按主键查询仓库（不按租户过滤），调用方需自行校验租户/数据权限。
     * 解决 {@link #selectFdWarehouseById} 因当前线程租户上下文与库中 tenant_id 不一致而误查不到的问题。
     */
    FdWarehouse selectFdWarehouseByIdIgnoreTenant(@Param("id") String id);

    /**
     * 查询仓库列表
     *
     * @param fdWarehouse 仓库
     * @return 仓库集合
     */
    public List<FdWarehouse> selectFdWarehouseList(FdWarehouse fdWarehouse);

    /**
     * 租户下同名仓库数量（未删除；可排除某 id，用于唯一校验）
     */
    int countWarehouseByTenantAndName(@Param("tenantId") String tenantId, @Param("name") String name, @Param("excludeId") Long excludeId);

    /**
     * 新增仓库
     *
     * @param fdWarehouse 仓库
     * @return 结果
     */
    public int insertFdWarehouse(FdWarehouse fdWarehouse);

    /**
     * 修改仓库
     *
     * @param fdWarehouse 仓库
     * @return 结果
     */
    public int updateFdWarehouse(FdWarehouse fdWarehouse);

    /**
     * 删除仓库
     *
     * @param id 仓库主键
     * @return 结果
     */
    public int deleteFdWarehouseById(String id);

    /**
     * 批量删除仓库
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFdWarehouseByIds(String[] ids);

    /**
     * 查询所有仓库列表
     * @return
     */
    List<FdWarehouse> selectwarehouseAll();

    /**
     * 根据用户ID获取仓库列表
     * @param userId
     * @return
     */
    List<Long> selectWarehouseListByUserId(Long userId);

    /**
     * 根据用户ID查询所有仓库列表（可选按租户过滤）
     * @param userId 用户ID
     * @param tenantId 租户ID，为空则不按租户过滤
     * @return 仓库列表
     */
    List<FdWarehouse> selectUserWarehouseAll(@Param("userId") Long userId, @Param("tenantId") String tenantId);

    /**
     * 校验仓库是否已存在出入库业务
     * @param id
     * @return
     */
    int selectWarehouseIsExist(Long id);
}

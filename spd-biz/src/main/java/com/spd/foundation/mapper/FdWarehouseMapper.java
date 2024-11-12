package com.spd.foundation.mapper;

import java.util.List;
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
     * 查询仓库列表
     *
     * @param fdWarehouse 仓库
     * @return 仓库集合
     */
    public List<FdWarehouse> selectFdWarehouseList(FdWarehouse fdWarehouse);

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
     * 根据用户ID查询所有仓库列表
     * @param userId
     * @return
     */
    List<FdWarehouse> selectUserWarehouseAll(Long userId);

    /**
     * 校验仓库是否已存在出入库业务
     * @param id
     * @return
     */
    int selectWarehouseIsExist(Long id);
}

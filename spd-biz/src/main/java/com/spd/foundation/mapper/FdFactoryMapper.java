package com.spd.foundation.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.foundation.domain.FdFactory;

/**
 * 厂家维护Mapper接口
 *
 * @author spd
 * @date 2024-03-04
 */
public interface FdFactoryMapper
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
     * 租户下同名厂家数量（未删除；可排除某 factory_id，用于唯一校验）
     */
    int countFactoryByTenantAndName(@Param("tenantId") String tenantId, @Param("factoryName") String factoryName, @Param("excludeFactoryId") Long excludeFactoryId);

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

    /**
     * 逻辑删除厂家维护（设置 del_flag=1, delete_by, delete_time）
     *
     * @param factoryId 厂家维护主键
     * @param deleteBy 删除者
     * @return 结果
     */
    public int deleteFdFactoryByFactoryId(@Param("factoryId") Long factoryId, @Param("deleteBy") String deleteBy);

    /**
     * 批量逻辑删除厂家维护
     *
     * @param factoryIds 需要删除的数据主键集合
     * @param deleteBy 删除者
     * @return 结果
     */
    public int deleteFdFactoryByFactoryIds(@Param("factoryIds") Long[] factoryIds, @Param("deleteBy") String deleteBy);

    /**
     * 校验厂家是否已存在出入库业务
     * @param id
     * @return
     */
    int selectFdFactoryIsExist(Long id);

    /**
     * 按厂家编码与租户查询（未删除）
     */
    FdFactory selectFdFactoryByCodeAndTenantId(@Param("factoryCode") String factoryCode, @Param("tenantId") String tenantId);

    /**
     * 租户下 HIS 生产厂家 ID 出现次数（可排除某 factory_id）
     */
    int countFactoryByTenantAndHisId(@Param("tenantId") String tenantId, @Param("hisId") String hisId, @Param("excludeFactoryId") Long excludeFactoryId);

    /**
     * 按租户 + HIS 生产厂家 ID 查一条（未删除）
     */
    FdFactory selectFdFactoryByTenantAndHisId(@Param("tenantId") String tenantId, @Param("hisId") String hisId);
}

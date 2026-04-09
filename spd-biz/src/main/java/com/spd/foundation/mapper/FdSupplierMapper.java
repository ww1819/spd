package com.spd.foundation.mapper;

import java.util.List;
import com.spd.foundation.domain.FdSupplier;
import org.apache.ibatis.annotations.Param;

/**
 * 供应商Mapper接口
 *
 * @author spd
 * @date 2023-12-05
 */
public interface FdSupplierMapper
{
    /**
     * 查询供应商
     *
     * @param id 供应商主键
     * @return 供应商
     */
    public FdSupplier selectFdSupplierById(Long id);

    /**
     * 按编码+租户查一条（未删除）
     */
    FdSupplier selectFdSupplierByCodeAndTenantId(@Param("code") String code, @Param("tenantId") String tenantId);

    /**
     * 租户内 his_id 已存在条数（可排除某 id，用于唯一校验）
     */
    int countSupplierByTenantAndHisId(@Param("tenantId") String tenantId, @Param("hisId") String hisId, @Param("excludeId") Long excludeId);

    /**
     * 按租户 + HIS 供应商 ID 查一条（未删除）
     */
    FdSupplier selectFdSupplierByTenantAndHisId(@Param("tenantId") String tenantId, @Param("hisId") String hisId);

    /**
     * 查询供应商列表
     *
     * @param fdSupplier 供应商
     * @return 供应商集合
     */
    public List<FdSupplier> selectFdSupplierList(FdSupplier fdSupplier);

    /**
     * 租户下同名供应商数量（未删除；可排除某 id，用于唯一校验）
     */
    int countSupplierByTenantAndName(@Param("tenantId") String tenantId, @Param("name") String name, @Param("excludeId") Long excludeId);

    /**
     * 新增供应商
     *
     * @param fdSupplier 供应商
     * @return 结果
     */
    public int insertFdSupplier(FdSupplier fdSupplier);

    /**
     * 修改供应商
     *
     * @param fdSupplier 供应商
     * @return 结果
     */
    public int updateFdSupplier(FdSupplier fdSupplier);

    /** 逻辑删除（设置 del_flag、delete_by、delete_time） */
    public int deleteFdSupplierById(@Param("id") Long id, @Param("deleteBy") String deleteBy);

    /** 批量逻辑删除 */
    public int deleteFdSupplierByIds(@Param("ids") Long[] ids, @Param("deleteBy") String deleteBy);

    /**
     * 校验供应商是否已存在出入库业务
     * @param id
     * @return
     */
    int selectSupplierIsExist(Long id);
}

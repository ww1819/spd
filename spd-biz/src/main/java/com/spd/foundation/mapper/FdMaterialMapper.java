package com.spd.foundation.mapper;

import java.util.Date;
import java.util.List;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.dto.MaterialBatchUpdateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 耗材产品Mapper接口
 *
 * @author spd
 * @date 2023-12-23
 */
@Mapper
@Repository
public interface FdMaterialMapper
{
    /**
     * 查询耗材产品
     *
     * @param code 耗材产品编码
     * @return 耗材产品
     */

    public FdMaterial selectFdMaterialByCode(String code);

    /**
     * 根据第三方系统产品档案ID（his_id）查询耗材产品
     *
     * @param hisId 第三方系统产品档案ID
     * @return 耗材产品
     */
    public FdMaterial selectFdMaterialByHisId(String hisId);

    /**
     * 按租户 + HIS 产品档案 ID 查一条（未删除）
     */
    FdMaterial selectFdMaterialByTenantAndHisId(@Param("tenantId") String tenantId, @Param("hisId") String hisId);
    /**
     * 按租户 + HIS 收费项目ID（his_charge_item_id）查一条（未删除）
     */
    FdMaterial selectFdMaterialByTenantAndHisChargeItemId(@Param("tenantId") String tenantId, @Param("hisChargeItemId") String hisChargeItemId);

    /**
     * 该收费项目下是否存在 is_gz=1 的耗材（多条对照时任一条为高值即视为高值）
     */
    int countHighValueMaterialByChargeItemId(@Param("tenantId") String tenantId, @Param("hisChargeItemId") String hisChargeItemId);

    /**
     * 按租户 + 耗材编码查一条（未删除）
     */
    FdMaterial selectFdMaterialByTenantAndCode(@Param("tenantId") String tenantId, @Param("code") String code);

    /**
     * 根据主条码或耗材编码查询耗材产品（用于扫码匹配产品档案）
     *
     * @param mainBarcode 主条码或耗材编码
     * @return 耗材产品
     */
    public FdMaterial selectFdMaterialByMainBarcode(String mainBarcode);

    /**
     * 查询耗材产品
     *
     * @param id 耗材产品主键
     * @return 耗材产品
     */
    public FdMaterial selectFdMaterialById(Long id);

    /**
     * 批量查询产品档案（与 {@link #selectFdMaterialById} 相同字段与关联，用于避免 N+1）
     */
    List<FdMaterial> selectFdMaterialByIds(@Param("ids") List<Long> ids);

    /**
     * 按主键 + 租户查产品档案（与单据 tenant 一致，避免仅用 getCustomerId() 导致查不到）
     */
    FdMaterial selectFdMaterialByIdAndTenant(@Param("id") Long id, @Param("tenantId") String tenantId);

    /**
     * 查询耗材产品列表
     *
     * @param fdMaterial 耗材产品
     * @return 耗材产品集合
     */
    public List<FdMaterial> selectFdMaterialList(FdMaterial fdMaterial);

    /**
     * 新增耗材产品
     *
     * @param fdMaterial 耗材产品
     * @return 结果
     */
    public int insertFdMaterial(FdMaterial fdMaterial);

    /**
     * 修改耗材产品
     *
     * @param fdMaterial 耗材产品
     * @return 结果
     */
    public int updateFdMaterial(FdMaterial fdMaterial);

    /** 解绑 HIS 收费项目：仅清空 his_charge_item_id */
    int clearHisChargeItemIdByMaterialId(@Param("id") Long id, @Param("updateBy") String updateBy);

    /** 逻辑删除（设置 del_flag、delete_by、delete_time） */
    public int deleteFdMaterialById(@Param("id") Long id, @Param("deleteBy") String deleteBy);

    /** 批量逻辑删除 */
    public int deleteFdMaterialByIds(@Param("ids") Long[] ids, @Param("deleteBy") String deleteBy);

    /** 按列表相同条件仅查主键（批量修改「更新全部」用，无多表关联） */
    List<Long> selectFdMaterialIdList(FdMaterial fdMaterial);

    /** 批量按 ID 补丁更新（单条 SQL，ids 建议 ≤500） */
    int batchPatchFdMaterialByIds(@Param("patch") MaterialBatchUpdateDto patch,
                                  @Param("ids") List<Long> ids,
                                  @Param("updateBy") String updateBy,
                                  @Param("updateTime") Date updateTime);

    /** 查询 ids 中使用状态与目标值不一致的主键（批量写启用停用流水前） */
    List<Long> selectIdsForIsUseChangeByIds(@Param("ids") List<Long> ids,
                                            @Param("newIsUse") String newIsUse);
}

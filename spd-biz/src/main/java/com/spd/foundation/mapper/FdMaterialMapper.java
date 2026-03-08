package com.spd.foundation.mapper;

import java.util.List;
import com.spd.foundation.domain.FdMaterial;
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
    public FdMaterial selectFdMaterialById(String id);

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

    /** 逻辑删除（设置 del_flag、delete_by、delete_time） */
    public int deleteFdMaterialById(@Param("id") Long id, @Param("deleteBy") String deleteBy);

    /** 批量逻辑删除 */
    public int deleteFdMaterialByIds(@Param("ids") Long[] ids, @Param("deleteBy") String deleteBy);
}

package com.spd.foundation.service;

import java.util.List;
import com.spd.foundation.domain.FdMaterial;

/**
 * 耗材产品Service接口
 *
 * @author spd
 * @date 2023-12-23
 */
public interface IFdMaterialService
{
    /**
     * 查询耗材产品
     *
     * @param id 耗材产品主键
     * @return 耗材产品
     */
    public FdMaterial selectFdMaterialById(Long id);

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

    /**
     * 批量删除耗材产品
     *
     * @param ids 需要删除的耗材产品主键集合
     * @return 结果
     */
    public int deleteFdMaterialByIds(Long ids);

//    /**
//     * 批量删除耗材产品
//     *
//     * @param ids 需要删除的耗材产品主键集合
//     * @return 结果
//     */
//    public int deleteFdMaterialByIds(Long[] ids);

//    /**
//     * 删除耗材产品信息
//     *
//     * @param id 耗材产品主键
//     * @return 结果
//     */
//    public int deleteFdMaterialById(Long id);
}

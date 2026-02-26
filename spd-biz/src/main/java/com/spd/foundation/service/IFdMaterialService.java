package com.spd.foundation.service;

import java.util.List;

import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdMaterialChangeLog;
import com.spd.foundation.domain.FdMaterialStatusLog;
import com.spd.foundation.vo.MaterialTimelineVo;

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



    /**
     * 导入耗材档案数据
     *
     * @param fdmaterialList 耗材档案数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作耗材档案
     * @return 结果
     */
    public String importFdMaterial(List<FdMaterial> fdmaterialList, Boolean isUpdateSupport, String operName);

    /**
     * 批量更新耗材产品名称简码
     *
     * @param ids 耗材ID列表
     */
    void updateReferred(List<Long> ids);

    /**
     * 产品档案停用：更新为停用并记录停用时间、停用人、停用原因
     *
     * @param materialId 产品档案ID
     * @param reason     停用原因
     */
    void disableMaterial(Long materialId, String reason);

    /**
     * 产品档案启用：更新为启用并记录启用时间、启用人、启用原因
     *
     * @param materialId 产品档案ID
     * @param reason     启用原因
     */
    void enableMaterial(Long materialId, String reason);

    /**
     * 查询产品档案启用停用记录列表（按时间倒序）
     *
     * @param materialId 产品档案ID
     * @return 启用停用记录列表
     */
    List<FdMaterialStatusLog> listStatusLogByMaterialId(Long materialId);

    /**
     * 查询产品档案变更记录列表（按时间倒序）
     *
     * @param materialId 产品档案ID
     * @return 变更记录列表
     */
    List<FdMaterialChangeLog> listChangeLogByMaterialId(Long materialId);

    /**
     * 查询产品档案时间轴（合并启用停用记录与变更记录，按变更时间倒序）
     *
     * @param materialId 产品档案ID
     * @return 时间轴列表
     */
    List<MaterialTimelineVo> getMaterialTimeline(Long materialId);


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

package com.spd.biz.mapper;

import java.util.List;
import com.spd.biz.domain.EquipmentPurchaseApplication;

/**
 * 设备采购申请Mapper接口
 * 
 * @author spd
 * @date 2024-01-15
 */
public interface EquipmentPurchaseApplicationMapper 
{
    /**
     * 查询设备采购申请
     * 
     * @param id 设备采购申请主键
     * @return 设备采购申请
     */
    public EquipmentPurchaseApplication selectEquipmentPurchaseApplicationById(String id);

    /**
     * 查询设备采购申请列表
     * 
     * @param equipmentPurchaseApplication 设备采购申请
     * @return 设备采购申请集合
     */
    public List<EquipmentPurchaseApplication> selectEquipmentPurchaseApplicationList(EquipmentPurchaseApplication equipmentPurchaseApplication);

    /**
     * 新增设备采购申请
     * 
     * @param equipmentPurchaseApplication 设备采购申请
     * @return 结果
     */
    public int insertEquipmentPurchaseApplication(EquipmentPurchaseApplication equipmentPurchaseApplication);

    /**
     * 修改设备采购申请
     * 
     * @param equipmentPurchaseApplication 设备采购申请
     * @return 结果
     */
    public int updateEquipmentPurchaseApplication(EquipmentPurchaseApplication equipmentPurchaseApplication);

    /**
     * 删除设备采购申请
     * 
     * @param id 设备采购申请主键
     * @return 结果
     */
    public int deleteEquipmentPurchaseApplicationById(String id);

    /**
     * 批量删除设备采购申请
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEquipmentPurchaseApplicationByIds(String[] ids);
} 
package com.spd.biz.service.impl;

import java.util.List;
import java.util.Date;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.biz.mapper.EquipmentPurchaseApplicationMapper;
import com.spd.biz.domain.EquipmentPurchaseApplication;
import com.spd.biz.service.IEquipmentPurchaseApplicationService;
import com.spd.common.utils.uuid.UUID7;

/**
 * 设备采购申请Service业务层处理
 * 
 * @author spd
 * @date 2024-01-15
 */
@Service
public class EquipmentPurchaseApplicationServiceImpl implements IEquipmentPurchaseApplicationService 
{
    @Autowired
    private EquipmentPurchaseApplicationMapper equipmentPurchaseApplicationMapper;

    /**
     * 查询设备采购申请
     * 
     * @param id 设备采购申请主键
     * @return 设备采购申请
     */
    @Override
    public EquipmentPurchaseApplication selectEquipmentPurchaseApplicationById(String id)
    {
        return equipmentPurchaseApplicationMapper.selectEquipmentPurchaseApplicationById(id);
    }

    /**
     * 查询设备采购申请列表
     * 
     * @param equipmentPurchaseApplication 设备采购申请
     * @return 设备采购申请
     */
    @Override
    public List<EquipmentPurchaseApplication> selectEquipmentPurchaseApplicationList(EquipmentPurchaseApplication equipmentPurchaseApplication)
    {
        return equipmentPurchaseApplicationMapper.selectEquipmentPurchaseApplicationList(equipmentPurchaseApplication);
    }

    /**
     * 新增设备采购申请
     * 
     * @param equipmentPurchaseApplication 设备采购申请
     * @return 结果
     */
    @Override
    public int insertEquipmentPurchaseApplication(EquipmentPurchaseApplication equipmentPurchaseApplication)
    {
        // 生成UUID作为主键
        equipmentPurchaseApplication.setId(UUID7.generateUUID7());
        
        // 生成申请单号
        if (equipmentPurchaseApplication.getApplicationNo() == null || equipmentPurchaseApplication.getApplicationNo().isEmpty()) {
            String applicationNo = "CG" + System.currentTimeMillis();
            equipmentPurchaseApplication.setApplicationNo(applicationNo);
        }
        
        // 设置申请日期
        if (equipmentPurchaseApplication.getApplicationDate() == null) {
            equipmentPurchaseApplication.setApplicationDate(new Date());
        }
        
        // 设置默认状态为待审核
        if (equipmentPurchaseApplication.getStatus() == null) {
            equipmentPurchaseApplication.setStatus("0");
        }
        
        // 计算总金额
        if (equipmentPurchaseApplication.getQuantity() != null && equipmentPurchaseApplication.getUnitPrice() != null) {
            BigDecimal totalAmount = equipmentPurchaseApplication.getUnitPrice().multiply(new BigDecimal(equipmentPurchaseApplication.getQuantity()));
            equipmentPurchaseApplication.setTotalAmount(totalAmount);
        }
        
        return equipmentPurchaseApplicationMapper.insertEquipmentPurchaseApplication(equipmentPurchaseApplication);
    }

    /**
     * 修改设备采购申请
     * 
     * @param equipmentPurchaseApplication 设备采购申请
     * @return 结果
     */
    @Override
    public int updateEquipmentPurchaseApplication(EquipmentPurchaseApplication equipmentPurchaseApplication)
    {
        // 计算总金额
        if (equipmentPurchaseApplication.getQuantity() != null && equipmentPurchaseApplication.getUnitPrice() != null) {
            BigDecimal totalAmount = equipmentPurchaseApplication.getUnitPrice().multiply(new BigDecimal(equipmentPurchaseApplication.getQuantity()));
            equipmentPurchaseApplication.setTotalAmount(totalAmount);
        }
        
        return equipmentPurchaseApplicationMapper.updateEquipmentPurchaseApplication(equipmentPurchaseApplication);
    }

    /**
     * 批量删除设备采购申请
     * 
     * @param ids 需要删除的设备采购申请主键
     * @return 结果
     */
    @Override
    public int deleteEquipmentPurchaseApplicationByIds(String[] ids)
    {
        return equipmentPurchaseApplicationMapper.deleteEquipmentPurchaseApplicationByIds(ids);
    }

    /**
     * 删除设备采购申请信息
     * 
     * @param id 设备采购申请主键
     * @return 结果
     */
    @Override
    public int deleteEquipmentPurchaseApplicationById(String id)
    {
        return equipmentPurchaseApplicationMapper.deleteEquipmentPurchaseApplicationById(id);
    }
} 
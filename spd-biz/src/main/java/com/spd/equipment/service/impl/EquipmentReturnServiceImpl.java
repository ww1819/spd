package com.spd.equipment.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spd.equipment.mapper.EquipmentReturnMapper;
import com.spd.equipment.mapper.EquipmentReturnDetailMapper;
import com.spd.equipment.domain.EquipmentReturn;
import com.spd.equipment.domain.EquipmentReturnDetail;
import com.spd.equipment.domain.EquipmentInfo;
import com.spd.equipment.service.IEquipmentReturnService;
import com.spd.equipment.service.IEquipmentInfoService;
import com.spd.foundation.service.IFdWarehouseService;
import com.spd.foundation.domain.FdWarehouse;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 设备退货Service业务层处理
 * 
 * @author spd
 * @date 2024-01-01
 */
@Service
public class EquipmentReturnServiceImpl implements IEquipmentReturnService 
{
    private static final Logger log = LoggerFactory.getLogger(EquipmentReturnServiceImpl.class);
    
    @Autowired
    private EquipmentReturnMapper equipmentReturnMapper;

    @Autowired
    private EquipmentReturnDetailMapper equipmentReturnDetailMapper;

    @Autowired
    private IEquipmentInfoService equipmentInfoService;

    @Autowired
    private IFdWarehouseService fdWarehouseService;

    /**
     * 查询设备退货
     * 
     * @param returnId 设备退货主键
     * @return 设备退货
     */
    @Override
    public EquipmentReturn selectEquipmentReturnById(Long returnId)
    {
        EquipmentReturn equipmentReturn = equipmentReturnMapper.selectEquipmentReturnById(returnId);
        if (equipmentReturn != null) {
            // 加载明细列表
            List<EquipmentReturnDetail> detailList = equipmentReturnDetailMapper.selectEquipmentReturnDetailList(returnId);
            equipmentReturn.setDetailList(detailList);
        }
        return equipmentReturn;
    }

    /**
     * 查询设备退货列表
     * 
     * @param equipmentReturn 设备退货
     * @return 设备退货
     */
    @Override
    public List<EquipmentReturn> selectEquipmentReturnList(EquipmentReturn equipmentReturn)
    {
        return equipmentReturnMapper.selectEquipmentReturnList(equipmentReturn);
    }

    /**
     * 新增设备退货
     * 
     * @param equipmentReturn 设备退货
     * @return 结果
     */
    @Override
    @Transactional
    public int insertEquipmentReturn(EquipmentReturn equipmentReturn)
    {
        equipmentReturn.setCreateTime(DateUtils.getNowDate());
        equipmentReturn.setCreateBy(SecurityUtils.getUsername());
        int result = equipmentReturnMapper.insertEquipmentReturn(equipmentReturn);
        
        // 保存明细列表
        if (equipmentReturn.getDetailList() != null && !equipmentReturn.getDetailList().isEmpty()) {
            for (EquipmentReturnDetail detail : equipmentReturn.getDetailList()) {
                detail.setReturnId(equipmentReturn.getReturnId());
            }
            equipmentReturnDetailMapper.batchInsertEquipmentReturnDetail(equipmentReturn.getDetailList());
        }
        
        return result;
    }

    /**
     * 修改设备退货
     * 
     * @param equipmentReturn 设备退货
     * @return 结果
     */
    @Override
    @Transactional
    public int updateEquipmentReturn(EquipmentReturn equipmentReturn)
    {
        equipmentReturn.setUpdateTime(DateUtils.getNowDate());
        equipmentReturn.setUpdateBy(SecurityUtils.getUsername());
        
        // 先删除原有明细
        equipmentReturnDetailMapper.deleteEquipmentReturnDetailByReturnId(equipmentReturn.getReturnId());
        
        // 保存新的明细列表
        if (equipmentReturn.getDetailList() != null && !equipmentReturn.getDetailList().isEmpty()) {
            for (EquipmentReturnDetail detail : equipmentReturn.getDetailList()) {
                detail.setReturnId(equipmentReturn.getReturnId());
            }
            equipmentReturnDetailMapper.batchInsertEquipmentReturnDetail(equipmentReturn.getDetailList());
        }
        
        return equipmentReturnMapper.updateEquipmentReturn(equipmentReturn);
    }

    /**
     * 批量删除设备退货
     * 
     * @param returnIds 需要删除的设备退货主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteEquipmentReturnByIds(Long[] returnIds)
    {
        // 先删除明细
        equipmentReturnDetailMapper.deleteEquipmentReturnDetailByReturnIds(returnIds);
        // 再删除主表
        return equipmentReturnMapper.deleteEquipmentReturnByIds(returnIds);
    }

    /**
     * 删除设备退货信息
     * 
     * @param returnId 设备退货主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteEquipmentReturnById(Long returnId)
    {
        // 先删除明细
        equipmentReturnDetailMapper.deleteEquipmentReturnDetailByReturnId(returnId);
        // 再删除主表
        return equipmentReturnMapper.deleteEquipmentReturnById(returnId);
    }

    /**
     * 审核设备退货
     * 
     * @param returnId 设备退货主键
     * @return 结果
     */
    @Override
    @Transactional
    public int auditEquipmentReturn(Long returnId)
    {
        EquipmentReturn equipmentReturn = equipmentReturnMapper.selectEquipmentReturnById(returnId);
        if (equipmentReturn == null) {
            throw new ServiceException(String.format("设备退货ID：%s，不存在!", returnId));
        }
        
        // 检查状态是否为未审核
        if (!"0".equals(equipmentReturn.getReturnStatus())) {
            throw new ServiceException(String.format("设备退货ID：%s，状态不正确，只能审核未审核状态的退货单!", returnId));
        }
        
        // 获取仓库名称（用于删除或更新设备信息记录）
        String warehouseName = equipmentReturn.getWarehouseName();
        if (StringUtils.isEmpty(warehouseName) && equipmentReturn.getWarehouseId() != null) {
            FdWarehouse warehouse = fdWarehouseService.selectFdWarehouseById(String.valueOf(equipmentReturn.getWarehouseId()));
            if (warehouse != null) {
                warehouseName = warehouse.getName();
            }
        }
        
        // 根据退货明细删除或更新设备信息记录（审核时才处理，减少仓库库存）
        List<EquipmentReturnDetail> detailList = equipmentReturnDetailMapper.selectEquipmentReturnDetailList(returnId);
        if (detailList != null && !detailList.isEmpty()) {
            for (EquipmentReturnDetail detail : detailList) {
                // 根据数量循环处理多条设备信息记录
                int quantity = detail.getQuantity() != null ? detail.getQuantity() : 1;
                String baseAssetCode = detail.getEquipmentCode();
                
                if (StringUtils.isEmpty(baseAssetCode)) {
                    log.warn("退货明细缺少档案编码，跳过处理设备信息记录");
                    continue;
                }
                
                for (int i = 0; i < quantity; i++) {
                    // 为每个数量生成唯一的档案编码（如果数量大于1，在原有编码后面加序号）
                    String assetCode = quantity > 1 ? baseAssetCode + "-" + (i + 1) : baseAssetCode;
                    
                    // 检查是否存在该档案编码的设备信息记录
                    EquipmentInfo existInfo = equipmentInfoService.selectEquipmentInfoByAssetCode(assetCode);
                    if (existInfo != null) {
                        // 检查该设备是否在指定仓库中
                        if (StringUtils.isNotEmpty(existInfo.getHospitalCode()) && 
                            warehouseName.equals(existInfo.getHospitalCode())) {
                            // 在指定仓库中，删除该设备信息记录（减少仓库库存）
                            equipmentInfoService.deleteEquipmentInfoById(existInfo.getId());
                            log.info("审核退货单时删除设备信息记录，档案编码：{}", assetCode);
                        } else {
                            // 不在指定仓库中，记录警告日志
                            log.warn("审核退货单时，设备信息记录的仓库不匹配，档案编码：{}，设备仓库：{}，退货仓库：{}", 
                                    assetCode, existInfo.getHospitalCode(), warehouseName);
                        }
                    } else {
                        // 不存在该设备信息记录，记录警告日志
                        log.warn("审核退货单时，未找到对应的设备信息记录，档案编码：{}", assetCode);
                    }
                }
            }
        }
        
        equipmentReturn.setReturnStatus("2"); // 已审核状态
        equipmentReturn.setAuditorId(SecurityUtils.getUserId()); // 审核人ID
        equipmentReturn.setAuditDate(DateUtils.getNowDate()); // 审核日期
        equipmentReturn.setUpdateBy(SecurityUtils.getUsername());
        equipmentReturn.setUpdateTime(DateUtils.getNowDate());
        
        return equipmentReturnMapper.auditEquipmentReturn(equipmentReturn);
    }
}

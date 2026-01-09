package com.spd.equipment.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spd.equipment.mapper.EquipmentStorageMapper;
import com.spd.equipment.mapper.EquipmentStorageDetailMapper;
import com.spd.equipment.domain.EquipmentStorage;
import com.spd.equipment.domain.EquipmentStorageDetail;
import com.spd.equipment.domain.EquipmentInfo;
import com.spd.equipment.service.IEquipmentStorageService;
import com.spd.equipment.service.IEquipmentInfoService;
import com.spd.foundation.service.IFdWarehouseService;
import com.spd.foundation.domain.FdWarehouse;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.common.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 设备入库Service业务层处理
 * 
 * @author spd
 * @date 2024-01-01
 */
@Service
public class EquipmentStorageServiceImpl implements IEquipmentStorageService 
{
    private static final Logger log = LoggerFactory.getLogger(EquipmentStorageServiceImpl.class);
    
    @Autowired
    private EquipmentStorageMapper equipmentStorageMapper;

    @Autowired
    private EquipmentStorageDetailMapper equipmentStorageDetailMapper;

    @Autowired
    private IEquipmentInfoService equipmentInfoService;

    @Autowired
    private IFdWarehouseService fdWarehouseService;

    /**
     * 查询设备入库
     * 
     * @param storageId 设备入库主键
     * @return 设备入库
     */
    @Override
    public EquipmentStorage selectEquipmentStorageById(Long storageId)
    {
        EquipmentStorage storage = equipmentStorageMapper.selectEquipmentStorageById(storageId);
        if (storage != null) {
            // 加载明细列表
            List<EquipmentStorageDetail> detailList = equipmentStorageDetailMapper.selectEquipmentStorageDetailList(storageId);
            storage.setDetailList(detailList);
        }
        return storage;
    }

    /**
     * 查询设备入库列表
     * 
     * @param equipmentStorage 设备入库
     * @return 设备入库
     */
    @Override
    public List<EquipmentStorage> selectEquipmentStorageList(EquipmentStorage equipmentStorage)
    {
        return equipmentStorageMapper.selectEquipmentStorageList(equipmentStorage);
    }

    /**
     * 新增设备入库
     * 
     * @param equipmentStorage 设备入库
     * @return 结果
     */
    @Override
    @Transactional
    public int insertEquipmentStorage(EquipmentStorage equipmentStorage)
    {
        equipmentStorage.setCreateTime(DateUtils.getNowDate());
        equipmentStorage.setCreateBy(SecurityUtils.getUsername());
        int result = equipmentStorageMapper.insertEquipmentStorage(equipmentStorage);
        
        // 保存明细列表
        if (equipmentStorage.getDetailList() != null && !equipmentStorage.getDetailList().isEmpty()) {
            for (EquipmentStorageDetail detail : equipmentStorage.getDetailList()) {
                detail.setStorageId(equipmentStorage.getStorageId());
            }
            equipmentStorageDetailMapper.batchInsertEquipmentStorageDetail(equipmentStorage.getDetailList());
        }
        
        return result;
    }

    /**
     * 修改设备入库
     * 
     * @param equipmentStorage 设备入库
     * @return 结果
     */
    @Override
    @Transactional
    public int updateEquipmentStorage(EquipmentStorage equipmentStorage)
    {
        equipmentStorage.setUpdateTime(DateUtils.getNowDate());
        equipmentStorage.setUpdateBy(SecurityUtils.getUsername());
        
        // 先删除原有明细
        equipmentStorageDetailMapper.deleteEquipmentStorageDetailByStorageId(equipmentStorage.getStorageId());
        
        // 保存新的明细列表
        if (equipmentStorage.getDetailList() != null && !equipmentStorage.getDetailList().isEmpty()) {
            for (EquipmentStorageDetail detail : equipmentStorage.getDetailList()) {
                detail.setStorageId(equipmentStorage.getStorageId());
            }
            equipmentStorageDetailMapper.batchInsertEquipmentStorageDetail(equipmentStorage.getDetailList());
        }
        
        return equipmentStorageMapper.updateEquipmentStorage(equipmentStorage);
    }

    /**
     * 批量删除设备入库
     * 
     * @param storageIds 需要删除的设备入库主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteEquipmentStorageByIds(Long[] storageIds)
    {
        // 先删除明细
        equipmentStorageDetailMapper.deleteEquipmentStorageDetailByStorageIds(storageIds);
        // 再删除主表
        return equipmentStorageMapper.deleteEquipmentStorageByIds(storageIds);
    }

    /**
     * 删除设备入库信息
     * 
     * @param storageId 设备入库主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteEquipmentStorageById(Long storageId)
    {
        // 先删除明细
        equipmentStorageDetailMapper.deleteEquipmentStorageDetailByStorageId(storageId);
        // 再删除主表
        return equipmentStorageMapper.deleteEquipmentStorageById(storageId);
    }

    /**
     * 审核设备入库
     * 
     * @param storageId 设备入库主键
     * @return 结果
     */
    @Override
    @Transactional
    public int auditEquipmentStorage(Long storageId)
    {
        EquipmentStorage equipmentStorage = equipmentStorageMapper.selectEquipmentStorageById(storageId);
        if (equipmentStorage == null) {
            throw new ServiceException(String.format("设备入库ID：%s，不存在!", storageId));
        }
        
        // 检查状态是否为未审核
        if (!"0".equals(equipmentStorage.getStorageStatus())) {
            throw new ServiceException(String.format("设备入库ID：%s，状态不正确，只能审核未审核状态的入库单!", storageId));
        }
        
        // 获取仓库名称（用于创建设备信息记录）
        String warehouseName = equipmentStorage.getWarehouseName();
        if (StringUtils.isEmpty(warehouseName) && equipmentStorage.getWarehouseId() != null) {
            FdWarehouse warehouse = fdWarehouseService.selectFdWarehouseById(String.valueOf(equipmentStorage.getWarehouseId()));
            if (warehouse != null) {
                warehouseName = warehouse.getName();
            }
        }
        
        // 根据入库明细创建设备信息记录（审核时才创建，增加仓库库存）
        List<EquipmentStorageDetail> detailList = equipmentStorage.getDetailList();
        if (detailList != null && !detailList.isEmpty()) {
            for (EquipmentStorageDetail detail : detailList) {
                // 根据数量循环创建多条设备信息记录
                int quantity = detail.getQuantity() != null ? detail.getQuantity() : 1;
                String baseAssetCode = detail.getEquipmentCode();
                
                if (StringUtils.isEmpty(baseAssetCode)) {
                    log.warn("入库明细缺少档案编码，跳过创建设备信息记录");
                    continue;
                }
                
                for (int i = 0; i < quantity; i++) {
                    // 为每个数量生成唯一的档案编码（如果数量大于1，在原有编码后面加序号）
                    String assetCode = quantity > 1 ? baseAssetCode + "-" + (i + 1) : baseAssetCode;
                    
                    // 检查是否已存在该档案编码的设备信息记录
                    EquipmentInfo existInfo = equipmentInfoService.selectEquipmentInfoByAssetCode(assetCode);
                    if (existInfo == null) {
                        // 不存在，创建新的设备信息记录
                        EquipmentInfo equipmentInfo = new EquipmentInfo();
                        equipmentInfo.setId(UUID7.generateUUID7Simple());
                        equipmentInfo.setAssetCode(assetCode); // 档案编码
                        equipmentInfo.setAssetName(detail.getEquipmentName()); // 档案名称
                        equipmentInfo.setHospitalCode(warehouseName); // 仓库名称（院内编码字段存储仓库）
                        // 价格：优先使用amount，其次使用totalPrice，最后使用unitPrice
                        BigDecimal price = detail.getAmount();
                        if (price == null) {
                            price = detail.getTotalPrice();
                        }
                        if (price == null && detail.getUnitPrice() != null) {
                            price = detail.getUnitPrice();
                        }
                        equipmentInfo.setBarcode(price != null ? price.toString() : "0"); // 价格
                        equipmentInfo.setSpecification(detail.getSpecification()); // 规格
                        equipmentInfo.setModel(detail.getModel()); // 型号
                        equipmentInfo.setBrand(detail.getManufacturer()); // 品牌（使用生产商）
                        equipmentInfo.setSerialNumber(detail.getSerialNo()); // 资产序列号
                        equipmentInfo.setSupplier(equipmentStorage.getSupplier()); // 供应商
                        equipmentInfo.setManufacturer(detail.getManufacturer()); // 生产厂家
                        equipmentInfo.setUnit(""); // 单位（明细中没有单位字段，暂时为空）
                        equipmentInfo.setRegistrationNumber(detail.getRegistrationNo()); // 注册证件号
                        equipmentInfo.setProductionDate(detail.getProductionDate()); // 生产日期
                        // 分类编码：从明细中获取，如果没有则从档案编码中提取
                        String categoryCode = detail.getCategoryCode();
                        if (StringUtils.isEmpty(categoryCode) && StringUtils.isNotEmpty(baseAssetCode)) {
                            // 从档案编码中提取分类编码（格式：分类编码-日期+序号）
                            int index = baseAssetCode.indexOf('-');
                            if (index > 0) {
                                categoryCode = baseAssetCode.substring(0, index);
                            }
                        }
                        equipmentInfo.setAssetType(categoryCode); // 所属分类（使用分类编码）
                        equipmentInfo.setArchiveCode(assetCode); // 档案编号
                        equipmentInfo.setAssetStatus("1"); // 资产状态：启用
                        equipmentInfo.setDelFlag("0"); // 删除标志：未删除
                        equipmentInfo.setCreateBy(SecurityUtils.getUsername());
                        equipmentInfo.setCreateTime(DateUtils.getNowDate());
                        
                        equipmentInfoService.insertEquipmentInfo(equipmentInfo);
                        log.info("审核入库单时创建设备信息记录，档案编码：{}", assetCode);
                    } else {
                        // 已存在，更新仓库信息（如果当前记录不在仓库中）
                        if (StringUtils.isEmpty(existInfo.getHospitalCode()) || 
                            !warehouseName.equals(existInfo.getHospitalCode())) {
                            existInfo.setHospitalCode(warehouseName);
                            existInfo.setUpdateBy(SecurityUtils.getUsername());
                            existInfo.setUpdateTime(DateUtils.getNowDate());
                            equipmentInfoService.updateEquipmentInfo(existInfo);
                            log.info("审核入库单时更新设备信息记录的仓库，档案编码：{}", assetCode);
                        }
                    }
                }
            }
        }
        
        equipmentStorage.setStorageStatus("2"); // 已审核状态
        equipmentStorage.setAuditorId(SecurityUtils.getUserId()); // 审核人ID
        equipmentStorage.setAuditDate(DateUtils.getNowDate()); // 审核日期
        equipmentStorage.setUpdateBy(SecurityUtils.getUsername());
        equipmentStorage.setUpdateTime(DateUtils.getNowDate());
        
        return equipmentStorageMapper.auditEquipmentStorage(equipmentStorage);
    }
}

package com.spd.gz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.gz.domain.GzDepotInventory;
import com.spd.gz.domain.GzShipmentEntry;
import com.spd.gz.mapper.GzDepotInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.spd.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.spd.gz.mapper.GzShipmentMapper;
import com.spd.gz.domain.GzShipment;
import com.spd.gz.service.IGzShipmentService;

/**
 * 高值出库Service业务层处理
 *
 * @author spd
 * @date 2024-12-08
 */
@Service
public class GzShipmentServiceImpl implements IGzShipmentService
{
    @Autowired
    private GzShipmentMapper gzShipmentMapper;

    @Autowired
    private GzDepotInventoryMapper gzDepotInventoryMapper;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    @Autowired
    private com.spd.gz.mapper.GzDepInventoryMapper gzDepInventoryMapper;

    /**
     * 查询高值出库
     *
     * @param id 高值出库主键
     * @return 高值出库
     */
    @Override
    public GzShipment selectGzShipmentById(Long id)
    {
        GzShipment gzShipment = gzShipmentMapper.selectGzShipmentById(id);
        if(gzShipment == null){
            return null;
        }

        List<GzShipmentEntry> gzShipmentEntryList = gzShipment.getGzShipmentEntryList();
        // 调试：打印查询到的院内码
        if (gzShipmentEntryList != null) {
            System.out.println("查询出库单明细，院内码信息:");
            for (GzShipmentEntry entry : gzShipmentEntryList) {
                System.out.println("  - id: " + entry.getId() + 
                    ", materialId: " + entry.getMaterialId() + 
                    ", batchNo: " + entry.getBatchNo() + 
                    ", inHospitalCode: " + entry.getInHospitalCode());
            }
        }
        List<FdMaterial> materialList = new ArrayList<FdMaterial>();
        if (gzShipmentEntryList != null) {
            for(GzShipmentEntry entry : gzShipmentEntryList){
                Long materialId = entry.getMaterialId();
                FdMaterial fdMaterial = fdMaterialMapper.selectFdMaterialById(materialId);
                materialList.add(fdMaterial);
            }
        }
        gzShipment.setMaterialList(materialList);
        return gzShipment;
    }

    /**
     * 查询高值出库列表
     *
     * @param gzShipment 高值出库
     * @return 高值出库
     */
    @Override
    public List<GzShipment> selectGzShipmentList(GzShipment gzShipment)
    {
        return gzShipmentMapper.selectGzShipmentList(gzShipment);
    }

    /**
     * 新增高值出库
     *
     * @param gzShipment 高值出库
     * @return 结果
     */
    @Transactional
    @Override
    public int insertGzShipment(GzShipment gzShipment)
    {
        gzShipment.setShipmentNo(getShipmentNo());
        gzShipment.setCreateTime(DateUtils.getNowDate());
        int rows = gzShipmentMapper.insertGzShipment(gzShipment);
        insertGzShipmentEntry(gzShipment);
        return rows;
    }

    //str：单号前缀
    //date：日期
    //result：最终结果，需要的流水号
    public String getShipmentNo() {
        String str = "GZCK";
        String date = FillRuleUtil.getDateNum();
        String maxNum = gzShipmentMapper.selectMaxBillNo(str, date);
        String result = FillRuleUtil.getNumber(str,maxNum,date);
        return result;
    }

    /**
     * 修改高值出库
     *
     * @param gzShipment 高值出库
     * @return 结果
     */
    @Transactional
    @Override
    public int updateGzShipment(GzShipment gzShipment)
    {
        gzShipment.setUpdateTime(DateUtils.getNowDate());
        gzShipmentMapper.deleteGzShipmentEntryByParenId(gzShipment.getId());
        insertGzShipmentEntry(gzShipment);
        return gzShipmentMapper.updateGzShipment(gzShipment);
    }

    /**
     * 删除高值出库信息
     *
     * @param id 高值出库主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteGzShipmentById(Long id)
    {
        GzShipment gzShipment = gzShipmentMapper.selectGzShipmentById(id);
        if(gzShipment == null){
            throw new ServiceException(String.format("高值出库业务：%s，不存在!", id));
        }

        gzShipment.setDelFlag(1);
        gzShipment.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        gzShipment.setUpdateTime(new Date());

        List<GzShipmentEntry> gzShipmentEntryList = gzShipment.getGzShipmentEntryList();
        for(GzShipmentEntry entry : gzShipmentEntryList){
            entry.setDelFlag(1);
            entry.setParenId(id);
            gzShipmentMapper.updateGzShipmentEntry(entry);
        }

        return gzShipmentMapper.updateGzShipment(gzShipment);
    }

    @Override
    @Transactional
    public int auditGzShipment(String id) {
        GzShipment gzShipment = gzShipmentMapper.selectGzShipmentById(Long.parseLong(id));
        if(gzShipment == null){
            throw new ServiceException(String.format("高值出库业务ID：%s，不存在!", id));
        }
        List<GzShipmentEntry> gzShipmentEntryList = gzShipment.getGzShipmentEntryList();

        //更新高值仓库库存（减少库存）
        updateDepotInventory(gzShipment, gzShipmentEntryList);

        //更新高值科室库存（增加库存）
        updateDepInventory(gzShipment, gzShipmentEntryList);

        gzShipment.setShipmentStatus(2);
        gzShipment.setAuditDate(new Date());
        gzShipment.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        gzShipment.setUpdateTime(new Date());
        int res = gzShipmentMapper.updateGzShipment(gzShipment);
        System.out.println("审核出库单 - id: " + id + ", shipmentStatus: " + gzShipment.getShipmentStatus() + ", 更新结果: " + res);
        return res;
    }

    private void updateDepotInventory(GzShipment gzShipment, List<GzShipmentEntry> gzShipmentEntryList){
        // 出库时减少库存，根据批次号和物料ID匹配库存记录
        for(GzShipmentEntry shipmentEntry : gzShipmentEntryList){
            if(shipmentEntry.getQty() != null && BigDecimal.ZERO.compareTo(shipmentEntry.getQty()) != 0){
                int qty = shipmentEntry.getQty().intValue();
                
                // 查询库存记录，按批次号和物料ID匹配
                GzDepotInventory queryInventory = new GzDepotInventory();
                queryInventory.setBatchNo(shipmentEntry.getBatchNo());
                queryInventory.setMaterialId(shipmentEntry.getMaterialId());
                queryInventory.setWarehouseId(gzShipment.getWarehouseId());
                
                List<GzDepotInventory> inventoryList = gzDepotInventoryMapper.selectGzDepotInventoryList(queryInventory);
                
                // 按数量减少库存，优先减少数量为1的记录
                int remainingQty = qty;
                for(GzDepotInventory inventory : inventoryList){
                    if(remainingQty <= 0){
                        break;
                    }
                    if(inventory.getQty().compareTo(BigDecimal.ONE) == 0 && remainingQty > 0){
                        // 删除数量为1的记录
                        gzDepotInventoryMapper.deleteGzDepotInventoryById(inventory.getId());
                        remainingQty--;
                    }
                }
                
                // 如果还有剩余数量，减少其他记录的数量
                for(GzDepotInventory inventory : inventoryList){
                    if(remainingQty <= 0){
                        break;
                    }
                    if(inventory.getQty().compareTo(BigDecimal.ONE) > 0){
                        BigDecimal reduceQty = BigDecimal.valueOf(Math.min(remainingQty, inventory.getQty().intValue()));
                        inventory.setQty(inventory.getQty().subtract(reduceQty));
                        if(inventory.getQty().compareTo(BigDecimal.ZERO) <= 0){
                            gzDepotInventoryMapper.deleteGzDepotInventoryById(inventory.getId());
                        } else {
                            gzDepotInventoryMapper.updateGzDepotInventory(inventory);
                        }
                        remainingQty -= reduceQty.intValue();
                    }
                }
                
                if(remainingQty > 0){
                    throw new ServiceException(String.format("批次号 %s 的库存不足，需要出库 %d，但只有 %d 可用", 
                        shipmentEntry.getBatchNo(), qty, qty - remainingQty));
                }
            }
        }
    }

    /**
     * 更新科室库存（出库审核时增加科室库存）
     * @param gzShipment 出库单
     * @param gzShipmentEntryList 出库明细列表
     */
    private void updateDepInventory(GzShipment gzShipment, List<GzShipmentEntry> gzShipmentEntryList){
        // 每个出库明细对应一个院内码，应该创建一条独立的科室库存记录
        for(GzShipmentEntry shipmentEntry : gzShipmentEntryList){
            if(shipmentEntry.getQty() != null && BigDecimal.ZERO.compareTo(shipmentEntry.getQty()) != 0){
                // 优先使用出库明细中的院内码
                String inHospitalCode = shipmentEntry.getInHospitalCode();
                
                // 从备货库存中根据院内码精确查询库存信息（用于获取生产日期、入库日期、有效期等）
                GzDepotInventory depotInventory = null;
                if(inHospitalCode != null && !inHospitalCode.trim().isEmpty()){
                    // 根据院内码精确查询备货库存（使用 like 查询，然后在代码中精确匹配）
                    GzDepotInventory queryInventory = new GzDepotInventory();
                    queryInventory.setInHospitalCode(inHospitalCode);
                    queryInventory.setMaterialId(shipmentEntry.getMaterialId());
                    queryInventory.setWarehouseId(gzShipment.getWarehouseId());
                    List<GzDepotInventory> depotInventoryList = gzDepotInventoryMapper.selectGzDepotInventoryList(queryInventory);
                    if(depotInventoryList != null && depotInventoryList.size() > 0){
                        // 精确匹配院内码（因为查询使用的是 like，需要精确匹配）
                        for(GzDepotInventory inv : depotInventoryList){
                            if(inHospitalCode.equals(inv.getInHospitalCode())){
                                depotInventory = inv;
                                break;
                            }
                        }
                    }
                }
                
                // 如果备货库存中没找到，尝试根据批次号和物料ID查询（取第一条）
                if(depotInventory == null){
                    GzDepotInventory queryInventory = new GzDepotInventory();
                    queryInventory.setBatchNo(shipmentEntry.getBatchNo());
                    queryInventory.setMaterialId(shipmentEntry.getMaterialId());
                    queryInventory.setWarehouseId(gzShipment.getWarehouseId());
                    List<GzDepotInventory> depotInventoryList = gzDepotInventoryMapper.selectGzDepotInventoryList(queryInventory);
                    if(depotInventoryList != null && depotInventoryList.size() > 0){
                        depotInventory = depotInventoryList.get(0);
                        // 如果出库明细中没有院内码，从备货库存中获取
                        if((inHospitalCode == null || inHospitalCode.trim().isEmpty()) && depotInventory.getInHospitalCode() != null){
                            inHospitalCode = depotInventory.getInHospitalCode();
                        }
                    }
                }
                
                // 根据批次号和院内码查询科室库存（每个院内码应该是一条独立记录）
                com.spd.gz.domain.GzDepInventory queryDepInventory = new com.spd.gz.domain.GzDepInventory();
                queryDepInventory.setBatchNo(shipmentEntry.getBatchNo());
                if(inHospitalCode != null && !inHospitalCode.trim().isEmpty()){
                    queryDepInventory.setInHospitalCode(inHospitalCode);
                }
                List<com.spd.gz.domain.GzDepInventory> existingDepInventoryList = gzDepInventoryMapper.selectGzDepInventoryList(queryDepInventory);
                com.spd.gz.domain.GzDepInventory existingDepInventory = null;
                // 如果指定了院内码，查找匹配的记录
                if(inHospitalCode != null && !inHospitalCode.trim().isEmpty() && existingDepInventoryList != null){
                    for(com.spd.gz.domain.GzDepInventory depInv : existingDepInventoryList){
                        if(inHospitalCode.equals(depInv.getInHospitalCode())){
                            existingDepInventory = depInv;
                            break;
                        }
                    }
                }
                
                // 每个院内码应该是一条独立的记录，数量为1
                // 如果已存在相同院内码的记录，不应该累加，而是应该报错或跳过（因为每个院内码对应一个物品）
                if(existingDepInventory == null){
                    // 如果不存在，新增科室库存（每个院内码一条记录，数量为1）
                    com.spd.gz.domain.GzDepInventory gzDepInventory = new com.spd.gz.domain.GzDepInventory();
                    gzDepInventory.setMaterialId(shipmentEntry.getMaterialId());
                    gzDepInventory.setDepartmentId(gzShipment.getDepartmentId());
                    // 每个院内码对应一个物品，数量固定为1
                    gzDepInventory.setQty(BigDecimal.ONE);
                    gzDepInventory.setUnitPrice(shipmentEntry.getPrice());
                    // 金额 = 单价 * 1
                    gzDepInventory.setAmt(shipmentEntry.getPrice());
                    gzDepInventory.setBatchNo(shipmentEntry.getBatchNo());
                    gzDepInventory.setMaterialNo(shipmentEntry.getBatchNumber());
                    // 必须设置院内码
                    if(inHospitalCode != null && !inHospitalCode.trim().isEmpty()){
                        gzDepInventory.setInHospitalCode(inHospitalCode);
                    } else {
                        throw new ServiceException(String.format("出库明细批次号 %s 的院内码为空，无法创建科室库存记录", shipmentEntry.getBatchNo()));
                    }
                    // 从备货库存中获取生产日期、入库日期、有效期
                    if(depotInventory != null){
                        gzDepInventory.setMaterialDate(depotInventory.getMaterialDate());
                        gzDepInventory.setWarehouseDate(depotInventory.getWarehouseDate());
                        // 有效期从 gz_order_entry 的 end_time 获取
                        if(depotInventory.getEndTime() != null){
                            gzDepInventory.setEndTime(depotInventory.getEndTime());
                        }
                    } else {
                        // 如果备货库存中找不到，使用默认值
                        gzDepInventory.setMaterialDate(new Date());
                        gzDepInventory.setWarehouseDate(new Date());
                    }
                    gzDepInventoryMapper.insertGzDepInventory(gzDepInventory);
                    System.out.println("创建科室库存记录 - 院内码: " + inHospitalCode + ", 批次号: " + shipmentEntry.getBatchNo() + ", 数量: 1");
                } else {
                    // 如果已存在相同院内码的记录，不应该累加（因为每个院内码对应一个物品）
                    // 这里可能是重复审核，记录日志但不报错
                    System.out.println("科室库存记录已存在，跳过 - 院内码: " + inHospitalCode + ", 批次号: " + shipmentEntry.getBatchNo());
                }
            }
        }
    }

    /**
     * 新增高值出库明细信息
     *
     * @param gzShipment 高值出库对象
     */
    public void insertGzShipmentEntry(GzShipment gzShipment)
    {
        List<GzShipmentEntry> gzShipmentEntryList = gzShipment.getGzShipmentEntryList();
        Long id = gzShipment.getId();
        if (StringUtils.isNotNull(gzShipmentEntryList))
        {
            List<GzShipmentEntry> list = new ArrayList<GzShipmentEntry>();
            for (GzShipmentEntry gzShipmentEntry : gzShipmentEntryList)
            {
                // 调试：打印保存前的数据，特别是院内码
                System.out.println("保存出库明细 - materialId: " + gzShipmentEntry.getMaterialId() + 
                    ", qty: " + gzShipmentEntry.getQty() + 
                    ", price: " + gzShipmentEntry.getPrice() + 
                    ", batchNo: " + gzShipmentEntry.getBatchNo() + 
                    ", inHospitalCode: " + gzShipmentEntry.getInHospitalCode());
                
                gzShipmentEntry.setParenId(id);
                // 只有在 batchNo 为空时才生成新的批次号，避免覆盖已有的批次号
                if (gzShipmentEntry.getBatchNo() == null || gzShipmentEntry.getBatchNo().isEmpty()) {
                    gzShipmentEntry.setBatchNo(getBatchNumber());
                }
                gzShipmentEntry.setDelFlag(0);
                list.add(gzShipmentEntry);
            }
            if (list.size() > 0)
            {
                gzShipmentMapper.batchGzShipmentEntry(list);
            }
        }
    }

    public String getBatchNumber() {
        String str = "GZPC";
        String createNo = FillRuleUtil.createBatchNo();
        String batchNo = str + createNo;
        return batchNo;
    }
}


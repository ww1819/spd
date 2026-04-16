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
import com.spd.gz.domain.GzDepInventory;
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
import com.spd.gz.service.GzStockValidationService;
import com.spd.gz.service.GzLineRefWriteService;

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

    @Autowired
    private GzStockValidationService gzStockValidationService;

    @Autowired
    private GzLineRefWriteService gzLineRefWriteService;

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
                if (entry == null) {
                    continue;
                }
                System.out.println("  - id: " + entry.getId() + 
                    ", materialId: " + entry.getMaterialId() + 
                    ", batchNo: " + entry.getBatchNo() + 
                    ", inHospitalCode: " + entry.getInHospitalCode());
            }
        }
        List<FdMaterial> materialList = new ArrayList<FdMaterial>();
        if (gzShipmentEntryList != null) {
            for(GzShipmentEntry entry : gzShipmentEntryList){
                if (entry == null) {
                    continue;
                }
                Long materialId = entry.getMaterialId();
                if (materialId == null) {
                    continue;
                }
                FdMaterial fdMaterial = fdMaterialMapper.selectFdMaterialById(materialId);
                if (fdMaterial != null) {
                    materialList.add(fdMaterial);
                }
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
        if (gzShipment != null && StringUtils.isEmpty(gzShipment.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            gzShipment.setTenantId(SecurityUtils.getCustomerId());
        }
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
        if (gzShipment == null) {
            throw new ServiceException("高值出库单不能为空");
        }
        if (StringUtils.isEmpty(gzShipment.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            gzShipment.setTenantId(SecurityUtils.getCustomerId());
        }
        gzShipment.setCreateBy(SecurityUtils.getUserIdStr());
        gzShipment.setShipmentNo(getShipmentNo());
        gzShipment.setCreateTime(DateUtils.getNowDate());
        gzStockValidationService.assertShipmentOutbound(gzShipment, gzShipment.getGzShipmentEntryList());
        int rows = gzShipmentMapper.insertGzShipment(gzShipment);
        insertGzShipmentEntry(gzShipment);
        gzLineRefWriteService.persistOutboundRefs(gzShipment, gzShipment.getGzShipmentEntryList());
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
        if (StringUtils.isEmpty(gzShipment.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            gzShipment.setTenantId(SecurityUtils.getCustomerId());
        }
        gzShipment.setUpdateBy(SecurityUtils.getUserIdStr());
        gzShipment.setUpdateTime(DateUtils.getNowDate());
        gzStockValidationService.assertShipmentOutbound(gzShipment, gzShipment.getGzShipmentEntryList());
        gzLineRefWriteService.deleteOutboundRefs(gzShipment.getId());
        gzShipmentMapper.deleteGzShipmentEntryByParenId(gzShipment.getId(), SecurityUtils.getUserIdStr());
        insertGzShipmentEntry(gzShipment);
        gzLineRefWriteService.persistOutboundRefs(gzShipment, gzShipment.getGzShipmentEntryList());
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
        String deleteBy = SecurityUtils.getUserIdStr();
        gzLineRefWriteService.deleteOutboundRefs(id);
        gzShipmentMapper.deleteGzShipmentEntryByParenId(id, deleteBy);
        return gzShipmentMapper.deleteGzShipmentById(id, deleteBy);
    }

    @Override
    @Transactional
    public int auditGzShipment(String id) {
        Long billId;
        try {
            billId = Long.parseLong(id);
        } catch (Exception e) {
            throw new ServiceException(String.format("高值出库业务ID：%s 非法", id));
        }
        GzShipment gzShipment = gzShipmentMapper.selectGzShipmentById(billId);
        if(gzShipment == null){
            throw new ServiceException(String.format("高值出库业务ID：%s，不存在!", id));
        }
        if (gzShipment.getShipmentStatus() != null && gzShipment.getShipmentStatus() == 2) {
            String no = StringUtils.isNotEmpty(gzShipment.getShipmentNo()) ? gzShipment.getShipmentNo() : id;
            throw new ServiceException(String.format("高值出库单 %s 已审核，请勿重复审核", no));
        }
        List<GzShipmentEntry> gzShipmentEntryList = gzShipment.getGzShipmentEntryList();
        if (gzShipmentEntryList == null || gzShipmentEntryList.isEmpty()) {
            throw new ServiceException(String.format("高值出库单 %s 无明细，无法审核", id));
        }

        gzStockValidationService.assertShipmentOutbound(gzShipment, gzShipmentEntryList);

        // 先增加科室库存（需从备货库存读取批次/效期等），再扣减备货库存；若先扣减，备货行被删后 updateDepInventory 查不到备货，科室行也无法正确落库
        updateDepInventory(gzShipment, gzShipmentEntryList);

        // 更新高值仓库库存（减少库存）
        updateDepotInventory(gzShipment, gzShipmentEntryList);

        gzShipment.setShipmentStatus(2);
        gzShipment.setAuditDate(new Date());
        gzShipment.setUpdateBy(SecurityUtils.getUserIdStr());
        gzShipment.setUpdateTime(new Date());
        int res = gzShipmentMapper.updateGzShipment(gzShipment);
        System.out.println("审核出库单 - id: " + id + ", shipmentStatus: " + gzShipment.getShipmentStatus() + ", 更新结果: " + res);
        return res;
    }

    private void updateDepotInventory(GzShipment gzShipment, List<GzShipmentEntry> gzShipmentEntryList){
        // 出库时减少库存，根据批次号和物料ID匹配库存记录
        if (gzShipmentEntryList == null || gzShipmentEntryList.isEmpty()) {
            return;
        }
        for(GzShipmentEntry shipmentEntry : gzShipmentEntryList){
            if (shipmentEntry == null) {
                continue;
            }
            if(shipmentEntry.getQty() != null && BigDecimal.ZERO.compareTo(shipmentEntry.getQty()) != 0){
                int qty = shipmentEntry.getQty().intValue();
                if (StringUtils.isEmpty(shipmentEntry.getBatchNo())) {
                    throw new ServiceException(String.format("出库明细批次号不能为空，出库单ID：%s", gzShipment.getId()));
                }
                if (shipmentEntry.getMaterialId() == null) {
                    throw new ServiceException(String.format("出库明细物料ID不能为空，出库单ID：%s", gzShipment.getId()));
                }
                
                // 查询库存记录，按批次号和物料ID匹配
                GzDepotInventory queryInventory = new GzDepotInventory();
                queryInventory.setBatchNo(shipmentEntry.getBatchNo());
                queryInventory.setMaterialId(shipmentEntry.getMaterialId());
                queryInventory.setWarehouseId(gzShipment.getWarehouseId());
                
                List<GzDepotInventory> inventoryList = gzDepotInventoryMapper.selectGzDepotInventoryList(queryInventory);
                if (inventoryList == null) {
                    inventoryList = new ArrayList<>();
                }
                
                // 按数量减少库存，优先减少数量为1的记录
                int remainingQty = qty;
                for(GzDepotInventory inventory : inventoryList){
                    if(remainingQty <= 0){
                        break;
                    }
                    if (inventory == null || inventory.getQty() == null) {
                        continue;
                    }
                    if(inventory.getQty().compareTo(BigDecimal.ONE) == 0 && remainingQty > 0){
                        // 出库扣减：将数量、金额置零，保留备货行（不做软删，便于追溯与列表外统计）
                        zeroOutDepotInventoryRow(inventory);
                        remainingQty--;
                    }
                }
                
                // 如果还有剩余数量，减少其他记录的数量
                for(GzDepotInventory inventory : inventoryList){
                    if(remainingQty <= 0){
                        break;
                    }
                    if (inventory == null || inventory.getQty() == null) {
                        continue;
                    }
                    if(inventory.getQty().compareTo(BigDecimal.ONE) > 0){
                        BigDecimal reduceQty = BigDecimal.valueOf(Math.min(remainingQty, inventory.getQty().intValue()));
                        inventory.setQty(inventory.getQty().subtract(reduceQty));
                        if(inventory.getQty().compareTo(BigDecimal.ZERO) <= 0){
                            zeroOutDepotInventoryRow(inventory);
                        } else {
                            syncDepotInventoryAmt(inventory);
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

    /** 备货出库扣减后库存为 0：更新 qty/amt，不软删备货行 */
    private void zeroOutDepotInventoryRow(GzDepotInventory inventory) {
        inventory.setQty(BigDecimal.ZERO);
        inventory.setAmt(BigDecimal.ZERO);
        gzDepotInventoryMapper.updateGzDepotInventory(inventory);
    }

    private void syncDepotInventoryAmt(GzDepotInventory inventory) {
        if (inventory.getUnitPrice() != null && inventory.getQty() != null) {
            inventory.setAmt(inventory.getUnitPrice().multiply(inventory.getQty()));
        }
    }

    /**
     * 更新科室库存（出库审核时增加科室库存）
     * @param gzShipment 出库单
     * @param gzShipmentEntryList 出库明细列表
     */
    private void updateDepInventory(GzShipment gzShipment, List<GzShipmentEntry> gzShipmentEntryList){
        // 每个出库明细对应一个院内码，应该创建一条独立的科室库存记录
        if (gzShipmentEntryList == null || gzShipmentEntryList.isEmpty()) {
            return;
        }
        for(GzShipmentEntry shipmentEntry : gzShipmentEntryList){
            if (shipmentEntry == null) {
                continue;
            }
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
                
                BigDecimal addQty = shipmentEntry.getQty() != null ? shipmentEntry.getQty() : BigDecimal.ONE;
                if (addQty.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                // 按「科室 + 院内码」判断是否已有科室库存（必须带科室，否则会误判其他科室已有而跳过插入）
                GzDepInventory existingDepInventory = null;
                if (inHospitalCode != null && !inHospitalCode.trim().isEmpty() && gzShipment.getDepartmentId() != null) {
                    existingDepInventory = gzDepInventoryMapper.selectGzDepInventoryByCodeAndDept(
                        inHospitalCode.trim(), gzShipment.getDepartmentId());
                }
                if (existingDepInventory == null) {
                    GzDepInventory queryDepInventory = new GzDepInventory();
                    queryDepInventory.setBatchNo(shipmentEntry.getBatchNo());
                    queryDepInventory.setDepartmentId(gzShipment.getDepartmentId());
                    if (inHospitalCode != null && !inHospitalCode.trim().isEmpty()) {
                        queryDepInventory.setInHospitalCode(inHospitalCode);
                    }
                    queryDepInventory.setShowZeroStock(true);
                    List<GzDepInventory> existingDepInventoryList = gzDepInventoryMapper.selectGzDepInventoryList(queryDepInventory);
                    if (inHospitalCode != null && !inHospitalCode.trim().isEmpty() && existingDepInventoryList != null) {
                        for (GzDepInventory depInv : existingDepInventoryList) {
                            if (depInv != null && inHospitalCode.equals(depInv.getInHospitalCode())) {
                                existingDepInventory = depInv;
                                break;
                            }
                        }
                    }
                }

                if (existingDepInventory == null) {
                    // 新增科室库存
                    GzDepInventory gzDepInventory = new GzDepInventory();
                    gzDepInventory.setMaterialId(shipmentEntry.getMaterialId());
                    gzDepInventory.setDepartmentId(gzShipment.getDepartmentId());
                    gzDepInventory.setQty(addQty);
                    BigDecimal unitPrice = shipmentEntry.getPrice() != null ? shipmentEntry.getPrice() : BigDecimal.ZERO;
                    gzDepInventory.setUnitPrice(unitPrice);
                    gzDepInventory.setAmt(unitPrice.multiply(addQty));
                    gzDepInventory.setBatchNo(shipmentEntry.getBatchNo());
                    gzDepInventory.setMaterialNo(shipmentEntry.getBatchNumber());
                    if (depotInventory != null && depotInventory.getSupplierId() != null) {
                        gzDepInventory.setSupplierId(depotInventory.getSupplierId());
                    } else {
                        gzDepInventory.setSupplierId(shipmentEntry.getSupplierId());
                    }
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
                    String tenantId = StringUtils.isNotEmpty(gzShipment.getTenantId())
                        ? gzShipment.getTenantId()
                        : SecurityUtils.getCustomerId();
                    gzDepInventory.setTenantId(tenantId);
                    gzDepInventory.setDelFlag(0);
                    String userId = SecurityUtils.getUserIdStr();
                    Date now = DateUtils.getNowDate();
                    gzDepInventory.setCreateBy(userId);
                    gzDepInventory.setUpdateBy(userId);
                    gzDepInventory.setCreateTime(now);
                    gzDepInventory.setUpdateTime(now);
                    gzDepInventory.setMasterBarcode(StringUtils.isNotEmpty(shipmentEntry.getMasterBarcode())
                        ? shipmentEntry.getMasterBarcode()
                        : (depotInventory != null ? depotInventory.getMasterBarcode() : null));
                    gzDepInventory.setSecondaryBarcode(StringUtils.isNotEmpty(shipmentEntry.getSecondaryBarcode())
                        ? shipmentEntry.getSecondaryBarcode()
                        : (depotInventory != null ? depotInventory.getSecondaryBarcode() : null));
                    gzDepInventoryMapper.insertGzDepInventory(gzDepInventory);
                    System.out.println("创建科室库存记录 - 院内码: " + inHospitalCode + ", 批次号: " + shipmentEntry.getBatchNo() + ", 数量: " + addQty);
                } else {
                    BigDecimal baseQty = existingDepInventory.getQty() != null ? existingDepInventory.getQty() : BigDecimal.ZERO;
                    existingDepInventory.setQty(baseQty.add(addQty));
                    BigDecimal unitPrice = shipmentEntry.getPrice() != null ? shipmentEntry.getPrice() : existingDepInventory.getUnitPrice();
                    if (unitPrice == null) {
                        unitPrice = BigDecimal.ZERO;
                    }
                    BigDecimal baseAmt = existingDepInventory.getAmt() != null ? existingDepInventory.getAmt() : BigDecimal.ZERO;
                    existingDepInventory.setAmt(baseAmt.add(unitPrice.multiply(addQty)));
                    existingDepInventory.setUnitPrice(unitPrice);
                    existingDepInventory.setUpdateBy(SecurityUtils.getUserIdStr());
                    existingDepInventory.setUpdateTime(DateUtils.getNowDate());
                    gzDepInventoryMapper.updateGzDepInventory(existingDepInventory);
                    System.out.println("科室库存累加 - 院内码: " + inHospitalCode + ", 批次号: " + shipmentEntry.getBatchNo() + ", 增加数量: " + addQty);
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
            // tenant_id 在 mapper 批量写入时依赖 entry.tenantId，必须严格解析且不允许为空
            String tenantId = StringUtils.isNotEmpty(gzShipment.getTenantId())
                ? gzShipment.getTenantId()
                : SecurityUtils.requiredScopedTenantIdForSql();
            String userId = SecurityUtils.getUserIdStr();
            Date now = DateUtils.getNowDate();
            for (GzShipmentEntry gzShipmentEntry : gzShipmentEntryList)
            {
                if (gzShipmentEntry == null) {
                    continue;
                }
                // 调试：打印保存前的数据，特别是院内码
                System.out.println("保存出库明细 - materialId: " + gzShipmentEntry.getMaterialId() + 
                    ", qty: " + gzShipmentEntry.getQty() + 
                    ", price: " + gzShipmentEntry.getPrice() + 
                    ", batchNo: " + gzShipmentEntry.getBatchNo() + 
                    ", inHospitalCode: " + gzShipmentEntry.getInHospitalCode());
                
                gzShipmentEntry.setParenId(id);
                gzShipmentEntry.setTenantId(tenantId);
                if (gzShipmentEntry.getSupplierId() == null && StringUtils.isNotEmpty(gzShipmentEntry.getBatchNo())) {
                    GzDepotInventory source = gzDepotInventoryMapper.selectGzDepotInventoryOne(gzShipmentEntry.getBatchNo());
                    if (source != null) {
                        gzShipmentEntry.setSupplierId(source.getSupplierId());
                    }
                }
                // 只有在 batchNo 为空时才生成新的批次号，避免覆盖已有的批次号
                if (gzShipmentEntry.getBatchNo() == null || gzShipmentEntry.getBatchNo().isEmpty()) {
                    gzShipmentEntry.setBatchNo(getBatchNumber());
                }
                gzShipmentEntry.setDelFlag(0);
                gzShipmentEntry.setWarehouseId(gzShipment.getWarehouseId());
                gzShipmentEntry.setDepartmentId(gzShipment.getDepartmentId());
                gzShipmentEntry.setBillNo(gzShipment.getShipmentNo());
                gzShipmentEntry.setCreateBy(userId);
                gzShipmentEntry.setCreateTime(now);
                gzShipmentEntry.setUpdateBy(userId);
                gzShipmentEntry.setUpdateTime(now);
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


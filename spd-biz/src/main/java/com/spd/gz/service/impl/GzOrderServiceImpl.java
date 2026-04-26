package com.spd.gz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;
import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.gz.domain.GzBillEntryChangeLog;
import com.spd.gz.domain.GzDepotInventory;
import com.spd.gz.domain.GzOrderEntry;
import com.spd.gz.domain.GzOrderEntryInhospitalcodeList;
import com.spd.gz.mapper.GzBillEntryChangeLogMapper;
import com.spd.gz.mapper.GzDepotInventoryMapper;
import com.spd.gz.mapper.SysSheetIdMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.spd.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.spd.gz.mapper.GzOrderMapper;
import com.spd.gz.domain.GzOrder;
import com.spd.gz.service.IGzOrderService;
import java.text.SimpleDateFormat;

/**
 * 高值入库Service业务层处理
 *
 * @author spd
 * @date 2024-06-11
 */
@Service
public class GzOrderServiceImpl implements IGzOrderService
{
    private static final Logger log = LoggerFactory.getLogger(GzOrderServiceImpl.class);
    @Autowired
    private GzOrderMapper gzOrderMapper;

    @Autowired
    private GzDepotInventoryMapper gzDepotInventoryMapper;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    @Autowired
    private SysSheetIdMapper sysSheetIdMapper;

    @Autowired
    private GzBillEntryChangeLogMapper gzBillEntryChangeLogMapper;

    /**
     * 查询高值入库
     *
     * @param id 高值入库主键
     * @return 高值入库
     */
    @Override
    public GzOrder selectGzOrderById(Long id)
    {
        GzOrder gzOrder = gzOrderMapper.selectGzOrderById(id);
        if (gzOrder != null) {
            SecurityUtils.ensureTenantAccess(gzOrder.getTenantId());
        }
        if(gzOrder == null){
            return null;
        }

        List<GzOrderEntry> gzOrderEntryList = gzOrder.getGzOrderEntryList();
        List<FdMaterial> materialList = new ArrayList<FdMaterial>();
        if (gzOrderEntryList != null) {
            for(GzOrderEntry entry : gzOrderEntryList){
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
        gzOrder.setMaterialList(materialList);
        return gzOrder;
    }

    /**
     * 查询高值入库列表
     *
     * @param gzOrder 高值入库
     * @return 高值入库
     */
    @Override
    public List<GzOrder> selectGzOrderList(GzOrder gzOrder)
    {
        if (gzOrder != null && StringUtils.isEmpty(gzOrder.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            gzOrder.setTenantId(SecurityUtils.getCustomerId());
        }
        return gzOrderMapper.selectGzOrderList(gzOrder);
    }

    /**
     * 新增高值入库
     *
     * @param gzOrder 高值入库
     * @return 结果
     */
    @Transactional
    @Override
    public int insertGzOrder(GzOrder gzOrder)
    {
        if (gzOrder == null) {
            throw new ServiceException("高值入库单不能为空");
        }
        if (gzOrder != null && StringUtils.isEmpty(gzOrder.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            gzOrder.setTenantId(SecurityUtils.getCustomerId());
        }
        gzOrder.setCreateBy(SecurityUtils.getUserIdStr());
        gzOrder.setOrderNo(getOrderNo(gzOrder.getOrderType()));
        gzOrder.setCreateTime(DateUtils.getNowDate());
        int rows = gzOrderMapper.insertGzOrder(gzOrder);
        insertGzOrderEntry(gzOrder);
        return rows;
    }

    //str：单号前缀
    //date：日期
    //result：最终结果，需要的流水号
    //orderType: 101=入库(GZRK), 102=出库(GZCK), 301=退库(GZTK), 401=跟台(GT)
    public String getOrderNo(Integer orderType) {
        // 根据orderType决定单号前缀：101=入库(GZRK), 102=出库(GZCK), 301=退库(GZTK), 401=跟台(GT)
        String str;
        if (orderType != null && orderType == 102) {
            str = "GZCK";  // 出库
        } else if (orderType != null && orderType == 301) {
            str = "GZTK";  // 退库
        } else if (orderType != null && orderType == 401) {
            str = "GT";    // 跟台
        } else {
            str = "GZRK";  // 入库（默认）
        }
        String date = FillRuleUtil.getDateNum();
        String maxNum = gzOrderMapper.selectMaxBillNo(str, date);
        String result = FillRuleUtil.getNumber(str,maxNum,date);
        return result;
    }

    /**
     * 修改高值入库
     *
     * @param gzOrder 高值入库
     * @return 结果
     */
    @Transactional
    @Override
    public int updateGzOrder(GzOrder gzOrder)
    {
        gzOrder.setUpdateBy(SecurityUtils.getUserIdStr());
        gzOrder.setUpdateTime(DateUtils.getNowDate());
        syncGzOrderEntry(gzOrder);
        return gzOrderMapper.updateGzOrder(gzOrder);
    }

    /**
     * 删除高值入库信息
     *
     * @param id 高值入库主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteGzOrderById(Long id)
    {
        GzOrder gzOrder = gzOrderMapper.selectGzOrderById(id);
        if(gzOrder == null){
            throw new ServiceException(String.format("高值入库业务：%s，不存在!", id));
        }
        SecurityUtils.ensureTenantAccess(gzOrder.getTenantId());
        String deleteBy = SecurityUtils.getUserIdStr();
        gzOrderMapper.deleteGzOrderEntryByParenId(id, deleteBy);
        return gzOrderMapper.deleteGzOrderById(id, deleteBy);
    }

    @Override
    @Transactional
    public int auditGzOrder(String id) {
        Long billId;
        try {
            billId = Long.parseLong(id);
        } catch (Exception e) {
            throw new ServiceException(String.format("高值入库业务ID：%s 非法", id));
        }
        GzOrder gzOrder = gzOrderMapper.selectGzOrderById(billId);
        if(gzOrder == null){
            throw new ServiceException(String.format("高值入库业务ID：%s，不存在!", id));
        }
        List<GzOrderEntry> gzOrderEntryList = gzOrder.getGzOrderEntryList();
        if (gzOrderEntryList == null || gzOrderEntryList.isEmpty()) {
            throw new ServiceException(String.format("高值入库单 %s 无明细，无法审核", id));
        }

        //更新高值仓库库存
        updateDepotInventory(gzOrder,gzOrderEntryList);

        gzOrder.setOrderStatus(2);
        gzOrder.setAuditBy(SecurityUtils.getUserIdStr());
        gzOrder.setUpdateBy(SecurityUtils.getUserIdStr());
        gzOrder.setAuditDate(new Date());
        int res = gzOrderMapper.updateGzOrder(gzOrder);
        return res;
    }

    private void updateDepotInventory(GzOrder gzOrder,List<GzOrderEntry> gzOrderEntryList){
        // 获取或初始化序列号
        Long sheetId = getOrInitSheetId();
        
        if (gzOrderEntryList == null || gzOrderEntryList.isEmpty()) {
            return;
        }
        for(GzOrderEntry orderEntry : gzOrderEntryList){
            if (orderEntry == null) {
                continue;
            }
            if(orderEntry.getQty() != null && BigDecimal.ZERO.compareTo(orderEntry.getQty()) != 0){
                if (orderEntry.getQty().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new ServiceException(String.format("高值入库明细数量必须大于0，物料ID：%s", orderEntry.getMaterialId()));
                }
                // 根据数量循环生成多条库存记录，每条数量为1，并生成一个院内码
                int qty = orderEntry.getQty().intValue();
                for(int i = 0; i < qty; i++){
                    sheetId = sheetId + 1;
                    String inHospitalCode = generateInHospitalCode(sheetId);
                    
                    GzDepotInventory gzDepotInventory = new GzDepotInventory();
                    gzDepotInventory.setMaterialNo(orderEntry.getBatchNumber());
                    gzDepotInventory.setBatchNo(orderEntry.getBatchNo());
                    gzDepotInventory.setMaterialId(orderEntry.getMaterialId());
                    gzDepotInventory.setWarehouseId(gzOrder.getWarehouseId());
                    gzDepotInventory.setQty(BigDecimal.ONE); // 每条记录数量为1
                    gzDepotInventory.setUnitPrice(orderEntry.getPrice());
                    gzDepotInventory.setAmt(orderEntry.getPrice()); // 金额 = 单价 * 1
                    gzDepotInventory.setMaterialDate(orderEntry.getBeginTime() != null ? orderEntry.getBeginTime() : new Date());
                    gzDepotInventory.setWarehouseDate(new Date());
                    gzDepotInventory.setSupplierId(gzOrder.getSupplerId());
                    gzDepotInventory.setInHospitalCode(inHospitalCode);
                    gzDepotInventory.setEndTime(orderEntry.getEndTime()); // 保存有效期
                    if (StringUtils.isEmpty(gzDepotInventory.getTenantId())) {
                        gzDepotInventory.setTenantId(StringUtils.isNotEmpty(gzOrder.getTenantId()) ? gzOrder.getTenantId() : SecurityUtils.getCustomerId());
                    }
                    gzDepotInventory.setMasterBarcode(orderEntry.getMasterBarcode());
                    gzDepotInventory.setSecondaryBarcode(orderEntry.getSecondaryBarcode());
                    gzDepotInventory.setOrderId(gzOrder.getId());
                    gzDepotInventory.setOrderNo(gzOrder.getOrderNo());
                    gzDepotInventory.setOrderEntryId(orderEntry.getId());

                    Date now = new Date();
                    String userId = SecurityUtils.getUserIdStr();
                    GzOrderEntryInhospitalcodeList inhospitalRow = new GzOrderEntryInhospitalcodeList();
                    inhospitalRow.setParentId(gzOrder.getId());
                    inhospitalRow.setCode(gzOrder.getOrderNo());
                    inhospitalRow.setDetailId(orderEntry.getId());
                    inhospitalRow.setMaterialId(orderEntry.getMaterialId());
                    inhospitalRow.setPrice(orderEntry.getPrice());
                    inhospitalRow.setQty(BigDecimal.ONE);
                    inhospitalRow.setBatchNo(orderEntry.getBatchNo());
                    inhospitalRow.setBatchNumber(orderEntry.getBatchNumber());
                    inhospitalRow.setMasterBarcode(orderEntry.getMasterBarcode());
                    inhospitalRow.setSecondaryBarcode(orderEntry.getSecondaryBarcode());
                    inhospitalRow.setEndDate(orderEntry.getEndTime());
                    inhospitalRow.setInHospitalCode(inHospitalCode);
                    inhospitalRow.setWarehouseId(gzOrder.getWarehouseId());
                    inhospitalRow.setSupplierId(gzOrder.getSupplerId());
                    inhospitalRow.setDelFlag(0);
                    inhospitalRow.setCreateDate(now);
                    inhospitalRow.setCreateBy(userId);
                    inhospitalRow.setCreateTime(now);
                    inhospitalRow.setUpdateBy(userId);
                    inhospitalRow.setUpdateTime(now);
                    inhospitalRow.setTenantId(StringUtils.isNotEmpty(gzOrder.getTenantId()) ? gzOrder.getTenantId() : SecurityUtils.getCustomerId());
                    gzOrderMapper.insertGzOrderEntryInhospitalcodeList(inhospitalRow);
                    gzDepotInventory.setInhospitalcodeListId(inhospitalRow.getId());

                    gzDepotInventoryMapper.insertGzDepotInventory(gzDepotInventory);
                }
            }
        }
        
        // 更新序列号
        updateSheetId(sheetId);
    }

    /**
     * 获取或初始化序列号
     */
    private Long getOrInitSheetId(){
        String businessType = "高值";
        String sheetType = "gzynm";
        
        int count = sysSheetIdMapper.countSheetId(businessType, sheetType);
        if(count == 0){
            // 如果不存在，插入初始记录
            sysSheetIdMapper.insertSheetId(businessType, sheetType, 0L);
            return 0L;
        } else {
            Long sheetId = sysSheetIdMapper.selectSheetId(businessType, sheetType);
            return sheetId != null ? sheetId : 0L;
        }
    }

    /**
     * 更新序列号
     */
    private void updateSheetId(Long sheetId){
        String businessType = "高值";
        String sheetType = "gzynm";
        sysSheetIdMapper.updateSheetId(businessType, sheetType, sheetId);
    }

    /**
     * 生成院内码
     * 格式：G + 日期时间（10位） + 序列号（6位）
     * 参考存储过程：CONCAT('G', RIGHT(DATE_FORMAT(NOW(), '%Y%m%d%H%i'), 10), '', right(CAST(sheetId + 1000000 AS CHAR),6))
     * 例如：G20251208130 + 1000001 = G202512081301000001
     */
    private String generateInHospitalCode(Long sheetId){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        String dateStr = sdf.format(new Date());
        // 取后10位（存储过程使用RIGHT函数取后10位）
        if(dateStr.length() > 10){
            dateStr = dateStr.substring(dateStr.length() - 10);
        }
        // 序列号格式：sheetId + 1000000，然后取后6位（存储过程使用right函数取后6位）
        Long seqNum = sheetId + 1000000;
        String seqStr = String.valueOf(seqNum);
        if(seqStr.length() > 6){
            seqStr = seqStr.substring(seqStr.length() - 6);
        } else {
            // 如果不足6位，左补0
            seqStr = String.format("%06d", seqNum);
        }
        return "G" + dateStr + seqStr;
    }


    /**
     * 新增高值退货明细信息
     *
     * @param gzOrder 高值入库对象
     */
    public void insertGzOrderEntry(GzOrder gzOrder)
    {
        List<GzOrderEntry> gzOrderEntryList = gzOrder.getGzOrderEntryList();
        Long id = gzOrder.getId();
        if (StringUtils.isNotNull(gzOrderEntryList))
        {
            List<GzOrderEntry> list = new ArrayList<GzOrderEntry>();
            String userId = SecurityUtils.getUserIdStr();
            Date now = DateUtils.getNowDate();
            for (GzOrderEntry gzOrderEntry : gzOrderEntryList)
            {
                gzOrderEntry.setParenId(id);
                gzOrderEntry.setBatchNo(getBatchNumber());
                gzOrderEntry.setDelFlag(0);
                gzOrderEntry.setSupplierId(gzOrder.getSupplerId());
                gzOrderEntry.setWarehouseId(gzOrder.getWarehouseId());
                gzOrderEntry.setBillNo(gzOrder.getOrderNo());
                gzOrderEntry.setCreateBy(userId);
                gzOrderEntry.setCreateTime(now);
                gzOrderEntry.setUpdateBy(userId);
                gzOrderEntry.setUpdateTime(now);
                list.add(gzOrderEntry);
            }
            if (list.size() > 0)
            {
                gzOrderMapper.batchGzOrderEntry(list);
            }
        }
    }

    /**
     * 修改单据时明细增量更新：保留原明细ID，按ID更新；新增无ID明细；移除的明细做逻辑删除
     */
    public void syncGzOrderEntry(GzOrder gzOrder)
    {
        List<GzOrderEntry> gzOrderEntryList = gzOrder.getGzOrderEntryList();
        Long parenId = gzOrder.getId();
        String userId = SecurityUtils.getUserIdStr();
        Date now = DateUtils.getNowDate();

        List<Long> existingIds = gzOrderMapper.selectActiveOrderEntryIdsByParenId(parenId);
        Set<Long> remainingIds = new HashSet<>();
        if (existingIds != null) {
            remainingIds.addAll(existingIds);
        }
        Map<Long, GzOrderEntry> oldEntryMap = gzOrderMapper.selectActiveOrderEntriesByParenId(parenId)
            .stream().collect(Collectors.toMap(GzOrderEntry::getId, e -> e, (a, b) -> a));

        if (StringUtils.isNotNull(gzOrderEntryList))
        {
            List<GzOrderEntry> newList = new ArrayList<GzOrderEntry>();
            for (GzOrderEntry entry : gzOrderEntryList)
            {
                if (entry == null) {
                    continue;
                }
                entry.setParenId(parenId);
                if (StringUtils.isEmpty(entry.getBatchNo())) {
                    entry.setBatchNo(getBatchNumber());
                }
                entry.setDelFlag(0);
                entry.setSupplierId(gzOrder.getSupplerId());
                entry.setWarehouseId(gzOrder.getWarehouseId());
                entry.setBillNo(gzOrder.getOrderNo());
                entry.setUpdateBy(userId);
                entry.setUpdateTime(now);

                if (entry.getId() != null) {
                    GzOrderEntry old = oldEntryMap.get(entry.getId());
                    gzOrderMapper.updateGzOrderEntryById(entry);
                    remainingIds.remove(entry.getId());
                    if (old != null && isOrderEntryChanged(old, entry)) {
                        log.info("GZ_ORDER_ENTRY_CHANGE UPDATE billId={}, entryId={}, before={}, after={}",
                            parenId, entry.getId(), JSON.toJSONString(old), JSON.toJSONString(entry));
                        saveEntryChangeLog("GZ_ORDER", parenId, "GZ_ORDER_ENTRY", entry.getId(), "UPDATE", old, entry, userId, gzOrder.getTenantId());
                    }
                } else {
                    entry.setCreateBy(userId);
                    entry.setCreateTime(now);
                    newList.add(entry);
                    log.info("GZ_ORDER_ENTRY_CHANGE INSERT billId={}, entry={}", parenId, JSON.toJSONString(entry));
                    saveEntryChangeLog("GZ_ORDER", parenId, "GZ_ORDER_ENTRY", null, "INSERT", null, entry, userId, gzOrder.getTenantId());
                }
            }

            if (!newList.isEmpty()) {
                gzOrderMapper.batchGzOrderEntry(newList);
            }
        }

        for (Long removedId : remainingIds) {
            GzOrderEntry toDelete = new GzOrderEntry();
            toDelete.setId(removedId);
            toDelete.setParenId(parenId);
            toDelete.setDelFlag(1);
            toDelete.setUpdateBy(userId);
            gzOrderMapper.updateGzOrderEntryById(toDelete);
            GzOrderEntry old = oldEntryMap.get(removedId);
            log.info("GZ_ORDER_ENTRY_CHANGE DELETE billId={}, entryId={}, before={}",
                parenId, removedId, JSON.toJSONString(old));
            saveEntryChangeLog("GZ_ORDER", parenId, "GZ_ORDER_ENTRY", removedId, "DELETE", old, null, userId, gzOrder.getTenantId());
        }
    }

    private void saveEntryChangeLog(String billType, Long billId, String entryType, Long entryId, String actionType,
                                    Object before, Object after, String operator, String tenantId) {
        GzBillEntryChangeLog rec = new GzBillEntryChangeLog();
        rec.setId(UUID7.generateUUID7());
        rec.setBillType(billType);
        rec.setBillId(billId);
        rec.setEntryType(entryType);
        rec.setEntryId(entryId);
        rec.setActionType(actionType);
        rec.setBeforeJson(before == null ? null : JSON.toJSONString(before));
        rec.setAfterJson(after == null ? null : JSON.toJSONString(after));
        rec.setOperator(operator);
        rec.setChangeTime(DateUtils.getNowDate());
        rec.setTenantId(StringUtils.isNotEmpty(tenantId) ? tenantId : SecurityUtils.requiredScopedTenantIdForSql());
        gzBillEntryChangeLogMapper.insert(rec);
    }

    private boolean isOrderEntryChanged(GzOrderEntry oldRow, GzOrderEntry newRow) {
        if (oldRow == null || newRow == null) {
            return false;
        }
        return !java.util.Objects.equals(oldRow.getMaterialId(), newRow.getMaterialId())
            || !java.util.Objects.equals(oldRow.getQty(), newRow.getQty())
            || !java.util.Objects.equals(oldRow.getPrice(), newRow.getPrice())
            || !java.util.Objects.equals(oldRow.getAmt(), newRow.getAmt())
            || !java.util.Objects.equals(oldRow.getBatchNo(), newRow.getBatchNo())
            || !java.util.Objects.equals(oldRow.getBatchNumber(), newRow.getBatchNumber())
            || !java.util.Objects.equals(oldRow.getBeginTime(), newRow.getBeginTime())
            || !java.util.Objects.equals(oldRow.getEndTime(), newRow.getEndTime())
            || !java.util.Objects.equals(oldRow.getMasterBarcode(), newRow.getMasterBarcode())
            || !java.util.Objects.equals(oldRow.getSecondaryBarcode(), newRow.getSecondaryBarcode())
            || !java.util.Objects.equals(oldRow.getSupplierId(), newRow.getSupplierId())
            || !java.util.Objects.equals(oldRow.getWarehouseId(), newRow.getWarehouseId())
            || !java.util.Objects.equals(oldRow.getBillNo(), newRow.getBillNo())
            || !java.util.Objects.equals(oldRow.getRemark(), newRow.getRemark());
    }

    public String getBatchNumber() {
        String str = "GZPC";
        String createNo = FillRuleUtil.createBatchNo();
        String batchNo = str + createNo;
        return batchNo;
    }

    @Override
    public List<String> selectOutboundOrderNosByInHospitalCode(String inHospitalCode) {
        return gzOrderMapper.selectOutboundOrderNosByInHospitalCode(inHospitalCode);
    }
}

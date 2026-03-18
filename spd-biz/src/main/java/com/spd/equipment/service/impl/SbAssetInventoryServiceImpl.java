package com.spd.equipment.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.equipment.domain.SbAssetInventory;
import com.spd.equipment.domain.SbAssetInventoryItem;
import com.spd.equipment.domain.SbAssetInventoryItemPrint;
import com.spd.equipment.domain.SbAssetPrintTask;
import com.spd.equipment.domain.SbAssetPrintTaskItem;
import com.spd.equipment.domain.SbCustomerAssetLedger;
import com.spd.equipment.mapper.SbAssetInventoryMapper;
import com.spd.equipment.mapper.SbCustomerAssetLedgerMapper;
import com.spd.equipment.service.ISbAssetInventoryService;
import com.spd.equipment.service.ISbAssetInventoryItemPrintService;
import com.spd.equipment.service.ISbAssetInventoryItemService;
import com.spd.equipment.service.ISbAssetPrintTaskItemService;
import com.spd.equipment.service.ISbAssetPrintTaskService;
import com.spd.foundation.domain.SbCustomerCategory68;
import com.spd.foundation.service.ISbCustomerCategory68Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 资产盘点单主表 Service 实现
 */
@Service
public class SbAssetInventoryServiceImpl implements ISbAssetInventoryService {

    @Autowired
    private SbAssetInventoryMapper mapper;
    @Autowired
    private ISbAssetInventoryItemService inventoryItemService;
    @Autowired
    private ISbAssetPrintTaskService printTaskService;
    @Autowired
    private ISbAssetPrintTaskItemService printTaskItemService;
    @Autowired
    private ISbAssetInventoryItemPrintService inventoryItemPrintService;
    @Autowired
    private SbCustomerAssetLedgerMapper ledgerMapper;
    @Autowired
    private ISbCustomerCategory68Service category68Service;

    @Override
    public List<SbAssetInventory> selectList(SbAssetInventory q) {
        if (q != null && StringUtils.isEmpty(q.getCustomerId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            q.setCustomerId(SecurityUtils.getCustomerId());
        }
        return mapper.selectList(q);
    }

    @Override
    public SbAssetInventory selectById(String id) {
        return mapper.selectById(id);
    }

    @Override
    public SbAssetInventory selectByOrderNo(String customerId, String orderNo) {
        return mapper.selectByOrderNo(customerId, orderNo);
    }

    @Override
    public int insert(SbAssetInventory row) {
        if (StringUtils.isEmpty(row.getCustomerId())) {
            row.setCustomerId(SecurityUtils.getCustomerId());
        }
        if (StringUtils.isEmpty(row.getId())) {
            row.setId(UUID7.generateUUID7());
        }
        if (StringUtils.isEmpty(row.getOrderNo())) {
            row.setOrderNo(generateOrderNo(row.getCustomerId()));
        }
        row.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getCreateBy())) {
            row.setCreateBy(SecurityUtils.getUserIdStr());
        }
        row.setUpdateTime(row.getCreateTime());
        if (StringUtils.isEmpty(row.getUpdateBy())) {
            row.setUpdateBy(row.getCreateBy());
        }
        if (row.getDelFlag() == null) row.setDelFlag(0);
        if (row.getTotalCount() == null) row.setTotalCount(0);
        if (row.getCheckedCount() == null) row.setCheckedCount(0);
        if (StringUtils.isEmpty(row.getStatus())) row.setStatus("draft");
        return mapper.insert(row);
    }

    @Override
    public int update(SbAssetInventory row) {
        row.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(row.getUpdateBy())) {
            row.setUpdateBy(SecurityUtils.getUserIdStr());
        }
        return mapper.update(row);
    }

    @Override
    public int deleteById(String id) {
        return mapper.deleteById(id, SecurityUtils.getUserIdStr());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SbAssetPrintTask createPrintTaskFromInventoryItems(String inventoryId, List<String> inventoryItemIds) {
        SbAssetInventory inv = mapper.selectById(inventoryId);
        if (inv == null) throw new ServiceException("盘点单不存在");
        String customerId = inv.getCustomerId();
        String orderNo = inv.getOrderNo();
        List<SbAssetInventoryItem> items;
        if (inventoryItemIds == null || inventoryItemIds.isEmpty()) {
            items = inventoryItemService.selectByInventoryId(inventoryId);
        } else {
            items = new ArrayList<>();
            for (String itemId : inventoryItemIds) {
                SbAssetInventoryItem item = inventoryItemService.selectById(itemId);
                if (item != null && inventoryId.equals(item.getInventoryId())) items.add(item);
            }
        }
        if (items == null || items.isEmpty()) throw new ServiceException("没有可打印的盘点明细");

        String taskNo = printTaskService.generateTaskNo(customerId);
        SbAssetPrintTask task = new SbAssetPrintTask();
        task.setCustomerId(customerId);
        task.setTaskNo(taskNo);
        task.setSourceType("盘点");
        task.setSourceRemark(orderNo);
        task.setTotalCount(items.size());
        task.setPrintedCount(0);
        task.setStatus("NEW");
        printTaskService.insert(task);

        for (SbAssetInventoryItem item : items) {
            SbAssetPrintTaskItem ptItem = new SbAssetPrintTaskItem();
            ptItem.setCustomerId(customerId);
            ptItem.setTaskId(task.getId());
            ptItem.setTaskNo(taskNo);
            ptItem.setAssetId(item.getAssetId());
            ptItem.setAssetName(item.getName());
            ptItem.setSpec(item.getSpec());
            ptItem.setModel(item.getModel());
            ptItem.setUnitPrice(item.getOriginalValue() != null ? item.getOriginalValue() : null);
            ptItem.setManufacturer(item.getManufacturerName());
            ptItem.setSerialNumber(null);
            ptItem.setEquipmentSerialNo(item.getEquipmentSerialNo());
            ptItem.setPrintStatus("未打印");
            ptItem.setPrintCount(0);
            printTaskItemService.insert(ptItem);

            SbAssetInventoryItemPrint link = new SbAssetInventoryItemPrint();
            link.setCustomerId(customerId);
            link.setInventoryId(inventoryId);
            link.setOrderNo(orderNo);
            link.setInventoryItemId(item.getId());
            link.setAssetId(item.getAssetId());
            link.setPrintTaskId(task.getId());
            link.setPrintTaskNo(taskNo);
            link.setPrintTaskItemId(ptItem.getId());
            inventoryItemPrintService.insert(link);
        }
        return task;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int buildItemsFromLedger(String inventoryId) {
        SbAssetInventory inv = mapper.selectById(inventoryId);
        if (inv == null) throw new ServiceException("盘点单不存在");
        if (!"draft".equals(inv.getStatus())) throw new ServiceException("仅草稿状态可生成明细");
        String customerId = inv.getCustomerId();
        String orderNo = inv.getOrderNo();
        String type = inv.getInventoryType();
        List<SbCustomerAssetLedger> ledgers;
        if ("dept".equals(type)) {
            if (StringUtils.isEmpty(inv.getInventoryDeptId())) throw new ServiceException("按科室盘点请先选择盘点科室");
            ledgers = ledgerMapper.selectListByDeptId(customerId, inv.getInventoryDeptId());
        } else if ("category68".equals(type)) {
            if (StringUtils.isEmpty(inv.getInventoryCategory68Id())) throw new ServiceException("按68分类盘点请先选择68分类");
            List<SbCustomerCategory68> tree = category68Service.selectTree(customerId);
            List<String> category68Ids = collectCategory68SelfAndDescendantIds(tree, inv.getInventoryCategory68Id());
            ledgers = ledgerMapper.selectListByCategory68Ids(customerId, category68Ids);
        } else if ("storage_place".equals(type)) {
            if (StringUtils.isEmpty(inv.getStoragePlace())) throw new ServiceException("按存放地点盘点请先选择存放地点");
            ledgers = ledgerMapper.selectListByStoragePlace(customerId, inv.getStoragePlace());
        } else {
            throw new ServiceException("不支持的盘点类型或请先保存盘点类型与范围");
        }
        if (ledgers == null) ledgers = Collections.emptyList();
        inventoryItemService.deleteByInventoryId(inventoryId);
        String createBy = SecurityUtils.getUserIdStr();
        java.util.Date now = DateUtils.getNowDate();
        List<SbAssetInventoryItem> items = new ArrayList<>();
        int sortOrder = 0;
        for (SbCustomerAssetLedger ledger : ledgers) {
            SbAssetInventoryItem item = new SbAssetInventoryItem();
            item.setCustomerId(customerId);
            item.setInventoryId(inventoryId);
            item.setOrderNo(orderNo);
            item.setAssetId(ledger.getId());
            item.setName(ledger.getName());
            item.setSpec(ledger.getSpec());
            item.setModel(ledger.getModel());
            item.setOriginalValue(ledger.getOriginalValue());
            item.setDeptId(ledger.getDeptId());
            item.setDeptName(ledger.getDeptName());
            item.setStoragePlace(ledger.getStoragePlace());
            item.setEquipmentSerialNo(ledger.getEquipmentSerialNo());
            item.setCategory68Id(ledger.getCategory68Id());
            item.setCategory68Name(ledger.getCategory68Code() != null ? ledger.getCategory68Code() + (ledger.getCategory68ArchiveNo() != null && !ledger.getCategory68ArchiveNo().isEmpty() ? " " + ledger.getCategory68ArchiveNo() : "") : (ledger.getCategory68ArchiveNo() != null ? ledger.getCategory68ArchiveNo() : ""));
            item.setManufacturerName(ledger.getManufacturerName());
            item.setSortOrder(sortOrder++);
            item.setCreateBy(createBy);
            item.setCreateTime(now);
            item.setUpdateBy(createBy);
            item.setUpdateTime(now);
            items.add(item);
        }
        if (!items.isEmpty()) {
            inventoryItemService.insertBatch(items);
        }
        inv.setTotalCount(items.size());
        inv.setCheckedCount(0);
        inv.setUpdateTime(now);
        inv.setUpdateBy(createBy);
        mapper.update(inv);
        return items.size();
    }

    /** 从68分类树中收集指定节点及其所有下级节点ID（含自身） */
    private List<String> collectCategory68SelfAndDescendantIds(List<SbCustomerCategory68> tree, String targetId) {
        List<String> ids = new ArrayList<>();
        if (tree == null || targetId == null) return ids;
        for (SbCustomerCategory68 node : tree) {
            if (targetId.equals(node.getId())) {
                ids.add(node.getId());
                collectDescendantIds(node.getChildren(), ids);
                return ids;
            }
            List<String> fromChild = collectFromChildren(node.getChildren(), targetId);
            if (!fromChild.isEmpty()) return fromChild;
        }
        return ids;
    }

    private List<String> collectFromChildren(List<SbCustomerCategory68> children, String targetId) {
        if (children == null) return Collections.emptyList();
        for (SbCustomerCategory68 node : children) {
            if (targetId.equals(node.getId())) {
                List<String> ids = new ArrayList<>();
                ids.add(node.getId());
                collectDescendantIds(node.getChildren(), ids);
                return ids;
            }
            List<String> fromChild = collectFromChildren(node.getChildren(), targetId);
            if (!fromChild.isEmpty()) return fromChild;
        }
        return Collections.emptyList();
    }

    private void collectDescendantIds(List<SbCustomerCategory68> children, List<String> ids) {
        if (children == null) return;
        for (SbCustomerCategory68 node : children) {
            ids.add(node.getId());
            collectDescendantIds(node.getChildren(), ids);
        }
    }

    /** 生成盘点单号：PD + yyyyMMdd + 4位序号 */
    private String generateOrderNo(String customerId) {
        String datePart = "PD" + DateUtils.dateTimeNow("yyyyMMdd");
        Integer maxSeq = mapper.selectMaxOrderNoSeqToday(customerId, datePart);
        int next = (maxSeq == null ? 0 : maxSeq) + 1;
        return datePart + String.format("%04d", next);
    }
}

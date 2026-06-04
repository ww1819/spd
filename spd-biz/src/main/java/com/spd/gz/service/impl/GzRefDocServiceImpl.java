package com.spd.gz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.StringUtils;
import com.spd.gz.domain.GzDepotInventory;
import com.spd.gz.domain.GzOrder;
import com.spd.gz.domain.GzOrderEntryInhospitalcodeList;
import com.spd.gz.domain.GzShipment;
import com.spd.gz.domain.GzShipmentEntry;
import com.spd.gz.domain.vo.GzAcceptanceRefMissingBarcodeVo;
import com.spd.gz.domain.vo.GzAcceptanceRefPreviewVo;
import com.spd.gz.mapper.GzDepInventoryMapper;
import com.spd.gz.mapper.GzDepotInventoryMapper;
import com.spd.gz.mapper.GzOrderEntryCodeRefMapper;
import com.spd.gz.mapper.GzOrderMapper;
import com.spd.gz.mapper.GzShipmentMapper;
import com.spd.gz.service.GzLineRefWriteService;
import com.spd.gz.service.IGzRefDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 引用查询实现
 */
@Service
public class GzRefDocServiceImpl implements IGzRefDocService
{
    @Autowired
    private GzOrderMapper gzOrderMapper;

    @Autowired
    private GzDepotInventoryMapper gzDepotInventoryMapper;

    @Autowired
    private GzShipmentMapper gzShipmentMapper;

    @Autowired
    private GzDepInventoryMapper gzDepInventoryMapper;

    @Autowired
    private GzOrderEntryCodeRefMapper gzOrderEntryCodeRefMapper;

    @Override
    public List<GzOrder> listAuditedAcceptance(GzOrder query)
    {
        if (query == null)
        {
            query = new GzOrder();
        }
        if (query.getRefWarehouseId() != null)
        {
            return gzOrderMapper.selectAuditedAcceptanceForShipmentRef(query);
        }
        query.setOrderType(101);
        query.setOrderStatus(2);
        return gzOrderMapper.selectGzOrderList(query);
    }

    @Override
    public GzAcceptanceRefPreviewVo previewAcceptanceRef(Long acceptanceOrderId, Long warehouseId, Long excludeShipmentId)
    {
        if (acceptanceOrderId == null || warehouseId == null)
        {
            throw new ServiceException("验收单与出库仓库不能为空");
        }
        GzOrder order = gzOrderMapper.selectGzOrderById(acceptanceOrderId);
        if (order == null || order.getOrderType() == null || order.getOrderType() != 101
            || order.getOrderStatus() == null || order.getOrderStatus() != 2)
        {
            throw new ServiceException("请选择已审核的备货验收单");
        }

        GzAcceptanceRefPreviewVo vo = new GzAcceptanceRefPreviewVo();
        vo.setApplyDepartmentId(order.getApplyDepartmentId());

        List<GzOrderEntryInhospitalcodeList> allBarcodes =
            gzOrderMapper.selectInhospitalcodeListByParentId(acceptanceOrderId);
        if (allBarcodes == null)
        {
            allBarcodes = new ArrayList<>();
        }

        List<GzDepotInventory> depotLines = listAcceptanceDepotLines(acceptanceOrderId, warehouseId);
        Set<String> availableLineIds = new HashSet<>();
        for (GzDepotInventory di : depotLines)
        {
            if (di != null && di.getInhospitalcodeListId() != null)
            {
                availableLineIds.add(String.valueOf(di.getInhospitalcodeListId()));
            }
        }

        List<String> barcodeLineIds = new ArrayList<>();
        for (GzOrderEntryInhospitalcodeList ic : allBarcodes)
        {
            if (ic != null && ic.getId() != null)
            {
                barcodeLineIds.add(String.valueOf(ic.getId()));
            }
        }
        String excludeMain = excludeShipmentId != null ? String.valueOf(excludeShipmentId) : null;
        Map<String, Map<String, Object>> occupiedMap = new HashMap<>();
        if (!barcodeLineIds.isEmpty())
        {
            List<Map<String, Object>> occupiedRows = gzOrderEntryCodeRefMapper.selectOccupiedByBarcodeLineIds(
                barcodeLineIds, GzLineRefWriteService.KIND_SHIPMENT, excludeMain);
            if (occupiedRows != null)
            {
                for (Map<String, Object> row : occupiedRows)
                {
                    if (row != null && row.get("barcodeLineId") != null)
                    {
                        occupiedMap.put(String.valueOf(row.get("barcodeLineId")), row);
                    }
                }
            }
        }

        List<GzDepotInventory> available = new ArrayList<>();
        for (GzDepotInventory di : depotLines)
        {
            if (di == null || di.getInhospitalcodeListId() == null)
            {
                continue;
            }
            String lid = String.valueOf(di.getInhospitalcodeListId());
            if (!occupiedMap.containsKey(lid))
            {
                available.add(di);
            }
        }
        vo.setAvailableLines(available);

        List<GzAcceptanceRefMissingBarcodeVo> missing = new ArrayList<>();
        for (GzOrderEntryInhospitalcodeList ic : allBarcodes)
        {
            if (ic == null || ic.getId() == null)
            {
                continue;
            }
            String lid = String.valueOf(ic.getId());
            if (occupiedMap.containsKey(lid))
            {
                continue;
            }
            if (!availableLineIds.contains(lid))
            {
                GzAcceptanceRefMissingBarcodeVo m = new GzAcceptanceRefMissingBarcodeVo();
                m.setBarcodeLineId(lid);
                m.setInHospitalCode(ic.getInHospitalCode());
                m.setMaterialName(ic.getMaterialName());
                missing.add(m);
            }
        }
        vo.setMissingBarcodes(missing);
        return vo;
    }

    @Override
    public List<GzDepotInventory> listAcceptanceDepotLines(Long acceptanceOrderId, Long warehouseId)
    {
        if (acceptanceOrderId == null || warehouseId == null)
        {
            return new ArrayList<>();
        }
        GzDepotInventory q = new GzDepotInventory();
        q.setOrderId(acceptanceOrderId);
        q.setWarehouseId(warehouseId);
        List<GzDepotInventory> list = gzDepotInventoryMapper.selectGzDepotInventoryList(q);
        if (list == null)
        {
            return new ArrayList<>();
        }
        List<GzDepotInventory> out = new ArrayList<>();
        for (GzDepotInventory row : list)
        {
            if (row != null && row.getQty() != null && row.getQty().compareTo(BigDecimal.ZERO) > 0)
            {
                out.add(row);
            }
        }
        return out;
    }

    @Override
    public List<GzShipment> listAuditedShipment(GzShipment query)
    {
        if (query == null)
        {
            query = new GzShipment();
        }
        query.setShipmentStatus(2);
        return gzShipmentMapper.selectGzShipmentList(query);
    }

    @Override
    public List<GzShipmentEntry> listShipmentLinesForTk(Long shipmentId, Long departmentId)
    {
        if (shipmentId == null || departmentId == null)
        {
            return new ArrayList<>();
        }
        GzShipment sh = gzShipmentMapper.selectGzShipmentById(shipmentId);
        if (sh == null || sh.getGzShipmentEntryList() == null)
        {
            return new ArrayList<>();
        }
        List<GzShipmentEntry> out = new ArrayList<>();
        for (GzShipmentEntry e : sh.getGzShipmentEntryList())
        {
            if (e == null || StringUtils.isEmpty(e.getInHospitalCode()))
            {
                continue;
            }
            com.spd.gz.domain.GzDepInventory dep = gzDepInventoryMapper.selectGzDepInventoryByCodeAndDept(
                e.getInHospitalCode().trim(), departmentId);
            if (dep != null && dep.getQty() != null && dep.getQty().compareTo(BigDecimal.ZERO) > 0)
            {
                out.add(e);
            }
        }
        return out;
    }
}

package com.spd.gz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.spd.common.utils.StringUtils;
import com.spd.gz.domain.GzDepInventory;
import com.spd.gz.domain.GzDepotInventory;
import com.spd.gz.domain.GzOrder;
import com.spd.gz.domain.GzShipment;
import com.spd.gz.domain.GzShipmentEntry;
import com.spd.gz.mapper.GzDepInventoryMapper;
import com.spd.gz.mapper.GzDepotInventoryMapper;
import com.spd.gz.mapper.GzOrderMapper;
import com.spd.gz.mapper.GzShipmentMapper;
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

    @Override
    public List<GzOrder> listAuditedAcceptance(GzOrder query)
    {
        if (query == null)
        {
            query = new GzOrder();
        }
        query.setOrderType(101);
        query.setOrderStatus(2);
        return gzOrderMapper.selectGzOrderList(query);
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
            GzDepInventory dep = gzDepInventoryMapper.selectGzDepInventoryByCodeAndDept(
                e.getInHospitalCode().trim(), departmentId);
            if (dep != null && dep.getQty() != null && dep.getQty().compareTo(BigDecimal.ZERO) > 0)
            {
                out.add(e);
            }
        }
        return out;
    }
}

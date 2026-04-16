package com.spd.gz.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.spd.common.exception.GzInventoryValidationException;
import com.spd.common.utils.StringUtils;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.gz.domain.GzDepInventory;
import com.spd.gz.domain.GzDepotInventory;
import com.spd.gz.domain.GzRefundGoods;
import com.spd.gz.domain.GzRefundGoodsEntry;
import com.spd.gz.domain.GzShipment;
import com.spd.gz.domain.GzShipmentEntry;
import com.spd.gz.domain.vo.GzInventoryValidateLine;
import com.spd.gz.mapper.GzDepInventoryMapper;
import com.spd.gz.mapper.GzDepotInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 高值备货出库/退库/退货库存校验（不通过则抛出 {@link GzInventoryValidationException}）
 */
@Service
public class GzStockValidationService
{
    @Autowired
    private GzDepotInventoryMapper gzDepotInventoryMapper;

    @Autowired
    private GzDepInventoryMapper gzDepInventoryMapper;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    public void assertShipmentOutbound(GzShipment bill, List<GzShipmentEntry> entries)
    {
        if (entries == null || entries.isEmpty())
        {
            return;
        }
        Long whId = bill != null ? bill.getWarehouseId() : null;
        List<GzInventoryValidateLine> errs = new ArrayList<>();
        int idx = 0;
        for (GzShipmentEntry e : entries)
        {
            idx++;
            if (e == null)
            {
                continue;
            }
            BigDecimal qty = e.getQty();
            if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            if (whId == null)
            {
                addLine(errs, idx, e, "表头仓库为空，无法校验备货库存");
                continue;
            }
            if (e.getMaterialId() == null || StringUtils.isEmpty(e.getBatchNo()))
            {
                addLine(errs, idx, e, "物料或批次号为空，无法校验备货库存");
                continue;
            }
            GzDepotInventory q = new GzDepotInventory();
            q.setWarehouseId(whId);
            q.setMaterialId(e.getMaterialId());
            q.setBatchNo(e.getBatchNo());
            List<GzDepotInventory> rows = gzDepotInventoryMapper.selectGzDepotInventoryList(q);
            BigDecimal sum = BigDecimal.ZERO;
            if (rows != null)
            {
                for (GzDepotInventory r : rows)
                {
                    if (r != null && r.getQty() != null && r.getQty().compareTo(BigDecimal.ZERO) > 0)
                    {
                        sum = sum.add(r.getQty());
                    }
                }
            }
            if (StringUtils.isNotEmpty(e.getInHospitalCode()))
            {
                GzDepotInventory one = gzDepotInventoryMapper.selectByInHospitalCodeAndWarehouse(
                    e.getInHospitalCode().trim(), whId);
                if (one == null || one.getQty() == null || one.getQty().compareTo(BigDecimal.ZERO) <= 0)
                {
                    addLine(errs, idx, e, "该院内码在出库仓库无可用备货库存或库存为0");
                }
                else if (!whId.equals(one.getWarehouseId()))
                {
                    addLine(errs, idx, e, "备货库存不属于当前出库仓库");
                }
                else if (one.getQty().compareTo(qty) < 0)
                {
                    addLine(errs, idx, e, "该院内码可用数量不足，当前可用 " + one.getQty() + "，出库数量 " + qty);
                }
            }
            else
            {
                if (rows == null || rows.isEmpty())
                {
                    addLine(errs, idx, e, "出库仓库不存在该批次/物料的备货库存");
                }
                else if (sum.compareTo(qty) < 0)
                {
                    addLine(errs, idx, e, "备货库存数量不足，当前可出 " + sum + "，申请出库 " + qty);
                }
            }
        }
        if (!errs.isEmpty())
        {
            throw new GzInventoryValidationException("备货出库库存校验未通过，请查看明细", errs);
        }
    }

    public void assertRefundTk(GzRefundGoods bill, List<GzRefundGoodsEntry> entries)
    {
        if (entries == null || entries.isEmpty())
        {
            return;
        }
        if (bill.getDepartmentId() == null)
        {
            throw new GzInventoryValidationException("请选择退库科室", java.util.Collections.emptyList());
        }
        if (bill.getWarehouseId() == null)
        {
            throw new GzInventoryValidationException("请选择退库仓库", java.util.Collections.emptyList());
        }
        List<GzInventoryValidateLine> errs = new ArrayList<>();
        int idx = 0;
        for (GzRefundGoodsEntry e : entries)
        {
            idx++;
            if (e == null)
            {
                continue;
            }
            BigDecimal qty = e.getQty();
            if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            if (StringUtils.isEmpty(e.getInHospitalCode()))
            {
                addLineGoods(errs, idx, e, "院内码为空，无法校验科室库存");
                continue;
            }
            String code = e.getInHospitalCode().trim();
            GzDepInventory dep = gzDepInventoryMapper.selectGzDepInventoryByCodeAndDept(code, bill.getDepartmentId());
            if (dep == null || dep.getQty() == null)
            {
                addLineGoods(errs, idx, e, "科室库存中不存在该院内码");
                continue;
            }
            if (dep.getQty().compareTo(qty) < 0)
            {
                addLineGoods(errs, idx, e, "科室库存数量不足，现存 " + dep.getQty() + "，申请退库 " + qty);
            }
            GzDepotInventory depotWh = gzDepotInventoryMapper.selectLatestDepotByInHospitalCodeAndWarehouse(code, bill.getWarehouseId());
            if (depotWh == null)
            {
                addLineGoods(errs, idx, e, "退库仓库无该院内码对应的备货库存记录，无法退回仓库");
            }
            else if (depotWh.getWarehouseId() != null && !depotWh.getWarehouseId().equals(bill.getWarehouseId()))
            {
                addLineGoods(errs, idx, e, "备货库存记录不属于当前退库仓库");
            }
        }
        if (!errs.isEmpty())
        {
            throw new GzInventoryValidationException("备货退库库存校验未通过，请查看明细", errs);
        }
    }

    public void assertRefundTh(GzRefundGoods bill, List<GzRefundGoodsEntry> entries)
    {
        if (entries == null || entries.isEmpty())
        {
            return;
        }
        if (bill.getWarehouseId() == null)
        {
            throw new GzInventoryValidationException("请选择退货仓库", java.util.Collections.emptyList());
        }
        if (bill.getSupplerId() == null)
        {
            throw new GzInventoryValidationException("请选择表头供应商", java.util.Collections.emptyList());
        }
        List<GzInventoryValidateLine> errs = new ArrayList<>();
        int idx = 0;
        for (GzRefundGoodsEntry e : entries)
        {
            idx++;
            if (e == null)
            {
                continue;
            }
            BigDecimal qty = e.getQty();
            if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            if (StringUtils.isEmpty(e.getBatchNo()))
            {
                addLineGoods(errs, idx, e, "批次号为空，无法校验备货库存");
                continue;
            }
            GzDepotInventory inv = gzDepotInventoryMapper.selectGzDepotInventoryOneByBatchNoAndWarehouse(e.getBatchNo(), bill.getWarehouseId());
            if (inv == null)
            {
                addLineGoods(errs, idx, e, "退货仓库不存在该批次的备货库存");
                continue;
            }
            if (inv.getWarehouseId() != null && !inv.getWarehouseId().equals(bill.getWarehouseId()))
            {
                addLineGoods(errs, idx, e, "备货库存不属于当前退货仓库");
            }
            BigDecimal iq = inv.getQty() != null ? inv.getQty() : BigDecimal.ZERO;
            if (iq.compareTo(qty) < 0)
            {
                addLineGoods(errs, idx, e, "备货库存数量不足，当前 " + iq + "，申请退货 " + qty);
            }
            if (inv.getSupplierId() != null && !inv.getSupplierId().equals(bill.getSupplerId()))
            {
                addLineGoods(errs, idx, e, "备货库存所属供应商与表头供应商不一致");
            }
        }
        if (!errs.isEmpty())
        {
            throw new GzInventoryValidationException("备货退货库存校验未通过，请查看明细", errs);
        }
    }

    private void addLine(List<GzInventoryValidateLine> errs, int idx, GzShipmentEntry e, String reason)
    {
        GzInventoryValidateLine l = new GzInventoryValidateLine();
        l.setLineNo(idx);
        l.setInHospitalCode(e.getInHospitalCode());
        l.setBatchNo(e.getBatchNo());
        l.setMaterialName(resolveMaterialName(e.getMaterialId()));
        l.setReason(reason);
        errs.add(l);
    }

    private void addLineGoods(List<GzInventoryValidateLine> errs, int idx, GzRefundGoodsEntry e, String reason)
    {
        GzInventoryValidateLine l = new GzInventoryValidateLine();
        l.setLineNo(idx);
        l.setInHospitalCode(e.getInHospitalCode());
        l.setBatchNo(e.getBatchNo());
        l.setMaterialName(resolveMaterialName(e.getMaterialId()));
        l.setReason(reason);
        errs.add(l);
    }

    private String resolveMaterialName(Long materialId)
    {
        if (materialId == null)
        {
            return null;
        }
        try
        {
            com.spd.foundation.domain.FdMaterial m = fdMaterialMapper.selectFdMaterialById(materialId);
            return m != null ? m.getName() : null;
        }
        catch (Exception ex)
        {
            return null;
        }
    }
}

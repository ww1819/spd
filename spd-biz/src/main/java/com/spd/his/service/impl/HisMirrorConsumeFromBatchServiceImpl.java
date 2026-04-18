package com.spd.his.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.HisMatchTextUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.uuid.IdUtils;
import com.spd.department.domain.DeptBatchConsume;
import com.spd.department.domain.DeptBatchConsumeEntry;
import com.spd.department.domain.StkDepInventory;
import com.spd.department.mapper.StkDepInventoryMapper;
import com.spd.department.service.IDeptBatchConsumeService;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdDepartmentMapper;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.his.domain.HisChargeFetchBatch;
import com.spd.his.domain.HisInpatientChargeMirror;
import com.spd.his.domain.HisMirrorConsumeLink;
import com.spd.his.domain.HisOutpatientChargeMirror;
import com.spd.his.domain.dto.HisGenerateConsumeBody;
import com.spd.his.domain.dto.HisGenerateConsumeResultVo;
import com.spd.his.mapper.HisChargeFetchBatchMapper;
import com.spd.his.mapper.HisInpatientChargeMirrorMapper;
import com.spd.his.mapper.HisMirrorConsumeLinkMapper;
import com.spd.his.mapper.HisOutpatientChargeMirrorMapper;
import com.spd.his.service.IHisMirrorConsumeFromBatchService;

@Service
public class HisMirrorConsumeFromBatchServiceImpl implements IHisMirrorConsumeFromBatchService
{
    private static final String KIND_IN = "INPATIENT";
    private static final String KIND_OUT = "OUTPATIENT";
    private static final String STATUS_PENDING = "PENDING_CONSUME";
    private static final String STATUS_CONSUMED = "CONSUMED";
    private static final String BILL_SOURCE = "HIS_MIRROR_BATCH";

    @Autowired
    private HisChargeFetchBatchMapper hisChargeFetchBatchMapper;
    @Autowired
    private HisInpatientChargeMirrorMapper hisInpatientChargeMirrorMapper;
    @Autowired
    private HisOutpatientChargeMirrorMapper hisOutpatientChargeMirrorMapper;
    @Autowired
    private FdDepartmentMapper fdDepartmentMapper;
    @Autowired
    private FdMaterialMapper fdMaterialMapper;
    @Autowired
    private StkDepInventoryMapper stkDepInventoryMapper;
    @Autowired
    private IDeptBatchConsumeService deptBatchConsumeService;
    @Autowired
    private HisMirrorConsumeLinkMapper hisMirrorConsumeLinkMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HisGenerateConsumeResultVo generateFromFetchBatch(HisGenerateConsumeBody body)
    {
        throw new ServiceException("已改为按明细手动处理：请在「患者收费查询」明细行使用「低值处理」或「高值处理」，不再支持按批次一键生成。");
    }

    private void flushLinks(List<HisMirrorConsumeLink> linkBuffer)
    {
        final int n = 80;
        for (int i = 0; i < linkBuffer.size(); i += n)
        {
            int end = Math.min(i + n, linkBuffer.size());
            hisMirrorConsumeLinkMapper.insertBatch(linkBuffer.subList(i, end));
        }
    }

    /**
     * @return 因数量为 0 跳过的行数（0 或 1）
     */
    private int appendPiecesForLine(String tenantId, String fetchBatchId, String visitKind, String mirrorRowId,
        String deptHisCode, String chargeItemId, BigDecimal qty, List<AllocPiece> out)
    {
        if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0)
        {
            return 1;
        }
        if (StringUtils.isBlank(deptHisCode))
        {
            throw new ServiceException("镜像行「" + mirrorRowId + "」缺少科室/就诊对照编码");
        }
        if (StringUtils.isBlank(chargeItemId))
        {
            throw new ServiceException("镜像行「" + mirrorRowId + "」缺少 charge_item_id");
        }
        FdDepartment dept = fdDepartmentMapper.selectFdDepartmentByTenantAndHisId(tenantId, HisMatchTextUtils.normalizeMatchKey(deptHisCode));
        if (dept == null || dept.getId() == null)
        {
            throw new ServiceException("镜像行「" + mirrorRowId + "」无法匹配科室（HIS编码「" + deptHisCode + "」）");
        }
        FdMaterial mat = fdMaterialMapper.selectFdMaterialByTenantAndHisId(tenantId, HisMatchTextUtils.normalizeMatchKey(chargeItemId));
        if (mat == null || mat.getId() == null)
        {
            throw new ServiceException("镜像行「" + mirrorRowId + "」无法匹配耗材档案（charge_item_id=" + chargeItemId + "）");
        }
        out.addAll(allocateLine(tenantId, dept.getId(), mat.getId(), qty, mirrorRowId, visitKind, fetchBatchId));
        return 0;
    }

    private List<AllocPiece> allocateLine(String tenantId, Long departmentId, Long materialId, BigDecimal need,
        String mirrorRowId, String visitKind, String fetchBatchId)
    {
        StkDepInventory probe = new StkDepInventory();
        probe.setTenantId(tenantId);
        probe.setDepartmentId(departmentId);
        probe.setMaterialId(materialId);
        probe.setReceiptConfirmStatus(1);
        List<StkDepInventory> rows = stkDepInventoryMapper.selectStkDepInventoryList(probe);
        if (rows == null)
        {
            rows = new ArrayList<>();
        }
        rows.sort(Comparator
            .comparing(StkDepInventory::getWarehouseDate, Comparator.nullsLast(Date::compareTo))
            .thenComparing(r -> r.getId() == null ? 0L : r.getId()));

        Long chosenWarehouseId = null;
        for (StkDepInventory r : rows)
        {
            if (r.getQty() != null && r.getQty().compareTo(BigDecimal.ZERO) > 0)
            {
                chosenWarehouseId = r.getWarehouseId();
                break;
            }
        }
        boolean hasPositive = rows.stream().anyMatch(r -> r.getQty() != null && r.getQty().compareTo(BigDecimal.ZERO) > 0);
        if (!hasPositive)
        {
            throw new ServiceException("科室「" + departmentId + "」耗材「" + materialId + "」无可用科室库存（镜像行 " + mirrorRowId + "）");
        }

        final Long whKey = chosenWarehouseId;
        List<StkDepInventory> filtered = rows.stream()
            .filter(r -> Objects.equals(r.getWarehouseId(), whKey))
            .collect(Collectors.toList());

        BigDecimal remain = need;
        List<AllocPiece> pieces = new ArrayList<>();
        for (StkDepInventory r : filtered)
        {
            if (remain.compareTo(BigDecimal.ZERO) <= 0)
            {
                break;
            }
            if (r.getQty() == null || r.getQty().compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            BigDecimal take = r.getQty().min(remain);
            if (take.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            AllocPiece p = new AllocPiece();
            p.mirrorRowId = mirrorRowId;
            p.visitKind = visitKind;
            p.fetchBatchId = fetchBatchId;
            p.departmentId = departmentId;
            p.inv = r;
            p.take = take;
            pieces.add(p);
            remain = remain.subtract(take);
        }
        if (remain.compareTo(BigDecimal.ZERO) > 0)
        {
            throw new ServiceException("科室库存不足：科室" + departmentId + " 耗材" + materialId + " 需求 " + need + " 镜像行 " + mirrorRowId);
        }
        return pieces;
    }

    private DeptBatchConsumeEntry buildConsumeEntry(AllocPiece p, Long departmentId, String createBy)
    {
        StkDepInventory inv = p.inv;
        DeptBatchConsumeEntry e = new DeptBatchConsumeEntry();
        e.setDepInventoryId(inv.getId());
        e.setKcNo(inv.getKcNo());
        e.setMaterialId(inv.getMaterialId());
        e.setBatchNo(inv.getBatchNo());
        e.setBatchNumer(inv.getBatchNumber());
        e.setBatchId(inv.getBatchId());
        e.setWarehouseId(inv.getWarehouseId());
        e.setDepartmentId(departmentId);
        e.setSupplierId(inv.getSupplierId());
        e.setFactoryId(inv.getFactoryId());
        e.setMaterialNo(inv.getMaterialNo());
        e.setQty(p.take);
        BigDecimal up = inv.getUnitPrice() != null ? inv.getUnitPrice() : BigDecimal.ZERO;
        e.setUnitPrice(up);
        e.setPrice(up);
        e.setAmt(up.multiply(p.take));
        e.setBeginTime(inv.getBeginDate());
        e.setEndTime(inv.getEndDate());
        e.setMaterialDate(inv.getMaterialDate());
        e.setWarehouseDate(inv.getWarehouseDate());
        e.setSettlementType(inv.getSettlementType());
        e.setMaterialName(inv.getSnapMaterialName());
        e.setMaterialSpeci(inv.getSnapMaterialSpeci());
        e.setMaterialModel(inv.getSnapMaterialModel());
        e.setMaterialFactoryId(inv.getSnapMaterialFactoryId());
        e.setMainBarcode(inv.getMainBarcode());
        e.setSubBarcode(inv.getSubBarcode());
        e.setRemark("HIS镜像:" + p.mirrorRowId);
        e.setCreateBy(createBy);
        return e;
    }

    private static class AllocPiece
    {
        String mirrorRowId;
        String visitKind;
        String fetchBatchId;
        Long departmentId;
        StkDepInventory inv;
        BigDecimal take;
    }
}

package com.spd.gz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.foundation.domain.FdWarehouse;
import com.spd.foundation.mapper.FdWarehouseMapper;
import com.spd.gz.domain.GzHighConsumeConfirmBill;
import com.spd.gz.domain.dto.GzHighChargeConfirmQuery;
import com.spd.gz.domain.dto.GzHighChargeConfirmResultVo;
import com.spd.gz.domain.dto.GzHighChargeConfirmRowVo;
import com.spd.gz.domain.dto.GzInstantIoAuditBody;
import com.spd.gz.domain.dto.GzInstantIoReverseBody;
import com.spd.gz.mapper.GzHighConsumeConfirmMapper;
import com.spd.gz.service.IGzHighChargeConfirmService;
import com.spd.gz.service.IGzInstantIoService;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.domain.StkIoBillEntry;
import com.spd.warehouse.service.IStkIoBillService;

/**
 * 库房高值即入即出：审核建 G-RK/G-CK；人工反向建 301/401（HV-F-003 / HV-F-004 / HV-B-003）。
 */
@Service
public class GzInstantIoServiceImpl implements IGzInstantIoService
{
    private static final String PREFIX_IN = "G-RK";
    private static final String PREFIX_OUT = "G-CK";
    private static final String PREFIX_TH = "G-TH";
    private static final String PREFIX_TK = "G-TK";
    private static final int BILL_TYPE_IN = 101;
    private static final int BILL_TYPE_OUT = 201;
    private static final int BILL_TYPE_RETURN_GOODS = 301;
    private static final int BILL_TYPE_RETURN_DEPOT = 401;

    @Autowired
    private IGzHighChargeConfirmService gzHighChargeConfirmService;
    @Autowired
    private GzHighConsumeConfirmMapper gzHighConsumeConfirmMapper;
    @Autowired
    private FdWarehouseMapper fdWarehouseMapper;
    @Autowired
    private IStkIoBillService stkIoBillService;

    @Override
    public List<GzHighChargeConfirmRowVo> selectList(GzHighChargeConfirmQuery query)
    {
        if (query == null)
        {
            query = new GzHighChargeConfirmQuery();
        }
        // 库房页只看临床已确认
        if (StringUtils.isBlank(query.getConfirmStatus()))
        {
            query.setConfirmStatus("1");
        }
        return gzHighChargeConfirmService.selectConfirmList(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GzHighChargeConfirmResultVo audit(GzInstantIoAuditBody body)
    {
        if (body == null || body.getLinkIds() == null || body.getLinkIds().isEmpty())
        {
            throw new ServiceException("请选择要审核的明细");
        }
        if (body.getWarehouseId() == null)
        {
            throw new ServiceException("请选择结算仓库");
        }
        String tenantId = requireTenant();
        FdWarehouse wh = fdWarehouseMapper.selectFdWarehouseById(String.valueOf(body.getWarehouseId()));
        if (wh == null || wh.getId() == null)
        {
            throw new ServiceException("仓库不存在");
        }
        if (!Integer.valueOf(1).equals(wh.getIsSettlementWarehouse()))
        {
            throw new ServiceException("请选择已标识为结算仓库的仓库");
        }
        List<String> linkIds = body.getLinkIds().stream()
            .filter(StringUtils::isNotBlank).map(String::trim).distinct().collect(Collectors.toList());
        List<GzHighChargeConfirmRowVo> lines = gzHighConsumeConfirmMapper.selectConfirmLineDetailsByLinkIds(tenantId, linkIds);
        if (lines == null || lines.size() != linkIds.size())
        {
            throw new ServiceException("部分明细不存在或不是高值核销记录");
        }
        for (GzHighChargeConfirmRowVo row : lines)
        {
            if (row.getConfirmStatus() == null || row.getConfirmStatus() != 1)
            {
                throw new ServiceException("存在未临床确认的明细，无法审核");
            }
            if (row.getInstantIoAuditStatus() != null && row.getInstantIoAuditStatus() != 0)
            {
                throw new ServiceException("存在已审核或已冲销的明细，请刷新后重选");
            }
            if (StringUtils.isBlank(row.getConfirmId()))
            {
                throw new ServiceException("明细缺少确认批次，无法审核");
            }
            if (StringUtils.isBlank(row.getSupplierId()))
            {
                throw new ServiceException("耗材【" + StringUtils.defaultString(row.getMaterialName(), "未知")
                    + "】缺少供应商，无法审核");
            }
        }
        Long departmentId = resolveSingleDepartment(lines);
        Date auditTime = DateUtils.getNowDate();
        String userId = SecurityUtils.getUserIdStr();

        Map<String, List<GzHighChargeConfirmRowVo>> bySupplier = lines.stream()
            .collect(Collectors.groupingBy(r -> r.getSupplierId().trim(), LinkedHashMap::new, Collectors.toList()));

        List<GzHighConsumeConfirmBill> billRefs = new ArrayList<>();
        GzHighChargeConfirmResultVo result = new GzHighChargeConfirmResultVo();
        result.setLineCount(lines.size());
        // 同一批审核可能跨多个确认批次：按确认批次回写仓库
        Set<String> confirmIds = lines.stream().map(GzHighChargeConfirmRowVo::getConfirmId)
            .filter(StringUtils::isNotBlank).collect(Collectors.toCollection(LinkedHashSet::new));
        for (String cid : confirmIds)
        {
            gzHighConsumeConfirmMapper.updateConfirmWarehouse(tenantId, cid, body.getWarehouseId(), userId);
        }
        if (confirmIds.size() == 1)
        {
            result.setConfirmId(confirmIds.iterator().next());
        }

        for (Map.Entry<String, List<GzHighChargeConfirmRowVo>> en : bySupplier.entrySet())
        {
            String supplierId = en.getKey();
            List<GzHighChargeConfirmRowVo> group = en.getValue();
            String supplierName = group.stream().map(GzHighChargeConfirmRowVo::getSupplierName)
                .filter(StringUtils::isNotBlank).findFirst().orElse(supplierId);
            // 同一供应商若跨确认批次，按确认批次拆单，避免 bill 关联混乱
            Map<String, List<GzHighChargeConfirmRowVo>> byConfirm = group.stream()
                .collect(Collectors.groupingBy(GzHighChargeConfirmRowVo::getConfirmId, LinkedHashMap::new, Collectors.toList()));
            for (Map.Entry<String, List<GzHighChargeConfirmRowVo>> ce : byConfirm.entrySet())
            {
                String confirmId = ce.getKey();
                List<GzHighChargeConfirmRowVo> cg = ce.getValue();
                StkIoBill inBill = buildSettlementBill(tenantId, BILL_TYPE_IN, body.getWarehouseId(), null,
                    supplierId, auditTime, userId, cg, "高值即入即出审核");
                Long inId = stkIoBillService.insertHighValueSettlementBill(inBill, PREFIX_IN);
                StkIoBill outBill = buildSettlementBill(tenantId, BILL_TYPE_OUT, body.getWarehouseId(), departmentId,
                    supplierId, auditTime, userId, cg, "高值即入即出审核");
                Long outId = stkIoBillService.insertHighValueSettlementBill(outBill, PREFIX_OUT);
                billRefs.add(newBillRef(tenantId, confirmId, supplierId, BILL_TYPE_IN, inId, inBill.getBillNo(), auditTime));
                billRefs.add(newBillRef(tenantId, confirmId, supplierId, BILL_TYPE_OUT, outId, outBill.getBillNo(), auditTime));

                GzHighChargeConfirmResultVo.GzHighChargeConfirmBillVo bv = new GzHighChargeConfirmResultVo.GzHighChargeConfirmBillVo();
                bv.setSupplierId(supplierId);
                bv.setSupplierName(supplierName);
                bv.setInboundBillNo(inBill.getBillNo());
                bv.setInboundBillId(inId);
                bv.setOutboundBillNo(outBill.getBillNo());
                bv.setOutboundBillId(outId);
                result.getBills().add(bv);
            }
        }
        gzHighConsumeConfirmMapper.insertConfirmBillBatch(billRefs);

        int updated = gzHighConsumeConfirmMapper.updateLinkInstantIoAudited(tenantId, linkIds, userId);
        if (updated != linkIds.size())
        {
            throw new ServiceException("部分明细已被他人审核，请刷新后重试");
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GzHighChargeConfirmResultVo reverse(GzInstantIoReverseBody body)
    {
        if (body == null || body.getLinkIds() == null || body.getLinkIds().isEmpty())
        {
            throw new ServiceException("请选择要生成反向单据的明细");
        }
        String tenantId = requireTenant();
        List<String> linkIds = body.getLinkIds().stream()
            .filter(StringUtils::isNotBlank).map(String::trim).distinct().collect(Collectors.toList());
        List<GzHighChargeConfirmRowVo> lines = gzHighConsumeConfirmMapper.selectConfirmLineDetailsByLinkIds(tenantId, linkIds);
        if (lines == null || lines.size() != linkIds.size())
        {
            throw new ServiceException("部分明细不存在或不是高值核销记录");
        }
        for (GzHighChargeConfirmRowVo row : lines)
        {
            if (row.getConfirmStatus() == null || row.getConfirmStatus() != 1)
            {
                throw new ServiceException("存在未临床确认的明细，无法生成反向单据");
            }
            if (row.getInstantIoAuditStatus() == null || row.getInstantIoAuditStatus() != 1)
            {
                throw new ServiceException("仅已审核且未冲销的明细可生成退货/退库单");
            }
            if (StringUtils.isBlank(row.getConfirmId()))
            {
                throw new ServiceException("明细缺少确认批次");
            }
        }
        Set<String> confirmIds = lines.stream().map(GzHighChargeConfirmRowVo::getConfirmId)
            .filter(StringUtils::isNotBlank).collect(Collectors.toCollection(LinkedHashSet::new));
        List<GzHighConsumeConfirmBill> existing = gzHighConsumeConfirmMapper.selectBillsByConfirmIds(tenantId, new ArrayList<>(confirmIds));
        if (existing == null || existing.isEmpty())
        {
            throw new ServiceException("未找到原入出库结算单，无法生成反向单据");
        }
        Map<String, List<GzHighConsumeConfirmBill>> billsByConfirm = existing.stream()
            .collect(Collectors.groupingBy(GzHighConsumeConfirmBill::getConfirmId, LinkedHashMap::new, Collectors.toList()));

        Date now = DateUtils.getNowDate();
        String userId = SecurityUtils.getUserIdStr();
        List<GzHighConsumeConfirmBill> newRefs = new ArrayList<>();
        GzHighChargeConfirmResultVo result = new GzHighChargeConfirmResultVo();
        result.setLineCount(lines.size());

        // 按确认批次+供应商对原 101/201 生成 301/401；仓库取原单
        for (String confirmId : confirmIds)
        {
            List<GzHighConsumeConfirmBill> cbs = billsByConfirm.getOrDefault(confirmId, java.util.Collections.emptyList());
            boolean alreadyReversed = cbs.stream().anyMatch(b -> b.getBillType() != null
                && (b.getBillType() == BILL_TYPE_RETURN_GOODS || b.getBillType() == BILL_TYPE_RETURN_DEPOT));
            if (alreadyReversed)
            {
                throw new ServiceException("确认批次已存在退货/退库单，请勿重复生成");
            }
            Map<String, List<GzHighConsumeConfirmBill>> bySupplier = cbs.stream()
                .filter(b -> b.getBillType() != null && (b.getBillType() == BILL_TYPE_IN || b.getBillType() == BILL_TYPE_OUT))
                .collect(Collectors.groupingBy(b -> StringUtils.defaultString(b.getSupplierId(), ""), LinkedHashMap::new, Collectors.toList()));
            for (Map.Entry<String, List<GzHighConsumeConfirmBill>> en : bySupplier.entrySet())
            {
                String supplierId = en.getKey();
                GzHighConsumeConfirmBill inRef = en.getValue().stream()
                    .filter(b -> Integer.valueOf(BILL_TYPE_IN).equals(b.getBillType())).findFirst().orElse(null);
                GzHighConsumeConfirmBill outRef = en.getValue().stream()
                    .filter(b -> Integer.valueOf(BILL_TYPE_OUT).equals(b.getBillType())).findFirst().orElse(null);
                if (inRef == null || outRef == null)
                {
                    throw new ServiceException("确认批次缺少成对的入库/出库单，无法反向");
                }
                StkIoBill srcIn = stkIoBillService.selectStkIoBillById(inRef.getStkIoBillId());
                StkIoBill srcOut = stkIoBillService.selectStkIoBillById(outRef.getStkIoBillId());
                if (srcIn == null || srcOut == null)
                {
                    throw new ServiceException("原入出库单不存在，无法反向");
                }
                Long warehouseId = srcIn.getWarehouseId() != null ? srcIn.getWarehouseId() : srcOut.getWarehouseId();
                if (warehouseId == null)
                {
                    throw new ServiceException("原入出库单缺少仓库，无法反向");
                }
                StkIoBill thBill = buildReverseFromSource(tenantId, BILL_TYPE_RETURN_GOODS, warehouseId,
                    srcIn.getDepartmentId(), supplierId, now, userId, srcIn, "高值即入即出退货");
                Long thId = stkIoBillService.insertHighValueSettlementBill(thBill, PREFIX_TH);
                StkIoBill tkBill = buildReverseFromSource(tenantId, BILL_TYPE_RETURN_DEPOT, warehouseId,
                    srcOut.getDepartmentId(), supplierId, now, userId, srcOut, "高值即入即出退库");
                Long tkId = stkIoBillService.insertHighValueSettlementBill(tkBill, PREFIX_TK);
                newRefs.add(newBillRef(tenantId, confirmId, supplierId, BILL_TYPE_RETURN_GOODS, thId, thBill.getBillNo(), now));
                newRefs.add(newBillRef(tenantId, confirmId, supplierId, BILL_TYPE_RETURN_DEPOT, tkId, tkBill.getBillNo(), now));

                GzHighChargeConfirmResultVo.GzHighChargeConfirmBillVo bv = new GzHighChargeConfirmResultVo.GzHighChargeConfirmBillVo();
                bv.setSupplierId(supplierId);
                bv.setReturnGoodsBillNo(thBill.getBillNo());
                bv.setReturnGoodsBillId(thId);
                bv.setReturnDepotBillNo(tkBill.getBillNo());
                bv.setReturnDepotBillId(tkId);
                bv.setInboundBillNo(inRef.getBillNo());
                bv.setInboundBillId(inRef.getStkIoBillId());
                bv.setOutboundBillNo(outRef.getBillNo());
                bv.setOutboundBillId(outRef.getStkIoBillId());
                result.getBills().add(bv);
            }
        }
        if (!newRefs.isEmpty())
        {
            gzHighConsumeConfirmMapper.insertConfirmBillBatch(newRefs);
        }
        // 同一确认批次共享入出库单，反向后整批标记已冲销
        int updated = gzHighConsumeConfirmMapper.updateLinkInstantIoReversedByConfirmIds(
            tenantId, new ArrayList<>(confirmIds), userId);
        if (updated <= 0)
        {
            throw new ServiceException("明细状态已变更，请刷新后重试");
        }
        return result;
    }

    private StkIoBill buildReverseFromSource(String tenantId, int billType, Long warehouseId, Long departmentId,
        String supplierId, Date billTime, String userId, StkIoBill source, String remark)
    {
        StkIoBill bill = new StkIoBill();
        bill.setTenantId(tenantId);
        bill.setBillType(billType);
        bill.setBillStatus(2);
        bill.setWarehouseId(warehouseId);
        bill.setDepartmentId(departmentId);
        bill.setBillDate(billTime);
        bill.setAuditDate(billTime);
        bill.setAuditBy(userId);
        bill.setUserId(SecurityUtils.getUserId());
        bill.setCreateBy(userId);
        bill.setRemark(remark + " 原单=" + StringUtils.defaultString(source.getBillNo()));
        if (StringUtils.isNotBlank(supplierId))
        {
            try
            {
                bill.setSupplerId(Long.parseLong(supplierId.trim()));
            }
            catch (NumberFormatException ignored)
            {
            }
        }
        List<StkIoBillEntry> entries = new ArrayList<>();
        if (source.getStkIoBillEntryList() != null)
        {
            for (StkIoBillEntry src : source.getStkIoBillEntryList())
            {
                if (src == null || (src.getDelFlag() != null && src.getDelFlag() == 1))
                {
                    continue;
                }
                StkIoBillEntry e = new StkIoBillEntry();
                e.setMaterialId(src.getMaterialId());
                e.setMaterialName(src.getMaterialName());
                e.setMaterialSpeci(src.getMaterialSpeci());
                e.setMaterialModel(src.getMaterialModel());
                e.setUnitPrice(src.getUnitPrice());
                e.setQty(src.getQty());
                e.setPrice(src.getPrice() != null ? src.getPrice() : src.getUnitPrice());
                e.setAmt(src.getAmt());
                if (e.getAmt() == null && e.getQty() != null && e.getUnitPrice() != null)
                {
                    e.setAmt(e.getUnitPrice().multiply(e.getQty()));
                }
                e.setBatchNo(src.getBatchNo());
                e.setBatchNumber(src.getBatchNumber());
                e.setBeginTime(src.getBeginTime());
                e.setEndTime(src.getEndTime());
                e.setMainBarcode(src.getMainBarcode());
                e.setSubBarcode(src.getSubBarcode());
                e.setMaterialFactoryId(src.getMaterialFactoryId());
                e.setSupplerId(src.getSupplerId());
                e.setRemark(remark + " 原明细=" + src.getId());
                entries.add(e);
            }
        }
        if (entries.isEmpty())
        {
            throw new ServiceException("原单【" + source.getBillNo() + "】无明细，无法反向");
        }
        bill.setStkIoBillEntryList(entries);
        return bill;
    }

    private StkIoBill buildSettlementBill(String tenantId, int billType, Long warehouseId, Long departmentId,
        String supplierId, Date confirmTime, String userId, List<GzHighChargeConfirmRowVo> group, String remark)
    {
        StkIoBill bill = new StkIoBill();
        bill.setTenantId(tenantId);
        bill.setBillType(billType);
        bill.setBillStatus(2);
        bill.setWarehouseId(warehouseId);
        bill.setDepartmentId(departmentId);
        bill.setBillDate(confirmTime);
        bill.setAuditDate(confirmTime);
        bill.setAuditBy(userId);
        bill.setUserId(SecurityUtils.getUserId());
        bill.setCreateBy(userId);
        bill.setRemark(remark);
        if (StringUtils.isNotBlank(supplierId))
        {
            try
            {
                bill.setSupplerId(Long.parseLong(supplierId.trim()));
            }
            catch (NumberFormatException ignored)
            {
            }
        }
        List<StkIoBillEntry> entries = new ArrayList<>();
        for (GzHighChargeConfirmRowVo row : group)
        {
            entries.add(toStkEntry(row, remark));
        }
        bill.setStkIoBillEntryList(entries);
        return bill;
    }

    private StkIoBillEntry toStkEntry(GzHighChargeConfirmRowVo row, String remark)
    {
        StkIoBillEntry e = new StkIoBillEntry();
        e.setMaterialId(row.getMaterialId());
        e.setMaterialName(row.getMaterialName());
        e.setMaterialSpeci(row.getMaterialSpeci());
        e.setMaterialModel(row.getMaterialModel());
        e.setUnitPrice(row.getUnitPrice());
        BigDecimal qty = row.getEntryQty() != null ? row.getEntryQty() : row.getAllocQty();
        e.setQty(qty);
        e.setPrice(row.getUnitPrice());
        if (row.getAmt() != null)
        {
            e.setAmt(row.getAmt());
        }
        else if (qty != null && row.getUnitPrice() != null)
        {
            e.setAmt(row.getUnitPrice().multiply(qty));
        }
        e.setBatchNo(row.getBatchNo());
        e.setBatchNumber(row.getBatchNumber());
        e.setBeginTime(row.getBeginTime());
        e.setEndTime(row.getEndTime());
        e.setMainBarcode(row.getMainBarcode());
        e.setSubBarcode(row.getSubBarcode());
        if (row.getFactoryId() != null)
        {
            e.setMaterialFactoryId(row.getFactoryId());
        }
        if (StringUtils.isNotBlank(row.getSupplierId()))
        {
            e.setSupplerId(row.getSupplierId().trim());
        }
        e.setRemark(remark + " link=" + row.getLinkId());
        return e;
    }

    private GzHighConsumeConfirmBill newBillRef(String tenantId, String confirmId, String supplierId,
        int billType, Long stkIoBillId, String billNo, Date createTime)
    {
        GzHighConsumeConfirmBill ref = new GzHighConsumeConfirmBill();
        ref.setId(UUID7.generateUUID7());
        ref.setTenantId(tenantId);
        ref.setConfirmId(confirmId);
        ref.setSupplierId(supplierId);
        ref.setBillType(billType);
        ref.setStkIoBillId(stkIoBillId);
        ref.setBillNo(billNo);
        ref.setDelFlag(0);
        ref.setCreateTime(createTime);
        return ref;
    }

    private Long resolveSingleDepartment(List<GzHighChargeConfirmRowVo> lines)
    {
        List<Long> deptIds = lines.stream().map(GzHighChargeConfirmRowVo::getDepartmentId)
            .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (deptIds.isEmpty())
        {
            throw new ServiceException("核销记录缺少核销科室，无法审核");
        }
        if (deptIds.size() > 1)
        {
            throw new ServiceException("请选择同一核销科室的明细进行审核");
        }
        return deptIds.get(0);
    }

    private String requireTenant()
    {
        String tenantId = SecurityUtils.getCustomerId();
        if (StringUtils.isBlank(tenantId))
        {
            throw new ServiceException("无法解析当前租户");
        }
        return tenantId.trim();
    }
}

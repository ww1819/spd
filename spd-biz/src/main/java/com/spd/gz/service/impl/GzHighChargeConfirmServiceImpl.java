package com.spd.gz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.common.utils.uuid.UUID7;
import com.spd.foundation.domain.FdWarehouse;
import com.spd.foundation.mapper.FdWarehouseMapper;
import com.spd.gz.domain.GzHighConsumeConfirm;
import com.spd.gz.domain.GzHighConsumeConfirmBill;
import com.spd.gz.domain.GzHighConsumeConfirmLine;
import com.spd.gz.domain.dto.GzHighChargeConfirmBody;
import com.spd.gz.domain.dto.GzHighChargeConfirmQuery;
import com.spd.gz.domain.dto.GzHighChargeConfirmResultVo;
import com.spd.gz.domain.dto.GzHighChargeConfirmRowVo;
import com.spd.gz.mapper.GzHighConsumeConfirmMapper;
import com.spd.gz.service.IGzHighChargeConfirmService;
import com.spd.system.service.ITenantScopeService;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.domain.StkIoBillEntry;
import com.spd.warehouse.service.IStkIoBillService;

@Service
public class GzHighChargeConfirmServiceImpl implements IGzHighChargeConfirmService
{
    private static final String PREFIX_CONFIRM = "GZQR";
    private static final String PREFIX_IN = "G-RK";
    private static final String PREFIX_OUT = "G-CK";
    private static final int BILL_TYPE_IN = 101;
    private static final int BILL_TYPE_OUT = 201;
    private static final String MSG_NO_WRITE_OFF_DEPT_PERM =
        "没有核销科室权限，不允许确认。如需确认请添加核销科室权限之后再做确认操作";

    @Autowired
    private GzHighConsumeConfirmMapper gzHighConsumeConfirmMapper;
    @Autowired
    private FdWarehouseMapper fdWarehouseMapper;
    @Autowired
    private IStkIoBillService stkIoBillService;
    @Autowired
    private ITenantScopeService tenantScopeService;

    @Override
    public List<GzHighChargeConfirmRowVo> selectConfirmList(GzHighChargeConfirmQuery query)
    {
        if (query == null)
        {
            query = new GzHighChargeConfirmQuery();
        }
        query.setTenantId(requireTenant());
        return gzHighConsumeConfirmMapper.selectConfirmList(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GzHighChargeConfirmResultVo confirm(GzHighChargeConfirmBody body)
    {
        if (body == null || body.getLinkIds() == null || body.getLinkIds().isEmpty())
        {
            throw new ServiceException("请选择要确认的消耗明细");
        }
        if (body.getWarehouseId() == null)
        {
            throw new ServiceException("请选择仓库");
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
            .filter(StringUtils::isNotBlank)
            .map(String::trim)
            .distinct()
            .collect(Collectors.toList());
        if (linkIds.isEmpty())
        {
            throw new ServiceException("请选择要确认的消耗明细");
        }
        List<GzHighChargeConfirmRowVo> lines = gzHighConsumeConfirmMapper.selectConfirmLineDetailsByLinkIds(tenantId, linkIds);
        if (lines == null || lines.size() != linkIds.size())
        {
            throw new ServiceException("部分明细不存在或不是高值核销记录");
        }
        for (GzHighChargeConfirmRowVo row : lines)
        {
            if (row.getConfirmStatus() != null && row.getConfirmStatus() == 1)
            {
                throw new ServiceException("存在已确认的明细，请刷新后重选");
            }
            if (StringUtils.isBlank(row.getSupplierId()))
            {
                throw new ServiceException("耗材【" + StringUtils.defaultString(row.getMaterialName(), "未知")
                    + "】缺少供应商，无法确认");
            }
        }
        Long departmentId = resolveSingleWriteOffDepartment(lines);
        if (body.getDepartmentId() != null && !body.getDepartmentId().equals(departmentId))
        {
            throw new ServiceException("核销科室与所选明细不一致，请刷新后重试");
        }
        assertWriteOffDepartmentInUserScope(tenantId, departmentId);
        Date confirmTime = DateUtils.getNowDate();
        String userId = SecurityUtils.getUserIdStr();
        String confirmId = UUID7.generateUUID7();
        String confirmNo = nextConfirmNo(tenantId);

        Date periodBegin = lines.stream()
            .map(GzHighChargeConfirmRowVo::getConsumeAuditTime)
            .filter(Objects::nonNull)
            .min(Date::compareTo)
            .orElse(null);
        Date periodEnd = lines.stream()
            .map(GzHighChargeConfirmRowVo::getConsumeAuditTime)
            .filter(Objects::nonNull)
            .max(Date::compareTo)
            .orElse(null);

        GzHighConsumeConfirm confirm = new GzHighConsumeConfirm();
        confirm.setId(confirmId);
        confirm.setTenantId(tenantId);
        confirm.setConfirmNo(confirmNo);
        confirm.setDepartmentId(departmentId);
        confirm.setWarehouseId(body.getWarehouseId());
        confirm.setConfirmTime(confirmTime);
        confirm.setConfirmBy(userId);
        confirm.setPeriodBegin(periodBegin);
        confirm.setPeriodEnd(periodEnd);
        confirm.setRemark("高值核销确认");
        confirm.setDelFlag(0);
        confirm.setCreateBy(userId);
        confirm.setCreateTime(confirmTime);
        gzHighConsumeConfirmMapper.insertConfirm(confirm);

        List<GzHighConsumeConfirmLine> confirmLines = new ArrayList<>();
        for (GzHighChargeConfirmRowVo row : lines)
        {
            GzHighConsumeConfirmLine cl = new GzHighConsumeConfirmLine();
            cl.setId(UUID7.generateUUID7());
            cl.setTenantId(tenantId);
            cl.setConfirmId(confirmId);
            cl.setConsumeLinkId(row.getLinkId());
            cl.setDeptBatchConsumeEntryId(row.getDeptBatchConsumeEntryId());
            cl.setTraceabilityEntryId(row.getConsumeEntryId());
            if (cl.getDeptBatchConsumeEntryId() == null && cl.getTraceabilityEntryId() == null)
            {
                throw new ServiceException("消耗明细缺少追溯关联，无法确认");
            }
            cl.setDelFlag(0);
            cl.setCreateTime(confirmTime);
            confirmLines.add(cl);
        }
        gzHighConsumeConfirmMapper.insertConfirmLineBatch(confirmLines);

        Map<String, List<GzHighChargeConfirmRowVo>> bySupplier = lines.stream()
            .collect(Collectors.groupingBy(r -> r.getSupplierId().trim(), LinkedHashMap::new, Collectors.toList()));

        List<GzHighConsumeConfirmBill> billRefs = new ArrayList<>();
        GzHighChargeConfirmResultVo result = new GzHighChargeConfirmResultVo();
        result.setConfirmId(confirmId);
        result.setConfirmNo(confirmNo);
        result.setLineCount(lines.size());

        for (Map.Entry<String, List<GzHighChargeConfirmRowVo>> en : bySupplier.entrySet())
        {
            String supplierId = en.getKey();
            List<GzHighChargeConfirmRowVo> group = en.getValue();
            String supplierName = group.stream()
                .map(GzHighChargeConfirmRowVo::getSupplierName)
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .orElse(supplierId);

            StkIoBill inBill = buildSettlementBill(tenantId, BILL_TYPE_IN, body.getWarehouseId(), null,
                supplierId, confirmTime, userId, group);
            Long inId = stkIoBillService.insertHighValueSettlementBill(inBill, PREFIX_IN);

            StkIoBill outBill = buildSettlementBill(tenantId, BILL_TYPE_OUT, body.getWarehouseId(), departmentId,
                supplierId, confirmTime, userId, group);
            Long outId = stkIoBillService.insertHighValueSettlementBill(outBill, PREFIX_OUT);

            GzHighConsumeConfirmBill inRef = newBillRef(tenantId, confirmId, supplierId, BILL_TYPE_IN, inId, inBill.getBillNo(), confirmTime);
            GzHighConsumeConfirmBill outRef = newBillRef(tenantId, confirmId, supplierId, BILL_TYPE_OUT, outId, outBill.getBillNo(), confirmTime);
            billRefs.add(inRef);
            billRefs.add(outRef);

            GzHighChargeConfirmResultVo.GzHighChargeConfirmBillVo bv = new GzHighChargeConfirmResultVo.GzHighChargeConfirmBillVo();
            bv.setSupplierId(supplierId);
            bv.setSupplierName(supplierName);
            bv.setInboundBillNo(inBill.getBillNo());
            bv.setInboundBillId(inId);
            bv.setOutboundBillNo(outBill.getBillNo());
            bv.setOutboundBillId(outId);
            result.getBills().add(bv);
        }
        gzHighConsumeConfirmMapper.insertConfirmBillBatch(billRefs);

        int updated = gzHighConsumeConfirmMapper.updateLinkConfirmStatus(tenantId, linkIds, confirmId, userId);
        if (updated != linkIds.size())
        {
            throw new ServiceException("部分明细已被他人确认，请刷新后重试");
        }
        return result;
    }

    @Override
    public GzHighChargeConfirmResultVo getConfirmDetail(String confirmId)
    {
        if (StringUtils.isBlank(confirmId))
        {
            throw new ServiceException("缺少确认批次ID");
        }
        String tenantId = requireTenant();
        List<GzHighConsumeConfirmBill> bills = gzHighConsumeConfirmMapper.selectBillsByConfirmId(tenantId, confirmId.trim());
        GzHighChargeConfirmResultVo vo = new GzHighChargeConfirmResultVo();
        vo.setConfirmId(confirmId.trim());
        Map<String, GzHighChargeConfirmResultVo.GzHighChargeConfirmBillVo> map = new LinkedHashMap<>();
        for (GzHighConsumeConfirmBill b : bills)
        {
            if (b == null)
            {
                continue;
            }
            String sid = StringUtils.defaultString(b.getSupplierId(), "");
            GzHighChargeConfirmResultVo.GzHighChargeConfirmBillVo item = map.computeIfAbsent(sid, k -> {
                GzHighChargeConfirmResultVo.GzHighChargeConfirmBillVo x = new GzHighChargeConfirmResultVo.GzHighChargeConfirmBillVo();
                x.setSupplierId(k);
                return x;
            });
            if (b.getBillType() != null && b.getBillType() == BILL_TYPE_IN)
            {
                item.setInboundBillNo(b.getBillNo());
                item.setInboundBillId(b.getStkIoBillId());
            }
            else if (b.getBillType() != null && b.getBillType() == BILL_TYPE_OUT)
            {
                item.setOutboundBillNo(b.getBillNo());
                item.setOutboundBillId(b.getStkIoBillId());
            }
        }
        vo.getBills().addAll(map.values());
        return vo;
    }

    private StkIoBill buildSettlementBill(String tenantId, int billType, Long warehouseId, Long departmentId,
        String supplierId, Date confirmTime, String userId, List<GzHighChargeConfirmRowVo> group)
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
        bill.setRemark("高值核销确认");
        if (StringUtils.isNotBlank(supplierId))
        {
            try
            {
                bill.setSupplerId(Long.parseLong(supplierId.trim()));
            }
            catch (NumberFormatException ignored)
            {
                // 表头供应商可为空，明细仍有 suppler_id
            }
        }
        List<StkIoBillEntry> entries = new ArrayList<>();
        for (GzHighChargeConfirmRowVo row : group)
        {
            entries.add(toStkEntry(row, billType));
        }
        bill.setStkIoBillEntryList(entries);
        return bill;
    }

    private StkIoBillEntry toStkEntry(GzHighChargeConfirmRowVo row, int billType)
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
        e.setRemark("高值核销确认 link=" + row.getLinkId());
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

    private Long resolveSingleWriteOffDepartment(List<GzHighChargeConfirmRowVo> lines)
    {
        List<Long> deptIds = lines.stream()
            .map(GzHighChargeConfirmRowVo::getDepartmentId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
        if (deptIds.isEmpty())
        {
            throw new ServiceException("核销记录缺少核销科室，无法确认");
        }
        if (deptIds.size() > 1)
        {
            throw new ServiceException("请选择同一核销科室的消耗明细进行确认");
        }
        return deptIds.get(0);
    }

    private String nextConfirmNo(String tenantId)
    {
        String date = FillRuleUtil.getDateNum();
        String prefix = PREFIX_CONFIRM + date;
        String max = gzHighConsumeConfirmMapper.selectMaxConfirmNo(tenantId, prefix);
        return FillRuleUtil.getNumber(PREFIX_CONFIRM, max, date);
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

    private void assertWriteOffDepartmentInUserScope(String tenantId, Long departmentId)
    {
        if (departmentId == null)
        {
            throw new ServiceException("核销记录缺少核销科室，无法确认");
        }
        Long userId = SecurityUtils.getUserId();
        if (tenantScopeService.isTenantSuper(userId, tenantId))
        {
            return;
        }
        List<Long> allowed = tenantScopeService.resolveDepartmentScope(userId, tenantId);
        if (allowed == null || allowed.isEmpty() || !allowed.contains(departmentId))
        {
            throw new ServiceException(MSG_NO_WRITE_OFF_DEPT_PERM);
        }
    }
}

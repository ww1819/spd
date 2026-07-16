package com.spd.gz.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.pagehelper.Page;
import com.spd.common.core.page.PageDomain;
import com.spd.common.core.page.TableSupport;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.PageUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.common.utils.uuid.UUID7;
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

/**
 * 高值核销确认：仅业务确认，不生成结算入出库单（HV-F-002）。
 */
@Service
public class GzHighChargeConfirmServiceImpl implements IGzHighChargeConfirmService
{
    private static final java.util.Set<String> CONFIRM_SORT_COLUMNS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
        "confirmStatus", "patientName", "visitNo", "chargeItemId", "itemName", "itemSpec",
        "materialName", "materialSpeci", "inHospitalCode", "unitPrice", "amt", "batchNumber")));

    private static final String PREFIX_CONFIRM = "GZQR";
    private static final int BILL_TYPE_IN = 101;
    private static final int BILL_TYPE_OUT = 201;
    private static final int BILL_TYPE_RETURN_GOODS = 301;
    private static final int BILL_TYPE_RETURN_DEPOT = 401;
    private static final String MSG_NO_WRITE_OFF_DEPT_PERM =
        "没有核销科室权限，不允许确认。如需确认请添加核销科室权限之后再做确认操作";

    @Autowired
    private GzHighConsumeConfirmMapper gzHighConsumeConfirmMapper;
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
        normalizeSortParams(query);
        PageDomain pageDomain = TableSupport.buildPageRequest();
        int pageNum = pageDomain.getPageNum() != null && pageDomain.getPageNum() > 0 ? pageDomain.getPageNum() : 1;
        int pageSize = pageDomain.getPageSize() != null && pageDomain.getPageSize() > 0 ? pageDomain.getPageSize() : 10;
        query.setOffset((pageNum - 1) * pageSize);
        query.setLimitSize(pageSize);
        PageUtils.clearPage();
        long total;
        List<GzHighChargeConfirmRowVo> rows;
        try
        {
            total = gzHighConsumeConfirmMapper.selectConfirmListCount(query);
            rows = gzHighConsumeConfirmMapper.selectConfirmList(query);
        }
        finally
        {
            PageUtils.clearPage();
        }
        if (rows == null)
        {
            rows = Collections.emptyList();
        }
        Page<GzHighChargeConfirmRowVo> page = new Page<>(pageNum, pageSize);
        page.setTotal(total);
        page.addAll(rows);
        return page;
    }

    private static void normalizeSortParams(GzHighChargeConfirmQuery query)
    {
        if (query == null)
        {
            return;
        }
        String col = StringUtils.trimToNull(query.getSortField());
        if (col == null || !CONFIRM_SORT_COLUMNS.contains(col))
        {
            query.setSortField(null);
            query.setSortOrder(null);
            return;
        }
        query.setSortField(col);
        query.setSortOrder("asc".equalsIgnoreCase(StringUtils.trimToEmpty(query.getSortOrder())) ? "asc" : "desc");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GzHighChargeConfirmResultVo confirm(GzHighChargeConfirmBody body)
    {
        if (body == null || body.getLinkIds() == null || body.getLinkIds().isEmpty())
        {
            throw new ServiceException("请选择要确认的消耗明细");
        }
        String tenantId = requireTenant();
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
        confirm.setWarehouseId(null);
        confirm.setConfirmTime(confirmTime);
        confirm.setConfirmBy(userId);
        confirm.setPeriodBegin(periodBegin);
        confirm.setPeriodEnd(periodEnd);
        confirm.setRemark("高值核销确认（待库房即入即出审核）");
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

        int updated = gzHighConsumeConfirmMapper.updateLinkConfirmStatus(tenantId, linkIds, confirmId, userId);
        if (updated != linkIds.size())
        {
            throw new ServiceException("部分明细已被他人确认，请刷新后重试");
        }

        GzHighChargeConfirmResultVo result = new GzHighChargeConfirmResultVo();
        result.setConfirmId(confirmId);
        result.setConfirmNo(confirmNo);
        result.setLineCount(lines.size());
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
            else if (b.getBillType() != null && b.getBillType() == BILL_TYPE_RETURN_GOODS)
            {
                item.setReturnGoodsBillNo(b.getBillNo());
                item.setReturnGoodsBillId(b.getStkIoBillId());
            }
            else if (b.getBillType() != null && b.getBillType() == BILL_TYPE_RETURN_DEPOT)
            {
                item.setReturnDepotBillNo(b.getBillNo());
                item.setReturnDepotBillId(b.getStkIoBillId());
            }
        }
        vo.getBills().addAll(map.values());
        return vo;
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

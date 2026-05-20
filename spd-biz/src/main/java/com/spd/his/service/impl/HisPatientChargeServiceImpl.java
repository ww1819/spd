package com.spd.his.service.impl;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.PageUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.uuid.IdUtils;
import com.spd.his.config.HisSqlServerProperties;
import com.spd.his.config.HisTenantDbHandle;
import com.spd.his.config.HisTenantJdbcAccess;
import com.spd.his.domain.HisChargeFetchBatch;
import com.spd.his.domain.HisInpatientChargeMirror;
import com.spd.his.domain.HisOutpatientChargeMirror;
import com.spd.his.domain.HisPatientChargeMirrorUnified;
import com.spd.his.domain.dto.HisFetchResultVo;
import com.spd.his.domain.dto.HisIdFingerprint;
import com.spd.his.domain.dto.HisPatientChargeFetchBody;
import com.spd.his.domain.dto.HisPatientChargeSummaryRow;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import java.util.Collections;
import com.spd.his.domain.HisChargeItemMirror;
import com.spd.his.mapper.HisChargeFetchBatchMapper;
import com.spd.his.mapper.HisChargeItemMirrorMapper;
import com.spd.his.mapper.HisInpatientChargeMirrorMapper;
import com.spd.his.mapper.HisOutpatientChargeMirrorMapper;
import com.spd.his.mapper.HisPatientChargeMirrorUnifiedMapper;
import com.spd.his.mapper.HisMirrorConsumeLinkMapper;
import com.spd.his.domain.dto.HisGenerateConsumeResultVo;
import com.spd.his.domain.dto.HisMirrorHighApplyBody;
import com.spd.his.domain.dto.HisMirrorHighApplyResultVo;
import com.spd.his.domain.dto.HisMirrorHighScanBody;
import com.spd.his.domain.dto.HisMirrorHighScanResultVo;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.mapper.FdDepartmentMapper;
import com.spd.his.domain.dto.HisMirrorLowBatchResultVo;
import com.spd.his.domain.dto.HisMirrorManualBatchBody;
import com.spd.his.domain.dto.HisMirrorManualRowBody;
import com.spd.his.domain.dto.HisPatientChargeAllQuery;
import com.spd.his.domain.dto.HisPatientChargeDetailRow;
import com.spd.his.domain.dto.HisPatientChargeMirrorUnifiedQuery;
import com.spd.his.domain.dto.HisMirrorConsumeRecordVo;
import com.spd.his.domain.dto.HisTenantBillingSettingBody;
import com.spd.his.constants.HisBillingTenantConstants;
import com.spd.his.service.IHisBillingRefundService;
import com.spd.his.service.IHisMirrorConsumeManualService;
import com.spd.his.service.IHisPatientChargeService;
import com.spd.his.support.HisInternalRequestContext;
import com.spd.his.service.support.HisMirrorStockEnricher;
import com.spd.his.support.HisPatientChargeMirrorUnifiedSupport;
import com.spd.foundation.service.ISbTenantSettingService;
import com.spd.system.service.ITenantScopeService;

@Service
public class HisPatientChargeServiceImpl implements IHisPatientChargeService
{
    private static final Logger log = LoggerFactory.getLogger(HisPatientChargeServiceImpl.class);

    private static final int HIS_ID_QUERY_BATCH = 400;
    private static final int INSERT_BATCH_SIZE = 80;
    private static final String KIND_IN = "INPATIENT";

    /** HIS 区间查询占位符绑定格式（字符串比较，与内置 SQL 一致） */
    private static final DateTimeFormatter HIS_FETCH_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private HisTenantJdbcAccess hisTenantJdbcAccess;

    @Autowired
    private HisSqlServerProperties hisSqlServerProperties;

    @Autowired
    private HisInpatientChargeMirrorMapper hisInpatientChargeMirrorMapper;

    @Autowired
    private HisOutpatientChargeMirrorMapper hisOutpatientChargeMirrorMapper;

    @Autowired
    private HisPatientChargeMirrorUnifiedMapper hisPatientChargeMirrorUnifiedMapper;

    @Autowired
    private HisChargeItemMirrorMapper hisChargeItemMirrorMapper;

    @Autowired
    private HisMirrorStockEnricher hisMirrorStockEnricher;

    @Autowired
    private HisChargeFetchBatchMapper hisChargeFetchBatchMapper;

    @Autowired
    private IHisMirrorConsumeManualService hisMirrorConsumeManualService;

    @Autowired
    private ITenantScopeService tenantScopeService;

    @Autowired
    private FdDepartmentMapper fdDepartmentMapper;

    @Autowired
    private HisMirrorConsumeLinkMapper hisMirrorConsumeLinkMapper;

    @Autowired
    private ISbTenantSettingService sbTenantSettingService;

    @Autowired
    private IHisBillingRefundService hisBillingRefundService;

    @Override
    public HisFetchResultVo fetchInpatientMirror(HisPatientChargeFetchBody body)
    {
        assertTenantAllowed();
        LocalDateTime[] win = parseWindow(body);
        String tenantId = SecurityUtils.getCustomerId();
        HisTenantDbHandle hisDb = hisTenantJdbcAccess.obtainHandle(tenantId);
        String batchId = IdUtils.fastUUID();
        String createBy = SecurityUtils.getUserIdStr();
        Date now = new Date();

        int inserted = 0;
        int skipped = 0;
        int drift = 0;

        int chunkDays = Math.max(1, hisSqlServerProperties.getFetch().getChunkDays());
        LocalDateTime cursor = win[0];
        while (cursor.isBefore(win[1]))
        {
            LocalDateTime next = cursor.plusDays(chunkDays);
            if (next.isAfter(win[1]))
            {
                next = win[1];
            }
            List<HisInpatientChargeMirror> chunkRows = queryInpatientChunk(hisDb, cursor, next, tenantId, batchId, createBy, now);
            int[] c = mergeInpatientChunk(tenantId, chunkRows);
            inserted += c[0];
            skipped += c[1];
            drift += c[2];
            cursor = next;
        }

        HisChargeFetchBatch logRow = new HisChargeFetchBatch();
        logRow.setId(batchId);
        logRow.setTenantId(tenantId);
        logRow.setChargeKind("INPATIENT");
        logRow.setWindowStart(Timestamp.valueOf(win[0]));
        logRow.setWindowEnd(Timestamp.valueOf(win[1]));
        logRow.setInsertedCount(inserted);
        logRow.setSkippedCount(skipped);
        logRow.setDriftCount(drift);
        logRow.setCreateBy(createBy);
        logRow.setCreateTime(now);
        hisChargeFetchBatchMapper.insertHisChargeFetchBatch(logRow);

        HisFetchResultVo vo = new HisFetchResultVo();
        vo.setFetchBatchId(batchId);
        vo.setInsertedCount(inserted);
        vo.setSkippedCount(skipped);
        vo.setDriftCount(drift);
        maybeAutoLvConsumeAfterFetch(tenantId, batchId, "INPATIENT");
        maybeAutoRefundAfterFetch(tenantId, batchId, "INPATIENT");
        return vo;
    }

    @Override
    public HisFetchResultVo fetchOutpatientMirror(HisPatientChargeFetchBody body)
    {
        assertTenantAllowed();
        LocalDateTime[] win = parseWindow(body);
        String tenantId = SecurityUtils.getCustomerId();
        int chunkDays = Math.max(1, hisSqlServerProperties.getFetch().getChunkDays());
        int queryTimeoutSec = Math.max(1, hisSqlServerProperties.getFetch().getQueryTimeoutSeconds());
        log.info("门诊收费镜像抓取: tenantId={}, window=[{} ~ {}), chunkDays={}, queryTimeoutSec={}",
            tenantId, win[0], win[1], chunkDays, queryTimeoutSec);
        HisTenantDbHandle hisDb = hisTenantJdbcAccess.obtainHandle(tenantId);
        if (hisDb.getOutpatientRangeSql() != null && hisDb.getOutpatientRangeSql().contains("LTRIM(RTRIM"))
        {
            throw new ServiceException(
                "当前运行的 SPD 仍是旧版抓取 SQL（含 LTRIM/RTRIM），请重新编译部署 spd-biz 后重启；"
                    + "或检查 sys_his_external_db 是否填写了自定义门诊区间 SQL");
        }
        String batchId = IdUtils.fastUUID();
        String createBy = SecurityUtils.getUserIdStr();
        Date now = new Date();

        int inserted = 0;
        int skipped = 0;
        int drift = 0;
        LocalDateTime cursor = win[0];
        while (cursor.isBefore(win[1]))
        {
            LocalDateTime next = cursor.plusDays(chunkDays);
            if (next.isAfter(win[1]))
            {
                next = win[1];
            }
            List<HisOutpatientChargeMirror> chunkRows = queryOutpatientChunk(hisDb, cursor, next, tenantId, batchId, createBy, now);
            int[] c = mergeOutpatientChunk(tenantId, chunkRows);
            inserted += c[0];
            skipped += c[1];
            drift += c[2];
            cursor = next;
        }

        HisChargeFetchBatch logRow = new HisChargeFetchBatch();
        logRow.setId(batchId);
        logRow.setTenantId(tenantId);
        logRow.setChargeKind("OUTPATIENT");
        logRow.setWindowStart(Timestamp.valueOf(win[0]));
        logRow.setWindowEnd(Timestamp.valueOf(win[1]));
        logRow.setInsertedCount(inserted);
        logRow.setSkippedCount(skipped);
        logRow.setDriftCount(drift);
        logRow.setCreateBy(createBy);
        logRow.setCreateTime(now);
        hisChargeFetchBatchMapper.insertHisChargeFetchBatch(logRow);

        HisFetchResultVo vo = new HisFetchResultVo();
        vo.setFetchBatchId(batchId);
        vo.setInsertedCount(inserted);
        vo.setSkippedCount(skipped);
        vo.setDriftCount(drift);
        maybeAutoLvConsumeAfterFetch(tenantId, batchId, "OUTPATIENT");
        maybeAutoRefundAfterFetch(tenantId, batchId, "OUTPATIENT");
        return vo;
    }

    @Override
    public HisTenantBillingSettingBody getTenantBillingSetting()
    {
        assertHengsuiBillingTenantOnly();
        HisTenantBillingSettingBody b = new HisTenantBillingSettingBody();
        String tenantId = SecurityUtils.getCustomerId();
        String v = sbTenantSettingService.getSettingValue(tenantId,
            HisBillingTenantConstants.SETTING_LV_AUTO_CONSUME_ENABLED, "0");
        b.setLvAutoConsumeEnabled(v);
        String refundOn = sbTenantSettingService.getSettingValue(tenantId,
            HisBillingTenantConstants.SETTING_BILLING_AUTO_REFUND_ENABLED, "0");
        b.setBillingAutoRefundEnabled(refundOn);
        return b;
    }

    @Override
    public void saveTenantBillingSetting(HisTenantBillingSettingBody body)
    {
        assertHengsuiBillingTenantOnly();
        String tenantId = SecurityUtils.getCustomerId();
        String on = body == null ? "0" : StringUtils.trimToEmpty(body.getLvAutoConsumeEnabled());
        if (!"0".equals(on) && !"1".equals(on))
        {
            throw new ServiceException("lvAutoConsumeEnabled 仅支持 0 或 1");
        }
        sbTenantSettingService.saveSettingValue(tenantId,
            HisBillingTenantConstants.SETTING_LV_AUTO_CONSUME_ENABLED, on, "低值计费抓取后自动生成消耗");
        String refundOn = body == null ? "0" : StringUtils.trimToEmpty(body.getBillingAutoRefundEnabled());
        if (!"0".equals(refundOn) && !"1".equals(refundOn))
        {
            throw new ServiceException("billingAutoRefundEnabled 仅支持 0 或 1");
        }
        sbTenantSettingService.saveSettingValue(tenantId,
            HisBillingTenantConstants.SETTING_BILLING_AUTO_REFUND_ENABLED, refundOn, "计费退费镜像抓取后自动返还库存");
    }

    private void assertHengsuiBillingTenantOnly()
    {
        if (!HisBillingTenantConstants.TENANT_HENGSHUI_THIRD.equals(SecurityUtils.getCustomerId()))
        {
            throw new ServiceException("该配置仅衡水三院租户可用");
        }
    }

    private void maybeAutoLvConsumeAfterFetch(String tenantId, String fetchBatchId, String chargeKind)
    {
        if (!HisBillingTenantConstants.TENANT_HENGSHUI_THIRD.equals(tenantId))
        {
            return;
        }
        String v = sbTenantSettingService.getSettingValue(tenantId,
            HisBillingTenantConstants.SETTING_LV_AUTO_CONSUME_ENABLED, "0");
        if (!"1".equals(v))
        {
            return;
        }
        if ("INPATIENT".equals(chargeKind))
        {
            List<HisInpatientChargeMirror> pending = hisInpatientChargeMirrorMapper.selectPendingByFetchBatch(tenantId, fetchBatchId);
            if (pending == null)
            {
                return;
            }
            for (HisInpatientChargeMirror row : pending)
            {
                if (row == null || !isLowValueMirrorRow(row.getValueLevel()))
                {
                    continue;
                }
                try
                {
                    HisMirrorManualRowBody b = new HisMirrorManualRowBody();
                    b.setVisitKind(KIND_IN);
                    b.setMirrorRowId(row.getId());
                    hisMirrorConsumeManualService.processLowValue(b);
                }
                catch (Exception e)
                {
                    log.warn("HIS自动低值消耗跳过 mirrorRowId={} err={}", row.getId(), e.toString());
                }
            }
        }
        else
        {
            List<HisOutpatientChargeMirror> pending = hisOutpatientChargeMirrorMapper.selectPendingByFetchBatch(tenantId, fetchBatchId);
            if (pending == null)
            {
                return;
            }
            for (HisOutpatientChargeMirror row : pending)
            {
                if (row == null || !isLowValueMirrorRow(row.getValueLevel()))
                {
                    continue;
                }
                try
                {
                    HisMirrorManualRowBody b = new HisMirrorManualRowBody();
                    b.setVisitKind("OUTPATIENT");
                    b.setMirrorRowId(row.getId());
                    hisMirrorConsumeManualService.processLowValue(b);
                }
                catch (Exception e)
                {
                    log.warn("HIS自动低值消耗跳过 mirrorRowId={} err={}", row.getId(), e.toString());
                }
            }
        }
    }

    @Override
    public void processFetchBatchAuto(String tenantId, String fetchBatchId, String visitKind, Long operatorUserId)
    {
        if (!HisBillingTenantConstants.TENANT_HENGSHUI_THIRD.equals(tenantId))
        {
            return;
        }
        String vk = visitKind == null ? "" : visitKind.trim().toUpperCase();
        if (!KIND_IN.equals(vk) && !"OUTPATIENT".equals(vk))
        {
            throw new ServiceException("visitKind 仅支持 INPATIENT 或 OUTPATIENT");
        }
        Long opUid = operatorUserId;
        if (opUid == null)
        {
            String raw = sbTenantSettingService.getSettingValue(tenantId,
                HisBillingTenantConstants.SETTING_INTERNAL_OPERATOR_USER_ID, "0");
            try
            {
                opUid = Long.parseLong(StringUtils.trimToEmpty(raw));
            }
            catch (Exception e)
            {
                opUid = 0L;
            }
        }
        Long finalOpUid = opUid;
        HisInternalRequestContext.run(tenantId, finalOpUid, () -> {
            maybeAutoLvConsumeAfterFetch(tenantId, fetchBatchId, vk);
            maybeAutoRefundAfterFetch(tenantId, fetchBatchId, vk);
        });
    }

    private void maybeAutoRefundAfterFetch(String tenantId, String fetchBatchId, String chargeKind)
    {
        if (!HisBillingTenantConstants.TENANT_HENGSHUI_THIRD.equals(tenantId))
        {
            return;
        }
        String v = sbTenantSettingService.getSettingValue(tenantId,
            HisBillingTenantConstants.SETTING_BILLING_AUTO_REFUND_ENABLED, "0");
        if (!"1".equals(v))
        {
            return;
        }
        try
        {
            hisBillingRefundService.processAutoRefundForFetchBatch(tenantId, fetchBatchId, chargeKind);
        }
        catch (Exception e)
        {
            log.warn("HIS自动退费批次处理异常 fetchBatchId={} err={}", fetchBatchId, e.toString());
        }
    }

    private static boolean isLowValueMirrorRow(String valueLevel)
    {
        String v = StringUtils.trimToEmpty(valueLevel);
        return v.isEmpty() || "2".equals(v);
    }

    /**
     * 统一表该租户无数据但住院/门诊镜像有历史数据时，从镜像回填统一表（与升级脚本逻辑一致），避免列表全空。
     */
    private void ensureUnifiedMirrorBackfill(String tenantId)
    {
        if (StringUtils.isEmpty(tenantId))
        {
            return;
        }
        if (hisPatientChargeMirrorUnifiedMapper.countByTenantId(tenantId) > 0)
        {
            return;
        }
        synchronized (("pcm-unified-backfill-" + tenantId).intern())
        {
            if (hisPatientChargeMirrorUnifiedMapper.countByTenantId(tenantId) > 0)
            {
                return;
            }
            if (hisPatientChargeMirrorUnifiedMapper.countMirrorSourceRowsByTenant(tenantId) <= 0)
            {
                return;
            }
            hisPatientChargeMirrorUnifiedMapper.backfillInpatientFromMirror(tenantId);
            hisPatientChargeMirrorUnifiedMapper.backfillOutpatientFromMirror(tenantId);
        }
    }

    @Override
    public List<HisInpatientChargeMirror> selectInpatientMirrorList(HisInpatientChargeMirror query)
    {
        if (query == null || StringUtils.isEmpty(query.getTenantId()))
        {
            query = query == null ? new HisInpatientChargeMirror() : query;
            query.setTenantId(SecurityUtils.getCustomerId());
        }
        String customerId = query.getTenantId();
        if (StringUtils.isNotEmpty(customerId) && !tenantScopeService.isTenantSuper(SecurityUtils.getUserId(), customerId))
        {
            tenantScopeService.applyDepartmentScopeQueryParams(query.getParams(), SecurityUtils.getUserId(), customerId);
        }
        ensureUnifiedMirrorBackfill(customerId);
        HisPatientChargeMirrorUnifiedQuery uq = HisPatientChargeMirrorUnifiedSupport.fromInpatientQuery(query);
        UnifiedMirrorPageSlice slice = selectUnifiedMirrorPageSlice(uq);
        hisMirrorStockEnricher.enrichUnifiedList(customerId, slice.rows);
        List<HisInpatientChargeMirror> out = new ArrayList<>(slice.rows.size());
        for (HisPatientChargeMirrorUnified u : slice.rows)
        {
            out.add(HisPatientChargeMirrorUnifiedSupport.toInpatientMirror(u));
        }
        sortInpatientPageByStockIfNeeded(out, query);
        return wrapAsPage(out, slice);
    }

    @Override
    public List<HisOutpatientChargeMirror> selectOutpatientMirrorList(HisOutpatientChargeMirror query)
    {
        if (query == null || StringUtils.isEmpty(query.getTenantId()))
        {
            query = query == null ? new HisOutpatientChargeMirror() : query;
            query.setTenantId(SecurityUtils.getCustomerId());
        }
        String customerId = query.getTenantId();
        if (StringUtils.isNotEmpty(customerId) && !tenantScopeService.isTenantSuper(SecurityUtils.getUserId(), customerId))
        {
            tenantScopeService.applyDepartmentScopeQueryParams(query.getParams(), SecurityUtils.getUserId(), customerId);
        }
        ensureUnifiedMirrorBackfill(customerId);
        HisPatientChargeMirrorUnifiedQuery uq = HisPatientChargeMirrorUnifiedSupport.fromOutpatientQuery(query);
        UnifiedMirrorPageSlice slice = selectUnifiedMirrorPageSlice(uq);
        hisMirrorStockEnricher.enrichUnifiedList(customerId, slice.rows);
        List<HisOutpatientChargeMirror> out = new ArrayList<>(slice.rows.size());
        for (HisPatientChargeMirrorUnified u : slice.rows)
        {
            out.add(HisPatientChargeMirrorUnifiedSupport.toOutpatientMirror(u));
        }
        sortOutpatientPageByStockIfNeeded(out, query);
        return wrapAsPage(out, slice);
    }

    @Override
    public List<HisPatientChargeDetailRow> selectAllMirrorList(HisPatientChargeAllQuery query)
    {
        HisPatientChargeAllQuery q = query == null ? new HisPatientChargeAllQuery() : query;
        if (StringUtils.isEmpty(q.getTenantId()))
        {
            q.setTenantId(SecurityUtils.getCustomerId());
        }
        String customerId = q.getTenantId();
        if (StringUtils.isNotEmpty(customerId) && !tenantScopeService.isTenantSuper(SecurityUtils.getUserId(), customerId))
        {
            tenantScopeService.applyDepartmentScopeQueryParams(q.getParams(), SecurityUtils.getUserId(), customerId);
        }
        ensureUnifiedMirrorBackfill(customerId);
        HisPatientChargeMirrorUnifiedQuery uq = HisPatientChargeMirrorUnifiedSupport.fromAllQuery(q);
        UnifiedMirrorPageSlice slice = selectUnifiedMirrorPageSlice(uq);
        hisMirrorStockEnricher.enrichUnifiedList(customerId, slice.rows);
        List<HisPatientChargeDetailRow> out = new ArrayList<>(slice.rows.size());
        for (HisPatientChargeMirrorUnified u : slice.rows)
        {
            out.add(HisPatientChargeMirrorUnifiedSupport.toDetailRow(u));
        }
        sortDetailPageByStockIfNeeded(out, q);
        return wrapAsPage(out, slice);
    }

    @Override
    public List<HisMirrorConsumeRecordVo> listMirrorConsumeRecords(String visitKind, String mirrorRowId)
    {
        assertTenantAllowed();
        String tenantId = SecurityUtils.getCustomerId();
        if (StringUtils.isAnyBlank(tenantId, visitKind, mirrorRowId))
        {
            return new ArrayList<>();
        }
        String vk = normalizeVisitKindForAuth(visitKind);
        if (!"INPATIENT".equals(vk) && !"OUTPATIENT".equals(vk))
        {
            throw new ServiceException("visitKind 仅支持 INPATIENT 或 OUTPATIENT");
        }
        String rowId = StringUtils.trimToEmpty(mirrorRowId);
        assertMirrorRowDepartmentAllowed(tenantId, vk, rowId);
        return hisMirrorConsumeLinkMapper.selectConsumeRecordsByMirrorRow(tenantId, vk, rowId);
    }

    @Override
    public List<HisPatientChargeSummaryRow> selectChargeSummary(String beginChargeDate, String endChargeDate)
    {
        if (StringUtils.isAnyEmpty(beginChargeDate, endChargeDate))
        {
            throw new ServiceException("汇总查询须指定计费开始、结束日期");
        }
        String tenantId = SecurityUtils.getCustomerId();
        List<HisPatientChargeSummaryRow> a = hisInpatientChargeMirrorMapper.selectSummary(tenantId, beginChargeDate, endChargeDate);
        List<HisPatientChargeSummaryRow> b = hisOutpatientChargeMirrorMapper.selectSummary(tenantId, beginChargeDate, endChargeDate);
        List<HisPatientChargeSummaryRow> all = new ArrayList<>(a.size() + b.size());
        all.addAll(a);
        all.addAll(b);
        return all;
    }

    @Override
    public List<HisChargeFetchBatch> listRecentFetchBatches(int limit)
    {
        int lim = limit > 0 && limit <= 100 ? limit : 30;
        return hisChargeFetchBatchMapper.selectRecentByTenant(SecurityUtils.getCustomerId(), lim);
    }

    @Override
    public HisGenerateConsumeResultVo processMirrorLowValue(HisMirrorManualRowBody body)
    {
        assertTenantAllowed();
        if (body != null && StringUtils.isNoneBlank(body.getMirrorRowId(), body.getVisitKind()))
        {
            assertMirrorRowDepartmentAllowed(SecurityUtils.getCustomerId(),
                normalizeVisitKindForAuth(body.getVisitKind()), StringUtils.trimToEmpty(body.getMirrorRowId()));
        }
        return hisMirrorConsumeManualService.processLowValue(body);
    }

    @Override
    public HisMirrorLowBatchResultVo processMirrorLowValueBatch(HisMirrorManualBatchBody body)
    {
        assertTenantAllowed();
        if (body == null || body.getMirrorRowIds() == null || body.getMirrorRowIds().isEmpty())
        {
            throw new ServiceException("请选择至少一条计费明细");
        }
        String vk = normalizeVisitKindForAuth(body.getVisitKind());
        if (!"INPATIENT".equals(vk) && !"OUTPATIENT".equals(vk))
        {
            throw new ServiceException("visitKind 仅支持 INPATIENT 或 OUTPATIENT");
        }
        String tenantId = SecurityUtils.getCustomerId();
        int ok = 0;
        int fail = 0;
        HisMirrorLowBatchResultVo vo = new HisMirrorLowBatchResultVo();
        for (String rid : body.getMirrorRowIds())
        {
            String id = StringUtils.trimToEmpty(rid);
            if (StringUtils.isEmpty(id))
            {
                continue;
            }
            try
            {
                assertMirrorRowDepartmentAllowed(tenantId, vk, id);
                HisMirrorManualRowBody one = new HisMirrorManualRowBody();
                one.setVisitKind(vk);
                one.setMirrorRowId(id);
                hisMirrorConsumeManualService.processLowValue(one);
                ok++;
            }
            catch (Exception ex)
            {
                fail++;
                String msg = ex.getMessage();
                if (StringUtils.isBlank(msg) && ex.getCause() != null)
                {
                    msg = ex.getCause().getMessage();
                }
                vo.getFailMessages().add(id + (StringUtils.isBlank(msg) ? "" : (": " + msg)));
            }
        }
        vo.setSuccessCount(ok);
        vo.setFailCount(fail);
        return vo;
    }

    @Override
    public HisMirrorHighScanResultVo scanMirrorHighBarcode(HisMirrorHighScanBody body)
    {
        assertTenantAllowed();
        if (body != null && StringUtils.isNoneBlank(body.getMirrorRowId(), body.getVisitKind()))
        {
            assertMirrorRowDepartmentAllowed(SecurityUtils.getCustomerId(),
                normalizeVisitKindForAuth(body.getVisitKind()), StringUtils.trimToEmpty(body.getMirrorRowId()));
        }
        return hisMirrorConsumeManualService.scanHighBarcode(body);
    }

    @Override
    public HisMirrorHighApplyResultVo applyMirrorHighConsume(HisMirrorHighApplyBody body)
    {
        assertTenantAllowed();
        if (body != null && StringUtils.isNoneBlank(body.getMirrorRowId(), body.getVisitKind()))
        {
            assertMirrorRowDepartmentAllowed(SecurityUtils.getCustomerId(),
                normalizeVisitKindForAuth(body.getVisitKind()), StringUtils.trimToEmpty(body.getMirrorRowId()));
        }
        return hisMirrorConsumeManualService.applyHighConsume(body);
    }

    private void assertMirrorRowDepartmentAllowed(String tenantId, String visitKind, String mirrorRowId)
    {
        if (StringUtils.isEmpty(tenantId))
        {
            throw new ServiceException("无法解析当前租户");
        }
        if (tenantScopeService.isTenantSuper(SecurityUtils.getUserId(), tenantId))
        {
            return;
        }
        List<Long> allowed = tenantScopeService.resolveDepartmentScope(SecurityUtils.getUserId(), tenantId);
        if (allowed == null || allowed.isEmpty())
        {
            throw new ServiceException("无科室数据权限");
        }
        Long deptPk = resolveMirrorDepartmentDbId(tenantId, visitKind, mirrorRowId);
        if (deptPk == null || !allowed.contains(deptPk))
        {
            throw new ServiceException("计费明细不在您的科室权限范围内");
        }
    }

    private Long resolveMirrorDepartmentDbId(String tenantId, String visitKind, String mirrorRowId)
    {
        if ("INPATIENT".equalsIgnoreCase(visitKind))
        {
            HisInpatientChargeMirror r = hisInpatientChargeMirrorMapper.selectByIdAndTenant(tenantId, mirrorRowId);
            if (r == null || StringUtils.isBlank(r.getDeptCode()))
            {
                return null;
            }
            FdDepartment d = fdDepartmentMapper.selectFdDepartmentByTenantAndHisId(tenantId, StringUtils.trimToEmpty(r.getDeptCode()));
            return d != null ? d.getId() : null;
        }
        if ("OUTPATIENT".equalsIgnoreCase(visitKind))
        {
            HisOutpatientChargeMirror r = hisOutpatientChargeMirrorMapper.selectByIdAndTenant(tenantId, mirrorRowId);
            if (r == null || StringUtils.isBlank(r.getClinicCode()))
            {
                return null;
            }
            FdDepartment d = fdDepartmentMapper.selectFdDepartmentByTenantAndHisId(tenantId, StringUtils.trimToEmpty(r.getClinicCode()));
            return d != null ? d.getId() : null;
        }
        return null;
    }

    private static String normalizeVisitKindForAuth(String raw)
    {
        if (StringUtils.isBlank(raw))
        {
            return "";
        }
        String v = raw.trim().toUpperCase();
        if ("INPATIENT".equals(v) || "OUTPATIENT".equals(v))
        {
            return v;
        }
        return "";
    }

    private void assertTenantAllowed()
    {
        List<String> allow = hisSqlServerProperties.getAllowedTenantIds();
        if (allow == null || allow.isEmpty())
        {
            return;
        }
        String t = SecurityUtils.getCustomerId();
        if (StringUtils.isEmpty(t) || !allow.contains(t))
        {
            throw new ServiceException("当前租户不允许执行 HIS 计费抓取");
        }
    }

    private LocalDateTime[] parseWindow(HisPatientChargeFetchBody body)
    {
        if (body == null || StringUtils.isAnyEmpty(body.getBeginDate(), body.getEndDate()))
        {
            throw new ServiceException("请指定抓取开始时间与结束时间");
        }
        LocalDateTime startInclusive = parseFetchLowerBound(body.getBeginDate().trim());
        LocalDateTime endInclusive = parseFetchUpperBound(body.getEndDate().trim());
        if (endInclusive.isBefore(startInclusive))
        {
            throw new ServiceException("结束时间不能早于开始时间");
        }
        long spanDays = ChronoUnit.DAYS.between(startInclusive.toLocalDate(), endInclusive.toLocalDate()) + 1;
        int maxDays = Math.max(1, hisSqlServerProperties.getFetch().getMaxRangeDays());
        if (spanDays > maxDays)
        {
            throw new ServiceException("单次抓取跨度不能超过 " + maxDays + " 天，请缩小时间范围");
        }
        LocalDateTime endExclusive = endInclusive.plusSeconds(1);
        return new LocalDateTime[] { startInclusive, endExclusive };
    }

    private static LocalDateTime parseFetchLowerBound(String raw)
    {
        try
        {
            if (raw.length() <= 10)
            {
                return LocalDate.parse(raw).atStartOfDay();
            }
            return LocalDateTime.parse(raw, HIS_FETCH_TIME_FMT);
        }
        catch (DateTimeParseException e)
        {
            throw new ServiceException("开始时间格式须为 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss");
        }
    }

    private static LocalDateTime parseFetchUpperBound(String raw)
    {
        try
        {
            if (raw.length() <= 10)
            {
                return LocalDate.parse(raw).atTime(23, 59, 59);
            }
            return LocalDateTime.parse(raw, HIS_FETCH_TIME_FMT);
        }
        catch (DateTimeParseException e)
        {
            throw new ServiceException("结束时间格式须为 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss");
        }
    }

    private List<HisInpatientChargeMirror> queryInpatientChunk(
        HisTenantDbHandle hisDb,
        LocalDateTime chunkStart, LocalDateTime chunkEndExcl,
        String tenantId, String batchId, String createBy, Date createTime)
    {
        String lo = chunkStart.format(HIS_FETCH_TIME_FMT);
        String hi = chunkEndExcl.format(HIS_FETCH_TIME_FMT);
        RowMapper<HisInpatientChargeMirror> rm = (rs, rowNum) -> mapInpatientRow(rs, tenantId, batchId, createBy, createTime);
        int queryTimeoutSec = Math.max(1, hisSqlServerProperties.getFetch().getQueryTimeoutSeconds());
        return hisDb.getJdbcTemplate().query(hisDb.getInpatientRangeSql(), ps ->
        {
            ps.setQueryTimeout(queryTimeoutSec);
            ps.setString(1, lo);
            ps.setString(2, hi);
        }, rm);
    }

    private HisInpatientChargeMirror mapInpatientRow(ResultSet rs, String tenantId, String batchId, String createBy, Date createTime) throws SQLException
    {
        HisInpatientChargeMirror e = new HisInpatientChargeMirror();
        e.setHisInpatientChargeId(toHisIdString(rs.getObject("inpatient_charge_id")));
        e.setHisInpatientChargeIdTf(toHisIdString(rs.getObject("inpatient_charge_id_tf")));
        e.setPatientId(toHisIdString(rs.getObject("patient_id")));
        e.setPatientName(rs.getString("patient_name"));
        e.setInpatientNo(rs.getString("inpatient_no"));
        e.setDeptCode(trimToNull(rs.getString("dept_code")));
        e.setDeptName(rs.getString("dept_name"));
        e.setDoctorId(trimToNull(rs.getString("doctor_id")));
        e.setDoctorName(rs.getString("doctor_name"));
        e.setChargeItemId(trimToNull(rs.getString("charge_item_id")));
        e.setItemName(rs.getString("item_name"));
        e.setSpecModel(rs.getString("spec_model"));
        e.setBatchNo(rs.getString("batch_no"));
        e.setExpireDate(rs.getString("expire_date"));
        e.setUseDate(parseHisDateTime(rs.getObject("use_date")));
        e.setChargeDate(parseHisDateTime(rs.getObject("charge_date")));
        e.setQuantity(rs.getBigDecimal("quantity"));
        e.setUnitPrice(rs.getBigDecimal("unit_price"));
        e.setTotalAmount(rs.getBigDecimal("total_amount"));
        e.setChargeOperator(rs.getString("charge_operator"));
        e.setRemark(rs.getString("remark"));
        e.setTenantId(tenantId);
        e.setFetchBatchId(batchId);
        e.setProcessStatus("PENDING_CONSUME");
        e.setCreateBy(createBy);
        e.setCreateTime(createTime);
        e.setRowFingerprint(fingerprintInpatient(e));
        return e;
    }

    private List<HisOutpatientChargeMirror> queryOutpatientChunk(
        HisTenantDbHandle hisDb,
        LocalDateTime chunkStart, LocalDateTime chunkEndExcl,
        String tenantId, String batchId, String createBy, Date createTime)
    {
        String lo = chunkStart.format(HIS_FETCH_TIME_FMT);
        String hi = chunkEndExcl.format(HIS_FETCH_TIME_FMT);
        RowMapper<HisOutpatientChargeMirror> rm = (rs, rowNum) -> mapOutpatientRow(rs, tenantId, batchId, createBy, createTime);
        int queryTimeoutSec = Math.max(1, hisSqlServerProperties.getFetch().getQueryTimeoutSeconds());
        log.debug("门诊分段查询: {} <= charge_date < {}", lo, hi);
        return hisDb.getJdbcTemplate().query(hisDb.getOutpatientRangeSql(), ps ->
        {
            ps.setQueryTimeout(queryTimeoutSec);
            ps.setString(1, lo);
            ps.setString(2, hi);
        }, rm);
    }

    private HisOutpatientChargeMirror mapOutpatientRow(ResultSet rs, String tenantId, String batchId, String createBy, Date createTime) throws SQLException
    {
        HisOutpatientChargeMirror e = new HisOutpatientChargeMirror();
        e.setHisOutpatientChargeId(toHisIdString(rs.getObject("outpatient_charge_id")));
        e.setHisOutpatientChargeIdTf(toHisIdString(rs.getObject("outpatient_charge_id_tf")));
        e.setPatientId(toHisIdString(rs.getObject("patient_id")));
        e.setPatientName(rs.getString("patient_name"));
        e.setOutpatientNo(rs.getString("outpatient_no"));
        e.setClinicCode(trimToNull(rs.getString("clinic_code")));
        e.setClinicName(rs.getString("clinic_name"));
        e.setDoctorId(trimToNull(rs.getString("doctor_id")));
        e.setDoctorName(rs.getString("doctor_name"));
        e.setChargeItemId(trimToNull(rs.getString("charge_item_id")));
        e.setItemName(rs.getString("item_name"));
        e.setSpecModel(rs.getString("spec_model"));
        e.setBatchNo(rs.getString("batch_no"));
        e.setExpireDate(rs.getString("expire_date"));
        e.setChargeDate(rs.getString("charge_date"));
        e.setQuantity(rs.getBigDecimal("quantity"));
        e.setUnitPrice(rs.getBigDecimal("unit_price"));
        e.setTotalAmount(rs.getBigDecimal("total_amount"));
        e.setChargeOperator(rs.getString("charge_operator"));
        e.setPaymentType(rs.getString("payment_type"));
        e.setReceiptNo(trimToNull(rs.getString("receipt_no")));
        e.setRemark(rs.getString("remark"));
        e.setTenantId(tenantId);
        e.setFetchBatchId(batchId);
        e.setProcessStatus("PENDING_CONSUME");
        e.setCreateBy(createBy);
        e.setCreateTime(createTime);
        e.setRowFingerprint(fingerprintOutpatient(e));
        return e;
    }

    private int[] mergeInpatientChunk(String tenantId, List<HisInpatientChargeMirror> chunkRows)
    {
        List<HisInpatientChargeMirror> candidates = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (HisInpatientChargeMirror r : chunkRows)
        {
            if (StringUtils.isEmpty(r.getHisInpatientChargeId()))
            {
                continue;
            }
            if (!seen.add(r.getHisInpatientChargeId()))
            {
                continue;
            }
            candidates.add(r);
        }
        if (candidates.isEmpty())
        {
            return new int[] { 0, 0, 0 };
        }
        List<String> ids = new ArrayList<>(candidates.size());
        for (HisInpatientChargeMirror c : candidates)
        {
            ids.add(c.getHisInpatientChargeId());
        }
        Map<String, String> existing = loadInpatientFingerprints(tenantId, ids);
        List<HisInpatientChargeMirror> toInsert = new ArrayList<>();
        int skipped = 0;
        int drift = 0;
        for (HisInpatientChargeMirror r : candidates)
        {
            String hid = r.getHisInpatientChargeId();
            String fp = r.getRowFingerprint();
            String old = existing.get(hid);
            if (old == null)
            {
                r.setId(IdUtils.fastUUID());
                toInsert.add(r);
                existing.put(hid, fp);
            }
            else if (Objects.equals(old, fp))
            {
                skipped++;
            }
            else
            {
                drift++;
            }
        }
        insertInpatientInBatches(toInsert);
        return new int[] { toInsert.size(), skipped, drift };
    }

    private int[] mergeOutpatientChunk(String tenantId, List<HisOutpatientChargeMirror> chunkRows)
    {
        List<HisOutpatientChargeMirror> candidates = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (HisOutpatientChargeMirror r : chunkRows)
        {
            if (StringUtils.isEmpty(r.getHisOutpatientChargeId()))
            {
                continue;
            }
            if (!seen.add(r.getHisOutpatientChargeId()))
            {
                continue;
            }
            candidates.add(r);
        }
        if (candidates.isEmpty())
        {
            return new int[] { 0, 0, 0 };
        }
        List<String> ids = new ArrayList<>(candidates.size());
        for (HisOutpatientChargeMirror c : candidates)
        {
            ids.add(c.getHisOutpatientChargeId());
        }
        Map<String, String> existing = loadOutpatientFingerprints(tenantId, ids);
        List<HisOutpatientChargeMirror> toInsert = new ArrayList<>();
        int skipped = 0;
        int drift = 0;
        for (HisOutpatientChargeMirror r : candidates)
        {
            String hid = r.getHisOutpatientChargeId();
            String fp = r.getRowFingerprint();
            String old = existing.get(hid);
            if (old == null)
            {
                r.setId(IdUtils.fastUUID());
                toInsert.add(r);
                existing.put(hid, fp);
            }
            else if (Objects.equals(old, fp))
            {
                skipped++;
            }
            else
            {
                drift++;
            }
        }
        insertOutpatientInBatches(toInsert);
        return new int[] { toInsert.size(), skipped, drift };
    }

    private Map<String, String> loadInpatientFingerprints(String tenantId, List<String> ids)
    {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < ids.size(); i += HIS_ID_QUERY_BATCH)
        {
            int end = Math.min(i + HIS_ID_QUERY_BATCH, ids.size());
            List<String> sub = ids.subList(i, end);
            List<HisIdFingerprint> rows = hisInpatientChargeMirrorMapper.selectFingerprintsByHisIds(tenantId, sub);
            for (HisIdFingerprint row : rows)
            {
                map.put(row.getHisChargeId(), row.getRowFingerprint());
            }
        }
        return map;
    }

    private Map<String, String> loadOutpatientFingerprints(String tenantId, List<String> ids)
    {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < ids.size(); i += HIS_ID_QUERY_BATCH)
        {
            int end = Math.min(i + HIS_ID_QUERY_BATCH, ids.size());
            List<String> sub = ids.subList(i, end);
            List<HisIdFingerprint> rows = hisOutpatientChargeMirrorMapper.selectFingerprintsByHisIds(tenantId, sub);
            for (HisIdFingerprint row : rows)
            {
                map.put(row.getHisChargeId(), row.getRowFingerprint());
            }
        }
        return map;
    }

    private void insertInpatientInBatches(List<HisInpatientChargeMirror> rows)
    {
        for (int i = 0; i < rows.size(); i += INSERT_BATCH_SIZE)
        {
            int end = Math.min(i + INSERT_BATCH_SIZE, rows.size());
            List<HisInpatientChargeMirror> slice = rows.subList(i, end);
            hisInpatientChargeMirrorMapper.insertBatch(slice);
            insertUnifiedInpatientSlice(slice);
        }
    }

    private void insertOutpatientInBatches(List<HisOutpatientChargeMirror> rows)
    {
        for (int i = 0; i < rows.size(); i += INSERT_BATCH_SIZE)
        {
            int end = Math.min(i + INSERT_BATCH_SIZE, rows.size());
            List<HisOutpatientChargeMirror> slice = rows.subList(i, end);
            hisOutpatientChargeMirrorMapper.insertBatch(slice);
            insertUnifiedOutpatientSlice(slice);
        }
    }

    /** 统一表分页：列表 SQL 无 JOIN；total 单独 count；转换 DTO 后须 wrapAsPage 保留总条数 */
    private static final class UnifiedMirrorPageSlice
    {
        private final List<HisPatientChargeMirrorUnified> rows;
        private final long total;
        private final int pageNum;
        private final int pageSize;

        private UnifiedMirrorPageSlice(List<HisPatientChargeMirrorUnified> rows, long total, int pageNum, int pageSize)
        {
            this.rows = rows;
            this.total = total;
            this.pageNum = pageNum;
            this.pageSize = pageSize;
        }
    }

    private UnifiedMirrorPageSlice selectUnifiedMirrorPageSlice(HisPatientChargeMirrorUnifiedQuery uq)
    {
        PageUtils.startPage(false);
        List<HisPatientChargeMirrorUnified> rows = hisPatientChargeMirrorUnifiedMapper.selectList(uq);
        long total = hisPatientChargeMirrorUnifiedMapper.countList(uq);
        int pageNum = 1;
        int pageSize = 10;
        Page<?> local = PageHelper.getLocalPage();
        if (local != null)
        {
            pageNum = local.getPageNum();
            pageSize = local.getPageSize();
        }
        PageHelper.clearPage();
        List<HisPatientChargeMirrorUnified> safeRows = rows != null ? rows : Collections.emptyList();
        return new UnifiedMirrorPageSlice(safeRows, total, pageNum, pageSize);
    }

    private static <T> Page<T> wrapAsPage(List<T> rows, UnifiedMirrorPageSlice slice)
    {
        Page<T> page = new Page<>(slice.pageNum, slice.pageSize);
        page.setTotal(slice.total);
        if (rows != null && !rows.isEmpty())
        {
            page.addAll(rows);
        }
        return page;
    }

    private void insertUnifiedInpatientSlice(List<HisInpatientChargeMirror> slice)
    {
        if (slice == null || slice.isEmpty())
        {
            return;
        }
        List<HisPatientChargeMirrorUnified> list = new ArrayList<>(slice.size());
        for (HisInpatientChargeMirror e : slice)
        {
            list.add(HisPatientChargeMirrorUnifiedSupport.fromInpatient(e));
        }
        enrichUnifiedValueLevel(slice.get(0).getTenantId(), list);
        hisPatientChargeMirrorUnifiedMapper.insertBatch(list);
    }

    private void insertUnifiedOutpatientSlice(List<HisOutpatientChargeMirror> slice)
    {
        if (slice == null || slice.isEmpty())
        {
            return;
        }
        List<HisPatientChargeMirrorUnified> list = new ArrayList<>(slice.size());
        for (HisOutpatientChargeMirror e : slice)
        {
            list.add(HisPatientChargeMirrorUnifiedSupport.fromOutpatient(e));
        }
        enrichUnifiedValueLevel(slice.get(0).getTenantId(), list);
        hisPatientChargeMirrorUnifiedMapper.insertBatch(list);
    }

    /** 入库前从收费项镜像补齐 value_level（默认低值 2） */
    private void enrichUnifiedValueLevel(String tenantId, List<HisPatientChargeMirrorUnified> list)
    {
        if (list == null || list.isEmpty() || StringUtils.isEmpty(tenantId))
        {
            return;
        }
        Set<String> needLookup = new HashSet<>();
        for (HisPatientChargeMirrorUnified m : list)
        {
            if (m == null)
            {
                continue;
            }
            m.setChargeItemId(HisPatientChargeMirrorUnifiedSupport.normalizeChargeItemId(m.getChargeItemId()));
            if (StringUtils.isBlank(m.getValueLevel()) && StringUtils.isNotBlank(m.getChargeItemId()))
            {
                needLookup.add(m.getChargeItemId());
            }
        }
        Map<String, String> levelByItemId = new HashMap<>();
        if (!needLookup.isEmpty())
        {
            List<String> ids = new ArrayList<>(needLookup);
            for (int i = 0; i < ids.size(); i += HIS_ID_QUERY_BATCH)
            {
                int end = Math.min(i + HIS_ID_QUERY_BATCH, ids.size());
                List<HisChargeItemMirror> mirrors = hisChargeItemMirrorMapper.selectValueLevelsByChargeItemIds(
                    tenantId, ids.subList(i, end));
                if (mirrors == null)
                {
                    continue;
                }
                for (HisChargeItemMirror cim : mirrors)
                {
                    if (cim != null && StringUtils.isNotBlank(cim.getChargeItemId()))
                    {
                        levelByItemId.put(cim.getChargeItemId(), StringUtils.trimToEmpty(cim.getValueLevel()));
                    }
                }
            }
        }
        for (HisPatientChargeMirrorUnified m : list)
        {
            if (m == null)
            {
                continue;
            }
            if (StringUtils.isNotBlank(m.getValueLevel()))
            {
                m.setValueLevel(StringUtils.trim(m.getValueLevel()));
                continue;
            }
            String fromMirror = levelByItemId.get(m.getChargeItemId());
            m.setValueLevel(StringUtils.isNotBlank(fromMirror) ? fromMirror : "2");
        }
    }

    /** 库存列已移出 SQL：仅对当前页按库存列做二次排序（跨页全局排序需另行物化）。 */
    private static void sortInpatientPageByStockIfNeeded(List<HisInpatientChargeMirror> rows, HisInpatientChargeMirror query)
    {
        if (rows == null || rows.isEmpty() || query == null)
        {
            return;
        }
        String col = query.getOrderByColumn();
        if (!"highValueStockQty".equals(col) && !"lowValueStockQty".equals(col))
        {
            return;
        }
        boolean asc = "asc".equalsIgnoreCase(query.getIsAsc());
        Comparator<HisInpatientChargeMirror> c = "highValueStockQty".equals(col)
            ? Comparator.comparing(r -> r.getHighValueStockQty() == null ? BigDecimal.ZERO : r.getHighValueStockQty())
            : Comparator.comparing(r -> r.getLowValueStockQty() == null ? BigDecimal.ZERO : r.getLowValueStockQty());
        if (!asc)
        {
            c = c.reversed();
        }
        c = c.thenComparing((a, b) -> compareDateDesc(a.getChargeDate(), b.getChargeDate()))
            .thenComparing((a, b) -> StringUtils.defaultString(b.getId()).compareTo(StringUtils.defaultString(a.getId())));
        rows.sort(c);
    }

    private static void sortOutpatientPageByStockIfNeeded(List<HisOutpatientChargeMirror> rows, HisOutpatientChargeMirror query)
    {
        if (rows == null || rows.isEmpty() || query == null)
        {
            return;
        }
        String col = query.getOrderByColumn();
        if (!"highValueStockQty".equals(col) && !"lowValueStockQty".equals(col))
        {
            return;
        }
        boolean asc = "asc".equalsIgnoreCase(query.getIsAsc());
        Comparator<HisOutpatientChargeMirror> c = "highValueStockQty".equals(col)
            ? Comparator.comparing(r -> r.getHighValueStockQty() == null ? BigDecimal.ZERO : r.getHighValueStockQty())
            : Comparator.comparing(r -> r.getLowValueStockQty() == null ? BigDecimal.ZERO : r.getLowValueStockQty());
        if (!asc)
        {
            c = c.reversed();
        }
        c = c.thenComparing((a, b) -> compareDateDesc(DateUtils.parseDate(a.getChargeDate()), DateUtils.parseDate(b.getChargeDate())))
            .thenComparing((a, b) -> StringUtils.defaultString(b.getId()).compareTo(StringUtils.defaultString(a.getId())));
        rows.sort(c);
    }

    private static void sortDetailPageByStockIfNeeded(List<HisPatientChargeDetailRow> rows, HisPatientChargeAllQuery query)
    {
        if (rows == null || rows.isEmpty() || query == null)
        {
            return;
        }
        String col = query.getOrderByColumn();
        if (!"highValueStockQty".equals(col) && !"lowValueStockQty".equals(col))
        {
            return;
        }
        boolean asc = "asc".equalsIgnoreCase(query.getIsAsc());
        Comparator<HisPatientChargeDetailRow> c = "highValueStockQty".equals(col)
            ? Comparator.comparing(r -> r.getHighValueStockQty() == null ? BigDecimal.ZERO : r.getHighValueStockQty())
            : Comparator.comparing(r -> r.getLowValueStockQty() == null ? BigDecimal.ZERO : r.getLowValueStockQty());
        if (!asc)
        {
            c = c.reversed();
        }
        c = c.thenComparing((a, b) -> compareDateDesc(a.getChargeDate(), b.getChargeDate()))
            .thenComparing((a, b) -> StringUtils.defaultString(b.getId()).compareTo(StringUtils.defaultString(a.getId())));
        rows.sort(c);
    }

    private static int compareDateDesc(Date a, Date b)
    {
        if (a == null && b == null)
        {
            return 0;
        }
        if (a == null)
        {
            return 1;
        }
        if (b == null)
        {
            return -1;
        }
        return b.compareTo(a);
    }

    private static String fingerprintInpatient(HisInpatientChargeMirror e)
    {
        String raw = String.join("|",
            nz(e.getHisInpatientChargeId()),
            nz(e.getHisInpatientChargeIdTf()),
            nz(e.getPatientId()),
            nz(e.getChargeItemId()),
            nz(e.getQuantity()),
            nz(e.getUnitPrice()),
            nz(e.getTotalAmount()),
            nz(e.getChargeDate()),
            nz(e.getDeptCode()));
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }

    private static String fingerprintOutpatient(HisOutpatientChargeMirror e)
    {
        String raw = String.join("|",
            nz(e.getHisOutpatientChargeId()),
            nz(e.getHisOutpatientChargeIdTf()),
            nz(e.getPatientId()),
            nz(e.getChargeItemId()),
            nz(e.getQuantity()),
            nz(e.getUnitPrice()),
            nz(e.getTotalAmount()),
            nz(e.getChargeDate()),
            nz(e.getClinicCode()));
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }

    private static String nz(Object o)
    {
        if (o == null)
        {
            return "";
        }
        if (o instanceof BigDecimal)
        {
            return ((BigDecimal) o).stripTrailingZeros().toPlainString();
        }
        return String.valueOf(o);
    }

    private static String toHisIdString(Object o)
    {
        if (o == null)
        {
            return null;
        }
        if (o instanceof BigDecimal)
        {
            return ((BigDecimal) o).stripTrailingZeros().toPlainString();
        }
        String s = String.valueOf(o).trim();
        return s.isEmpty() ? null : s;
    }

    private static String trimToNull(String s)
    {
        if (s == null)
        {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static Date parseHisDateTime(Object raw)
    {
        if (raw == null)
        {
            return null;
        }
        if (raw instanceof Timestamp)
        {
            return new Date(((Timestamp) raw).getTime());
        }
        if (raw instanceof java.util.Date)
        {
            return new Date(((java.util.Date) raw).getTime());
        }
        String text = String.valueOf(raw).trim();
        if (text.isEmpty())
        {
            return null;
        }
        String normalized = text.replace('/', '-');
        if (normalized.length() == 10)
        {
            normalized = normalized + " 00:00:00";
        }
        if (normalized.length() > 19)
        {
            normalized = normalized.substring(0, 19);
        }
        try
        {
            LocalDateTime ldt = LocalDateTime.parse(normalized, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return Timestamp.valueOf(ldt);
        }
        catch (Exception ex)
        {
            return null;
        }
    }

}

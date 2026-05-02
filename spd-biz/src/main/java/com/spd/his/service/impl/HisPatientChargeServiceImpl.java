package com.spd.his.service.impl;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import com.spd.common.utils.PageUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.uuid.IdUtils;
import com.spd.his.config.HisSqlServerProperties;
import com.spd.his.config.HisTenantDbHandle;
import com.spd.his.config.HisTenantJdbcAccess;
import com.spd.his.domain.HisChargeFetchBatch;
import com.spd.his.domain.HisInpatientChargeMirror;
import com.spd.his.domain.HisOutpatientChargeMirror;
import com.spd.his.domain.dto.HisFetchResultVo;
import com.spd.his.domain.dto.HisIdFingerprint;
import com.spd.his.domain.dto.HisPatientChargeFetchBody;
import com.spd.his.domain.dto.HisPatientChargeSummaryRow;
import com.spd.his.mapper.HisChargeFetchBatchMapper;
import com.spd.his.mapper.HisInpatientChargeMirrorMapper;
import com.spd.his.mapper.HisOutpatientChargeMirrorMapper;
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
import com.spd.his.domain.dto.HisMirrorConsumeRecordVo;
import com.spd.his.domain.dto.HisTenantBillingSettingBody;
import com.spd.his.constants.HisBillingTenantConstants;
import com.spd.his.service.IHisMirrorConsumeManualService;
import com.spd.his.service.IHisPatientChargeService;
import com.spd.foundation.service.ISbTenantSettingService;
import com.spd.system.service.ITenantScopeService;

@Service
public class HisPatientChargeServiceImpl implements IHisPatientChargeService
{
    private static final Logger log = LoggerFactory.getLogger(HisPatientChargeServiceImpl.class);

    private static final int HIS_ID_QUERY_BATCH = 400;
    private static final int INSERT_BATCH_SIZE = 80;
    private static final String KIND_IN = "INPATIENT";

    @Autowired
    private HisTenantJdbcAccess hisTenantJdbcAccess;

    @Autowired
    private HisSqlServerProperties hisSqlServerProperties;

    @Autowired
    private HisInpatientChargeMirrorMapper hisInpatientChargeMirrorMapper;

    @Autowired
    private HisOutpatientChargeMirrorMapper hisOutpatientChargeMirrorMapper;

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

    @Override
    public HisFetchResultVo fetchInpatientMirror(HisPatientChargeFetchBody body)
    {
        assertTenantAllowed();
        LocalDateTime[] win = parseWindow(body);
        String tenantId = SecurityUtils.getCustomerId();
        HisTenantDbHandle hisDb = hisTenantJdbcAccess.obtainHandle(tenantId);
        String batchId = IdUtils.fastUUID();
        String createBy = SecurityUtils.getUsername();
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
        return vo;
    }

    @Override
    public HisFetchResultVo fetchOutpatientMirror(HisPatientChargeFetchBody body)
    {
        assertTenantAllowed();
        LocalDateTime[] win = parseWindow(body);
        String tenantId = SecurityUtils.getCustomerId();
        HisTenantDbHandle hisDb = hisTenantJdbcAccess.obtainHandle(tenantId);
        String batchId = IdUtils.fastUUID();
        String createBy = SecurityUtils.getUsername();
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
        return vo;
    }

    @Override
    public HisTenantBillingSettingBody getTenantBillingSetting()
    {
        assertHengsuiBillingTenantOnly();
        HisTenantBillingSettingBody b = new HisTenantBillingSettingBody();
        String v = sbTenantSettingService.getSettingValue(SecurityUtils.getCustomerId(),
            HisBillingTenantConstants.SETTING_LV_AUTO_CONSUME_ENABLED, "0");
        b.setLvAutoConsumeEnabled(v);
        return b;
    }

    @Override
    public void saveTenantBillingSetting(HisTenantBillingSettingBody body)
    {
        assertHengsuiBillingTenantOnly();
        String on = body == null ? "0" : StringUtils.trimToEmpty(body.getLvAutoConsumeEnabled());
        if (!"0".equals(on) && !"1".equals(on))
        {
            throw new ServiceException("lvAutoConsumeEnabled 仅支持 0 或 1");
        }
        sbTenantSettingService.saveSettingValue(SecurityUtils.getCustomerId(),
            HisBillingTenantConstants.SETTING_LV_AUTO_CONSUME_ENABLED, on, "低值计费抓取后自动生成消耗");
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

    private static boolean isLowValueMirrorRow(String valueLevel)
    {
        String v = StringUtils.trimToEmpty(valueLevel);
        return v.isEmpty() || "2".equals(v);
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
        PageUtils.startPage();
        return hisInpatientChargeMirrorMapper.selectMirrorList(query);
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
        PageUtils.startPage();
        return hisOutpatientChargeMirrorMapper.selectMirrorList(query);
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
        PageUtils.startPage();
        return hisInpatientChargeMirrorMapper.selectAllMirrorList(q);
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
            throw new ServiceException("请指定抓取开始日期与结束日期");
        }
        LocalDate begin;
        LocalDate end;
        try
        {
            begin = LocalDate.parse(body.getBeginDate().trim());
            end = LocalDate.parse(body.getEndDate().trim());
        }
        catch (Exception e)
        {
            throw new ServiceException("日期格式须为 yyyy-MM-dd");
        }
        if (end.isBefore(begin))
        {
            throw new ServiceException("结束日期不能早于开始日期");
        }
        long spanDays = ChronoUnit.DAYS.between(begin, end) + 1;
        int maxDays = Math.max(1, hisSqlServerProperties.getFetch().getMaxRangeDays());
        if (spanDays > maxDays)
        {
            throw new ServiceException("单次抓取跨度不能超过 " + maxDays + " 天，请缩小时间范围");
        }
        LocalDateTime start = begin.atStartOfDay();
        LocalDateTime endExclusive = end.plusDays(1).atStartOfDay();
        return new LocalDateTime[] { start, endExclusive };
    }

    private List<HisInpatientChargeMirror> queryInpatientChunk(
        HisTenantDbHandle hisDb,
        LocalDateTime chunkStart, LocalDateTime chunkEndExcl,
        String tenantId, String batchId, String createBy, Date createTime)
    {
        Timestamp t0 = Timestamp.valueOf(chunkStart);
        Timestamp t1 = Timestamp.valueOf(chunkEndExcl);
        RowMapper<HisInpatientChargeMirror> rm = (rs, rowNum) -> mapInpatientRow(rs, tenantId, batchId, createBy, createTime);
        return hisDb.getJdbcTemplate().query(hisDb.getInpatientRangeSql(), ps ->
        {
            ps.setTimestamp(1, t0);
            ps.setTimestamp(2, t1);
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
        Timestamp t0 = Timestamp.valueOf(chunkStart);
        Timestamp t1 = Timestamp.valueOf(chunkEndExcl);
        RowMapper<HisOutpatientChargeMirror> rm = (rs, rowNum) -> mapOutpatientRow(rs, tenantId, batchId, createBy, createTime);
        return hisDb.getJdbcTemplate().query(hisDb.getOutpatientRangeSql(), ps ->
        {
            ps.setTimestamp(1, t0);
            ps.setTimestamp(2, t1);
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
            hisInpatientChargeMirrorMapper.insertBatch(rows.subList(i, end));
        }
    }

    private void insertOutpatientInBatches(List<HisOutpatientChargeMirror> rows)
    {
        for (int i = 0; i < rows.size(); i += INSERT_BATCH_SIZE)
        {
            int end = Math.min(i + INSERT_BATCH_SIZE, rows.size());
            hisOutpatientChargeMirrorMapper.insertBatch(rows.subList(i, end));
        }
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

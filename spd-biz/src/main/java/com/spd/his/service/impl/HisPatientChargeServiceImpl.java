package com.spd.his.service.impl;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import com.spd.common.exception.ServiceException;
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
import com.spd.his.domain.dto.HisGenerateConsumeResultVo;
import com.spd.his.domain.dto.HisMirrorHighApplyBody;
import com.spd.his.domain.dto.HisMirrorHighApplyResultVo;
import com.spd.his.domain.dto.HisMirrorHighScanBody;
import com.spd.his.domain.dto.HisMirrorHighScanResultVo;
import com.spd.his.domain.dto.HisMirrorManualRowBody;
import com.spd.his.service.IHisMirrorConsumeManualService;
import com.spd.his.service.IHisPatientChargeService;

@Service
public class HisPatientChargeServiceImpl implements IHisPatientChargeService
{
    private static final int HIS_ID_QUERY_BATCH = 400;
    private static final int INSERT_BATCH_SIZE = 80;

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
        return vo;
    }

    @Override
    public List<HisInpatientChargeMirror> selectInpatientMirrorList(HisInpatientChargeMirror query)
    {
        if (query == null || StringUtils.isEmpty(query.getTenantId()))
        {
            query = query == null ? new HisInpatientChargeMirror() : query;
            query.setTenantId(SecurityUtils.getCustomerId());
        }
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
        return hisOutpatientChargeMirrorMapper.selectMirrorList(query);
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
        return hisMirrorConsumeManualService.processLowValue(body);
    }

    @Override
    public HisMirrorHighScanResultVo scanMirrorHighBarcode(HisMirrorHighScanBody body)
    {
        assertTenantAllowed();
        return hisMirrorConsumeManualService.scanHighBarcode(body);
    }

    @Override
    public HisMirrorHighApplyResultVo applyMirrorHighConsume(HisMirrorHighApplyBody body)
    {
        assertTenantAllowed();
        return hisMirrorConsumeManualService.applyHighConsume(body);
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
        e.setUseDate(rs.getString("use_date"));
        e.setChargeDate(rs.getString("charge_date"));
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
}

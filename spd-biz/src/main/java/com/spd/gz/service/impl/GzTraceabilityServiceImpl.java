package com.spd.gz.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.gz.domain.GzDepInventory;
import com.spd.gz.domain.GzMaterialUsageReportQuery;
import com.spd.gz.domain.GzMaterialUsageReportVo;
import com.spd.gz.domain.GzTraceability;
import com.spd.gz.domain.GzTraceabilityEntry;
import com.spd.gz.domain.GzTraceabilitySummaryVo;
import com.spd.gz.mapper.GzDepInventoryMapper;
import com.spd.gz.mapper.GzTraceabilityMapper;
import com.spd.gz.service.IGzTraceabilityService;
import com.spd.gz.support.GzTraceSourceConstants;
import com.spd.hc.service.IHcBarcodeLifecycleService;
import com.spd.his.mapper.HisInpatientChargeMirrorMapper;
import com.spd.his.mapper.HisMirrorConsumeLinkMapper;
import com.spd.his.mapper.HisOutpatientChargeMirrorMapper;
import com.spd.his.mapper.HisPatientChargeMirrorUnifiedMapper;

/**
 * 高值追溯单Service业务层处理
 *
 * @author spd
 * @date 2025-01-01
 */
@Service
public class GzTraceabilityServiceImpl implements IGzTraceabilityService
{
    @Autowired
    private GzTraceabilityMapper gzTraceabilityMapper;
    
    @Autowired
    private GzDepInventoryMapper gzDepInventoryMapper;

    @Autowired
    private IHcBarcodeLifecycleService hcBarcodeLifecycleService;

    @Autowired
    private HisMirrorConsumeLinkMapper hisMirrorConsumeLinkMapper;

    @Autowired
    private HisInpatientChargeMirrorMapper hisInpatientChargeMirrorMapper;

    @Autowired
    private HisOutpatientChargeMirrorMapper hisOutpatientChargeMirrorMapper;

    @Autowired
    private HisPatientChargeMirrorUnifiedMapper hisPatientChargeMirrorUnifiedMapper;

    /**
     * 查询高值追溯单
     *
     * @param id 高值追溯单主键
     * @return 高值追溯单
     */
    @Override
    public GzTraceability selectGzTraceabilityById(Long id)
    {
        GzTraceability traceability = gzTraceabilityMapper.selectGzTraceabilityById(id);
        // 明细列表已通过Mapper的collection自动加载
        return traceability;
    }

    /**
     * 查询高值追溯单列表
     *
     * @param gzTraceability 高值追溯单
     * @return 高值追溯单
     */
    @Override
    public List<GzTraceability> selectGzTraceabilityList(GzTraceability gzTraceability)
    {
        if (gzTraceability != null && StringUtils.isEmpty(gzTraceability.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            gzTraceability.setTenantId(SecurityUtils.getCustomerId());
        }
        return gzTraceabilityMapper.selectGzTraceabilityList(gzTraceability);
    }

    /**
     * 新增高值追溯单
     *
     * @param gzTraceability 高值追溯单
     * @return 结果
     */
    @Transactional
    @Override
    public int insertGzTraceability(GzTraceability gzTraceability)
    {
        if (gzTraceability == null) {
            throw new ServiceException("高值追溯单不能为空");
        }
        if (StringUtils.isEmpty(gzTraceability.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            gzTraceability.setTenantId(SecurityUtils.getCustomerId());
        }
        // 生成追溯单号
        if (StringUtils.isEmpty(gzTraceability.getTraceNo())) {
            gzTraceability.setTraceNo(generateTraceNo());
        }
        
        gzTraceability.setOrderStatus(1); // 默认未审核状态
        gzTraceability.setDelFlag("0");
        gzTraceability.setCreateTime(DateUtils.getNowDate());
        gzTraceability.setCreateBy(SecurityUtils.getUserIdStr());
        
        int rows = gzTraceabilityMapper.insertGzTraceability(gzTraceability);
        insertGzTraceabilityEntry(gzTraceability);
        // 扣减科室库存
        deductDepartmentInventory(gzTraceability);
        return rows;
    }

    /**
     * 写入已审核的高值消耗追溯记录（库存已在科室消耗单审核时扣减，此处不再重复扣减）
     */
    @Transactional
    @Override
    public int insertConsumedTraceability(GzTraceability gzTraceability)
    {
        if (gzTraceability == null)
        {
            throw new ServiceException("高值追溯单不能为空");
        }
        if (StringUtils.isEmpty(gzTraceability.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            gzTraceability.setTenantId(SecurityUtils.getCustomerId());
        }
        if (StringUtils.isEmpty(gzTraceability.getTraceNo()))
        {
            gzTraceability.setTraceNo(generateTraceNo());
        }
        Date now = DateUtils.getNowDate();
        String userId = SecurityUtils.getUserIdStr();
        if (gzTraceability.getOrderStatus() == null)
        {
            gzTraceability.setOrderStatus(2);
        }
        if (gzTraceability.getAuditDate() == null && Integer.valueOf(2).equals(gzTraceability.getOrderStatus()))
        {
            gzTraceability.setAuditDate(now);
        }
        if (StringUtils.isEmpty(gzTraceability.getAuditBy()) && Integer.valueOf(2).equals(gzTraceability.getOrderStatus()))
        {
            gzTraceability.setAuditBy(userId);
        }
        gzTraceability.setDelFlag("0");
        gzTraceability.setCreateTime(now);
        if (StringUtils.isEmpty(gzTraceability.getCreateBy()))
        {
            gzTraceability.setCreateBy(userId);
        }
        int rows = gzTraceabilityMapper.insertGzTraceability(gzTraceability);
        insertGzTraceabilityEntry(gzTraceability);
        return rows;
    }

    /**
     * HIS 镜像高值扫码核销：已审核计费单 + 扣库存 + 高值科室流水
     */
    @Transactional
    @Override
    public GzTraceability insertAuditedMirrorHighBill(GzTraceability gzTraceability)
    {
        if (gzTraceability == null || gzTraceability.getTraceabilityEntryList() == null
            || gzTraceability.getTraceabilityEntryList().isEmpty())
        {
            throw new ServiceException("高值计费明细不能为空");
        }
        if (StringUtils.isEmpty(gzTraceability.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            gzTraceability.setTenantId(SecurityUtils.getCustomerId());
        }
        if (StringUtils.isEmpty(gzTraceability.getTraceNo()))
        {
            gzTraceability.setTraceNo(generateTraceNo());
        }
        Date now = DateUtils.getNowDate();
        String userId = SecurityUtils.getUserIdStr();
        gzTraceability.setTraceSource(StringUtils.isNotEmpty(gzTraceability.getTraceSource())
            ? gzTraceability.getTraceSource() : GzTraceSourceConstants.HIS_MIRROR_HIGH);
        gzTraceability.setOrderStatus(2);
        gzTraceability.setAuditDate(now);
        gzTraceability.setAuditBy(userId);
        gzTraceability.setDelFlag("0");
        gzTraceability.setCreateTime(now);
        if (StringUtils.isEmpty(gzTraceability.getCreateBy()))
        {
            gzTraceability.setCreateBy(userId);
        }
        gzTraceabilityMapper.insertGzTraceability(gzTraceability);
        insertGzTraceabilityEntry(gzTraceability);
        deductDepartmentInventory(gzTraceability);
        GzTraceability persisted = selectGzTraceabilityById(gzTraceability.getId());
        if (persisted == null || persisted.getTraceabilityEntryList() == null)
        {
            throw new ServiceException("高值计费单保存失败");
        }
        for (GzTraceabilityEntry entry : persisted.getTraceabilityEntryList())
        {
            if (entry == null || entry.getInventoryId() == null)
            {
                continue;
            }
            GzDepInventory gz = gzDepInventoryMapper.selectGzDepInventoryById(entry.getInventoryId());
            if (gz != null)
            {
                hcBarcodeLifecycleService.onMirrorHighChargeConsume(persisted, entry, gz);
            }
        }
        return persisted;
    }

    /**
     * 高值冲销反向计费单：只落单据与明细（数量为负），不改库存（库存已由冲销回补）；同时标记原单已冲销。
     */
    @Transactional
    @Override
    public GzTraceability insertReverseWriteOffBill(GzTraceability original,
        Map<Long, BigDecimal> entryReturnQty, Map<Long, String> entryLinkIds, String remark)
    {
        if (original == null || original.getId() == null)
        {
            throw new ServiceException("冲销反向单失败：原计费单不存在");
        }
        if (entryReturnQty == null || entryReturnQty.isEmpty())
        {
            throw new ServiceException("冲销反向单失败：无冲销明细");
        }
        List<GzTraceabilityEntry> srcEntries = original.getTraceabilityEntryList();
        if (srcEntries == null || srcEntries.isEmpty())
        {
            throw new ServiceException("冲销反向单失败：原计费单无明细");
        }
        List<GzTraceabilityEntry> revEntries = new ArrayList<>();
        for (GzTraceabilityEntry src : srcEntries)
        {
            if (src == null || src.getId() == null)
            {
                continue;
            }
            BigDecimal q = entryReturnQty.get(src.getId());
            if (q == null || q.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            GzTraceabilityEntry e = new GzTraceabilityEntry();
            e.setMaterialId(src.getMaterialId());
            e.setInventoryId(src.getInventoryId());
            e.setMaterialName(src.getMaterialName());
            e.setSpecification(src.getSpecification());
            e.setModel(src.getModel());
            e.setUnit(src.getUnit());
            e.setQuantity(q.negate());
            e.setSrcConsumeLinkId(entryLinkIds == null ? null : entryLinkIds.get(src.getId()));
            e.setChargePrice(src.getChargePrice());
            e.setBatchNo(src.getBatchNo());
            e.setExpiryDate(src.getExpiryDate());
            e.setMasterBarcode(src.getMasterBarcode());
            e.setSecondaryBarcode(src.getSecondaryBarcode());
            e.setInHospitalCode(src.getInHospitalCode());
            e.setManufacturer(src.getManufacturer());
            e.setSupplier(src.getSupplier());
            e.setSupplierId(src.getSupplierId());
            e.setCertificateNo(src.getCertificateNo());
            e.setBillingFollow(src.getBillingFollow());
            revEntries.add(e);
        }
        if (revEntries.isEmpty())
        {
            throw new ServiceException("冲销反向单失败：未匹配到可冲销明细");
        }
        Date now = DateUtils.getNowDate();
        String userId = SecurityUtils.getUserIdStr();
        GzTraceability rev = new GzTraceability();
        rev.setTenantId(StringUtils.isNotEmpty(original.getTenantId())
            ? original.getTenantId() : SecurityUtils.getCustomerId());
        rev.setTraceNo(generateTraceNo());
        rev.setMedicalRecordNo(original.getMedicalRecordNo());
        rev.setPatientName(original.getPatientName());
        rev.setPatientSex(original.getPatientSex());
        rev.setPatientAge(original.getPatientAge());
        rev.setHospitalNumber(original.getHospitalNumber());
        rev.setWard(original.getWard());
        rev.setWardNo(original.getWardNo());
        rev.setBedNo(original.getBedNo());
        rev.setApplyDeptId(original.getApplyDeptId());
        rev.setExecDeptId(original.getExecDeptId());
        rev.setWriteOffDeptId(original.getWriteOffDeptId());
        rev.setHospitalDate(original.getHospitalDate());
        rev.setContactPhone(original.getContactPhone());
        rev.setContactAddress(original.getContactAddress());
        rev.setChiefSurgeon(original.getChiefSurgeon());
        rev.setSurgeryDate(original.getSurgeryDate());
        rev.setSurgeryName(original.getSurgeryName());
        rev.setAdmissionDiagnosis(original.getAdmissionDiagnosis());
        rev.setSurgeryId(original.getSurgeryId());
        rev.setVisitKind(original.getVisitKind());
        rev.setMirrorRowId(original.getMirrorRowId());
        rev.setTraceSource(GzTraceSourceConstants.HIS_MIRROR_HIGH_REVERSE);
        rev.setReverseFlag(1);
        rev.setReverseOfTraceId(original.getId());
        rev.setReverseOfTraceNo(original.getTraceNo());
        rev.setOrderStatus(2);
        rev.setAuditDate(now);
        rev.setAuditBy(userId);
        rev.setDelFlag("0");
        rev.setCreateBy(userId);
        rev.setCreateTime(now);
        String rm = StringUtils.isNotEmpty(remark) ? remark.trim() : "高值核销冲销";
        rev.setRemark(rm + "；冲销原单 " + StringUtils.defaultString(original.getTraceNo()));
        rev.setTraceabilityEntryList(revEntries);
        gzTraceabilityMapper.insertGzTraceability(rev);
        insertGzTraceabilityEntry(rev);
        gzTraceabilityMapper.markTraceWrittenOff(original.getId(), userId, now, rev.getTraceNo());
        return selectGzTraceabilityById(rev.getId());
    }

    /**
     * 修改高值追溯单
     *
     * @param gzTraceability 高值追溯单
     * @return 结果
     */
    @Transactional
    @Override
    public int updateGzTraceability(GzTraceability gzTraceability)
    {
        // 先恢复旧的明细占用的库存
        restoreDepartmentInventory(gzTraceability.getId());
        
        if (StringUtils.isEmpty(gzTraceability.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            gzTraceability.setTenantId(SecurityUtils.getCustomerId());
        }
        gzTraceability.setUpdateTime(DateUtils.getNowDate());
        gzTraceability.setUpdateBy(SecurityUtils.getUserIdStr());
        gzTraceabilityMapper.deleteGzTraceabilityEntryByParentId(gzTraceability.getId(), SecurityUtils.getUserIdStr());
        insertGzTraceabilityEntry(gzTraceability);
        
        // 扣减新的明细占用的库存
        deductDepartmentInventory(gzTraceability);
        
        return gzTraceabilityMapper.updateGzTraceability(gzTraceability);
    }

    /**
     * 批量删除高值追溯单
     *
     * @param ids 需要删除的高值追溯单主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteGzTraceabilityByIds(Long[] ids)
    {
        if (ids == null || ids.length == 0) {
            return 0;
        }
        String operator = SecurityUtils.getUserIdStr();
        for (Long id : ids) {
            assertMayDeleteTrace(id);
            releaseMirrorLinksAfterTraceDelete(id, operator);
            restoreDepartmentInventory(id);
        }
        return gzTraceabilityMapper.deleteGzTraceabilityByIds(ids, operator);
    }

    /**
     * 删除高值追溯单信息
     *
     * @param id 高值追溯单主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteGzTraceabilityById(Long id)
    {
        assertMayDeleteTrace(id);
        String operator = SecurityUtils.getUserIdStr();
        releaseMirrorLinksAfterTraceDelete(id, operator);
        restoreDepartmentInventory(id);
        return gzTraceabilityMapper.deleteGzTraceabilityById(id, operator);
    }

    /**
     * HIS 镜像高值核销单删除后：软删消耗关联；若该计费行已无有效净核销量则回退为待核销。
     */
    private void releaseMirrorLinksAfterTraceDelete(Long traceabilityId, String operator)
    {
        if (traceabilityId == null)
        {
            return;
        }
        GzTraceability trace = gzTraceabilityMapper.selectGzTraceabilityById(traceabilityId);
        if (trace == null)
        {
            return;
        }
        String tenantId = StringUtils.isNotEmpty(trace.getTenantId())
            ? trace.getTenantId() : SecurityUtils.getCustomerId();
        if (StringUtils.isEmpty(tenantId))
        {
            return;
        }
        hisMirrorConsumeLinkMapper.softDeleteLinksByTraceabilityId(tenantId, traceabilityId, operator);
        if (!GzTraceSourceConstants.HIS_MIRROR_HIGH.equals(StringUtils.trimToEmpty(trace.getTraceSource())))
        {
            return;
        }
        String visitKind = StringUtils.trimToEmpty(trace.getVisitKind()).toUpperCase();
        String mirrorRowId = StringUtils.trimToEmpty(trace.getMirrorRowId());
        if (StringUtils.isEmpty(visitKind) || StringUtils.isEmpty(mirrorRowId))
        {
            return;
        }
        BigDecimal net = hisMirrorConsumeLinkMapper.sumActiveNetAllocQtyForMirrorRow(tenantId, visitKind, mirrorRowId);
        if (net != null && net.compareTo(BigDecimal.ZERO) > 0)
        {
            return;
        }
        Date procTime = DateUtils.getNowDate();
        List<String> ids = Collections.singletonList(mirrorRowId);
        if ("INPATIENT".equals(visitKind))
        {
            hisInpatientChargeMirrorMapper.updateMirrorProcessByIds(tenantId, ids,
                "PENDING_CONSUME", "", procTime, operator, "", "");
        }
        else
        {
            hisOutpatientChargeMirrorMapper.updateMirrorProcessByIds(tenantId, ids,
                "PENDING_CONSUME", "", procTime, operator, "", "");
        }
        hisPatientChargeMirrorUnifiedMapper.updateMirrorProcessByIds(tenantId, ids,
            "PENDING_CONSUME", "", procTime, operator, "", "");
    }

    /**
     * 审核高值追溯单
     *
     * @param id 高值追溯单主键
     * @return 结果
     */
    @Override
    public int auditGzTraceability(Long id)
    {
        GzTraceability gzTraceability = new GzTraceability();
        gzTraceability.setId(id);
        gzTraceability.setOrderStatus(2); // 已审核
        gzTraceability.setAuditDate(DateUtils.getNowDate());
        gzTraceability.setAuditBy(SecurityUtils.getUserIdStr());
        gzTraceability.setUpdateTime(DateUtils.getNowDate());
        gzTraceability.setUpdateBy(SecurityUtils.getUserIdStr());
        return gzTraceabilityMapper.updateGzTraceability(gzTraceability);
    }

    /**
     * 反审核高值追溯单
     *
     * <p>仅翻转 order_status，不回补库存、不清理消耗关联、不生成退货退库。
     * HIS 核销单及已进入确认/即入即出的单据禁止反审核，须走 HV-Q-006 冲销。</p>
     *
     * @param id 高值追溯单主键
     * @return 结果
     */
    @Override
    public int unauditGzTraceability(Long id)
    {
        GzTraceability existing = gzTraceabilityMapper.selectGzTraceabilityById(id);
        assertMayUnauditTrace(existing);
        GzTraceability gzTraceability = new GzTraceability();
        gzTraceability.setId(id);
        gzTraceability.setOrderStatus(1); // 未审核
        gzTraceability.setUpdateTime(DateUtils.getNowDate());
        gzTraceability.setUpdateBy(SecurityUtils.getUserIdStr());
        return gzTraceabilityMapper.updateGzTraceability(gzTraceability);
    }

    /** HIS 核销单禁止反审核；已确认/即入即出的单据亦禁止（与冲销入口冲突） */
    private void assertMayUnauditTrace(GzTraceability existing)
    {
        if (existing == null)
        {
            throw new ServiceException("追溯单不存在或已删除");
        }
        if (Integer.valueOf(1).equals(existing.getReverseFlag())
            || GzTraceSourceConstants.HIS_MIRROR_HIGH_REVERSE.equals(StringUtils.trimToEmpty(existing.getTraceSource())))
        {
            throw new ServiceException("冲销反向计费单禁止反审核");
        }
        if (GzTraceSourceConstants.HIS_MIRROR_HIGH.equals(StringUtils.trimToEmpty(existing.getTraceSource())))
        {
            throw new ServiceException(
                "HIS高值扫码核销生成的计费单禁止反审核。纠错请按进度到「高值核销确认」或「高值即入即出」做冲销");
        }
        assertNoDownstreamConfirmOrInstantIo(existing.getId(), "反审核");
    }

    /** 已进入临床确认或即入即出时禁止删除，须走冲销（否则库存/库房单据不一致） */
    private void assertMayDeleteTrace(Long id)
    {
        GzTraceability existing = gzTraceabilityMapper.selectGzTraceabilityById(id);
        if (existing == null)
        {
            return;
        }
        if (Integer.valueOf(1).equals(existing.getReverseFlag())
            || GzTraceSourceConstants.HIS_MIRROR_HIGH_REVERSE.equals(StringUtils.trimToEmpty(existing.getTraceSource())))
        {
            throw new ServiceException("冲销反向计费单禁止删除，以保留冲销凭证");
        }
        assertNoDownstreamConfirmOrInstantIo(id, "删除");
    }

    private void assertNoDownstreamConfirmOrInstantIo(Long traceabilityId, String action)
    {
        if (traceabilityId == null)
        {
            return;
        }
        int locked = hisMirrorConsumeLinkMapper.countDownstreamLockedLinksByTraceabilityId(traceabilityId);
        if (locked > 0)
        {
            throw new ServiceException(
                "该计费单已进入临床确认或即入即出，禁止" + action + "；请到「高值核销确认」或「高值即入即出」冲销");
        }
    }

    /**
     * 新增追溯单明细信息
     *
     * @param gzTraceability 追溯单对象
     */
    public void insertGzTraceabilityEntry(GzTraceability gzTraceability)
    {
        List<GzTraceabilityEntry> traceabilityEntryList = gzTraceability.getTraceabilityEntryList();
        Long id = gzTraceability.getId();
        if (StringUtils.isNotNull(traceabilityEntryList))
        {
            // tenant_id 在 mapper 批量写入时依赖 entry.tenantId，必须严格解析且不允许为空
            String tenantId = StringUtils.isNotEmpty(gzTraceability.getTenantId())
                ? gzTraceability.getTenantId()
                : SecurityUtils.requiredScopedTenantIdForSql();
            String userId = SecurityUtils.getUserIdStr();
            Date now = DateUtils.getNowDate();
            List<GzTraceabilityEntry> list = new ArrayList<GzTraceabilityEntry>();
            for (GzTraceabilityEntry entry : traceabilityEntryList)
            {
                if (entry == null) {
                    continue;
                }
                entry.setParentId(id);
                entry.setTraceNo(gzTraceability.getTraceNo());
                entry.setTenantId(tenantId);
                entry.setDelFlag("0");
                entry.setCreateTime(now);
                entry.setCreateBy(userId);
                entry.setUpdateTime(now);
                entry.setUpdateBy(userId);
                if (entry.getSupplierId() == null && entry.getInventoryId() != null) {
                    GzDepInventory depInventory = gzDepInventoryMapper.selectGzDepInventoryById(entry.getInventoryId());
                    if (depInventory != null) {
                        entry.setSupplierId(depInventory.getSupplierId());
                    }
                }
                list.add(entry);
            }
            if (list.size() > 0)
            {
                gzTraceabilityMapper.batchGzTraceabilityEntry(list);
            }
        }
    }

    /**
     * 生成追溯单号
     * 格式：GZ-01 + 当前日期（yyyyMMd格式，如2026011） + -01，然后自动叠加
     * 例如：GZ-012026011-01, GZ-012026011-02, GZ-012026011-03...
     *
     * @return 追溯单号
     */
    private static final Object GZ_TRACE_NO_LOCK = new Object();

    private String generateTraceNo() {
        synchronized (GZ_TRACE_NO_LOCK) {
            String prefix = "GZ-01";
            // 获取当前日期，格式为yyyyMMd（去掉最后一位）
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String dateFull = sdf.format(new Date());
            String date = dateFull.substring(0, 7); // 取前7位，如2026011

            // 查询当天最大的单号
            String maxTraceNo = gzTraceabilityMapper.selectMaxTraceNo(prefix, date);

            int sequence = 1;
            if (StringUtils.isNotEmpty(maxTraceNo)) {
                // 提取序号部分，格式：GZ-01 + 日期 + -序号
                String expectedPrefix = prefix + date + "-";
                if (maxTraceNo.startsWith(expectedPrefix)) {
                    String seqStr = maxTraceNo.substring(expectedPrefix.length());
                    try {
                        sequence = Integer.parseInt(seqStr) + 1;
                    } catch (NumberFormatException e) {
                        sequence = 1;
                    }
                }
            }

            // 格式化序号为两位数字
            String seqStr = String.format("%02d", sequence);
            return prefix + date + "-" + seqStr;
        }
    }
    
    /**
     * 扣减科室库存。
     * HIS 高值扫码核销可能只有 writeOffDeptId、execDeptId 为空；已绑定 inventoryId 时应照常扣减。
     */
    private void deductDepartmentInventory(GzTraceability gzTraceability) {
        if (gzTraceability.getTraceabilityEntryList() == null || gzTraceability.getTraceabilityEntryList().isEmpty()) {
            return;
        }

        Long deptForCodeLookup = gzTraceability.getWriteOffDeptId() != null
            ? gzTraceability.getWriteOffDeptId()
            : gzTraceability.getExecDeptId();

        for (GzTraceabilityEntry entry : gzTraceability.getTraceabilityEntryList()) {
            if (entry == null) {
                continue;
            }
            if (entry.getInventoryId() != null) {
                // 根据inventoryId扣减库存（不依赖 execDeptId）
                GzDepInventory inventory = gzDepInventoryMapper.selectGzDepInventoryById(entry.getInventoryId());
                if (inventory != null && inventory.getQty() != null) {
                    assertTraceEntryMatchesDepInventory(entry, inventory);
                    BigDecimal deductQty = entry.getQuantity() != null ? entry.getQuantity() : BigDecimal.ONE;
                    BigDecimal newQty = inventory.getQty().subtract(deductQty);
                    if (newQty.compareTo(BigDecimal.ZERO) < 0) {
                        newQty = BigDecimal.ZERO;
                    }
                    inventory.setQty(newQty);
                    syncDepInventoryAmt(inventory);
                    gzDepInventoryMapper.updateGzDepInventory(inventory);
                }
            } else if (deptForCodeLookup != null
                && entry.getInHospitalCode() != null && !entry.getInHospitalCode().trim().isEmpty()) {
                // 无 inventoryId：按院内码+核销/执行科室查找
                GzDepInventory inventory = gzDepInventoryMapper.selectGzDepInventoryByCodeAndDept(
                    entry.getInHospitalCode(), deptForCodeLookup);
                if (inventory != null && inventory.getQty() != null) {
                    BigDecimal deductQty = entry.getQuantity() != null ? entry.getQuantity() : BigDecimal.ONE;
                    BigDecimal newQty = inventory.getQty().subtract(deductQty);
                    if (newQty.compareTo(BigDecimal.ZERO) < 0) {
                        newQty = BigDecimal.ZERO;
                    }
                    inventory.setQty(newQty);
                    syncDepInventoryAmt(inventory);
                    gzDepInventoryMapper.updateGzDepInventory(inventory);
                }
            }
        }
    }
    
    /**
     * 查询追溯单明细列表（用于使用追溯明细表）
     *
     * @param gzTraceability 查询条件
     * @return 追溯单明细集合
     */
    @Override
    public List<GzTraceabilityEntry> selectTraceabilityEntryList(GzTraceability gzTraceability)
    {
        return gzTraceabilityMapper.selectTraceabilityEntryList(gzTraceability);
    }

    @Override
    public List<GzMaterialUsageReportVo> selectMaterialUsageReportList(GzMaterialUsageReportQuery query)
    {
        return gzTraceabilityMapper.selectMaterialUsageReportList(query);
    }

    @Override
    public List<GzTraceabilitySummaryVo> selectTraceabilitySummaryByExecDept(GzTraceability gzTraceability)
    {
        return gzTraceabilityMapper.selectTraceabilitySummaryByExecDept(gzTraceability);
    }

    @Override
    public List<GzTraceabilitySummaryVo> selectTraceabilitySummaryByApplyDept(GzTraceability gzTraceability)
    {
        return gzTraceabilityMapper.selectTraceabilitySummaryByApplyDept(gzTraceability);
    }

    @Override
    public List<GzTraceabilitySummaryVo> selectTraceabilitySummaryBySupplier(GzTraceability gzTraceability)
    {
        return gzTraceabilityMapper.selectTraceabilitySummaryBySupplier(gzTraceability);
    }

    @Override
    public com.spd.common.core.page.TotalInfo selectTraceabilityEntryListTotal(GzTraceability gzTraceability)
    {
        return gzTraceabilityMapper.selectTraceabilityEntryListTotal(gzTraceability);
    }

    @Override
    public com.spd.common.core.page.TotalInfo selectTraceabilitySummaryByExecDeptTotal(GzTraceability gzTraceability)
    {
        return gzTraceabilityMapper.selectTraceabilitySummaryByExecDeptTotal(gzTraceability);
    }

    @Override
    public com.spd.common.core.page.TotalInfo selectTraceabilitySummaryByApplyDeptTotal(GzTraceability gzTraceability)
    {
        return gzTraceabilityMapper.selectTraceabilitySummaryByApplyDeptTotal(gzTraceability);
    }

    @Override
    public com.spd.common.core.page.TotalInfo selectTraceabilitySummaryBySupplierTotal(GzTraceability gzTraceability)
    {
        return gzTraceabilityMapper.selectTraceabilitySummaryBySupplierTotal(gzTraceability);
    }

    /**
     * 恢复科室库存（修改追溯单时，先恢复旧的明细占用的库存）
     *
     * @param traceabilityId 追溯单ID
     */
    private void restoreDepartmentInventory(Long traceabilityId) {
        if (traceabilityId == null) {
            return;
        }
        // 查询旧的明细数据
        GzTraceability oldTraceability = gzTraceabilityMapper.selectGzTraceabilityById(traceabilityId);
        if (oldTraceability == null || oldTraceability.getTraceabilityEntryList() == null || oldTraceability.getTraceabilityEntryList().isEmpty()) {
            return;
        }
        
        Long deptForCodeLookup = oldTraceability.getWriteOffDeptId() != null
            ? oldTraceability.getWriteOffDeptId()
            : oldTraceability.getExecDeptId();

        for (GzTraceabilityEntry entry : oldTraceability.getTraceabilityEntryList()) {
            if (entry == null) {
                continue;
            }
            if (entry.getInventoryId() != null) {
                // 根据inventoryId恢复库存
                GzDepInventory inventory = gzDepInventoryMapper.selectGzDepInventoryById(entry.getInventoryId());
                if (inventory != null && inventory.getQty() != null) {
                    BigDecimal addQty = entry.getQuantity() != null ? entry.getQuantity() : BigDecimal.ONE;
                    inventory.setQty(inventory.getQty().add(addQty));
                    syncDepInventoryAmt(inventory);
                    gzDepInventoryMapper.updateGzDepInventory(inventory);
                }
            } else if (deptForCodeLookup != null
                && entry.getInHospitalCode() != null && !entry.getInHospitalCode().trim().isEmpty()) {
                // 如果没有inventoryId，根据院内码和核销/执行科室查找库存（不过滤数量为0的记录）
                GzDepInventory inventory = gzDepInventoryMapper.selectGzDepInventoryByCodeAndDept(
                    entry.getInHospitalCode(), deptForCodeLookup);
                if (inventory != null && inventory.getQty() != null) {
                    BigDecimal addQty = entry.getQuantity() != null ? entry.getQuantity() : BigDecimal.ONE;
                    inventory.setQty(inventory.getQty().add(addQty));
                    syncDepInventoryAmt(inventory);
                    gzDepInventoryMapper.updateGzDepInventory(inventory);
                }
            }
        }
    }

    /** 核销扣减科室库存时，明细院内码必须与库存行一致，避免 inventoryId 命中错码行 */
    private void assertTraceEntryMatchesDepInventory(GzTraceabilityEntry entry, GzDepInventory inventory)
    {
        if (entry == null || inventory == null || StringUtils.isEmpty(entry.getInHospitalCode())
            || StringUtils.isEmpty(inventory.getInHospitalCode()))
        {
            return;
        }
        if (!entry.getInHospitalCode().trim().equals(inventory.getInHospitalCode().trim()))
        {
            throw new ServiceException(String.format(
                "核销院内码 %s 与科室库存院内码 %s 不一致，请重新扫码",
                entry.getInHospitalCode().trim(), inventory.getInHospitalCode().trim()));
        }
    }

    private void syncDepInventoryAmt(GzDepInventory inventory)
    {
        if (inventory == null)
        {
            return;
        }
        BigDecimal qty = inventory.getQty() != null ? inventory.getQty() : BigDecimal.ZERO;
        BigDecimal unitPrice = inventory.getUnitPrice() != null ? inventory.getUnitPrice() : BigDecimal.ZERO;
        inventory.setAmt(unitPrice.multiply(qty));
    }
}

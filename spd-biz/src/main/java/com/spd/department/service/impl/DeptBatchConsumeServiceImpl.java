package com.spd.department.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.common.core.page.TotalInfo;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.common.utils.uuid.UUID7;
import com.spd.department.domain.DeptBatchConsumeReverseReq;
import com.spd.department.domain.HcKsFlow;
import com.spd.department.domain.StkDepInventory;
import com.spd.department.mapper.HcKsFlowMapper;
import com.spd.department.mapper.StkDepInventoryMapper;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.gz.domain.GzDepInventory;
import com.spd.gz.mapper.GzDepInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.spd.department.domain.DeptBatchConsumeEntry;
import com.spd.department.mapper.DeptBatchConsumeMapper;
import com.spd.department.domain.DeptBatchConsume;
import com.spd.department.service.IDeptBatchConsumeService;

/**
 * 科室批量消耗Service业务层处理
 *
 * @author spd
 * @date 2025-01-15
 */
@Service
public class DeptBatchConsumeServiceImpl implements IDeptBatchConsumeService
{
    @Autowired
    private DeptBatchConsumeMapper deptBatchConsumeMapper;
    @Autowired
    private FdMaterialMapper fdMaterialMapper;
    @Autowired
    private StkDepInventoryMapper stkDepInventoryMapper;
    @Autowired
    private HcKsFlowMapper hcKsFlowMapper;
    @Autowired
    private GzDepInventoryMapper gzDepInventoryMapper;

    /**
     * 查询科室批量消耗
     *
     * @param id 科室批量消耗主键
     * @return 科室批量消耗
     */
    @Override
    public DeptBatchConsume selectDeptBatchConsumeById(Long id)
    {
        DeptBatchConsume deptBatchConsume = deptBatchConsumeMapper.selectDeptBatchConsumeById(id);
        if (deptBatchConsume == null) {
            return null;
        }
        List<DeptBatchConsumeEntry> deptBatchConsumeEntryList = deptBatchConsume.getDeptBatchConsumeEntryList();
        if (deptBatchConsumeEntryList != null) {
            for (DeptBatchConsumeEntry deptBatchConsumeEntry : deptBatchConsumeEntryList) {
                if (deptBatchConsumeEntry == null) {
                    continue;
                }
                FdMaterial material = this.fdMaterialMapper.selectFdMaterialById(deptBatchConsumeEntry.getMaterialId());
                deptBatchConsumeEntry.setMaterial(material);
            }
        }
        return deptBatchConsume;
    }

    /**
     * 查询科室批量消耗列表
     *
     * @param deptBatchConsume 科室批量消耗
     * @return 科室批量消耗
     */
    @Override
    public List<DeptBatchConsume> selectDeptBatchConsumeList(DeptBatchConsume deptBatchConsume)
    {
        if (deptBatchConsume != null && StringUtils.isEmpty(deptBatchConsume.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            deptBatchConsume.setTenantId(SecurityUtils.getCustomerId());
        }
        return deptBatchConsumeMapper.selectDeptBatchConsumeList(deptBatchConsume);
    }

    /**
     * 新增科室批量消耗
     *
     * @param deptBatchConsume 科室批量消耗
     * @return 结果
     */
    @Transactional
    @Override
    public int insertDeptBatchConsume(DeptBatchConsume deptBatchConsume)
    {
        deptBatchConsume.setCreateTime(DateUtils.getNowDate());
        deptBatchConsume.setConsumeBillNo(getNumber());
        deptBatchConsume.setConsumeBillStatus(1); // 待审核状态
        deptBatchConsume.setDelFlag(0);
        if (StringUtils.isEmpty(deptBatchConsume.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            deptBatchConsume.setTenantId(SecurityUtils.getCustomerId());
        }
        int rows = deptBatchConsumeMapper.insertDeptBatchConsume(deptBatchConsume);
        int filteredCount = insertDeptBatchConsumeEntry(deptBatchConsume);
        deptBatchConsume.setDedupFilteredCount(filteredCount);
        return rows;
    }

    /**
     * 生成消耗单号
     * @return 消耗单号
     */
    public String getNumber() {
        String str = "KSXH"; // 消耗单前缀
        String date = FillRuleUtil.getDateNum();
        String maxNum = deptBatchConsumeMapper.selectMaxBillNo(date);
        String result = FillRuleUtil.getNumber(str, maxNum, date);
        return result;
    }

    /**
     * 修改科室批量消耗
     *
     * @param deptBatchConsume 科室批量消耗
     * @return 结果
     */
    @Transactional
    @Override
    public int updateDeptBatchConsume(DeptBatchConsume deptBatchConsume)
    {
        deptBatchConsume.setUpdateTime(DateUtils.getNowDate());
        String deleteBy = SecurityUtils.getUserIdStr();
        String tenantId = StringUtils.isNotEmpty(deptBatchConsume.getTenantId()) ? deptBatchConsume.getTenantId() : SecurityUtils.getCustomerId();
        deptBatchConsumeMapper.deleteDeptBatchConsumeEntryRefByConsumeIds(new Long[]{deptBatchConsume.getId()}, tenantId);
        deptBatchConsumeMapper.deleteDeptBatchConsumeEntryByParenId(deptBatchConsume.getId(), deleteBy);
        int filteredCount = insertDeptBatchConsumeEntry(deptBatchConsume);
        deptBatchConsume.setDedupFilteredCount(filteredCount);
        return deptBatchConsumeMapper.updateDeptBatchConsume(deptBatchConsume);
    }

    /**
     * 批量删除科室批量消耗
     *
     * @param ids 需要删除的科室批量消耗主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteDeptBatchConsumeByIds(Long[] ids)
    {
        if (ids == null || ids.length == 0) {
            return 0;
        }
        String deleteBy = SecurityUtils.getUserIdStr();
        deptBatchConsumeMapper.deleteDeptBatchConsumeEntryRefByConsumeIds(ids, SecurityUtils.getCustomerId());
        deptBatchConsumeMapper.deleteDeptBatchConsumeEntryByParenIds(ids, deleteBy);
        return deptBatchConsumeMapper.deleteDeptBatchConsumeByIds(ids, deleteBy);
    }

    /**
     * 删除科室批量消耗信息
     *
     * @param id 科室批量消耗主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteDeptBatchConsumeById(Long id)
    {
        String deleteBy = SecurityUtils.getUserIdStr();
        deptBatchConsumeMapper.deleteDeptBatchConsumeEntryRefByConsumeIds(new Long[]{id}, SecurityUtils.getCustomerId());
        deptBatchConsumeMapper.deleteDeptBatchConsumeEntryByParenId(id, deleteBy);
        return deptBatchConsumeMapper.deleteDeptBatchConsumeById(id, deleteBy);
    }

    /**
     * 审核科室批量消耗
     * @param id 消耗单ID
     * @param auditBy 审核人
     * @return 结果
     */
    @Override
    @Transactional
    public int auditConsume(String id, String auditBy) {
        Long consumeId;
        try {
            consumeId = Long.parseLong(id);
        } catch (Exception e) {
            throw new ServiceException(String.format("科室批量消耗ID：%s 非法", id));
        }
        DeptBatchConsume deptBatchConsume = deptBatchConsumeMapper.selectDeptBatchConsumeById(consumeId);
        if(deptBatchConsume == null){
            throw new ServiceException(String.format("科室批量消耗ID：%s，不存在!", id));
        }
        if (deptBatchConsume.getConsumeBillStatus() != null && deptBatchConsume.getConsumeBillStatus() == 2) {
            throw new ServiceException(String.format("科室批量消耗单 %s 已审核，请勿重复审核", deptBatchConsume.getConsumeBillNo()));
        }
        List<DeptBatchConsumeEntry> entryList = deptBatchConsume.getDeptBatchConsumeEntryList();
        if (entryList == null || entryList.isEmpty()) {
            throw new ServiceException(String.format("科室批量消耗单 %s 无明细，无法审核", deptBatchConsume.getConsumeBillNo()));
        }
        applyInventoryAndFlow(deptBatchConsume, entryList, auditBy);
        deptBatchConsume.setConsumeBillStatus(2);//已审核状态
        deptBatchConsume.setAuditBy(auditBy);
        deptBatchConsume.setAuditDate(new Date());
        return deptBatchConsumeMapper.updateDeptBatchConsume(deptBatchConsume);
    }

    /**
     * 新增科室批量消耗明细信息
     *
     * @param deptBatchConsume 科室批量消耗对象
     */
    public int insertDeptBatchConsumeEntry(DeptBatchConsume deptBatchConsume)
    {
        List<DeptBatchConsumeEntry> deptBatchConsumeEntryList = deptBatchConsume.getDeptBatchConsumeEntryList();
        Long id = deptBatchConsume.getId();
        int filteredCount = 0;
        if (StringUtils.isNotNull(deptBatchConsumeEntryList))
        {
            Set<String> dedupKeys = new HashSet<>();
            for (DeptBatchConsumeEntry deptBatchConsumeEntry : deptBatchConsumeEntryList)
            {
                if (deptBatchConsumeEntry == null) {
                    continue;
                }
                String dedupKey = buildConsumeEntryDedupKey(deptBatchConsumeEntry);
                if (!Boolean.TRUE.equals(deptBatchConsume.getDisableEntryDedup())
                    && StringUtils.isNotBlank(dedupKey) && dedupKeys.contains(dedupKey)) {
                    filteredCount++;
                    continue;
                }
                deptBatchConsumeEntry.setParenId(id);
                deptBatchConsumeEntry.setDelFlag(0);
                deptBatchConsumeEntry.setCreateBy(deptBatchConsume.getCreateBy());
                deptBatchConsumeEntry.setDepartmentId(deptBatchConsume.getDepartmentId());
                deptBatchConsumeEntry.setTenantId(deptBatchConsume.getTenantId());
                if (StringUtils.isBlank(deptBatchConsumeEntry.getMaterialName())
                        || StringUtils.isBlank(deptBatchConsumeEntry.getMaterialSpeci())
                        || StringUtils.isBlank(deptBatchConsumeEntry.getMaterialModel())
                        || deptBatchConsumeEntry.getMaterialFactoryId() == null)
                {
                    FdMaterial material = this.fdMaterialMapper.selectFdMaterialById(deptBatchConsumeEntry.getMaterialId());
                    if (material != null)
                    {
                        if (StringUtils.isBlank(deptBatchConsumeEntry.getMaterialName())) {
                            deptBatchConsumeEntry.setMaterialName(material.getName());
                        }
                        if (StringUtils.isBlank(deptBatchConsumeEntry.getMaterialSpeci())) {
                            deptBatchConsumeEntry.setMaterialSpeci(material.getSpeci());
                        }
                        if (StringUtils.isBlank(deptBatchConsumeEntry.getMaterialModel())) {
                            deptBatchConsumeEntry.setMaterialModel(material.getModel());
                        }
                        if (deptBatchConsumeEntry.getMaterialFactoryId() == null && material.getFactoryId() != null) {
                            deptBatchConsumeEntry.setMaterialFactoryId(material.getFactoryId().longValue());
                        }
                    }
                }
                deptBatchConsumeMapper.insertDeptBatchConsumeEntry(deptBatchConsumeEntry);
                if (StringUtils.isNotBlank(dedupKey)) {
                    dedupKeys.add(dedupKey);
                }
                if (StringUtils.isNotBlank(deptBatchConsumeEntry.getRefOutEntryId()) || StringUtils.isNotBlank(deptBatchConsumeEntry.getRefOutBillId()))
                {
                    if (deptBatchConsumeEntry.getRefOutAvailableQty() == null) {
                        deptBatchConsumeEntry.setRefOutAvailableQty(deptBatchConsumeEntry.getQty());
                    }
                    if (deptBatchConsumeEntry.getRefDefaultConsumeQty() == null) {
                        deptBatchConsumeEntry.setRefDefaultConsumeQty(deptBatchConsumeEntry.getQty());
                    }
                    if (StringUtils.isBlank(deptBatchConsumeEntry.getRemark())) {
                        deptBatchConsumeEntry.setRemark(null);
                    }
                    deptBatchConsumeEntry.setRefId(UUID7.generateUUID7());
                    deptBatchConsumeMapper.insertDeptBatchConsumeEntryRef(deptBatchConsumeEntry);
                }
            }
        }
        return filteredCount;
    }

    private String buildConsumeEntryDedupKey(DeptBatchConsumeEntry e) {
        if (e == null) {
            return null;
        }
        if (StringUtils.isNotBlank(e.getRefOutEntryId())) {
            return "REF_OUT_ENTRY#" + e.getRefOutEntryId().trim();
        }
        if (e.getGzDepInventoryId() != null) {
            return "GZ_DEP_INV#" + e.getGzDepInventoryId();
        }
        if (e.getDepInventoryId() != null) {
            return "DEP_INV#" + e.getDepInventoryId();
        }
        if (e.getMaterialId() != null && StringUtils.isNotBlank(e.getBatchNo())) {
            return "MAT_BATCH#" + e.getMaterialId() + "#" + e.getBatchNo().trim();
        }
        if (e.getMaterialId() != null && StringUtils.isNotBlank(e.getBatchNumer())) {
            return "MAT_BATCHNUM#" + e.getMaterialId() + "#" + e.getBatchNumer().trim();
        }
        return null;
    }

    /**
     * 查询已审核的科室批量消耗明细列表（用于消耗追溯报表）
     * 
     * @param deptBatchConsume 查询条件
     * @return 明细列表
     */
    @Override
    public List<Map<String, Object>> selectAuditedConsumeDetailList(DeptBatchConsume deptBatchConsume)
    {
        return deptBatchConsumeMapper.selectAuditedConsumeDetailList(deptBatchConsume);
    }

    @Override
    public TotalInfo selectAuditedConsumeReportTotal(DeptBatchConsume deptBatchConsume)
    {
        return deptBatchConsumeMapper.selectAuditedConsumeReportTotal(deptBatchConsume);
    }

    /**
     * 查询已审核的科室批量消耗汇总列表（按耗材汇总，用于消耗追溯报表）
     * 
     * @param deptBatchConsume 查询条件
     * @return 汇总列表
     */
    @Override
    public List<Map<String, Object>> selectAuditedConsumeSummaryList(DeptBatchConsume deptBatchConsume)
    {
        return deptBatchConsumeMapper.selectAuditedConsumeSummaryList(deptBatchConsume);
    }

    @Override
    public List<Map<String, Object>> selectOutRefEntryList(DeptBatchConsume deptBatchConsume)
    {
        if (deptBatchConsume != null && StringUtils.isEmpty(deptBatchConsume.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            deptBatchConsume.setTenantId(SecurityUtils.getCustomerId());
        }
        return deptBatchConsumeMapper.selectOutRefEntryList(deptBatchConsume);
    }

    @Override
    public List<Map<String, Object>> selectReverseableEntryList(Long consumeId)
    {
        if (consumeId == null) {
            return new ArrayList<>();
        }
        return deptBatchConsumeMapper.selectReverseableEntryList(consumeId);
    }

    @Override
    @Transactional
    public DeptBatchConsume reverseConsume(DeptBatchConsumeReverseReq req, String operator)
    {
        if (req == null || req.getConsumeId() == null) {
            throw new ServiceException("反消耗失败：来源消耗单不能为空");
        }
        DeptBatchConsume srcBill = deptBatchConsumeMapper.selectDeptBatchConsumeById(req.getConsumeId());
        if (srcBill == null) {
            throw new ServiceException("反消耗失败：来源消耗单不存在");
        }
        if (srcBill.getDisallowReverse() != null && srcBill.getDisallowReverse().intValue() == 1) {
            throw new ServiceException("反消耗失败：该单来源于HIS计费镜像消耗，禁止手工退消耗");
        }
        if (srcBill.getConsumeBillStatus() == null || srcBill.getConsumeBillStatus() != 2) {
            throw new ServiceException("反消耗失败：仅支持对已审核消耗单执行反消耗");
        }
        List<Map<String, Object>> reverseableRows = deptBatchConsumeMapper.selectReverseableEntryList(req.getConsumeId());
        if (reverseableRows == null || reverseableRows.isEmpty()) {
            throw new ServiceException("反消耗失败：来源单没有可反消耗明细");
        }
        Map<Long, Map<String, Object>> reverseableByEntryId = new HashMap<>();
        for (Map<String, Object> row : reverseableRows) {
            Long srcEntryId = toLong(row.get("srcConsumeEntryId"));
            if (srcEntryId != null) {
                reverseableByEntryId.put(srcEntryId, row);
            }
        }
        if (reverseableByEntryId.isEmpty()) {
            throw new ServiceException("反消耗失败：来源单可反消耗明细为空");
        }

        List<DeptBatchConsumeReverseReq.ReverseItem> items = req.getItems();
        Map<Long, BigDecimal> requestQtyMap = new HashMap<>();
        if (items != null && !items.isEmpty()) {
            for (DeptBatchConsumeReverseReq.ReverseItem item : items) {
                if (item == null || item.getSrcConsumeEntryId() == null || item.getReverseQty() == null) {
                    continue;
                }
                if (item.getReverseQty().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new ServiceException("反消耗失败：反消耗数量必须大于0");
                }
                requestQtyMap.merge(item.getSrcConsumeEntryId(), item.getReverseQty(), BigDecimal::add);
            }
        } else {
            for (Map.Entry<Long, Map<String, Object>> en : reverseableByEntryId.entrySet()) {
                BigDecimal canReverseQty = toBigDecimal(en.getValue().get("canReverseQty"));
                if (canReverseQty != null && canReverseQty.compareTo(BigDecimal.ZERO) > 0) {
                    requestQtyMap.put(en.getKey(), canReverseQty);
                }
            }
        }
        if (requestQtyMap.isEmpty()) {
            throw new ServiceException("反消耗失败：未提供有效反消耗数量");
        }

        Map<Long, DeptBatchConsumeEntry> srcEntryMap = new HashMap<>();
        List<DeptBatchConsumeEntry> srcEntries = srcBill.getDeptBatchConsumeEntryList();
        if (srcEntries != null) {
            for (DeptBatchConsumeEntry e : srcEntries) {
                if (e != null && e.getId() != null) {
                    srcEntryMap.put(e.getId(), e);
                }
            }
        }

        List<DeptBatchConsumeEntry> reverseEntries = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> en : requestQtyMap.entrySet()) {
            Long srcEntryId = en.getKey();
            BigDecimal requestReverseQty = en.getValue();
            Map<String, Object> reverseable = reverseableByEntryId.get(srcEntryId);
            if (reverseable == null) {
                throw new ServiceException(String.format("反消耗失败：来源明细[%s]不存在或不可反消耗", srcEntryId));
            }
            BigDecimal canReverseQty = toBigDecimal(reverseable.get("canReverseQty"));
            if (canReverseQty == null || canReverseQty.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ServiceException(String.format("反消耗失败：来源明细[%s]可反消耗数量为0", srcEntryId));
            }
            if (requestReverseQty.compareTo(canReverseQty) > 0) {
                throw new ServiceException(String.format("反消耗失败：来源明细[%s]反消耗数量超限，最多可反消耗%s", srcEntryId, canReverseQty));
            }
            DeptBatchConsumeEntry srcEntry = srcEntryMap.get(srcEntryId);
            if (srcEntry == null) {
                throw new ServiceException(String.format("反消耗失败：来源明细[%s]不存在", srcEntryId));
            }
            DeptBatchConsumeEntry reverseEntry = cloneReverseEntry(srcBill, srcEntry, requestReverseQty, canReverseQty);
            reverseEntries.add(reverseEntry);
        }

        if (reverseEntries.isEmpty()) {
            throw new ServiceException("反消耗失败：未生成有效反消耗明细");
        }

        DeptBatchConsume reverseBill = new DeptBatchConsume();
        reverseBill.setConsumeBillDate(DateUtils.getNowDate());
        reverseBill.setWarehouseId(srcBill.getWarehouseId());
        reverseBill.setDepartmentId(srcBill.getDepartmentId());
        reverseBill.setUserId(srcBill.getUserId());
        reverseBill.setConsumeBillStatus(2);
        reverseBill.setDelFlag(0);
        reverseBill.setCreateBy(SecurityUtils.getUserIdStr());
        reverseBill.setCreateTime(DateUtils.getNowDate());
        reverseBill.setAuditBy(operator);
        reverseBill.setAuditDate(DateUtils.getNowDate());
        reverseBill.setConsumeBillNo(getNumber());
        reverseBill.setRemark(StringUtils.isNotBlank(req.getRemark()) ? req.getRemark() : String.format("反消耗来源：%s", srcBill.getConsumeBillNo()));
        reverseBill.setReverseFlag(1);
        reverseBill.setReverseOfConsumeId(srcBill.getId());
        reverseBill.setReverseOfBillNo(srcBill.getConsumeBillNo());
        reverseBill.setTenantId(StringUtils.isNotEmpty(srcBill.getTenantId()) ? srcBill.getTenantId() : SecurityUtils.getCustomerId());
        reverseBill.setDeptBatchConsumeEntryList(reverseEntries);

        deptBatchConsumeMapper.insertDeptBatchConsume(reverseBill);
        insertDeptBatchConsumeEntry(reverseBill);
        applyInventoryAndFlow(reverseBill, reverseEntries, operator);
        return reverseBill;
    }

    private DeptBatchConsumeEntry cloneReverseEntry(DeptBatchConsume srcBill, DeptBatchConsumeEntry srcEntry, BigDecimal reverseQty, BigDecimal canReverseQty) {
        DeptBatchConsumeEntry e = new DeptBatchConsumeEntry();
        e.setMaterialId(srcEntry.getMaterialId());
        e.setUnitPrice(srcEntry.getUnitPrice());
        e.setPrice(srcEntry.getPrice());
        e.setQty(reverseQty.negate());
        BigDecimal unitPrice = srcEntry.getUnitPrice() == null ? BigDecimal.ZERO : srcEntry.getUnitPrice();
        e.setAmt(unitPrice.multiply(e.getQty()));
        e.setBatchNo(srcEntry.getBatchNo());
        e.setBatchNumer(srcEntry.getBatchNumer());
        e.setDepInventoryId(srcEntry.getDepInventoryId());
        e.setKcNo(srcEntry.getKcNo());
        e.setBatchId(srcEntry.getBatchId());
        e.setWarehouseId(srcEntry.getWarehouseId());
        e.setDepartmentId(srcEntry.getDepartmentId());
        e.setSupplierId(srcEntry.getSupplierId());
        e.setFactoryId(srcEntry.getFactoryId());
        e.setMaterialNo(srcEntry.getMaterialNo());
        e.setBeginTime(srcEntry.getBeginTime());
        e.setEndTime(srcEntry.getEndTime());
        e.setMaterialDate(srcEntry.getMaterialDate());
        e.setWarehouseDate(srcEntry.getWarehouseDate());
        e.setSettlementType(srcEntry.getSettlementType());
        e.setMaterialName(srcEntry.getMaterialName());
        e.setMaterialSpeci(srcEntry.getMaterialSpeci());
        e.setMaterialModel(srcEntry.getMaterialModel());
        e.setMaterialFactoryId(srcEntry.getMaterialFactoryId());
        e.setMainBarcode(srcEntry.getMainBarcode());
        e.setSubBarcode(srcEntry.getSubBarcode());
        e.setRefOutBillId(srcEntry.getRefOutBillId());
        e.setRefOutBillNo(srcEntry.getRefOutBillNo());
        e.setRefOutEntryId(srcEntry.getRefOutEntryId());
        e.setRefOutEntryQty(srcEntry.getRefOutEntryQty());
        e.setRefOutAvailableQty(srcEntry.getRefOutAvailableQty());
        e.setRefDefaultConsumeQty(srcEntry.getRefDefaultConsumeQty());
        e.setSrcConsumeId(srcBill.getId());
        e.setSrcConsumeBillNo(srcBill.getConsumeBillNo());
        e.setSrcConsumeEntryId(srcEntry.getId());
        e.setSrcConsumeQty(srcEntry.getQty());
        e.setSrcCanReverseQty(canReverseQty);
        e.setRemark(String.format("反消耗来源明细:%s", srcEntry.getId()));
        return e;
    }

    private void applyInventoryAndFlow(DeptBatchConsume bill, List<DeptBatchConsumeEntry> entries, String operator) {
        String user = StringUtils.defaultIfBlank(operator, SecurityUtils.getUserIdStr());
        Date now = DateUtils.getNowDate();
        for (DeptBatchConsumeEntry entry : entries) {
            if (entry == null || entry.getQty() == null || entry.getQty().compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            if (entry.getGzDepInventoryId() != null) {
                GzDepInventory gz = gzDepInventoryMapper.selectGzDepInventoryById(entry.getGzDepInventoryId());
                if (gz == null) {
                    throw new ServiceException(String.format("消耗审核失败：高值科室库存[%s]不存在", entry.getGzDepInventoryId()));
                }
                if (bill.getDepartmentId() != null && gz.getDepartmentId() != null
                    && !bill.getDepartmentId().equals(gz.getDepartmentId())) {
                    throw new ServiceException(String.format("消耗审核失败：高值科室库存[%s]不属于当前科室", gz.getId()));
                }
                BigDecimal gzQty = gz.getQty() == null ? BigDecimal.ZERO : gz.getQty();
                BigDecimal gzTarget = gzQty.subtract(entry.getQty());
                if (gzTarget.compareTo(BigDecimal.ZERO) < 0) {
                    throw new ServiceException(String.format("消耗审核失败：高值科室库存[%s]不足，当前%s，本次消耗%s", gz.getId(), gzQty, entry.getQty()));
                }
                gz.setQty(gzTarget);
                BigDecimal gzPrice = gz.getUnitPrice() != null ? gz.getUnitPrice() : (entry.getUnitPrice() == null ? BigDecimal.ZERO : entry.getUnitPrice());
                gz.setUnitPrice(gzPrice);
                gz.setAmt(gzPrice.multiply(gzTarget));
                gz.setUpdateBy(user);
                gz.setUpdateTime(now);
                gzDepInventoryMapper.updateGzDepInventory(gz);

                HcKsFlow flow = new HcKsFlow();
                flow.setBillId(bill.getId());
                flow.setEntryId(entry.getId());
                flow.setDepartmentId(bill.getDepartmentId());
                flow.setWarehouseId(entry.getWarehouseId());
                flow.setMaterialId(entry.getMaterialId());
                flow.setBatchNo(entry.getBatchNo());
                flow.setBatchNumber(entry.getBatchNumer());
                flow.setBatchId(entry.getBatchId());
                flow.setQty(entry.getQty());
                flow.setUnitPrice(entry.getUnitPrice());
                flow.setAmt(entry.getAmt());
                flow.setBeginTime(entry.getBeginTime());
                flow.setEndTime(entry.getEndTime());
                flow.setSupplierId(entry.getSupplierId());
                flow.setFactoryId(entry.getFactoryId());
                flow.setKcNo(entry.getGzDepInventoryId());
                flow.setLx(entry.getQty().compareTo(BigDecimal.ZERO) > 0 ? "XH" : "TXH");
                flow.setFlowTime(now);
                flow.setOriginBusinessType(entry.getQty().compareTo(BigDecimal.ZERO) > 0 ? "科室批量消耗(高值)" : "科室退消耗(高值)");
                flow.setDelFlag(0);
                flow.setMainBarcode(entry.getMainBarcode());
                flow.setSubBarcode(entry.getSubBarcode());
                flow.setCreateBy(user);
                flow.setCreateTime(now);
                flow.setTenantId(StringUtils.isNotEmpty(bill.getTenantId()) ? bill.getTenantId() : SecurityUtils.getCustomerId());
                hcKsFlowMapper.insertHcKsFlow(flow);
            }
            else if (entry.getDepInventoryId() != null) {
                StkDepInventory depInv = stkDepInventoryMapper.selectStkDepInventoryById(entry.getDepInventoryId());
                if (depInv == null) {
                    throw new ServiceException(String.format("消耗审核失败：科室库存[%s]不存在", entry.getDepInventoryId()));
                }
                if (bill.getDepartmentId() != null && depInv.getDepartmentId() != null
                    && !bill.getDepartmentId().equals(depInv.getDepartmentId())) {
                    throw new ServiceException(String.format("消耗审核失败：明细库存[%s]不属于当前科室", depInv.getId()));
                }
                BigDecimal currentQty = depInv.getQty() == null ? BigDecimal.ZERO : depInv.getQty();
                BigDecimal targetQty = currentQty.subtract(entry.getQty());
                if (targetQty.compareTo(BigDecimal.ZERO) < 0) {
                    throw new ServiceException(String.format("消耗审核失败：明细[%s]库存不足，当前库存%s，消耗数量%s", entry.getId(), currentQty, entry.getQty()));
                }
                depInv.setQty(targetQty);
                BigDecimal invPrice = depInv.getUnitPrice() != null ? depInv.getUnitPrice() : (entry.getUnitPrice() == null ? BigDecimal.ZERO : entry.getUnitPrice());
                depInv.setUnitPrice(invPrice);
                depInv.setAmt(invPrice.multiply(targetQty));
                depInv.setUpdateBy(user);
                depInv.setUpdateTime(now);
                stkDepInventoryMapper.updateStkDepInventory(depInv);

                HcKsFlow flow = new HcKsFlow();
                flow.setBillId(bill.getId());
                flow.setEntryId(entry.getId());
                flow.setDepartmentId(bill.getDepartmentId());
                flow.setWarehouseId(depInv.getWarehouseId());
                flow.setMaterialId(entry.getMaterialId());
                flow.setBatchNo(entry.getBatchNo());
                flow.setBatchNumber(entry.getBatchNumer());
                flow.setBatchId(entry.getBatchId());
                flow.setQty(entry.getQty());
                flow.setUnitPrice(entry.getUnitPrice());
                flow.setAmt(entry.getAmt());
                flow.setBeginTime(entry.getBeginTime());
                flow.setEndTime(entry.getEndTime());
                flow.setSupplierId(entry.getSupplierId());
                flow.setFactoryId(entry.getFactoryId());
                flow.setKcNo(depInv.getId());
                flow.setLx(entry.getQty().compareTo(BigDecimal.ZERO) > 0 ? "XH" : "TXH");
                flow.setFlowTime(now);
                flow.setOriginBusinessType(entry.getQty().compareTo(BigDecimal.ZERO) > 0 ? "科室批量消耗" : "科室退消耗");
                flow.setDelFlag(0);
                flow.setMainBarcode(entry.getMainBarcode());
                flow.setSubBarcode(entry.getSubBarcode());
                flow.setCreateBy(user);
                flow.setCreateTime(now);
                flow.setTenantId(StringUtils.isNotEmpty(bill.getTenantId()) ? bill.getTenantId() : SecurityUtils.getCustomerId());
                hcKsFlowMapper.insertHcKsFlow(flow);
            }
            else {
                throw new ServiceException(String.format("消耗审核失败：明细耗材[%s]缺少来源库存（科室低值库存或高值科室库存）", entry.getMaterialId()));
            }
        }
    }

    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).longValue();
        try {
            return Long.parseLong(String.valueOf(v));
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal toBigDecimal(Object v) {
        if (v == null) return null;
        if (v instanceof BigDecimal) return (BigDecimal) v;
        try {
            return new BigDecimal(String.valueOf(v));
        } catch (Exception e) {
            return null;
        }
    }
}

package com.spd.department.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spd.common.core.page.TotalInfo;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.common.utils.uuid.UUID7;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
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
        deptBatchConsume.setConsumeBillStatus(2);//已审核状态
        deptBatchConsume.setAuditBy(auditBy);
        deptBatchConsume.setAuditDate(new Date());
        int res = deptBatchConsumeMapper.updateDeptBatchConsume(deptBatchConsume);
        // TODO: 审核后扣减库存逻辑（参考科室申领）
        return res;
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
                if (StringUtils.isNotBlank(dedupKey) && dedupKeys.contains(dedupKey)) {
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
}

package com.spd.department.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.spd.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.spd.department.domain.DepPurchaseApplyEntry;
import com.spd.department.mapper.DepPurchaseApplyMapper;
import com.spd.department.domain.DepPurchaseApply;
import com.spd.department.service.IDepPurchaseApplyService;

/**
 * 科室申购Service业务层处理
 * 
 * @author spd
 * @date 2025-01-01
 */
@Service
public class DepPurchaseApplyServiceImpl implements IDepPurchaseApplyService 
{
    @Autowired
    private DepPurchaseApplyMapper depPurchaseApplyMapper;

    /**
     * 查询科室申购
     * 
     * @param id 科室申购主键
     * @return 科室申购
     */
    @Override
    public DepPurchaseApply selectDepPurchaseApplyById(Long id)
    {
        DepPurchaseApply a = depPurchaseApplyMapper.selectDepPurchaseApplyById(id);
        if (a != null) {
            SecurityUtils.ensureTenantAccess(a.getTenantId());
        }
        return a;
    }

    /**
     * 查询科室申购列表
     * 
     * @param depPurchaseApply 科室申购
     * @return 科室申购
     */
    @Override
    public List<DepPurchaseApply> selectDepPurchaseApplyList(DepPurchaseApply depPurchaseApply)
    {
        if (depPurchaseApply != null && StringUtils.isEmpty(depPurchaseApply.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            depPurchaseApply.setTenantId(SecurityUtils.getCustomerId());
        }
        return depPurchaseApplyMapper.selectDepPurchaseApplyList(depPurchaseApply);
    }

    /**
     * 新增科室申购
     * 
     * @param depPurchaseApply 科室申购
     * @return 结果
     */
    /** 校验明细数量：有耗材的明细数量不能为空且必须大于0 */
    private void validateEntryQty(List<DepPurchaseApplyEntry> list) {
        if (list == null) return;
        for (DepPurchaseApplyEntry e : list) {
            if (e.getMaterialId() != null && (e.getQty() == null || e.getQty().compareTo(BigDecimal.ZERO) <= 0)) {
                throw new ServiceException("科室申购单明细中数量不能为空且必须大于0，请检查后保存。");
            }
        }
    }

    @Transactional
    @Override
    public int insertDepPurchaseApply(DepPurchaseApply depPurchaseApply)
    {
        validateEntryQty(depPurchaseApply.getDepPurchaseApplyEntryList());
        // 生成申购单号
        if (StringUtils.isEmpty(depPurchaseApply.getPurchaseBillNo())) {
            depPurchaseApply.setPurchaseBillNo(generatePurchaseBillNo());
        }
        
        depPurchaseApply.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(depPurchaseApply.getCreateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            depPurchaseApply.setCreateBy(SecurityUtils.getUserIdStr());
        }
        if (StringUtils.isEmpty(depPurchaseApply.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            depPurchaseApply.setTenantId(SecurityUtils.getCustomerId());
        }
        int rows = depPurchaseApplyMapper.insertDepPurchaseApply(depPurchaseApply);
        insertDepPurchaseApplyEntry(depPurchaseApply);
        return rows;
    }

    /**
     * 修改科室申购
     * 
     * @param depPurchaseApply 科室申购
     * @return 结果
     */
    @Transactional
    @Override
    public int updateDepPurchaseApply(DepPurchaseApply depPurchaseApply)
    {
        validateEntryQty(depPurchaseApply.getDepPurchaseApplyEntryList());
        depPurchaseApply.setUpdateTime(DateUtils.getNowDate());
        depPurchaseApply.setUpdateBy(SecurityUtils.getUserIdStr());
        
        depPurchaseApplyMapper.deleteDepPurchaseApplyEntryByParentId(depPurchaseApply.getId(), com.spd.common.utils.SecurityUtils.getUserIdStr());
        insertDepPurchaseApplyEntry(depPurchaseApply);
        return depPurchaseApplyMapper.updateDepPurchaseApply(depPurchaseApply);
    }

    /**
     * 批量删除科室申购
     * 
     * @param ids 需要删除的科室申购主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteDepPurchaseApplyByIds(Long[] ids)
    {
        for (Long id : ids) {
            DepPurchaseApply existing = depPurchaseApplyMapper.selectDepPurchaseApplyById(id);
            if (existing != null) {
                SecurityUtils.ensureTenantAccess(existing.getTenantId());
            }
        }
        String deleteBy = com.spd.common.utils.SecurityUtils.getUserIdStr();
        depPurchaseApplyMapper.deleteDepPurchaseApplyEntryByParentIds(ids, deleteBy);
        return depPurchaseApplyMapper.deleteDepPurchaseApplyByIds(ids, deleteBy);
    }

    /**
     * 删除科室申购信息
     * 
     * @param id 科室申购主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteDepPurchaseApplyById(Long id)
    {
        DepPurchaseApply existing = depPurchaseApplyMapper.selectDepPurchaseApplyById(id);
        if (existing != null) {
            SecurityUtils.ensureTenantAccess(existing.getTenantId());
        }
        String deleteBy = com.spd.common.utils.SecurityUtils.getUserIdStr();
        depPurchaseApplyMapper.deleteDepPurchaseApplyEntryByParentId(id, deleteBy);
        return depPurchaseApplyMapper.deleteDepPurchaseApplyById(id, deleteBy);
    }

    /**
     * 新增科室申购明细信息
     * 
     * @param depPurchaseApply 科室申购对象
     */
    public void insertDepPurchaseApplyEntry(DepPurchaseApply depPurchaseApply)
    {
        List<DepPurchaseApplyEntry> depPurchaseApplyEntryList = depPurchaseApply.getDepPurchaseApplyEntryList();
        Long id = depPurchaseApply.getId();
        if (id == null) {
            return;
        }
        if (StringUtils.isNotNull(depPurchaseApplyEntryList) && !depPurchaseApplyEntryList.isEmpty())
        {
            List<DepPurchaseApplyEntry> list = new ArrayList<DepPurchaseApplyEntry>();
            for (DepPurchaseApplyEntry depPurchaseApplyEntry : depPurchaseApplyEntryList)
            {
                if (depPurchaseApplyEntry.getMaterialId() == null) {
                    continue;
                }
                depPurchaseApplyEntry.setParentId(id);
                if (StringUtils.isEmpty(depPurchaseApplyEntry.getTenantId()) && StringUtils.isNotEmpty(depPurchaseApply.getTenantId())) {
                    depPurchaseApplyEntry.setTenantId(depPurchaseApply.getTenantId());
                }
                depPurchaseApplyEntry.setCreateTime(DateUtils.getNowDate());
                depPurchaseApplyEntry.setCreateBy(SecurityUtils.getUserIdStr());
                list.add(depPurchaseApplyEntry);
            }
            if (!list.isEmpty())
            {
                depPurchaseApplyMapper.batchDepPurchaseApplyEntry(list);
            }
        }
    }

    /**
     * 生成申购单号
     * 
     * @return 申购单号
     */
    private String generatePurchaseBillNo() {
        String dateStr = DateUtils.dateTimeNow("yyyyMMdd");
        String prefix = "SG" + dateStr;
        
        // 简单的序号生成，实际项目中可能需要更复杂的逻辑
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
        return prefix + timestamp;
    }

    /**
     * 审核科室申购
     * 
     * @param id 科室申购主键
     * @param auditBy 审核人
     * @return 结果
     */
    @Override
    public int auditPurchaseApply(String id, String auditBy) {
        DepPurchaseApply depPurchaseApply = depPurchaseApplyMapper.selectDepPurchaseApplyById(Long.parseLong(id));
        if (depPurchaseApply == null) {
            throw new ServiceException(String.format("科室申购ID：%s，不存在!", id));
        }
        if (depPurchaseApply.getPurchaseBillStatus() == null || depPurchaseApply.getPurchaseBillStatus() != 1) {
            throw new ServiceException("只有待审核状态(1)的科室申购可审核，当前状态：" + depPurchaseApply.getPurchaseBillStatus());
        }
        validateEntryQty(depPurchaseApply.getDepPurchaseApplyEntryList());
        depPurchaseApply.setPurchaseBillStatus(2);//已审核状态
        depPurchaseApply.setUpdateBy(auditBy);
        depPurchaseApply.setUpdateTime(new Date());
        int res = depPurchaseApplyMapper.updateDepPurchaseApply(depPurchaseApply);
        return res;
    }

    /**
     * 驳回科室申购
     * 
     * @param id 科室申购主键
     * @param rejectReason 驳回原因
     * @return 结果
     */
    @Override
    public int rejectPurchaseApply(String id, String rejectReason) {
        DepPurchaseApply depPurchaseApply = depPurchaseApplyMapper.selectDepPurchaseApplyById(Long.parseLong(id));
        if (depPurchaseApply == null) {
            throw new ServiceException(String.format("科室申购ID：%s，不存在!", id));
        }
        if (depPurchaseApply.getPurchaseBillStatus() == null || depPurchaseApply.getPurchaseBillStatus() != 1) {
            throw new ServiceException("只有待审核状态(1)的科室申购可驳回，当前状态：" + depPurchaseApply.getPurchaseBillStatus());
        }
        depPurchaseApply.setPlanStatus(2); // 驳回状态
        depPurchaseApply.setRejectReason(rejectReason);
        depPurchaseApply.setUpdateBy(SecurityUtils.getUserIdStr());
        depPurchaseApply.setUpdateTime(new Date());
        int res = depPurchaseApplyMapper.updateDepPurchaseApply(depPurchaseApply);
        return res;
    }

    /**
     * 确认收货
     * 
     * @param id 科室申购主键
     * @param confirmBy 确认人
     * @return 结果
     */
    @Override
    public int confirmReceipt(String id, String confirmBy) {
        DepPurchaseApply depPurchaseApply = depPurchaseApplyMapper.selectDepPurchaseApplyById(Long.parseLong(id));
        if(depPurchaseApply == null){
            throw new ServiceException(String.format("科室申购ID：%s，不存在!", id));
        }
        if(depPurchaseApply.getPurchaseBillStatus() != 2){
            throw new ServiceException("只有已审核的申购单才能确认收货!");
        }
        // 使用planStatus字段标识收货状态：1=已确认收货
        depPurchaseApply.setPlanStatus(1);
        depPurchaseApply.setUpdateBy(confirmBy);
        depPurchaseApply.setUpdateTime(new Date());
        int res = depPurchaseApplyMapper.updateDepPurchaseApply(depPurchaseApply);
        return res;
    }

    /**
     * 驳回收货
     * 
     * @param id 科室申购主键
     * @param rejectReason 驳回原因
     * @return 结果
     */
    @Override
    public int rejectReceipt(String id, String rejectReason) {
        DepPurchaseApply depPurchaseApply = depPurchaseApplyMapper.selectDepPurchaseApplyById(Long.parseLong(id));
        if(depPurchaseApply == null){
            throw new ServiceException(String.format("科室申购ID：%s，不存在!", id));
        }
        if(depPurchaseApply.getPurchaseBillStatus() != 2){
            throw new ServiceException("只有已审核的申购单才能驳回收货!");
        }
        // 使用planStatus字段标识收货状态：2=驳回收货
        depPurchaseApply.setPlanStatus(2);
        depPurchaseApply.setRejectReason(rejectReason);
        depPurchaseApply.setUpdateBy(SecurityUtils.getUserIdStr());
        depPurchaseApply.setUpdateTime(new Date());
        int res = depPurchaseApplyMapper.updateDepPurchaseApply(depPurchaseApply);
        return res;
    }
}

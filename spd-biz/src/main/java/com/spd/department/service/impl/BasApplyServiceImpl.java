package com.spd.department.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.common.utils.SecurityUtils;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.spd.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.spd.department.domain.BasApplyEntry;
import com.spd.department.mapper.BasApplyMapper;
import com.spd.department.domain.BasApply;
import com.spd.department.service.IBasApplyService;

/**
 * 科室申领Service业务层处理
 *
 * @author spd
 * @date 2024-02-26
 */
@Service
public class BasApplyServiceImpl implements IBasApplyService
{
    @Autowired
    private BasApplyMapper basApplyMapper;
    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    /**
     * 查询科室申领
     *
     * @param id 科室申领主键
     * @return 科室申领
     */
    @Override
    public BasApply selectBasApplyById(Long id)
    {
        BasApply basApply = basApplyMapper.selectBasApplyById(id);
        if (basApply == null) {
            return null;
        }
        SecurityUtils.ensureTenantAccess(basApply.getTenantId());
        List<BasApplyEntry> basApplyEntryList = basApply.getBasApplyEntryList();
        if (basApplyEntryList != null) {
            for (BasApplyEntry basApplyEntry : basApplyEntryList) {
                if (basApplyEntry.getMaterialId() != null) {
                    FdMaterial material = this.fdMaterialMapper.selectFdMaterialById(basApplyEntry.getMaterialId());
                    basApplyEntry.setMaterial(material);
                }
            }
        }
        return basApply;
    }

    /**
     * 查询科室申领列表
     *
     * @param basApply 科室申领
     * @return 科室申领
     */
    @Override
    public List<BasApply> selectBasApplyList(BasApply basApply)
    {
        if (basApply != null && StringUtils.isEmpty(basApply.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            basApply.setTenantId(SecurityUtils.getCustomerId());
        }
        return basApplyMapper.selectBasApplyList(basApply);
    }

    /** 校验明细数量：有耗材的明细数量不能为空且必须大于0 */
    private void validateEntryQty(List<BasApplyEntry> list) {
        if (list == null) return;
        for (BasApplyEntry e : list) {
            if (e.getMaterialId() != null && (e.getQty() == null || e.getQty().compareTo(BigDecimal.ZERO) <= 0)) {
                throw new ServiceException("科室申领单明细中数量不能为空且必须大于0，请检查后保存。");
            }
        }
    }

    /**
     * 新增科室申领
     *
     * @param basApply 科室申领
     * @return 结果
     */
    @Transactional
    @Override
    public int insertBasApply(BasApply basApply)
    {
        validateEntryQty(basApply.getBasApplyEntryList());
        if (StringUtils.isEmpty(basApply.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            basApply.setTenantId(SecurityUtils.getCustomerId());
        }
        basApply.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(basApply.getCreateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            basApply.setCreateBy(SecurityUtils.getUserIdStr());
        }
        basApply.setApplyBillNo(getNumber(basApply.getBillType()));
        int rows = basApplyMapper.insertBasApply(basApply);
        insertBasApplyEntry(basApply);
        return rows;
    }

    //str：单号前缀
    //date：日期
    //result：最终结果，需要的流水号
    public String getNumber(Integer billType) {
        String str = "SL"; // 默认申领单前缀
        if (billType != null) {
            if (billType == 2) {
                str = "SG"; // 申购单前缀
            } else if (billType == 3) {
                str = "ZK"; // 转科申请单前缀
            }
        }
        String date = FillRuleUtil.getDateNum();
        String maxNum = basApplyMapper.selectMaxBillNo(date);
        String result = FillRuleUtil.getNumber(str,maxNum,date);
        return result;
    }

    /**
     * 修改科室申领
     *
     * @param basApply 科室申领
     * @return 结果
     */
    @Transactional
    @Override
    public int updateBasApply(BasApply basApply)
    {
        validateEntryQty(basApply.getBasApplyEntryList());
        basApply.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isEmpty(basApply.getUpdateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            basApply.setUpdateBy(SecurityUtils.getUserIdStr());
        }
        String deleteBy = SecurityUtils.getUserIdStr();
        basApplyMapper.deleteBasApplyEntryByParenId(basApply.getId(), deleteBy);
        insertBasApplyEntry(basApply);
        return basApplyMapper.updateBasApply(basApply);
    }

    /**
     * 批量删除科室申领
     *
     * @param ids 需要删除的科室申领主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteBasApplyByIds(Long[] ids)
    {
        for (Long id : ids) {
            BasApply existing = basApplyMapper.selectBasApplyById(id);
            if (existing != null) {
                SecurityUtils.ensureTenantAccess(existing.getTenantId());
            }
        }
        String deleteBy = SecurityUtils.getUserIdStr();
        basApplyMapper.deleteBasApplyEntryByParenIds(ids, deleteBy);
        return basApplyMapper.deleteBasApplyByIds(ids, deleteBy);
    }

    /**
     * 删除科室申领信息
     *
     * @param id 科室申领主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteBasApplyById(Long id)
    {
        BasApply existing = basApplyMapper.selectBasApplyById(id);
        if (existing != null) {
            SecurityUtils.ensureTenantAccess(existing.getTenantId());
        }
        String deleteBy = SecurityUtils.getUserIdStr();
        basApplyMapper.deleteBasApplyEntryByParenId(id, deleteBy);
        return basApplyMapper.deleteBasApplyById(id, deleteBy);
    }

    /**
     * 审核科室申领
     * @param id
     * @return
     */
    @Override
    public int auditApply(String id, String auditBy) {
        BasApply basApply = basApplyMapper.selectBasApplyById(Long.parseLong(id));
        if (basApply == null) {
            throw new ServiceException(String.format("科室申领ID：%s，不存在!", id));
        }
        if (basApply.getApplyBillStatus() == null || basApply.getApplyBillStatus() != 1) {
            throw new ServiceException("只有待审核状态(1)的科室申领可审核，当前状态：" + basApply.getApplyBillStatus());
        }
        validateEntryQty(basApply.getBasApplyEntryList());
        basApply.setApplyBillStatus(2);//已审核状态
        basApply.setAuditBy(auditBy);
        basApply.setAuditDate(new Date());
        int res = basApplyMapper.updateBasApply(basApply);
        return res;
    }

    /**
     * 驳回科室申领
     * 
     * @param id 科室申领主键
     * @param rejectReason 驳回原因
     * @return 结果
     */
    @Override
    public int rejectApply(String id, String rejectReason) {
        BasApply basApply = basApplyMapper.selectBasApplyById(Long.parseLong(id));
        if (basApply == null) {
            throw new ServiceException(String.format("科室申领ID：%s，不存在!", id));
        }
        if (basApply.getApplyBillStatus() == null || basApply.getApplyBillStatus() != 1) {
            throw new ServiceException("只有待审核状态(1)的科室申领可驳回，当前状态：" + basApply.getApplyBillStatus());
        }
        basApply.setRejectReason(rejectReason);
        basApply.setUpdateBy(SecurityUtils.getUserIdStr());
        basApply.setAuditBy(String.valueOf(SecurityUtils.getUserId()));
        basApply.setAuditDate(new Date());
        basApply.setUpdateBy(SecurityUtils.getUsername());
        basApply.setUpdateTime(new Date());
        int res = basApplyMapper.updateBasApply(basApply);
        return res;
    }

    /**
     * 新增科室申领明细信息
     *
     * @param basApply 科室申领对象
     */
    public void insertBasApplyEntry(BasApply basApply)
    {
        List<BasApplyEntry> basApplyEntryList = basApply.getBasApplyEntryList();
        Long id = basApply.getId();
        if (id == null) {
            return;
        }
        if (StringUtils.isNotNull(basApplyEntryList) && !basApplyEntryList.isEmpty())
        {
            List<BasApplyEntry> list = new ArrayList<BasApplyEntry>();
            for (BasApplyEntry basApplyEntry : basApplyEntryList)
            {
                if (basApplyEntry.getMaterialId() == null) {
                    continue;
                }
                basApplyEntry.setParenId(id);
                if (StringUtils.isEmpty(basApplyEntry.getTenantId()) && StringUtils.isNotEmpty(basApply.getTenantId())) {
                    basApplyEntry.setTenantId(basApply.getTenantId());
                }
                list.add(basApplyEntry);
            }
            if (!list.isEmpty())
            {
                basApplyMapper.batchBasApplyEntry(list);
            }
        }
    }
}

package com.spd.gz.service.impl;

import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import org.springframework.transaction.annotation.Transactional;
import com.spd.gz.domain.GzDepApplyEntry;
import com.spd.gz.mapper.GzDepApplyMapper;
import com.spd.gz.domain.GzDepApply;
import com.spd.gz.service.IGzDepApplyService;

/**
 * 高值科室申领Service业务层处理
 *
 * @author spd
 * @date 2024-06-22
 */
@Service
public class GzDepApplyServiceImpl implements IGzDepApplyService
{
    @Autowired
    private GzDepApplyMapper gzDepApplyMapper;

    /**
     * 查询高值科室申领
     *
     * @param id 高值科室申领主键
     * @return 高值科室申领
     */
    @Override
    public GzDepApply selectGzDepApplyById(Long id)
    {
        GzDepApply a = gzDepApplyMapper.selectGzDepApplyById(id);
        if (a != null) {
            SecurityUtils.ensureTenantAccess(a.getTenantId());
        }
        return a;
    }

    /**
     * 查询高值科室申领列表
     *
     * @param gzDepApply 高值科室申领
     * @return 高值科室申领
     */
    @Override
    public List<GzDepApply> selectGzDepApplyList(GzDepApply gzDepApply)
    {
        if (gzDepApply != null && StringUtils.isEmpty(gzDepApply.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            gzDepApply.setTenantId(SecurityUtils.getCustomerId());
        }
        return gzDepApplyMapper.selectGzDepApplyList(gzDepApply);
    }

    /**
     * 新增高值科室申领
     *
     * @param gzDepApply 高值科室申领
     * @return 结果
     */
    @Transactional
    @Override
    public int insertGzDepApply(GzDepApply gzDepApply)
    {
        gzDepApply.setApplyBillNo(getNumber());
        gzDepApply.setCreateTime(DateUtils.getNowDate());
        gzDepApply.setCreateBy(SecurityUtils.getUserIdStr());
        if (StringUtils.isEmpty(gzDepApply.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            gzDepApply.setTenantId(SecurityUtils.getCustomerId());
        }
        int rows = gzDepApplyMapper.insertGzDepApply(gzDepApply);
        insertGzDepApplyEntry(gzDepApply);
        return rows;
    }

    //result：最终结果，需要的流水号
    public String getNumber() {
        String str = "GZSL";
        String date = FillRuleUtil.getDateNum();
        String maxNum = gzDepApplyMapper.selectMaxBillNo(date);
        String result = FillRuleUtil.getNumber(str,maxNum,date);
        return result;
    }

    /**
     * 修改高值科室申领
     *
     * @param gzDepApply 高值科室申领
     * @return 结果
     */
    @Transactional
    @Override
    public int updateGzDepApply(GzDepApply gzDepApply)
    {
        gzDepApply.setUpdateTime(DateUtils.getNowDate());
        gzDepApply.setUpdateBy(SecurityUtils.getUserIdStr());
        gzDepApplyMapper.deleteGzDepApplyEntryByParenId(gzDepApply.getId(), com.spd.common.utils.SecurityUtils.getUserIdStr());
        insertGzDepApplyEntry(gzDepApply);
        return gzDepApplyMapper.updateGzDepApply(gzDepApply);
    }

    /**
     * 批量删除高值科室申领
     *
     * @param ids 需要删除的高值科室申领主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteGzDepApplyByIds(Long[] ids)
    {
        for (Long id : ids) {
            GzDepApply existing = gzDepApplyMapper.selectGzDepApplyById(id);
            if (existing != null) {
                SecurityUtils.ensureTenantAccess(existing.getTenantId());
            }
        }
        String deleteBy = com.spd.common.utils.SecurityUtils.getUserIdStr();
        gzDepApplyMapper.deleteGzDepApplyEntryByParenIds(ids, deleteBy);
        return gzDepApplyMapper.deleteGzDepApplyByIds(ids, deleteBy);
    }

    /**
     * 删除高值科室申领信息
     *
     * @param id 高值科室申领主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteGzDepApplyById(Long id)
    {
        GzDepApply existing = gzDepApplyMapper.selectGzDepApplyById(id);
        if (existing != null) {
            SecurityUtils.ensureTenantAccess(existing.getTenantId());
        }
        String deleteBy = com.spd.common.utils.SecurityUtils.getUserIdStr();
        gzDepApplyMapper.deleteGzDepApplyEntryByParenId(id, deleteBy);
        return gzDepApplyMapper.deleteGzDepApplyById(id, deleteBy);
    }

    @Override
    public int auditApply(String id) {
        GzDepApply gzDepApply = gzDepApplyMapper.selectGzDepApplyById(Long.valueOf(id));
        if(gzDepApply == null){
            throw new ServiceException(String.format("高值科室申领ID：%s，不存在!", id));
        }

        gzDepApply.setApplyBillStatus(2);
        gzDepApply.setUpdateBy(SecurityUtils.getUserIdStr());
        gzDepApply.setUpdateTime(DateUtils.getNowDate());

        int res = gzDepApplyMapper.updateGzDepApply(gzDepApply);
        return res;
    }

    /**
     * 新增高值科室申领明细信息
     *
     * @param gzDepApply 高值科室申领对象
     */
    public void insertGzDepApplyEntry(GzDepApply gzDepApply)
    {
        List<GzDepApplyEntry> gzDepApplyEntryList = gzDepApply.getGzDepApplyEntryList();
        Long id = gzDepApply.getId();
        if (StringUtils.isNotNull(gzDepApplyEntryList))
        {
            List<GzDepApplyEntry> list = new ArrayList<GzDepApplyEntry>();
            String tenantId = StringUtils.isNotEmpty(gzDepApply.getTenantId())
                ? gzDepApply.getTenantId()
                : SecurityUtils.requiredScopedTenantIdForSql();
            for (GzDepApplyEntry gzDepApplyEntry : gzDepApplyEntryList)
            {
                gzDepApplyEntry.setParenId(id);
                gzDepApplyEntry.setTenantId(tenantId);
                gzDepApplyEntry.setDelFlag(0);
                list.add(gzDepApplyEntry);
            }
            if (list.size() > 0)
            {
                gzDepApplyMapper.batchGzDepApplyEntry(list);
            }
        }
    }
}

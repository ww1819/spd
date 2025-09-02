package com.spd.department.service.impl;

import java.util.List;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
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
        return depPurchaseApplyMapper.selectDepPurchaseApplyById(id);
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
        return depPurchaseApplyMapper.selectDepPurchaseApplyList(depPurchaseApply);
    }

    /**
     * 新增科室申购
     * 
     * @param depPurchaseApply 科室申购
     * @return 结果
     */
    @Transactional
    @Override
    public int insertDepPurchaseApply(DepPurchaseApply depPurchaseApply)
    {
        // 生成申购单号
        if (StringUtils.isEmpty(depPurchaseApply.getPurchaseBillNo())) {
            depPurchaseApply.setPurchaseBillNo(generatePurchaseBillNo());
        }
        
        depPurchaseApply.setCreateTime(DateUtils.getNowDate());
        depPurchaseApply.setCreateBy(SecurityUtils.getUsername());
        
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
        depPurchaseApply.setUpdateTime(DateUtils.getNowDate());
        depPurchaseApply.setUpdateBy(SecurityUtils.getUsername());
        
        depPurchaseApplyMapper.deleteDepPurchaseApplyEntryByParentId(depPurchaseApply.getId());
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
        depPurchaseApplyMapper.deleteDepPurchaseApplyEntryByParentIds(ids);
        return depPurchaseApplyMapper.deleteDepPurchaseApplyByIds(ids);
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
        depPurchaseApplyMapper.deleteDepPurchaseApplyEntryByParentId(id);
        return depPurchaseApplyMapper.deleteDepPurchaseApplyById(id);
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
        if (StringUtils.isNotNull(depPurchaseApplyEntryList))
        {
            List<DepPurchaseApplyEntry> list = new ArrayList<DepPurchaseApplyEntry>();
            for (DepPurchaseApplyEntry depPurchaseApplyEntry : depPurchaseApplyEntryList)
            {
                depPurchaseApplyEntry.setParentId(id);
                depPurchaseApplyEntry.setCreateTime(DateUtils.getNowDate());
                depPurchaseApplyEntry.setCreateBy(SecurityUtils.getUsername());
                list.add(depPurchaseApplyEntry);
            }
            if (list.size() > 0)
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
}

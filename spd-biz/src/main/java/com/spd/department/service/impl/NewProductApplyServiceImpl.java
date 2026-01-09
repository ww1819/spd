package com.spd.department.service.impl;

import java.util.List;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.spd.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.spd.department.domain.NewProductApplyEntry;
import com.spd.department.domain.NewProductApplyDetail;
import com.spd.department.mapper.NewProductApplyMapper;
import com.spd.department.domain.NewProductApply;
import com.spd.department.service.INewProductApplyService;

/**
 * 新品申购申请Service业务层处理
 * 
 * @author spd
 * @date 2025-01-01
 */
@Service
public class NewProductApplyServiceImpl implements INewProductApplyService 
{
    @Autowired
    private NewProductApplyMapper newProductApplyMapper;

    /**
     * 查询新品申购申请
     * 
     * @param id 新品申购申请主键
     * @return 新品申购申请
     */
    @Override
    public NewProductApply selectNewProductApplyById(Long id)
    {
        return newProductApplyMapper.selectNewProductApplyById(id);
    }

    /**
     * 查询新品申购申请列表
     * 
     * @param newProductApply 新品申购申请
     * @return 新品申购申请
     */
    @Override
    public List<NewProductApply> selectNewProductApplyList(NewProductApply newProductApply)
    {
        return newProductApplyMapper.selectNewProductApplyList(newProductApply);
    }

    /**
     * 新增新品申购申请
     * 
     * @param newProductApply 新品申购申请
     * @return 结果
     */
    @Transactional
    @Override
    public int insertNewProductApply(NewProductApply newProductApply)
    {
        // 生成申购单号
        if (StringUtils.isEmpty(newProductApply.getApplyNo())) {
            newProductApply.setApplyNo(generateApplyNo());
        }
        
        newProductApply.setCreateTime(DateUtils.getNowDate());
        newProductApply.setCreateBy(SecurityUtils.getUsername());
        
        int rows = newProductApplyMapper.insertNewProductApply(newProductApply);
        insertNewProductApplyEntry(newProductApply);
        insertNewProductApplyDetail(newProductApply);
        return rows;
    }

    /**
     * 修改新品申购申请
     * 
     * @param newProductApply 新品申购申请
     * @return 结果
     */
    @Transactional
    @Override
    public int updateNewProductApply(NewProductApply newProductApply)
    {
        newProductApply.setUpdateTime(DateUtils.getNowDate());
        newProductApply.setUpdateBy(SecurityUtils.getUsername());
        
        newProductApplyMapper.deleteNewProductApplyEntryByParentId(newProductApply.getId());
        newProductApplyMapper.deleteNewProductApplyDetailByParentId(newProductApply.getId());
        insertNewProductApplyEntry(newProductApply);
        insertNewProductApplyDetail(newProductApply);
        return newProductApplyMapper.updateNewProductApply(newProductApply);
    }

    /**
     * 批量删除新品申购申请
     * 
     * @param ids 需要删除的新品申购申请主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteNewProductApplyByIds(Long[] ids)
    {
        newProductApplyMapper.deleteNewProductApplyEntryByParentIds(ids);
        return newProductApplyMapper.deleteNewProductApplyByIds(ids);
    }

    /**
     * 删除新品申购申请信息
     * 
     * @param id 新品申购申请主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteNewProductApplyById(Long id)
    {
        newProductApplyMapper.deleteNewProductApplyEntryByParentId(id);
        newProductApplyMapper.deleteNewProductApplyDetailByParentId(id);
        return newProductApplyMapper.deleteNewProductApplyById(id);
    }

    /**
     * 新增新品申购申请明细信息
     * 
     * @param newProductApply 新品申购申请对象
     */
    public void insertNewProductApplyEntry(NewProductApply newProductApply)
    {
        List<NewProductApplyEntry> newProductApplyEntryList = newProductApply.getApplyEntryList();
        Long id = newProductApply.getId();
        if (StringUtils.isNotNull(newProductApplyEntryList))
        {
            List<NewProductApplyEntry> list = new ArrayList<NewProductApplyEntry>();
            for (NewProductApplyEntry newProductApplyEntry : newProductApplyEntryList)
            {
                newProductApplyEntry.setParentId(id);
                newProductApplyEntry.setCreateTime(DateUtils.getNowDate());
                newProductApplyEntry.setCreateBy(SecurityUtils.getUsername());
                list.add(newProductApplyEntry);
            }
            if (list.size() > 0)
            {
                newProductApplyMapper.batchNewProductApplyEntry(list);
            }
        }
    }

    /**
     * 新增院内同类产品信息
     * 
     * @param newProductApply 新品申购申请对象
     */
    public void insertNewProductApplyDetail(NewProductApply newProductApply)
    {
        List<NewProductApplyDetail> newProductApplyDetailList = newProductApply.getApplyDetailList();
        Long id = newProductApply.getId();
        if (StringUtils.isNotNull(newProductApplyDetailList))
        {
            List<NewProductApplyDetail> list = new ArrayList<NewProductApplyDetail>();
            for (NewProductApplyDetail newProductApplyDetail : newProductApplyDetailList)
            {
                // 只保存有数据的记录
                if (StringUtils.isNotEmpty(newProductApplyDetail.getSimilarProduct()) 
                    || StringUtils.isNotEmpty(newProductApplyDetail.getSpeci())
                    || StringUtils.isNotEmpty(newProductApplyDetail.getModel()))
                {
                    newProductApplyDetail.setParentId(id);
                    newProductApplyDetail.setCreateTime(DateUtils.getNowDate());
                    newProductApplyDetail.setCreateBy(SecurityUtils.getUsername());
                    list.add(newProductApplyDetail);
                }
            }
            if (list.size() > 0)
            {
                newProductApplyMapper.batchNewProductApplyDetail(list);
            }
        }
    }

    /**
     * 生成申购单号
     * 
     * @return 申购单号
     */
    private String generateApplyNo() {
        String dateStr = DateUtils.dateTimeNow("yyyyMMdd");
        String prefix = "XPSG" + dateStr;
        
        // 简单的序号生成，实际项目中可能需要更复杂的逻辑
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
        return prefix + timestamp;
    }
}

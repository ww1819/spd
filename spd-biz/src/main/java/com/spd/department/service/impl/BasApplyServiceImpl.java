package com.spd.department.service.impl;

import java.util.Date;
import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.rule.FillRuleUtil;
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
        List<BasApplyEntry> basApplyEntryList = basApply.getBasApplyEntryList();
        for (BasApplyEntry basApplyEntry : basApplyEntryList) {
            FdMaterial material = this.fdMaterialMapper.selectFdMaterialById(basApplyEntry.getMaterialId());
            basApplyEntry.setMaterial(material);
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
        return basApplyMapper.selectBasApplyList(basApply);
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
        basApply.setCreateTime(DateUtils.getNowDate());
        basApply.setApplyBillNo(getNumber());
        int rows = basApplyMapper.insertBasApply(basApply);
        insertBasApplyEntry(basApply);
        return rows;
    }

    //str：单号前缀
    //date：日期
    //result：最终结果，需要的流水号
    public String getNumber() {
        String str = "SL";
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
        basApply.setUpdateTime(DateUtils.getNowDate());
        basApplyMapper.deleteBasApplyEntryByParenId(basApply.getId());
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
        basApplyMapper.deleteBasApplyEntryByParenIds(ids);
        return basApplyMapper.deleteBasApplyByIds(ids);
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
        basApplyMapper.deleteBasApplyEntryByParenId(id);
        return basApplyMapper.deleteBasApplyById(id);
    }

    /**
     * 审核科室申领
     * @param id
     * @return
     */
    @Override
    public int auditApply(String id) {
        BasApply basApply = basApplyMapper.selectBasApplyById(Long.parseLong(id));
        if(basApply == null){
            throw new ServiceException(String.format("科室申领ID：%s，不存在!", id));
        }
        basApply.setApplyBillStatus(2);//已审核状态

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
        if (StringUtils.isNotNull(basApplyEntryList))
        {
            List<BasApplyEntry> list = new ArrayList<BasApplyEntry>();
            for (BasApplyEntry basApplyEntry : basApplyEntryList)
            {
                basApplyEntry.setParenId(id);
                list.add(basApplyEntry);
            }
            if (list.size() > 0)
            {
                basApplyMapper.batchBasApplyEntry(list);
            }
        }
    }
}

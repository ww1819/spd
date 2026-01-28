package com.spd.department.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
        List<DeptBatchConsumeEntry> deptBatchConsumeEntryList = deptBatchConsume.getDeptBatchConsumeEntryList();
        if (deptBatchConsumeEntryList != null) {
            for (DeptBatchConsumeEntry deptBatchConsumeEntry : deptBatchConsumeEntryList) {
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
        int rows = deptBatchConsumeMapper.insertDeptBatchConsume(deptBatchConsume);
        insertDeptBatchConsumeEntry(deptBatchConsume);
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
        deptBatchConsumeMapper.deleteDeptBatchConsumeEntryByParenId(deptBatchConsume.getId());
        insertDeptBatchConsumeEntry(deptBatchConsume);
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
        deptBatchConsumeMapper.deleteDeptBatchConsumeEntryByParenIds(ids);
        return deptBatchConsumeMapper.deleteDeptBatchConsumeByIds(ids);
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
        deptBatchConsumeMapper.deleteDeptBatchConsumeEntryByParenId(id);
        return deptBatchConsumeMapper.deleteDeptBatchConsumeById(id);
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
        DeptBatchConsume deptBatchConsume = deptBatchConsumeMapper.selectDeptBatchConsumeById(Long.parseLong(id));
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
    public void insertDeptBatchConsumeEntry(DeptBatchConsume deptBatchConsume)
    {
        List<DeptBatchConsumeEntry> deptBatchConsumeEntryList = deptBatchConsume.getDeptBatchConsumeEntryList();
        Long id = deptBatchConsume.getId();
        if (StringUtils.isNotNull(deptBatchConsumeEntryList))
        {
            List<DeptBatchConsumeEntry> list = new ArrayList<DeptBatchConsumeEntry>();
            for (DeptBatchConsumeEntry deptBatchConsumeEntry : deptBatchConsumeEntryList)
            {
                deptBatchConsumeEntry.setParenId(id);
                deptBatchConsumeEntry.setDelFlag(0);
                deptBatchConsumeEntry.setCreateBy(deptBatchConsume.getCreateBy());
                list.add(deptBatchConsumeEntry);
            }
            if (list.size() > 0)
            {
                deptBatchConsumeMapper.batchDeptBatchConsumeEntry(list);
            }
        }
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
}

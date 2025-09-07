package com.spd.caigou.service.impl;

import com.spd.caigou.domain.PurchasePlan;
import com.spd.caigou.domain.PurchasePlanEntry;
import com.spd.caigou.mapper.PurchasePlanMapper;
import com.spd.caigou.service.IPurchasePlanService;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 采购计划Service业务层处理
 *
 * @author spd
 * @date 2024-01-15
 */
@Service
public class PurchasePlanServiceImpl implements IPurchasePlanService 
{
    @Autowired
    private PurchasePlanMapper purchasePlanMapper;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    /**
     * 查询采购计划
     *
     * @param id 采购计划主键
     * @return 采购计划
     */
    @Override
    public PurchasePlan selectPurchasePlanById(Long id)
    {
        PurchasePlan purchasePlan = purchasePlanMapper.selectPurchasePlanById(id);
        if (purchasePlan == null) {
            return null;
        }
        List<PurchasePlanEntry> purchasePlanEntryList = purchasePlanMapper.selectPurchasePlanEntryByParentId(id);
        List<FdMaterial> materialList = new ArrayList<FdMaterial>();
        for(PurchasePlanEntry entry : purchasePlanEntryList){
            Long materialId = entry.getMaterialId();
            FdMaterial fdMaterial = fdMaterialMapper.selectFdMaterialById(materialId);
            materialList.add(fdMaterial);
        }
        purchasePlan.setPurchasePlanEntryList(purchasePlanEntryList);
        return purchasePlan;
    }

    /**
     * 查询采购计划列表
     *
     * @param purchasePlan 采购计划
     * @return 采购计划
     */
    @Override
    public List<PurchasePlan> selectPurchasePlanList(PurchasePlan purchasePlan)
    {
        return purchasePlanMapper.selectPurchasePlanList(purchasePlan);
    }

    /**
     * 新增采购计划
     *
     * @param purchasePlan 采购计划
     * @return 结果
     */
    @Transactional
    @Override
    public int insertPurchasePlan(PurchasePlan purchasePlan)
    {
        purchasePlan.setPlanNo(getPlanNumber());
        purchasePlan.setPlanStatus("1"); // 待审核状态
        purchasePlan.setCreateTime(DateUtils.getNowDate());
        purchasePlan.setCreateBy(SecurityUtils.getLoginUser().getUsername());
        int rows = purchasePlanMapper.insertPurchasePlan(purchasePlan);
        insertPurchasePlanEntry(purchasePlan);
        return rows;
    }

    /**
     * 修改采购计划
     *
     * @param purchasePlan 采购计划
     * @return 结果
     */
    @Transactional
    @Override
    public int updatePurchasePlan(PurchasePlan purchasePlan)
    {
        purchasePlan.setUpdateTime(DateUtils.getNowDate());
        purchasePlan.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        purchasePlanMapper.deletePurchasePlanEntryByParentId(purchasePlan.getId());
        insertPurchasePlanEntry(purchasePlan);
        return purchasePlanMapper.updatePurchasePlan(purchasePlan);
    }

    /**
     * 批量删除采购计划
     *
     * @param ids 需要删除的采购计划主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletePurchasePlanByIds(Long[] ids)
    {
        purchasePlanMapper.deletePurchasePlanEntryByParentIds(ids);
        return purchasePlanMapper.deletePurchasePlanByIds(ids);
    }

    /**
     * 删除采购计划信息
     *
     * @param id 采购计划主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletePurchasePlanById(Long id)
    {
        PurchasePlan purchasePlan = purchasePlanMapper.selectPurchasePlanById(id);
        if(purchasePlan == null){
            throw new ServiceException(String.format("采购计划ID：%s，不存在!", id));
        }

        purchasePlan.setDelFlag("1");
        purchasePlan.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        purchasePlan.setUpdateTime(new Date());

        List<PurchasePlanEntry> purchasePlanEntryList = purchasePlan.getPurchasePlanEntryList();
        for(PurchasePlanEntry entry : purchasePlanEntryList){
            entry.setDelFlag("1");
            entry.setParentId(id);
            purchasePlanMapper.updatePurchasePlanEntry(entry);
        }

        return purchasePlanMapper.updatePurchasePlan(purchasePlan);
    }

    /**
     * 审核采购计划
     *
     * @param id 采购计划主键
     * @param auditBy 审核人
     * @return 结果
     */
    @Transactional
    @Override
    public int auditPurchasePlan(Long id, String auditBy)
    {
        PurchasePlan purchasePlan = purchasePlanMapper.selectPurchasePlanById(id);
        if(purchasePlan == null){
            throw new ServiceException(String.format("采购计划ID：%s，不存在!", id));
        }

        purchasePlan.setPlanStatus("2"); // 已审核状态
        purchasePlan.setAuditBy(auditBy);
        purchasePlan.setAuditDate(new Date());
        purchasePlan.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        purchasePlan.setUpdateTime(new Date());

        return purchasePlanMapper.auditPurchasePlan(purchasePlan);
    }

    /**
     * 生成计划单号
     *
     * @return 计划单号
     */
    public String getPlanNumber() {
        String str = "JH";
        String date = FillRuleUtil.getDateNum();
        String maxNum = purchasePlanMapper.selectMaxPlanNo(date);
        String result = FillRuleUtil.getNumber(str, maxNum, date);
        return result;
    }

    /**
     * 新增采购计划明细信息
     *
     * @param purchasePlan 采购计划对象
     */
    public void insertPurchasePlanEntry(PurchasePlan purchasePlan)
    {
        List<PurchasePlanEntry> purchasePlanEntryList = purchasePlan.getPurchasePlanEntryList();
        Long id = purchasePlan.getId();
        if (StringUtils.isNotNull(purchasePlanEntryList))
        {
            List<PurchasePlanEntry> list = new ArrayList<PurchasePlanEntry>();
            for (PurchasePlanEntry purchasePlanEntry : purchasePlanEntryList)
            {
                purchasePlanEntry.setParentId(id);
                purchasePlanEntry.setDelFlag("0");
                purchasePlanEntry.setCreateBy(SecurityUtils.getLoginUser().getUsername());
                purchasePlanEntry.setCreateTime(new Date());
                list.add(purchasePlanEntry);
            }
            if (list.size() > 0)
            {
                purchasePlanMapper.batchPurchasePlanEntry(list);
            }
        }
    }
}

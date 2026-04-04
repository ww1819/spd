package com.spd.department.service.impl;

import java.math.BigDecimal;
import java.util.List;

import com.spd.common.core.page.TotalInfo;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.department.mapper.StkDepInventoryMapper;
import com.spd.department.domain.StkDepInventory;
import com.spd.department.service.IStkDepInventoryService;
import com.spd.department.vo.InventorySummaryVo;
import com.spd.department.vo.DepartmentInOutDetailVo;

/**
 * 科室库存Service业务层处理
 * 
 * @author spd
 * @date 2024-03-04
 */
@Service
public class StkDepInventoryServiceImpl implements IStkDepInventoryService 
{
    @Autowired
    private StkDepInventoryMapper stkDepInventoryMapper;
    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    /**
     * 查询科室库存
     * 
     * @param id 科室库存主键
     * @return 科室库存
     */
    @Override
    public StkDepInventory selectStkDepInventoryById(Long id)
    {
        StkDepInventory inv = stkDepInventoryMapper.selectStkDepInventoryById(id);
        if (inv != null) {
            SecurityUtils.ensureTenantAccess(inv.getTenantId());
        }
        return inv;
    }

    /**
     * 查询科室库存列表
     * 
     * @param stkDepInventory 科室库存
     * @return 科室库存
     */
    @Override
    public List<StkDepInventory> selectStkDepInventoryList(StkDepInventory stkDepInventory)
    {
        if (stkDepInventory != null && StringUtils.isEmpty(stkDepInventory.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkDepInventory.setTenantId(SecurityUtils.getCustomerId());
        }
        List<StkDepInventory> list = stkDepInventoryMapper.selectStkDepInventoryList(stkDepInventory);
        for (StkDepInventory depInventory : list) {
            FdMaterial fdMaterial = this.fdMaterialMapper.selectFdMaterialById(depInventory.getMaterialId());
            depInventory.setMaterial(fdMaterial);
        }
        return list;
    }

    @Override
    public TotalInfo selectStkDepInventoryListTotal(StkDepInventory stkDepInventory)
    {
        if (stkDepInventory != null && StringUtils.isEmpty(stkDepInventory.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkDepInventory.setTenantId(SecurityUtils.getCustomerId());
        }
        return stkDepInventoryMapper.selectStkDepInventoryListTotal(stkDepInventory);
    }

    /**
     * 新增科室库存
     * 
     * @param stkDepInventory 科室库存
     * @return 结果
     */
    @Override
    public int insertStkDepInventory(StkDepInventory stkDepInventory)
    {
        if (StringUtils.isEmpty(stkDepInventory.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            stkDepInventory.setTenantId(SecurityUtils.getCustomerId());
        }
        if (StringUtils.isEmpty(stkDepInventory.getCreateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            stkDepInventory.setCreateBy(SecurityUtils.getUserIdStr());
        }
        return stkDepInventoryMapper.insertStkDepInventory(stkDepInventory);
    }

    /**
     * 修改科室库存
     * 
     * @param stkDepInventory 科室库存
     * @return 结果
     */
    @Override
    public int updateStkDepInventory(StkDepInventory stkDepInventory)
    {
        if (stkDepInventory == null) {
            return 0;
        }
        if (stkDepInventory.getQty() != null && stkDepInventory.getUnitPrice() != null) {
            stkDepInventory.setAmt(stkDepInventory.getQty().multiply(stkDepInventory.getUnitPrice()));
        } else if (stkDepInventory.getQty() != null && stkDepInventory.getAmt() == null) {
            stkDepInventory.setAmt(BigDecimal.ZERO);
        }
        if (StringUtils.isEmpty(stkDepInventory.getUpdateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            stkDepInventory.setUpdateBy(SecurityUtils.getUserIdStr());
        }
        return stkDepInventoryMapper.updateStkDepInventory(stkDepInventory);
    }

    /**
     * 批量删除科室库存
     * 
     * @param ids 需要删除的科室库存主键
     * @return 结果
     */
    @Override
    public int deleteStkDepInventoryByIds(Long[] ids)
    {
        for (Long id : ids) {
            StkDepInventory existing = stkDepInventoryMapper.selectStkDepInventoryById(id);
            if (existing != null) {
                SecurityUtils.ensureTenantAccess(existing.getTenantId());
            }
        }
        return stkDepInventoryMapper.deleteStkDepInventoryByIds(ids, SecurityUtils.getUserIdStr());
    }

    /**
     * 删除科室库存信息
     * 
     * @param id 科室库存主键
     * @return 结果
     */
    @Override
    public int deleteStkDepInventoryById(Long id)
    {
        StkDepInventory existing = stkDepInventoryMapper.selectStkDepInventoryById(id);
        if (existing != null) {
            SecurityUtils.ensureTenantAccess(existing.getTenantId());
        }
        return stkDepInventoryMapper.deleteStkDepInventoryById(id, SecurityUtils.getUserIdStr());
    }

    /**
     * 查询库存汇总列表
     * 
     * @param stkDepInventory 查询条件
     * @return 库存汇总集合
     */
    @Override
    public List<InventorySummaryVo> selectInventorySummaryList(StkDepInventory stkDepInventory)
    {
        return stkDepInventoryMapper.selectInventorySummaryList(stkDepInventory);
    }

    /**
     * 查询科室进销存明细列表
     * 
     * @param stkDepInventory 查询条件
     * @return 进销存明细集合
     */
    @Override
    public List<DepartmentInOutDetailVo> selectDepartmentInOutDetailList(StkDepInventory stkDepInventory)
    {
        return stkDepInventoryMapper.selectDepartmentInOutDetailList(stkDepInventory);
    }
}

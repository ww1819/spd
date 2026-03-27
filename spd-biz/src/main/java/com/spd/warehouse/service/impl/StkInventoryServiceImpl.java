package com.spd.warehouse.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.spd.common.core.page.TotalInfo;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.warehouse.mapper.StkInventoryMapper;
import com.spd.warehouse.domain.StkInventory;
import com.spd.warehouse.service.IStkInventoryService;

/**
 * 库存明细Service业务层处理
 *
 * @author spd
 * @date 2023-12-17
 */
@Service
public class StkInventoryServiceImpl implements IStkInventoryService
{
    @Autowired
    private StkInventoryMapper stkInventoryMapper;
    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    /**
     * 查询库存明细
     *
     * @param id 库存明细主键
     * @return 库存明细
     */
    @Override
    public StkInventory selectStkInventoryById(Long id)
    {
        StkInventory inv = stkInventoryMapper.selectStkInventoryById(id);
        if (inv != null) {
            SecurityUtils.ensureTenantAccess(inv.getTenantId());
        }
        return inv;
    }

    /**
     * 查询库存明细列表
     *
     * @param stkInventory 库存明细
     * @return 库存明细
     */
    @Override
    public List<StkInventory> selectStkInventoryList(StkInventory stkInventory)
    {
        if (stkInventory != null && StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        List<StkInventory> list = stkInventoryMapper.selectStkInventoryList(stkInventory);
        if (list != null && list.size() > 0) {
            for (StkInventory inventory : list) {
                Long materialId = inventory.getMaterialId();
                FdMaterial material = this.fdMaterialMapper.selectFdMaterialById(materialId);
                inventory.setMaterial(material);
            }
        }
        return list;
    }

    @Override
    public TotalInfo selectStkInventoryListTotal(StkInventory stkInventory) {
        if (stkInventory != null && StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        return this.stkInventoryMapper.selectStkInventoryListTotal(stkInventory);
    }

    /**
     * 新增库存明细
     *
     * @param stkInventory 库存明细
     * @return 结果
     */
    @Override
    public int insertStkInventory(StkInventory stkInventory)
    {
        if (StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        if (StringUtils.isEmpty(stkInventory.getCreateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            stkInventory.setCreateBy(SecurityUtils.getUserIdStr());
        }
        return stkInventoryMapper.insertStkInventory(stkInventory);
    }

    /**
     * 修改库存明细
     *
     * @param stkInventory 库存明细
     * @return 结果
     */
    @Override
    public int updateStkInventory(StkInventory stkInventory)
    {
        if (stkInventory == null) {
            return 0;
        }
        if (stkInventory.getQty() != null && stkInventory.getUnitPrice() != null) {
            stkInventory.setAmt(stkInventory.getQty().multiply(stkInventory.getUnitPrice()));
        } else if (stkInventory.getQty() != null && stkInventory.getAmt() == null) {
            stkInventory.setAmt(BigDecimal.ZERO);
        }
        if (StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        if (StringUtils.isEmpty(stkInventory.getUpdateBy()) && StringUtils.isNotEmpty(SecurityUtils.getUserIdStr())) {
            stkInventory.setUpdateBy(SecurityUtils.getUserIdStr());
        }
        return stkInventoryMapper.updateStkInventory(stkInventory);
    }

    /**
     * 批量删除库存明细
     *
     * @param ids 需要删除的库存明细主键
     * @return 结果
     */
    @Override
    public int deleteStkInventoryByIds(Long[] ids)
    {
        for (Long id : ids) {
            StkInventory existing = stkInventoryMapper.selectStkInventoryById(id);
            if (existing != null) {
                SecurityUtils.ensureTenantAccess(existing.getTenantId());
            }
        }
        return stkInventoryMapper.deleteStkInventoryByIds(ids, SecurityUtils.getUserIdStr());
    }

    /**
     * 删除库存明细信息
     *
     * @param id 库存明细主键
     * @return 结果
     */
    @Override
    public int deleteStkInventoryById(Long id)
    {
        StkInventory existing = stkInventoryMapper.selectStkInventoryById(id);
        if (existing != null) {
            SecurityUtils.ensureTenantAccess(existing.getTenantId());
        }
        return stkInventoryMapper.deleteStkInventoryById(id, SecurityUtils.getUserIdStr());
    }

    /**
     * 按仓库筛选实时库存耗材
     * @param stkInventory
     * @return
     */
    @Override
    public List<StkInventory> selectStkMaterialList(StkInventory stkInventory) {
        if (stkInventory != null && StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        return stkInventoryMapper.selectStkMaterialList(stkInventory);
    }

    @Override
    public List<StkInventory> selectPDInventoryFilter(StkInventory stkInventory) {
        if (stkInventory != null && StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        return stkInventoryMapper.selectPDInventoryFilter(stkInventory);
    }

    /**
     * 查询库存明细汇总列表
     * @param stkInventory
     * @return
     */
    @Override
    public List<Map<String, Object>> selectStkInventoryListSummary(StkInventory stkInventory) {
        if (stkInventory != null && StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        return stkInventoryMapper.selectStkInventoryListSummary(stkInventory);
    }

    @Override
    public TotalInfo selectStkInventoryListSummaryTotal(StkInventory stkInventory) {
        if (stkInventory != null && StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        return stkInventoryMapper.selectStkInventoryListSummaryTotal(stkInventory);
    }

    @Override
    public List<Map<String, Object>> selectInventoryAlertList(StkInventory stkInventory) {
        if (stkInventory != null && StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        return stkInventoryMapper.selectInventoryAlertList(stkInventory);
    }

    @Override
    public List<Map<String, Object>> selectExpiryAlertList(StkInventory stkInventory) {
        if (stkInventory != null && StringUtils.isEmpty(stkInventory.getTenantId())) {
            stkInventory.setTenantId(SecurityUtils.requiredScopedTenantIdForSql());
        }
        return stkInventoryMapper.selectExpiryAlertList(stkInventory);
    }

}

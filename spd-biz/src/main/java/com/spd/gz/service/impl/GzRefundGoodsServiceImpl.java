package com.spd.gz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.gz.domain.GzDepotInventory;
import com.spd.gz.mapper.GzDepotInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.spd.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.spd.gz.domain.GzRefundGoodsEntry;
import com.spd.gz.mapper.GzRefundGoodsMapper;
import com.spd.gz.domain.GzRefundGoods;
import com.spd.gz.service.IGzRefundGoodsService;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;

/**
 * 高值退货Service业务层处理
 *
 * @author spd
 * @date 2024-06-11
 */
@Service
public class GzRefundGoodsServiceImpl implements IGzRefundGoodsService
{
    @Autowired
    private GzRefundGoodsMapper gzRefundGoodsMapper;

    @Autowired
    private GzDepotInventoryMapper gzDepotInventoryMapper;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    /**
     * 查询高值退货
     *
     * @param id 高值退货主键
     * @return 高值退货
     */
    @Override
    public GzRefundGoods selectGzRefundGoodsById(Long id)
    {
        GzRefundGoods gzRefundGoods = gzRefundGoodsMapper.selectGzRefundGoodsById(id);
        if(gzRefundGoods == null){
            return null;
        }

        List<GzRefundGoodsEntry> gzRefundGoodsEntryList = gzRefundGoods.getGzRefundGoodsEntryList();
        if(gzRefundGoodsEntryList != null && !gzRefundGoodsEntryList.isEmpty()){
            List<FdMaterial> materialList = new ArrayList<FdMaterial>();
            for(GzRefundGoodsEntry entry : gzRefundGoodsEntryList){
                Long materialId = entry.getMaterialId();
                if(materialId != null){
                    FdMaterial fdMaterial = fdMaterialMapper.selectFdMaterialById(materialId);
                    if(fdMaterial != null){
                        materialList.add(fdMaterial);
                    }
                }
            }
            gzRefundGoods.setMaterialList(materialList);
        }
        return gzRefundGoods;
    }

    /**
     * 查询高值退货列表
     *
     * @param gzRefundGoods 高值退货
     * @return 高值退货
     */
    @Override
    public List<GzRefundGoods> selectGzRefundGoodsList(GzRefundGoods gzRefundGoods)
    {
        if (gzRefundGoods != null && StringUtils.isEmpty(gzRefundGoods.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            gzRefundGoods.setTenantId(SecurityUtils.getCustomerId());
        }
        return gzRefundGoodsMapper.selectGzRefundGoodsList(gzRefundGoods);
    }

    /**
     * 新增高值退货
     *
     * @param gzRefundGoods 高值退货
     * @return 结果
     */
    @Transactional
    @Override
    public int insertGzRefundGoods(GzRefundGoods gzRefundGoods)
    {
        if (gzRefundGoods == null) {
            throw new ServiceException("高值退货单不能为空");
        }
        if (StringUtils.isEmpty(gzRefundGoods.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            gzRefundGoods.setTenantId(SecurityUtils.getCustomerId());
        }
        gzRefundGoods.setCreateBy(SecurityUtils.getUserIdStr());
        gzRefundGoods.setGoodsNo(getOrderNo());
        gzRefundGoods.setCreateTime(DateUtils.getNowDate());
        int rows = gzRefundGoodsMapper.insertGzRefundGoods(gzRefundGoods);
        insertGzRefundGoodsEntry(gzRefundGoods);
        return rows;
    }

    //生成单号
    public String getOrderNo() {
        String str = "GZTH-";
        String date = FillRuleUtil.getDateNum();
        String maxNum = gzRefundGoodsMapper.selectMaxBillNo(date);
        String result = FillRuleUtil.getNumber(str,maxNum,date);
        return result;
    }

    /**
     * 修改高值退货
     *
     * @param gzRefundGoods 高值退货
     * @return 结果
     */
    @Transactional
    @Override
    public int updateGzRefundGoods(GzRefundGoods gzRefundGoods)
    {
        if (StringUtils.isEmpty(gzRefundGoods.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            gzRefundGoods.setTenantId(SecurityUtils.getCustomerId());
        }
        gzRefundGoods.setUpdateBy(SecurityUtils.getUserIdStr());
        gzRefundGoods.setUpdateTime(DateUtils.getNowDate());
        gzRefundGoodsMapper.deleteGzRefundGoodsEntryByParenId(gzRefundGoods.getId(), SecurityUtils.getUserIdStr());
        insertGzRefundGoodsEntry(gzRefundGoods);
        return gzRefundGoodsMapper.updateGzRefundGoods(gzRefundGoods);
    }

    /**
     * 删除高值退货信息
     *
     * @param id 高值退货主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteGzRefundGoodsById(Long id)
    {
        GzRefundGoods gzRefundGoods = gzRefundGoodsMapper.selectGzRefundGoodsById(id);
        if(gzRefundGoods == null){
            throw new ServiceException(String.format("高值退货业务：%s，不存在!", id));
        }
        String deleteBy = SecurityUtils.getUserIdStr();
        gzRefundGoodsMapper.deleteGzRefundGoodsEntryByParenId(id, deleteBy);
        return gzRefundGoodsMapper.deleteGzRefundGoodsById(id, deleteBy);
    }

    @Override
    public int auditGoods(String id) {
        Long billId;
        try {
            billId = Long.valueOf(id);
        } catch (Exception e) {
            throw new ServiceException(String.format("高值退货业务ID：%s 非法", id));
        }
        GzRefundGoods gzRefundGoods = gzRefundGoodsMapper.selectGzRefundGoodsById(billId);
        if(gzRefundGoods == null){
            throw new ServiceException(String.format("高值退货业务ID：%s，不存在!", id));
        }

        List<GzRefundGoodsEntry> gzRefundGoodsEntryList = gzRefundGoods.getGzRefundGoodsEntryList();
        if (gzRefundGoodsEntryList == null || gzRefundGoodsEntryList.isEmpty()) {
            throw new ServiceException(String.format("高值退货单 %s 无明细，无法审核", id));
        }
        if (gzRefundGoods.getSupplerId() == null) {
            throw new ServiceException(String.format("高值退货单 %s 表头供应商不能为空", id));
        }
        for (GzRefundGoodsEntry entry : gzRefundGoodsEntryList) {
            if (entry == null) {
                continue;
            }
            if (entry.getSupplierId() == null) {
                throw new ServiceException(String.format("高值退货单 %s 明细供应商不能为空", id));
            }
            if (!entry.getSupplierId().equals(gzRefundGoods.getSupplerId())) {
                throw new ServiceException(String.format("高值退货单 %s 明细供应商与表头供应商不一致，不允许审核", id));
            }
        }

        //更新高值库存明细表
        updateGzDepotInventory(gzRefundGoods,gzRefundGoodsEntryList);

        gzRefundGoods.setGoodsStatus(2);
        gzRefundGoods.setAuditDate(new Date());
        gzRefundGoods.setAuditBy(SecurityUtils.getUserIdStr());
        int res = gzRefundGoodsMapper.updateGzRefundGoods(gzRefundGoods);
        return res;
    }

    /**
     * 更新高值库存明细表
     * 备货退货逻辑：直接减少备货库存（仓库退货给供应商）
     * @param gzRefundGoods
     * @param gzRefundGoodsEntryList
     */
    private void updateGzDepotInventory(GzRefundGoods gzRefundGoods,List<GzRefundGoodsEntry> gzRefundGoodsEntryList){
        if (gzRefundGoodsEntryList == null || gzRefundGoodsEntryList.isEmpty()) {
            return;
        }
        for(GzRefundGoodsEntry entry : gzRefundGoodsEntryList){
            if (entry == null) {
                continue;
            }
            String batchNo = entry.getBatchNo();
            BigDecimal qty = entry.getQty();
            if (StringUtils.isEmpty(batchNo)) {
                throw new ServiceException(String.format("高值退货明细批次号不能为空，退货单ID：%s", gzRefundGoods.getId()));
            }
            if (qty == null) {
                throw new ServiceException(String.format("高值退货明细数量不能为空，批次号：%s", batchNo));
            }
            if (qty.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ServiceException(String.format("高值退货明细数量必须大于0，批次号：%s", batchNo));
            }
            
            // 根据批次号查询备货库存
            GzDepotInventory gzDepotInventory = gzDepotInventoryMapper.selectGzDepotInventoryOne(batchNo);
            if(gzDepotInventory == null){
                throw new ServiceException(String.format("高值退货-批次号：%s，在备货库存中不存在!", batchNo));
            }
            
            BigDecimal depotInventoryQty = gzDepotInventory.getQty();
            if (depotInventoryQty == null) {
                throw new ServiceException(String.format("高值退货-备货库存数量为空，批次号：%s", batchNo));
            }
            
            // 备货库存数量不能小于退货数量
            if(qty.compareTo(depotInventoryQty) > 0){
                throw new ServiceException(String.format("高值备货库存不足！退货数量：%s，备货库存：%s", qty, depotInventoryQty));
            }
            
            // 减少备货库存（仓库退货给供应商）
            gzDepotInventory.setQty(depotInventoryQty.subtract(qty));
            gzDepotInventory.setUpdateTime(new Date());
            gzDepotInventory.setUpdateBy(SecurityUtils.getUserIdStr());
            gzDepotInventoryMapper.updateGzDepotInventory(gzDepotInventory);
        }
    }

    /**
     * 新增高值退货明细信息
     *
     * @param gzRefundGoods 高值退货对象
     */
    public void insertGzRefundGoodsEntry(GzRefundGoods gzRefundGoods)
    {
        List<GzRefundGoodsEntry> gzRefundGoodsEntryList = gzRefundGoods.getGzRefundGoodsEntryList();
        Long id = gzRefundGoods.getId();
        if (StringUtils.isNotNull(gzRefundGoodsEntryList))
        {
            List<GzRefundGoodsEntry> list = new ArrayList<GzRefundGoodsEntry>();
            // tenant_id 在 mapper 批量写入时依赖 entry.tenantId，必须严格解析且不允许为空
            String tenantId = StringUtils.isNotEmpty(gzRefundGoods.getTenantId())
                ? gzRefundGoods.getTenantId()
                : SecurityUtils.requiredScopedTenantIdForSql();
            String userId = SecurityUtils.getUserIdStr();
            Date now = DateUtils.getNowDate();
            for (GzRefundGoodsEntry gzRefundGoodsEntry : gzRefundGoodsEntryList)
            {
                if (gzRefundGoodsEntry == null) {
                    continue;
                }
                validateGzDepotInventory(gzRefundGoodsEntry.getBatchNo(),gzRefundGoodsEntryList);
                gzRefundGoodsEntry.setParenId(id);
                gzRefundGoodsEntry.setDelFlag(0);
                gzRefundGoodsEntry.setTenantId(tenantId);
                gzRefundGoodsEntry.setSupplierId(gzRefundGoods.getSupplerId());
                gzRefundGoodsEntry.setWarehouseId(gzRefundGoods.getWarehouseId());
                gzRefundGoodsEntry.setBillNo(gzRefundGoods.getGoodsNo());
                gzRefundGoodsEntry.setCreateBy(userId);
                gzRefundGoodsEntry.setCreateTime(now);
                gzRefundGoodsEntry.setUpdateBy(userId);
                gzRefundGoodsEntry.setUpdateTime(now);
                list.add(gzRefundGoodsEntry);
            }
            if (list.size() > 0)
            {
                gzRefundGoodsMapper.batchGzRefundGoodsEntry(list);
            }
        }
    }

    /**
     * 校验高值备货库存
     */
    private void validateGzDepotInventory(String oldBatchNo,List<GzRefundGoodsEntry> gzRefundGoodsEntryList){
        if (StringUtils.isEmpty(oldBatchNo) || gzRefundGoodsEntryList == null) {
            return;
        }
        for(GzRefundGoodsEntry entry : gzRefundGoodsEntryList){
            if (entry == null) {
                continue;
            }
            String batchNo = entry.getBatchNo();//批次号
            BigDecimal qty = entry.getQty();//退货数量
            if(!oldBatchNo.equals(batchNo)){
                continue;
            }

            // 根据批次号查询备货库存
            GzDepotInventory gzDepotInventory = gzDepotInventoryMapper.selectGzDepotInventoryOne(batchNo);
            if(gzDepotInventory == null){
                throw new ServiceException(String.format("高值退货-批次号：%s，在备货库存中不存在!", batchNo));
            }
            
            BigDecimal inventoryQty = gzDepotInventory.getQty();
            if (inventoryQty == null) {
                throw new ServiceException(String.format("高值退货-备货库存数量为空，批次号：%s", batchNo));
            }
            if (qty == null) {
                throw new ServiceException(String.format("高值退货明细数量不能为空，批次号：%s", batchNo));
            }
            if (qty.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ServiceException(String.format("高值退货明细数量必须大于0，批次号：%s", batchNo));
            }

            if(qty.compareTo(inventoryQty) > 0){
                throw new ServiceException(String.format("高值备货库存不足！退货数量：%s，备货库存：%s", qty, inventoryQty));
            }
        }
    }
}

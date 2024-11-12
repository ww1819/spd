package com.spd.gz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.gz.domain.GzDepotInventory;
import com.spd.gz.domain.GzOrderEntry;
import com.spd.gz.mapper.GzDepotInventoryMapper;
import com.spd.warehouse.domain.StkIoBillEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.spd.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.spd.gz.mapper.GzOrderMapper;
import com.spd.gz.domain.GzOrder;
import com.spd.gz.service.IGzOrderService;

/**
 * 高值入库Service业务层处理
 *
 * @author spd
 * @date 2024-06-11
 */
@Service
public class GzOrderServiceImpl implements IGzOrderService
{
    @Autowired
    private GzOrderMapper gzOrderMapper;

    @Autowired
    private GzDepotInventoryMapper gzDepotInventoryMapper;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    /**
     * 查询高值入库
     *
     * @param id 高值入库主键
     * @return 高值入库
     */
    @Override
    public GzOrder selectGzOrderById(Long id)
    {
        GzOrder gzOrder = gzOrderMapper.selectGzOrderById(id);
        if(gzOrder == null){
            return null;
        }

        List<GzOrderEntry> gzOrderEntryList = gzOrder.getGzOrderEntryList();
        List<FdMaterial> materialList = new ArrayList<FdMaterial>();
        for(GzOrderEntry entry : gzOrderEntryList){
            Long materialId = entry.getMaterialId();
            FdMaterial fdMaterial = fdMaterialMapper.selectFdMaterialById(materialId);
            materialList.add(fdMaterial);
        }
        gzOrder.setMaterialList(materialList);
        return gzOrder;
    }

    /**
     * 查询高值入库列表
     *
     * @param gzOrder 高值入库
     * @return 高值入库
     */
    @Override
    public List<GzOrder> selectGzOrderList(GzOrder gzOrder)
    {
        return gzOrderMapper.selectGzOrderList(gzOrder);
    }

    /**
     * 新增高值入库
     *
     * @param gzOrder 高值入库
     * @return 结果
     */
    @Transactional
    @Override
    public int insertGzOrder(GzOrder gzOrder)
    {
        gzOrder.setOrderNo(getOrderNo());
        gzOrder.setCreateTime(DateUtils.getNowDate());
        int rows = gzOrderMapper.insertGzOrder(gzOrder);
        insertGzOrderEntry(gzOrder);
        return rows;
    }

    //str：单号前缀
    //date：日期
    //result：最终结果，需要的流水号
    public String getOrderNo() {
        String str = "GZRK";
        String date = FillRuleUtil.getDateNum();
        String maxNum = gzOrderMapper.selectMaxBillNo(date);
        String result = FillRuleUtil.getNumber(str,maxNum,date);
        return result;
    }

    /**
     * 修改高值入库
     *
     * @param gzOrder 高值入库
     * @return 结果
     */
    @Transactional
    @Override
    public int updateGzOrder(GzOrder gzOrder)
    {
        gzOrder.setUpdateTime(DateUtils.getNowDate());
        gzOrderMapper.deleteGzOrderEntryByParenId(gzOrder.getId());
        insertGzOrderEntry(gzOrder);
        return gzOrderMapper.updateGzOrder(gzOrder);
    }

    /**
     * 删除高值入库信息
     *
     * @param id 高值入库主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteGzOrderById(Long id)
    {
        GzOrder gzOrder = gzOrderMapper.selectGzOrderById(id);
        if(gzOrder == null){
            throw new ServiceException(String.format("高值入库业务：%s，不存在!", id));
        }

        gzOrder.setDelFlag(1);
        gzOrder.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        gzOrder.setUpdateTime(new Date());

        List<GzOrderEntry> gzOrderEntryList = gzOrder.getGzOrderEntryList();
        for(GzOrderEntry entry : gzOrderEntryList){
            entry.setDelFlag(1);
            entry.setParenId(id);
            gzOrderMapper.updateGzOrderEntry(entry);
        }

        return gzOrderMapper.updateGzOrder(gzOrder);
    }

    @Override
    @Transactional
    public int auditGzOrder(String id) {
        GzOrder gzOrder = gzOrderMapper.selectGzOrderById(Long.parseLong(id));
        if(gzOrder == null){
            throw new ServiceException(String.format("高值入库业务ID：%s，不存在!", id));
        }
        List<GzOrderEntry> gzOrderEntryList = gzOrder.getGzOrderEntryList();

        //更新高值仓库库存
        updateDepotInventory(gzOrder,gzOrderEntryList);

        gzOrder.setOrderStatus(2);
        gzOrder.setAuditDate(new Date());
        int res = gzOrderMapper.updateGzOrder(gzOrder);
        return res;
    }

    private void updateDepotInventory(GzOrder gzOrder,List<GzOrderEntry> gzOrderEntryList){
        GzDepotInventory gzDepotInventory = null;
        for(GzOrderEntry orderEntry : gzOrderEntryList){

            if(orderEntry.getQty() != null && BigDecimal.ZERO.compareTo(orderEntry.getQty()) != 0){
                gzDepotInventory = new GzDepotInventory();
                gzDepotInventory.setMaterialNo(orderEntry.getBatchNumber());
                gzDepotInventory.setBatchNo(orderEntry.getBatchNo());
                gzDepotInventory.setMaterialId(orderEntry.getMaterialId());
                gzDepotInventory.setWarehouseId(gzOrder.getWarehouseId());
                gzDepotInventory.setQty(orderEntry.getQty());
                gzDepotInventory.setUnitPrice(orderEntry.getPrice());
                gzDepotInventory.setAmt(orderEntry.getAmt());
                gzDepotInventory.setMaterialDate(new Date());
                gzDepotInventory.setWarehouseDate(new Date());
                gzDepotInventory.setSupplierId(gzOrder.getSupplerId());

                gzDepotInventoryMapper.insertGzDepotInventory(gzDepotInventory);
            }
        }
    }


    /**
     * 新增高值退货明细信息
     *
     * @param gzOrder 高值入库对象
     */
    public void insertGzOrderEntry(GzOrder gzOrder)
    {
        List<GzOrderEntry> gzOrderEntryList = gzOrder.getGzOrderEntryList();
        Long id = gzOrder.getId();
        if (StringUtils.isNotNull(gzOrderEntryList))
        {
            List<GzOrderEntry> list = new ArrayList<GzOrderEntry>();
            for (GzOrderEntry gzOrderEntry : gzOrderEntryList)
            {
                gzOrderEntry.setParenId(id);
                gzOrderEntry.setBatchNo(getBatchNumber());
                gzOrderEntry.setDelFlag(0);
                list.add(gzOrderEntry);
            }
            if (list.size() > 0)
            {
                gzOrderMapper.batchGzOrderEntry(list);
            }
        }
    }

    public String getBatchNumber() {
        String str = "GZPC";
        String createNo = FillRuleUtil.createBatchNo();
        String batchNo = str + createNo;
        return batchNo;
    }
}

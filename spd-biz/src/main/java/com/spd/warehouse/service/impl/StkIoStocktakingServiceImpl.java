package com.spd.warehouse.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.warehouse.domain.StkInventory;
import com.spd.warehouse.mapper.StkInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.spd.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.spd.warehouse.domain.StkIoStocktakingEntry;
import com.spd.warehouse.mapper.StkIoStocktakingMapper;
import com.spd.warehouse.domain.StkIoStocktaking;
import com.spd.warehouse.service.IStkIoStocktakingService;

/**
 * 盘点Service业务层处理
 *
 * @author spd
 * @date 2024-06-27
 */
@Service
public class StkIoStocktakingServiceImpl implements IStkIoStocktakingService
{
    @Autowired
    private StkIoStocktakingMapper stkIoStocktakingMapper;

    @Autowired
    private StkInventoryMapper stkInventoryMapper;

    /**
     * 查询盘点
     *
     * @param id 盘点主键
     * @return 盘点
     */
    @Override
    public StkIoStocktaking selectStkIoStocktakingById(Long id)
    {
        return stkIoStocktakingMapper.selectStkIoStocktakingById(id);
    }

    /**
     * 查询盘点列表
     *
     * @param stkIoStocktaking 盘点
     * @return 盘点
     */
    @Override
    public List<StkIoStocktaking> selectStkIoStocktakingList(StkIoStocktaking stkIoStocktaking)
    {
        return stkIoStocktakingMapper.selectStkIoStocktakingList(stkIoStocktaking);
    }

    /**
     * 新增盘点
     *
     * @param stkIoStocktaking 盘点
     * @return 结果
     */
    @Transactional
    @Override
    public int insertStkIoStocktaking(StkIoStocktaking stkIoStocktaking)
    {
        stkIoStocktaking.setStockNo(getNumber());
        stkIoStocktaking.setCreateTime(DateUtils.getNowDate());
        int rows = stkIoStocktakingMapper.insertStkIoStocktaking(stkIoStocktaking);
        insertStkIoStocktakingEntry(stkIoStocktaking);
        return rows;
    }

    //流水号
    public String getNumber() {
        String str = "PD";
        String date = FillRuleUtil.getDateNum();
        String maxNum = stkIoStocktakingMapper.selectMaxBillNo(date);
        String result = FillRuleUtil.getNumber(str,maxNum,date);
        return result;
    }

    /**
     * 修改盘点
     *
     * @param stkIoStocktaking 盘点
     * @return 结果
     */
    @Transactional
    @Override
    public int updateStkIoStocktaking(StkIoStocktaking stkIoStocktaking)
    {
        stkIoStocktaking.setUpdateTime(DateUtils.getNowDate());
        stkIoStocktakingMapper.deleteStkIoStocktakingEntryByParenId(stkIoStocktaking.getId());
        insertStkIoStocktakingEntry(stkIoStocktaking);
        return stkIoStocktakingMapper.updateStkIoStocktaking(stkIoStocktaking);
    }

    /**
     * 批量删除盘点
     *
     * @param ids 需要删除的盘点主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteStkIoStocktakingByIds(Long[] ids)
    {
        stkIoStocktakingMapper.deleteStkIoStocktakingEntryByParenIds(ids);
        return stkIoStocktakingMapper.deleteStkIoStocktakingByIds(ids);
    }

    /**
     * 删除盘点信息
     *
     * @param id 盘点主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteStkIoStocktakingById(Long id)
    {
        stkIoStocktakingMapper.deleteStkIoStocktakingEntryByParenId(id);
        return stkIoStocktakingMapper.deleteStkIoStocktakingById(id);
    }

    /**
     * 审核盘点信息
     * @param id
     * @return
     */
    @Transactional
    @Override
    public int auditStkIoBill(String id) {
        StkIoStocktaking stkIoStocktaking = stkIoStocktakingMapper.selectStkIoStocktakingById(Long.valueOf(id));
        if(stkIoStocktaking == null){
            throw new ServiceException(String.format("盘点业务ID：%s，不存在!", id));
        }

        List<StkIoStocktakingEntry> stkIoStocktakingEntryList = stkIoStocktaking.getStkIoStocktakingEntryList();

        //更新库存
        updateInventory(stkIoStocktaking,stkIoStocktakingEntryList);

        stkIoStocktaking.setAuditDate(new Date());
        stkIoStocktaking.setStockStatus(2);

        int res = stkIoStocktakingMapper.updateStkIoStocktaking(stkIoStocktaking);
        return res;
    }

    @Override
    public List<StkIoStocktaking> getMonthHandleDataList(String beginDate, String endDate) {
        return stkIoStocktakingMapper.getMonthHandleDataList(beginDate,endDate);
    }

    private void updateInventory(StkIoStocktaking stkIoStocktaking,List<StkIoStocktakingEntry> stkIoStocktakingEntryList){
        Integer stockType = stkIoStocktaking.getStockType();
        StkInventory stkInventory = null;

        for(StkIoStocktakingEntry entry : stkIoStocktakingEntryList){
            if(entry.getQty() != null && BigDecimal.ZERO.compareTo(entry.getQty()) != 0){

                if(stockType == 501){//期初
                    stkInventory = new StkInventory();
                    stkInventory.setBatchNo(entry.getBatchNo());
                    stkInventory.setMaterialId(entry.getMaterialId());
                    stkInventory.setWarehouseId(stkIoStocktaking.getWarehouseId());
                    stkInventory.setQty(entry.getQty());
                    // 优先使用 unitPrice，如果为空则使用 price
                    BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
                    stkInventory.setUnitPrice(unitPrice);
                    stkInventory.setAmt(entry.getAmt());
                    stkInventory.setMaterialDate(new Date());
                    stkInventory.setWarehouseDate(new Date());
                    stkInventory.setSupplierId(stkIoStocktaking.getSupplerId());
                    stkInventory.setCreateTime(new Date());
                    stkInventory.setCreateBy(SecurityUtils.getLoginUser().getUsername());

                    stkInventoryMapper.insertStkInventory(stkInventory);
                }else if(stockType == 502){//盘点
                    String batchNo = entry.getBatchNo();
                    BigDecimal stockQty = entry.getStockQty();//盘点数量
                    BigDecimal qty = entry.getQty();//库存数量

                    StkInventory inventory = stkInventoryMapper.selectStkInventoryOne(batchNo);

                    if(inventory == null){
                        throw new ServiceException(String.format("库存批次号：%s，不存在!", batchNo));
                    }

                    if(stockQty.compareTo(qty) == 0){
                        continue;
                    }

                    BigDecimal totalQty = BigDecimal.ZERO;
                    BigDecimal totalAmt = BigDecimal.ZERO;

                    if(stockQty.compareTo(qty) > 0){
                        BigDecimal stkQty = stockQty.subtract(qty);//最终盘点数
                        //库存数量+最终盘点数
                        totalQty = totalQty.add(inventory.getQty().add(stkQty));
                        totalAmt = totalAmt.add(inventory.getQty().add(stkQty).multiply(entry.getPrice()));
                    }else{
                        totalQty = totalQty.add(stockQty);//取盘点数
                        totalAmt = totalAmt.add(stockQty.multiply(entry.getPrice()));
                    }
                    inventory.setQty(totalQty);
                    inventory.setAmt(totalAmt);
                    inventory.setUnitPrice(entry.getPrice());
                    inventory.setWarehouseDate(new Date());

                    stkInventoryMapper.updateStkInventory(inventory);

//                    if(inventory == null){
//                        inventory = new StkInventory();
//
//                        inventory.setBatchNo(entry.getBatchNo());
//                        inventory.setMaterialId(entry.getMaterialId());
//                        inventory.setWarehouseId(stkIoStocktaking.getWarehouseId());
//                        inventory.setQty(entry.getQty());
//                        inventory.setUnitPrice(entry.getPrice());
//                        inventory.setAmt(entry.getAmt());
//                        inventory.setMaterialDate(new Date());
//                        inventory.setWarehouseDate(new Date());
//                        inventory.setSupplierId(stkIoStocktaking.getSupplerId());
//                        inventory.setCreateTime(new Date());
//                        inventory.setCreateBy(SecurityUtils.getLoginUser().getUsername());
//
//                        stkInventoryMapper.insertStkInventory(inventory);
//                    }else{
//                        BigDecimal totalQty = inventory.getQty().add(entry.getQty());
//                        inventory.setQty(totalQty);
//                        inventory.setAmt(totalQty.multiply(entry.getPrice()));
//                        inventory.setWarehouseDate(new Date());
//
//                        stkInventoryMapper.updateStkInventory(inventory);
//                    }
                }
            }
        }
    }

    /**
     * 新增盘点明细信息
     *
     * @param stkIoStocktaking 盘点对象
     */
    public void insertStkIoStocktakingEntry(StkIoStocktaking stkIoStocktaking)
    {
        List<StkIoStocktakingEntry> stkIoStocktakingEntryList = stkIoStocktaking.getStkIoStocktakingEntryList();
        Long id = stkIoStocktaking.getId();
        if (StringUtils.isNotNull(stkIoStocktakingEntryList))
        {
            List<StkIoStocktakingEntry> list = new ArrayList<StkIoStocktakingEntry>();
            for (StkIoStocktakingEntry stkIoStocktakingEntry : stkIoStocktakingEntryList)
            {
                stkIoStocktakingEntry.setParenId(id);
                if(StringUtils.isEmpty(stkIoStocktakingEntry.getBatchNo())){
                    stkIoStocktakingEntry.setBatchNo(getBatchNumber());
                }
                list.add(stkIoStocktakingEntry);
            }
            if (list.size() > 0)
            {
                stkIoStocktakingMapper.batchStkIoStocktakingEntry(list);
            }
        }
    }

    public String getBatchNumber() {
        String str = "PC";
        String createNo = FillRuleUtil.createBatchNo();
        String batchNo = str + createNo;
        return batchNo;
    }
}

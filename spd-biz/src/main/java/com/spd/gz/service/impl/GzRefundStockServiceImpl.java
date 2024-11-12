package com.spd.gz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.gz.domain.GzDepInventory;
import com.spd.gz.domain.GzDepotInventory;
import com.spd.gz.mapper.GzDepInventoryMapper;
import com.spd.gz.mapper.GzDepotInventoryMapper;
import com.spd.warehouse.domain.StkIoBillEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.spd.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.spd.gz.domain.GzRefundStockEntry;
import com.spd.gz.mapper.GzRefundStockMapper;
import com.spd.gz.domain.GzRefundStock;
import com.spd.gz.service.IGzRefundStockService;

/**
 * 高值退库Service业务层处理
 *
 * @author spd
 * @date 2024-06-11
 */
@Service
public class GzRefundStockServiceImpl implements IGzRefundStockService
{
    @Autowired
    private GzRefundStockMapper gzRefundStockMapper;

    @Autowired
    private GzDepotInventoryMapper gzDepotInventoryMapper;

    @Autowired
    private GzDepInventoryMapper gzDepInventoryMapper;

    /**
     * 查询高值退库
     *
     * @param id 高值退库主键
     * @return 高值退库
     */
    @Override
    public GzRefundStock selectGzRefundStockById(Long id)
    {
        return gzRefundStockMapper.selectGzRefundStockById(id);
    }

    /**
     * 查询高值退库列表
     *
     * @param gzRefundStock 高值退库
     * @return 高值退库
     */
    @Override
    public List<GzRefundStock> selectGzRefundStockList(GzRefundStock gzRefundStock)
    {
        return gzRefundStockMapper.selectGzRefundStockList(gzRefundStock);
    }

    /**
     * 新增高值退库
     *
     * @param gzRefundStock 高值退库
     * @return 结果
     */
    @Transactional
    @Override
    public int insertGzRefundStock(GzRefundStock gzRefundStock)
    {
        gzRefundStock.setStockNo(getNumber());
        gzRefundStock.setCreateTime(DateUtils.getNowDate());
        int rows = gzRefundStockMapper.insertGzRefundStock(gzRefundStock);
        insertGzRefundStockEntry(gzRefundStock);
        return rows;
    }

    //result：流水号
    public String getNumber() {
        String str = "GZTK";
        String date = FillRuleUtil.getDateNum();
        String maxNum = gzRefundStockMapper.selectMaxBillNo(date);
        String result = FillRuleUtil.getNumber(str,maxNum,date);
        return result;
    }

    /**
     * 修改高值退库
     *
     * @param gzRefundStock 高值退库
     * @return 结果
     */
    @Transactional
    @Override
    public int updateGzRefundStock(GzRefundStock gzRefundStock)
    {
        gzRefundStock.setUpdateTime(DateUtils.getNowDate());
        gzRefundStockMapper.deleteGzRefundStockEntryByParenId(gzRefundStock.getId());
        insertGzRefundStockEntry(gzRefundStock);
        return gzRefundStockMapper.updateGzRefundStock(gzRefundStock);
    }

    /**
     * 批量删除高值退库
     *
     * @param id 需要删除的高值退库主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteGzRefundStockById(Long id)
    {
        GzRefundStock gzRefundStock = gzRefundStockMapper.selectGzRefundStockById(id);
        if(gzRefundStock == null){
            throw new ServiceException(String.format("高值退库业务ID：%s，不存在!", id));
        }

        List<GzRefundStockEntry> gzRefundStockEntryList = gzRefundStock.getGzRefundStockEntryList();
        for(GzRefundStockEntry entry : gzRefundStockEntryList){
            entry.setDelFlag(1);
            entry.setParenId(id);
            gzRefundStockMapper.updateGzRefundStockEntry(entry);
        }

        gzRefundStock.setDelFlag(0);
        gzRefundStock.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        gzRefundStock.setUpdateTime(new Date());

        return gzRefundStockMapper.updateGzRefundStock(gzRefundStock);
    }

    @Override
    public int auditStock(String id) {
        GzRefundStock gzRefundStock = gzRefundStockMapper.selectGzRefundStockById(Long.valueOf(id));
        if(gzRefundStock == null){
            throw new ServiceException(String.format("高值退库业务ID：%s，不存在!", id));
        }

        List<GzRefundStockEntry> gzRefundStockEntryList = gzRefundStock.getGzRefundStockEntryList();

        updateGzDepotAndGzDepInventory(gzRefundStock,gzRefundStockEntryList);

        gzRefundStock.setAuditDate(new Date());
        gzRefundStock.setStockStatus(2);
        int res = gzRefundStockMapper.updateGzRefundStock(gzRefundStock);
        return res;
    }

    //更新高值库存、科室库存明细表
    private void updateGzDepotAndGzDepInventory(GzRefundStock gzRefundStock,List<GzRefundStockEntry> gzRefundStockEntryList){

        for (GzRefundStockEntry entry : gzRefundStockEntryList) {
            String batchNo = entry.getBatchNo();//批次号
            BigDecimal qty = entry.getQty();//数量

            //根据批次号查询高值科室库存详情
            GzDepInventory gzDepInventory = gzDepInventoryMapper.selectGzDepInventoryOne(batchNo);

            if(gzDepInventory == null){
                throw new ServiceException(String.format("高值退库-批次号：%s，不存在!", batchNo));
            }

            BigDecimal gzDepInventoryQty = gzDepInventory.getQty();//高值科室实际库存数量

            if(qty.compareTo(gzDepInventoryQty) > 0){
                throw new ServiceException(String.format("高值科室库存不足！退库数量：%s，实际库存：%s", qty,gzDepInventoryQty));
            }

            gzDepInventory.setQty(gzDepInventoryQty.subtract(qty));
            gzDepInventory.setUpdateTime(new Date());
            gzDepInventory.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
            gzDepInventoryMapper.updateGzDepInventory(gzDepInventory);

            GzDepotInventory gzDepotInventory = gzDepotInventoryMapper.selectGzDepotInventoryOne(batchNo);

            if(gzDepotInventory == null){
                throw new ServiceException(String.format("高值退库-批次号：%s，不存在!", batchNo));
            }

            BigDecimal inventoryQty = gzDepotInventory.getQty();

            gzDepotInventory.setQty(inventoryQty.add(qty));
            gzDepotInventory.setUpdateTime(new Date());
            gzDepotInventory.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
            gzDepotInventoryMapper.updateGzDepotInventory(gzDepotInventory);
        }
    }

    /**
     * 新增高值退货明细信息
     *
     * @param gzRefundStock 高值退库对象
     */
    public void insertGzRefundStockEntry(GzRefundStock gzRefundStock)
    {
        List<GzRefundStockEntry> gzRefundStockEntryList = gzRefundStock.getGzRefundStockEntryList();
        Long id = gzRefundStock.getId();
        if (StringUtils.isNotNull(gzRefundStockEntryList))
        {
            List<GzRefundStockEntry> list = new ArrayList<GzRefundStockEntry>();
            for (GzRefundStockEntry gzRefundStockEntry : gzRefundStockEntryList)
            {
                validateGzTKDepInventory(gzRefundStockEntry.getBatchNo(),gzRefundStockEntryList);
                gzRefundStockEntry.setParenId(id);
                list.add(gzRefundStockEntry);
            }
            if (list.size() > 0)
            {
                gzRefundStockMapper.batchGzRefundStockEntry(list);
            }
        }
    }

    private void validateGzTKDepInventory(String oldBatchNo,List<GzRefundStockEntry> gzRefundStockEntryList){
        for(GzRefundStockEntry entry : gzRefundStockEntryList){
            String batchNo = entry.getBatchNo();
            BigDecimal qty = entry.getQty();//退库数量

            if(!oldBatchNo.equals(batchNo)){
                continue;
            }

            //当前批次实际数量
            BigDecimal inventoryQty = gzDepInventoryMapper.selectTKDepInvntoryByBatchNo(batchNo);

            if(qty.compareTo(inventoryQty) > 0){
                throw new ServiceException(String.format("高值科室库存不足！退库数量：%s，实际库存：%s", qty,inventoryQty));
            }

        }
    }
}

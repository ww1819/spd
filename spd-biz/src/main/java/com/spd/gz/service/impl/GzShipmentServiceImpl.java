package com.spd.gz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.department.domain.StkDepInventory;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.gz.domain.*;
import com.spd.gz.mapper.GzDepInventoryMapper;
import com.spd.gz.mapper.GzDepotInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.spd.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.spd.gz.mapper.GzShipmentMapper;
import com.spd.gz.service.IGzShipmentService;

/**
 * 高值出库Service业务层处理
 *
 * @author spd
 * @date 2024-06-11
 */
@Service
public class GzShipmentServiceImpl implements IGzShipmentService
{
    @Autowired
    private GzShipmentMapper gzShipmentMapper;

    @Autowired
    private GzDepotInventoryMapper gzDepotInventoryMapper;

    @Autowired
    private GzDepInventoryMapper gzDepInventoryMapper;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    /**
     * 查询高值出库
     *
     * @param id 高值出库主键
     * @return 高值出库
     */
    @Override
    public GzShipment selectGzShipmentById(Long id)
    {
        GzShipment gzShipment = gzShipmentMapper.selectGzShipmentById(id);
        if(gzShipment == null){
            return  null;
        }

        List<GzShipmentEntry> gzShipmentEntryList = gzShipment.getGzShipmentEntryList();

        List<FdMaterial> materialList = new ArrayList<FdMaterial>();
        for(GzShipmentEntry entry : gzShipmentEntryList){
            Long materialId = entry.getMaterialId();
            FdMaterial fdMaterial = fdMaterialMapper.selectFdMaterialById(materialId);
            materialList.add(fdMaterial);
        }
        gzShipment.setMaterialList(materialList);
        return gzShipment;
    }

    /**
     * 查询高值出库列表
     *
     * @param gzShipment 高值出库
     * @return 高值出库
     */
    @Override
    public List<GzShipment> selectGzShipmentList(GzShipment gzShipment)
    {
        return gzShipmentMapper.selectGzShipmentList(gzShipment);
    }

    /**
     * 新增高值出库
     *
     * @param gzShipment 高值出库
     * @return 结果
     */
    @Transactional
    @Override
    public int insertGzShipment(GzShipment gzShipment)
    {
        gzShipment.setShipmentNo(getNumber());
        gzShipment.setCreateTime(DateUtils.getNowDate());
        int rows = gzShipmentMapper.insertGzShipment(gzShipment);
        insertGzShipmentEntry(gzShipment);
        return rows;
    }

    //result：流水号
    public String getNumber() {
        String str = "GZRK";
        String date = FillRuleUtil.getDateNum();
        String maxNum = gzShipmentMapper.selectMaxBillNo(date);
        String result = FillRuleUtil.getNumber(str,maxNum,date);
        return result;
    }

    /**
     * 修改高值出库
     *
     * @param gzShipment 高值出库
     * @return 结果
     */
    @Transactional
    @Override
    public int updateGzShipment(GzShipment gzShipment)
    {
        gzShipment.setUpdateTime(DateUtils.getNowDate());
        gzShipmentMapper.deleteGzShipmentEntryByParenId(gzShipment.getId());
        insertGzShipmentEntry(gzShipment);
        return gzShipmentMapper.updateGzShipment(gzShipment);
    }

    /**
     * 删除高值出库信息
     *
     * @param id 高值出库主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteGzShipmentById(Long id)
    {
        GzShipment gzShipment = gzShipmentMapper.selectGzShipmentById(id);
        if(gzShipment == null){
            throw new ServiceException(String.format("高值出库业务ID：%s，不存在!", id));
        }

        gzShipment.setDelFlag(1);
        gzShipment.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        gzShipment.setUpdateTime(new Date());

        List<GzShipmentEntry> gzShipmentEntryList = gzShipment.getGzShipmentEntryList();
        for(GzShipmentEntry entry : gzShipmentEntryList){
            entry.setDelFlag(1);
            entry.setParenId(id);
            gzShipmentMapper.updateGzShipmentEntry(entry);
        }
        return gzShipmentMapper.updateGzShipment(gzShipment);
    }

    /**
     * 审核高值出库信息
     * @param id
     * @return
     */
    @Override
    @Transactional
    public int auditShipment(String id) {
        GzShipment gzShipment = gzShipmentMapper.selectGzShipmentById(Long.parseLong(id));
        if(gzShipment == null){
            throw new ServiceException(String.format("高值出库业务ID：%s，不存在!", id));
        }

        List<GzShipmentEntry> gzShipmentEntryList = gzShipment.getGzShipmentEntryList();

        //更新高值库存明细表
        updateGzDepotInventory(gzShipment,gzShipmentEntryList);

        gzShipment.setAuditDate(new Date());
        gzShipment.setShipmentStatus(2);
        int res = gzShipmentMapper.updateGzShipment(gzShipment);
        return res;
    }

    private void updateGzDepotInventory(GzShipment gzShipment,List<GzShipmentEntry> gzShipmentEntryList){

        for(GzShipmentEntry entry : gzShipmentEntryList){
            String batchNo = entry.getBatchNo();
            BigDecimal qty = entry.getQty();

            GzDepotInventory gzDepotInventory = gzDepotInventoryMapper.selectGzDepotInventoryOne(batchNo);

            if(gzDepotInventory == null){
                throw new ServiceException(String.format("高值出库-批次号：%s，不存在!", batchNo));
            }

            validateGzDepotInventory(batchNo,gzShipmentEntryList);

            BigDecimal inventoryQty = gzDepotInventory.getQty();

            //高值出库数量不能大于库存数量
            if(qty.compareTo(inventoryQty) > 0){
                throw new ServiceException(String.format("高值实际库存不足！高值出库数量：%s，高值实际库存：%s", qty,inventoryQty));
            }

            gzDepotInventory.setQty(inventoryQty.subtract(qty));
            gzDepotInventory.setUpdateTime(new Date());
            gzDepotInventory.setUpdateBy(SecurityUtils.getLoginUser().getUsername());

            //更新库存明细表
            gzDepotInventoryMapper.updateGzDepotInventory(gzDepotInventory);

            //更新高值科室库存明细表
            updateGZDepInventory(gzDepotInventory,gzShipment,entry);
        }


    }

    //更新高值科室库存明细表
    private void updateGZDepInventory(GzDepotInventory depotInventory ,GzShipment gzShipment,GzShipmentEntry entry){
        String batchNo = entry.getBatchNo();
        //根据批次号查询高值科室库存详情
        GzDepInventory gzDepInventory = gzDepInventoryMapper.selectGzDepInventoryOne(batchNo);

        if(gzDepInventory == null){
            //更新科室库存明细表
            gzDepInventory = new GzDepInventory();

            gzDepInventory.setMaterialId(entry.getMaterialId());
            gzDepInventory.setMaterialNo(entry.getBatchNumber());
            gzDepInventory.setDepartmentId(gzShipment.getDepartmentId());
            gzDepInventory.setQty(entry.getQty());
            gzDepInventory.setUnitPrice(entry.getPrice());
            gzDepInventory.setAmt(entry.getAmt());
            gzDepInventory.setBatchNo(entry.getBatchNo());
            gzDepInventory.setMaterialNo(depotInventory.getMaterialNo());
            gzDepInventory.setMaterialDate(depotInventory.getMaterialDate());
            gzDepInventory.setWarehouseDate(depotInventory.getWarehouseDate());
            gzDepInventoryMapper.insertGzDepInventory(gzDepInventory);
        }else{
            BigDecimal oldQty = gzDepInventory.getQty();
            BigDecimal qty = entry.getQty();

            gzDepInventory.setQty(oldQty.add(qty));//数量
            gzDepInventoryMapper.updateGzDepInventory(gzDepInventory);
        }
    }

    /**
     * 新增高值出库明细信息
     *
     * @param gzShipment 高值出库对象
     */
    public void insertGzShipmentEntry(GzShipment gzShipment)
    {
        List<GzShipmentEntry> gzShipmentEntryList = gzShipment.getGzShipmentEntryList();
        Long id = gzShipment.getId();
        if (StringUtils.isNotNull(gzShipmentEntryList))
        {
            List<GzShipmentEntry> list = new ArrayList<GzShipmentEntry>();
            for (GzShipmentEntry gzShipmentEntry : gzShipmentEntryList)
            {
                validateGzDepotInventory(gzShipmentEntry.getBatchNo(),gzShipmentEntryList);
                gzShipmentEntry.setParenId(id);
                gzShipmentEntry.setDelFlag(0);
                list.add(gzShipmentEntry);
            }
            if (list.size() > 0)
            {
                gzShipmentMapper.batchGzShipmentEntry(list);
            }
        }
    }

    /**
     * 校验高值库存
     */
    private void validateGzDepotInventory(String oldBatchNo,List<GzShipmentEntry> gzShipmentEntryList){
        for(GzShipmentEntry entry : gzShipmentEntryList){
            String batchNo = entry.getBatchNo();//批次号
            BigDecimal qty = entry.getQty();//出库数量

            if(!oldBatchNo.equals(batchNo)){
                continue;
            }

            //当前批次实际数量
            BigDecimal inventoryQty = gzDepotInventoryMapper.selectGzDepotInventoryByBatchNo(batchNo);

            if(qty.compareTo(inventoryQty) > 0){
                throw new ServiceException(String.format("高值实际库存不足！出库数量：%s，实际库存：%s", qty,inventoryQty));
            }
        }
    }
}

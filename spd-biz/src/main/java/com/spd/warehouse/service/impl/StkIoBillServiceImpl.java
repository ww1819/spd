package com.spd.warehouse.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.department.domain.StkDepInventory;
import com.spd.department.mapper.StkDepInventoryMapper;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.warehouse.domain.StkInventory;
import com.spd.warehouse.mapper.StkInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Map;

import com.spd.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.spd.warehouse.domain.StkIoBillEntry;
import com.spd.warehouse.mapper.StkIoBillMapper;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.service.IStkIoBillService;

/**
 * 出入库Service业务层处理
 *
 * @author spd
 * @date 2023-12-17
 */
@Service
public class StkIoBillServiceImpl implements IStkIoBillService
{
    @Autowired
    private StkIoBillMapper stkIoBillMapper;

    @Autowired
    private StkInventoryMapper stkInventoryMapper;

    @Autowired
    private StkDepInventoryMapper stkDepInventoryMapper;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    /**
     * 查询出入库
     *
     * @param id 出入库主键
     * @return 出入库
     */
    @Override
    public StkIoBill selectStkIoBillById(Long id)
    {
        StkIoBill stkIoBill = stkIoBillMapper.selectStkIoBillById(id);
        if (stkIoBill == null) {
            return null;
        }
        List<StkIoBillEntry> stkIoBillEntryList = stkIoBill.getStkIoBillEntryList();
        List<FdMaterial> materialList = new ArrayList<FdMaterial>();
        for(StkIoBillEntry entry : stkIoBillEntryList){
            Long materialId = entry.getMaterialId();
            FdMaterial fdMaterial = fdMaterialMapper.selectFdMaterialById(materialId);
            materialList.add(fdMaterial);
        }
        stkIoBill.setMaterialList(materialList);
        return stkIoBill;
    }

    /**
     * 查询出入库列表
     *
     * @param stkIoBill 出入库
     * @return 出入库
     */
    @Override
    public List<StkIoBill> selectStkIoBillList(StkIoBill stkIoBill)
    {
        return stkIoBillMapper.selectStkIoBillList(stkIoBill);
    }

    /**
     * 新增入库
     *
     * @param stkIoBill 入库
     * @return 结果
     */
    @Transactional
    @Override
    public int insertStkIoBill(StkIoBill stkIoBill)
    {

        stkIoBill.setBillNo(getNumber());
        stkIoBill.setCreateTime(DateUtils.getNowDate());
        int rows = stkIoBillMapper.insertStkIoBill(stkIoBill);
        insertStkIoBillEntry(stkIoBill);
        return rows;
    }

    //str：单号前缀
    //date：日期
    //result：最终结果，需要的流水号
    public String getNumber() {
        String str = "RK";
        String date = FillRuleUtil.getDateNum();
        String maxNum = stkIoBillMapper.selectMaxBillNo(date);
        String result = FillRuleUtil.getNumber(str,maxNum,date);
        return result;
    }


    /**
     * 修改出入库
     *
     * @param stkIoBill 出入库
     * @return 结果
     */
    @Transactional
    @Override
    public int updateStkIoBill(StkIoBill stkIoBill)
    {
        stkIoBill.setUpdateTime(DateUtils.getNowDate());
        stkIoBillMapper.deleteStkIoBillEntryByParenId(stkIoBill.getId());
        updateStkIoBillEntry(stkIoBill);
        return stkIoBillMapper.updateStkIoBill(stkIoBill);
    }

//    /**
//     * 批量删除出入库
//     *
//     * @param ids 需要删除的出入库主键
//     * @return 结果
//     */
//    @Transactional
//    @Override
//    public int deleteStkIoBillByIds(Long[] ids)
//    {
//        stkIoBillMapper.deleteStkIoBillEntryByParenIds(ids);
//        return stkIoBillMapper.deleteStkIoBillByIds(ids);
//    }

    /**
     * 删除出入库信息
     *
     * @param id 出入库主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteStkIoBillById(Long id)
    {
        StkIoBill stkIoBill = stkIoBillMapper.selectStkIoBillById(id);
        if(stkIoBill == null){
            throw new ServiceException(String.format("业务：%s，不存在!", id));
        }

        stkIoBill.setDelFlag(1);
        stkIoBill.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
        stkIoBill.setUpdateTime(new Date());

        List<StkIoBillEntry> stkIoBillEntryList = stkIoBill.getStkIoBillEntryList();
        for(StkIoBillEntry entry : stkIoBillEntryList){
            entry.setDelFlag(1);
            entry.setParenId(id);
            stkIoBillMapper.updatestkIobillEntry(entry);
        }

        return stkIoBillMapper.updateStkIoBill(stkIoBill);
    }

    /**
     * 审核入库信息
     * @param id
     * @return
     */
    @Override
    @Transactional
    public int auditStkIoBill(String id) {
        StkIoBill stkIoBill = stkIoBillMapper.selectStkIoBillById(Long.parseLong(id));
        if(stkIoBill == null){
            throw new ServiceException(String.format("入库业务ID：%s，不存在!", id));
        }
        List<StkIoBillEntry> stkIoBillEntryList = stkIoBill.getStkIoBillEntryList();

        //更新库存
        updateInventory(stkIoBill,stkIoBillEntryList);

        stkIoBill.setBillStatus(2);//已审核状态
        stkIoBill.setAuditDate(new Date());
        int res = stkIoBillMapper.updateStkIoBill(stkIoBill);
        return res;
    }


    private void updateInventory(StkIoBill stkIoBill,List<StkIoBillEntry> stkIoBillEntryList){

        Integer billType = stkIoBill.getBillType();
        Long supplerId = stkIoBill.getSupplerId();
        StkInventory stkInventory = null;
        for(StkIoBillEntry entry : stkIoBillEntryList){

            if(entry.getQty() != null && BigDecimal.ZERO.compareTo(entry.getQty()) != 0){
                if(billType == 101){//入库
                    stkInventory = new StkInventory();
                    stkInventory.setBatchNo(entry.getBatchNo());
                    stkInventory.setMaterialNo(entry.getBatchNumber());
                    stkInventory.setMaterialId(entry.getMaterialId());
                    stkInventory.setWarehouseId(stkIoBill.getWarehouseId());
                    stkInventory.setQty(entry.getQty());
                    stkInventory.setUnitPrice(entry.getPrice());
                    stkInventory.setAmt(entry.getAmt());
                    stkInventory.setMaterialDate(new Date());
                    stkInventory.setWarehouseDate(new Date());
                    stkInventory.setSupplierId(supplerId);
                    stkInventory.setBeginTime(entry.getBeginTime());
                    stkInventory.setEndTime(entry.getAndTime());
                    stkInventory.setReceiptOrderNo(stkIoBill.getBillNo());
                    stkInventory.setCreateTime(new Date());
                    stkInventory.setCreateBy(SecurityUtils.getLoginUser().getUsername());

                    stkInventoryMapper.insertStkInventory(stkInventory);
                }else if(billType == 201){//出库
                    String batchNo = entry.getBatchNo();
                    BigDecimal qty = entry.getQty();
                    StkInventory inventory = stkInventoryMapper.selectStkInventoryOne(batchNo);

                    if(inventory == null){
                        throw new ServiceException(String.format("出库-批次号：%s，不存在!", batchNo));
                    }

                    validateInventory(batchNo,stkIoBillEntryList);

                    BigDecimal inventoryQty = inventory.getQty();//库存数量
                    BigDecimal unitPrice = inventory.getUnitPrice();//单价

                    //出库数量不能大于库存数量
                    if(qty.compareTo(inventoryQty) > 0){
                        throw new ServiceException(String.format("实际库存不足！出库数量：%s，实际库存：%s", qty,inventoryQty));
                    }
                    BigDecimal subQty = inventoryQty.subtract(qty);
                    inventory.setQty(subQty);
                    inventory.setAmt(subQty.multiply(unitPrice));
                    inventory.setUpdateTime(new Date());
                    inventory.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
                    //更新库存明细表
                    stkInventoryMapper.updateStkInventory(inventory);

                    //按批次号查询，不存在新增，则更新
                    updateDepInventory(inventory,stkIoBill,entry);
                }else if(billType == 301){//退货
                    String batchNo = entry.getBatchNo();
                    BigDecimal qty = entry.getQty();
                    StkInventory inventory = stkInventoryMapper.selectStkInventoryOne(batchNo);
                    BigDecimal inventoryQty = inventory.getQty();//实际库存数量
                    BigDecimal unitPrice = inventory.getUnitPrice();

                    if(inventory == null){
                        throw new ServiceException(String.format("退货-批次号：%s，不存在!", batchNo));
                    }

                    //退货数量不能大于库存数量
                    if(qty.compareTo(inventoryQty) > 0){
                        throw new ServiceException(String.format("实际库存不足！退货数量：%s，实际库存：%s", qty,inventoryQty));
                    }else{
                        BigDecimal subQty = inventoryQty.subtract(qty);
                        inventory.setQty(subQty);
                        inventory.setAmt(subQty.multiply(unitPrice));
                        inventory.setUpdateTime(new Date());
                        inventory.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
                        //更新库存明细表
                        stkInventoryMapper.updateStkInventory(inventory);
                    }

                }else if(billType == 401){//退库
                    String batchNo = entry.getBatchNo();//退库批次号
                    BigDecimal qty = entry.getQty();//退库数量

                    //更新科室库存数量
                    StkDepInventory stkDepInventory = stkDepInventoryMapper.selectStkDepInventoryOne(batchNo);

                    if(stkDepInventory == null){
                        throw new ServiceException(String.format("退库-批次号：%s，不存在!", batchNo));
                    }

                    BigDecimal stkDepInventoryQty = stkDepInventory.getQty();//科室库存实际数量
                    if(qty.compareTo(stkDepInventoryQty) > 0){
                        throw new ServiceException(String.format("科室库存不足！退库数量：%s，实际库存：%s", qty,stkDepInventoryQty));
                    }
                    stkDepInventory.setQty(stkDepInventoryQty.subtract(qty));
                    stkDepInventory.setUpdateTime(new Date());
                    stkDepInventory.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
                    stkDepInventoryMapper.updateStkDepInventory(stkDepInventory);

                    //更新库存数量
                    StkInventory inventory = stkInventoryMapper.selectStkInventoryOne(batchNo);

                    if(inventory == null){
                        throw new ServiceException(String.format("退库-批次号：%s，不存在!", batchNo));
                    }
                    BigDecimal inventoryQty = inventory.getQty();
                    inventoryQty = inventoryQty.add(qty);

                    inventory.setQty(inventoryQty);
                    inventory.setAmt(inventoryQty.multiply(inventory.getUnitPrice()));
                    inventory.setUpdateTime(new Date());
                    inventory.setUpdateBy(SecurityUtils.getLoginUser().getUsername());
                    stkInventoryMapper.updateStkInventory(inventory);
                }
            }
        }
    }

    /**
     * 更新科室库存
     * @param inventory 库存明细
     * @param stkIoBill 出入库库存表
     * @param entry 出入库库存明细表
     */
    private void updateDepInventory(StkInventory inventory,StkIoBill stkIoBill,StkIoBillEntry entry){

        String batchNo = entry.getBatchNo();
        StkDepInventory stkDepInventory = stkDepInventoryMapper.selectStkDepInventoryOne(batchNo);

        if(stkDepInventory == null){
            //更新科室库存明细表
            stkDepInventory = new StkDepInventory();
            stkDepInventory.setMaterialId(entry.getMaterialId());
            stkDepInventory.setMaterialNo(entry.getBatchNumber());
            stkDepInventory.setDepartmentId(stkIoBill.getDepartmentId());
            stkDepInventory.setQty(entry.getQty());
            stkDepInventory.setUnitPrice(entry.getUnitPrice());
            stkDepInventory.setAmt(entry.getAmt());
            stkDepInventory.setBatchNo(entry.getBatchNo());
            stkDepInventory.setMaterialNo(inventory.getMaterialNo());
            stkDepInventory.setMaterialDate(inventory.getMaterialDate());
            stkDepInventory.setWarehouseDate(inventory.getWarehouseDate());
            stkDepInventoryMapper.insertStkDepInventory(stkDepInventory);
        }else{
            BigDecimal oldQty = stkDepInventory.getQty();
            BigDecimal qty = entry.getQty();

            stkDepInventory.setQty(oldQty.add(qty));//数量
            stkDepInventoryMapper.updateStkDepInventory(stkDepInventory);
        }
    }

    public String getBatchNumber() {
        String str = "PC";
        String createNo = FillRuleUtil.createBatchNo();
        String batchNo = str + createNo;
        return batchNo;
    }

    /**
     * 新增出入库明细信息
     *
     * @param stkIoBill 出入库对象
     */
    public void insertStkIoBillEntry(StkIoBill stkIoBill)
    {
        List<StkIoBillEntry> stkIoBillEntryList = stkIoBill.getStkIoBillEntryList();
        Long id = stkIoBill.getId();
        if (StringUtils.isNotNull(stkIoBillEntryList))
        {
            List<StkIoBillEntry> list = new ArrayList<StkIoBillEntry>();
            for (StkIoBillEntry stkIoBillEntry : stkIoBillEntryList)
            {
                stkIoBillEntry.setParenId(id);
                stkIoBillEntry.setBatchNo(getBatchNumber());
                stkIoBillEntry.setDelFlag(0);
                list.add(stkIoBillEntry);
            }
            if (list.size() > 0)
            {
                stkIoBillMapper.batchStkIoBillEntry(list);
            }
        }
    }

    /**
     * 更新出入库明细信息
     *
     * @param stkIoBill 出入库对象
     */
    public void updateStkIoBillEntry(StkIoBill stkIoBill)
    {
        List<StkIoBillEntry> stkIoBillEntryList = stkIoBill.getStkIoBillEntryList();
        Long id = stkIoBill.getId();
        if (StringUtils.isNotNull(stkIoBillEntryList))
        {
            List<StkIoBillEntry> list = new ArrayList<StkIoBillEntry>();
            for (StkIoBillEntry stkIoBillEntry : stkIoBillEntryList)
            {
                stkIoBillEntry.setParenId(id);
                if(StringUtils.isEmpty(stkIoBillEntry.getBatchNo())){
                    stkIoBillEntry.setBatchNo(getBatchNumber());
                }
                list.add(stkIoBillEntry);
            }
            if (list.size() > 0)
            {
                stkIoBillMapper.batchStkIoBillEntry(list);
            }
        }
    }

    /**
     * 新增出库
     *
     * @param stkIoBill 出库
     * @return 结果
     */
    @Transactional
    @Override
    public int insertOutStkIoBill(StkIoBill stkIoBill)
    {
        stkIoBill.setBillNo(getBillNumber("CK"));
        stkIoBill.setCreateTime(DateUtils.getNowDate());
        int rows = stkIoBillMapper.insertStkIoBill(stkIoBill);
        insertOutStkIoBillEntry(stkIoBill);
        return rows;
    }

    //str：单号前缀
    //date：日期
    //result：最终结果，需要的流水号
    public String getBillNumber(String str) {
        String date = FillRuleUtil.getDateNum();
        String maxNum = stkIoBillMapper.selectOutMaxBillNo(date);
        String result = FillRuleUtil.getNumber(str,maxNum,date);
        return result;
    }

    @Transactional
    @Override
    public int updateOutStkIoBill(StkIoBill stkIoBill) {
        stkIoBill.setUpdateTime(DateUtils.getNowDate());
        stkIoBillMapper.deleteStkIoBillEntryByParenId(stkIoBill.getId());
        insertOutStkIoBillEntry(stkIoBill);
        return stkIoBillMapper.updateStkIoBill(stkIoBill);
    }

    @Transactional
    @Override
    public int insertTkStkIoBill(StkIoBill stkIoBill) {
        stkIoBill.setBillNo(getTKNumber("TK"));
        stkIoBill.setCreateTime(DateUtils.getNowDate());
        int rows = stkIoBillMapper.insertStkIoBill(stkIoBill);
        insertTKStkIoBillEntry(stkIoBill);
        return rows;
    }

    public String getTKNumber(String str) {
        String date = FillRuleUtil.getDateNum();
        String maxNum = stkIoBillMapper.selectTKMaxBillNo(date);
        String result = FillRuleUtil.getNumber(str,maxNum,date);
        return result;
    }

    public String getTHNumber(String str) {
        String date = FillRuleUtil.getDateNum();
        String maxNum = stkIoBillMapper.selectTHMaxBillNo(date);
        String result = FillRuleUtil.getNumber(str,maxNum,date);
        return result;
    }

    @Transactional
    @Override
    public int insertTHStkIoBill(StkIoBill stkIoBill) {
        stkIoBill.setBillNo(getTHNumber("TH"));
        stkIoBill.setCreateTime(DateUtils.getNowDate());
        int rows = stkIoBillMapper.insertStkIoBill(stkIoBill);
        insertOutStkIoBillEntry(stkIoBill);
        return rows;
    }

    @Transactional
    @Override
    public int updateTKStkIoBill(StkIoBill stkIoBill) {
        stkIoBill.setUpdateTime(DateUtils.getNowDate());
        stkIoBillMapper.deleteStkIoBillEntryByParenId(stkIoBill.getId());
        insertTKStkIoBillEntry(stkIoBill);
        return stkIoBillMapper.updateStkIoBill(stkIoBill);
    }

    @Override
    public List<Map<String, Object>> selectRTHStkIoBillList(StkIoBill stkIoBill) {
        return stkIoBillMapper.selectRTHStkIoBillList(stkIoBill);
    }

    @Override
    public List<Map<String, Object>> selectCTKStkIoBillList(StkIoBill stkIoBill) {
        return stkIoBillMapper.selectCTKStkIoBillList(stkIoBill);
    }
    @Override
    public List<Map<String, Object>> selectRTHStkIoBillSummaryList(StkIoBill stkIoBill) {
        return stkIoBillMapper.selectRTHStkIoBillSummaryList(stkIoBill);
    }

    @Override
    public List<Map<String, Object>> selectCTKStkIoBillListSummary(StkIoBill stkIoBill) {
        return stkIoBillMapper.selectCTKStkIoBillListSummary(stkIoBill);
    }

    @Override
    public List<Map<String, Object>> selectHistoryInventory(String previousDateString) {
        return stkIoBillMapper.selectHistoryInventory(previousDateString);
    }

    /**
     * 查询进销存明细列表
     * @param stkIoBill
     * @return
     */
    @Override
    public List<Map<String, Object>> selectListPurInventory(StkIoBill stkIoBill) {
        return stkIoBillMapper.selectListPurInventory(stkIoBill);
    }

    @Override
    public List<Map<String, Object>> selectMonthInitDataList(String beginDate,String endDate,String toStatDate,String toEndDate) {
        return stkIoBillMapper.selectMonthInitDataList(beginDate,endDate,toStatDate,toEndDate);
    }

    @Override
    public List<StkIoBill> getMonthHandleDataList(String beginDate, String endDate) {
        return stkIoBillMapper.getMonthHandleDataList(beginDate,endDate);
    }

    /**
     * 新增退库明细信息
     *
     * @param stkIoBill 出库对象
     */
    public void insertTKStkIoBillEntry(StkIoBill stkIoBill)
    {
        List<StkIoBillEntry> stkIoBillEntryList = stkIoBill.getStkIoBillEntryList();
        Long id = stkIoBill.getId();
        if (StringUtils.isNotNull(stkIoBillEntryList))
        {
            List<StkIoBillEntry> list = new ArrayList<StkIoBillEntry>();
            for (StkIoBillEntry stkIoBillEntry : stkIoBillEntryList)
            {
                validateTKInventory(stkIoBillEntry.getBatchNo(),stkIoBillEntryList);
                stkIoBillEntry.setParenId(id);
                stkIoBillEntry.setDelFlag(0);
                list.add(stkIoBillEntry);
            }
            if (list.size() > 0)
            {
                stkIoBillMapper.batchStkIoBillEntry(list);
            }
        }
    }

    /**
     * 校验科室商品库存
     * @param stkIoBillEntryList
     */
    private void validateTKInventory(String oldBatchNo,List<StkIoBillEntry> stkIoBillEntryList){
        for(StkIoBillEntry entry : stkIoBillEntryList){
            String batchNo = entry.getBatchNo();
            BigDecimal qty = entry.getQty();//退库数量

            if(!oldBatchNo.equals(batchNo)){
                continue;
            }

            //当前批次实际数量
            BigDecimal inventoryQty = stkDepInventoryMapper.selectTKStkInvntoryByBatchNo(batchNo);

            if(qty.compareTo(inventoryQty) > 0){
                throw new ServiceException(String.format("科室库存不足！退库数量：%s，实际库存：%s", qty,inventoryQty));
            }

        }
    }

    /**
     * 新增出库明细信息
     *
     * @param stkIoBill 出库对象
     */
    public void insertOutStkIoBillEntry(StkIoBill stkIoBill)
    {
        List<StkIoBillEntry> stkIoBillEntryList = stkIoBill.getStkIoBillEntryList();
        Long id = stkIoBill.getId();
        if (StringUtils.isNotNull(stkIoBillEntryList))
        {
            List<StkIoBillEntry> list = new ArrayList<StkIoBillEntry>();
            for (StkIoBillEntry stkIoBillEntry : stkIoBillEntryList)
            {
                validateInventory(stkIoBillEntry.getBatchNo(),stkIoBillEntryList);
                stkIoBillEntry.setParenId(id);
                stkIoBillEntry.setDelFlag(0);
                list.add(stkIoBillEntry);
            }
            if (list.size() > 0)
            {
                stkIoBillMapper.batchStkIoBillEntry(list);
            }
        }
    }

    /**
     * 校验商品库存
     * @param stkIoBillEntryList
     */
    private void validateInventory(String oldBatchNo,List<StkIoBillEntry> stkIoBillEntryList){
        for(StkIoBillEntry stkIoBillEntry : stkIoBillEntryList){
            String batchNo = stkIoBillEntry.getBatchNo();
            BigDecimal qty = stkIoBillEntry.getQty();//出库数量

            if(!oldBatchNo.equals(batchNo)){
                continue;
            }

            //当前批次实际数量
            BigDecimal inventoryQty = stkInventoryMapper.selectStkInvntoryByBatchNo(batchNo);

            if(qty.compareTo(inventoryQty) > 0){
                throw new ServiceException(String.format("实际库存不足！出库数量：%s，实际库存：%s", qty,inventoryQty));
            }

        }
    }
}

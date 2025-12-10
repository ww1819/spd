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
import com.spd.gz.mapper.SysSheetIdMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.spd.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.spd.gz.mapper.GzOrderMapper;
import com.spd.gz.domain.GzOrder;
import com.spd.gz.service.IGzOrderService;
import java.text.SimpleDateFormat;

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

    @Autowired
    private SysSheetIdMapper sysSheetIdMapper;

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
        gzOrder.setOrderNo(getOrderNo(gzOrder.getOrderType()));
        gzOrder.setCreateTime(DateUtils.getNowDate());
        int rows = gzOrderMapper.insertGzOrder(gzOrder);
        insertGzOrderEntry(gzOrder);
        return rows;
    }

    //str：单号前缀
    //date：日期
    //result：最终结果，需要的流水号
    //orderType: 101=入库(GZRK), 102=出库(GZCK), 301=退库(GZTK), 401=跟台(GT)
    public String getOrderNo(Integer orderType) {
        // 根据orderType决定单号前缀：101=入库(GZRK), 102=出库(GZCK), 301=退库(GZTK), 401=跟台(GT)
        String str;
        if (orderType != null && orderType == 102) {
            str = "GZCK";  // 出库
        } else if (orderType != null && orderType == 301) {
            str = "GZTK";  // 退库
        } else if (orderType != null && orderType == 401) {
            str = "GT";    // 跟台
        } else {
            str = "GZRK";  // 入库（默认）
        }
        String date = FillRuleUtil.getDateNum();
        String maxNum = gzOrderMapper.selectMaxBillNo(str, date);
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
        // 获取或初始化序列号
        Long sheetId = getOrInitSheetId();
        
        for(GzOrderEntry orderEntry : gzOrderEntryList){
            if(orderEntry.getQty() != null && BigDecimal.ZERO.compareTo(orderEntry.getQty()) != 0){
                // 根据数量循环生成多条库存记录，每条数量为1，并生成一个院内码
                int qty = orderEntry.getQty().intValue();
                for(int i = 0; i < qty; i++){
                    sheetId = sheetId + 1;
                    String inHospitalCode = generateInHospitalCode(sheetId);
                    
                    GzDepotInventory gzDepotInventory = new GzDepotInventory();
                    gzDepotInventory.setMaterialNo(orderEntry.getBatchNumber());
                    gzDepotInventory.setBatchNo(orderEntry.getBatchNo());
                    gzDepotInventory.setMaterialId(orderEntry.getMaterialId());
                    gzDepotInventory.setWarehouseId(gzOrder.getWarehouseId());
                    gzDepotInventory.setQty(BigDecimal.ONE); // 每条记录数量为1
                    gzDepotInventory.setUnitPrice(orderEntry.getPrice());
                    gzDepotInventory.setAmt(orderEntry.getPrice()); // 金额 = 单价 * 1
                    gzDepotInventory.setMaterialDate(orderEntry.getBeginTime() != null ? orderEntry.getBeginTime() : new Date());
                    gzDepotInventory.setWarehouseDate(new Date());
                    gzDepotInventory.setSupplierId(gzOrder.getSupplerId());
                    gzDepotInventory.setInHospitalCode(inHospitalCode);
                    gzDepotInventory.setEndTime(orderEntry.getEndTime()); // 保存有效期

                    gzDepotInventoryMapper.insertGzDepotInventory(gzDepotInventory);
                }
            }
        }
        
        // 更新序列号
        updateSheetId(sheetId);
    }

    /**
     * 获取或初始化序列号
     */
    private Long getOrInitSheetId(){
        String businessType = "高值";
        String sheetType = "gzynm";
        
        int count = sysSheetIdMapper.countSheetId(businessType, sheetType);
        if(count == 0){
            // 如果不存在，插入初始记录
            sysSheetIdMapper.insertSheetId(businessType, sheetType, 0L);
            return 0L;
        } else {
            Long sheetId = sysSheetIdMapper.selectSheetId(businessType, sheetType);
            return sheetId != null ? sheetId : 0L;
        }
    }

    /**
     * 更新序列号
     */
    private void updateSheetId(Long sheetId){
        String businessType = "高值";
        String sheetType = "gzynm";
        sysSheetIdMapper.updateSheetId(businessType, sheetType, sheetId);
    }

    /**
     * 生成院内码
     * 格式：G + 日期时间（10位） + 序列号（6位）
     * 参考存储过程：CONCAT('G', RIGHT(DATE_FORMAT(NOW(), '%Y%m%d%H%i'), 10), '', right(CAST(sheetId + 1000000 AS CHAR),6))
     * 例如：G20251208130 + 1000001 = G202512081301000001
     */
    private String generateInHospitalCode(Long sheetId){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        String dateStr = sdf.format(new Date());
        // 取后10位（存储过程使用RIGHT函数取后10位）
        if(dateStr.length() > 10){
            dateStr = dateStr.substring(dateStr.length() - 10);
        }
        // 序列号格式：sheetId + 1000000，然后取后6位（存储过程使用right函数取后6位）
        Long seqNum = sheetId + 1000000;
        String seqStr = String.valueOf(seqNum);
        if(seqStr.length() > 6){
            seqStr = seqStr.substring(seqStr.length() - 6);
        } else {
            // 如果不足6位，左补0
            seqStr = String.format("%06d", seqNum);
        }
        return "G" + dateStr + seqStr;
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

    @Override
    public List<String> selectOutboundOrderNosByInHospitalCode(String inHospitalCode) {
        return gzOrderMapper.selectOutboundOrderNosByInHospitalCode(inHospitalCode);
    }
}

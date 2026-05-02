package com.spd.gz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;
import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.gz.domain.GzBillEntryChangeLog;
import com.spd.gz.domain.GzDepInventory;
import com.spd.gz.domain.GzDepotInventory;
import com.spd.gz.mapper.GzDepInventoryMapper;
import com.spd.gz.mapper.GzBillEntryChangeLogMapper;
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
import com.spd.hc.service.IHcBarcodeLifecycleService;
import com.spd.gz.service.GzStockValidationService;
import com.spd.gz.service.GzLineRefWriteService;
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
    private static final Logger log = LoggerFactory.getLogger(GzRefundGoodsServiceImpl.class);
    @Autowired
    private GzRefundGoodsMapper gzRefundGoodsMapper;

    @Autowired
    private GzDepotInventoryMapper gzDepotInventoryMapper;

    @Autowired
    private GzDepInventoryMapper gzDepInventoryMapper;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    @Autowired
    private GzStockValidationService gzStockValidationService;

    @Autowired
    private GzLineRefWriteService gzLineRefWriteService;

    @Autowired
    private GzBillEntryChangeLogMapper gzBillEntryChangeLogMapper;

    @Autowired
    private IHcBarcodeLifecycleService hcBarcodeLifecycleService;

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

    @Override
    public GzRefundGoods selectGzRefundStockById(Long id)
    {
        return gzRefundGoodsMapper.selectGzRefundStockById(id);
    }

    @Override
    public List<GzRefundGoods> selectGzRefundStockList(GzRefundGoods gzRefundGoods)
    {
        if (gzRefundGoods != null && StringUtils.isEmpty(gzRefundGoods.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            gzRefundGoods.setTenantId(SecurityUtils.getCustomerId());
        }
        return gzRefundGoodsMapper.selectGzRefundStockList(gzRefundGoods);
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
        if (StringUtils.isEmpty(gzRefundGoods.getGoodsNo())) {
            gzRefundGoods.setGoodsNo(nextGoodsNoForGoods());
        }
        gzRefundGoods.setCreateTime(DateUtils.getNowDate());
        gzStockValidationService.assertRefundTh(gzRefundGoods, gzRefundGoods.getGzRefundGoodsEntryList());
        int rows = gzRefundGoodsMapper.insertGzRefundGoods(gzRefundGoods);
        int filteredCount = insertGzRefundGoodsEntry(gzRefundGoods, false);
        gzRefundGoods.setDedupFilteredCount(filteredCount);
        gzLineRefWriteService.persistRefundGoodsRefs(gzRefundGoods, gzRefundGoods.getGzRefundGoodsEntryList(), false);
        return rows;
    }

    @Override
    @Transactional
    public int insertGzRefundStock(GzRefundGoods gzRefundGoods)
    {
        if (gzRefundGoods == null) {
            throw new ServiceException("高值备货退库单不能为空");
        }
        if (StringUtils.isEmpty(gzRefundGoods.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            gzRefundGoods.setTenantId(SecurityUtils.getCustomerId());
        }
        gzRefundGoods.setCreateBy(SecurityUtils.getUserIdStr());
        gzRefundGoods.setSupplerId(null);
        if (StringUtils.isEmpty(gzRefundGoods.getGoodsNo())) {
            gzRefundGoods.setGoodsNo(nextGoodsNoForStock());
        }
        gzRefundGoods.setCreateTime(DateUtils.getNowDate());
        gzStockValidationService.assertRefundTk(gzRefundGoods, gzRefundGoods.getGzRefundGoodsEntryList());
        int rows = gzRefundGoodsMapper.insertGzRefundStock(gzRefundGoods);
        int filteredCount = insertGzRefundGoodsEntry(gzRefundGoods, true);
        gzRefundGoods.setDedupFilteredCount(filteredCount);
        gzLineRefWriteService.persistRefundGoodsRefs(gzRefundGoods, gzRefundGoods.getGzRefundGoodsEntryList(), true);
        return rows;
    }

    private String nextGoodsNoForGoods() {
        String prefix = "GZTH-";
        String date = FillRuleUtil.getDateNum();
        String maxNum = gzRefundGoodsMapper.selectMaxBillNoByPrefix(prefix, date);
        return FillRuleUtil.getNumber(prefix, maxNum, date);
    }

    private String nextGoodsNoForStock() {
        String prefix = "GZTK-";
        String date = FillRuleUtil.getDateNum();
        String maxNum = gzRefundGoodsMapper.selectMaxStockBillNoByPrefix(prefix, date);
        return FillRuleUtil.getNumber(prefix, maxNum, date);
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
        gzStockValidationService.assertRefundTh(gzRefundGoods, gzRefundGoods.getGzRefundGoodsEntryList());
        gzLineRefWriteService.deleteRefundGoodsRefs(gzRefundGoods.getId());
        int filteredCount = syncGzRefundGoodsEntry(gzRefundGoods, false);
        gzRefundGoods.setDedupFilteredCount(filteredCount);
        gzLineRefWriteService.persistRefundGoodsRefs(gzRefundGoods, gzRefundGoods.getGzRefundGoodsEntryList(), false);
        return gzRefundGoodsMapper.updateGzRefundGoods(gzRefundGoods);
    }

    @Override
    @Transactional
    public int updateGzRefundStock(GzRefundGoods gzRefundGoods)
    {
        if (StringUtils.isEmpty(gzRefundGoods.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId())) {
            gzRefundGoods.setTenantId(SecurityUtils.getCustomerId());
        }
        gzRefundGoods.setSupplerId(null);
        gzRefundGoods.setUpdateBy(SecurityUtils.getUserIdStr());
        gzRefundGoods.setUpdateTime(DateUtils.getNowDate());
        gzStockValidationService.assertRefundTk(gzRefundGoods, gzRefundGoods.getGzRefundGoodsEntryList());
        gzLineRefWriteService.deleteRefundGoodsRefs(gzRefundGoods.getId());
        int filteredCount = syncGzRefundGoodsEntry(gzRefundGoods, true);
        gzRefundGoods.setDedupFilteredCount(filteredCount);
        gzLineRefWriteService.persistRefundGoodsRefs(gzRefundGoods, gzRefundGoods.getGzRefundGoodsEntryList(), true);
        return gzRefundGoodsMapper.updateGzRefundStock(gzRefundGoods);
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
        gzLineRefWriteService.deleteRefundGoodsRefs(id);
        gzRefundGoodsMapper.deleteGzRefundGoodsEntryByParenId(id, deleteBy);
        return gzRefundGoodsMapper.deleteGzRefundGoodsById(id, deleteBy);
    }

    @Override
    @Transactional
    public int deleteGzRefundStockById(Long id)
    {
        GzRefundGoods gzRefundGoods = gzRefundGoodsMapper.selectGzRefundStockById(id);
        if(gzRefundGoods == null){
            throw new ServiceException(String.format("高值退库业务：%s，不存在!", id));
        }
        String deleteBy = SecurityUtils.getUserIdStr();
        gzLineRefWriteService.deleteRefundGoodsRefs(id);
        gzRefundGoodsMapper.deleteGzRefundStockEntryByParenId(id, deleteBy);
        return gzRefundGoodsMapper.deleteGzRefundStockById(id, deleteBy);
    }

    @Override
    @Transactional
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
        if (gzRefundGoods.getGoodsStatus() != null && gzRefundGoods.getGoodsStatus() == 2) {
            String no = StringUtils.isNotEmpty(gzRefundGoods.getGoodsNo()) ? gzRefundGoods.getGoodsNo() : id;
            throw new ServiceException(String.format("单据 %s 已审核，请勿重复审核", no));
        }

        List<GzRefundGoodsEntry> gzRefundGoodsEntryList = gzRefundGoods.getGzRefundGoodsEntryList();
        if (gzRefundGoodsEntryList == null || gzRefundGoodsEntryList.isEmpty()) {
            throw new ServiceException(String.format("高值退货单 %s 无明细，无法审核", id));
        }

        gzStockValidationService.assertRefundTh(gzRefundGoods, gzRefundGoodsEntryList);
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
        updateGzDepotInventoryForSupplierReturn(gzRefundGoods, gzRefundGoodsEntryList);

        gzRefundGoods.setGoodsStatus(2);
        gzRefundGoods.setAuditDate(new Date());
        gzRefundGoods.setAuditBy(SecurityUtils.getUserIdStr());
        gzRefundGoods.setUpdateBy(SecurityUtils.getUserIdStr());
        gzRefundGoods.setUpdateTime(new Date());
        return gzRefundGoodsMapper.updateGzRefundGoods(gzRefundGoods);
    }

    @Override
    @Transactional
    public int auditStock(String id) {
        Long billId;
        try {
            billId = Long.valueOf(id);
        } catch (Exception e) {
            throw new ServiceException(String.format("高值退库业务ID：%s 非法", id));
        }
        GzRefundGoods gzRefundGoods = gzRefundGoodsMapper.selectGzRefundStockById(billId);
        if(gzRefundGoods == null){
            throw new ServiceException(String.format("高值退库业务ID：%s，不存在!", id));
        }
        if (gzRefundGoods.getGoodsStatus() != null && gzRefundGoods.getGoodsStatus() == 2) {
            String no = StringUtils.isNotEmpty(gzRefundGoods.getGoodsNo()) ? gzRefundGoods.getGoodsNo() : id;
            throw new ServiceException(String.format("单据 %s 已审核，请勿重复审核", no));
        }
        List<GzRefundGoodsEntry> gzRefundGoodsEntryList = gzRefundGoods.getGzRefundGoodsEntryList();
        if (gzRefundGoodsEntryList == null || gzRefundGoodsEntryList.isEmpty()) {
            throw new ServiceException(String.format("高值退库单 %s 无明细，无法审核", id));
        }
        gzStockValidationService.assertRefundTk(gzRefundGoods, gzRefundGoodsEntryList);
        auditWarehouseStockRefund(gzRefundGoods, gzRefundGoodsEntryList);
        gzRefundGoods.setGoodsStatus(2);
        gzRefundGoods.setAuditDate(new Date());
        gzRefundGoods.setAuditBy(SecurityUtils.getUserIdStr());
        gzRefundGoods.setUpdateBy(SecurityUtils.getUserIdStr());
        gzRefundGoods.setUpdateTime(new Date());
        return gzRefundGoodsMapper.updateGzRefundStock(gzRefundGoods);
    }

    /**
     * 备货退库审核：扣科室库存，按院内码回写高值备货库存（累加或新增）
     */
    private void auditWarehouseStockRefund(GzRefundGoods bill, List<GzRefundGoodsEntry> entries) {
        if (bill.getWarehouseId() == null) {
            throw new ServiceException("备货退库审核失败：表头仓库不能为空");
        }
        if (bill.getDepartmentId() == null) {
            throw new ServiceException("备货退库审核失败：表头科室不能为空");
        }
        for (GzRefundGoodsEntry entry : entries) {
            if (entry == null) {
                continue;
            }
            BigDecimal qty = entry.getQty();
            if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ServiceException("备货退库明细数量须大于0");
            }
            if (StringUtils.isEmpty(entry.getInHospitalCode())) {
                throw new ServiceException("备货退库审核失败：明细院内码不能为空");
            }
            String code = entry.getInHospitalCode().trim();
            GzDepInventory dep = gzDepInventoryMapper.selectGzDepInventoryByCodeAndDept(code, bill.getDepartmentId());
            if (dep == null || dep.getQty() == null) {
                throw new ServiceException(String.format("科室无该院内码库存：%s", code));
            }
            if (dep.getQty().compareTo(qty) < 0) {
                throw new ServiceException(String.format("科室库存不足，院内码 %s，需退 %s 现存 %s", code, qty, dep.getQty()));
            }
            reduceDepInventoryQty(dep, qty);

            GzDepotInventory latest = gzDepotInventoryMapper.selectLatestDepotByInHospitalCodeAndWarehouse(code, bill.getWarehouseId());
            if (latest != null) {
                BigDecimal base = latest.getQty() != null ? latest.getQty() : BigDecimal.ZERO;
                latest.setQty(base.add(qty));
                BigDecimal unit = latest.getUnitPrice() != null ? latest.getUnitPrice()
                    : (entry.getPrice() != null ? entry.getPrice() : BigDecimal.ZERO);
                latest.setUnitPrice(unit);
                latest.setAmt(unit.multiply(latest.getQty()));
                latest.setUpdateBy(SecurityUtils.getUserIdStr());
                latest.setUpdateTime(new Date());
                gzDepotInventoryMapper.updateGzDepotInventory(latest);
            } else {
                GzDepotInventory row = new GzDepotInventory();
                row.setQty(qty);
                row.setMaterialId(entry.getMaterialId());
                row.setWarehouseId(bill.getWarehouseId());
                BigDecimal unit = entry.getPrice() != null ? entry.getPrice() : BigDecimal.ZERO;
                row.setUnitPrice(unit);
                row.setAmt(unit.multiply(qty));
                row.setBatchNo(entry.getBatchNo());
                row.setMaterialNo(entry.getBatchNumber());
                row.setMaterialDate(entry.getBeginTime());
                row.setWarehouseDate(DateUtils.getNowDate());
                row.setSupplierId(entry.getSupplierId() != null ? entry.getSupplierId() : dep.getSupplierId());
                row.setInHospitalCode(code);
                row.setMasterBarcode(entry.getMasterBarcode());
                row.setSecondaryBarcode(entry.getSecondaryBarcode());
                row.setTenantId(StringUtils.isNotEmpty(bill.getTenantId()) ? bill.getTenantId() : SecurityUtils.getCustomerId());
                gzDepotInventoryMapper.insertGzDepotInventory(row);
            }
            hcBarcodeLifecycleService.onGzRefundStockLine(bill, entry);
        }
    }

    private void reduceDepInventoryQty(GzDepInventory row, BigDecimal qty) {
        BigDecimal newQty = row.getQty().subtract(qty);
        if (newQty.compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException("科室库存扣减异常");
        }
        row.setQty(newQty);
        if (row.getUnitPrice() != null) {
            row.setAmt(row.getUnitPrice().multiply(newQty));
        } else if (newQty.compareTo(BigDecimal.ZERO) == 0) {
            row.setAmt(BigDecimal.ZERO);
        }
        row.setUpdateBy(SecurityUtils.getUserIdStr());
        row.setUpdateTime(DateUtils.getNowDate());
        gzDepInventoryMapper.updateGzDepInventory(row);
    }

    /**
     * 备货退货给供应商：按院内码单行扣减；无院内码时按批次 + 供应商对多行正数量 FIFO 扣减（避免同批次多条记录时误命中 id 最大且 qty=0 的行）
     */
    private void updateGzDepotInventoryForSupplierReturn(GzRefundGoods gzRefundGoods,List<GzRefundGoodsEntry> gzRefundGoodsEntryList){
        if (gzRefundGoodsEntryList == null || gzRefundGoodsEntryList.isEmpty()) {
            return;
        }
        if (gzRefundGoods.getWarehouseId() == null) {
            throw new ServiceException("备货退货审核失败：表头仓库不能为空");
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

            String inCode = entry.getInHospitalCode() == null ? "" : entry.getInHospitalCode().trim();
            if (StringUtils.isNotEmpty(inCode)) {
                GzDepotInventory inv = gzDepotInventoryMapper.selectByInHospitalCodeAndWarehouse(inCode, gzRefundGoods.getWarehouseId());
                if (inv == null) {
                    throw new ServiceException(String.format("该仓库下不存在院内码 %s 的可用备货库存，无法退货", inCode));
                }
                BigDecimal depotInventoryQty = inv.getQty() != null ? inv.getQty() : BigDecimal.ZERO;
                if (qty.compareTo(depotInventoryQty) > 0) {
                    throw new ServiceException(String.format("高值备货库存不足！退货数量：%s，备货库存：%s", qty, depotInventoryQty));
                }
                inv.setQty(depotInventoryQty.subtract(qty));
                if (inv.getUnitPrice() != null) {
                    inv.setAmt(inv.getUnitPrice().multiply(inv.getQty()));
                }
                inv.setUpdateTime(new Date());
                inv.setUpdateBy(SecurityUtils.getUserIdStr());
                gzDepotInventoryMapper.updateGzDepotInventory(inv);
                hcBarcodeLifecycleService.onGzRefundGoodsLine(gzRefundGoods, entry);
                continue;
            }

            List<GzDepotInventory> rows = gzDepotInventoryMapper.selectPositiveDepotByBatchWarehouseSupplierAsc(
                batchNo, gzRefundGoods.getWarehouseId(), gzRefundGoods.getSupplerId());
            BigDecimal sum = BigDecimal.ZERO;
            for (GzDepotInventory r : rows) {
                sum = sum.add(r.getQty() != null ? r.getQty() : BigDecimal.ZERO);
            }
            if (rows.isEmpty() || sum.compareTo(qty) < 0) {
                throw new ServiceException(String.format("高值备货库存不足！退货数量：%s，同批次（表头供应商）可用备货合计：%s", qty, sum));
            }
            subtractDepotInventoryFifoForSupplierReturn(rows, qty, batchNo);
            hcBarcodeLifecycleService.onGzRefundGoodsLine(gzRefundGoods, entry);
        }
    }

    private void subtractDepotInventoryFifoForSupplierReturn(List<GzDepotInventory> rows, BigDecimal need, String batchNo) {
        BigDecimal remaining = need;
        String updateBy = SecurityUtils.getUserIdStr();
        Date now = new Date();
        for (GzDepotInventory row : rows) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
            BigDecimal q = row.getQty() != null ? row.getQty() : BigDecimal.ZERO;
            if (q.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            BigDecimal take = q.min(remaining);
            row.setQty(q.subtract(take));
            if (row.getUnitPrice() != null) {
                row.setAmt(row.getUnitPrice().multiply(row.getQty()));
            }
            row.setUpdateTime(now);
            row.setUpdateBy(updateBy);
            gzDepotInventoryMapper.updateGzDepotInventory(row);
            remaining = remaining.subtract(take);
        }
        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            throw new ServiceException(String.format("备货库存扣减未完成，批次号：%s", batchNo));
        }
    }

    /**
     * 新增高值退货明细信息
     *
     * @param gzRefundGoods 高值退货对象
     */
    public int insertGzRefundGoodsEntry(GzRefundGoods gzRefundGoods, boolean warehouseStockRefund)
    {
        List<GzRefundGoodsEntry> gzRefundGoodsEntryList = gzRefundGoods.getGzRefundGoodsEntryList();
        Long id = gzRefundGoods.getId();
        int filteredCount = 0;
        if (StringUtils.isNotNull(gzRefundGoodsEntryList))
        {
            List<GzRefundGoodsEntry> list = new ArrayList<GzRefundGoodsEntry>();
            String tenantId = StringUtils.isNotEmpty(gzRefundGoods.getTenantId())
                ? gzRefundGoods.getTenantId()
                : SecurityUtils.requiredScopedTenantIdForSql();
            String userId = SecurityUtils.getUserIdStr();
            Date now = DateUtils.getNowDate();
            Set<String> dedupRefKeys = new HashSet<>();
            for (GzRefundGoodsEntry gzRefundGoodsEntry : gzRefundGoodsEntryList)
            {
                if (gzRefundGoodsEntry == null) {
                    continue;
                }
                String refKey = buildRefundRefDedupKey(gzRefundGoodsEntry);
                if (StringUtils.isNotEmpty(refKey) && dedupRefKeys.contains(refKey)) {
                    filteredCount++;
                    continue;
                }
                if (!warehouseStockRefund && gzRefundGoods.getWarehouseId() == null) {
                    throw new ServiceException("备货退货保存失败：请先选择仓库");
                }
                gzRefundGoodsEntry.setParenId(id);
                gzRefundGoodsEntry.setDelFlag(0);
                gzRefundGoodsEntry.setTenantId(tenantId);
                if (gzRefundGoods.getSupplerId() != null) {
                    gzRefundGoodsEntry.setSupplierId(gzRefundGoods.getSupplerId());
                }
                gzRefundGoodsEntry.setWarehouseId(gzRefundGoods.getWarehouseId());
                gzRefundGoodsEntry.setDepartmentId(gzRefundGoods.getDepartmentId());
                gzRefundGoodsEntry.setBillNo(gzRefundGoods.getGoodsNo());
                gzRefundGoodsEntry.setCreateBy(userId);
                gzRefundGoodsEntry.setCreateTime(now);
                gzRefundGoodsEntry.setUpdateBy(userId);
                gzRefundGoodsEntry.setUpdateTime(now);
                list.add(gzRefundGoodsEntry);
                if (StringUtils.isNotEmpty(refKey)) {
                    dedupRefKeys.add(refKey);
                }
            }
            if (list.size() > 0)
            {
                if (warehouseStockRefund) {
                    gzRefundGoodsMapper.batchGzRefundStockEntry(list);
                } else {
                    gzRefundGoodsMapper.batchGzRefundGoodsEntry(list);
                }
            }
        }
        return filteredCount;
    }

    /**
     * 修改单据时明细增量更新：按ID更新/新增，缺失项逻辑删除
     */
    public int syncGzRefundGoodsEntry(GzRefundGoods gzRefundGoods, boolean warehouseStockRefund)
    {
        String entryType = warehouseStockRefund ? "GZ_REFUND_STOCK_ENTRY" : "GZ_REFUND_GOODS_ENTRY";
        List<GzRefundGoodsEntry> entryList = gzRefundGoods.getGzRefundGoodsEntryList();
        Long parenId = gzRefundGoods.getId();
        int filteredCount = 0;
        String userId = SecurityUtils.getUserIdStr();
        Date now = DateUtils.getNowDate();

        List<Long> existingIds = warehouseStockRefund
            ? gzRefundGoodsMapper.selectActiveRefundStockEntryIdsByParenId(parenId)
            : gzRefundGoodsMapper.selectActiveRefundGoodsEntryIdsByParenId(parenId);
        Set<Long> remainingIds = new HashSet<>();
        if (existingIds != null) {
            remainingIds.addAll(existingIds);
        }
        Map<Long, GzRefundGoodsEntry> oldEntryMap = (warehouseStockRefund
            ? gzRefundGoodsMapper.selectActiveRefundStockEntriesByParenId(parenId)
            : gzRefundGoodsMapper.selectActiveRefundGoodsEntriesByParenId(parenId))
            .stream().collect(Collectors.toMap(GzRefundGoodsEntry::getId, e -> e, (a, b) -> a));

        if (StringUtils.isNotNull(entryList))
        {
            List<GzRefundGoodsEntry> newList = new ArrayList<GzRefundGoodsEntry>();
            String tenantId = StringUtils.isNotEmpty(gzRefundGoods.getTenantId())
                ? gzRefundGoods.getTenantId()
                : SecurityUtils.requiredScopedTenantIdForSql();
            Set<String> dedupRefKeys = new HashSet<>();
            for (GzRefundGoodsEntry entry : entryList)
            {
                if (entry == null) {
                    continue;
                }
                String refKey = buildRefundRefDedupKey(entry);
                if (StringUtils.isNotEmpty(refKey) && dedupRefKeys.contains(refKey)) {
                    filteredCount++;
                    continue;
                }
                if (!warehouseStockRefund && gzRefundGoods.getWarehouseId() == null) {
                    throw new ServiceException("备货退货保存失败：请先选择仓库");
                }
                entry.setParenId(parenId);
                entry.setDelFlag(0);
                entry.setTenantId(tenantId);
                if (gzRefundGoods.getSupplerId() != null) {
                    entry.setSupplierId(gzRefundGoods.getSupplerId());
                }
                entry.setWarehouseId(gzRefundGoods.getWarehouseId());
                entry.setDepartmentId(gzRefundGoods.getDepartmentId());
                entry.setBillNo(gzRefundGoods.getGoodsNo());
                entry.setUpdateBy(userId);
                entry.setUpdateTime(now);

                if (entry.getId() != null) {
                    GzRefundGoodsEntry old = oldEntryMap.get(entry.getId());
                    if (warehouseStockRefund) {
                        gzRefundGoodsMapper.updateGzRefundStockEntryById(entry);
                    } else {
                        gzRefundGoodsMapper.updateGzRefundGoodsEntryById(entry);
                    }
                    remainingIds.remove(entry.getId());
                    if (old != null && isRefundEntryChanged(old, entry)) {
                        log.info("GZ_REFUND_ENTRY_CHANGE UPDATE billId={}, entryId={}, before={}, after={}",
                            parenId, entry.getId(), JSON.toJSONString(old), JSON.toJSONString(entry));
                        saveEntryChangeLog(warehouseStockRefund ? "GZ_REFUND_STOCK" : "GZ_REFUND_GOODS",
                            parenId, entryType, entry.getId(), "UPDATE", old, entry, userId, gzRefundGoods.getTenantId());
                    }
                } else {
                    entry.setCreateBy(userId);
                    entry.setCreateTime(now);
                    newList.add(entry);
                    log.info("GZ_REFUND_ENTRY_CHANGE INSERT billId={}, entry={}", parenId, JSON.toJSONString(entry));
                    saveEntryChangeLog(warehouseStockRefund ? "GZ_REFUND_STOCK" : "GZ_REFUND_GOODS",
                        parenId, entryType, null, "INSERT", null, entry, userId, gzRefundGoods.getTenantId());
                }
                if (StringUtils.isNotEmpty(refKey)) {
                    dedupRefKeys.add(refKey);
                }
            }
            if (!newList.isEmpty()) {
                if (warehouseStockRefund) {
                    gzRefundGoodsMapper.batchGzRefundStockEntry(newList);
                } else {
                    gzRefundGoodsMapper.batchGzRefundGoodsEntry(newList);
                }
            }
        }

        for (Long removedId : remainingIds) {
            GzRefundGoodsEntry toDelete = new GzRefundGoodsEntry();
            toDelete.setId(removedId);
            toDelete.setParenId(parenId);
            toDelete.setDelFlag(1);
            toDelete.setUpdateBy(userId);
            if (warehouseStockRefund) {
                gzRefundGoodsMapper.updateGzRefundStockEntryById(toDelete);
            } else {
                gzRefundGoodsMapper.updateGzRefundGoodsEntryById(toDelete);
            }
            GzRefundGoodsEntry old = oldEntryMap.get(removedId);
            log.info("GZ_REFUND_ENTRY_CHANGE DELETE billId={}, entryId={}, before={}",
                parenId, removedId, JSON.toJSONString(old));
            saveEntryChangeLog(warehouseStockRefund ? "GZ_REFUND_STOCK" : "GZ_REFUND_GOODS",
                parenId, entryType, removedId, "DELETE", old, null, userId, gzRefundGoods.getTenantId());
        }
        return filteredCount;
    }

    private void saveEntryChangeLog(String billType, Long billId, String entryType, Long entryId, String actionType,
                                    Object before, Object after, String operator, String tenantId) {
        GzBillEntryChangeLog rec = new GzBillEntryChangeLog();
        rec.setId(UUID7.generateUUID7());
        rec.setBillType(billType);
        rec.setBillId(billId);
        rec.setEntryType(entryType);
        rec.setEntryId(entryId);
        rec.setActionType(actionType);
        rec.setBeforeJson(before == null ? null : JSON.toJSONString(before));
        rec.setAfterJson(after == null ? null : JSON.toJSONString(after));
        rec.setOperator(operator);
        rec.setChangeTime(DateUtils.getNowDate());
        rec.setTenantId(StringUtils.isNotEmpty(tenantId) ? tenantId : SecurityUtils.requiredScopedTenantIdForSql());
        gzBillEntryChangeLogMapper.insert(rec);
    }

    private boolean isRefundEntryChanged(GzRefundGoodsEntry oldRow, GzRefundGoodsEntry newRow) {
        if (oldRow == null || newRow == null) {
            return false;
        }
        return !java.util.Objects.equals(oldRow.getMaterialId(), newRow.getMaterialId())
            || !java.util.Objects.equals(oldRow.getQty(), newRow.getQty())
            || !java.util.Objects.equals(oldRow.getPrice(), newRow.getPrice())
            || !java.util.Objects.equals(oldRow.getAmt(), newRow.getAmt())
            || !java.util.Objects.equals(oldRow.getBatchNo(), newRow.getBatchNo())
            || !java.util.Objects.equals(oldRow.getBatchNumber(), newRow.getBatchNumber())
            || !java.util.Objects.equals(oldRow.getMasterBarcode(), newRow.getMasterBarcode())
            || !java.util.Objects.equals(oldRow.getSecondaryBarcode(), newRow.getSecondaryBarcode())
            || !java.util.Objects.equals(oldRow.getInHospitalCode(), newRow.getInHospitalCode())
            || !java.util.Objects.equals(oldRow.getBeginTime(), newRow.getBeginTime())
            || !java.util.Objects.equals(oldRow.getEndTime(), newRow.getEndTime())
            || !java.util.Objects.equals(oldRow.getSupplierId(), newRow.getSupplierId())
            || !java.util.Objects.equals(oldRow.getWarehouseId(), newRow.getWarehouseId())
            || !java.util.Objects.equals(oldRow.getDepartmentId(), newRow.getDepartmentId())
            || !java.util.Objects.equals(oldRow.getBillNo(), newRow.getBillNo())
            || !java.util.Objects.equals(oldRow.getRemark(), newRow.getRemark());
    }

    private String buildRefundRefDedupKey(GzRefundGoodsEntry e) {
        if (e == null) {
            return null;
        }
        if (StringUtils.isNotEmpty(e.getRefSrcShipmentEntryId())) {
            return "SHIP_ENTRY#" + e.getRefSrcShipmentEntryId().trim();
        }
        if (StringUtils.isNotEmpty(e.getRefSrcBarcodeLineId())) {
            return "ACC_BARCODE#" + e.getRefSrcBarcodeLineId().trim();
        }
        if (StringUtils.isNotEmpty(e.getRefSrcOrderEntryId())) {
            return "ACC_ENTRY#" + e.getRefSrcOrderEntryId().trim();
        }
        if (StringUtils.isNotEmpty(e.getInHospitalCode()) && StringUtils.isNotEmpty(e.getBatchNo())) {
            return "IHC_BATCH#" + e.getInHospitalCode().trim() + "#" + e.getBatchNo().trim();
        }
        return null;
    }
}

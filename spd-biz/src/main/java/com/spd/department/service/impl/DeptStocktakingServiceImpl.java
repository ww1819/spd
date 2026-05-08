package com.spd.department.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import com.spd.department.domain.HcKsFlow;
import com.spd.department.domain.StkDepInventory;
import com.spd.department.mapper.HcKsFlowMapper;
import com.spd.department.mapper.StkDepInventoryMapper;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.warehouse.domain.StkIoStocktaking;
import com.spd.warehouse.domain.StkIoStocktakingEntry;
import com.spd.warehouse.domain.StkBatch;
import com.spd.warehouse.domain.StkInventory;
import com.spd.department.mapper.DeptStocktakingMapper;
import com.spd.department.vo.DeptStocktakingExportRow;
import com.spd.warehouse.mapper.StkBatchMapper;
import com.spd.warehouse.mapper.StkInventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import com.spd.department.service.IDeptStocktakingService;

/**
 * 科室盘点Service业务层处理
 *
 * @author spd
 * @date 2025-01-28
 */
@Service
public class DeptStocktakingServiceImpl implements IDeptStocktakingService
{
    @Autowired
    private DeptStocktakingMapper deptStocktakingMapper;

    @Autowired
    private StkDepInventoryMapper stkDepInventoryMapper;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    @Autowired
    private StkBatchMapper stkBatchMapper;

    @Autowired
    private StkInventoryMapper stkInventoryMapper;

    @Autowired
    private HcKsFlowMapper hcKsFlowMapper;

    /**
     * 查询科室盘点
     *
     * @param id 科室盘点主键
     * @return 科室盘点
     */
    @Override
    public StkIoStocktaking selectDeptStocktakingById(Long id)
    {
        return deptStocktakingMapper.selectDeptStocktakingById(id);
    }

    /**
     * 查询科室盘点列表
     * 只查询科室盘点（departmentId不为空，warehouseId为空或忽略）
     *
     * @param stkIoStocktaking 科室盘点
     * @return 科室盘点集合
     */
    @Override
    public List<StkIoStocktaking> selectDeptStocktakingList(StkIoStocktaking stkIoStocktaking)
    {
        // 确保只查询科室盘点，不查询仓库盘点
        stkIoStocktaking.setWarehouseId(null);
        return deptStocktakingMapper.selectDeptStocktakingList(stkIoStocktaking);
    }

    @Override
    public List<DeptStocktakingExportRow> selectDeptStocktakingExportList(StkIoStocktaking stkIoStocktaking)
    {
        stkIoStocktaking.setWarehouseId(null);
        return deptStocktakingMapper.selectDeptStocktakingExportList(stkIoStocktaking);
    }

    /**
     * 新增科室盘点
     *
     * @param stkIoStocktaking 科室盘点
     * @return 结果
     */
    @Transactional
    @Override
    public int insertDeptStocktaking(StkIoStocktaking stkIoStocktaking)
    {
        stkIoStocktaking.setStockNo(getNumber());
        stkIoStocktaking.setCreateTime(DateUtils.getNowDate());
        // 确保warehouseId为null，表示这是科室盘点
        stkIoStocktaking.setWarehouseId(null);
        if (stkIoStocktaking.getAuditAdjustsInventory() == null) {
            stkIoStocktaking.setAuditAdjustsInventory(0);
        }
        int rows = deptStocktakingMapper.insertDeptStocktaking(stkIoStocktaking);
        insertStkIoStocktakingEntry(stkIoStocktaking);
        return rows;
    }

    //流水号
    public String getNumber() {
        String str = "KSPD"; // 科室盘点前缀
        String date = FillRuleUtil.getDateNum();
        String maxNum = deptStocktakingMapper.selectMaxBillNo(date);
        String result = FillRuleUtil.getNumber(str,maxNum,date);
        return result;
    }

    /**
     * 修改科室盘点
     *
     * @param stkIoStocktaking 科室盘点
     * @return 结果
     */
    @Transactional
    @Override
    public int updateDeptStocktaking(StkIoStocktaking stkIoStocktaking)
    {
        stkIoStocktaking.setUpdateTime(DateUtils.getNowDate());
        // 确保warehouseId为null，表示这是科室盘点
        stkIoStocktaking.setWarehouseId(null);
        Long parenId = stkIoStocktaking.getId();
        List<StkIoStocktakingEntry> entryList = stkIoStocktaking.getStkIoStocktakingEntryList();
        List<Long> keepIds = new ArrayList<>();
        if (StringUtils.isNotNull(entryList)) {
            for (StkIoStocktakingEntry entry : entryList) {
                entry.setParenId(parenId);
                if (StringUtils.isEmpty(entry.getBatchNo())) {
                    entry.setBatchNo(getBatchNumber());
                }
                // 补齐盘盈明细的可退库仓库：从产品档案默认所属仓库获取
                fillReturnWarehouseIdByMaterial(entry);
                if (entry.getId() != null) {
                    deptStocktakingMapper.updateDeptStocktakingEntry(entry);
                    keepIds.add(entry.getId());
                } else {
                    deptStocktakingMapper.insertDeptStocktakingEntrySingle(entry);
                    // 必须加入 keepIds，否则 deleteDeptStocktakingEntryByParenIdExceptIds 会把刚插入的明细全部软删
                    if (entry.getId() != null) {
                        keepIds.add(entry.getId());
                    }
                }
            }
            deptStocktakingMapper.deleteDeptStocktakingEntryByParenIdExceptIds(parenId, keepIds);
        }
        return deptStocktakingMapper.updateDeptStocktaking(stkIoStocktaking);
    }

    /**
     * 批量删除科室盘点
     *
     * @param ids 需要删除的科室盘点主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteDeptStocktakingByIds(Long[] ids)
    {
        deptStocktakingMapper.deleteDeptStocktakingEntryByParenIds(ids);
        return deptStocktakingMapper.deleteDeptStocktakingByIds(ids);
    }

    /**
     * 删除科室盘点信息
     *
     * @param id 科室盘点主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteDeptStocktakingById(Long id)
    {
        deptStocktakingMapper.deleteDeptStocktakingEntryByParenId(id);
        return deptStocktakingMapper.deleteDeptStocktakingById(id);
    }

    /**
     * 审核科室盘点信息
     * 更新科室库存（stk_dep_inventory表）
     * @param id
     * @return
     */
    @Transactional
    @Override
    public int auditDeptStocktaking(String id) {
        StkIoStocktaking stkIoStocktaking = deptStocktakingMapper.selectDeptStocktakingById(Long.valueOf(id));
        if(stkIoStocktaking == null){
            throw new ServiceException(String.format("科室盘点业务ID：%s，不存在!", id));
        }

        List<StkIoStocktakingEntry> stkIoStocktakingEntryList = stkIoStocktaking.getStkIoStocktakingEntryList();
        if (stkIoStocktaking.getStockType() != null && stkIoStocktaking.getStockType() == 502) {
            if (stkIoStocktakingEntryList == null || stkIoStocktakingEntryList.isEmpty()) {
                throw new ServiceException("盘点单无有效明细（可能保存时明细被误删），无法审核。请驳回或删除本单后重新制单并保存。");
            }
        }

        // audit_adjusts_inventory=1：审核直接变更科室库存；=0：仅过账，由科室盈亏单处理库存
        if (stkIoStocktaking.getAuditAdjustsInventory() != null && stkIoStocktaking.getAuditAdjustsInventory() == 1) {
            updateDepInventory(stkIoStocktaking, stkIoStocktakingEntryList);
        }

        stkIoStocktaking.setAuditDate(new Date());
        stkIoStocktaking.setStockStatus(2);
        stkIoStocktaking.setUpdateBy(SecurityUtils.getUserIdStr());
        stkIoStocktaking.setUpdateTime(new Date());

        int res = deptStocktakingMapper.updateDeptStocktaking(stkIoStocktaking);
        return res;
    }

    /**
     * 驳回科室盘点信息
     * @param id 盘点ID
     * @param rejectReason 驳回原因
     * @return
     */
    @Transactional
    @Override
    public int rejectDeptStocktaking(String id, String rejectReason) {
        StkIoStocktaking stkIoStocktaking = deptStocktakingMapper.selectDeptStocktakingById(Long.valueOf(id));
        if(stkIoStocktaking == null){
            throw new ServiceException(String.format("科室盘点业务ID：%s，不存在!", id));
        }
        if(stkIoStocktaking.getStockStatus() != 1){
            throw new ServiceException(String.format("科室盘点业务ID：%s，状态不是未审核，无法驳回!", id));
        }

        stkIoStocktaking.setStockStatus(3); // 驳回状态
        stkIoStocktaking.setRejectReason(rejectReason);
        stkIoStocktaking.setUpdateBy(SecurityUtils.getUserIdStr());
        stkIoStocktaking.setUpdateTime(new Date());

        int res = deptStocktakingMapper.updateDeptStocktaking(stkIoStocktaking);
        return res;
    }

    /**
     * 更新科室库存
     * @param stkIoStocktaking 科室盘点
     * @param stkIoStocktakingEntryList 盘点明细列表
     */
    private void updateDepInventory(StkIoStocktaking stkIoStocktaking, List<StkIoStocktakingEntry> stkIoStocktakingEntryList){
        Integer stockType = stkIoStocktaking.getStockType();
        if (stockType == null || stkIoStocktakingEntryList == null) {
            return;
        }

        for (StkIoStocktakingEntry entry : stkIoStocktakingEntryList) {
            if (stockType == 501) {
                if (entry.getQty() == null || BigDecimal.ZERO.compareTo(entry.getQty()) == 0) {
                    continue;
                }
                //期初
                StkDepInventory stkDepInventory = new StkDepInventory();
                stkDepInventory.setBatchNo(entry.getBatchNo());
                stkDepInventory.setMaterialId(entry.getMaterialId());
                stkDepInventory.setDepartmentId(stkIoStocktaking.getDepartmentId());
                stkDepInventory.setWarehouseId(entry.getReturnWarehouseId());
                stkDepInventory.setQty(entry.getQty());
                // 优先使用 unitPrice，如果为空则使用 price
                BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
                stkDepInventory.setUnitPrice(unitPrice);
                stkDepInventory.setAmt(unitPrice != null ? entry.getQty().multiply(unitPrice) : BigDecimal.ZERO);
                stkDepInventory.setMaterialDate(new Date());
                stkDepInventory.setWarehouseDate(new Date());
                stkDepInventory.setMaterialNo(entry.getBatchNumber());
                // batch_number 为产品生产批号；batch_no 为系统追溯批次号
                stkDepInventory.setBatchNumber(entry.getBatchNumber());
                stkDepInventory.setBeginDate(entry.getBeginTime());
                stkDepInventory.setEndDate(entry.getEndTime());
                // 科室盘点审核后视为已收货确认，避免退库时被 receipt_confirm_status=0 拦截
                stkDepInventory.setReceiptConfirmStatus(1);
                // 生成/补齐批次字典并回填科室库存 batch_id
                FdMaterial material = fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
                if (material == null) {
                    throw new ServiceException(String.format("耗材ID：%s，产品档案不存在!", entry.getMaterialId()));
                }
                StkBatch stkBatch = ensureStkBatchByStocktaking(stkIoStocktaking, entry, material);
                if (stkBatch != null && stkBatch.getId() != null) {
                    stkDepInventory.setBatchId(stkBatch.getId());
                    ensureWarehouseQtyZeroInventory(entry, stkBatch);
                }
                stkDepInventory.setCreateTime(new Date());
                stkDepInventory.setCreateBy(SecurityUtils.getUserIdStr());

                stkDepInventoryMapper.insertStkDepInventory(stkDepInventory);
            } else if (stockType == 502) {
                //盘点：需处理账面为 0 的盘盈行（原逻辑仅处理 qty!=0 会整行跳过）
                String batchNo = entry.getBatchNo();
                if (StringUtils.isEmpty(batchNo)) {
                    throw new ServiceException("盘点明细缺少批次号，无法审核。");
                }
                BigDecimal bookQty = entry.getQty() != null ? entry.getQty() : BigDecimal.ZERO;
                BigDecimal stockQty = entry.getStockQty() != null ? entry.getStockQty() : BigDecimal.ZERO;
                if (bookQty.compareTo(BigDecimal.ZERO) == 0 && stockQty.compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }

                FdMaterial material = fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
                if (material == null) {
                    throw new ServiceException(String.format("耗材ID：%s，产品档案不存在!", entry.getMaterialId()));
                }

                // 先按明细仓库查；科室库存行的 warehouse_id 来自出库，可能与产品档案默认仓不一致，再按「科室+批次」回退
                Long warehouseId = entry.getReturnWarehouseId();
                StkDepInventory depInventory = null;
                if (warehouseId != null) {
                    depInventory = stkDepInventoryMapper.selectStkDepInventoryOneForStocktaking(batchNo, warehouseId);
                }
                Long departmentId = stkIoStocktaking.getDepartmentId();
                if (depInventory == null && departmentId != null) {
                    depInventory = stkDepInventoryMapper.selectStkDepInventoryByBatchAndDeptForStocktaking(batchNo, departmentId);
                }
                if (depInventory != null && depInventory.getWarehouseId() != null) {
                    entry.setDepInventoryId(String.valueOf(depInventory.getId()));
                    entry.setReturnWarehouseId(depInventory.getWarehouseId());
                    warehouseId = depInventory.getWarehouseId();
                } else {
                    fillReturnWarehouseIdByMaterial(entry);
                    warehouseId = entry.getReturnWarehouseId();
                }
                if (warehouseId == null) {
                    throw new ServiceException("盘点明细缺少所属仓库（且科室下未找到该批次库存），无法审核。");
                }
                if (depInventory == null) {
                    if (stockQty.compareTo(bookQty) <= 0) {
                        throw new ServiceException(String.format(
                            "科室库存批次号：%s，在本盘点科室下不存在；若为盘盈请保证盘点数量大于账面库存。"
                                + "若从科室库存带出明细，请勿依赖产品默认仓库，应保存后重试或重新从科室库存选择。", batchNo));
                    }
                    insertStkDepInventoryProfitFromDeptStocktaking(stkIoStocktaking, entry, material, stockQty);
                    continue;
                }

                StkBatch stkBatch = ensureStkBatchByStocktaking(stkIoStocktaking, entry, material);
                if (stkBatch != null && stkBatch.getId() != null && depInventory.getBatchId() == null) {
                    depInventory.setBatchId(stkBatch.getId());
                }

                boolean isProfit = stockQty.compareTo(bookQty) > 0;
                if (stockQty.compareTo(bookQty) == 0) {
                    depInventory.setReceiptConfirmStatus(1);
                    if (depInventory.getWarehouseId() == null) {
                        depInventory.setWarehouseId(warehouseId);
                    }
                    if (depInventory.getBatchNumber() == null) {
                        depInventory.setBatchNumber(entry.getBatchNumber());
                    }
                    stkDepInventoryMapper.updateStkDepInventory(depInventory);
                    continue;
                }

                BigDecimal totalQty;
                BigDecimal totalAmt;
                BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();

                if (stockQty.compareTo(bookQty) > 0) {
                    BigDecimal stkQty = stockQty.subtract(bookQty);
                    totalQty = depInventory.getQty().add(stkQty);
                    totalAmt = unitPrice != null ? totalQty.multiply(unitPrice) : BigDecimal.ZERO;
                } else {
                    totalQty = stockQty;
                    totalAmt = unitPrice != null ? stockQty.multiply(unitPrice) : BigDecimal.ZERO;
                }
                depInventory.setQty(totalQty);
                depInventory.setAmt(totalAmt);
                depInventory.setUnitPrice(unitPrice);
                depInventory.setWarehouseDate(new Date());
                depInventory.setReceiptConfirmStatus(1);
                if (depInventory.getWarehouseId() == null) {
                    depInventory.setWarehouseId(warehouseId);
                }
                if (stkBatch != null && stkBatch.getId() != null) {
                    depInventory.setBatchId(stkBatch.getId());
                }
                if (depInventory.getBatchNumber() == null) {
                    depInventory.setBatchNumber(entry.getBatchNumber());
                }
                if (isProfit) {
                    ensureWarehouseQtyZeroInventory(entry, stkBatch);
                }
                depInventory.setUpdateTime(new Date());
                depInventory.setUpdateBy(SecurityUtils.getUserIdStr());

                stkDepInventoryMapper.updateStkDepInventory(depInventory);
            }
        }
    }

    /**
     * 科室盘点盘盈且科室尚无该批次库存行时，新增 stk_dep_inventory（数量为盘点实盘）
     */
    private void insertStkDepInventoryProfitFromDeptStocktaking(StkIoStocktaking stkIoStocktaking, StkIoStocktakingEntry entry,
            FdMaterial material, BigDecimal finalQty) {
        StkBatch stkBatch = ensureStkBatchByStocktaking(stkIoStocktaking, entry, material);
        StkDepInventory stkDepInventory = new StkDepInventory();
        stkDepInventory.setBatchNo(entry.getBatchNo());
        stkDepInventory.setMaterialId(entry.getMaterialId());
        stkDepInventory.setDepartmentId(stkIoStocktaking.getDepartmentId());
        stkDepInventory.setWarehouseId(entry.getReturnWarehouseId());
        stkDepInventory.setQty(finalQty);
        BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
        stkDepInventory.setUnitPrice(unitPrice);
        stkDepInventory.setAmt(unitPrice != null ? finalQty.multiply(unitPrice) : BigDecimal.ZERO);
        stkDepInventory.setMaterialDate(new Date());
        stkDepInventory.setWarehouseDate(new Date());
        stkDepInventory.setMaterialNo(entry.getBatchNumber());
        stkDepInventory.setBatchNumber(entry.getBatchNumber());
        stkDepInventory.setBeginDate(entry.getBeginTime());
        stkDepInventory.setEndDate(entry.getEndTime());
        stkDepInventory.setReceiptConfirmStatus(1);
        if (stkBatch != null && stkBatch.getId() != null) {
            stkDepInventory.setBatchId(stkBatch.getId());
            ensureWarehouseQtyZeroInventory(entry, stkBatch);
        }
        stkDepInventory.setCreateTime(new Date());
        stkDepInventory.setCreateBy(SecurityUtils.getUserIdStr());
        stkDepInventoryMapper.insertStkDepInventory(stkDepInventory);
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
                // 补齐盘盈明细的可退库仓库：从产品档案默认所属仓库获取
                fillReturnWarehouseIdByMaterial(stkIoStocktakingEntry);
                list.add(stkIoStocktakingEntry);
            }
            if (list.size() > 0)
            {
                deptStocktakingMapper.batchDeptStocktakingEntry(list);
            }
        }
    }

    /**
     * 补齐盘盈明细的可退库仓库ID：
     * 1) 从产品档案 fd_material.default_warehouse_id 取值
     * 2) 缺失则直接拦截，要求库房先维护产品档案
     */
    private void fillReturnWarehouseIdByMaterial(StkIoStocktakingEntry entry) {
        if (entry == null || entry.getMaterialId() == null) {
            return;
        }
        Long current = entry.getReturnWarehouseId();
        if (current != null) {
            return;
        }
        FdMaterial material = fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
        if (material == null) {
            throw new ServiceException(String.format("耗材ID：%s，产品档案不存在!", entry.getMaterialId()));
        }
        Long defaultWarehouseId = material.getDefaultWarehouseId();
        if (defaultWarehouseId == null || defaultWarehouseId == 0) {
            throw new ServiceException("产品档案未维护默认所属仓库ID，请联系库房维护后再生成该产品档案的明细数据。");
        }
        entry.setReturnWarehouseId(defaultWarehouseId);
    }

    private StkBatch ensureStkBatchByStocktaking(StkIoStocktaking stkIoStocktaking, StkIoStocktakingEntry entry, FdMaterial material) {
        if (entry == null || StringUtils.isEmpty(entry.getBatchNo())) {
            return null;
        }
        Long warehouseId = entry.getReturnWarehouseId();
        StkBatch stkBatch = stkBatchMapper.selectByBatchNo(entry.getBatchNo());
        if (stkBatch != null) {
            // 补齐缺失批次字典关联
            if (stkBatch.getWarehouseId() == null && warehouseId != null) {
                stkBatch.setWarehouseId(warehouseId);
                // 这里不额外更新数据库，避免引入不必要的写操作
            }
            return stkBatch;
        }

        BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
        Long supplierId = entry.getSupplierId() != null ? entry.getSupplierId() : material.getSupplierId();

        String batchSource;
        String originBusinessType;
        String originFlowLx;
        Integer stockType = stkIoStocktaking.getStockType();
        if (stockType != null && stockType == 501) {
            batchSource = "科室期初盘盈";
            originBusinessType = "科室期初盘盈";
            originFlowLx = "PY";
        } else {
            boolean isProfit;
            if (entry.getProfitQty() != null) {
                isProfit = entry.getProfitQty().compareTo(BigDecimal.ZERO) > 0;
            } else {
                BigDecimal sq = entry.getStockQty() != null ? entry.getStockQty() : BigDecimal.ZERO;
                BigDecimal bq = entry.getQty() != null ? entry.getQty() : BigDecimal.ZERO;
                isProfit = sq.compareTo(bq) > 0;
            }
            if (isProfit) {
                batchSource = "科室盘盈";
                originBusinessType = "科室盘盈";
                originFlowLx = "PY";
            } else {
                batchSource = "科室盘亏";
                originBusinessType = "科室盘亏";
                originFlowLx = "PK";
            }
        }

        StkBatch b = new StkBatch();
        b.setBatchNo(entry.getBatchNo());
        b.setMaterialId(entry.getMaterialId());
        b.setMaterialCode(material.getCode());
        b.setMaterialName(material.getName());
        b.setSpeci(material.getSpeci());
        b.setModel(material.getModel());
        b.setUnitId(material.getUnitId());
        if (material.getFdUnit() != null) {
            b.setUnitName(material.getFdUnit().getUnitName());
        }
        b.setUnitPrice(unitPrice);
        b.setBatchNumber(entry.getBatchNumber());
        b.setBeginTime(entry.getBeginTime());
        b.setEndTime(entry.getEndTime());

        b.setSupplierId(supplierId);
        b.setWarehouseId(warehouseId);
        b.setDepartmentId(stkIoStocktaking.getDepartmentId());

        b.setBatchSource(batchSource);
        b.setOriginBusinessType(originBusinessType);
        b.setOriginFlowLx(originFlowLx);

        b.setDelFlag(0);
        b.setCreateTime(new Date());
        b.setCreateBy(SecurityUtils.getUserIdStr());
        b.setUpdateTime(new Date());
        b.setUpdateBy(SecurityUtils.getUserIdStr());
        b.setTenantId(SecurityUtils.getCustomerId());

        stkBatchMapper.insertStkBatch(b);
        return stkBatchMapper.selectByBatchNo(entry.getBatchNo());
    }

    private void ensureWarehouseQtyZeroInventory(StkIoStocktakingEntry entry, StkBatch stkBatch) {
        if (entry == null || stkBatch == null || stkBatch.getId() == null) {
            return;
        }
        Long warehouseId = entry.getReturnWarehouseId();
        if (warehouseId == null) {
            return;
        }
        StkInventory inventory = stkInventoryMapper.selectStkInventoryByBatchNoAndWarehouse(entry.getBatchNo(), warehouseId);
        if (inventory != null) {
            // 缺少批次对象表ID时补齐（不强制覆盖数量/金额）
            if (inventory.getBatchId() == null) {
                inventory.setBatchId(stkBatch.getId());
                stkInventoryMapper.updateStkInventory(inventory);
            }
            return;
        }

        StkInventory inv = new StkInventory();
        inv.setQty(BigDecimal.ZERO);
        inv.setMaterialId(entry.getMaterialId());
        inv.setWarehouseId(warehouseId);
        inv.setUnitPrice(stkBatch.getUnitPrice());
        inv.setAmt(BigDecimal.ZERO);
        inv.setBatchNo(entry.getBatchNo());
        inv.setBatchId(stkBatch.getId());
        inv.setMaterialNo(entry.getBatchNumber());
        inv.setMaterialDate(new Date());
        inv.setWarehouseDate(new Date());
        inv.setSupplierId(stkBatch.getSupplierId());
        inv.setBeginTime(entry.getBeginTime());
        inv.setEndTime(entry.getEndTime());
        inv.setBatchNumber(entry.getBatchNumber());
        if (StringUtils.isEmpty(inv.getTenantId())) {
            inv.setTenantId(SecurityUtils.getCustomerId());
        }
        stkInventoryMapper.insertStkInventory(inv);
    }

    public String getBatchNumber() {
        String str = "PC";
        String createNo = FillRuleUtil.createBatchNo();
        String batchNo = str + createNo;
        return batchNo;
    }
}

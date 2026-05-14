package com.spd.department.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.spd.department.dto.StocktakingQtyAdjustDto;
import com.spd.department.vo.DeptStocktakingExportRow;
import com.spd.department.vo.StocktakingQtyMismatchVo;
import com.spd.warehouse.mapper.StkBatchMapper;
import com.spd.warehouse.mapper.StkInventoryMapper;
import com.spd.warehouse.mapper.StkIoStocktakingMapper;
import com.spd.warehouse.utils.InventoryMaterialSnapshotHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Collections;
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
    private StkIoStocktakingMapper stkIoStocktakingMapper;

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
        stkIoStocktaking.setCreateTime(new Date());
        // 制单人存用户ID（varchar），避免前端误传 nickName 到 create_by
        stkIoStocktaking.setCreateBy(SecurityUtils.getUserIdStr());
        Long opUserId = SecurityUtils.getUserId();
        if (opUserId != null) {
            stkIoStocktaking.setUserId(opUserId);
        }
        validateAndNormalizeEntries(stkIoStocktaking, null);
        // 确保warehouseId为null，表示这是科室盘点
        stkIoStocktaking.setWarehouseId(null);
        if (stkIoStocktaking.getAuditAdjustsInventory() == null) {
            stkIoStocktaking.setAuditAdjustsInventory(1);
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
        stkIoStocktaking.setUpdateTime(new Date());
        stkIoStocktaking.setUpdateBy(SecurityUtils.getUserIdStr());
        // 确保warehouseId为null，表示这是科室盘点
        stkIoStocktaking.setWarehouseId(null);
        Long parenId = stkIoStocktaking.getId();
        StkIoStocktaking oldBill = deptStocktakingMapper.selectDeptStocktakingById(parenId);
        if (oldBill != null && oldBill.getStkIoStocktakingEntryList() != null
            && !oldBill.getStkIoStocktakingEntryList().isEmpty()
            && !Objects.equals(oldBill.getDepartmentId(), stkIoStocktaking.getDepartmentId())) {
            throw new com.spd.common.exception.ServiceException("盘点单存在明细后不允许变更科室，请先清空明细再调整。");
        }
        Map<Long, StkIoStocktakingEntry> oldEntryMap = new HashMap<>();
        if (oldBill != null && oldBill.getStkIoStocktakingEntryList() != null) {
            for (StkIoStocktakingEntry old : oldBill.getStkIoStocktakingEntryList()) {
                if (old != null && old.getId() != null) {
                    oldEntryMap.put(old.getId(), old);
                }
            }
        }
        validateAndNormalizeEntries(stkIoStocktaking, oldEntryMap);
        List<StkIoStocktakingEntry> entryList = stkIoStocktaking.getStkIoStocktakingEntryList();
        List<Long> keepIds = new ArrayList<>();
        if (StringUtils.isNotNull(entryList)) {
            for (StkIoStocktakingEntry entry : entryList) {
                entry.setParenId(parenId);
                if (StringUtils.isEmpty(entry.getBatchNo())) {
                    entry.setBatchNo(getBatchNumber());
                }
                fillProfitLossFlag(entry);
                if (entry.getId() != null) {
                    applyStocktakingEntryAuditFields(entry, false);
                    deptStocktakingMapper.updateDeptStocktakingEntry(entry);
                    keepIds.add(entry.getId());
                } else {
                    applyStocktakingEntryAuditFields(entry, true);
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
        return auditDeptStocktaking(id, null);
    }

    @Transactional
    @Override
    public int auditDeptStocktaking(String id, List<StocktakingQtyAdjustDto> adjustList) {
        StkIoStocktaking stkIoStocktaking = deptStocktakingMapper.selectDeptStocktakingById(Long.valueOf(id));
        if(stkIoStocktaking == null){
            throw new com.spd.common.exception.ServiceException(String.format("科室盘点业务ID：%s，不存在!", id));
        }

        List<StkIoStocktakingEntry> stkIoStocktakingEntryList = stkIoStocktaking.getStkIoStocktakingEntryList();
        if (stkIoStocktaking.getStockType() != null && stkIoStocktaking.getStockType() == 502) {
            if (stkIoStocktakingEntryList == null || stkIoStocktakingEntryList.isEmpty()) {
                throw new com.spd.common.exception.ServiceException("盘点单无有效明细（可能保存时明细被误删），无法审核。请驳回或删除本单后重新制单并保存。");
            }
        }
        applyQtyAdjustmentsIfNeeded(stkIoStocktaking, adjustList);
        List<StocktakingQtyMismatchVo> mismatches = buildQtyMismatches(stkIoStocktaking);
        if (!mismatches.isEmpty()) {
            throw new com.spd.common.exception.ServiceException("盘点明细库存数量与当前科室账面库存不一致，请先逐条确认后再审核。");
        }

        // 科室盘点审核默认直接回写科室库存与批次，避免出现“已审核但无库存落账”。
        if (stkIoStocktaking.getAuditAdjustsInventory() == null || stkIoStocktaking.getAuditAdjustsInventory() == 0) {
            stkIoStocktaking.setAuditAdjustsInventory(1);
        }
        updateDepInventory(stkIoStocktaking, stkIoStocktakingEntryList);
        verifyProfitEntriesInventoryAndBatch(stkIoStocktaking, stkIoStocktakingEntryList);

        stkIoStocktaking.setAuditDate(new Date());
        stkIoStocktaking.setStockStatus(2);
        stkIoStocktaking.setUpdateBy(SecurityUtils.getUserIdStr());
        stkIoStocktaking.setUpdateTime(new Date());

        int res = deptStocktakingMapper.updateDeptStocktaking(stkIoStocktaking);
        return res;
    }

    /**
     * 审核后兜底校验：盘盈明细必须已生成科室库存，且库存必须关联到有效批次。
     * 不满足即抛错回滚，避免出现“已审核但库存/批次未落账”。
     */
    private void verifyProfitEntriesInventoryAndBatch(StkIoStocktaking bill, List<StkIoStocktakingEntry> entryList) {
        if (bill == null || entryList == null || bill.getStockType() == null || bill.getStockType() != 502) {
            return;
        }
        for (StkIoStocktakingEntry entry : entryList) {
            if (entry == null) {
                continue;
            }
            BigDecimal bookQty = entry.getQty() == null ? BigDecimal.ZERO : entry.getQty();
            BigDecimal stockQty = entry.getStockQty() == null ? BigDecimal.ZERO : entry.getStockQty();
            if (stockQty.compareTo(bookQty) <= 0) {
                continue;
            }
            StkDepInventory depInventory = null;
            if (StringUtils.isNotEmpty(entry.getDepInventoryId())) {
                try {
                    depInventory = stkDepInventoryMapper.selectStkDepInventoryById(Long.valueOf(entry.getDepInventoryId().trim()));
                } catch (Exception ignore) {
                    // fallback below
                }
            }
            if (depInventory == null && StringUtils.isNotEmpty(entry.getBatchNo()) && bill.getDepartmentId() != null) {
                depInventory = stkDepInventoryMapper.selectStkDepInventoryByBatchAndDeptForStocktaking(entry.getBatchNo(), bill.getDepartmentId());
            }
            if (depInventory == null || depInventory.getId() == null) {
                throw new com.spd.common.exception.ServiceException(String.format("盘盈明细审核后未生成科室库存，耗材ID：%s，批次号：%s",
                    entry.getMaterialId(), entry.getBatchNo()));
            }
            if (StringUtils.isEmpty(entry.getDepInventoryId()) && entry.getId() != null) {
                entry.setDepInventoryId(String.valueOf(depInventory.getId()));
                applyStocktakingEntryAuditFields(entry, false);
                deptStocktakingMapper.updateDeptStocktakingEntry(entry);
            }
            if (depInventory.getBatchId() == null) {
                StkBatch batch = StringUtils.isEmpty(entry.getBatchNo()) ? null : stkBatchMapper.selectByBatchNo(entry.getBatchNo());
                if (batch == null || batch.getId() == null) {
                    throw new com.spd.common.exception.ServiceException(String.format("盘盈明细审核后未生成批次对象，耗材ID：%s，批次号：%s",
                        entry.getMaterialId(), entry.getBatchNo()));
                }
                depInventory.setBatchId(batch.getId());
                depInventory.setUpdateTime(new Date());
                depInventory.setUpdateBy(SecurityUtils.getUserIdStr());
                stkDepInventoryMapper.updateStkDepInventory(depInventory);
            }
        }
    }

    @Override
    public List<StocktakingQtyMismatchVo> checkStocktakingQtyMismatch(String id) {
        StkIoStocktaking bill = deptStocktakingMapper.selectDeptStocktakingById(Long.valueOf(id));
        if (bill == null) {
            throw new com.spd.common.exception.ServiceException(String.format("科室盘点业务ID：%s，不存在!", id));
        }
        return buildQtyMismatches(bill);
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
            throw new com.spd.common.exception.ServiceException(String.format("科室盘点业务ID：%s，不存在!", id));
        }
        if(stkIoStocktaking.getStockStatus() != 1){
            throw new com.spd.common.exception.ServiceException(String.format("科室盘点业务ID：%s，状态不是未审核，无法驳回!", id));
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

        Date flowNow = new Date();
        String flowUser = SecurityUtils.getUserIdStr();
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
                    throw new com.spd.common.exception.ServiceException(String.format("耗材ID：%s，产品档案不存在!", entry.getMaterialId()));
                }
                StkBatch stkBatch = ensureStkBatchByStocktaking(stkIoStocktaking, entry, material);
                if (stkBatch != null && stkBatch.getId() != null) {
                    stkDepInventory.setBatchId(stkBatch.getId());
                    // 科室盘点只维护科室库存 stk_dep_inventory，不向 stk_inventory 写入占位行，避免「库存明细查询」出现与仓库无关的 0 库存行
                }
                stkDepInventory.setCreateTime(new Date());
                stkDepInventory.setCreateBy(SecurityUtils.getUserIdStr());

                stkDepInventoryMapper.insertStkDepInventory(stkDepInventory);
                if (stkDepInventory.getId() != null && entry.getQty() != null
                    && entry.getQty().compareTo(BigDecimal.ZERO) > 0) {
                    insertHcKsFlowDeptStocktakingLine(stkIoStocktaking, entry, stkDepInventory, entry.getReturnWarehouseId(),
                        entry.getQty(), "PY", "科室盘点期初", flowNow, flowUser);
                }
            } else if (stockType == 502) {
                //盘点：需处理账面为 0 的盘盈行（原逻辑仅处理 qty!=0 会整行跳过）
                String batchNo = entry.getBatchNo();
                if (StringUtils.isEmpty(batchNo)) {
                    throw new com.spd.common.exception.ServiceException("盘点明细缺少批次号，无法审核。");
                }
                BigDecimal bookQty = entry.getQty() != null ? entry.getQty() : BigDecimal.ZERO;
                BigDecimal stockQty = entry.getStockQty() != null ? entry.getStockQty() : BigDecimal.ZERO;
                if (bookQty.compareTo(BigDecimal.ZERO) == 0 && stockQty.compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }

                FdMaterial material = fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
                if (material == null) {
                    throw new com.spd.common.exception.ServiceException(String.format("耗材ID：%s，产品档案不存在!", entry.getMaterialId()));
                }

                Long departmentId = stkIoStocktaking.getDepartmentId();
                // 先按明细上的科室库存主键定位（与前端选行一致）；否则同批次多行时可能更新错行，表现为盘亏未扣减
                StkDepInventory depInventory = null;
                if (StringUtils.isNotEmpty(entry.getDepInventoryId())) {
                    try {
                        StkDepInventory byId = stkDepInventoryMapper.selectStkDepInventoryById(Long.valueOf(entry.getDepInventoryId().trim()));
                        if (byId != null && (byId.getDelFlag() == null || byId.getDelFlag() == 0)
                            && (departmentId == null || departmentId.equals(byId.getDepartmentId()))) {
                            depInventory = byId;
                        }
                    } catch (Exception ignore) {
                        // 回退为按批次+仓库/科室查找
                    }
                }
                Long warehouseId = entry.getReturnWarehouseId();
                if (depInventory == null && warehouseId != null) {
                    depInventory = stkDepInventoryMapper.selectStkDepInventoryOneForStocktaking(batchNo, warehouseId);
                }
                if (depInventory == null && departmentId != null) {
                    depInventory = stkDepInventoryMapper.selectStkDepInventoryByBatchAndDeptForStocktaking(batchNo, departmentId);
                }
                if (depInventory != null && depInventory.getWarehouseId() != null) {
                    entry.setDepInventoryId(String.valueOf(depInventory.getId()));
                    entry.setReturnWarehouseId(depInventory.getWarehouseId());
                    warehouseId = depInventory.getWarehouseId();
                } else {
                    warehouseId = entry.getReturnWarehouseId();
                }
                if (warehouseId == null) {
                    throw new com.spd.common.exception.ServiceException("盘点明细缺少所属仓库（且科室下未找到该批次库存），无法审核。");
                }
                if (depInventory == null) {
                    if (stockQty.compareTo(bookQty) <= 0) {
                        throw new com.spd.common.exception.ServiceException(String.format(
                            "科室库存批次号：%s，在本盘点科室下不存在；若为盘盈请保证盘点数量大于账面库存。"
                                + "若从科室库存带出明细，请勿依赖产品默认仓库，应保存后重试或重新从科室库存选择。", batchNo));
                    }
                    StkDepInventory created = insertStkDepInventoryProfitFromDeptStocktaking(stkIoStocktaking, entry, material, stockQty);
                    if (created != null && created.getId() != null && entry.getId() != null) {
                        entry.setDepInventoryId(String.valueOf(created.getId()));
                        applyStocktakingEntryAuditFields(entry, false);
                        deptStocktakingMapper.updateDeptStocktakingEntry(entry);
                    }
                    if (created != null && created.getId() != null && stockQty != null
                        && stockQty.compareTo(BigDecimal.ZERO) > 0) {
                        insertHcKsFlowDeptStocktakingLine(stkIoStocktaking, entry, created, created.getWarehouseId(),
                            stockQty, "PY", "科室盘点盘盈入库", flowNow, flowUser);
                    }
                    continue;
                }

                StkBatch stkBatch = ensureStkBatchByStocktaking(stkIoStocktaking, entry, material);
                if (stkBatch != null && stkBatch.getId() != null && depInventory.getBatchId() == null) {
                    depInventory.setBatchId(stkBatch.getId());
                }

                if (stockQty.compareTo(bookQty) == 0) {
                    depInventory.setReceiptConfirmStatus(1);
                    if (depInventory.getWarehouseId() == null) {
                        depInventory.setWarehouseId(warehouseId);
                    }
                    if (depInventory.getBatchNumber() == null) {
                        depInventory.setBatchNumber(entry.getBatchNumber());
                    }
                    stkDepInventoryMapper.updateStkDepInventory(depInventory);
                    if (entry.getId() != null) {
                        applyStocktakingEntryAuditFields(entry, false);
                        deptStocktakingMapper.updateDeptStocktakingEntry(entry);
                    }
                    continue;
                }

                BigDecimal oldQty = depInventory.getQty() != null ? depInventory.getQty() : BigDecimal.ZERO;

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
                depInventory.setUpdateTime(new Date());
                depInventory.setUpdateBy(SecurityUtils.getUserIdStr());

                stkDepInventoryMapper.updateStkDepInventory(depInventory);

                BigDecimal delta = totalQty.subtract(oldQty);
                if (delta.compareTo(BigDecimal.ZERO) != 0) {
                    String lx = delta.compareTo(BigDecimal.ZERO) > 0 ? "PY" : "PK";
                    String origin = delta.compareTo(BigDecimal.ZERO) > 0 ? "科室盘点盘盈" : "科室盘点盘亏";
                    insertHcKsFlowDeptStocktakingLine(stkIoStocktaking, entry, depInventory, warehouseId,
                        delta.abs(), lx, origin, flowNow, flowUser);
                }

                if (stockQty.compareTo(bookQty) > 0 && entry.getId() != null) {
                    Long kc = entry.getKcNo();
                    String kcStr = StringUtils.isNotEmpty(entry.getKcNoStr()) ? entry.getKcNoStr()
                        : (kc != null ? String.valueOf(kc) : null);
                    String depId = StringUtils.isNotEmpty(entry.getDepInventoryId()) ? entry.getDepInventoryId().trim()
                        : (depInventory.getId() != null ? String.valueOf(depInventory.getId()) : null);
                    stkIoStocktakingMapper.updateStocktakingEntryPostingInventoryRef(
                        entry.getId(), kc, kcStr, depId, SecurityUtils.getUserIdStr());
                }
            }
        }
    }

    /**
     * 科室盘点审核（直接改 stk_dep_inventory）时写入科室流水，与科室盈亏单审核口径一致（lx=PK/PY）。
     */
    private void insertHcKsFlowDeptStocktakingLine(StkIoStocktaking bill, StkIoStocktakingEntry entry, StkDepInventory dep,
            Long resolvedWarehouseId, BigDecimal absQty, String lx, String originBusinessType, Date now, String username) {
        if (bill == null || entry == null || dep == null || dep.getId() == null
            || absQty == null || absQty.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        if (entry.getId() == null || bill.getId() == null) {
            return;
        }
        Long flowWh = resolvedWarehouseId != null ? resolvedWarehouseId : dep.getWarehouseId();
        BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
        if (unitPrice == null) {
            unitPrice = dep.getUnitPrice();
        }
        HcKsFlow ks = new HcKsFlow();
        ks.setBillId(bill.getId());
        ks.setEntryId(entry.getId());
        ks.setDepartmentId(bill.getDepartmentId());
        ks.setWarehouseId(flowWh);
        ks.setMaterialId(entry.getMaterialId());
        ks.setBatchNo(entry.getBatchNo());
        ks.setBatchNumber(entry.getBatchNumber());
        ks.setQty(absQty);
        ks.setUnitPrice(unitPrice);
        ks.setAmt(unitPrice != null ? absQty.multiply(unitPrice) : null);
        ks.setBeginTime(entry.getBeginTime());
        ks.setEndTime(entry.getEndTime());
        ks.setMainBarcode(entry.getMainBarcode() != null ? entry.getMainBarcode() : dep.getMainBarcode());
        ks.setSubBarcode(entry.getSubBarcode() != null ? entry.getSubBarcode() : dep.getSubBarcode());
        if (StringUtils.isNotEmpty(dep.getSupplierId())) {
            ks.setSupplierId(dep.getSupplierId());
        } else if (entry.getSupplierId() != null) {
            ks.setSupplierId(String.valueOf(entry.getSupplierId()));
        }
        ks.setFactoryId(dep.getFactoryId());
        ks.setBatchId(dep.getBatchId());
        ks.setLx(lx);
        ks.setOriginBusinessType(originBusinessType);
        ks.setKcNo(dep.getId());
        ks.setFlowTime(now);
        ks.setDelFlag(0);
        ks.setCreateTime(now);
        ks.setCreateBy(username);
        InventoryMaterialSnapshotHelper.enrichHcKsFlowAfterDeptStocktaking(ks, bill, entry, flowWh, fdMaterialMapper);
        hcKsFlowMapper.insertHcKsFlow(ks);
    }

    /**
     * 科室盘点盘盈且科室尚无该批次库存行时，新增 stk_dep_inventory（数量为盘点实盘）
     */
    private StkDepInventory insertStkDepInventoryProfitFromDeptStocktaking(StkIoStocktaking stkIoStocktaking, StkIoStocktakingEntry entry,
            FdMaterial material, BigDecimal finalQty) {
        StkBatch stkBatch = ensureStkBatchByStocktaking(stkIoStocktaking, entry, material);
        StkDepInventory stkDepInventory = new StkDepInventory();
        stkDepInventory.setBatchNo(entry.getBatchNo());
        stkDepInventory.setMaterialId(entry.getMaterialId());
        stkDepInventory.setDepartmentId(stkIoStocktaking.getDepartmentId());
        if (entry.getReturnWarehouseId() == null) {
            throw new com.spd.common.exception.ServiceException("盘盈明细缺少归属仓库。");
        }
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
        }
        stkDepInventory.setCreateTime(new Date());
        stkDepInventory.setCreateBy(SecurityUtils.getUserIdStr());
        stkDepInventoryMapper.insertStkDepInventory(stkDepInventory);
        return stkDepInventory;
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
                fillProfitLossFlag(stkIoStocktakingEntry);
                applyStocktakingEntryAuditFields(stkIoStocktakingEntry, true);
                list.add(stkIoStocktakingEntry);
            }
            if (list.size() > 0)
            {
                deptStocktakingMapper.batchDeptStocktakingEntry(list);
            }
        }
    }

    private void validateAndNormalizeEntries(StkIoStocktaking bill, Map<Long, StkIoStocktakingEntry> oldEntryMap) {
        if (bill == null || bill.getStkIoStocktakingEntryList() == null) {
            return;
        }
        Set<String> depInventoryIdSeen = new HashSet<>();
        for (StkIoStocktakingEntry entry : bill.getStkIoStocktakingEntryList()) {
            if (entry == null) {
                continue;
            }
            if (StringUtils.isNotEmpty(bill.getStockNo())) {
                entry.setStockNo(bill.getStockNo());
            }
            if (entry.getMaterialId() == null) {
                throw new com.spd.common.exception.ServiceException("盘点明细缺少耗材，无法保存。");
            }
            FdMaterial material = fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
            if (material == null) {
                throw new com.spd.common.exception.ServiceException(String.format("耗材ID：%s，产品档案不存在!", entry.getMaterialId()));
            }
            // 供应商：已关联科室库存的明细（含盘点初始化带出）优先取 stk_dep_inventory.supplier_id；无关联时再取产品档案。
            // 仅「未关联科室库存」的新增盘盈行才强制要求产品档案维护供应商（否则无法生成新批次等）。
            Long supplierFromDep = null;
            if (StringUtils.isNotEmpty(entry.getDepInventoryId())) {
                try {
                    StkDepInventory depInv = stkDepInventoryMapper.selectStkDepInventoryById(
                        Long.valueOf(entry.getDepInventoryId().trim()));
                    if (depInv != null && StringUtils.isNotEmpty(depInv.getSupplierId())) {
                        supplierFromDep = parseLongSupplierId(depInv.getSupplierId());
                    }
                } catch (Exception ignore) {
                    // 非法 depInventoryId 时忽略，后续仍可按批次等解析
                }
            }
            Long supplierToSet = supplierFromDep != null ? supplierFromDep : material.getSupplierId();
            if (supplierToSet != null) {
                entry.setSupplierId(supplierToSet);
            }
            if (StringUtils.isEmpty(entry.getDepInventoryId()) && entry.getSupplierId() == null) {
                throw new com.spd.common.exception.ServiceException(String.format(
                    "耗材[%s]：产品档案未维护供应商时，请在新增盘盈明细中选择供应商后再保存。", material.getName()));
            }

            if (entry.getStockQty() == null) {
                entry.setStockQty(BigDecimal.ZERO);
            }
            if (entry.getBeginTime() != null && entry.getEndTime() != null
                && entry.getEndTime().before(entry.getBeginTime())) {
                throw new com.spd.common.exception.ServiceException("盘点明细有效期不能早于生产日期。");
            }
            BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
            entry.setAmt(unitPrice == null ? BigDecimal.ZERO : entry.getStockQty().multiply(unitPrice));
            fillProfitLossFlag(entry);

            // 来源科室库存的明细只允许盘亏/平，不允许盘盈
            if (StringUtils.isNotEmpty(entry.getDepInventoryId())) {
                String depInventoryIdKey = String.valueOf(entry.getDepInventoryId()).trim();
                if (depInventoryIdSeen.contains(depInventoryIdKey)) {
                    throw new com.spd.common.exception.ServiceException("同一科室库存明细不允许重复加入盘点单。");
                }
                depInventoryIdSeen.add(depInventoryIdKey);
                BigDecimal bookQty = entry.getQty() == null ? BigDecimal.ZERO : entry.getQty();
                if (entry.getStockQty().compareTo(bookQty) > 0) {
                    throw new com.spd.common.exception.ServiceException("来源于科室库存的明细仅允许盘亏，不允许盘盈。");
                }
            } else {
                // 衡水市第三人民医院租户：盘盈明细归属仓库默认 10（与前端一致，退库目标仓）
                if (entry.getReturnWarehouseId() == null && "hengsui-third-001".equals(SecurityUtils.getCustomerId())) {
                    entry.setReturnWarehouseId(10L);
                }
                // 新增盘盈行必须录入归属仓库、批号、有效期（退库退货依赖明细目标仓库）
                if (entry.getId() == null) {
                    if (entry.getReturnWarehouseId() == null || StringUtils.isEmpty(entry.getBatchNumber())
                        || entry.getEndTime() == null) {
                        throw new com.spd.common.exception.ServiceException("新增盘盈明细必须录入归属仓库、批号、有效期。");
                    }
                }
            }

            fillStocktakingEntryRefStrings(bill, entry);

            if (oldEntryMap != null && entry.getId() != null) {
                StkIoStocktakingEntry old = oldEntryMap.get(entry.getId());
                if (old == null) {
                    throw new com.spd.common.exception.ServiceException("存在无法识别的历史明细，禁止保存。");
                }
                // 除盘点数量外，其余字段不允许编辑
                assertNoChange("耗材", old.getMaterialId(), entry.getMaterialId());
                assertNoChange("库存数量", old.getQty(), entry.getQty());
                assertNoChange("单价", old.getUnitPrice(), entry.getUnitPrice());
                assertNoChange("批次号", old.getBatchNo(), entry.getBatchNo());
                assertNoChange("批号", old.getBatchNumber(), entry.getBatchNumber());
                assertNoChange("生产日期", old.getBeginTime(), entry.getBeginTime());
                assertNoChange("有效期", old.getEndTime(), entry.getEndTime());
                assertNoChange("所属仓库", old.getReturnWarehouseId(), entry.getReturnWarehouseId());
                assertNoChange("备注", old.getRemark(), entry.getRemark());
                assertNoChange("科室库存ID", old.getDepInventoryId(), entry.getDepInventoryId());
            }
        }
    }

    /**
     * 校验历史明细字段未被篡改。注意：DB/MyBatis 多为 {@link BigDecimal}，前端 JSON 经 Jackson 后常见 {@link Integer}/{@link Long}/{@link Double}，
     * 若仍用 {@link Objects#equals} 会把「0」与「0.00」或 {@link BigDecimal} 与 {@link Integer} 判为不等，误报「库存数量不允许编辑」。
     */
    private void assertNoChange(String fieldName, Object oldVal, Object newVal) {
        if (Objects.equals(oldVal, newVal)) {
            return;
        }
        BigDecimal bdOld = toBigDecimalForAudit(oldVal);
        BigDecimal bdNew = toBigDecimalForAudit(newVal);
        if (bdOld != null && bdNew != null && bdOld.compareTo(bdNew) == 0) {
            return;
        }
        // 更新请求体未带「库存数量」「单价」时视为未修改（与旧值一致）
        if (newVal == null && bdOld != null && ("库存数量".equals(fieldName) || "单价".equals(fieldName))) {
            return;
        }
        if (oldVal instanceof Date && newVal instanceof Date
            && ((Date) oldVal).getTime() == ((Date) newVal).getTime()) {
            return;
        }
        Object left = oldVal;
        Object right = newVal;
        if (oldVal instanceof String || newVal instanceof String) {
            left = oldVal == null ? "" : String.valueOf(oldVal).trim();
            right = newVal == null ? "" : String.valueOf(newVal).trim();
        }
        if (!Objects.equals(left, right)) {
            throw new com.spd.common.exception.ServiceException("盘点明细字段[" + fieldName + "]不允许编辑。");
        }
    }

    /** 可参与数值比对的类型转为 BigDecimal；无法解析或非数值类型返回 null */
    private static BigDecimal toBigDecimalForAudit(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof BigDecimal) {
            return (BigDecimal) o;
        }
        if (o instanceof Number) {
            return new BigDecimal(((Number) o).toString());
        }
        if (o instanceof String) {
            String s = ((String) o).trim();
            if (s.isEmpty()) {
                return null;
            }
            try {
                return new BigDecimal(s);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /** 科室库存 supplier_id 为 varchar，解析为 Long 供盘点明细 supplierId 使用 */
    private static Long parseLongSupplierId(String raw) {
        if (StringUtils.isEmpty(raw)) {
            return null;
        }
        String s = raw.trim();
        if (s.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void applyQtyAdjustmentsIfNeeded(StkIoStocktaking bill, List<StocktakingQtyAdjustDto> adjustList) {
        if (bill == null || adjustList == null || adjustList.isEmpty() || bill.getStkIoStocktakingEntryList() == null) {
            return;
        }
        Map<Long, StocktakingQtyAdjustDto> adjustMap = adjustList.stream()
            .filter(a -> a != null && a.getEntryId() != null)
            .collect(Collectors.toMap(StocktakingQtyAdjustDto::getEntryId, a -> a, (a, b) -> b));
        if (adjustMap.isEmpty()) {
            return;
        }
        for (StkIoStocktakingEntry entry : bill.getStkIoStocktakingEntryList()) {
            if (entry == null || entry.getId() == null) {
                continue;
            }
            StocktakingQtyAdjustDto adjust = adjustMap.get(entry.getId());
            if (adjust == null) {
                continue;
            }
            BigDecimal currentQty = queryCurrentDepInventoryQty(bill, entry);
            if (currentQty == null) {
                currentQty = BigDecimal.ZERO;
            }
            entry.setQty(currentQty);
            entry.setStockQty(adjust.getStockQty() == null ? currentQty : adjust.getStockQty());
            BigDecimal unitPrice = entry.getUnitPrice() != null ? entry.getUnitPrice() : entry.getPrice();
            entry.setAmt(unitPrice == null ? BigDecimal.ZERO : entry.getStockQty().multiply(unitPrice));
            fillProfitLossFlag(entry);
            applyStocktakingEntryAuditFields(entry, false);
            deptStocktakingMapper.updateDeptStocktakingEntry(entry);
        }
    }

    /**
     * 盘点明细审计字段：新增行写 create/update；修改行仅刷新 update。
     */
    private void applyStocktakingEntryAuditFields(StkIoStocktakingEntry entry, boolean newLine) {
        if (entry == null) {
            return;
        }
        Date now = new Date();
        String user = SecurityUtils.getUserIdStr();
        if (newLine) {
            entry.setCreateTime(now);
            entry.setCreateBy(user);
        }
        entry.setUpdateTime(now);
        entry.setUpdateBy(user);
    }

    private void fillProfitLossFlag(StkIoStocktakingEntry entry) {
        if (entry == null) {
            return;
        }
        BigDecimal bookQty = entry.getQty() == null ? BigDecimal.ZERO : entry.getQty();
        BigDecimal stockQty = entry.getStockQty() == null ? BigDecimal.ZERO : entry.getStockQty();
        int cmp = stockQty.compareTo(bookQty);
        if (cmp > 0) {
            entry.setProfitLossFlag("PROFIT");
        } else if (cmp < 0) {
            entry.setProfitLossFlag("LOSS");
        } else {
            entry.setProfitLossFlag("EQUAL");
        }
    }

    /** 盘点明细行 varchar 快照：科室/仓库库存id、仓库/科室/供应商 */
    private void fillStocktakingEntryRefStrings(StkIoStocktaking bill, StkIoStocktakingEntry entry) {
        if (entry == null) {
            return;
        }
        if (entry.getKcNo() != null) {
            entry.setKcNoStr(String.valueOf(entry.getKcNo()));
        }
        if (StringUtils.isNotEmpty(entry.getDepInventoryId())) {
            entry.setDepInventoryId(String.valueOf(entry.getDepInventoryId()).trim());
        }
        Long wh = entry.getReturnWarehouseId() != null ? entry.getReturnWarehouseId() : bill.getWarehouseId();
        entry.setWarehouseIdStr(wh != null ? String.valueOf(wh) : null);
        entry.setDepartmentIdStr(bill.getDepartmentId() != null ? String.valueOf(bill.getDepartmentId()) : null);
        entry.setSupplierIdStr(entry.getSupplierId() != null ? String.valueOf(entry.getSupplierId()) : null);
    }

    private List<StocktakingQtyMismatchVo> buildQtyMismatches(StkIoStocktaking bill) {
        List<StocktakingQtyMismatchVo> out = new ArrayList<>();
        if (bill == null || bill.getStkIoStocktakingEntryList() == null) {
            return out;
        }
        for (StkIoStocktakingEntry entry : bill.getStkIoStocktakingEntryList()) {
            if (entry == null || entry.getId() == null) {
                continue;
            }
            BigDecimal detailQty = entry.getQty() == null ? BigDecimal.ZERO : entry.getQty();
            BigDecimal currentQty = queryCurrentDepInventoryQty(bill, entry);
            if (currentQty == null) {
                currentQty = BigDecimal.ZERO;
            }
            if (detailQty.compareTo(currentQty) != 0) {
                StocktakingQtyMismatchVo r = new StocktakingQtyMismatchVo();
                r.setEntryId(entry.getId());
                r.setMaterialName(entry.getMaterial() != null ? entry.getMaterial().getName() : null);
                r.setBatchNo(entry.getBatchNo());
                r.setDetailQty(detailQty);
                r.setCurrentQty(currentQty);
                r.setStockQty(entry.getStockQty() == null ? BigDecimal.ZERO : entry.getStockQty());
                out.add(r);
            }
        }
        return out;
    }

    private BigDecimal queryCurrentDepInventoryQty(StkIoStocktaking bill, StkIoStocktakingEntry entry) {
        if (entry == null) {
            return BigDecimal.ZERO;
        }
        StkDepInventory depInventory = null;
        if (StringUtils.isNotEmpty(entry.getDepInventoryId())) {
            try {
                depInventory = stkDepInventoryMapper.selectStkDepInventoryById(Long.valueOf(entry.getDepInventoryId().trim()));
            } catch (Exception ignore) {
                // ignore and fallback by batch+dept
            }
        }
        if (depInventory == null && StringUtils.isNotEmpty(entry.getBatchNo()) && bill != null && bill.getDepartmentId() != null) {
            depInventory = stkDepInventoryMapper.selectStkDepInventoryByBatchAndDeptForStocktaking(entry.getBatchNo(), bill.getDepartmentId());
        }
        return depInventory == null || depInventory.getQty() == null ? BigDecimal.ZERO : depInventory.getQty();
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
            if (inventory.getId() != null) {
                entry.setKcNo(inventory.getId());
                entry.setKcNoStr(String.valueOf(inventory.getId()));
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
        if (inv.getId() != null) {
            entry.setKcNo(inv.getId());
            entry.setKcNoStr(String.valueOf(inv.getId()));
        }
    }

    public String getBatchNumber() {
        String str = "PC";
        String createNo = FillRuleUtil.createBatchNo();
        String batchNo = str + createNo;
        return batchNo;
    }

    @Transactional
    @Override
    public int appendDeptStocktakingEntries(Long billId, List<StkIoStocktakingEntry> newEntries) {
        if (billId == null || newEntries == null || newEntries.isEmpty()) {
            return 0;
        }
        for (StkIoStocktakingEntry e : newEntries) {
            if (e != null && e.getId() != null) {
                throw new com.spd.common.exception.ServiceException("追加明细必须为未落库的新行（不能带明细 id）。");
            }
        }
        StkIoStocktaking head = deptStocktakingMapper.selectDeptStocktakingById(billId);
        if (head == null) {
            throw new com.spd.common.exception.ServiceException("盘点单不存在或无权访问。");
        }
        if (head.getStockType() == null || head.getStockType() != 502) {
            throw new com.spd.common.exception.ServiceException("仅支持科室盘点单追加明细。");
        }
        if (head.getStockStatus() != null && head.getStockStatus() == 2) {
            throw new com.spd.common.exception.ServiceException("已审核的盘点单不可追加明细。");
        }
        List<StkIoStocktakingEntry> existing = head.getStkIoStocktakingEntryList();
        if (existing == null) {
            existing = Collections.emptyList();
        }
        Set<String> usedDepKeys = new HashSet<>();
        for (StkIoStocktakingEntry ex : existing) {
            if (ex == null) {
                continue;
            }
            if (StringUtils.isNotEmpty(ex.getDepInventoryId())) {
                usedDepKeys.add(String.valueOf(ex.getDepInventoryId()).trim());
            }
        }
        List<StkIoStocktakingEntry> clean = new ArrayList<>();
        for (StkIoStocktakingEntry e : newEntries) {
            if (e == null) {
                continue;
            }
            if (StringUtils.isNotEmpty(e.getDepInventoryId())) {
                String dk = e.getDepInventoryId().trim();
                if (usedDepKeys.contains(dk)) {
                    throw new com.spd.common.exception.ServiceException("同一科室库存明细不允许重复加入盘点单。");
                }
                usedDepKeys.add(dk);
            }
            clean.add(e);
        }
        if (clean.isEmpty()) {
            return 0;
        }
        for (StkIoStocktakingEntry entry : clean) {
            if (StringUtils.isEmpty(entry.getBatchNo())) {
                entry.setBatchNo(getBatchNumber());
            }
        }
        StkIoStocktaking slice = new StkIoStocktaking();
        slice.setId(head.getId());
        slice.setStockNo(head.getStockNo());
        slice.setDepartmentId(head.getDepartmentId());
        slice.setWarehouseId(null);
        slice.setStockType(502);
        slice.setTenantId(head.getTenantId());
        slice.setStkIoStocktakingEntryList(clean);
        validateAndNormalizeEntries(slice, null);
        for (StkIoStocktakingEntry entry : clean) {
            entry.setParenId(billId);
            entry.setStockNo(head.getStockNo());
            if (StringUtils.isEmpty(entry.getBatchNo())) {
                entry.setBatchNo(getBatchNumber());
            }
            entry.setDelFlag(0);
            fillProfitLossFlag(entry);
            applyStocktakingEntryAuditFields(entry, true);
            deptStocktakingMapper.insertDeptStocktakingEntrySingle(entry);
        }
        head.setUpdateTime(new Date());
        head.setUpdateBy(SecurityUtils.getUserIdStr());
        deptStocktakingMapper.updateDeptStocktaking(head);
        return clean.size();
    }

    @Override
    public int updateDeptStocktakingEntryCountedFlag(Long entryId, Integer countedFlag)
    {
        if (entryId == null || countedFlag == null)
        {
            throw new com.spd.common.exception.ServiceException("参数错误。");
        }
        if (countedFlag != 0 && countedFlag != 1)
        {
            throw new com.spd.common.exception.ServiceException("已盘标志只能为 0 或 1。");
        }
        int n = stkIoStocktakingMapper.updateStocktakingEntryCountedFlag(entryId, countedFlag, 502, SecurityUtils.getUserIdStr());
        if (n == 0)
        {
            throw new com.spd.common.exception.ServiceException("未找到可更新的明细（可能已审核或不属于科室盘点）。");
        }
        return n;
    }
}

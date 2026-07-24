package com.spd.caigou.forecast.service.impl;

import com.spd.caigou.domain.PurchasePlan;
import com.spd.caigou.domain.PurchasePlanEntry;
import com.spd.caigou.forecast.domain.ForecastCalcRequest;
import com.spd.caigou.forecast.domain.ForecastEntryUpdateBody;
import com.spd.caigou.forecast.domain.ForecastFixedMaterialRow;
import com.spd.caigou.forecast.domain.ForecastGeneratePlanBody;
import com.spd.caigou.forecast.domain.ForecastMaterialQtyRow;
import com.spd.caigou.forecast.domain.PurchaseForecastEntry;
import com.spd.caigou.forecast.domain.PurchaseForecastTask;
import com.spd.caigou.forecast.mapper.ForecastReplenishMapper;
import com.spd.caigou.forecast.service.IForecastReplenishService;
import com.spd.caigou.service.IPurchasePlanService;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.rule.FillRuleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 采购预测补货：定数范围 + 日均净出库 + 提前期 → 建议量 → 草稿采购计划
 */
@Service
public class ForecastReplenishServiceImpl implements IForecastReplenishService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    @Autowired
    private ForecastReplenishMapper forecastReplenishMapper;

    @Autowired
    private IPurchasePlanService purchasePlanService;

    @Override
    public List<PurchaseForecastTask> selectTaskList(PurchaseForecastTask query) {
        return forecastReplenishMapper.selectTaskList(query);
    }

    @Override
    public PurchaseForecastTask selectTaskDetail(Long id) {
        if (id == null) {
            throw new ServiceException("任务ID不能为空");
        }
        PurchaseForecastTask task = forecastReplenishMapper.selectTaskById(id);
        if (task == null) {
            throw new ServiceException("预测补货任务不存在");
        }
        SecurityUtils.ensureTenantAccess(task.getTenantId());
        task.setEntryList(forecastReplenishMapper.selectEntryListByTaskId(id));
        return task;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurchaseForecastTask calc(ForecastCalcRequest request) {
        if (request == null || request.getWarehouseId() == null) {
            throw new ServiceException("请选择仓库");
        }
        int calcDays = request.getCalcDays() != null && request.getCalcDays() > 0 ? request.getCalcDays() : 30;
        int leadTimeDays = request.getLeadTimeDays() != null && request.getLeadTimeDays() > 0
            ? request.getLeadTimeDays() : 7;
        int safetyDays = request.getSafetyDays() != null && request.getSafetyDays() >= 0
            ? request.getSafetyDays() : 3;
        if (calcDays > 365) {
            throw new ServiceException("回顾天数不能超过365天");
        }

        Long warehouseId = request.getWarehouseId();
        String isGz = StringUtils.isNotEmpty(request.getIsGz()) ? request.getIsGz().trim() : null;

        List<ForecastFixedMaterialRow> fixedRows =
            forecastReplenishMapper.selectEnabledFixedMaterials(warehouseId, isGz);
        if (fixedRows == null || fixedRows.isEmpty()) {
            throw new ServiceException("该仓库没有启用中的定数监测物料，无法计算");
        }

        List<Long> materialIds = fixedRows.stream()
            .map(ForecastFixedMaterialRow::getMaterialId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

        Date endDate = DateUtils.getNowDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        cal.add(Calendar.DAY_OF_MONTH, -(calcDays - 1));
        Date beginDate = cal.getTime();

        Map<Long, BigDecimal> consumeMap = toQtyMap(
            forecastReplenishMapper.selectNetOutboundQty(warehouseId, beginDate, endDate, materialIds));
        Map<Long, BigDecimal> transitMap = toQtyMap(
            forecastReplenishMapper.selectInTransitPlanQty(warehouseId, materialIds));
        Map<Long, BigDecimal> stockMap = purchasePlanService.mapMaterialStockQtyByWarehouse(warehouseId, materialIds);

        String tenantId = SecurityUtils.requiredScopedTenantIdForSql();
        String userId = SecurityUtils.getUserIdStr();
        Date now = DateUtils.getNowDate();

        List<PurchaseForecastEntry> entries = new ArrayList<>();
        BigDecimal calcDaysBd = BigDecimal.valueOf(calcDays);
        BigDecimal leadBd = BigDecimal.valueOf(leadTimeDays);
        BigDecimal safetyBd = BigDecimal.valueOf(safetyDays);

        for (ForecastFixedMaterialRow row : fixedRows) {
            Long materialId = row.getMaterialId();
            if (materialId == null) {
                continue;
            }
            BigDecimal stock = nvl(stockMap.get(materialId));
            BigDecimal transit = nvl(transitMap.get(materialId));
            BigDecimal netConsume = nvl(consumeMap.get(materialId));
            if (netConsume.compareTo(ZERO) < 0) {
                netConsume = ZERO;
            }
            BigDecimal avgDaily = netConsume.divide(calcDaysBd, 6, RoundingMode.HALF_UP);
            BigDecimal lower = row.getLowerLimit() != null
                ? BigDecimal.valueOf(row.getLowerLimit()) : ZERO;
            BigDecimal upper = row.getUpperLimit() != null
                ? BigDecimal.valueOf(row.getUpperLimit()) : ZERO;

            BigDecimal safetyStock = lower.compareTo(ZERO) > 0
                ? lower
                : avgDaily.multiply(safetyBd).setScale(6, RoundingMode.HALF_UP);
            BigDecimal rop = avgDaily.multiply(leadBd).add(safetyStock).setScale(6, RoundingMode.HALF_UP);

            BigDecimal available = stock.add(transit);
            boolean needByRop = available.compareTo(rop) <= 0;
            boolean needByLower = lower.compareTo(ZERO) > 0 && stock.compareTo(lower) < 0;
            if (!needByRop && !needByLower) {
                continue;
            }

            BigDecimal target;
            String formula;
            if (upper.compareTo(ZERO) > 0) {
                target = upper;
                formula = "目标=上限";
            } else {
                target = rop.add(avgDaily.multiply(leadBd)).setScale(6, RoundingMode.HALF_UP);
                formula = "目标=ROP+日均×提前期";
            }

            BigDecimal suggest = target.subtract(available);
            if (suggest.compareTo(ZERO) < 0) {
                suggest = ZERO;
            }

            if (avgDaily.compareTo(ZERO) == 0 && needByLower) {
                if (upper.compareTo(ZERO) > 0) {
                    suggest = upper.subtract(available).max(ZERO);
                    formula = "零消耗：按上限补货";
                } else {
                    suggest = lower.subtract(available).max(ZERO);
                    formula = "零消耗：按下限缺口补货";
                }
            }

            suggest = ceilToMinPackage(suggest, row.getMinPackageQty());
            if (suggest.compareTo(ZERO) <= 0) {
                continue;
            }

            PurchaseForecastEntry entry = new PurchaseForecastEntry();
            entry.setWarehouseId(warehouseId);
            entry.setMaterialId(materialId);
            entry.setSupplierId(row.getSupplierId());
            entry.setStockQty(stock);
            entry.setInTransitQty(transit);
            entry.setAvgDailyQty(avgDaily);
            entry.setLowerLimit(lower);
            entry.setUpperLimit(upper);
            entry.setRopQty(rop);
            entry.setSuggestQty(suggest);
            entry.setConfirmQty(suggest);
            entry.setSelected("1");
            entry.setPrice(row.getPrice());
            entry.setFormulaRemark(String.format(
                "%s；日均=%s 提前期=%d 安全=%s ROP=%s 库存=%s 在途=%s",
                formula,
                avgDaily.stripTrailingZeros().toPlainString(),
                leadTimeDays,
                safetyStock.stripTrailingZeros().toPlainString(),
                rop.stripTrailingZeros().toPlainString(),
                stock.stripTrailingZeros().toPlainString(),
                transit.stripTrailingZeros().toPlainString()));
            entry.setTenantId(tenantId);
            entry.setCreateBy(userId);
            entry.setCreateTime(now);
            entries.add(entry);
        }

        if (entries.isEmpty()) {
            throw new ServiceException("计算结果无需补货（现存量+在途均高于补货点）");
        }

        PurchaseForecastTask task = new PurchaseForecastTask();
        task.setTaskNo(nextTaskNo());
        task.setWarehouseId(warehouseId);
        task.setIsGz(isGz);
        task.setCalcDays(calcDays);
        task.setLeadTimeDays(leadTimeDays);
        task.setSafetyDays(safetyDays);
        task.setStatus("0");
        task.setTenantId(tenantId);
        task.setCreateBy(userId);
        task.setCreateTime(now);
        forecastReplenishMapper.insertTask(task);

        for (PurchaseForecastEntry entry : entries) {
            entry.setTaskId(task.getId());
        }
        forecastReplenishMapper.batchInsertEntry(entries);
        return selectTaskDetail(task.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEntries(ForecastEntryUpdateBody body) {
        if (body == null || body.getEntries() == null || body.getEntries().isEmpty()) {
            throw new ServiceException("无待更新明细");
        }
        String userId = SecurityUtils.getUserIdStr();
        Date now = DateUtils.getNowDate();
        for (ForecastEntryUpdateBody.Item item : body.getEntries()) {
            if (item == null || item.getId() == null) {
                continue;
            }
            PurchaseForecastEntry existing = forecastReplenishMapper.selectEntryById(item.getId());
            if (existing == null) {
                throw new ServiceException("建议明细不存在：" + item.getId());
            }
            PurchaseForecastEntry upd = new PurchaseForecastEntry();
            upd.setId(item.getId());
            if (item.getConfirmQty() != null) {
                if (item.getConfirmQty().compareTo(ZERO) < 0) {
                    throw new ServiceException("确认数量不能为负数");
                }
                upd.setConfirmQty(item.getConfirmQty());
            }
            if (item.getSelected() != null) {
                upd.setSelected("1".equals(item.getSelected()) ? "1" : "0");
            }
            upd.setUpdateBy(userId);
            upd.setUpdateTime(now);
            forecastReplenishMapper.updateEntryConfirm(upd);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> generatePlan(ForecastGeneratePlanBody body) {
        if (body == null || body.getTaskId() == null) {
            throw new ServiceException("任务ID不能为空");
        }
        PurchaseForecastTask task = selectTaskDetail(body.getTaskId());
        List<PurchaseForecastEntry> all = task.getEntryList();
        if (all == null || all.isEmpty()) {
            throw new ServiceException("任务无建议明细");
        }

        Set<Long> filterIds = null;
        if (body.getEntryIds() != null && !body.getEntryIds().isEmpty()) {
            filterIds = new HashSet<>(body.getEntryIds());
        }

        List<PurchaseForecastEntry> selected = new ArrayList<>();
        List<String> skippedNoSupplier = new ArrayList<>();
        for (PurchaseForecastEntry e : all) {
            if (filterIds != null && !filterIds.contains(e.getId())) {
                continue;
            }
            if (filterIds == null && !"1".equals(e.getSelected())) {
                continue;
            }
            BigDecimal qty = e.getConfirmQty() != null ? e.getConfirmQty() : e.getSuggestQty();
            if (qty == null || qty.compareTo(ZERO) <= 0) {
                continue;
            }
            if (e.getSupplierId() == null) {
                String name = StringUtils.isNotEmpty(e.getMaterialName()) ? e.getMaterialName() : String.valueOf(e.getMaterialId());
                skippedNoSupplier.add(name);
                continue;
            }
            e.setConfirmQty(qty);
            selected.add(e);
        }
        if (selected.isEmpty()) {
            String tip = skippedNoSupplier.isEmpty()
                ? "没有可生成的明细（请勾选并填写确认数量，且需有供应商）"
                : "勾选明细均无供应商，已跳过：" + String.join("、", skippedNoSupplier);
            throw new ServiceException(tip);
        }

        // 按高低值拆单：任务指定则一张；否则按明细档案 is_gz 分组（缺省按低值）
        Map<String, List<PurchaseForecastEntry>> byGz = new LinkedHashMap<>();
        if (StringUtils.isNotEmpty(task.getIsGz())) {
            byGz.put(task.getIsGz(), selected);
        } else {
            for (PurchaseForecastEntry e : selected) {
                String gz = "1".equals(e.getIsGz()) ? "1" : "2";
                byGz.computeIfAbsent(gz, k -> new ArrayList<>()).add(e);
            }
        }

        List<String> planNos = new ArrayList<>();
        List<Long> planIds = new ArrayList<>();
        String userId = SecurityUtils.getUserIdStr();

        for (Map.Entry<String, List<PurchaseForecastEntry>> group : byGz.entrySet()) {
            List<PurchasePlanEntry> planEntries = new ArrayList<>();
            BigDecimal totalAmt = ZERO;
            for (PurchaseForecastEntry e : group.getValue()) {
                PurchasePlanEntry pe = new PurchasePlanEntry();
                pe.setMaterialId(e.getMaterialId());
                pe.setSupplierId(e.getSupplierId());
                pe.setQty(e.getConfirmQty());
                pe.setPrice(e.getPrice() != null ? e.getPrice() : ZERO);
                pe.setAmt(pe.getQty().multiply(pe.getPrice()).setScale(6, RoundingMode.HALF_UP));
                pe.setSpeci(e.getSpeci());
                pe.setModel(e.getModel());
                pe.setRemark("预测补货 " + task.getTaskNo());
                planEntries.add(pe);
                totalAmt = totalAmt.add(pe.getAmt());
            }
            PurchasePlan plan = new PurchasePlan();
            plan.setWarehouseId(task.getWarehouseId());
            plan.setIsGz(group.getKey());
            plan.setPlanDate(DateUtils.getNowDate());
            plan.setPlanStatus("0");
            plan.setPlanEntryMode("1");
            plan.setProPerson(userId);
            plan.setTotalAmount(totalAmt);
            plan.setRemark("来源预测补货任务号：" + task.getTaskNo());
            plan.setPurchasePlanEntryList(planEntries);
            purchasePlanService.insertPurchasePlan(plan);
            planIds.add(plan.getId());
            planNos.add(plan.getPlanNo());
        }

        PurchaseForecastTask upd = new PurchaseForecastTask();
        upd.setId(task.getId());
        upd.setStatus("1");
        upd.setGeneratedPlanIds(planIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
        upd.setGeneratedPlanNos(String.join(",", planNos));
        upd.setUpdateBy(userId);
        upd.setUpdateTime(DateUtils.getNowDate());
        forecastReplenishMapper.updateTask(upd);

        Map<String, Object> result = new HashMap<>();
        result.put("planIds", planIds);
        result.put("planNos", planNos);
        result.put("skippedNoSupplier", skippedNoSupplier);
        result.put("generatedCount", selected.size() - 0);
        result.put("message", buildGenerateMessage(planNos, skippedNoSupplier));
        return result;
    }

    private String buildGenerateMessage(List<String> planNos, List<String> skipped) {
        StringBuilder sb = new StringBuilder();
        sb.append("已生成采购计划：").append(String.join("、", planNos));
        if (skipped != null && !skipped.isEmpty()) {
            sb.append("；以下物料无供应商已跳过：").append(String.join("、", skipped));
        }
        return sb.toString();
    }

    private String nextTaskNo() {
        String date = FillRuleUtil.getDateNum();
        String maxNum = forecastReplenishMapper.selectMaxTaskNo(date);
        return FillRuleUtil.getNumber("YCB", maxNum, date);
    }

    private Map<Long, BigDecimal> toQtyMap(List<ForecastMaterialQtyRow> rows) {
        Map<Long, BigDecimal> map = new HashMap<>();
        if (rows == null) {
            return map;
        }
        for (ForecastMaterialQtyRow row : rows) {
            if (row != null && row.getMaterialId() != null) {
                map.put(row.getMaterialId(), nvl(row.getQty()));
            }
        }
        return map;
    }

    private static BigDecimal nvl(BigDecimal v) {
        return v != null ? v : ZERO;
    }

    /** 向上取整到最小包装倍数；未维护最小包装则保留原数量（最多 6 位） */
    static BigDecimal ceilToMinPackage(BigDecimal qty, BigDecimal minPackageQty) {
        if (qty == null || qty.compareTo(ZERO) <= 0) {
            return ZERO;
        }
        if (minPackageQty == null || minPackageQty.compareTo(ZERO) <= 0) {
            return qty.setScale(6, RoundingMode.HALF_UP);
        }
        BigDecimal[] div = qty.divideAndRemainder(minPackageQty);
        if (div[1].compareTo(ZERO) == 0) {
            return qty.setScale(6, RoundingMode.HALF_UP);
        }
        return div[0].add(BigDecimal.ONE).multiply(minPackageQty).setScale(6, RoundingMode.HALF_UP);
    }
}

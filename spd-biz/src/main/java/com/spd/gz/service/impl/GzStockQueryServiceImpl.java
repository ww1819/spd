package com.spd.gz.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.gz.domain.GzDepInventory;
import com.spd.gz.domain.GzDepotInventory;
import com.spd.gz.domain.GzShipmentEntry;
import com.spd.gz.domain.GzStockQueryParam;
import com.spd.gz.domain.vo.GzDepInventoryCodeRepairVo;
import com.spd.gz.domain.vo.GzDepotInventoryTraceResultVo;
import com.spd.gz.domain.vo.GzDepotInventoryTraceVo;
import com.spd.gz.domain.vo.GzStockQueryEntryVo;
import com.spd.gz.mapper.GzDepInventoryMapper;
import com.spd.gz.mapper.GzDepotInventoryMapper;
import com.spd.gz.mapper.GzShipmentMapper;
import com.spd.gz.mapper.GzStockQueryMapper;
import com.spd.gz.service.IGzStockQueryService;

@Service
public class GzStockQueryServiceImpl implements IGzStockQueryService
{
    @Autowired
    private GzStockQueryMapper gzStockQueryMapper;

    @Autowired
    private GzDepInventoryMapper gzDepInventoryMapper;

    @Autowired
    private GzShipmentMapper gzShipmentMapper;

    @Autowired
    private GzDepotInventoryMapper gzDepotInventoryMapper;

    @Override
    public List<GzStockQueryEntryVo> selectOutboundRefundEntryList(GzStockQueryParam param)
    {
        if (param == null) {
            param = new GzStockQueryParam();
        }
        normalizeBillKindScope(param);
        if (StringUtils.isEmpty(param.getTimeField())) {
            param.setTimeField("createTime");
        }
        return gzStockQueryMapper.selectOutboundRefundEntryList(param);
    }

    @Override
    public List<GzDepotInventoryTraceVo> selectDepotInventoryTrace(String inHospitalCode)
    {
        GzDepotInventoryTraceResultVo result = buildDepotInventoryTraceResult(inHospitalCode);
        return result != null ? result.getTraces() : Collections.emptyList();
    }

    @Override
    public GzDepotInventoryTraceResultVo buildDepotInventoryTraceResult(String inHospitalCode)
    {
        if (StringUtils.isEmpty(inHospitalCode)) {
            return null;
        }
        String code = inHospitalCode.trim();
        GzDepotInventoryTraceResultVo result = gzStockQueryMapper.selectDepotInventorySnapshotByInHospitalCode(code);
        if (result == null) {
            result = new GzDepotInventoryTraceResultVo();
        }
        result.setTraces(gzStockQueryMapper.selectDepotInventoryTraceByInHospitalCode(code));
        result.setSuspectDeductions(gzStockQueryMapper.selectSuspectBatchDeductionByInHospitalCode(code));
        return result;
    }

    @Override
    @Transactional
    public String repairDepInventoryInHospitalCode(String inHospitalCode, String shipmentNo)
    {
        if (StringUtils.isEmpty(inHospitalCode)) {
            throw new ServiceException("院内码不能为空");
        }
        String code = inHospitalCode.trim();
        GzDepInventoryCodeRepairVo candidate = gzStockQueryMapper.selectDepInventoryCodeRepairCandidate(code, shipmentNo);
        if (candidate == null || candidate.getDepInventoryId() == null) {
            throw new ServiceException("未找到可修复的科室库存错码记录，请确认该院内码存在同批次已审核出库且科室仍有错码库存");
        }
        if (StringUtils.isEmpty(candidate.getWrongInHospitalCode())) {
            throw new ServiceException("出库明细院内码为空，无法自动修复");
        }
        if (code.equals(candidate.getWrongInHospitalCode())) {
            throw new ServiceException("出库明细院内码已与备货码一致，无需修复");
        }

        GzDepInventory wrongDep = gzDepInventoryMapper.selectGzDepInventoryById(candidate.getDepInventoryId());
        if (wrongDep == null) {
            throw new ServiceException("错码科室库存不存在");
        }

        String wrongCode = candidate.getWrongInHospitalCode().trim();
        GzDepInventory existingCorrect = gzDepInventoryMapper.selectGzDepInventoryByCodeAndDept(code, candidate.getDepartmentId());
        if (existingCorrect != null && !existingCorrect.getId().equals(wrongDep.getId())) {
            throw new ServiceException(String.format("科室已存在另一条院内码 %s 的库存记录，无法自动修复", code));
        }

        wrongDep.setInHospitalCode(code);
        wrongDep.setUpdateBy(SecurityUtils.getUserIdStr());
        wrongDep.setUpdateTime(DateUtils.getNowDate());
        gzDepInventoryMapper.updateGzDepInventory(wrongDep);

        GzShipmentEntry entry = new GzShipmentEntry();
        entry.setId(candidate.getShipmentEntryId());
        entry.setParenId(candidate.getShipmentId());
        entry.setInHospitalCode(code);
        entry.setUpdateBy(SecurityUtils.getUserIdStr());
        entry.setUpdateTime(DateUtils.getNowDate());
        int entryRows = gzShipmentMapper.updateGzShipmentEntryById(entry);
        if (entryRows <= 0) {
            throw new ServiceException("出库明细院内码更新失败，请刷新后重试");
        }

        gzStockQueryMapper.updateDepFlowInHospitalCode(wrongCode, code);
        if (StringUtils.isNotEmpty(candidate.getShipmentNo())) {
            gzStockQueryMapper.updateWhFlowInHospitalCodeForShipment(wrongCode, code, candidate.getShipmentNo());
        }
        swapDepotInventoryIfNeeded(candidate, code, wrongDep.getQty() != null ? wrongDep.getQty() : BigDecimal.ONE);

        BigDecimal depQty = wrongDep.getQty() != null ? wrongDep.getQty() : BigDecimal.ZERO;
        return String.format("已修复：出库单 %s 院内码 %s → %s（科室库存数量 %s，若为0请开启「显示」零库存查看）",
            candidate.getShipmentNo(), wrongCode, code, depQty.stripTrailingZeros().toPlainString());
    }

    /** 若仓库仍扣着正确码、错误码被误扣为0，则互换两行备货库存数量 */
    private void swapDepotInventoryIfNeeded(GzDepInventoryCodeRepairVo candidate, String correctCode, BigDecimal moveQty)
    {
        if (candidate.getWarehouseId() == null) {
            return;
        }
        if (moveQty == null || moveQty.compareTo(BigDecimal.ZERO) <= 0) {
            moveQty = BigDecimal.ONE;
        }
        GzDepotInventory correctDepot = gzDepotInventoryMapper.selectByInHospitalCodeAndWarehouse(
            correctCode, candidate.getWarehouseId());
        GzDepotInventory wrongDepot = gzDepotInventoryMapper.selectLatestDepotByInHospitalCodeAndWarehouse(
            candidate.getWrongInHospitalCode(), candidate.getWarehouseId());
        if (correctDepot == null || wrongDepot == null) {
            return;
        }
        BigDecimal correctQty = correctDepot.getQty() != null ? correctDepot.getQty() : BigDecimal.ZERO;
        BigDecimal wrongQty = wrongDepot.getQty() != null ? wrongDepot.getQty() : BigDecimal.ZERO;
        if (correctQty.compareTo(BigDecimal.ZERO) <= 0 || wrongQty.compareTo(BigDecimal.ZERO) > 0) {
            return;
        }
        correctDepot.setQty(BigDecimal.ZERO);
        correctDepot.setAmt(BigDecimal.ZERO);
        correctDepot.setUpdateBy(SecurityUtils.getUserIdStr());
        correctDepot.setUpdateTime(DateUtils.getNowDate());
        gzDepotInventoryMapper.updateGzDepotInventory(correctDepot);

        wrongDepot.setQty(moveQty);
        if (wrongDepot.getUnitPrice() != null) {
            wrongDepot.setAmt(wrongDepot.getUnitPrice().multiply(moveQty));
        }
        wrongDepot.setUpdateBy(SecurityUtils.getUserIdStr());
        wrongDepot.setUpdateTime(DateUtils.getNowDate());
        gzDepotInventoryMapper.updateGzDepotInventory(wrongDepot);
    }

    private void normalizeBillKindScope(GzStockQueryParam param)
    {
        String orderNo = param.getOrderNo() != null ? param.getOrderNo().trim().toUpperCase() : "";
        if (StringUtils.isEmpty(orderNo)) {
            return;
        }
        if (orderNo.startsWith("GZCK")) {
            param.setBillKindScope("shipment");
        } else if (orderNo.startsWith("GZTK")) {
            param.setBillKindScope("refundStock");
        }
    }
}

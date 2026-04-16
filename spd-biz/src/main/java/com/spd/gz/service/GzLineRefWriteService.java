package com.spd.gz.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.gz.domain.GzOrderEntryCodeRef;
import com.spd.gz.domain.GzRefundGoods;
import com.spd.gz.domain.GzRefundGoodsEntry;
import com.spd.gz.domain.GzRefundGoodsEntryRef;
import com.spd.gz.domain.GzShipment;
import com.spd.gz.domain.GzShipmentEntry;
import com.spd.gz.domain.GzShipmentEntryRef;
import com.spd.gz.mapper.GzOrderEntryCodeRefMapper;
import com.spd.gz.mapper.GzRefundGoodsEntryRefMapper;
import com.spd.gz.mapper.GzShipmentEntryRefMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 高值单据明细引用关系写入（UUID7）
 */
@Service
public class GzLineRefWriteService
{
    public static final String KIND_ACCEPTANCE = "GZ_ACCEPTANCE";
    public static final String KIND_SHIPMENT = "GZ_SHIPMENT";
    public static final String KIND_REFUND_GOODS = "GZ_REFUND_GOODS";

    @Autowired
    private GzOrderEntryCodeRefMapper gzOrderEntryCodeRefMapper;

    @Autowired
    private GzShipmentEntryRefMapper gzShipmentEntryRefMapper;

    @Autowired
    private GzRefundGoodsEntryRefMapper gzRefundGoodsEntryRefMapper;

    @Autowired
    private FdMaterialMapper fdMaterialMapper;

    public void deleteOutboundRefs(Long shipmentId)
    {
        if (shipmentId == null)
        {
            return;
        }
        String mid = String.valueOf(shipmentId);
        gzOrderEntryCodeRefMapper.deleteByTgtMainIdAndKind(mid, KIND_SHIPMENT);
        gzShipmentEntryRefMapper.deleteByParenShipmentId(shipmentId);
    }

    public void persistOutboundRefs(GzShipment sh, List<GzShipmentEntry> entries)
    {
        if (sh == null || sh.getId() == null || entries == null || entries.isEmpty())
        {
            return;
        }
        String tenantId = StringUtils.isNotEmpty(sh.getTenantId()) ? sh.getTenantId() : SecurityUtils.getCustomerId();
        String userId = SecurityUtils.getUserIdStr();
        Date now = new Date();
        String mainId = String.valueOf(sh.getId());
        String billNo = sh.getShipmentNo();
        Long whId = sh.getWarehouseId();

        List<GzOrderEntryCodeRef> codeList = new ArrayList<>();
        List<GzShipmentEntryRef> shipList = new ArrayList<>();

        for (GzShipmentEntry e : entries)
        {
            if (e == null || e.getId() == null)
            {
                continue;
            }
            if (!hasOutboundAcceptanceRef(e))
            {
                continue;
            }
            String tgtEid = String.valueOf(e.getId());
            String matName = resolveMaterialName(e.getMaterialId());

            GzOrderEntryCodeRef cr = new GzOrderEntryCodeRef();
            cr.setId(UUID7.generateUUID7());
            cr.setTenantId(tenantId);
            cr.setSrcAcceptanceId(e.getRefSrcAcceptanceId());
            cr.setSrcAcceptanceNo(e.getRefSrcAcceptanceNo());
            cr.setSrcOrderEntryId(e.getRefSrcOrderEntryId());
            cr.setSrcBarcodeLineId(e.getRefSrcBarcodeLineId());
            cr.setSrcInHospitalCode(e.getInHospitalCode());
            cr.setTgtBillKind(KIND_SHIPMENT);
            cr.setTgtMainId(mainId);
            cr.setTgtBillNo(billNo);
            cr.setTgtEntryId(tgtEid);
            cr.setRefPurpose("引用备货验收生成备货出库明细");
            cr.setMaterialId(e.getMaterialId());
            cr.setMaterialName(matName);
            cr.setWarehouseId(whId);
            cr.setCreateBy(userId);
            cr.setCreateTime(now);
            codeList.add(cr);

            GzShipmentEntryRef sr = new GzShipmentEntryRef();
            sr.setId(UUID7.generateUUID7());
            sr.setTenantId(tenantId);
            sr.setShipmentEntryId(tgtEid);
            sr.setSrcBillKind(KIND_ACCEPTANCE);
            sr.setSrcMainId(e.getRefSrcAcceptanceId());
            sr.setSrcBillNo(e.getRefSrcAcceptanceNo());
            sr.setSrcDetailId(StringUtils.isNotEmpty(e.getRefSrcBarcodeLineId()) ? e.getRefSrcBarcodeLineId() : e.getRefSrcOrderEntryId());
            sr.setSrcInHospitalCode(e.getInHospitalCode());
            sr.setTgtBillKind(KIND_SHIPMENT);
            sr.setTgtMainId(mainId);
            sr.setTgtBillNo(billNo);
            sr.setTgtEntryId(tgtEid);
            sr.setRefPurpose("引用备货验收生成备货出库明细");
            sr.setMaterialId(e.getMaterialId());
            sr.setMaterialName(matName);
            sr.setCreateBy(userId);
            sr.setCreateTime(now);
            shipList.add(sr);
        }

        if (!codeList.isEmpty())
        {
            gzOrderEntryCodeRefMapper.batchInsert(codeList);
        }
        if (!shipList.isEmpty())
        {
            gzShipmentEntryRefMapper.batchInsert(shipList);
        }
    }

    private boolean hasOutboundAcceptanceRef(GzShipmentEntry e)
    {
        return StringUtils.isNotEmpty(e.getRefSrcAcceptanceId())
            && (StringUtils.isNotEmpty(e.getRefSrcBarcodeLineId()) || StringUtils.isNotEmpty(e.getRefSrcOrderEntryId()));
    }

    public void deleteRefundGoodsRefs(Long refundGoodsParenId)
    {
        if (refundGoodsParenId == null)
        {
            return;
        }
        gzRefundGoodsEntryRefMapper.deleteByParenRefundGoodsId(refundGoodsParenId);
    }

    public void persistRefundGoodsRefs(GzRefundGoods bill, List<GzRefundGoodsEntry> entries, boolean warehouseStockRefund)
    {
        if (bill == null || bill.getId() == null || entries == null || entries.isEmpty())
        {
            return;
        }
        String tenantId = StringUtils.isNotEmpty(bill.getTenantId()) ? bill.getTenantId() : SecurityUtils.getCustomerId();
        String userId = SecurityUtils.getUserIdStr();
        Date now = new Date();
        String mainId = String.valueOf(bill.getId());
        String billNo = bill.getGoodsNo();

        List<GzRefundGoodsEntryRef> list = new ArrayList<>();
        for (GzRefundGoodsEntry e : entries)
        {
            if (e == null || e.getId() == null)
            {
                continue;
            }
            String entryIdStr = String.valueOf(e.getId());
            String matName = resolveMaterialName(e.getMaterialId());

            if (warehouseStockRefund)
            {
                if (StringUtils.isEmpty(e.getRefSrcShipmentId()) || StringUtils.isEmpty(e.getRefSrcShipmentEntryId()))
                {
                    continue;
                }
                GzRefundGoodsEntryRef r = new GzRefundGoodsEntryRef();
                r.setId(UUID7.generateUUID7());
                r.setTenantId(tenantId);
                r.setRefundGoodsEntryId(entryIdStr);
                r.setSrcBillKind(KIND_SHIPMENT);
                r.setSrcMainId(e.getRefSrcShipmentId());
                r.setSrcBillNo(e.getRefSrcShipmentNo());
                r.setSrcDetailId(e.getRefSrcShipmentEntryId());
                r.setSrcInHospitalCode(e.getInHospitalCode());
                r.setTgtBillKind(KIND_REFUND_GOODS);
                r.setTgtMainId(mainId);
                r.setTgtBillNo(billNo);
                r.setTgtEntryId(entryIdStr);
                r.setRefPurpose("引用备货出库生成备货退库明细");
                r.setMaterialId(e.getMaterialId());
                r.setMaterialName(matName);
                r.setCreateBy(userId);
                r.setCreateTime(now);
                list.add(r);
            }
            else
            {
                if (StringUtils.isEmpty(e.getRefSrcAcceptanceId())
                    || (StringUtils.isEmpty(e.getRefSrcBarcodeLineId()) && StringUtils.isEmpty(e.getRefSrcOrderEntryId())))
                {
                    continue;
                }
                GzRefundGoodsEntryRef r = new GzRefundGoodsEntryRef();
                r.setId(UUID7.generateUUID7());
                r.setTenantId(tenantId);
                r.setRefundGoodsEntryId(entryIdStr);
                r.setSrcBillKind(KIND_ACCEPTANCE);
                r.setSrcMainId(e.getRefSrcAcceptanceId());
                r.setSrcBillNo(e.getRefSrcAcceptanceNo());
                r.setSrcDetailId(StringUtils.isNotEmpty(e.getRefSrcBarcodeLineId()) ? e.getRefSrcBarcodeLineId() : e.getRefSrcOrderEntryId());
                r.setSrcInHospitalCode(e.getInHospitalCode());
                r.setTgtBillKind(KIND_REFUND_GOODS);
                r.setTgtMainId(mainId);
                r.setTgtBillNo(billNo);
                r.setTgtEntryId(entryIdStr);
                r.setRefPurpose("引用备货验收生成备货退货明细");
                r.setMaterialId(e.getMaterialId());
                r.setMaterialName(matName);
                r.setCreateBy(userId);
                r.setCreateTime(now);
                list.add(r);
            }
        }

        if (!list.isEmpty())
        {
            gzRefundGoodsEntryRefMapper.batchInsert(list);
        }
    }

    private String resolveMaterialName(Long materialId)
    {
        if (materialId == null)
        {
            return null;
        }
        try
        {
            com.spd.foundation.domain.FdMaterial m = fdMaterialMapper.selectFdMaterialById(materialId);
            return m != null ? m.getName() : null;
        }
        catch (Exception ex)
        {
            return null;
        }
    }
}

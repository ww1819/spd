package com.spd.gz.domain.dto;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;
import lombok.Data;

/**
 * 高值即入即出导出行
 */
@Data
public class GzInstantIoExportRow
{
    @Excel(name = "即入即出")
    private String instantIoStatusText;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "核销时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date consumeAuditTime;

    @Excel(name = "核销科室")
    private String departmentName;

    @Excel(name = "患者姓名")
    private String patientName;

    @Excel(name = "住院/门诊号")
    private String visitNo;

    @Excel(name = "产品名称")
    private String materialName;

    @Excel(name = "院内码")
    private String inHospitalCode;

    @Excel(name = "数量")
    private BigDecimal entryQty;

    @Excel(name = "已退费量")
    private BigDecimal returnedQty;

    @Excel(name = "单价")
    private BigDecimal unitPrice;

    @Excel(name = "金额")
    private BigDecimal amt;

    @Excel(name = "供应商")
    private String supplierName;

    @Excel(name = "确认批次")
    private String confirmNo;

    @Excel(name = "临床确认人")
    private String confirmBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "临床确认时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date confirmTime;

    @Excel(name = "审核人")
    private String instantIoAuditBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审核时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date instantIoAuditTime;

    @Excel(name = "入库单")
    private String inboundBillNo;

    @Excel(name = "出库单")
    private String outboundBillNo;

    @Excel(name = "退货单")
    private String returnGoodsBillNo;

    @Excel(name = "退库单")
    private String returnDepotBillNo;

    public static GzInstantIoExportRow from(GzHighChargeConfirmRowVo r)
    {
        GzInstantIoExportRow e = new GzInstantIoExportRow();
        if (r == null)
        {
            return e;
        }
        Integer st = r.getInstantIoAuditStatus();
        if (st != null && st == 1)
        {
            e.setInstantIoStatusText("已审核");
        }
        else if (st != null && st == 2)
        {
            e.setInstantIoStatusText("已冲销");
        }
        else
        {
            e.setInstantIoStatusText("待审核");
        }
        e.setConsumeAuditTime(r.getConsumeAuditTime());
        e.setDepartmentName(r.getDepartmentName());
        e.setPatientName(r.getPatientName());
        e.setVisitNo(r.getVisitNo());
        e.setMaterialName(r.getMaterialName());
        e.setInHospitalCode(r.getInHospitalCode());
        e.setEntryQty(r.getEntryQty());
        e.setReturnedQty(r.getReturnedQty());
        e.setUnitPrice(r.getUnitPrice());
        e.setAmt(r.getAmt());
        e.setSupplierName(r.getSupplierName());
        e.setConfirmNo(r.getConfirmNo());
        e.setConfirmBy(r.getConfirmBy());
        e.setConfirmTime(r.getConfirmTime());
        e.setInstantIoAuditBy(r.getInstantIoAuditBy());
        e.setInstantIoAuditTime(r.getInstantIoAuditTime());
        e.setInboundBillNo(r.getInboundBillNo());
        e.setOutboundBillNo(r.getOutboundBillNo());
        e.setReturnGoodsBillNo(r.getReturnGoodsBillNo());
        e.setReturnDepotBillNo(r.getReturnDepotBillNo());
        return e;
    }
}

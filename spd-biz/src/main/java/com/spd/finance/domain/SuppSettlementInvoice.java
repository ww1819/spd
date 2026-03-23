package com.spd.finance.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 供应商结算单与发票关联表 supp_settlement_invoice
 * 一张供应商结算单可关联多张发票
 */
public class SuppSettlementInvoice {

    private String id;
    private String tenantId;
    private String suppSettlementId;
    private String invoiceId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /** 删除者 */
    private String deleteBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;

    /** 关联查询：发票信息（非持久化） */
    private FinInvoice invoice;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getSuppSettlementId() { return suppSettlementId; }
    public void setSuppSettlementId(String suppSettlementId) { this.suppSettlementId = suppSettlementId; }
    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public String getDeleteBy() { return deleteBy; }
    public void setDeleteBy(String deleteBy) { this.deleteBy = deleteBy; }
    public Date getDeleteTime() { return deleteTime; }
    public void setDeleteTime(Date deleteTime) { this.deleteTime = deleteTime; }
    public FinInvoice getInvoice() { return invoice; }
    public void setInvoice(FinInvoice invoice) { this.invoice = invoice; }
}

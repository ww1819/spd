package com.spd.warehouse.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 配送单行可入上限快照（按租户+配送单号+行签名聚合，取历次接口返回的最大数量）。
 */
public class StkDeliveryLineCap {

    private String id;
    private String tenantId;
    private String deliveryNo;
    private String lineSign;
    private BigDecimal qtyCap;
    private Date createTime;
    private Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDeliveryNo() {
        return deliveryNo;
    }

    public void setDeliveryNo(String deliveryNo) {
        this.deliveryNo = deliveryNo;
    }

    public String getLineSign() {
        return lineSign;
    }

    public void setLineSign(String lineSign) {
        this.lineSign = lineSign;
    }

    public BigDecimal getQtyCap() {
        return qtyCap;
    }

    public void setQtyCap(BigDecimal qtyCap) {
        this.qtyCap = qtyCap;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}

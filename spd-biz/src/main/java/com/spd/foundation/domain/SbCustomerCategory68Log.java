package com.spd.foundation.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.BaseEntity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 客户68分类操作记录对象 sb_customer_category68_log（主键 UUID7，用于查看修改记录）
 *
 * @author spd
 */
public class SbCustomerCategory68Log extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键 UUID7 */
    private String id;

    /** 客户ID */
    private String customerId;

    /** 客户68分类表主键(sb_customer_category68.id) */
    private String targetId;

    /** 对应标准68分类ID */
    private Long refCategory68Id;

    /** 操作类型: add=新增, update=修改, delete=删除 */
    private String operationType;

    /** 变更前内容(JSON或摘要) */
    private String contentOld;

    /** 变更后内容(JSON或摘要) */
    private String contentNew;

    /** 操作人 */
    private String operateBy;

    /** 操作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operateTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }

    public Long getRefCategory68Id() { return refCategory68Id; }
    public void setRefCategory68Id(Long refCategory68Id) { this.refCategory68Id = refCategory68Id; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }

    public String getContentOld() { return contentOld; }
    public void setContentOld(String contentOld) { this.contentOld = contentOld; }

    public String getContentNew() { return contentNew; }
    public void setContentNew(String contentNew) { this.contentNew = contentNew; }

    public String getOperateBy() { return operateBy; }
    public void setOperateBy(String operateBy) { this.operateBy = operateBy; }

    public Date getOperateTime() { return operateTime; }
    public void setOperateTime(Date operateTime) { this.operateTime = operateTime; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", id)
            .append("customerId", customerId)
            .append("targetId", targetId)
            .append("refCategory68Id", refCategory68Id)
            .append("operationType", operationType)
            .append("operateBy", operateBy)
            .append("operateTime", operateTime)
            .toString();
    }
}

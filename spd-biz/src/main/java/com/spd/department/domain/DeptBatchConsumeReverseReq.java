package com.spd.department.domain;

import java.math.BigDecimal;
import java.util.List;

/**
 * 科室批量消耗-反消耗请求
 */
public class DeptBatchConsumeReverseReq {

    /** 正向消耗主单ID */
    private Long consumeId;

    /** 反消耗备注 */
    private String remark;

    /** 反消耗明细（为空时按可退数量整单反消耗） */
    private List<ReverseItem> items;

    public Long getConsumeId() {
        return consumeId;
    }

    public void setConsumeId(Long consumeId) {
        this.consumeId = consumeId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<ReverseItem> getItems() {
        return items;
    }

    public void setItems(List<ReverseItem> items) {
        this.items = items;
    }

    public static class ReverseItem {
        /** 正向消耗明细ID */
        private Long srcConsumeEntryId;
        /** 本次反消耗数量（正数传入） */
        private BigDecimal reverseQty;

        public Long getSrcConsumeEntryId() {
            return srcConsumeEntryId;
        }

        public void setSrcConsumeEntryId(Long srcConsumeEntryId) {
            this.srcConsumeEntryId = srcConsumeEntryId;
        }

        public BigDecimal getReverseQty() {
            return reverseQty;
        }

        public void setReverseQty(BigDecimal reverseQty) {
            this.reverseQty = reverseQty;
        }
    }
}

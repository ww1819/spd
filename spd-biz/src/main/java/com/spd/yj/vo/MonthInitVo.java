package com.spd.yj.vo;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 月结初始化列表
 */
public class MonthInitVo {

    private static final long serialVersionUID = 1L;

    /** 耗材分类 */
    private String wCategoryName;

    /** 进项金额 */
    private BigDecimal initAmount;

    /** 期初金额 */
    private BigDecimal beginAmount;

    /** 出项金额 */
    private BigDecimal endAmount;

    /** 盘盈金额 */
    private BigDecimal profitAmount;

    /** 盘亏金额 */
    private BigDecimal loseAmount;

    /** 结存金额 */
    private BigDecimal settleAmount;

    /** 结存实物金额 */
    private BigDecimal settleRealityAmount;

    public String getwCategoryName() {
        return wCategoryName;
    }

    public void setwCategoryName(String wCategoryName) {
        this.wCategoryName = wCategoryName;
    }

    public BigDecimal getInitAmount() {
        return initAmount;
    }

    public void setInitAmount(BigDecimal initAmount) {
        this.initAmount = initAmount;
    }

    public BigDecimal getBeginAmount() {
        return beginAmount;
    }

    public void setBeginAmount(BigDecimal beginAmount) {
        this.beginAmount = beginAmount;
    }

    public BigDecimal getEndAmount() {
        return endAmount;
    }

    public void setEndAmount(BigDecimal endAmount) {
        this.endAmount = endAmount;
    }

    public BigDecimal getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(BigDecimal profitAmount) {
        this.profitAmount = profitAmount;
    }

    public BigDecimal getLoseAmount() {
        return loseAmount;
    }

    public void setLoseAmount(BigDecimal loseAmount) {
        this.loseAmount = loseAmount;
    }

    public BigDecimal getSettleAmount() {
        return settleAmount;
    }

    public void setSettleAmount(BigDecimal settleAmount) {
        this.settleAmount = settleAmount;
    }

    public BigDecimal getSettleRealityAmount() {
        return settleRealityAmount;
    }

    public void setSettleRealityAmount(BigDecimal settleRealityAmount) {
        this.settleRealityAmount = settleRealityAmount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("wCategoryName", getwCategoryName())
                .append("initAmount", getInitAmount())
                .append("beginAmount", getBeginAmount())
                .append("endAmount", getEndAmount())
                .append("settleAmount", getSettleAmount())
                .append("profitAmount", getProfitAmount())
                .append("loseAmount", getLoseAmount())
                .append("settleRealityAmount", getSettleRealityAmount())
                .toString();
    }
}

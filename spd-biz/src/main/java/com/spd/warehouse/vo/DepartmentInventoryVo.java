package com.spd.warehouse.vo;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 科室库存查询VO
 */
public class DepartmentInventoryVo {

    /** 科室ID */
    private String KS_NO;

    /** 科室编码 */
    private String KS_CODE;

    /** 科室名称 */
    private String KS_NAME;

    /** 产品档案ID */
    private String HC_NO;

    /** 产品档案编码 */
    private String HC_CODE;

    /** 产品名称 */
    private String HC_NAME;

    /** 规格 */
    private String GG;

    /** 型号 */
    private String XH;

    /** 单位 */
    private String DW;

    /** 期初数量 */
    private BigDecimal QC_SL;

    /** 期初金额 */
    private BigDecimal QC_JE;

    /** 出库数量 */
    private BigDecimal CK_SL;

    /** 出库金额 */
    private BigDecimal CK_JE;

    /** 退库数量 */
    private BigDecimal TK_SL;

    /** 退库金额 */
    private BigDecimal TK_JE;

    /** 调拨转入数量 */
    private BigDecimal DR_SL;

    /** 调拨转入金额 */
    private BigDecimal DR_JE;

    /** 调拨转出数量 */
    private BigDecimal DC_SL;

    /** 调拨转出金额 */
    private BigDecimal DC_JE;

    /** 科室消耗数量 */
    private BigDecimal KSXH_SL;

    /** 科室消耗金额 */
    private BigDecimal KSXH_JE;

    /** 计费数量 */
    private BigDecimal ZZJ_SL;

    /** 计费金额 */
    private BigDecimal ZZJ_JE;

    /** 退费数量 */
    private BigDecimal ZZT_SL;

    /** 退费金额 */
    private BigDecimal ZZT_JE;

    /** 结存数量 */
    private BigDecimal JC_SL;

    /** 结存金额 */
    private BigDecimal JC_JE;

    public String getKS_NO() {
        return KS_NO;
    }

    public void setKS_NO(String KS_NO) {
        this.KS_NO = KS_NO;
    }

    public String getKS_CODE() {
        return KS_CODE;
    }

    public void setKS_CODE(String KS_CODE) {
        this.KS_CODE = KS_CODE;
    }

    public String getKS_NAME() {
        return KS_NAME;
    }

    public void setKS_NAME(String KS_NAME) {
        this.KS_NAME = KS_NAME;
    }

    public String getHC_NO() {
        return HC_NO;
    }

    public void setHC_NO(String HC_NO) {
        this.HC_NO = HC_NO;
    }

    public String getHC_CODE() {
        return HC_CODE;
    }

    public void setHC_CODE(String HC_CODE) {
        this.HC_CODE = HC_CODE;
    }

    public String getHC_NAME() {
        return HC_NAME;
    }

    public void setHC_NAME(String HC_NAME) {
        this.HC_NAME = HC_NAME;
    }

    public String getGG() {
        return GG;
    }

    public void setGG(String GG) {
        this.GG = GG;
    }

    public String getXH() {
        return XH;
    }

    public void setXH(String XH) {
        this.XH = XH;
    }

    public String getDW() {
        return DW;
    }

    public void setDW(String DW) {
        this.DW = DW;
    }

    public BigDecimal getQC_SL() {
        return QC_SL;
    }

    public void setQC_SL(BigDecimal QC_SL) {
        this.QC_SL = QC_SL;
    }

    public BigDecimal getQC_JE() {
        return QC_JE;
    }

    public void setQC_JE(BigDecimal QC_JE) {
        this.QC_JE = QC_JE;
    }

    public BigDecimal getCK_SL() {
        return CK_SL;
    }

    public void setCK_SL(BigDecimal CK_SL) {
        this.CK_SL = CK_SL;
    }

    public BigDecimal getCK_JE() {
        return CK_JE;
    }

    public void setCK_JE(BigDecimal CK_JE) {
        this.CK_JE = CK_JE;
    }

    public BigDecimal getTK_SL() {
        return TK_SL;
    }

    public void setTK_SL(BigDecimal TK_SL) {
        this.TK_SL = TK_SL;
    }

    public BigDecimal getTK_JE() {
        return TK_JE;
    }

    public void setTK_JE(BigDecimal TK_JE) {
        this.TK_JE = TK_JE;
    }

    public BigDecimal getDR_SL() {
        return DR_SL;
    }

    public void setDR_SL(BigDecimal DR_SL) {
        this.DR_SL = DR_SL;
    }

    public BigDecimal getDR_JE() {
        return DR_JE;
    }

    public void setDR_JE(BigDecimal DR_JE) {
        this.DR_JE = DR_JE;
    }

    public BigDecimal getDC_SL() {
        return DC_SL;
    }

    public void setDC_SL(BigDecimal DC_SL) {
        this.DC_SL = DC_SL;
    }

    public BigDecimal getDC_JE() {
        return DC_JE;
    }

    public void setDC_JE(BigDecimal DC_JE) {
        this.DC_JE = DC_JE;
    }

    public BigDecimal getKSXH_SL() {
        return KSXH_SL;
    }

    public void setKSXH_SL(BigDecimal KSXH_SL) {
        this.KSXH_SL = KSXH_SL;
    }

    public BigDecimal getKSXH_JE() {
        return KSXH_JE;
    }

    public void setKSXH_JE(BigDecimal KSXH_JE) {
        this.KSXH_JE = KSXH_JE;
    }

    public BigDecimal getZZJ_SL() {
        return ZZJ_SL;
    }

    public void setZZJ_SL(BigDecimal ZZJ_SL) {
        this.ZZJ_SL = ZZJ_SL;
    }

    public BigDecimal getZZJ_JE() {
        return ZZJ_JE;
    }

    public void setZZJ_JE(BigDecimal ZZJ_JE) {
        this.ZZJ_JE = ZZJ_JE;
    }

    public BigDecimal getZZT_SL() {
        return ZZT_SL;
    }

    public void setZZT_SL(BigDecimal ZZT_SL) {
        this.ZZT_SL = ZZT_SL;
    }

    public BigDecimal getZZT_JE() {
        return ZZT_JE;
    }

    public void setZZT_JE(BigDecimal ZZT_JE) {
        this.ZZT_JE = ZZT_JE;
    }

    public BigDecimal getJC_SL() {
        return JC_SL;
    }

    public void setJC_SL(BigDecimal JC_SL) {
        this.JC_SL = JC_SL;
    }

    public BigDecimal getJC_JE() {
        return JC_JE;
    }

    public void setJC_JE(BigDecimal JC_JE) {
        this.JC_JE = JC_JE;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("KS_NO", getKS_NO())
                .append("KS_CODE", getKS_CODE())
                .append("KS_NAME", getKS_NAME())
                .append("HC_NO", getHC_NO())
                .append("HC_CODE", getHC_CODE())
                .append("HC_NAME", getHC_NAME())
                .append("GG", getGG())
                .append("XH", getXH())
                .append("DW", getDW())
                .append("QC_SL", getQC_SL())
                .append("QC_JE", getQC_JE())
                .append("CK_SL", getCK_SL())
                .append("CK_JE", getCK_JE())
                .append("TK_SL", getTK_SL())
                .append("TK_JE", getTK_JE())
                .append("DR_SL", getDR_SL())
                .append("DR_JE", getDR_JE())
                .append("DC_SL", getDC_SL())
                .append("DC_JE", getDC_JE())
                .append("KSXH_SL", getKSXH_SL())
                .append("KSXH_JE", getKSXH_JE())
                .append("ZZJ_SL", getZZJ_SL())
                .append("ZZJ_JE", getZZJ_JE())
                .append("ZZT_SL", getZZT_SL())
                .append("ZZT_JE", getZZT_JE())
                .append("JC_SL", getJC_SL())
                .append("JC_JE", getJC_JE())
                .toString();
    }
} 
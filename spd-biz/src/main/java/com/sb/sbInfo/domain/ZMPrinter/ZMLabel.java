package com.sb.sbInfo.domain.ZMPrinter;

public class ZMLabel {
    private Float labelwidth;//标签宽度
    private Float labelheight;//标签高度
    private Float labelrowgap;//行距
    private Float labelcolumngap;//列距
    private Integer labelrownum;//行数
    private Integer labelcolumnnum;//列数
    private Float leftoffset;//左侧位置微调
    private Float topoffset;//顶部位置微调
    private Float pageleftedges;//左空
    private Float pagerightedges;//右空
    private Integer pagestartlocation;//多行列时起始位置，0为左上，1为右上，2为左下，3为右下
    private Integer pagelabelorder;// 多行列时标签顺序，0为水平，1为垂直


    public ZMLabel() {
    }
    public Float getLabelwidth() {
        return this.labelwidth;
    }

    public void setLabelwidth(Float labelwidth) {
        this.labelwidth = labelwidth;
    }

    public Float getLabelheight() {
        return this.labelheight;
    }

    public void setLabelheight(Float labelheight) {
        this.labelheight = labelheight;
    }

    public Float getLabelrowgap() {
        return this.labelrowgap;
    }

    public void setLabelrowgap(Float labelrowgap) {
        this.labelrowgap = labelrowgap;
    }

    public Float getLabelcolumngap() {
        return this.labelcolumngap;
    }

    public void setLabelcolumngap(Float labelcolumngap) {
        this.labelcolumngap = labelcolumngap;
    }

    public int getLabelrownum() {
        return this.labelrownum;
    }

    public void setLabelrownum(Integer labelrownum) {
        this.labelrownum = labelrownum;
    }

    public Integer getLabelcolumnnum() {
        return this.labelcolumnnum;
    }

    public void setLabelcolumnnum(Integer labelcolumnnum) {
        this.labelcolumnnum = labelcolumnnum;
    }

    public Float getLeftoffset() {
        return this.leftoffset;
    }

    public void setLeftoffset(Float leftoffset) {
        this.leftoffset = leftoffset;
    }

    public Float getTopoffset() {
        return this.topoffset;
    }

    public void setTopoffset(Float topoffset) {
        this.topoffset = topoffset;
    }

    public Float getPageleftedges() {
        return this.pageleftedges;
    }

    public void setPageleftedges(Float pageleftedges) {
        this.pageleftedges = pageleftedges;
    }

    public Float getPagerightedges() {
        return this.pagerightedges;
    }

    public void setPagerightedges(Float pagerightedges) {
        this.pagerightedges = pagerightedges;
    }

    public Integer getPagestartlocation() {
        return this.pagestartlocation;
    }

    public void setPagestartlocation(Integer pagestartlocation) {
        this.pagestartlocation = pagestartlocation;
    }

    public Integer getPagelabelorder() {
        return this.pagelabelorder;
    }

    public void setPagelabelorder(Integer pagelabelorder) {
        this.pagelabelorder = pagelabelorder;
    }
}

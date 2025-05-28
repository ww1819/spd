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
        // Default constructor
        this.setLabelwidth(104f);
        this.setLabelheight(75f);
        this.setLabelrowgap(2f);
        this.setLabelcolumngap(2f);
        this.setLabelrownum(1);
        this.setLabelcolumnnum(1);
        this.setLeftoffset(0f);
        this.setTopoffset(0f);
        this.setPageleftedges(0f);
        this.setPagerightedges(0f);
        this.setPagestartlocation(0);
        this.setPagelabelorder(0);
    }
    public Float getLabelwidth() {
        return labelwidth;
    }

    public void setLabelwidth(Float labelwidth) {
        this.labelwidth = labelwidth;
    }

    public Float getLabelheight() {
        return labelheight;
    }

    public void setLabelheight(Float labelheight) {
        this.labelheight = labelheight;
    }

    public Float getLabelrowgap() {
        return labelrowgap;
    }

    public void setLabelrowgap(Float labelrowgap) {
        this.labelrowgap = labelrowgap;
    }

    public Float getLabelcolumngap() {
        return labelcolumngap;
    }

    public void setLabelcolumngap(Float labelcolumngap) {
        this.labelcolumngap = labelcolumngap;
    }

    public int getLabelrownum() {
        return labelrownum;
    }

    public void setLabelrownum(Integer labelrownum) {
        this.labelrownum = labelrownum;
    }

    public Integer getLabelcolumnnum() {
        return labelcolumnnum;
    }

    public void setLabelcolumnnum(Integer labelcolumnnum) {
        this.labelcolumnnum = labelcolumnnum;
    }

    public Float getLeftoffset() {
        return leftoffset;
    }

    public void setLeftoffset(Float leftoffset) {
        this.leftoffset = leftoffset;
    }

    public Float getTopoffset() {
        return topoffset;
    }

    public void setTopoffset(Float topoffset) {
        this.topoffset = topoffset;
    }

    public Float getPageleftedges() {
        return pageleftedges;
    }

    public void setPageleftedges(Float pageleftedges) {
        this.pageleftedges = pageleftedges;
    }

    public Float getPagerightedges() {
        return pagerightedges;
    }

    public void setPagerightedges(Float pagerightedges) {
        this.pagerightedges = pagerightedges;
    }

    public Integer getPagestartlocation() {
        return pagestartlocation;
    }

    public void setPagestartlocation(Integer pagestartlocation) {
        this.pagestartlocation = pagestartlocation;
    }

    public Integer getPagelabelorder() {
        return pagelabelorder;
    }

    public void setPagelabelorder(Integer pagelabelorder) {
        this.pagelabelorder = pagelabelorder;
    }
}

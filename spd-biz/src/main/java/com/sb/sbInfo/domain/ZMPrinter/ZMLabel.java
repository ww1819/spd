package com.sb.sbInfo.domain.ZMPrinter;

public class ZMLabel {
    private float labelwidth;//标签宽度
    private float labelheight;//标签高度
    private float labelrowgap;//行距
    private float labelcolumngap;//列距
    private int labelrownum;//行数
    private int labelcolumnnum;//列数
    private float leftoffset;//左侧位置微调
    private float topoffset;//顶部位置微调
    private float pageleftedges;//左空
    private float pagerightedges;//右空
    private int pagestartlocation;//多行列时起始位置，0为左上，1为右上，2为左下，3为右下
    private int pagelabelorder;// 多行列时标签顺序，0为水平，1为垂直


    public ZMLabel() {
        // Default constructor
        this.setLabelwidth(104);
        this.setLabelheight(75);
        this.setLabelrowgap(2);
        this.setLabelcolumngap(2);
        this.setLabelrownum(1);
        this.setLabelcolumnnum(1);
        this.setLeftoffset(0);
        this.setTopoffset(0);
        this.setPageleftedges(0);
        this.setPagerightedges(0);
        this.setPagestartlocation(0);
        this.setPagelabelorder(0);
    }
    public float getLabelwidth() {
        return labelwidth;
    }

    public void setLabelwidth(float labelwidth) {
        this.labelwidth = labelwidth;
    }

    public float getLabelheight() {
        return labelheight;
    }

    public void setLabelheight(float labelheight) {
        this.labelheight = labelheight;
    }

    public float getLabelrowgap() {
        return labelrowgap;
    }

    public void setLabelrowgap(float labelrowgap) {
        this.labelrowgap = labelrowgap;
    }

    public float getLabelcolumngap() {
        return labelcolumngap;
    }

    public void setLabelcolumngap(float labelcolumngap) {
        this.labelcolumngap = labelcolumngap;
    }

    public int getLabelrownum() {
        return labelrownum;
    }

    public void setLabelrownum(int labelrownum) {
        this.labelrownum = labelrownum;
    }

    public int getLabelcolumnnum() {
        return labelcolumnnum;
    }

    public void setLabelcolumnnum(int labelcolumnnum) {
        this.labelcolumnnum = labelcolumnnum;
    }

    public float getLeftoffset() {
        return leftoffset;
    }

    public void setLeftoffset(float leftoffset) {
        this.leftoffset = leftoffset;
    }

    public float getTopoffset() {
        return topoffset;
    }

    public void setTopoffset(float topoffset) {
        this.topoffset = topoffset;
    }

    public float getPageleftedges() {
        return pageleftedges;
    }

    public void setPageleftedges(float pageleftedges) {
        this.pageleftedges = pageleftedges;
    }

    public float getPagerightedges() {
        return pagerightedges;
    }

    public void setPagerightedges(float pagerightedges) {
        this.pagerightedges = pagerightedges;
    }

    public int getPagestartlocation() {
        return pagestartlocation;
    }

    public void setPagestartlocation(int pagestartlocation) {
        this.pagestartlocation = pagestartlocation;
    }

    public int getPagelabelorder() {
        return pagelabelorder;
    }

    public void setPagelabelorder(int pagelabelorder) {
        this.pagelabelorder = pagelabelorder;
    }
}

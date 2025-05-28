package com.sb.sbInfo.domain.ZMPrinter;

public class ZMPrinter {
    private PrinterStyle printerinterface;//打印机的数据传输接口类型
    private String printername;//打印机名称
    private String printermbsn;//打印机主板序号，如需要控制多台打印机则必须设置
    private Integer printerdpi;//打印机分辨率，203，300，600dpi
    private Integer printSpeed;//打印速度，单位英寸每秒，2~8有效
    private Integer printDarkness;//打印黑度，0~20有效，值越大打印温度越高
    private String printernetip;//打印机网卡ip地址，网络打印时必须设置
    private Integer printnum;//打印数量
    private Integer copynum;//复制份数
    private Boolean reverse;//是否旋转180度打印，即反向打印
    private Boolean labelhavegap;//有间隙标签请设置为true，连续纸请设置为false
    private Integer pageDirection;//1 竖向，2 横向

    public ZMPrinter() {
        this.setPrinterinterface(PrinterStyle.RFID_DRIVER);
        this.setPrintername("Default");
        this.setPrintermbsn("");
        this.setPrinterdpi(300);
        this.setPrintSpeed(4);
        this.setPrintDarkness(10);
        this.setPrinternetip("192.168.8.180");
        this.setPrintnum(1);
        this.setCopynum(1);
        this.setReverse(false);
        this.setLabelhavegap(true);
        this.setPageDirection(1);
    }
    public PrinterStyle getPrinterinterface() {
        return printerinterface;
    }

    public void setPrinterinterface(PrinterStyle printerinterface) {
        this.printerinterface = printerinterface;
    }

    public String getPrintername() {
        return printername;
    }

    public void setPrintername(String printername) {
        this.printername = printername;
    }

    public String getPrintermbsn() {
        return printermbsn;
    }

    public void setPrintermbsn(String printermbsn) {
        this.printermbsn = printermbsn;
    }

    public Integer getPrinterdpi() {
        return printerdpi;
    }

    public void setPrinterdpi(Integer printerdpi) {
        this.printerdpi = printerdpi;
    }

    public Integer getPrintSpeed() {
        return printSpeed;
    }

    public void setPrintSpeed(Integer printSpeed) {
        this.printSpeed = printSpeed;
    }

    public Integer getPrintDarkness() {
        return printDarkness;
    }

    public void setPrintDarkness(Integer printDarkness) {
        this.printDarkness = printDarkness;
    }

    public String getPrinternetip() {
        return printernetip;
    }

    public void setPrinternetip(String printernetip) {
        this.printernetip = printernetip;
    }

    public Integer getPrintnum() {
        return printnum;
    }

    public void setPrintnum(Integer printnum) {
        this.printnum = printnum;
    }

    public Integer getCopynum() {
        return copynum;
    }

    public void setCopynum(Integer copynum) {
        this.copynum = copynum;
    }

    public Boolean getReverse() {
        return reverse;
    }

    public void setReverse(Boolean reverse) {
        this.reverse = reverse;
    }

    public Boolean getLabelhavegap() {
        return labelhavegap;
    }

    public void setLabelhavegap(Boolean labelhavegap) {
        this.labelhavegap = labelhavegap;
    }

    public Integer getPageDirection() {
        return pageDirection;
    }

    public void setPageDirection(Integer pageDirection) {
        this.pageDirection = pageDirection;
    }
}

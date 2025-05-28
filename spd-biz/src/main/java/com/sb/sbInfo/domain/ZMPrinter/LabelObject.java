package com.sb.sbInfo.domain.ZMPrinter;

public class LabelObject{
    //标签对象

    //对象名称的命名规则：
    //1、条码对象以“barcode”开头，如“barcode-01”，“barcode-02”...
    //2、文字对象以“text”开头，如“text-01”，“text-02”...
    //3、直线对象以“line”开头，如“line-01”，“line-02”...
    //4、矩形对象以“rectangle”开头，如“rectangle-01”，“rectangle-02”...
    //5、图片对象以“image”开头，如“image-01”，“image-02”...
    //6、RFID对象以“rfiduhf”开头，如“rfiduhf-01”，“rfiduhf-02”...注意：超高频和高频的对象名称都是以“rfiduhf”开头
    private String ObjectName;//对象名称

    //文字、条码或RFID对象的内容
    private String objectdata;

    private Float Xposition;// 文字、条码、图片在标签上的起始位置X坐标，单位是mm
    private Float Yposition;// 文字、条码、图片在标签上的起始位置Y坐标，单位是mm
    private Float startXposition;//线条、矩形在标签上起始点的位置，单位是mm
    private Float startYposition;// 线条、矩形在标签上起始点的位置，单位是mm
    private Float endXposition;// 线条、矩形在标签上终止点的位置，单位是mm
    private Float endYposition;// 线条、矩形在标签上终止点的位置，单位是mm

    private String barcodekind;//条码类型
    //条码的类型：常用的一维条码是Code 128 Auto，二维条码是Q RCode(2D)
//Code 128 Auto
//Code 128 A
//Code 128 B
//Code 128 C
//EAN-13
//QR Code(2D)
//PDF 417(2D)
//Data Matrix(2D)
//Code 39
//Code 39 Extended
//Code 93
//EAN 128 Auto
//EAN 128 A
//EAN 128 B
//EAN 128 C
    private Float barcodescale;//缩放系数，用于控制条码的宽度
    private Float barcodeheight;//条码的高度，单位mm

    private Integer direction;//旋转方向，0是0度，1是90度，2是180度，3是270度
    private Integer errorcorrection;//QR码的纠错等级
    private Integer charencoding;//QR码的字符编码,0为UTF-8，1为GB2312
    private Integer qrversion;//QR符号版本，1~40，数字越大包含字符越多，0为自动
    private Integer code39widthratio;//Code39码的条宽比
    private Boolean code39startchar;//Code39码是否包含起始符*
    private Integer barcodealign;//条码的对齐方式，0为左对齐，1为居中对齐，2为右对齐
    private Integer pdf417_rows;//PDF417条码的行数，即层数，0为自动
    private Integer pdf417_columns;//PDF417条码的列数，即数据的块数，左右两个标识块不算在内，0为自动
    private Integer datamatrixShape;//DataMatrix的形状，0为自动，1为正方形，2为矩形
    private Integer textposition;//文字相对于条码的方位，0为在条码下方，1为上方，2为不显示
    private Float textoffset;// 文字相对于条码的距离，单位mm
    private Integer textalign;//文字相对于条码的对齐方式，0为左侧、1为右侧、2为居中、3为撑满

    private String textfont;//字体名称
    private Integer fontstyle;//字体样式，0 正常体，1 粗体，2 斜体，3 斜粗体
    private Float fontsize;//字体大小
    private Boolean blackbackground;//是否黑底白字
    private Float chargap;//字符间距，单位mm
    private Float charHZoom;//字符横向缩放倍数
    private Integer texttype;//文本类型，0为单行文本，1为段落文本
    private Integer texttextalign;// 文字的对齐方式，0为左对齐，1为居中，2为右对齐
    private Integer texttextvalign;// 文字的对齐方式，0为顶边对齐，1为垂直居中，2为底边对齐
    private Float textwidth;// 段落文本的宽度，单位mm
    private Integer textwidthbeyound;//超出段落文本的宽度后处理，0为自动换行，1为自动压扁字体，
    //2为自动缩小字体
    private Integer linegapindex;// 段落文本的行间距，0为单倍，1为一倍半，2为双倍，3为自定义
    private Float linegap;// 段落文本的自定义行间距

    private Integer objectclass;//线条对象的类别，1为直线，2为矩形
    private Integer lineclass;//直线的类别，1为横线，2为竖线，3为斜线
    private Integer rectangleclass;//矩形的类别，0为直角矩形，1为圆角矩形，2为椭圆
    private Float cornerRadius;//圆角矩形的半径，单位是mm
    private Float lineWidth;//线的宽度，单位是mm
    private Integer lineDashStyle;//条线样式，0为实线，1为破折虚线，2为破折点虚线，3为破折点点虚线，
    //4为点虚线
    private Boolean fillRectangle;//是否填充矩形

    private Byte[] imagedata;//存储原始图像的数据
    private Float hscale;//横向缩放率百分比
    private Float vscale;//竖向缩放率百分比
    private Boolean aspectRatio;//图片是否保持长宽比，默认是
    private Boolean imagefixedsize;//是否固定尺寸
    private Float imagefixedwidth;//图片固定宽度，单位是mm
    private Float imagefixedheight;//图片固定高度，单位是mm


    //RFID写入对象
//UHF
    private Integer RFIDEncodertype;//协议类型：0为UHF，1为HF 15693，2为HF 14443，3为NFC
    private Integer RFIDDatablock;//写入数据区：0为EPC，1为USER
    private Integer RFIDDatatype;//写入数据类型：0为文本，1为16进制，2为网址链接（NDEF），
    //3为纯文本（NDEF）
    private Integer RFIDTextencoding;//文本编码：0为ASCII，1为UTF-8
    private Integer DataAlignment;//数据对齐方式，0为后端补零，1为前端补零
    private Integer RFIDerrortimes;//错误重试次数

    private Integer RFIDepccontrol;//EPC区访问控制
    private Integer RFIDusercontrol; //USER区访问控制
    private Integer RFIDtidcontrol; //TID区访问控制
    private Integer RFIDaccesspwdcontrol; //访问密码区访问控制
    private Integer RFIDkillpwdcontrol; //灭活密码区访问控制
    //以上访问控制的值：0为开放，1为锁定，2为解除锁定，3为永久锁定，4为永久解除锁定，
//5为先解除锁定再重新锁定，6为先解除锁定再重新永久锁定
    private String RFIDaccessnewpwd;//访问密码 新密码
    private String RFIDaccessoldpwd;//访问密码 旧密码
    private Boolean usekillpwd;//是否使用灭活密码
    private String RFIDkillpwd;//灭活密码

    //HF
    private Integer HFstartblock;//高频要写的块的起点地址
    private Integer HFmodulepower;//高频模块功率，0为自动
    private Boolean Encrypt14443A;//是否要加密14443A标签
    private Integer Sector14443A;//需要加密的1443A的扇区
    private Integer KEYAB14443A;//需要加密KEYA或KEYB，0为KEYA，1为KEYB，2为两个都加密
    private String KEYAnewpwd;//KEYA新密码
    private String KEYAoldpwd;//KEYA旧密码
    private String KEYBnewpwd;//KEYB新密码
    private String KEYBoldpwd;//KEYB旧密码
    private Boolean Encrypt14443AControl;//是否要设置14443A的控制字
    private String Encrypt14443AControlvalue ;//默认的控制字
    private Integer Controlarea15693;//0为不设置，1为AFI，2为DSFID
    private String Controlvalue15693;//设置的值，默认是00


    public String getObjectName() {
        return this.ObjectName;
    }

    public void setObjectName(String objectName) {
        this.ObjectName = objectName;
    }

    public String getObjectdata() {
        return this.objectdata;
    }

    public void setObjectdata(String objectdata) {
        this.objectdata = objectdata;
    }

    public Float getXposition() {
        return this.Xposition;
    }

    public void setXposition(Float xposition) {
        this.Xposition = xposition;
    }

    public Float getYposition() {
        return this.Yposition;
    }

    public void setYposition(Float yposition) {
        this.Yposition = yposition;
    }

    public Float getStartXposition() {
        return this.startXposition;
    }

    public void setStartXposition(Float startXposition) {
        this.startXposition = startXposition;
    }

    public Float getStartYposition() {
        return this.startYposition;
    }

    public void setStartYposition(Float startYposition) {
        this.startYposition = startYposition;
    }

    public Float getEndXposition() {
        return this.endXposition;
    }

    public void setEndXposition(Float endXposition) {
        this.endXposition = endXposition;
    }

    public Float getEndYposition() {
        return this.endYposition;
    }

    public void setEndYposition(Float endYposition) {
        this.endYposition = endYposition;
    }

    public String getBarcodekind() {
        return this.barcodekind;
    }

    public void setBarcodekind(String barcodekind) {
        this.barcodekind = barcodekind;
    }

    public Float getBarcodescale() {
        return this.barcodescale;
    }

    public void setBarcodescale(Float barcodescale) {
        this.barcodescale = barcodescale;
    }

    public Float getBarcodeheight() {
        return this.barcodeheight;
    }

    public void setBarcodeheight(Float barcodeheight) {
        this.barcodeheight = barcodeheight;
    }

    public Integer getDirection() {
        return this.direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public Integer getErrorcorrection() {
        return this.errorcorrection;
    }

    public void setErrorcorrection(Integer errorcorrection) {
        this.errorcorrection = errorcorrection;
    }

    public Integer getCharencoding() {
        return this.charencoding;
    }

    public void setCharencoding(Integer charencoding) {
        this.charencoding = charencoding;
    }

    public Integer getQrversion() {
        return this.qrversion;
    }

    public void setQrversion(Integer qrversion) {
        this.qrversion = qrversion;
    }

    public Integer getCode39widthratio() {
        return this.code39widthratio;
    }

    public void setCode39widthratio(Integer code39widthratio) {
        this.code39widthratio = code39widthratio;
    }

    public Boolean getCode39startchar() {
        return this.code39startchar;
    }

    public void setCode39startchar(Boolean code39startchar) {
        this.code39startchar = code39startchar;
    }

    public Integer getBarcodealign() {
        return this.barcodealign;
    }

    public void setBarcodealign(Integer barcodealign) {
        this.barcodealign = barcodealign;
    }

    public Integer getPdf417_rows() {
        return this.pdf417_rows;
    }

    public void setPdf417_rows(Integer pdf417_rows) {
        this.pdf417_rows = pdf417_rows;
    }

    public Integer getPdf417_columns() {
        return this.pdf417_columns;
    }

    public void setPdf417_columns(Integer pdf417_columns) {
        this.pdf417_columns = pdf417_columns;
    }

    public Integer getDatamatrixShape() {
        return this.datamatrixShape;
    }

    public void setDatamatrixShape(Integer datamatrixShape) {
        this.datamatrixShape = datamatrixShape;
    }

    public Integer getTextposition() {
        return this.textposition;
    }

    public void setTextposition(Integer textposition) {
        this.textposition = textposition;
    }

    public Float getTextoffset() {
        return this.textoffset;
    }

    public void setTextoffset(Float textoffset) {
        this.textoffset = textoffset;
    }

    public Integer getTextalign() {
        return this.textalign;
    }

    public void setTextalign(Integer textalign) {
        this.textalign = textalign;
    }

    public String getTextfont() {
        return this.textfont;
    }

    public void setTextfont(String textfont) {
        this.textfont = textfont;
    }

    public Integer getFontstyle() {
        return this.fontstyle;
    }

    public void setFontstyle(Integer fontstyle) {
        this.fontstyle = fontstyle;
    }

    public Float getFontsize() {
        return this.fontsize;
    }

    public void setFontsize(Float fontsize) {
        this.fontsize = fontsize;
    }

    public Boolean getBlackbackground() {
        return this.blackbackground;
    }

    public void setBlackbackground(Boolean blackbackground) {
        this.blackbackground = blackbackground;
    }

    public Float getChargap() {
        return this.chargap;
    }

    public void setChargap(Float chargap) {
        this.chargap = chargap;
    }

    public Float getCharHZoom() {
        return this.charHZoom;
    }

    public void setCharHZoom(Float charHZoom) {
        this.charHZoom = charHZoom;
    }

    public Integer getTexttype() {
        return this.texttype;
    }

    public void setTexttype(Integer texttype) {
        this.texttype = texttype;
    }

    public Integer getTexttextalign() {
        return this.texttextalign;
    }

    public void setTexttextalign(Integer texttextalign) {
        this.texttextalign = texttextalign;
    }

    public Integer getTexttextvalign() {
        return this.texttextvalign;
    }

    public void setTexttextvalign(Integer texttextvalign) {
        this.texttextvalign = texttextvalign;
    }

    public Float getTextwidth() {
        return this.textwidth;
    }

    public void setTextwidth(Float textwidth) {
        this.textwidth = textwidth;
    }

    public Integer getTextwidthbeyound() {
        return this.textwidthbeyound;
    }

    public void setTextwidthbeyound(Integer textwidthbeyound) {
        this.textwidthbeyound = textwidthbeyound;
    }

    public Integer getLinegapindex() {
        return this.linegapindex;
    }

    public void setLinegapindex(Integer linegapindex) {
        this.linegapindex = linegapindex;
    }

    public Float getLinegap() {
        return this.linegap;
    }

    public void setLinegap(Float linegap) {
        this.linegap = linegap;
    }

    public Integer getObjectclass() {
        return this.objectclass;
    }

    public void setObjectclass(Integer objectclass) {
        this.objectclass = objectclass;
    }

    public Integer getLineclass() {
        return this.lineclass;
    }

    public void setLineclass(Integer lineclass) {
        this.lineclass = lineclass;
    }

    public Integer getRectangleclass() {
        return this.rectangleclass;
    }

    public void setRectangleclass(Integer rectangleclass) {
        this.rectangleclass = rectangleclass;
    }

    public Float getCornerRadius() {
        return this.cornerRadius;
    }

    public void setCornerRadius(Float cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    public Float getLineWidth() {
        return this.lineWidth;
    }

    public void setLineWidth(Float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public Integer getLineDashStyle() {
        return this.lineDashStyle;
    }

    public void setLineDashStyle(Integer lineDashStyle) {
        this.lineDashStyle = lineDashStyle;
    }

    public Boolean getFillRectangle() {
        return this.fillRectangle;
    }

    public void setFillRectangle(Boolean fillRectangle) {
        this.fillRectangle = fillRectangle;
    }

    public Byte[] getImagedata() {
        return this.imagedata;
    }

    public void setImagedata(Byte[] imagedata) {
        this.imagedata = imagedata;
    }

    public Float getHscale() {
        return this.hscale;
    }

    public void setHscale(Float hscale) {
        this.hscale = hscale;
    }

    public Float getVscale() {
        return this.vscale;
    }

    public void setVscale(Float vscale) {
        this.vscale = vscale;
    }

    public Boolean getAspectRatio() {
        return this.aspectRatio;
    }

    public void setAspectRatio(Boolean aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public Boolean getImagefixedsize() {
        return this.imagefixedsize;
    }

    public void setImagefixedsize(Boolean imagefixedsize) {
        this.imagefixedsize = imagefixedsize;
    }

    public Float getImagefixedwidth() {
        return this.imagefixedwidth;
    }

    public void setImagefixedwidth(Float imagefixedwidth) {
        this.imagefixedwidth = imagefixedwidth;
    }

    public Float getImagefixedheight() {
        return this.imagefixedheight;
    }

    public void setImagefixedheight(Float imagefixedheight) {
        this.imagefixedheight = imagefixedheight;
    }

    public Integer getRFIDEncodertype() {
        return this.RFIDEncodertype;
    }

    public void setRFIDEncodertype(Integer RFIDEncodertype) {
        this.RFIDEncodertype = RFIDEncodertype;
    }

    public Integer getRFIDDatablock() {
        return this.RFIDDatablock;
    }

    public void setRFIDDatablock(Integer RFIDDatablock) {
        this.RFIDDatablock = RFIDDatablock;
    }

    public Integer getRFIDDatatype() {
        return this.RFIDDatatype;
    }

    public void setRFIDDatatype(Integer RFIDDatatype) {
        this.RFIDDatatype = RFIDDatatype;
    }

    public Integer getRFIDTextencoding() {
        return this.RFIDTextencoding;
    }

    public void setRFIDTextencoding(Integer RFIDTextencoding) {
        this.RFIDTextencoding = RFIDTextencoding;
    }

    public Integer getDataAlignment() {
        return this.DataAlignment;
    }

    public void setDataAlignment(Integer dataAlignment) {
        this.DataAlignment = dataAlignment;
    }

    public Integer getRFIDerrortimes() {
        return this.RFIDerrortimes;
    }

    public void setRFIDerrortimes(Integer RFIDerrortimes) {
        this.RFIDerrortimes = RFIDerrortimes;
    }

    public Integer getRFIDepccontrol() {
        return this.RFIDepccontrol;
    }

    public void setRFIDepccontrol(Integer RFIDepccontrol) {
        this.RFIDepccontrol = RFIDepccontrol;
    }

    public Integer getRFIDusercontrol() {
        return this.RFIDusercontrol;
    }

    public void setRFIDusercontrol(Integer RFIDusercontrol) {
        this.RFIDusercontrol = RFIDusercontrol;
    }

    public Integer getRFIDtidcontrol() {
        return this.RFIDtidcontrol;
    }

    public void setRFIDtidcontrol(Integer RFIDtidcontrol) {
        this.RFIDtidcontrol = RFIDtidcontrol;
    }

    public Integer getRFIDaccesspwdcontrol() {
        return this.RFIDaccesspwdcontrol;
    }

    public void setRFIDaccesspwdcontrol(Integer RFIDaccesspwdcontrol) {
        this.RFIDaccesspwdcontrol = RFIDaccesspwdcontrol;
    }

    public Integer getRFIDkillpwdcontrol() {
        return this.RFIDkillpwdcontrol;
    }

    public void setRFIDkillpwdcontrol(Integer RFIDkillpwdcontrol) {
        this.RFIDkillpwdcontrol = RFIDkillpwdcontrol;
    }

    public String getRFIDaccessnewpwd() {
        return this.RFIDaccessnewpwd;
    }

    public void setRFIDaccessnewpwd(String RFIDaccessnewpwd) {
        this.RFIDaccessnewpwd = RFIDaccessnewpwd;
    }

    public String getRFIDaccessoldpwd() {
        return this.RFIDaccessoldpwd;
    }

    public void setRFIDaccessoldpwd(String RFIDaccessoldpwd) {
        this.RFIDaccessoldpwd = RFIDaccessoldpwd;
    }

    public Boolean getUsekillpwd() {
        return this.usekillpwd;
    }

    public void setUsekillpwd(Boolean usekillpwd) {
        this.usekillpwd = usekillpwd;
    }

    public String getRFIDkillpwd() {
        return this.RFIDkillpwd;
    }

    public void setRFIDkillpwd(String RFIDkillpwd) {
        this.RFIDkillpwd = RFIDkillpwd;
    }

    public Integer getHFstartblock() {
        return this.HFstartblock;
    }

    public void setHFstartblock(Integer HFstartblock) {
        this.HFstartblock = HFstartblock;
    }

    public Integer getHFmodulepower() {
        return this.HFmodulepower;
    }

    public void setHFmodulepower(Integer HFmodulepower) {
        this.HFmodulepower = HFmodulepower;
    }

    public Boolean getEncrypt14443A() {
        return this.Encrypt14443A;
    }

    public void setEncrypt14443A(Boolean encrypt14443A) {
        this.Encrypt14443A = encrypt14443A;
    }

    public Integer getSector14443A() {
        return this.Sector14443A;
    }

    public void setSector14443A(Integer sector14443A) {
        this.Sector14443A = sector14443A;
    }

    public Integer getKEYAB14443A() {
        return this.KEYAB14443A;
    }

    public void setKEYAB14443A(Integer KEYAB14443A) {
        this.KEYAB14443A = KEYAB14443A;
    }

    public String getKEYAnewpwd() {
        return this.KEYAnewpwd;
    }

    public void setKEYAnewpwd(String KEYAnewpwd) {
        this.KEYAnewpwd = KEYAnewpwd;
    }

    public String getKEYAoldpwd() {
        return this.KEYAoldpwd;
    }

    public void setKEYAoldpwd(String KEYAoldpwd) {
        this.KEYAoldpwd = KEYAoldpwd;
    }

    public String getKEYBnewpwd() {
        return this.KEYBnewpwd;
    }

    public void setKEYBnewpwd(String KEYBnewpwd) {
        this.KEYBnewpwd = KEYBnewpwd;
    }

    public String getKEYBoldpwd() {
        return this.KEYBoldpwd;
    }

    public void setKEYBoldpwd(String KEYBoldpwd) {
        this.KEYBoldpwd = KEYBoldpwd;
    }

    public Boolean getEncrypt14443AControl() {
        return this.Encrypt14443AControl;
    }

    public void setEncrypt14443AControl(Boolean encrypt14443AControl) {
        this.Encrypt14443AControl = encrypt14443AControl;
    }

    public String getEncrypt14443AControlvalue() {
        return this.Encrypt14443AControlvalue;
    }

    public void setEncrypt14443AControlvalue(String encrypt14443AControlvalue) {
        this.Encrypt14443AControlvalue = encrypt14443AControlvalue;
    }

    public Integer getControlarea15693() {
        return this.Controlarea15693;
    }

    public void setControlarea15693(Integer controlarea15693) {
        this.Controlarea15693 = controlarea15693;
    }

    public String getControlvalue15693() {
        return this.Controlvalue15693;
    }

    public void setControlvalue15693(String controlvalue15693) {
        this.Controlvalue15693 = controlvalue15693;
    }
}
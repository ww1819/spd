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
    public String ObjectName = "";//对象名称

    //文字、条码或RFID对象的内容
    public String objectdata = "";

    public Float Xposition = 2f;// 文字、条码、图片在标签上的起始位置X坐标，单位是mm
    public Float Yposition = 2f;// 文字、条码、图片在标签上的起始位置Y坐标，单位是mm
    public Float startXposition = 1f;//线条、矩形在标签上起始点的位置，单位是mm
    public Float startYposition = 1f;// 线条、矩形在标签上起始点的位置，单位是mm
    public Float endXposition = 3f;// 线条、矩形在标签上终止点的位置，单位是mm
    public Float endYposition = 3f;// 线条、矩形在标签上终止点的位置，单位是mm

    public String barcodekind = "Code 128 Auto";//条码类型
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
    public Float barcodescale = 1f;//缩放系数，用于控制条码的宽度
    public Float barcodeheight = 10f;//条码的高度，单位mm

    public Integer direction = 0;//旋转方向，0是0度，1是90度，2是180度，3是270度
    public Integer errorcorrection = 0;//QR码的纠错等级
    public Integer charencoding = 0;//QR码的字符编码,0为UTF-8，1为GB2312
    public Integer qrversion = 0;//QR符号版本，1~40，数字越大包含字符越多，0为自动
    public Integer code39widthratio = 3;//Code39码的条宽比
    public Boolean code39startchar = true;//Code39码是否包含起始符*
    public Integer barcodealign = 0;//条码的对齐方式，0为左对齐，1为居中对齐，2为右对齐
    public Integer pdf417_rows = 0;//PDF417条码的行数，即层数，0为自动
    public Integer pdf417_columns = 0;//PDF417条码的列数，即数据的块数，左右两个标识块不算在内，0为自动
    public Integer datamatrixShape = 0;//DataMatrix的形状，0为自动，1为正方形，2为矩形
    public Integer textposition = 0;//文字相对于条码的方位，0为在条码下方，1为上方，2为不显示
    public Float textoffset = 0f;// 文字相对于条码的距离，单位mm
    public Integer textalign = 2;//文字相对于条码的对齐方式，0为左侧、1为右侧、2为居中、3为撑满

    public String textfont = "黑体";//字体名称
    public Integer fontstyle = 0;//字体样式，0 正常体，1 粗体，2 斜体，3 斜粗体
    public Float fontsize = 10f;//字体大小
    public Boolean blackbackground = false;//是否黑底白字
    public Float chargap = 0f;//字符间距，单位mm
    public Float charHZoom = 1f;//字符横向缩放倍数
    public Integer texttype = 0;//文本类型，0为单行文本，1为段落文本
    public Integer texttextalign = 0;// 文字的对齐方式，0为左对齐，1为居中，2为右对齐
    public Integer texttextvalign = 0;// 文字的对齐方式，0为顶边对齐，1为垂直居中，2为底边对齐
    public Float textwidth = 10f;// 段落文本的宽度，单位mm
    public Integer textwidthbeyound = 0;//超出段落文本的宽度后处理，0为自动换行，1为自动压扁字体，
    //2为自动缩小字体
    public Integer linegapindex = 0;// 段落文本的行间距，0为单倍，1为一倍半，2为双倍，3为自定义
    public Float linegap = 0f;// 段落文本的自定义行间距

    public Integer objectclass = 1;//线条对象的类别，1为直线，2为矩形
    public Integer lineclass = 1;//直线的类别，1为横线，2为竖线，3为斜线
    public Integer rectangleclass = 0;//矩形的类别，0为直角矩形，1为圆角矩形，2为椭圆
    public Float cornerRadius = 2f;//圆角矩形的半径，单位是mm
    public Float lineWidth = 0.4f;//线的宽度，单位是mm
    public Integer lineDashStyle = 0;//条线样式，0为实线，1为破折虚线，2为破折点虚线，3为破折点点虚线，
    //4为点虚线
    public Boolean fillRectangle = false;//是否填充矩形

    public Byte[] imagedata = null;//存储原始图像的数据
    public Float hscale = 1f;//横向缩放率百分比
    public Float vscale = 1f;//竖向缩放率百分比
    public Boolean aspectRatio = true;//图片是否保持长宽比，默认是
    public Boolean imagefixedsize = false;//是否固定尺寸
    public Float imagefixedwidth = 1f;//图片固定宽度，单位是mm
    public Float imagefixedheight = 1f;//图片固定高度，单位是mm


    //RFID写入对象
//UHF
    public Integer RFIDEncodertype = 0;//协议类型：0为UHF，1为HF 15693，2为HF 14443，3为NFC
    public Integer RFIDDatablock = 0;//写入数据区：0为EPC，1为USER
    public Integer RFIDDatatype = 0;//写入数据类型：0为文本，1为16进制，2为网址链接（NDEF），
    //3为纯文本（NDEF）
    public Integer RFIDTextencoding = 0;//文本编码：0为ASCII，1为UTF-8
    public Integer DataAlignment = 0;//数据对齐方式，0为后端补零，1为前端补零
    public Integer RFIDerrortimes = 2;//错误重试次数

    public Integer RFIDepccontrol = 0;//EPC区访问控制
    public Integer RFIDusercontrol = 0; //USER区访问控制
    public Integer RFIDtidcontrol = 0; //TID区访问控制
    public Integer RFIDaccesspwdcontrol = 0; //访问密码区访问控制
    public Integer RFIDkillpwdcontrol = 0; //灭活密码区访问控制
    //以上访问控制的值：0为开放，1为锁定，2为解除锁定，3为永久锁定，4为永久解除锁定，
//5为先解除锁定再重新锁定，6为先解除锁定再重新永久锁定
    public String RFIDaccessnewpwd = "00000000";//访问密码 新密码
    public String RFIDaccessoldpwd = "00000000";//访问密码 旧密码
    public Boolean usekillpwd = false;//是否使用灭活密码
    public String RFIDkillpwd = "00000000";//灭活密码

    //HF
    public Integer HFstartblock = 0;//高频要写的块的起点地址
    public Integer HFmodulepower = 0;//高频模块功率，0为自动
    public Boolean Encrypt14443A = false;//是否要加密14443A标签
    public Integer Sector14443A = 1;//需要加密的1443A的扇区
    public Integer KEYAB14443A = 0;//需要加密KEYA或KEYB，0为KEYA，1为KEYB，2为两个都加密
    public String KEYAnewpwd = "";//KEYA新密码
    public String KEYAoldpwd = "FFFFFFFFFFFF";//KEYA旧密码
    public String KEYBnewpwd = "";//KEYB新密码
    public String KEYBoldpwd = "FFFFFFFFFFFF";//KEYB旧密码
    public Boolean Encrypt14443AControl = false;//是否要设置14443A的控制字
    public String Encrypt14443AControlvalue = "FF078069";//默认的控制字
    public Integer Controlarea15693 = 0;//0为不设置，1为AFI，2为DSFID
    public String Controlvalue15693 = "00";//设置的值，默认是00
}
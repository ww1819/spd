package com.sb.sbInfo.service.impl;

import com.sb.sbInfo.domain.SbInfo;
import com.sb.sbInfo.domain.ZMPrinter.LabelFormat;
import com.sb.sbInfo.domain.ZMPrinter.LabelObject;
import com.sb.sbInfo.domain.ZMPrinter.PrinterStyle;
import com.sb.sbInfo.domain.ZMPrinter.ZMPrinter;
import com.sb.sbInfo.service.SbInfoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SbInfoServiceImpl implements SbInfoService {
    @Override
    public SbInfo selectSbInfoById(Long id) {
        return null;
    }

    @Override
    public String selectSbInfoByCode(String code) {
        return "";
    }

    @Override
    public String getSbLabelInfo(String code) {
        if (code == null || code.isEmpty()) {
            return "";
        }

        SbInfo sbInfo = new SbInfo();
        sbInfo.setId(1L);
        sbInfo.setName("测试设备");
        sbInfo.setType("测试类型");
        sbInfo.setCode(code);
        sbInfo.setStatus("正常");
        sbInfo.setLocation("测试位置");
        sbInfo.setDescription("这是一个测试设备");


        ZMPrinter zmPrinter = new ZMPrinter();
        zmPrinter.setPrinterinterface(PrinterStyle.RFID_USB);
        zmPrinter.setPrinterdpi(300);
        zmPrinter.setPrintSpeed(4);
        zmPrinter.setPrintDarkness(10);

        LabelFormat labelFormat = new LabelFormat();
        labelFormat.setLabelwidth(80);
        labelFormat.setLabelheight(20);
        labelFormat.setLabelrowgap(2);

        //编码
        LabelObject text01Object = new LabelObject();
        text01Object.setObjectName("text-01");
        text01Object.setObjectdata(sbInfo.getCode());
        text01Object.setXposition(30f);
        text01Object.setYposition(5f);
        text01Object.setTextfont("黑体");
        text01Object.setTextalign(1);

        //条形码
        LabelObject barcode01Object = new LabelObject();
        barcode01Object.setObjectName("barcode-01");
        barcode01Object.setObjectdata(sbInfo.getCode());
        barcode01Object.setXposition(30f);
        barcode01Object.setYposition(10f);
        barcode01Object.setBarcodekind("Code 128 Auto");
        barcode01Object.setBarcodescale(3f);
        barcode01Object.setBarcodeheight(6f);
        barcode01Object.setBarcodealign(1);
        barcode01Object.setTextfont("Arial");
        barcode01Object.setFontsize(10f);
        barcode01Object.setTextposition(2);

        //二维码
        LabelObject barcode02Object = new LabelObject();
        barcode02Object.setObjectName("barcode-02");
        barcode02Object.setObjectdata(sbInfo.getCode());
        barcode02Object.setXposition(58f);
        barcode02Object.setYposition(3f);
        barcode02Object.setBarcodekind("QR Code(2D)");
        barcode02Object.setBarcodescale(5f);
        barcode02Object.setBarcodealign(0);
        barcode02Object.setTextposition(2);

        // RFID标签
        LabelObject rfidUHFObject = new LabelObject();
        rfidUHFObject.setObjectName("rfiduhf-01");
        rfidUHFObject.setObjectdata("12345678");
        rfidUHFObject.setRFIDDatablock(0);
        rfidUHFObject.setRFIDDatatype(1);
        rfidUHFObject.setRFIDerrortimes(2);

        List<LabelObject> labelObjectList = new ArrayList<>();
        labelObjectList.add(text01Object);
        labelObjectList.add(barcode01Object);
        labelObjectList.add(barcode02Object);
        labelObjectList.add(rfidUHFObject);


        Map map = new HashMap<>();
        map.put("Printer", zmPrinter);
        map.put("LabelFormat", labelFormat);
        map.put("LabelObjectList", labelObjectList);
        map.put("Operate", "print");

        String labelInfo = null;
//        labelInfo = convertMapToJson(map);
        return labelInfo;
    }
}

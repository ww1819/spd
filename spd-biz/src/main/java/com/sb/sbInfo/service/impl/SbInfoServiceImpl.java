package com.sb.sbInfo.service.impl;

import com.sb.sbInfo.domain.SbInfo;
import com.sb.sbInfo.service.SbInfoService;

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
        String labelInfo = null;
        labelInfo = "{\n" +
                "  \"Printer\": {\n" +
                "    \"printerinterface\": \"RFID_USB\",\n" +
                "    \"printerdpi\": \"300\",\n" +
                "    \"printSpeed\": \"4\",\n" +
                "    \"printDarkness\": \"10\"\n" +
                "  },\n" +
                "  \"LabelFormat\": {\n" +
                "    \"labelwidth\": \"80\",\n" +
                "    \"labelheight\": \"20\",\n" +
                "    \"labelrowgap\": \"2\"\n" +
                "  },\n" +
                "  \"LabelObjectList\": [\n" +
                "    {\n" +
                "      \"ObjectName\": \"text-01\",\n" +
                "      \"objectdata\": \"XX1234567890\",\n" +
                "      \"Xposition\": \"30\",\n" +
                "      \"Yposition\": \"5\",\n" +
                "      \"textfont\": \"黑体\",\n" +
                "      \"fontsize\": \"10\",\n" +
                "      \"texttextalign\": \"1\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"ObjectName\": \"barcode-01\",\n" +
                "      \"objectdata\": \"XX1234567890\",\n" +
                "      \"Xposition\": \"30\",\n" +
                "      \"Yposition\": \"10\",\n" +
                "      \"barcodekind\": \"Code 128 Auto\",\n" +
                "      \"barcodescale\": \"3\",\n" +
                "      \"barcodeheight\": \"6\",\n" +
                "      \"barcodealign\": \"1\",\n" +
                "      \"textfont\": \"Arial\",\n" +
                "      \"fontsize\": \"10\",\n" +
                "      \"textposition\": \"2\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"ObjectName\": \"barcode-02\",\n" +
                "      \"objectdata\": \"XX1234567890\",\n" +
                "      \"Xposition\": \"58\",\n" +
                "      \"Yposition\": \"3\",\n" +
                "      \"barcodekind\": \"QR Code(2D)\",\n" +
                "      \"barcodescale\": \"5\",\n" +
                "      \"barcodealign\": \"0\",\n" +
                "      \"textposition\": \"2\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"ObjectName\": \"rfiduhf-01\",\n" +
                "      \"objectdata\": \"12345679\",\n" +
                "      \"RFIDEncodertype\": 0,\n" +
                "      \"RFIDDatablock\": \"0\",\n" +
                "      \"RFIDDatatype\": \"1\",\n" +
                "      \"RFIDerrortimes\": \"2\",\n" +
                "      \"RFIDTextencoding\": 1\n" +
                "    }\n" +
                "  ],\n" +
                "  \"Operate\": \"print\"\n" +
                "}";

        return labelInfo;
    }
}

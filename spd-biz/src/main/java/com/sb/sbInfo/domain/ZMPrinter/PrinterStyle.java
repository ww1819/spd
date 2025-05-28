package com.sb.sbInfo.domain.ZMPrinter;

public enum PrinterStyle {
    DRIVER(0),
    USB(1),
    NET(2),
    RFID_DRIVER(3),
    RFID_USB(4),
    RFID_NET(5);

    private final Integer value;

    PrinterStyle(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

}

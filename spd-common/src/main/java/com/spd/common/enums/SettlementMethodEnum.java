package com.spd.common.enums;

/**
 * 耗材仓库结算方式枚举（创建时必选，创建后不可修改）
 */
public enum SettlementMethodEnum {

    /** 入库结算 */
    INBOUND("1", "入库结算"),
    /** 出库结算 */
    OUTBOUND("2", "出库结算"),
    /** 消耗结算 */
    CONSUME("3", "消耗结算");

    private final String code;
    private final String label;

    SettlementMethodEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static SettlementMethodEnum fromCode(String code) {
        if (code == null) return null;
        for (SettlementMethodEnum e : values()) {
            if (e.code.equals(code)) return e;
        }
        return null;
    }
}

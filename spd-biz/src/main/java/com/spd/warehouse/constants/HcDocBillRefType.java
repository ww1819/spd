package com.spd.warehouse.constants;

/**
 * 单据引用类型（hc_doc_bill_ref.ref_type）
 */
public final class HcDocBillRefType {

    private HcDocBillRefType() {}

    /** 引用入库单办理出库单 */
    public static final String RK_TO_CK = "RK_TO_CK";
    /** 引用出库单办理退库单 */
    public static final String CK_TO_TK = "CK_TO_TK";
    /** 引用退库单办理出库单 */
    public static final String TK_TO_CK = "TK_TO_CK";
    /** 引用退库单办理退货单 */
    public static final String TK_TO_TH = "TK_TO_TH";
    /** 引用入库单办理退货单 */
    public static final String RK_TO_TH = "RK_TO_TH";
}

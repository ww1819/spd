package com.spd.common.bridge;

/**
 * 与前置机 / 云端稳态桥 action 对齐（勿在业务里硬编码散落字符串）
 */
public final class SpdBridgeActions
{
    private SpdBridgeActions()
    {
    }

    public static final String SUPPLIER_LIST_BY_HOSPITAL = "supplier.listByHospital";
    public static final String SUPPLIER_PROFILE = "supplier.profile";
    public static final String DELIVERY_QUERY = "delivery.query";
    public static final String DELIVERY_DOWNLOAD = "delivery.download";
    public static final String ORDER_PUBLISH_PAYLOAD = "order.publishPayload";
}

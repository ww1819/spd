package com.spd.system.service;

/**
 * 客户 68 分类 Service（系统模块最小依赖；实现在 biz）。
 * <p>系统标准模板表 {@code fd_category68} 为全库字典参照（无 tenant_id）；初始化/同步均以此为蓝本。</p>
 */
public interface ISbCustomerCategory68Service {

    /**
     * 新增客户时：按 fd_category68 全量初始化该客户的68分类（一一对应）
     */
    void initForCustomer(String customerId);

    /**
     * 以 fd_category68 为蓝本同步：更新已有数据，新增没有的数据
     */
    void syncFromStandard(String customerId);
}

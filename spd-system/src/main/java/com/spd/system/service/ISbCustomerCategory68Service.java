package com.spd.system.service;

/**
 * 客户68分类 Service 接口（系统模块最小依赖）
 * 新增客户时初始化、重置时同步，具体实现在 biz 模块
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

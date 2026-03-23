package com.spd.system.service;

/**
 * 租户/平台数据清理：物理删除，仅平台管理员可调用。
 */
public interface ITenantDataPurgeService
{
    /**
     * 删除指定租户在「耗材」侧的数据：所有含 tenant_id 的表按租户删除；
     * 删除 sys_user.customer_id = tenantId 的用户；删除 sys_user_post.tenant_id；
     * 不删除 sb_customer 记录。
     */
    int purgeConsumablesDataForTenant(String tenantId);

    /**
     * 删除指定客户在「设备」侧的数据：所有含 customer_id 的表按客户删除（不含 sb_customer 表本身）；
     * 删除 sys_user.customer_id = customerId 的用户。
     */
    int purgeEquipmentDataForCustomer(String customerId);

    /**
     * 平台级「初始化数据库」：清空租户与业务数据，保留 sys_menu、sys_dict_*、sys_config、sys_role、sys_role_menu、
     * sb_menu、定时任务相关表等；另保留 fd_category68、名称以 scm_/spd_ 开头的表（供应链/SPD 配置与字典类数据）。
     * 另支持「租户维度白名单」（见 {@code TenantDataPurgeServiceImpl} 中 {@code FULL_RESET_TENANT_NULL_PRESERVE_WHITELIST}）：
     * 对白名单中的表仅删除 tenant_id/customer_id 有值的行，保留 NULL/空白行（如平台操作记录）。
     * 仅保留 user_name='admin' 的用户并保证其超级管理员角色。
     * 调用前须二次确认口令。
     *
     * @param confirmToken 必须为固定口令 {@link #FULL_RESET_CONFIRM_TOKEN}
     */
    void purgeAllDataKeepPlatform(String confirmToken);

    String FULL_RESET_CONFIRM_TOKEN = "CONFIRM_PURGE_ALL_TENANT_DATA";
}

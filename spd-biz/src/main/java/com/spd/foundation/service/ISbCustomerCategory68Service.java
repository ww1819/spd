package com.spd.foundation.service;

import java.util.List;

import com.spd.foundation.domain.SbCustomerCategory68;
import com.spd.foundation.domain.SbCustomerCategory68Log;

/**
 * 客户 68 分类 Service（扩展系统模块接口）。
 * <p>系统标准模板为 {@code fd_category68}（全库字典/参照，无 tenant_id）；本接口管理客户表 {@code sb_customer_category68}，
 * 含初始化、同步、增删改查及操作记录。</p>
 *
 * @author spd
 */
public interface ISbCustomerCategory68Service extends com.spd.system.service.ISbCustomerCategory68Service {

    List<SbCustomerCategory68> selectList(SbCustomerCategory68 query);

    /** 树形列表（仅未删除） */
    List<SbCustomerCategory68> selectTree(String customerId);

    SbCustomerCategory68 selectById(String id);

    /** 新增（写操作记录） */
    int insert(SbCustomerCategory68 row);

    /** 修改（写操作记录） */
    int update(SbCustomerCategory68 row);

    /** 逻辑删除（写操作记录） */
    int deleteById(String id);

    List<SbCustomerCategory68Log> selectLogByCustomerId(String customerId);

    List<SbCustomerCategory68Log> selectLogByTargetId(String targetId);

    /** 根据分类名称批量更新当前客户下68分类的拼音简码 */
    void updatePinyinForCustomer(String customerId);
}

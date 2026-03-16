package com.spd.foundation.service;

import java.util.List;

import com.spd.foundation.domain.SbCustomerCategory68;
import com.spd.foundation.domain.SbCustomerCategory68Log;

/**
 * 客户68分类 Service 接口（扩展系统模块接口）
 * 以 fd_category68 为蓝本，客户可自行维护；含初始化、同步、增删改查及操作记录
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
}

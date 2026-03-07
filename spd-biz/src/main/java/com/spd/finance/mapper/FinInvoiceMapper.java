package com.spd.finance.mapper;

import com.spd.finance.domain.FinInvoice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 发票管理Mapper接口
 *
 * @author spd
 */
public interface FinInvoiceMapper {

    FinInvoice selectById(String id);

    List<FinInvoice> selectList(FinInvoice query);

    int insert(FinInvoice row);

    int update(FinInvoice row);

    /** 审核：设置 audit_status=1, audit_by, audit_time（仅待审核可审核） */
    int updateAuditStatus(@Param("id") String id, @Param("auditBy") String auditBy);

    /** 逻辑删除：设置 del_flag=1, delete_by, delete_time */
    int deleteById(@Param("id") String id, @Param("deleteBy") String deleteBy);
}

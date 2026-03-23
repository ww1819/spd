package com.spd.foundation.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.foundation.domain.SbCustomerCategory68;

/**
 * 客户68分类 Mapper 接口
 *
 * @author spd
 */
public interface SbCustomerCategory68Mapper {

    SbCustomerCategory68 selectById(@Param("id") String id);

    SbCustomerCategory68 selectByCustomerIdAndRefId(@Param("customerId") String customerId, @Param("refCategory68Id") Long refCategory68Id);

    List<SbCustomerCategory68> selectList(SbCustomerCategory68 query);

    /** 按客户查树形（未删除），按 parent_id, ref_category68_id 排序 */
    List<SbCustomerCategory68> selectTreeByCustomerId(@Param("customerId") String customerId);

    int insert(SbCustomerCategory68 row);

    int update(SbCustomerCategory68 row);

    /** 按客户统计已存在的 ref_category68_id（未删除） */
    List<Long> selectRefCategory68IdsByCustomerId(@Param("customerId") String customerId);

    /** 更新单条记录的拼音简码 */
    int updatePinyinById(SbCustomerCategory68 row);
}

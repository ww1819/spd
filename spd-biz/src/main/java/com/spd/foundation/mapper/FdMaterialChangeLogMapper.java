package com.spd.foundation.mapper;

import com.spd.foundation.domain.FdMaterialChangeLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 产品档案变更记录Mapper接口
 *
 * @author spd
 */
@Mapper
@Repository
public interface FdMaterialChangeLogMapper {

    /**
     * 新增变更记录
     */
    int insert(FdMaterialChangeLog record);

    /**
     * 按产品档案ID查询变更记录列表（按时间倒序）
     */
    List<FdMaterialChangeLog> selectByMaterialId(@Param("materialId") Long materialId);
}

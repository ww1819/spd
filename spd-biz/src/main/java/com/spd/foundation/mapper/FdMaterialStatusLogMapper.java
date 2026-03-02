package com.spd.foundation.mapper;

import com.spd.foundation.domain.FdMaterialStatusLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 耗材档案启用停用记录Mapper接口
 *
 * @author spd
 */
@Mapper
@Repository
public interface FdMaterialStatusLogMapper {

    /**
     * 新增启用停用记录
     */
    int insert(FdMaterialStatusLog record);

    /**
     * 按产品档案ID查询启用停用记录列表（按时间倒序）
     */
    List<FdMaterialStatusLog> selectByMaterialId(@Param("materialId") Long materialId);
}

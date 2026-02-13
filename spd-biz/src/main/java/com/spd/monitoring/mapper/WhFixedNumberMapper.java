package com.spd.monitoring.mapper;

import com.spd.monitoring.domain.WhFixedNumber;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 仓库定数监测 Mapper
 */
public interface WhFixedNumberMapper {

    /**
     * 查询仓库定数监测列表
     */
    List<WhFixedNumber> selectWhFixedNumberList(WhFixedNumber query);

    /**
     * 新增仓库定数监测
     */
    int insertWhFixedNumber(WhFixedNumber entity);

    /**
     * 更新仓库定数监测
     */
    int updateWhFixedNumber(WhFixedNumber entity);

    /**
     * 根据仓库ID和物资ID查询单条记录
     */
    WhFixedNumber selectByWarehouseAndMaterial(@Param("warehouseId") Long warehouseId,
                                               @Param("materialId") Long materialId);
}


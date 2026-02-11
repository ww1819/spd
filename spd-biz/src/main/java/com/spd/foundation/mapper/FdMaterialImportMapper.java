package com.spd.foundation.mapper;

import com.spd.foundation.domain.FdMaterialImport;

/**
 * 耗材产品导入中间表 Mapper
 *
 * 对应表：fd_material_import
 */
public interface FdMaterialImportMapper {

    /**
     * 插入一条导入记录
     *
     * @param record 导入记录
     * @return 影响行数
     */
    int insertFdMaterialImport(FdMaterialImport record);
}


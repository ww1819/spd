package com.spd.foundation.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.foundation.domain.FdJcType;

/**
 * 集采类型 Mapper
 */
public interface FdJcTypeMapper
{
    FdJcType selectFdJcTypeById(Long id);

    List<FdJcType> selectFdJcTypeList(FdJcType query);

    int insertFdJcType(FdJcType row);

    int updateFdJcType(FdJcType row);

    int deleteFdJcTypeById(@Param("id") Long id, @Param("deleteBy") String deleteBy);

    int countByTenantAndCode(@Param("tenantId") String tenantId, @Param("code") String code, @Param("excludeId") Long excludeId);

    int countByTenantAndName(@Param("tenantId") String tenantId, @Param("name") String name, @Param("excludeId") Long excludeId);

    int countMaterialRef(@Param("jcTypeId") Long jcTypeId);

    int countReportRef(@Param("jcTypeId") Long jcTypeId);
}

package com.spd.foundation.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.spd.foundation.domain.SpdScmMaterialArchive;

@Mapper
@Repository
public interface SpdScmMaterialArchiveMapper
{
    SpdScmMaterialArchive selectById(@Param("id") String id);

    SpdScmMaterialArchive selectByTenantAndScmArchiveId(@Param("tenantId") String tenantId,
        @Param("scmArchiveId") String scmArchiveId);

    List<SpdScmMaterialArchive> selectList(@Param("tenantId") String tenantId,
        @Param("keyword") String keyword,
        @Param("applyStatus") String applyStatus);

    int insert(SpdScmMaterialArchive row);

    int update(SpdScmMaterialArchive row);

    /**
     * 可推送候选：本租户未删除产品，且供应商已绑定平台编码
     */
    List<Map<String, Object>> selectPushCandidates(@Param("tenantId") String tenantId,
        @Param("keyword") String keyword);
}

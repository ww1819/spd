package com.spd.dashboard.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * 首页个人视角与常用菜单
 */
public interface HomeUserWorkspaceMapper
{
    String selectHomeView(@Param("userId") Long userId, @Param("tenantId") String tenantId);

    int upsertHomeView(@Param("id") String id, @Param("userId") Long userId,
        @Param("tenantId") String tenantId, @Param("homeView") String homeView);

    int upsertMenuHit(@Param("id") String id, @Param("userId") Long userId,
        @Param("tenantId") String tenantId, @Param("path") String path, @Param("title") String title);

    List<Map<String, Object>> selectTopMenus(@Param("userId") Long userId,
        @Param("tenantId") String tenantId, @Param("limit") int limit);

    List<Map<String, Object>> selectHomeIoQtyTrendByDay(@Param("beginDate") Date beginDate,
        @Param("endDate") Date endDate);

    List<Map<String, Object>> selectHomeApplyQtyTrendByDay(@Param("beginDate") Date beginDate,
        @Param("endDate") Date endDate);

    List<Map<String, Object>> selectHomePurchaseQtyTrendByDay(@Param("beginDate") Date beginDate,
        @Param("endDate") Date endDate);
}

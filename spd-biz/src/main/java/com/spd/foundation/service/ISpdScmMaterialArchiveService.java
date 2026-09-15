package com.spd.foundation.service;

import java.util.List;
import java.util.Map;

import com.spd.foundation.domain.SpdScmMaterialArchive;
import com.spd.foundation.domain.SpdScmMaterialPushFieldCfg;

/**
 * 院内产品档案 ↔ 供应链档案（推送 / 同步镜像 / 应用）
 */
public interface ISpdScmMaterialArchiveService
{
    List<Map<String, Object>> listLocalMaterialsForPush(String keyword);

    Object pushSelected(List<Long> materialIds);

    Map<String, Object> syncFromPlatform(String keyword);

    List<SpdScmMaterialArchive> listMirrors(String keyword, String applyStatus);

    Map<String, Object> apply(String mirrorId, List<String> fields);

    List<SpdScmMaterialPushFieldCfg> listFieldCfg();

    void saveFieldCfg(List<SpdScmMaterialPushFieldCfg> list);

    void ensureDefaultFieldCfg();
}

package com.sb.sbInfo.service;

import com.sb.sbInfo.domain.SbInfo;

public interface SbInfoService {
    /**
     * 查询设备信息
     *
     * @param id 设备ID
     * @return 设备信息
     */
    public SbInfo selectSbInfoById(Long id);

    public String selectSbInfoByCode(String code);

    public String getSbLabelInfo(String code);
}

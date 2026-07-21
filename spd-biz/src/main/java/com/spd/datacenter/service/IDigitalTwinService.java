package com.spd.datacenter.service;

import java.util.List;
import java.util.Map;

/**
 * 数字孪生监控大屏
 */
public interface IDigitalTwinService
{
    Map<String, Object> overview(Long warehouseId);

    Map<String, Object> shelves(Long warehouseId);

    Map<String, Object> alerts(Long warehouseId);

    List<Map<String, Object>> ioRealtime(Long warehouseId, Integer limit);

    List<Map<String, Object>> locationDetail(Long locationId, Long warehouseId);
}

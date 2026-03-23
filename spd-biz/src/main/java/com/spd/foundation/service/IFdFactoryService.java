package com.spd.foundation.service;

import java.util.List;
import java.util.Map;

import com.spd.foundation.domain.FdFactory;
import com.spd.foundation.domain.FdFactoryChangeLog;

/**
 * 厂家维护Service接口
 *
 * @author spd
 * @date 2024-03-04
 */
public interface IFdFactoryService
{
    FdFactory selectFdFactoryByFactoryId(Long factoryId);

    List<FdFactory> selectFdFactoryList(FdFactory fdFactory);

    int insertFdFactory(FdFactory fdFactory);

    int updateFdFactory(FdFactory fdFactory);

    int deleteFdFactoryByFactoryId(Long factoryId);

    void updateReferred(java.util.List<Long> ids);

    List<FdFactoryChangeLog> selectFactoryChangeLog(Long factoryId);

    Map<String, Object> validateFdFactoryImport(List<FdFactory> list, Boolean isUpdateSupport);

    String importFdFactory(List<FdFactory> list, Boolean isUpdateSupport, String operName, boolean confirmed);
}

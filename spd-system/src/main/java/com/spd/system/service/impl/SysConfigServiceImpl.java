package com.spd.system.service.impl;

import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.common.annotation.DataSource;
import com.spd.common.constant.CacheConstants;
import com.spd.common.constant.UserConstants;
import com.spd.common.core.redis.RedisCache;
import com.spd.common.core.text.Convert;
import com.spd.common.enums.DataSourceType;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.StringUtils;
import com.spd.system.domain.SysConfig;
import com.spd.system.mapper.SysConfigMapper;
import com.spd.system.service.ISysConfigService;

/**
 * 参数配置 服务层实现
 * 
 * @author spd
 */
@Service
public class SysConfigServiceImpl implements ISysConfigService
{
    @Autowired
    private SysConfigMapper configMapper;

    @Autowired
    private RedisCache redisCache;

    /**
     * 项目启动时，初始化参数到缓存
     */
    @PostConstruct
    public void init()
    {
        try {
            loadingConfigCache();
        } catch (Exception e) {
            // Redis连接失败时不阻止应用启动，只记录日志
            System.err.println("警告: Redis连接失败，配置缓存初始化跳过。错误信息: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 查询参数配置信息
     * 
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    @Override
    @DataSource(DataSourceType.MASTER)
    public SysConfig selectConfigById(Long configId)
    {
        SysConfig config = new SysConfig();
        config.setConfigId(configId);
        return configMapper.selectConfig(config);
    }

    /**
     * 根据键名查询参数配置信息
     * 
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey)
    {
        try {
            String configValue = Convert.toStr(redisCache.getCacheObject(getCacheKey(configKey)));
            if (StringUtils.isNotEmpty(configValue))
            {
                return configValue;
            }
        } catch (Exception e) {
            // Redis不可用时，直接从数据库查询
        }
        SysConfig config = new SysConfig();
        config.setConfigKey(configKey);
        SysConfig retConfig = configMapper.selectConfig(config);
        if (StringUtils.isNotNull(retConfig))
        {
            try {
                redisCache.setCacheObject(getCacheKey(configKey), retConfig.getConfigValue());
            } catch (Exception e) {
                // Redis不可用时，忽略缓存操作
            }
            return retConfig.getConfigValue();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取验证码开关
     * 
     * @return true开启，false关闭
     */
    @Override
    public boolean selectCaptchaEnabled()
    {
        String captchaEnabled = selectConfigByKey("sys.account.captchaEnabled");
        if (StringUtils.isEmpty(captchaEnabled))
        {
            return true;
        }
        return Convert.toBool(captchaEnabled);
    }

    /**
     * 查询参数配置列表
     * 
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysConfig> selectConfigList(SysConfig config)
    {
        return configMapper.selectConfigList(config);
    }

    /**
     * 新增参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int insertConfig(SysConfig config)
    {
        int row = configMapper.insertConfig(config);
        if (row > 0)
        {
            try {
                redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
            } catch (Exception e) {
                // Redis不可用时，忽略缓存操作
            }
        }
        return row;
    }

    /**
     * 修改参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int updateConfig(SysConfig config)
    {
        SysConfig temp = configMapper.selectConfigById(config.getConfigId());
        if (!StringUtils.equals(temp.getConfigKey(), config.getConfigKey()))
        {
            try {
                redisCache.deleteObject(getCacheKey(temp.getConfigKey()));
            } catch (Exception e) {
                // Redis不可用时，忽略缓存操作
            }
        }

        int row = configMapper.updateConfig(config);
        if (row > 0)
        {
            try {
                redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
            } catch (Exception e) {
                // Redis不可用时，忽略缓存操作
            }
        }
        return row;
    }

    /**
     * 批量删除参数信息
     * 
     * @param configIds 需要删除的参数ID
     */
    @Override
    public void deleteConfigByIds(Long[] configIds)
    {
        for (Long configId : configIds)
        {
            SysConfig config = selectConfigById(configId);
            if (StringUtils.equals(UserConstants.YES, config.getConfigType()))
            {
                throw new ServiceException(String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
            }
            configMapper.deleteConfigById(configId);
            try {
                redisCache.deleteObject(getCacheKey(config.getConfigKey()));
            } catch (Exception e) {
                // Redis不可用时，忽略缓存操作
            }
        }
    }

    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache()
    {
        try {
            List<SysConfig> configsList = configMapper.selectConfigList(new SysConfig());
            for (SysConfig config : configsList)
            {
                redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
            }
        } catch (Exception e) {
            // Redis连接失败时只记录日志，不抛出异常
            System.err.println("警告: 加载配置缓存失败，Redis可能未启动。错误信息: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache()
    {
        try {
            Collection<String> keys = redisCache.keys(CacheConstants.SYS_CONFIG_KEY + "*");
            redisCache.deleteObject(keys);
        } catch (Exception e) {
            // Redis不可用时，忽略缓存操作
        }
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache()
    {
        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 校验参数键名是否唯一
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public boolean checkConfigKeyUnique(SysConfig config)
    {
        Long configId = StringUtils.isNull(config.getConfigId()) ? -1L : config.getConfigId();
        SysConfig info = configMapper.checkConfigKeyUnique(config.getConfigKey());
        if (StringUtils.isNotNull(info) && info.getConfigId().longValue() != configId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 设置cache key
     * 
     * @param configKey 参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey)
    {
        return CacheConstants.SYS_CONFIG_KEY + configKey;
    }
}

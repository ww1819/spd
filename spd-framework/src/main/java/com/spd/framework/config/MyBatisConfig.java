package com.spd.framework.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import com.spd.common.utils.StringUtils;

/**
 * Mybatis支持*匹配扫描包
 * 
 * @author spd
 */
@Configuration
public class MyBatisConfig implements ResourceLoaderAware
{
    private static final Logger log = LoggerFactory.getLogger(MyBatisConfig.class);

    /** 与各环境 application-*.yml 保持一致；若未配置则使用此默认，避免 XML 未加载导致 Invalid bound statement */
    private static final String DEFAULT_MAPPER_LOCATIONS = "classpath*:mapper/**/*Mapper.xml";

    @Autowired
    private Environment env;

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    public static String setTypeAliasesPackage(String typeAliasesPackage)
    {
        ResourcePatternResolver resolver = (ResourcePatternResolver) new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);
        List<String> allResult = new ArrayList<String>();
        try
        {
            for (String aliasesPackage : typeAliasesPackage.split(","))
            {
                List<String> result = new ArrayList<String>();
                aliasesPackage = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                        + ClassUtils.convertClassNameToResourcePath(aliasesPackage.trim()) + "/" + DEFAULT_RESOURCE_PATTERN;
                Resource[] resources = resolver.getResources(aliasesPackage);
                if (resources != null && resources.length > 0)
                {
                    MetadataReader metadataReader = null;
                    for (Resource resource : resources)
                    {
                        if (resource.isReadable())
                        {
                            metadataReader = metadataReaderFactory.getMetadataReader(resource);
                            try
                            {
                                result.add(Class.forName(metadataReader.getClassMetadata().getClassName()).getPackage().getName());
                            }
                            catch (ClassNotFoundException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (result.size() > 0)
                {
                    HashSet<String> hashResult = new HashSet<String>(result);
                    allResult.addAll(hashResult);
                }
            }
            if (allResult.size() > 0)
            {
                typeAliasesPackage = String.join(",", (String[]) allResult.toArray(new String[0]));
            }
            else
            {
                throw new RuntimeException("mybatis typeAliasesPackage 路径扫描错误,参数typeAliasesPackage:" + typeAliasesPackage + "未找到任何包");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return typeAliasesPackage;
    }

    public Resource[] resolveMapperLocations(String[] mapperLocations)
    {
        ResourceLoader loader = resourceLoader != null ? resourceLoader : new DefaultResourceLoader();
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver(loader);
        List<Resource> resources = new ArrayList<Resource>();
        if (mapperLocations != null)
        {
            for (String mapperLocation : mapperLocations)
            {
                if (StringUtils.isEmpty(mapperLocation))
                {
                    continue;
                }
                try
                {
                    Resource[] mappers = resourceResolver.getResources(mapperLocation.trim());
                    resources.addAll(Arrays.asList(mappers));
                }
                catch (IOException e)
                {
                    log.warn("解析 MyBatis mapper 路径失败: {}, {}", mapperLocation, e.getMessage());
                }
            }
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception
    {
        String typeAliasesPackage = env.getProperty("mybatis.typeAliasesPackage");
        String mapperLocations = env.getProperty("mybatis.mapperLocations");
        if (StringUtils.isEmpty(mapperLocations))
        {
            mapperLocations = env.getProperty("mybatis.mapper-locations");
        }
        if (StringUtils.isEmpty(mapperLocations))
        {
            mapperLocations = DEFAULT_MAPPER_LOCATIONS;
            log.warn("未读取到 mybatis.mapperLocations，已使用默认: {}", DEFAULT_MAPPER_LOCATIONS);
        }
        String configLocation = env.getProperty("mybatis.configLocation");
        typeAliasesPackage = setTypeAliasesPackage(typeAliasesPackage);
        VFS.addImplClass(SpringBootVFS.class);

        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setTypeAliasesPackage(typeAliasesPackage);
        Resource[] mapperResources = resolveMapperLocations(StringUtils.split(mapperLocations, ","));
        if (mapperResources.length == 0)
        {
            log.error("MyBatis 未解析到任何 mapper XML，请检查 mybatis.mapperLocations 与依赖模块资源是否已打包进 classpath");
        }
        else
        {
            log.info("MyBatis 已加载 mapper XML 数量: {}", mapperResources.length);
        }
        sessionFactory.setMapperLocations(mapperResources);
        if (StringUtils.isNotEmpty(configLocation))
        {
            sessionFactory.setConfigLocation(new DefaultResourceLoader().getResource(Objects.requireNonNull(configLocation)));
        }
        return sessionFactory.getObject();
    }
}
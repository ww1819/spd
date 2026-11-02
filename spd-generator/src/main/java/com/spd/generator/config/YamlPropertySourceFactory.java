package com.spd.generator.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

/**
 * 支持 {@code @PropertySource} 加载 YAML（默认工厂只认 properties）。
 * 资源不存在时抛出 {@link FileNotFoundException}，以便配合 ignoreResourceNotFound。
 */
public class YamlPropertySourceFactory implements PropertySourceFactory
{
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException
    {
        Resource res = resource.getResource();
        if (!res.exists())
        {
            throw new FileNotFoundException(res + " cannot be opened because it does not exist");
        }
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(res);
        Properties properties = factory.getObject();
        String sourceName = name != null ? name : res.getFilename();
        return new PropertiesPropertySource(sourceName != null ? sourceName : "generator",
            properties != null ? properties : new Properties());
    }
}

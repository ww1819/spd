package com.spd.generator.config;

import java.io.IOException;
import java.util.Properties;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

/**
 * 支持 {@code @PropertySource} 加载 YAML（默认工厂只认 properties）。
 */
public class YamlPropertySourceFactory implements PropertySourceFactory
{
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException
    {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());
        Properties properties = factory.getObject();
        String sourceName = name != null ? name : resource.getResource().getFilename();
        return new PropertiesPropertySource(sourceName != null ? sourceName : "generator", properties != null ? properties : new Properties());
    }
}

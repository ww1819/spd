package com.spd.framework.config;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.TimeZone;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.spd.common.utils.MoneyScaleUtils;

/**
 * 程序注解配置
 *
 * @author spd
 */
@Configuration
// 表示通过aop框架暴露该代理对象,AopContext能够访问
@EnableAspectJAutoProxy(exposeProxy = true)
// 指定要扫描的Mapper类的包的路径
@MapperScan("com.spd.**.mapper")
public class ApplicationConfig
{
    /**
     * 时区配置
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization()
    {
        return jacksonObjectMapperBuilder -> {
            jacksonObjectMapperBuilder.timeZone(TimeZone.getDefault());
            jacksonObjectMapperBuilder.serializerByType(BigDecimal.class, new JsonSerializer<BigDecimal>()
            {
                @Override
                public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException
                {
                    if (value == null)
                    {
                        gen.writeNull();
                        return;
                    }
                    gen.writeNumber(MoneyScaleUtils.toPlainStripZeros(value));
                }
            });
        };
    }
}

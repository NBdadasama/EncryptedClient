package com.middleware.demo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties("spring.rabbitmq.connection")
public class MyRabbitModuleProperties {
    /**
     * 模块配置
     */
    List<ModuleProperties> modules;
}

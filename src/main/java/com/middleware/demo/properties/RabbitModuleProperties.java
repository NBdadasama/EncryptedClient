package com.middleware.demo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties("spring.rabbitmq")
public class RabbitModuleProperties {
    /**
     * 模块配置
     */
    List<ModuleProperties> modules;
}

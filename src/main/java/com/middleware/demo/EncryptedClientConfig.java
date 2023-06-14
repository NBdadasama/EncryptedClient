package com.middleware.demo;

import com.middleware.demo.client.Client;
import com.middleware.demo.client.impl.EncryptedClient;
import com.middleware.demo.properties.MyRabbitModuleProperties;
import com.middleware.demo.service.AbsproducerService;
import com.middleware.demo.service.RsapublickeyService;
import com.middleware.demo.utils.SpringUtil;
import jakarta.annotation.Resource;
import lombok.Data;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Configuration
@ConfigurationProperties("middleware.encryptedclient")
@Data
@ComponentScan("com.middleware.*")
@MapperScan(value = "com.middleware.demo.mapper")
public class EncryptedClientConfig {
    private String username;


    @Resource
    public SpringUtil springUtil;

    // 配置RabbitMQ连接工厂
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    // 创建RabbitTemplate实例
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // 设置消息转换器为Jackson2JsonMessageConverter
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        // 设置编码方式为UTF-8
        rabbitTemplate.setEncoding("UTF-8");
        // 设置事务模式为true
        rabbitTemplate.setChannelTransacted(true);
        return rabbitTemplate;
    }

    @Bean
    public AmqpAdmin amqpAdmin(RabbitTemplate rabbitTemplate){
        return new RabbitAdmin(rabbitTemplate);
    }

    @Bean
    public Client client(ConnectionFactory connectionFactory,
                         AmqpAdmin amqpAdmin,
                         MyRabbitModuleProperties myRabbitModuleProperties,
                         RabbitTemplate rabbitTemplate,
                         AbsproducerService absproducerService,
                         RsapublickeyService rsapublickeyService){
        return new EncryptedClient(username,connectionFactory,amqpAdmin,myRabbitModuleProperties,rabbitTemplate,absproducerService,rsapublickeyService);
    }

}

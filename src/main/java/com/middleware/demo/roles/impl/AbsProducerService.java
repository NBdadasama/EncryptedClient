package com.middleware.demo.roles.impl;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;
import com.middleware.demo.roles.ProducerService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.PublicKey;

import static com.middleware.demo.utils.FileUtil.getFile;

@Slf4j
@Data
@Service
public class AbsProducerService implements ProducerService {


    private RabbitTemplate rabbitTemplate;

    private PublicKey otherPublicRSAKey;

    /**
     * 交换机
     */
    private String exchange;

    /**
     * 路由
     */
    private String routingKey;

    /**
     * 过期时间
     */
    private long ttl;

    @Autowired
    public AbsProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void send(String message) {
        // 开启事务
        rabbitTemplate.setChannelTransacted(true);
        // 生成一个随机的AES密钥对象
        AES aes = new AES();
        // 使用AES加密消息内容
        String encryptedMessage = aes.encryptHex(message);

        RSA rsa = new RSA(null,otherPublicRSAKey);

        // 使用RSA公钥加密AES密钥
        byte[] encryptedAESKey =rsa.encrypt(aes.getSecretKey().getEncoded(), KeyType.PublicKey);
        // 设置一个合适的TTL，单位为毫秒，这里假设为10秒
        long ttl = 10000;
        // 创建一个消息对象，包含混合加密后的消息和TTL
        Message msg = MessageBuilder.withBody(encryptedMessage.getBytes())
                .setHeader("encryptedKey", encryptedAESKey)
                .setExpiration(String.valueOf(ttl))
                .build();
        // 将消息发送到指定的交换器和路由键
        rabbitTemplate.convertAndSend(this.exchange, this.routingKey, msg);
    }

    @Override
    public void sendPic(byte[] pic) {
        // 开启事务
        rabbitTemplate.setChannelTransacted(true);
        // 生成一个随机的AES密钥对象
        AES aes = new AES();
        // 使用AES加密消息内容
        String encryptedMessage = aes.encryptHex(pic);
        // 从数据库获取RSA公钥
        RSA rsa = new RSA(null,otherPublicRSAKey);
        // 使用RSA公钥加密AES密钥
        byte[] encryptedAESKey =rsa.encrypt(aes.getSecretKey().getEncoded(), KeyType.PublicKey);
        // 设置一个合适的TTL，单位为毫秒，这里设为10秒
        long ttl = 10000;
        // 创建一个消息对象，包含混合加密后的消息和TTL
        Message msg = MessageBuilder.withBody(encryptedMessage.getBytes())
                .setHeader("encryptedKey", encryptedAESKey)
                .setExpiration(String.valueOf(ttl))
                .build();
        // 将消息发送到指定的交换器和路由键
        rabbitTemplate.convertAndSend(this.exchange, this.routingKey, pic);
    }

    @Override
    public void sendFile() {
        //选择要传送的文件
        File file = getFile();

        try {
            InputStream input = new FileInputStream(file);
            try {
                byte[] message = new byte[input.available()];
                // 开启事务
                rabbitTemplate.setChannelTransacted(true);
                // 生成一个随机的AES密钥对象
                AES aes = new AES();
                // 使用AES加密消息内容
                String encryptedMessage = aes.encryptHex(message);

                RSA rsa = new RSA(null,otherPublicRSAKey);

                // 使用RSA公钥加密AES密钥
                byte[] encryptedAESKey =rsa.encrypt(aes.getSecretKey().getEncoded(), KeyType.PublicKey);
                // 设置一个合适的TTL，单位为毫秒，这里假设为10秒
                long ttl = 10000;
                // 创建一个消息对象，包含混合加密后的消息和TTL
                Message msg = MessageBuilder.withBody(encryptedMessage.getBytes())
                        .setHeader("encryptedKey", encryptedAESKey)
                        .setExpiration(String.valueOf(ttl))
                        .build();
                // 将消息发送到指定的交换器和路由键
                rabbitTemplate.convertAndSend(this.exchange, this.routingKey, message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }
}


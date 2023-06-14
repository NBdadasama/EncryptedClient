package com.middleware.demo.roles;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class Receiver {
    private String privateRSAKey;

    private String encryptedAESKey;


    // 注入RabbitTemplate
    @Autowired
    private RabbitTemplate rabbitTemplate;


    // 使用RSA解密AES密钥
    private String decryptByRSA(String key) {
        RSA rsa = new RSA(privateRSAKey,"");

        return rsa.decryptStr(key, KeyType.PrivateKey, StandardCharsets.UTF_8);
    }

    // 使用AES解密消息内容
    private String decryptByAES(String message, String key) {
        AES aes = SecureUtil.aes(key.getBytes());

        return aes.decryptStr(message);
    }

    // 处理消息
    @RabbitListener(queues = "test")
    public void receive(Message msg) {
        // 开启事务
        rabbitTemplate.setChannelTransacted(true);
        // 获取混合加密后的消息内容和AES密钥
        String encryptedMessage = new String(msg.getBody());
        encryptedAESKey = msg.getMessageProperties().getHeader("encryptedKey");
        long deliveryTag = msg.getMessageProperties().getDeliveryTag();
        // 使用RSA解密AES密钥
        String aesKey = decryptByRSA(encryptedAESKey);
        // 使用AES解密消息内容
        String decryptedMessage = decryptByAES(encryptedMessage, aesKey);
        // 处理解密后的消息内容，比如打印到控制台等
        System.out.println(decryptedMessage);
        // 向消息中间件发送确认信号，触发事务提交和消息删除
        // rabbitTemplate.basicAck();
    }

}

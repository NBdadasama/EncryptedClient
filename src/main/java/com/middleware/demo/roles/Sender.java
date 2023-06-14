package com.middleware.demo.roles;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;
import lombok.Data;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.middleware.demo.utils.SignUtil.generateAESKey;

@Component
@Data
public class Sender {

    private String otherPublicRSAKey;

    private String exchange;

    private String routingKey;

    // 注入RabbitTemplate
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public Sender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    // 发送消息
    public void send(String message) {
        // 开启事务
        rabbitTemplate.setChannelTransacted(true);
        // 生成一个随机的AES密钥对象
        AES aes = generateAESKey();
        // 使用AES加密消息内容
        String encryptedMessage = aes.encryptHex(message);

        RSA rsa = new RSA("",otherPublicRSAKey);

        // 使用RSA公钥加密AES密钥
        String encryptedAESKey =rsa.encryptHex(aes.getSecretKey().getEncoded().toString(), KeyType.PublicKey);
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

}

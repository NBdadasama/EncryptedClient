package com.middleware.demo.roles.impl;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;
import com.middleware.demo.roles.ConsumerService;
import com.rabbitmq.client.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Data
@Service
public class AbsConsumerService implements ConsumerService {
    /**
     * 消息
     */
    private Message message;

    /**
     * 通道
     */
    private Channel channel;

    private RSA rsa;

    private byte[] encryptedAESKey;



    // 注入RabbitTemplate
    private RabbitTemplate rabbitTemplate;
    @Autowired
    public AbsConsumerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;

    }


    // 使用RSA解密AES密钥
    private byte[] decryptByRSA(byte[] key) {
        return rsa.decrypt(key, KeyType.PrivateKey);
    }

    // 使用AES解密消息内容
    private String decryptByAES(String message, byte[] key) {
        AES aes = SecureUtil.aes(key);

        return aes.decryptStr(message);
    }


    @Override
    @RabbitListener
    public void onMessage(Message message, Channel channel) throws Exception {
        this.message = message;
        this.channel = channel;
        // 开启事务
        rabbitTemplate.setChannelTransacted(true);
        // 获取混合加密后的消息内容和AES密钥
        String encryptedMessage = new String(message.getBody());
        encryptedAESKey = message.getMessageProperties().getHeader("encryptedKey");
        // 使用RSA解密AES密钥
        byte[] aesKey = decryptByRSA(encryptedAESKey);
        // 使用AES解密消息内容
        String decryptedMessage = decryptByAES(encryptedMessage, aesKey);
        // 处理解密后的消息内容，比如打印到控制台等
        System.out.println(decryptedMessage);
        // 向消息中间件发送确认信号，触发事务提交和消息删除
        ack();
    }

    /**
     * 确认消息
     */
    protected void ack() throws IOException {
        ack(Boolean.FALSE);
    }

    /**
     * 拒绝消息
     */
    protected void nack() throws IOException {
        nack(Boolean.FALSE, Boolean.FALSE);
    }

    /**
     * 拒绝消息
     */
    protected void basicReject() throws IOException {
        basicReject(Boolean.FALSE);
    }

    /**
     * 拒绝消息
     * @param multiple 当前 DeliveryTag 的消息是否确认所有 true 是， false 否
     */
    protected void basicReject(Boolean multiple) throws IOException {
        this.channel.basicReject(this.message.getMessageProperties().getDeliveryTag(), multiple);
    }

    /**
     * 是否自动确认
     * @param multiple 当前 DeliveryTag 的消息是否确认所有 true 是， false 否
     */
    protected void ack(Boolean multiple) throws IOException {
        this.channel.basicAck(this.message.getMessageProperties().getDeliveryTag(), multiple);
    }

    /**
     * 拒绝消息
     * @param multiple 当前 DeliveryTag 的消息是否确认所有 true 是， false 否
     * @param requeue 当前 DeliveryTag 消息是否重回队列 true 是 false 否
     */
    protected void nack(Boolean multiple, Boolean requeue) throws IOException {
        this.channel.basicNack(this.message.getMessageProperties().getDeliveryTag(), multiple, requeue);
    }
}

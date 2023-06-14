package com.middleware.demo.roles;

public interface ProducerService {
    /**
     * 发送消息
     * @param message
     */
    void send(String message);

    void sendPic(byte[] pic);

    void sendFile();
}

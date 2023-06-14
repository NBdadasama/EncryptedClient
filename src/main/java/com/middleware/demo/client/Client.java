package com.middleware.demo.client;

public interface Client {

    /**
     * 简单登录，将上线用户记录到数据库中
     * @param username
     */
    void login(String username);


    /**
     * 创建与其他客户端的连接
     * @param username
     */
    void addConnection(String username);

    /**
     * 创建群聊
     * @param groupName
     */
    void addGroup(String groupName);

    /**
     * 绑定群聊
     * @param groupName
     * @param username
     */
    void bindGroup(String groupName,String username);

    /**
     * 发送文本信息给其他客户端(群聊)
     * @param username
     * @param message
     */
    void send(String username, String message);

    /**
     * 发送图片给其他客户端(群聊)
     * @param username
     * @param pic
     */
    void sendPic(String username,byte[] pic);

    /**
     * 发送本地文件给其他客户端(群聊)
     */
    void sendFile(String username);

}

package com.middleware.demo.emun;

import lombok.Getter;

public enum RabbitEnum {
    QUEUE("xxx.{}.queue", "队列名称"),

    EXCHANGE("xxx.{}.exchange", "交换机名称"),

    ROUTER_KEY("xxx.{}.key", "路由名称"),
    ;

    RabbitEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Getter
    private String value;

    @Getter
    private String desc;


}

package com.middleware.demo.emun;

import lombok.Getter;

public enum MyRabbitEnum {
    QUEUE("{}.str.{}", "队列名称"),

    EXCHANGE("{}.exchange", "交换机名称"),

    ROUTER_KEY("{}.str.{}.key", "路由名称"),
    ;

    MyRabbitEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Getter
    private String value;

    @Getter
    private String desc;
}

package com.middleware.demo.roles;

import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

public interface ConsumerService extends ChannelAwareMessageListener {
}

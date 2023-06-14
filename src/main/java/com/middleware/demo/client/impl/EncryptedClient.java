package com.middleware.demo.client.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.RSA;
import com.middleware.demo.client.Client;
import com.middleware.demo.emun.MyRabbitEnum;
import com.middleware.demo.emun.RabbitEnum;
import com.middleware.demo.emun.RabbitExchangeTypeEnum;
import com.middleware.demo.entity.Absproducer;
import com.middleware.demo.entity.Rsapublickey;
import com.middleware.demo.factories.ConsumerContainerFactory;
import com.middleware.demo.properties.ModuleProperties;
import com.middleware.demo.properties.MyRabbitModuleProperties;
import com.middleware.demo.roles.CustomRetryListener;
import com.middleware.demo.roles.ProducerService;
import com.middleware.demo.roles.impl.AbsConsumerService;
import com.middleware.demo.roles.impl.AbsProducerService;
import com.middleware.demo.service.AbsproducerService;
import com.middleware.demo.service.RsapublickeyService;
import com.middleware.demo.translation.Translate;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j

public class EncryptedClient implements Client {

    @Getter
    @Setter
    private String username;


    /**
     * MQ链接工厂
     */
    private ConnectionFactory connectionFactory;

    /**
     * MQ操作管理器
     */
    private AmqpAdmin amqpAdmin;

    /**
     * YML配置
     */
    private MyRabbitModuleProperties myRabbitModuleProperties;

    // 注入RabbitTemplate
    private RabbitTemplate rabbitTemplate;


    private ProducerService producerService;

    private RsapublickeyService rsapublickeyService;

    private AbsproducerService absproducerService;

    private RSA myRsa;

    private AbsConsumerService absConsumerService;


    public EncryptedClient(
            String username,
            ConnectionFactory connectionFactory,
                           AmqpAdmin amqpAdmin,
                           MyRabbitModuleProperties myRabbitModuleProperties,
                           RabbitTemplate rabbitTemplate,
                           AbsproducerService absproducerService,
                           RsapublickeyService rsapublickeyService) {
        this.username = username;
        this.connectionFactory = connectionFactory;
        this.amqpAdmin = amqpAdmin;
        this.myRabbitModuleProperties = myRabbitModuleProperties;
        this.rabbitTemplate = rabbitTemplate;
        this.absproducerService = absproducerService;
        this.rsapublickeyService = rsapublickeyService;
    }

    private RSA rsaInit() {
        // 生成新的RSA，并上传自己的公钥
        RSA rsa = new RSA();

        Rsapublickey rsapublickey = new Rsapublickey();
        rsapublickey.setUsername(username);
        rsapublickey.setRsapublickey(rsa.getPublicKey().getEncoded());
        rsapublickeyService.saveOrUpdate(rsapublickey);
        return rsa;
    }

    /**
     * 简单登录，将用户记录到数据库中
     *
     * @param username
     */
    @Override
    public void login(String username) {
        Absproducer absproducer = new Absproducer();
        absproducer.setUsername(username);
        absproducerService.saveOrUpdate(absproducer);
    }

    @Override
    public void addConnection(String consumerName) {
        this.myRsa=rsaInit();
        List<ModuleProperties> modules = myRabbitModuleProperties.getModules();
        for (ModuleProperties module : modules) {
            // 创建对方客户端的队列和交换机
            Queue queue = genQueue(username,consumerName, module);
            Exchange exchange = genQueueExchange(consumerName, module);
            queueBindExchange(queue, exchange, username, consumerName);
            bindConsumer(queue, exchange, consumerName,module);
        }
    }

    /**
     * 创建群聊
     *
     * @param groupName
     */
    @Override
    public void addGroup(String groupName) {

    }

    /**
     * 绑定群聊
     *
     * @param groupName
     * @param username
     */
    @Override
    public void bindGroup(String groupName, String username) {

    }

    @Override
    public void send(String consumerName, String message) {
        bindProducer(consumerName);
        producerService.send(message);
    }

    /**
     * 发送图片给其他客户端
     *
     * @param consumerName
     * @param pic
     */
    @Override
    public void sendPic(String consumerName, byte[] pic) {
        bindProducer(consumerName);
        producerService.sendPic(pic);
    }

    /**
     * 发送本地文件给其他客户端
     */
    @Override
    public void sendFile(String consumerName) {
        bindProducer(consumerName);
        producerService.sendFile();
    }

    @Override
    public void sendTranslate(String consumerName, String msg, String language) {
        bindProducer(consumerName);
        Translate translate = new Translate();
        // 发送要翻译的消息以及要翻译的语言
        producerService.send(translate.inPut(msg,language));
    }


    /**
     * 绑定生产者（设置生产者要发送到的交换机和以及路由键，以发送到对面客户端绑定的监听队列）
     *
     * @param consumerName
     */
    private void bindProducer(String consumerName) {
        try {
            Absproducer absproducer = absproducerService.selectAbsproducerByConsumerName(consumerName);
            if (!absproducer.equals(null)) {
                AbsProducerService absProducerService = new AbsProducerService(rabbitTemplate);
                Rsapublickey rsapublickey = rsapublickeyService.selectRsapublickeyByUsername(consumerName);
                RSA rsa = new RSA(null,rsapublickey.getRsapublickey());
                absProducerService.setOtherPublicRSAKey(rsa.getPublicKey());
                absProducerService.setExchange(StrUtil.format(MyRabbitEnum.EXCHANGE.getValue(), consumerName));
                absProducerService.setRoutingKey(StrUtil.format(MyRabbitEnum.ROUTER_KEY.getValue(), username, consumerName));
                this.producerService = absProducerService;
                log.debug("绑定生产者: {}", username);
            }
        } catch (Exception e) {
            log.warn("无法在容器中找到该生产者[{}]，若需要此生产者则需要做具体实现", username);
        }
    }

    /**
     * 绑定消费者(获取要监听对象的RSA公钥)
     * @param queue
     * @param exchange
     * @param consumerName
     */
    private void bindConsumer(Queue queue, Exchange exchange, String consumerName, ModuleProperties module) {
        CustomRetryListener customRetryListener = null;
        this.absConsumerService = new AbsConsumerService(rabbitTemplate);
        this.absConsumerService.setRsa(myRsa);
        try {
            ConsumerContainerFactory factory = ConsumerContainerFactory.builder()
                    .connectionFactory(connectionFactory)
                    .queue(genQueue(consumerName,username,module))
                    .exchange(genQueueExchange(username,module))
                    .consumer(this.absConsumerService)
                    .retryListener(customRetryListener)
                    .autoAck(module.getAutoAck())
                    .amqpAdmin(amqpAdmin)
                    .build();
            SimpleMessageListenerContainer container = factory.getObject();
            if (Objects.nonNull(container)) {
                container.start();
            }
            log.debug("绑定消费者: {}", username);
        } catch (Exception e) {
            log.warn("无法在容器中找到该消费者[{}]，若需要此消费者则需要做具体实现", username);
        }
    }

    /**
     * 队列绑定交换机
     *
     * @param queue
     * @param exchange
     * @param consumerName
     */
    private void queueBindExchange(Queue queue, Exchange exchange, String username, String consumerName) {
        log.debug("初始化交换机: {}", exchange.getName());
        String queueName = queue.getName();
        String exchangeName = exchange.getName();
        String routingKey = StrUtil.format(MyRabbitEnum.ROUTER_KEY.getValue(), username, consumerName);
        Binding binding = new Binding(queueName,
                Binding.DestinationType.QUEUE,
                exchangeName,
                routingKey,
                null);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareBinding(binding);
        log.debug("队列绑定交换机: 队列: {}, 交换机: {}", queueName, exchangeName);
    }

    /**
     * 创建交换机
     *
     * @param consumerName
     * @param module
     * @return
     */
    private Exchange genQueueExchange(String consumerName, ModuleProperties module) {
        ModuleProperties.Exchange exchange = module.getExchange();
        RabbitExchangeTypeEnum exchangeType = exchange.getType();
        exchange.setName(StrUtil.format(MyRabbitEnum.EXCHANGE.getValue(), consumerName));
        String exchangeName = exchange.getName();
        Boolean isDurable = exchange.isDurable();
        Boolean isAutoDelete = exchange.isAutoDelete();
        Map<String, Object> arguments = exchange.getArguments();
        return getExchangeByType(exchangeType, exchangeName, isDurable, isAutoDelete, arguments);
    }

    /**
     * 根据类型生成交换机
     *
     * @param exchangeType
     * @param exchangeName
     * @param isDurable
     * @param isAutoDelete
     * @param arguments
     * @return
     */
    private Exchange getExchangeByType(RabbitExchangeTypeEnum exchangeType, String exchangeName, Boolean isDurable, Boolean isAutoDelete, Map<String, Object> arguments) {
        AbstractExchange exchange = null;
        switch (exchangeType) {
            // 直连交换机
            case DIRECT:
                exchange = new DirectExchange(exchangeName, isDurable, isAutoDelete, arguments);
                break;
            // 主题交换机
            case TOPIC:
                exchange = new TopicExchange(exchangeName, isDurable, isAutoDelete, arguments);
                break;
            //扇形交换机
            case FANOUT:
                exchange = new FanoutExchange(exchangeName, isDurable, isAutoDelete, arguments);
                break;
            // 头交换机
            case HEADERS:
                exchange = new HeadersExchange(exchangeName, isDurable, isAutoDelete, arguments);
                break;
            default:
                log.warn("未匹配到交换机类型");
                break;
        }
        return exchange;
    }

    /**
     * 创建队列
     *
     * @param consumerName
     * @return
     */
    private Queue genQueue(String username,String consumerName, ModuleProperties module) {
        ModuleProperties.Queue queue = module.getQueue();
        queue.setName(StrUtil.format(MyRabbitEnum.QUEUE.getValue(), username, consumerName));
        log.debug("初始化队列: {}", queue.getName());
        Map<String, Object> arguments = queue.getArguments();
        if (MapUtil.isEmpty(arguments)) {
            arguments = new HashMap<>();
        }

        // 转换ttl的类型为long
        if (arguments.containsKey("x-message-ttl")) {
            arguments.put("x-message-ttl", Convert.toLong(arguments.get("x-message-ttl")));
        }

        // 绑定死信队列
        String deadLetterExchange = queue.getDeadLetterExchange();
        String deadLetterRoutingKey = queue.getDeadLetterRoutingKey();
        if (StrUtil.isNotBlank(deadLetterExchange) && StrUtil.isNotBlank(deadLetterRoutingKey)) {
            deadLetterExchange = StrUtil.format(RabbitEnum.EXCHANGE.getValue(), deadLetterExchange);
            deadLetterRoutingKey = StrUtil.format(RabbitEnum.ROUTER_KEY.getValue(), deadLetterRoutingKey);
            arguments.put("x-dead-letter-exchange", deadLetterExchange);
            arguments.put("x-dead-letter-routing-key", deadLetterRoutingKey);
            log.debug("绑定死信队列: 交换机: {}, 路由: {}", deadLetterExchange, deadLetterRoutingKey);
        }

        return new Queue(queue.getName(), queue.isDurable(), queue.isExclusive(), queue.isAutoDelete(), arguments);
    }
}






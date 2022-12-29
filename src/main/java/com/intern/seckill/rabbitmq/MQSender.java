package com.intern.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ的消息发送者
 * @author Ricardo.A.Gu
 * @since 1.0.0
 */
@Service
@Slf4j
public class MQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(Object msg) {
        log.info("发送消息：" + msg);
        rabbitTemplate.convertAndSend("fanoutExchange", "", msg);
    }

    public void send01(Object msg) {
        log.info("发送red消息：" + msg);
        rabbitTemplate.convertAndSend("directExchange", "queue.red", msg);
    }

    public void send02(Object msg) {
        log.info("发送green消息：" + msg);
        rabbitTemplate.convertAndSend("directExchange", "queue.green", msg);
    }

    public void send03(Object msg) {
        log.info("发送消息(QUEUE01接收)：" + msg);
        rabbitTemplate.convertAndSend("topicExchange", "queue.red.message", msg);
    }

    public void send04(Object msg) {
        log.info("发送消息(两个QUEUE接收)：" + msg);
        rabbitTemplate.convertAndSend("topicExchange", "message.queue.green", msg);
    }

}

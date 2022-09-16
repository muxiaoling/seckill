package com.tang.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Component
@Slf4j
public class MQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE = "testExchange1";

    //发送秒杀信息
    public void sendSeckillMessage(String message) {
        log.info("发送消息" + message);
        rabbitTemplate.convertAndSend(EXCHANGE, "test1.msg", message);
    }
//
//
//    public void send (Object msg) {
//        log.info("发送消息" + msg);
//        rabbitTemplate.convertAndSend("fanoutExchange", "", msg);
//    }
//
//
//    public void send01(Object msg) {
//        log.info("发送red消息" + msg);
//        rabbitTemplate.convertAndSend("directExchange", "queue_red", msg);
//    }
//
//    public void send02(Object msg) {
//        log.info("发送green消息" + msg);
//        rabbitTemplate.convertAndSend("directExchange", "queue_green", msg);
//    }
//
//    public void send03(Object msg) {
//        log.info("发送消息（QUEUE_01接收）:" + msg);
//        rabbitTemplate.convertAndSend("topicExchange", "queue.red.message", msg);
//    }
//
//    public void send04(Object msg) {
//        log.info("发送消息（QUEUE_02接收）:" + msg);
//        rabbitTemplate.convertAndSend("topicExchange", "message.queue.green.tjh", msg);
//    }



}


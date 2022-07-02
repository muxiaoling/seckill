package com.tang.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQTopicConfig {

    private static final String QUEUE = "seckillQueue";
    private static final String EXCHANGE = "seckillExchange";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE);
    }
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE);
    }
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(topicExchange()).with("seckill.#");
    }
/*    private static final String QUEUE_01 = "queue_topic01";
    private static final String QUEUE_02 = "queue_topic02";
    private static final String EXCHANGE = "topicExchange";
    private static final String ROUTINGKEY01 = "#.queue.#";
    private static final String ROUTINGKEY02 = "*.queue.#";

    @Bean
    public Queue queue_01() {
        return new Queue(QUEUE_01);
    }
    @Bean
    public Queue queue_02() {
        return new Queue(QUEUE_02);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE);
    }
    @Bean
    public Binding binding_01() {
        return BindingBuilder.bind(queue_01()).to(topicExchange()).with(ROUTINGKEY01);
    }
    @Bean
    public Binding binding_02() {
        return BindingBuilder.bind(queue_02()).to(topicExchange()).with(ROUTINGKEY02);
    }*/
}

package com.fooddelivery.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.queue}")
    private String queueName;

    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Queue deliveryQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding orderCreatedBinding(Queue deliveryQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(deliveryQueue)
                .to(orderExchange)
                .with(ORDER_CREATED_ROUTING_KEY);
    }
}

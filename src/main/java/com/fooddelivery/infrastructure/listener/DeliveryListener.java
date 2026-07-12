package com.fooddelivery.infrastructure.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.domain.event.OrderCreateEvent;
import com.fooddelivery.infrastructure.service.DeliverOrderComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryListener {

    private final ObjectMapper objectMapper;
    private final DeliverOrderComponent deliverOrderComponent;

    @RabbitListener(queues = "${spring.rabbitmq.queue}")
    public void handleDeliveryEvents(String event) throws Exception {
        log.info("Received event: {}", event);
        OrderCreateEvent orderCreateEvent = objectMapper.readValue(event, OrderCreateEvent.class);
        deliverOrderComponent.deliverOrder(orderCreateEvent.orderItems());
    }
}

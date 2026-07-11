package com.fooddelivery.infrastructure.service;

import com.fooddelivery.domain.event.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DeliverOrderComponent {

    public void deliverOrder(List<OrderItem> orderItems) {
        log.info("Processing order delivery for items: {}", orderItems);
    }
}

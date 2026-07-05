package com.fooddelivery.domain.event;

import lombok.Builder;

import java.util.List;

@Builder
public record OrderCreateEvent(List<OrderItem> orderItems) {
    public static OrderCreateEvent instance(List<OrderItem> orderItems) {
        return new OrderCreateEvent(orderItems);
    }
}

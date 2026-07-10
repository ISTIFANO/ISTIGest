package com.fooddelivery.application.usecase;

import com.fooddelivery.application.service.OrderEventPublisher;
import com.fooddelivery.domain.event.OrderCreateEvent;
import com.fooddelivery.domain.event.OrderItem;
import com.fooddelivery.domain.repository.MenuRepository;
import com.fooddelivery.framework.dto.CreateOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderUseCase {

    private final OrderEventPublisher orderEventPublisher;
    private final MenuRepository menuRepository;

    public void createOrder(CreateOrderRequest request) {
        List<OrderItem> orderItems = new ArrayList<>();
        menuRepository.findAllById(request.itemIds())
                .forEach(menuItem -> orderItems.add(
                        OrderItem.instance(menuItem.getName(), menuItem.getPrice())));

        orderEventPublisher.publishOrderCreatedEvent(OrderCreateEvent.instance(orderItems));
    }
}

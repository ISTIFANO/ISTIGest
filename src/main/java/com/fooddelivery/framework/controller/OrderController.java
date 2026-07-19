package com.fooddelivery.framework.controller;

import com.fooddelivery.application.usecase.OrderUseCase;
import com.fooddelivery.framework.dto.CreateOrderRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Tag(name = "Order", description = "Order creation and async delivery")
public class OrderController {

    private final OrderUseCase orderUseCase;

    @PostMapping
    @Operation(
            summary = "Create order",
            description = "Creates an order from menu item IDs and publishes a delivery event to RabbitMQ")
    public void createOrder(@RequestBody CreateOrderRequest request) {
        orderUseCase.createOrder(request);
    }
}

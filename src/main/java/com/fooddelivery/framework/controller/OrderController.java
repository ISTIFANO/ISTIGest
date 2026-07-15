package com.fooddelivery.framework.controller;

import com.fooddelivery.application.usecase.OrderUseCase;
import com.fooddelivery.framework.dto.CreateOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderUseCase orderUseCase;

    @PostMapping
    public void createOrder(@RequestBody CreateOrderRequest request) {
        orderUseCase.createOrder(request);
    }
}

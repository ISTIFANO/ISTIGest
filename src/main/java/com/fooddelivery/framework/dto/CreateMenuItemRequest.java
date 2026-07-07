package com.fooddelivery.framework.dto;

import java.math.BigDecimal;

public record CreateMenuItemRequest(String name, BigDecimal price) {
    public static CreateMenuItemRequest instance(String name, BigDecimal price) {
        return new CreateMenuItemRequest(name, price);
    }
}

package com.fooddelivery.framework.dto;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(List<UUID> itemIds) {
}

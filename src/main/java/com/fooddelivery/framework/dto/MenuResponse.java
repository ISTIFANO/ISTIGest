package com.fooddelivery.framework.dto;

import com.fooddelivery.domain.entity.MenuEntity;

import java.math.BigDecimal;
import java.util.UUID;

public record MenuResponse(UUID id, String name, BigDecimal price) {
    public static MenuResponse fromEntity(MenuEntity entity) {
        return new MenuResponse(entity.getId(), entity.getName(), entity.getPrice());
    }
}

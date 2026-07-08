package com.fooddelivery.application.usecase;

import com.fooddelivery.domain.entity.MenuEntity;
import com.fooddelivery.domain.repository.MenuRepository;
import com.fooddelivery.framework.dto.CreateMenuItemRequest;
import com.fooddelivery.framework.dto.MenuResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuUseCase {

    private final MenuRepository menuRepository;

    public List<MenuResponse> getMenu() {
        return menuRepository.findAll()
                .stream()
                .map(MenuResponse::fromEntity)
                .toList();
    }

    public MenuResponse addItem(CreateMenuItemRequest request) {
        MenuEntity menuEntity = MenuEntity.builder()
                .name(request.name())
                .price(request.price())
                .build();
        menuRepository.save(menuEntity);
        return MenuResponse.fromEntity(menuEntity);
    }
}

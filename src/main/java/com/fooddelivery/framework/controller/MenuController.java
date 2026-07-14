package com.fooddelivery.framework.controller;

import com.fooddelivery.application.usecase.MenuUseCase;
import com.fooddelivery.framework.dto.CreateMenuItemRequest;
import com.fooddelivery.framework.dto.MenuResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuUseCase menuUseCase;

    @GetMapping
    public List<MenuResponse> getMenu() {
        return menuUseCase.getMenu();
    }

    @PostMapping
    public MenuResponse addItem(@RequestBody CreateMenuItemRequest request) {
        return menuUseCase.addItem(request);
    }
}

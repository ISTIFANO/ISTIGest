package com.fooddelivery.framework.controller;

import com.fooddelivery.application.usecase.MenuUseCase;
import com.fooddelivery.framework.dto.CreateMenuItemRequest;
import com.fooddelivery.framework.dto.MenuResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
@Tag(name = "Menu", description = "Menu item management")
public class MenuController {

    private final MenuUseCase menuUseCase;

    @GetMapping
    @Operation(summary = "List menu items", description = "Returns all available menu items")
    public List<MenuResponse> getMenu() {
        return menuUseCase.getMenu();
    }

    @PostMapping
    @Operation(summary = "Add menu item", description = "Creates a new menu item")
    public MenuResponse addItem(@RequestBody CreateMenuItemRequest request) {
        return menuUseCase.addItem(request);
    }
}

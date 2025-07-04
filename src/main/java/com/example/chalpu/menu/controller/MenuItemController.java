package com.example.chalpu.menu.controller;

import com.example.chalpu.common.response.ApiResponse;
import com.example.chalpu.menu.dto.MenuItemRequest;
import com.example.chalpu.menu.dto.MenuItemResponse;
import com.example.chalpu.menu.service.MenuService;
import com.example.chalpu.oauth.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menus/{menuId}/items")
@RequiredArgsConstructor
@Tag(name = "MenuItem", description = "메뉴 아이템 관련 API")
public class MenuItemController {

    private final MenuService menuService;

    @PostMapping
    @Operation(
        summary = "메뉴판에 음식 추가",
        description = "메뉴판에 새로운 음식을 추가합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    public ResponseEntity<ApiResponse<MenuItemResponse>> addMenuItem(
            @PathVariable Long menuId,
            @RequestBody MenuItemRequest menuItemRequest,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        MenuItemResponse menuItem = menuService.addMenuItem(menuId, menuItemRequest, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("메뉴 아이템 추가가 완료되었습니다.", menuItem));
    }
} 
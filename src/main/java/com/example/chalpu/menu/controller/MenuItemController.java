package com.example.chalpu.menu.controller;

import com.example.chalpu.common.response.ApiResponse;
import com.example.chalpu.common.response.PageResponse;
import com.example.chalpu.menu.dto.MenuItemOrderUpdateRequest;
import com.example.chalpu.menu.dto.MenuItemRequest;
import com.example.chalpu.menu.dto.MenuItemResponse;
import com.example.chalpu.menu.service.MenuItemService;
import com.example.chalpu.oauth.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/menus/{menuId}/items")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping
    @Operation(summary = "메뉴 아이템 추가", description = "특정 메뉴에 음식 아이템을 추가합니다.")
    public ResponseEntity<ApiResponse<MenuItemResponse>> addMenuItem(
            @PathVariable Long menuId,
            @RequestBody MenuItemRequest menuItemRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        MenuItemResponse response = menuItemService.addMenuItem(menuId, menuItemRequest, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("메뉴 아이템 추가가 완료되었습니다.", response));
    }

    @PatchMapping("/{menuItemId}/order")
    @Operation(summary = "메뉴 아이템 표시 순서 수정", description = "특정 메뉴 아이템의 표시 순서를 수정합니다.")
    public ResponseEntity<ApiResponse<MenuItemResponse>> updateMenuItemOrder(
            @PathVariable Long menuItemId,
            @RequestBody MenuItemOrderUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        MenuItemResponse response = menuItemService.updateMenuItemOrder(menuItemId, request, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("메뉴 아이템 순서 수정이 완료되었습니다.", response));
    }

    @DeleteMapping("/{menuItemId}")
    @Operation(summary = "메뉴 아이템 삭제", description = "특정 메뉴에서 음식 아이템을 제거합니다.")
    public ResponseEntity<ApiResponse<Void>> removeMenuItem(
            @PathVariable Long menuId,
            @PathVariable Long menuItemId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        menuItemService.removeMenuItem(menuId, menuItemId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("메뉴 아이템 삭제가 완료되었습니다.", null));
    }

    @GetMapping
    @Operation(summary = "메뉴 아이템 목록 조회", description = "특정 메뉴에 속한 음식 아이템 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<PageResponse<MenuItemResponse>>> getMenuItems(
            @PathVariable Long menuId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Pageable pageable) {
        PageResponse<MenuItemResponse> response = menuItemService.getMenuItems(menuId, userDetails.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success("메뉴 아이템 목록 조회가 완료되었습니다.", response));
    }
} 
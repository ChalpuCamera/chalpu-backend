package com.example.chalpu.menu.controller;

import com.example.chalpu.common.response.ApiResponse;
import com.example.chalpu.common.response.PageResponse;
import com.example.chalpu.menu.dto.MenuRequest;
import com.example.chalpu.menu.dto.MenuResponse;
import com.example.chalpu.menu.service.MenuService;
import com.example.chalpu.oauth.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stores/{storeId}/menus")
@RequiredArgsConstructor
@Tag(name = "Menu", description = "메뉴 관련 API")
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    @Operation(
        summary = "메뉴판 목록",
        description = """
                특정 매장의 모든 메뉴판 목록을 조회합니다.
                
                **페이지네이션 파라미터:**
                - `page`: 페이지 번호 (0부터 시작, 기본값: 0)
                - `size`: 페이지 크기 (기본값: 20)
                - `sort`: 정렬 조건 (선택사항)

                
                **요청 예시:**
                ```
                GET /api/stores/{storeId}/menus?page=0&size=10&sort=createdAt,desc&sort=menuName,asc
                ```
                위처럼 정렬 조건을 리스트로 줘도 되고 아래처럼 String으로 줘도 됩니다.
                ```
                GET /api/stores/{storeId}/menus?page=0&size=10&sort=createdAt,desc
                ```
                """,
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    public ResponseEntity<ApiResponse<PageResponse<MenuResponse>>> getMenus(
            @PathVariable Long storeId,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<MenuResponse> menus = menuService.getMenus(storeId, pageable);
        return ResponseEntity.ok(ApiResponse.success("메뉴판 목록 조회가 완료되었습니다.", menus));
    }

    @PostMapping
    @Operation(
        summary = "메뉴판 생성",
        description = "새로운 메뉴판을 생성합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    public ResponseEntity<ApiResponse<MenuResponse>> createMenu(
            @PathVariable Long storeId,
            @RequestBody MenuRequest menuRequest,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        MenuResponse menu = menuService.createMenu(storeId, menuRequest, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("메뉴판 생성이 완료되었습니다.", menu));
    }

    @PutMapping("/{menuId}")
    @Operation(
        summary = "메뉴판 수정",
        description = "기존 메뉴판의 정보를 수정합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    public ResponseEntity<ApiResponse<MenuResponse>> updateMenu(
            @PathVariable Long storeId,
            @PathVariable Long menuId,
            @RequestBody MenuRequest menuRequest,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        MenuResponse menu = menuService.updateMenu(menuId, menuRequest, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("메뉴판 수정이 완료되었습니다.", menu));
    }

    @DeleteMapping("/{menuId}")
    @Operation(summary = "메뉴판 삭제", description = "특정 메뉴판을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteMenu(
            @PathVariable Long storeId,
            @PathVariable Long menuId,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        menuService.deleteMenu(menuId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("메뉴판 삭제가 완료되었습니다.", null));
    }
} 
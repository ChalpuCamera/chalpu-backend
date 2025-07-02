package com.example.chalpu.store.controller;

import com.example.chalpu.common.response.ApiResponse;
import com.example.chalpu.common.response.PageResponse;
import com.example.chalpu.oauth.security.jwt.UserDetailsImpl;
import com.example.chalpu.store.domain.Store;
import com.example.chalpu.store.domain.UserStoreRole;
import com.example.chalpu.store.service.StoreService;
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
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Tag(name = "Store", description = "매장 관련 API")
public class StoreController {

    private final StoreService storeService;

    @GetMapping("/my")
    @Operation(
        summary = "내가 속한 가게 목록",
        description = """
                현재 로그인한 사용자가 속한 모든 가게 목록을 조회합니다.
                
                **페이지네이션 파라미터:**
                - `page`: 페이지 번호 (0부터 시작, 기본값: 0)
                - `size`: 페이지 크기 (기본값: 20)
                - `sort`: 정렬 조건 (선택사항)
                
                **요청 예시:**
                ```json
                {
                  "page": 0,
                  "size": 10,
                  "sort": ["createdAt,desc", "storeName,asc"]
                }
                위처럼 정렬 조건을 리스트로 줘도 되고 아래처럼 String으로 줘도 됩니다.
                {
                  "page": 0,
                  "size": 10,
                  "sort": "createdAt,desc"
                }
                ```
                """,
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    public ResponseEntity<ApiResponse<PageResponse<Store>>> getMyStores(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<Store> stores = storeService.getMyStores(currentUser.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success("가게 목록 조회가 완료되었습니다.", stores));
    }

    @GetMapping("/{storeId}")
    @Operation(
        summary = "가게 상세",
        description = "특정 가게의 상세 정보를 조회합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    public ResponseEntity<ApiResponse<Store>> getStore(@PathVariable Long storeId) {
        Store store = storeService.getStore(storeId);
        return ResponseEntity.ok(ApiResponse.success("가게 정보 조회가 완료되었습니다.", store));
    }

    @PostMapping
    @Operation(
        summary = "가게 생성",
        description = "새로운 가게를 생성합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    public ResponseEntity<ApiResponse<Store>> createStore(
            @RequestBody Store storeRequest,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        Store store = storeService.createStore(storeRequest, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("가게 생성이 완료되었습니다.", store));
    }

    @PatchMapping("/{storeId}")
    @Operation(
        summary = "가게 정보 수정",
        description = "가게 정보를 수정합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    public ResponseEntity<ApiResponse<Store>> updateStore(
            @PathVariable Long storeId,
            @RequestBody Store storeRequest,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        Store store = storeService.updateStore(storeId, storeRequest, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("가게 정보 수정이 완료되었습니다.", store));
    }

    @PostMapping("/{storeId}/members")
    @Operation(
        summary = "멤버 초대",
        description = "가게에 새로운 멤버를 초대합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    public ResponseEntity<ApiResponse<UserStoreRole>> inviteMember(
            @PathVariable Long storeId,
            @RequestBody UserStoreRole memberRequest,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        UserStoreRole userStoreRole = storeService.inviteMember(storeId, memberRequest, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("멤버 초대가 완료되었습니다.", userStoreRole));
    }
} 
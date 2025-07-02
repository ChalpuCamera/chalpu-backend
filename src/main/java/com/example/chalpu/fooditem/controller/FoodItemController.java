package com.example.chalpu.fooditem.controller;

import com.example.chalpu.common.response.ApiResponse;
import com.example.chalpu.fooditem.domain.FoodItem;
import com.example.chalpu.fooditem.service.FoodItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
@Tag(name = "FoodItem", description = "음식 관련 API")
public class FoodItemController {

    private final FoodItemService foodItemService;

    @GetMapping("/{foodId}")
    @Operation(
        summary = "음식 상세",
        description = "특정 음식의 상세 정보를 조회합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    public ResponseEntity<ApiResponse<FoodItem>> getFoodItem(@PathVariable Long foodId) {
        FoodItem foodItem = foodItemService.getFoodItem(foodId);
        return ResponseEntity.ok(ApiResponse.success("음식 정보 조회가 완료되었습니다.", foodItem));
    }
} 
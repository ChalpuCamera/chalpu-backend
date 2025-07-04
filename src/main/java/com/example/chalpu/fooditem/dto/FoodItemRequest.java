package com.example.chalpu.fooditem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "음식 생성/수정 요청")
public class FoodItemRequest {
    
    @Schema(description = "음식명", example = "김치찌개", required = true)
    private String foodName;
    
    @Schema(description = "설명", example = "매콤하고 시원한 김치찌개")
    private String description;
    
    @Schema(description = "재료", example = "김치, 돼지고기, 두부, 대파")
    private String ingredients;
    
    @Schema(description = "조리법", example = "김치를 볶아 우린 후 끓인다")
    private String cookingMethod;
    
    @Schema(description = "가격", example = "8000")
    private BigDecimal price;
    
    @Schema(description = "활성화 여부", example = "true")
    private Boolean isActive = true;
} 
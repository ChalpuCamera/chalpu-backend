package com.example.chalpu.fooditem.dto;

import com.example.chalpu.fooditem.domain.FoodItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "음식 응답")
public class FoodItemResponse {
    
    @Schema(description = "음식 ID", example = "1")
    private Long foodItemId;
    
    @Schema(description = "매장 ID", example = "1")
    private Long storeId;
    
    @Schema(description = "음식명", example = "김치찌개")
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
    private Boolean isActive;

    @Schema(description = "썸네일 URL", example = "https://chalpu.s3.ap-northeast-2.amazonaws.com/photos/stores/1/a1b2c3d4-e5f6-7890-1234-567890abcdef.jpg")
    private String thumbnailUrl;
    
    @Schema(description = "생성 시간", example = "2024-01-15T09:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "수정 시간", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
    
    public static FoodItemResponse from(FoodItem foodItem) {
        return FoodItemResponse.builder()
                .foodItemId(foodItem.getId())
                .storeId(foodItem.getStore() != null ? foodItem.getStore().getId() : null)
                .foodName(foodItem.getFoodName())
                .description(foodItem.getDescription())
                .ingredients(foodItem.getIngredients())
                .cookingMethod(foodItem.getCookingMethod())
                .thumbnailUrl(foodItem.getThumbnailUrl())
                .price(foodItem.getPrice())
                .isActive(foodItem.getIsActive())
                .createdAt(foodItem.getCreatedAt())
                .updatedAt(foodItem.getUpdatedAt())
                .build();
    }
} 
package com.example.chalpu.menu.dto;

import com.example.chalpu.menu.domain.MenuItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메뉴 아이템 응답")
public class MenuItemResponse {
    
    @Schema(description = "메뉴 아이템 ID", example = "1")
    private Long id;
    
    @Schema(description = "메뉴판 ID", example = "1")
    private Long menuId;
    
    @Schema(description = "음식 ID", example = "1")
    private Long foodId;
    
    @Schema(description = "음식 이름", example = "김치찌개")
    private String foodName;
    
    @Schema(description = "표시 순서", example = "1")
    private Integer displayOrder;
    
    public static MenuItemResponse from(MenuItem menuItem) {
        return MenuItemResponse.builder()
                .id(menuItem.getId())
                .menuId(menuItem.getMenu() != null ? menuItem.getMenu().getId() : null)
                .foodId(menuItem.getFoodItem() != null ? menuItem.getFoodItem().getId() : null)
                .foodName(menuItem.getFoodItem() != null ? menuItem.getFoodItem().getFoodName() : null)
                .displayOrder(menuItem.getDisplayOrder())
                .build();
    }
} 
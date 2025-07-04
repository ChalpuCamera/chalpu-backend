package com.example.chalpu.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메뉴 아이템 생성 요청")
public class MenuItemRequest {
    
    @Schema(description = "음식 ID", example = "1", required = true)
    private Long foodId;
    
    @Schema(description = "표시 순서", example = "1")
    private Integer displayOrder = 1;
} 
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
@Schema(description = "메뉴 아이템 표시 순서 수정 요청")
public class MenuItemOrderUpdateRequest {
    @Schema(description = "새로운 표시 순서", example = "1", required = true)
    private Integer displayOrder;
} 
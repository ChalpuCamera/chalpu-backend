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
@Schema(description = "메뉴판 생성 요청")
public class MenuRequest {
    
    @Schema(description = "메뉴판 이름", example = "런치 메뉴", required = true)
    private String menuName;
    
    @Schema(description = "메뉴판 설명", example = "점심시간 특별 메뉴")
    private String description;
} 
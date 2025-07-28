package com.example.chalpu.menu.dto;

import com.example.chalpu.menu.domain.Menu;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메뉴판 응답")
public class MenuResponse {
    
    @Schema(description = "메뉴판 ID", example = "1")
    private Long id;
    
    @Schema(description = "매장 ID", example = "1")
    private Long storeId;
    
    @Schema(description = "메뉴판 이름", example = "런치 메뉴")
    private String menuName;
    
    @Schema(description = "메뉴판 설명", example = "점심시간 특별 메뉴")
    private String description;
    
    @Schema(description = "활성화 여부", example = "true")
    private Boolean isActive;
    
    @Schema(description = "생성 시간", example = "2024-01-15T09:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "수정 시간", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
    
    public static MenuResponse from(Menu menu) {
        return MenuResponse.builder()
                .id(menu.getId())
                .storeId(menu.getStore() != null ? menu.getStore().getId() : null)
                .menuName(menu.getMenuName())
                .description(menu.getDescription())
                .isActive(menu.getIsActive())
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .build();
    }
} 
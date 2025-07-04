package com.example.chalpu.photo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사진 업로드 요청")
public class PhotoUploadRequest {
    
    @Schema(description = "필터", example = "VINTAGE")
    private String filter;
    
    @Schema(description = "설명", example = "맛있는 김치찌개 사진")
    private String description;
} 
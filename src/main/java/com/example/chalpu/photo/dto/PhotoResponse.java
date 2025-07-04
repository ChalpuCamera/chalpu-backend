package com.example.chalpu.photo.dto;

import com.example.chalpu.photo.domain.Photo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사진 응답")
public class PhotoResponse {
    
    @Schema(description = "사진 ID", example = "1")
    private Long id;
    
    @Schema(description = "업로드한 사용자 ID", example = "1")
    private Long userId;
    
    @Schema(description = "매장 ID", example = "1")
    private Long storeId;
    
    @Schema(description = "음식 ID", example = "1")
    private Long foodId;
    
    @Schema(description = "파일 경로", example = "/uploads/photos/20240115/image.jpg")
    private String filePath;
    
    @Schema(description = "파일명", example = "image.jpg")
    private String fileName;
    
    @Schema(description = "필터", example = "VINTAGE")
    private String filter;
    
    @Schema(description = "파일 크기", example = "1024")
    private Integer fileSize;
    
    @Schema(description = "이미지 너비", example = "1920")
    private Integer imageWidth;
    
    @Schema(description = "이미지 높이", example = "1080")
    private Integer imageHeight;
    
    @Schema(description = "업로드 시간", example = "2024-01-15T09:30:00")
    private Timestamp uploadDate;
    
    @Schema(description = "대표 사진 여부", example = "false")
    private Boolean isFeatured;
    
    @Schema(description = "활성화 여부", example = "true")
    private Boolean isActive;
    
    @Schema(description = "생성 시간", example = "2024-01-15T09:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "수정 시간", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
    
    public static PhotoResponse from(Photo photo) {
        return PhotoResponse.builder()
                .id(photo.getId())
                .userId(photo.getUser() != null ? photo.getUser().getId() : null)
                .storeId(photo.getStore() != null ? photo.getStore().getId() : null)
                .foodId(photo.getFoodItem() != null ? photo.getFoodItem().getId() : null)
                .filePath(photo.getFilePath())
                .fileName(photo.getFileName())
                .filter(photo.getFilter())
                .fileSize(photo.getFileSize())
                .imageWidth(photo.getImageWidth())
                .imageHeight(photo.getImageHeight())
                .uploadDate(photo.getUploadDate())
                .isFeatured(photo.getIsFeatured())
                .isActive(photo.getIsActive())
                .createdAt(photo.getCreatedAt())
                .updatedAt(photo.getUpdatedAt())
                .build();
    }
} 
package com.example.chalpu.guide.dto;

import com.example.chalpu.guide.domain.Guide;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GuideResponse {

    @Schema(description = "가이드 ID")
    private Long id;

    @Schema(description = "S3 키")
    private String s3Key;

    @Schema(description = "원본 파일명")
    private String fileName;

    @Schema(description = "생성일")
    private LocalDateTime createdAt;

    public static GuideResponse from(Guide guide) {
        return GuideResponse.builder()
                .id(guide.getId())
                .s3Key(guide.getS3Key())
                .fileName(guide.getFileName())
                .createdAt(guide.getCreatedAt())
                .build();
    }
} 
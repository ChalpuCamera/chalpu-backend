package com.example.chalpu.guide.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GuideRegisterRequest {
    @Schema(description = "S3에 업로드된 파일의 고유 키 (Presigned URL 생성 시 받은 값)", example = "guides/c1f8f3a3-3b1b-4b8b-8b5b-4b8b8b5b4b8b.xml")
    private String guideS3Key;

    @Schema(description = "업로드한 파일의 원본 이름", example = "guide_v1.xml")
    private String fileName;

    @Schema(description = "S3에 업로드된 이미지의 고유 키 (Presigned URL 생성 시 받은 값)", example = "images/c1f8f3a3-3b1b-4b8b-8b5b-4b8b8b5b4b8b.xml")
    private String imageS3Key;

    @Schema(description = "svg 파일의 고유 키 (Presigned URL 생성 시 받은 값)", example = "svgs/c1f8f3a3-3b1b-4b8b-8b5b-4b8b8b5b4b8b.svg")
    private String svgS3Key;

    private String content;
    private Long subCategoryId;
    private List<String> tags;
} 
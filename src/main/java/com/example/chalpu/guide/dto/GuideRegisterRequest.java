package com.example.chalpu.guide.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GuideRegisterRequest {
    @Schema(description = "S3에 업로드된 파일의 고유 키 (Presigned URL 생성 시 받은 값)", example = "guides/c1f8f3a3-3b1b-4b8b-8b5b-4b8b8b5b4b8b.xml")
    private String s3Key;

    @Schema(description = "업로드한 파일의 원본 이름", example = "guide_v1.xml")
    private String fileName;
} 
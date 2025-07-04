package com.example.chalpu.guide.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GuidePresignedUrlResponse {

    @Schema(description = "파일 업로드에 사용할 S3 Presigned URL")
    private String presignedUrl;

    @Schema(description = "S3에 저장될 파일의 고유 키 (경로 포함). 업로드 완료 후 서버에 알려줘야 합니다.")
    private String s3Key;
} 
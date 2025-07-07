package com.example.chalpu.guide.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GuidePresignedUrlsResponse {
    private final String guideS3Key;
    private final String guideUploadUrl;
    private final String imageS3Key;
    private final String imageUploadUrl;
} 
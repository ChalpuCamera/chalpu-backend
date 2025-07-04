package com.example.chalpu.guide.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GuideUploadRequest {

    @Schema(description = "업로드할 가이드 파일의 원본 이름 (확장자 포함)", example = "guide_v1.xml")
    private String fileName;

    @Schema(description = "파일의 Content Type. XML 파일이므로 'application/xml'로 고정됩니다.", example = "application/xml", requiredMode = Schema.RequiredMode.NOT_REQUIRED, hidden = true)
    private final String contentType = "application/xml";
} 
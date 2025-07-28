package com.example.chalpu.guide.dto;

import lombok.Getter;

@Getter
public class GuideUpdateRequest {
    private String content;
    private Long subCategoryId;
    private String fileName;
} 
package com.example.chalpu.photo.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PhotoBackgroundRemovalResponse {
    private String processedImageBase64;
    private String originalFileName;
}
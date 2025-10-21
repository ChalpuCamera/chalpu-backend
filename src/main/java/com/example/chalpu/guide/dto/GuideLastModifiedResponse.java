package com.example.chalpu.guide.dto;

import java.time.LocalDateTime;

public record GuideLastModifiedResponse(String lastModifiedAt) {
    public static GuideLastModifiedResponse from(LocalDateTime lastModifiedAt) {
        return new GuideLastModifiedResponse(
            lastModifiedAt != null ? lastModifiedAt.toString() : null
        );
    }
}

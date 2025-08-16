package com.example.chalpu.version.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppVersionRequestDTO {
    private String minSupportedVersion;
    private String latestVersion;
    private String platform;
    private String description;
}
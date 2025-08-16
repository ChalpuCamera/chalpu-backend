package com.example.chalpu.version.dto;

import com.example.chalpu.version.domain.AppVersion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppVersionResponseDTO {
    private String minSupportedVersion;
    private String latestVersion;

    public static AppVersionResponseDTO from(AppVersion appVersion) {
        return AppVersionResponseDTO.builder()
                .minSupportedVersion(appVersion.getMinSupportedVersion())
                .latestVersion(appVersion.getLatestVersion())
                .build();
    }
}

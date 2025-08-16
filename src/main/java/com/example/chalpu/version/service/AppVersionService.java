package com.example.chalpu.version.service;

import com.example.chalpu.common.exception.AppVersionException;
import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.version.domain.AppVersion;
import com.example.chalpu.version.dto.AppVersionRequestDTO;
import com.example.chalpu.version.dto.AppVersionResponseDTO;
import com.example.chalpu.version.repository.AppVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppVersionService {

    private final AppVersionRepository appVersionRepository;

    /**
     * 최신 앱 버전 정보 조회
     */
    public AppVersionResponseDTO getLatestVersion() {
        AppVersion latestVersion = appVersionRepository.findLatestVersion()
                .orElseThrow(() -> new AppVersionException(ErrorMessage.APP_VERSION_NOT_FOUND));

        return AppVersionResponseDTO.from(latestVersion);
    }

    public AppVersionResponseDTO getVersionById(Long id) {
        AppVersion version = appVersionRepository.findById(id)
                .orElseThrow(() -> new AppVersionException(ErrorMessage.APP_VERSION_NOT_FOUND));

        return AppVersionResponseDTO.from(version);
    }

    @Transactional
    public AppVersionResponseDTO updateVersion(Long id, AppVersionRequestDTO requestDTO) {
        AppVersion version = appVersionRepository.findById(id)
                .orElseThrow(() -> new AppVersionException(ErrorMessage.APP_VERSION_NOT_FOUND));

        AppVersion updatedVersion = AppVersion.builder()
                .id(version.getId())
                .minSupportedVersion(requestDTO.getMinSupportedVersion())
                .latestVersion(requestDTO.getLatestVersion())
                .platform(requestDTO.getPlatform())
                .description(requestDTO.getDescription())
                .build();

        AppVersion savedVersion = appVersionRepository.save(updatedVersion);
        return AppVersionResponseDTO.from(savedVersion);
    }

    @Transactional
    public AppVersionResponseDTO createVersion(AppVersionRequestDTO requestDTO) {
        AppVersion newVersion = AppVersion.builder()
                .minSupportedVersion(requestDTO.getMinSupportedVersion())
                .latestVersion(requestDTO.getLatestVersion())
                .platform(requestDTO.getPlatform())
                .description(requestDTO.getDescription())
                .build();

        AppVersion savedVersion = appVersionRepository.save(newVersion);
        return AppVersionResponseDTO.from(savedVersion);
    }
}

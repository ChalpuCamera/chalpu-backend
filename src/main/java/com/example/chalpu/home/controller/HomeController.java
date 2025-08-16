package com.example.chalpu.home.controller;

import com.example.chalpu.common.response.ApiResponse;
import com.example.chalpu.feedback.dto.AppImprovementFeedbackDto;
import com.example.chalpu.feedback.dto.FoodGuideFeedbackDto;
import com.example.chalpu.feedback.service.FeedbackService;
import com.example.chalpu.version.dto.AppVersionRequestDTO;
import com.example.chalpu.version.dto.AppVersionResponseDTO;
import com.example.chalpu.version.service.AppVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.example.chalpu.home.domain.PhotoTip;
import com.example.chalpu.home.dto.PhotoTipDto;
import com.example.chalpu.oauth.security.jwt.UserDetailsImpl;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
@Tag(name = "홈 API", description = "홈 화면 및 피드백 관련 API")
public class HomeController {

    private final FeedbackService feedbackService;
    private final AppVersionService appVersionService;

    @GetMapping("/tips")
    public ApiResponse<List<PhotoTipDto>> getAllTips() {
        return ApiResponse.success(Arrays.stream(PhotoTip.values())
                .map(PhotoTipDto::from)
                .toList());
    }

    @GetMapping("/tips/{id}")
    public ApiResponse<PhotoTipDto> getTipById(@PathVariable("id") String id) {
        return ApiResponse.success(PhotoTip.findById(id)
                .map(PhotoTipDto::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @GetMapping("/version")
    @Operation(summary = "최신 앱 버전 조회", description = "최소 지원 버전과 최신 버전 정보를 조회합니다.")
    public ApiResponse<AppVersionResponseDTO> getLatestVersion() {
        return ApiResponse.success(appVersionService.getLatestVersion());
    }

    @GetMapping("/version/{id}")
    @Operation(summary = "특정 버전 정보 조회", description = "ID로 특정 버전 정보를 조회합니다.")
    public ApiResponse<AppVersionResponseDTO> getVersionById(@PathVariable("id") Long id) {
        return ApiResponse.success(appVersionService.getVersionById(id));
    }

    @PostMapping("/version")
    @Operation(summary = "새 버전 생성", description = "새로운 앱 버전 정보를 생성합니다.")
    public ApiResponse<AppVersionResponseDTO> createVersion(@RequestBody AppVersionRequestDTO requestDTO) {
        return ApiResponse.success(appVersionService.createVersion(requestDTO));
    }

    @PutMapping("/version/{id}")
    @Operation(summary = "버전 정보 수정", description = "기존 버전 정보를 수정합니다.")
    public ApiResponse<AppVersionResponseDTO> updateVersion(
            @PathVariable("id") Long id,
            @RequestBody AppVersionRequestDTO requestDTO) {
        return ApiResponse.success(appVersionService.updateVersion(id, requestDTO));
    }
}

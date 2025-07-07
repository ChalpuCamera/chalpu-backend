package com.example.chalpu.guide.controller;

import com.example.chalpu.common.response.ApiResponse;
import com.example.chalpu.common.response.PageResponse;
import com.example.chalpu.guide.dto.GuidePresignedUrlRequest;
import com.example.chalpu.guide.dto.GuidePresignedUrlsResponse;
import com.example.chalpu.guide.dto.GuideRegisterRequest;
import com.example.chalpu.guide.dto.GuideResponse;
import com.example.chalpu.guide.dto.GuideDeleteRequest;
import com.example.chalpu.guide.dto.GuideUpdateRequest;
import com.example.chalpu.guide.service.GuideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "가이드 API", description = "가이드 관련 API")
@RestController
@RequestMapping("api/guides")
@RequiredArgsConstructor
public class GuideController {

    private final GuideService guideService;

    @Operation(summary = "가이드 업로드용 Presigned URL 생성", description = "가이드 파일과 이미지 파일을 S3에 업로드하기 위한 Presigned URL을 각각 생성합니다.")
    @PostMapping("/presigned-urls")
    public ResponseEntity<ApiResponse<GuidePresignedUrlsResponse>> getPresignedUrls(@RequestBody GuidePresignedUrlRequest request) {
        GuidePresignedUrlsResponse response = guideService.getPresignedUrls(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "가이드 정보 등록", description = "S3에 파일 업로드 완료 후, 가이드 메타데이터를 서버에 최종 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<GuideResponse>> registerGuide(@RequestBody GuideRegisterRequest request) {
        GuideResponse response = guideService.registerGuide(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "가이드 단건 조회", description = "특정 가이드의 상세 정보를 조회합니다.")
    @GetMapping("/{guideId}")
    public ResponseEntity<ApiResponse<GuideResponse>> getGuide(@PathVariable Long guideId) {
        GuideResponse response = guideService.findById(guideId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "가이드 정보 수정", description = "특정 가이드의 내용, 파일 이름, 카테고리를 수정합니다.")
    @PatchMapping("/{guideId}")
    public ResponseEntity<ApiResponse<GuideResponse>> updateGuide(@PathVariable Long guideId, @RequestBody GuideUpdateRequest request) {
        GuideResponse response = guideService.updateGuide(guideId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "가이드 목록 조회", description = "모든 가이드 목록을 페이지네이션하여 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<GuideResponse>>> getGuides(Pageable pageable) {
        PageResponse<GuideResponse> response = guideService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "가이드 다중 삭제", description = "요청받은 ID 목록에 해당하는 가이드를 DB와 S3에서 모두 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteGuides(@RequestBody GuideDeleteRequest request) {
        guideService.deleteGuides(request.getGuideIds());
        return ResponseEntity.ok(ApiResponse.success());
    }
} 
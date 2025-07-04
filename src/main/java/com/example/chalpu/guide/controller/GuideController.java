package com.example.chalpu.guide.controller;

import com.example.chalpu.common.response.ApiResponse;
import com.example.chalpu.guide.dto.GuidePresignedUrlResponse;
import com.example.chalpu.guide.dto.GuideRegisterRequest;
import com.example.chalpu.guide.dto.GuideResponse;
import com.example.chalpu.guide.dto.GuideUploadRequest;
import com.example.chalpu.guide.service.GuideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.chalpu.common.response.PageResponse;

@RestController
@RequestMapping("/api/guides")
@RequiredArgsConstructor
@Tag(name = "가이드 API (Admin)", description = "가이드(XML) 파일 관련 API. 어드민 권한이 필요합니다.")
@PreAuthorize("hasRole('ADMIN')")
public class GuideController {

    private final GuideService guideService;

    @Operation(summary = "가이드 전체 목록 조회 (Admin)", description = "모든 가이드 목록을 페이지네이션하여 조회합니다.")
    @GetMapping
    public ApiResponse<PageResponse<GuideResponse>> getAllGuides(@PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.success(guideService.getAllGuides(pageable));
    }

    @Operation(summary = "가이드 상세 조회 (Admin)", description = "특정 가이드의 상세 정보를 조회합니다.")
    @GetMapping("/{guideId}")
    public ApiResponse<GuideResponse> getGuide(@PathVariable Long guideId) {
        return ApiResponse.success(guideService.getGuide(guideId));
    }

    @Operation(summary = "가이드 업로드용 Presigned URL 생성 (Admin)", description = """
            가이드 XML 파일을 S3에 직접 업로드하기 위한 Presigned URL을 생성합니다.
            
            **클라이언트 처리 순서:**
            1. 이 API를 호출하여 `presignedUrl`과 `s3Key`를 받습니다.
            2. 받은 `presignedUrl`을 목적지로, 업로드할 XML 파일의 원본 데이터를 body에 담아 **HTTP PUT** 요청을 보냅니다.
               - **주의:** `Content-Type` 헤더에 반드시 `application/xml`을 포함해야 합니다.
            3. S3 업로드가 성공하면, `/api/guides/register` API를 호출하여 업로드 완료 사실을 서버에 알립니다.
            """)
    @PostMapping("/presigned-url")
    public ApiResponse<GuidePresignedUrlResponse> generatePresignedUrl(@RequestBody GuideUploadRequest request) {
        return ApiResponse.success(guideService.generatePresignedUrl(request));
    }

    @Operation(summary = "가이드 정보 등록 (Admin)", description = "S3에 가이드 파일 업로드 완료 후, 파일 메타데이터를 서버에 등록합니다.")
    @PostMapping("/register")
    public ApiResponse<GuideResponse> registerGuide(@RequestBody GuideRegisterRequest request) {
        return ApiResponse.success(guideService.registerGuide(request));
    }

    @Operation(summary = "가이드 삭제 (Admin)", description = "특정 가이드 정보를 DB에서 삭제하고, S3에 있는 실제 파일도 함께 삭제합니다.")
    @DeleteMapping("/{guideId}")
    public ApiResponse<Void> deleteGuide(@PathVariable Long guideId) {
        guideService.deleteGuide(guideId);
        return ApiResponse.success();
    }
} 
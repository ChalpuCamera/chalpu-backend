package com.example.chalpu.customerfeedback.controller;

import com.example.chalpu.common.response.ApiResponse;
import com.example.chalpu.customerfeedback.dto.FeedbackCreateRequest;
import com.example.chalpu.customerfeedback.dto.FeedbackPhotosUploadRequest;
import com.example.chalpu.customerfeedback.dto.FeedbackPhotosPresignedUrlResponse;
import com.example.chalpu.customerfeedback.dto.FeedbackResponse;
import com.example.chalpu.customerfeedback.service.CustomerFeedbackService;
import com.example.chalpu.oauth.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-feedback")
@RequiredArgsConstructor
@Tag(name = "고객 피드백", description = "고객 음식 피드백 관리 API")
public class CustomerFeedbackController {

    private final CustomerFeedbackService feedbackService;

    @PostMapping
    @Operation(summary = "피드백 생성", description = "음식에 대한 피드백과 설문 응답을 생성합니다.")
    public ResponseEntity<ApiResponse<FeedbackResponse>> createFeedback(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody FeedbackCreateRequest request) {
        
        FeedbackResponse response = feedbackService.createFeedback(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me")
    @Operation(summary = "내 피드백 목록 조회", description = "현재 로그인한 고객이 작성한 모든 피드백을 조회합니다.")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getMyFeedbacks(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        List<FeedbackResponse> responses = feedbackService.getCustomerFeedbacks(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/me/page")
    @Operation(summary = "내 피드백 페이징 조회", description = "현재 로그인한 고객이 작성한 피드백을 페이징으로 조회합니다.")
    public ResponseEntity<ApiResponse<Page<FeedbackResponse>>> getMyFeedbacksWithPage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<FeedbackResponse> responses = feedbackService.getCustomerFeedbacks(userDetails.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/store/{storeId}")
    @Operation(summary = "매장별 피드백 목록 조회", description = "특정 매장에 대한 모든 피드백을 조회합니다.")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getStoreFeedbacks(
            @Parameter(description = "매장 ID", example = "1")
            @PathVariable("storeId") Long storeId) {
        
        List<FeedbackResponse> responses = feedbackService.getStoreFeedbacks(storeId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{feedbackId}")
    @Operation(summary = "피드백 상세 조회", description = "특정 피드백의 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<FeedbackResponse>> getFeedback(
            @Parameter(description = "피드백 ID", example = "1")
            @PathVariable("feedbackId") Long feedbackId) {
        
        FeedbackResponse response = feedbackService.getFeedbackById(feedbackId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/presigned-urls")
    @Operation(summary = "피드백 사진들 Presigned URL 생성", description = """
            고객이 여러 개의 피드백 사진을 S3에 직접 업로드하기 위한 Presigned URL들을 한 번에 생성합니다.
            
            **클라이언트 처리 순서:**
            1. 이 API를 호출하여 각 파일별 `presignedUrl`과 `s3Key`를 받습니다.
            2. 각 `presignedUrl`에 해당하는 파일을 **HTTP PUT** 요청으로 업로드합니다.
               - **주의:** `Content-Type` 헤더에 반드시 실제 파일의 MIME 타입(예: `image/jpeg`)을 포함해야 합니다.
            3. 모든 S3 업로드가 성공하면, 피드백 생성 API를 호출할 때 모든 `s3Key`를 포함해서 전송합니다.
            
            **사용 예시:**
            - 1개 파일: `["photo1.jpg"]`
            - 여러개 파일: `["photo1.jpg", "photo2.jpg", "photo3.jpg"]`
            """)
    public ResponseEntity<ApiResponse<FeedbackPhotosPresignedUrlResponse>> generateMultiplePresignedUrls(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody FeedbackPhotosUploadRequest request) {
        
        FeedbackPhotosPresignedUrlResponse response = feedbackService.generateMultipleFeedbackPhotosPresignedUrl(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
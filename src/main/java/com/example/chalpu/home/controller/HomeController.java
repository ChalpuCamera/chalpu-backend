package com.example.chalpu.home.controller;

import com.example.chalpu.common.response.ApiResponse;
import com.example.chalpu.feedback.dto.AppImprovementFeedbackDto;
import com.example.chalpu.feedback.dto.FoodGuideFeedbackDto;
import com.example.chalpu.feedback.service.FeedbackService;
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

    @PostMapping("/feedback/food-guide")
    @Operation(
        summary = "음식 가이드 피드백 제출",
        description = "특정 음식에 대한 가이드 요청 피드백을 제출합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "음식 가이드 피드백 데이터",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = """
                {
                  "content": "김치찌개 맛있게 끓이는 법 알려주세요",
                  "foodName": "김치찌개"
                }
                """
            )
        )
    )
    public ApiResponse<String> submitFoodGuideFeedback(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody FoodGuideFeedbackDto feedbackDto) {
        feedbackService.submitFoodGuideFeedback(userDetails.getId(), feedbackDto);
        return ApiResponse.success("제출이 성공하였습니다");
    }

    @PostMapping("/feedback/app-improvement")
    @Operation(
        summary = "앱 개선 피드백 제출",
        description = "앱 기능 개선 및 버그 신고 등의 피드백을 제출합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "앱 개선 피드백 데이터",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = """
                {
                  "content": "사진 업로드 속도가 느려요. 개선해주세요."
                }
                """
            )
        )
    )
    public ApiResponse<String> submitAppImprovementFeedback(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody AppImprovementFeedbackDto feedbackDto) {
        feedbackService.submitAppImprovementFeedback(userDetails.getId(), feedbackDto);
        return ApiResponse.success("제출이 성공하였습니다");
    }
}

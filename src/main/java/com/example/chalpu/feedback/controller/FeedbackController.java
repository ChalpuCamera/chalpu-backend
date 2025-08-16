package com.example.chalpu.feedback.controller;

import com.example.chalpu.common.response.ApiResponse;
import com.example.chalpu.feedback.dto.AppImprovementFeedbackDto;
import com.example.chalpu.feedback.dto.FeedbackRequestDto;
import com.example.chalpu.feedback.dto.FoodGuideFeedbackDto;
import com.example.chalpu.feedback.service.FeedbackService;
import com.example.chalpu.oauth.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feedback")
@io.swagger.v3.oas.annotations.tags.Tag(name = "피드백 API", description = "음식 가이드 요청 및 앱 개선 피드백을 제출하는 API")
public class FeedbackController {
    private final FeedbackService feedbackService;

    @PostMapping
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
                  "content": "김치찌개 맛있게 끓이는 법 알려주세요"
                }
                """
                    )
            )
    )
    public ApiResponse<String> submitFoodGuideFeedback(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody FeedbackRequestDto feedbackDto) {
        feedbackService.submitFoodGuideFeedback(userDetails.getId(), feedbackDto);
        return ApiResponse.success("제출이 성공하였습니다");
    }
}

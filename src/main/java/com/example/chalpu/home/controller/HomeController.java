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
}

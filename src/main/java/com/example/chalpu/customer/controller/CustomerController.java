package com.example.chalpu.customer.controller;

import com.example.chalpu.common.response.ApiResponse;
import com.example.chalpu.customer.dto.CustomerResponse;
import com.example.chalpu.customer.service.CustomerService;
import com.example.chalpu.oauth.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "고객 관리", description = "고객 정보 관리 API")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 고객의 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<CustomerResponse>> getMyInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        CustomerResponse response = customerService.getCustomerById(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me/feedback-count")
    @Operation(summary = "내 피드백 수 조회", description = "현재 로그인한 고객의 총 피드백 작성 수를 조회합니다.")
    public ResponseEntity<ApiResponse<Integer>> getMyFeedbackCount(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        Integer feedbackCount = customerService.getFeedbackCount(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(feedbackCount));
    }

}
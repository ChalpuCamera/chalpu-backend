package com.example.chalpu.customer.controller;

import com.example.chalpu.common.response.ApiResponse;
import com.example.chalpu.customer.dto.CustomerResponse;
import com.example.chalpu.customer.dto.CustomerTasteRequest;
import com.example.chalpu.customer.dto.CustomerTasteResponse;
import com.example.chalpu.customer.service.CustomerService;
import com.example.chalpu.oauth.security.jwt.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "고객 관리", description = "고객 정보 관리 API - 인증된 고객의 프로필, 취향, 피드백 통계를 관리합니다")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/me")
    @Operation(
        summary = "내 정보 조회",
        description = "현재 로그인한 고객의 기본 정보를 조회합니다. 고객 ID, 이름, 이메일 등의 프로필 정보를 반환합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "고객 정보 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CustomerResponse.class),
                examples = @ExampleObject(
                    value = "{\"success\": true, \"message\": \"성공했습니다\", \"data\": {\"id\": 1, \"name\": \"홍길동\", \"email\": \"customer@example.com\", \"provider\": \"GOOGLE\", \"createdAt\": \"2024-01-01T10:00:00\"}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증이 필요합니다")
    })
    public ResponseEntity<ApiResponse<CustomerResponse>> getMyInfo(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        CustomerResponse response = customerService.getCustomerById(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me/feedback-count")
    @Operation(
        summary = "내 피드백 수 조회",
        description = "현재 로그인한 고객이 지금까지 작성한 총 피드백 개수를 조회합니다. 리워드 수령 자격 확인에 활용됩니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "피드백 개수 조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"success\": true, \"message\": \"성공했습니다\", \"data\": 15}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증이 필요합니다")
    })
    public ResponseEntity<ApiResponse<Integer>> getMyFeedbackCount(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        Integer feedbackCount = customerService.getFeedbackCount(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(feedbackCount));
    }

    @GetMapping("/me/taste")
    @Operation(
        summary = "내 취향 정보 조회",
        description = "현재 로그인한 고객의 음식 취향 설정을 조회합니다. 매운맛 선호도, 식사량, 식비 수준 등의 정보를 포함합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "취향 정보 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CustomerTasteResponse.class),
                examples = @ExampleObject(
                    value = "{\"success\": true, \"message\": \"성공했습니다\", \"data\": {\"spicyLevel\": 3, \"mealAmount\": 2, \"mealSpending\": 4}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증이 필요합니다"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "취향 정보가 설정되지 않았습니다")
    })
    public ResponseEntity<ApiResponse<CustomerTasteResponse>> getMyTaste(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        CustomerTasteResponse response = customerService.getCustomerTaste(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/me/taste")
    @Operation(
        summary = "내 취향 정보 생성/수정",
        description = "현재 로그인한 고객의 음식 취향을 설정하거나 업데이트합니다. 개인화된 음식 추천에 활용됩니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "취향 정보 저장 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"success\": true, \"message\": \"성공했습니다\", \"data\": null}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증이 필요합니다")
    })
    public ResponseEntity<ApiResponse<Void>> updateMyTaste(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "취향 정보 설정 데이터",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CustomerTasteRequest.class),
                    examples = @ExampleObject(
                        value = "{\"spicyLevel\": 3, \"mealAmount\": 2, \"mealSpending\": 4}"
                    )
                )
            )
            @RequestBody CustomerTasteRequest request) {
        
        customerService.updateCustomerTaste(userDetails.getId(), request.toEntity());
        return ResponseEntity.ok(ApiResponse.success());
    }
}
package com.example.chalpu.reward.controller;

import com.example.chalpu.common.response.ApiResponse;
import com.example.chalpu.oauth.security.jwt.UserDetailsImpl;
import com.example.chalpu.reward.dto.RewardRedemptionRequest;
import com.example.chalpu.reward.dto.RewardRedemptionResponse;
import com.example.chalpu.reward.dto.RewardResponse;
import com.example.chalpu.reward.service.RewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
@Tag(name = "리워드", description = "고객 리워드 관리 API")
public class RewardController {

    private final RewardService rewardService;

    @GetMapping
    @Operation(summary = "전체 리워드 목록 조회", description = "사용 가능한 모든 리워드 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<RewardResponse>>> getAllRewards() {
        List<RewardResponse> responses = rewardService.getAvailableRewards();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/me")
    @Operation(summary = "내가 받을 수 있는 리워드 조회", description = "현재 로그인한 고객이 받을 수 있는 리워드 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<RewardResponse>>> getMyAvailableRewards(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        List<RewardResponse> responses = rewardService.getAvailableRewardsForCustomer(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping("/redeem")
    @Operation(summary = "리워드 교환", description = "현재 로그인한 고객이 리워드를 교환합니다.")
    public ResponseEntity<ApiResponse<RewardRedemptionResponse>> redeemReward(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody RewardRedemptionRequest request) {
        
        RewardRedemptionResponse response = rewardService.redeemReward(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/redemptions/me")
    @Operation(summary = "내 리워드 교환 내역 조회", description = "현재 로그인한 고객의 모든 리워드 교환 내역을 조회합니다.")
    public ResponseEntity<ApiResponse<List<RewardRedemptionResponse>>> getMyRedemptions(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        List<RewardRedemptionResponse> responses = rewardService.getCustomerRedemptions(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/redemptions/me/active")
    @Operation(summary = "내가 사용 가능한 리워드 조회", description = "현재 로그인한 고객의 사용 가능한 리워드를 조회합니다.")
    public ResponseEntity<ApiResponse<List<RewardRedemptionResponse>>> getMyActiveRedemptions(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        List<RewardRedemptionResponse> responses = rewardService.getActiveRedemptions(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/redemptions/{redemptionId}/use")
    @Operation(summary = "리워드 사용 처리", description = "교환된 리워드를 사용 처리합니다.")
    public ResponseEntity<ApiResponse<Void>> markRedemptionAsUsed(
            @Parameter(description = "교환 ID", example = "1")
            @PathVariable("redemptionId") Long redemptionId) {
        
        rewardService.markRedemptionAsUsed(redemptionId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/redemptions/{redemptionId}/cancel")
    @Operation(summary = "리워드 사용 취소", description = "교환된 리워드의 사용을 취소합니다.")
    public ResponseEntity<ApiResponse<Void>> cancelRedemption(
            @Parameter(description = "교환 ID", example = "1")
            @PathVariable("redemptionId") Long redemptionId) {
        
        rewardService.cancelRedemption(redemptionId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/eligible")
    @Operation(summary = "내 리워드 수령 자격 확인", description = "현재 로그인한 고객의 리워드 수령 자격을 확인합니다.")
    public ResponseEntity<ApiResponse<Boolean>> checkMyRewardEligibility(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        boolean eligible = rewardService.isEligibleForReward(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(eligible));
    }
}
package com.example.chalpu.reward.service;

import com.example.chalpu.customer.domain.Customer;
import com.example.chalpu.customer.repository.CustomerRepository;

import com.example.chalpu.reward.domain.Reward;
import com.example.chalpu.reward.domain.RewardRedemption;
import com.example.chalpu.reward.dto.RewardRedemptionRequest;
import com.example.chalpu.reward.dto.RewardRedemptionResponse;
import com.example.chalpu.reward.dto.RewardResponse;
import com.example.chalpu.reward.repository.RewardRepository;
import com.example.chalpu.reward.repository.RewardRedemptionRepository;
import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.CustomerException;
import com.example.chalpu.common.exception.RewardException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RewardService {

    private final RewardRepository rewardRepository;
    private final RewardRedemptionRepository redemptionRepository;
    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<RewardResponse> getAvailableRewards() {
        List<Reward> rewards = rewardRepository.findAvailableRewards(LocalDate.now());
        return rewards.stream()
                .map(RewardResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RewardResponse> getAvailableRewardsForCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException(ErrorMessage.CUSTOMER_NOT_FOUND));

        List<Reward> allRewards = rewardRepository.findAvailableRewards(LocalDate.now());

        return allRewards.stream()
                .filter(reward -> canCustomerAffordReward(customer, reward))
                .map(RewardResponse::from)
                .collect(Collectors.toList());
    }

    private boolean canCustomerAffordReward(Customer customer, Reward reward) {
        Integer customerCount = customer.getRewardCount();
        Integer requiredCount = reward.getRequiredCount();
        
        return customerCount != null && requiredCount != null && customerCount >= requiredCount;
    }

    public RewardRedemptionResponse redeemReward(Long customerId, RewardRedemptionRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException(ErrorMessage.CUSTOMER_NOT_FOUND));

        Reward reward = rewardRepository.findById(request.getRewardId())
                .orElseThrow(() -> new RewardException(ErrorMessage.REWARD_NOT_FOUND));

        if (!reward.isAvailable()) {
            throw new RewardException(ErrorMessage.REWARD_NOT_AVAILABLE);
        }

        if (!canCustomerAffordReward(customer, reward)) {
            throw new RewardException(ErrorMessage.INSUFFICIENT_REWARD_COUNT);
        }

        // 리워드 횟수 차감
        try {
            customer.decrementRewardCount(reward.getRequiredCount());
        } catch (IllegalArgumentException e) {
            throw new RewardException(ErrorMessage.INSUFFICIENT_REWARD_COUNT);
        }

        RewardRedemption redemption = RewardRedemption.createRedemption(
                customer, reward, customer.getRewardCount());

        RewardRedemption savedRedemption = redemptionRepository.save(redemption);

        log.info("리워드 교환 완료: customerId={}, rewardId={}, required_count={}, remaining_count={}", 
                customerId, request.getRewardId(), reward.getRequiredCount(), 
                customer.getRewardCount());

        return RewardRedemptionResponse.from(savedRedemption);
    }

    @Transactional(readOnly = true)
    public List<RewardRedemptionResponse> getCustomerRedemptions(Long customerId) {
        List<RewardRedemption> redemptions = redemptionRepository
                .findByCustomerIdOrderByCreatedAtDesc(customerId);

        return redemptions.stream()
                .map(RewardRedemptionResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RewardRedemptionResponse> getActiveRedemptions(Long customerId) {
        List<RewardRedemption> redemptions = redemptionRepository
                .findByCustomerIdAndStatusOrderByCreatedAtDesc(
                        customerId, RewardRedemption.RedemptionStatus.ISSUED);

        return redemptions.stream()
                .map(RewardRedemptionResponse::from)
                .collect(Collectors.toList());
    }

    public void markRedemptionAsUsed(Long redemptionId) {
        RewardRedemption redemption = redemptionRepository.findById(redemptionId)
                .orElseThrow(() -> new RewardException(ErrorMessage.REWARD_REDEMPTION_NOT_FOUND));

        redemption.markAsUsed();
        log.info("리워드 사용 처리 완료: redemptionId={}", redemptionId);
    }

    public void cancelRedemption(Long redemptionId) {
        RewardRedemption redemption = redemptionRepository.findById(redemptionId)
                .orElseThrow(() -> new RewardException(ErrorMessage.REWARD_REDEMPTION_NOT_FOUND));

        redemption.cancel();
        log.info("리워드 사용 취소 완료: redemptionId={}", redemptionId);
    }

    @Transactional(readOnly = true)
    public boolean isEligibleForReward(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException(ErrorMessage.CUSTOMER_NOT_FOUND));
        
        return customer.getRewardCount() != null && customer.getRewardCount() > 0;
    }
}
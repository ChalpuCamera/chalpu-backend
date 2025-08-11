package com.example.chalpu.reward.repository;

import com.example.chalpu.reward.domain.RewardRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRedemptionRepository extends JpaRepository<RewardRedemption, Long> {

    List<RewardRedemption> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    
    List<RewardRedemption> findByCustomerIdAndStatusOrderByCreatedAtDesc(Long customerId, RewardRedemption.RedemptionStatus status);
    
    @Query("SELECT COUNT(rr) FROM RewardRedemption rr WHERE rr.customer.id = :customerId AND rr.status = 'ISSUED'")
    int countActiveRedemptionsByCustomerId(@Param("customerId") Long customerId);
    
    boolean existsByCustomerIdAndRewardIdAndStatus(Long customerId, Long rewardId, RewardRedemption.RedemptionStatus status);
}
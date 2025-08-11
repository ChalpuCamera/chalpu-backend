package com.example.chalpu.reward.domain;

import com.example.chalpu.common.entity.BaseTimeEntity;
import com.example.chalpu.customer.domain.Customer;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reward_redemptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class RewardRedemption extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "redemption_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;

    @Column(name = "reward_count")
    private Integer rewardCount;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private RedemptionStatus status;

    public static RewardRedemption createRedemption(Customer customer, Reward reward, Integer rewardCount) {
        return RewardRedemption.builder()
                .customer(customer)
                .reward(reward)
                .rewardCount(rewardCount)
                .status(RedemptionStatus.ISSUED)
                .build();
    }

    public void markAsUsed() {
        this.status = RedemptionStatus.USED;
    }

    public void cancel() {
        this.status = RedemptionStatus.CANCELLED;
    }

    public boolean isActive() {
        return this.status == RedemptionStatus.ISSUED;
    }

    public enum RedemptionStatus {
        ISSUED, USED, CANCELLED
    }
}
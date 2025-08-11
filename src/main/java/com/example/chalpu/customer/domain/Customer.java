package com.example.chalpu.customer.domain;

import com.example.chalpu.common.entity.BaseTimeEntity;
import com.example.chalpu.oauth.model.AuthProvider;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Customer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;

    @Column(length = 100)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    private Integer age;

    @Builder.Default
    @Column(name = "feedback_count")
    private Integer feedbackCount = 0;

    @Builder.Default
    @Column(name = "reward_count")
    private Integer rewardCount = 0;

    @Column(length = 100, unique = true, nullable = false)
    private String email;

    @Column(length = 100)
    private String socialId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AuthProvider provider;

    @Column(length = 255)
    private String picture;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public static Customer createCustomer(String email, String nickname, String socialId, 
                                        AuthProvider provider, String picture) {
        return Customer.builder()
                .email(email)
                .nickname(nickname)
                .socialId(socialId)
                .provider(provider)
                .picture(picture)
                .build();
    }

    public void updateProfile(String nickname, Gender gender, Integer age) {
        this.nickname = nickname;
        this.gender = gender;
        this.age = age;
    }

    public void updateOAuth2Info(String nickname, String picture) {
        this.nickname = nickname;
        this.picture = picture;
    }

    public void incrementFeedbackCount() {
        this.feedbackCount = this.feedbackCount + 1;
    }

    public void incrementRewardCount() {
        this.rewardCount = this.rewardCount + 1;
    }

    public void decrementRewardCount(Integer count) {
        if (this.rewardCount < count) {
            throw new IllegalArgumentException("보유 리워드 횟수가 부족합니다.");
        }
        this.rewardCount = this.rewardCount - count;
    }

    public boolean canAffordReward(Integer requiredCount) {
        return this.rewardCount >= requiredCount;
    }

    public void softDelete() {
        this.isActive = false;
    }

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}
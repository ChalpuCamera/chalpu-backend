package com.example.chalpu.feedback.domain;

import com.example.chalpu.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feedbacks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Feedback extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_type", nullable = false, length = 30)
    private FeedbackType feedbackType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "food_name", length = 100)
    private String foodName;

    public static Feedback createFoodGuideFeedback(Long userId, String content, String foodName) {
        return Feedback.builder()
                .userId(userId)
                .feedbackType(FeedbackType.FOOD_GUIDE_REQUEST)
                .content(content)
                .foodName(foodName)
                .build();
    }

    public static Feedback createAppImprovementFeedback(Long userId, String content) {
        return Feedback.builder()
                .userId(userId)
                .feedbackType(FeedbackType.APP_IMPROVEMENT)
                .content(content)
                .build();
    }

    public boolean isFoodGuideFeedback() {
        return this.feedbackType == FeedbackType.FOOD_GUIDE_REQUEST;
    }

    public boolean isAppImprovementFeedback() {
        return this.feedbackType == FeedbackType.APP_IMPROVEMENT;
    }
}
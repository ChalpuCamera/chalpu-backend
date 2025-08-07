package com.example.chalpu.feedback.service;

import com.example.chalpu.feedback.domain.Feedback;
import com.example.chalpu.feedback.dto.AppImprovementFeedbackDto;
import com.example.chalpu.feedback.dto.FoodGuideFeedbackDto;
import com.example.chalpu.feedback.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public void submitFoodGuideFeedback(Long userId, FoodGuideFeedbackDto feedbackDto) {
        Feedback feedback = Feedback.createFoodGuideFeedback(
                userId,
                feedbackDto.getContent(),
                feedbackDto.getFoodName()
        );
        
        feedbackRepository.save(feedback);
        log.info("event=food_fuide_feedback_post_success - ID: {}", feedback.getId());
    }

    public void submitAppImprovementFeedback(Long userId, AppImprovementFeedbackDto feedbackDto) {
        Feedback feedback = Feedback.createAppImprovementFeedback(
                userId,
                feedbackDto.getContent()
        );
        
        feedbackRepository.save(feedback);
        log.info("event=app_improvement_feedback_post_success - ID: {}", feedback.getId());
    }
}
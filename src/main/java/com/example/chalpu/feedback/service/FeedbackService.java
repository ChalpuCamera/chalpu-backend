package com.example.chalpu.feedback.service;

import com.example.chalpu.feedback.domain.Feedback;
import com.example.chalpu.feedback.dto.AppImprovementFeedbackDto;
import com.example.chalpu.feedback.dto.FoodGuideFeedbackDto;
import com.example.chalpu.feedback.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.chalpu.feedback.dto.FeedbackRequestDto;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public void submitFoodGuideFeedback(Long userId, FeedbackRequestDto feedbackDto) {
        Feedback feedback = Feedback.createFoodGuideFeedback(
                userId,
                feedbackDto.getContent()
        );
        
        feedbackRepository.save(feedback);
        log.info("event=food_fuide_feedback_post_success - ID: {}", feedback.getId());
    }
}
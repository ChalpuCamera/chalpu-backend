package com.example.chalpu.survey.repository;

import com.example.chalpu.survey.domain.SurveyAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long> {

    List<SurveyAnswer> findByFeedbackIdOrderByQuestionId(Long feedbackId);
    
    List<SurveyAnswer> findByQuestionId(Long questionId);
    
    void deleteByFeedbackId(Long feedbackId);
}
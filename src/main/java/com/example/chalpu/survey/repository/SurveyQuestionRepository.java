package com.example.chalpu.survey.repository;

import com.example.chalpu.survey.domain.SurveyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyQuestionRepository extends JpaRepository<SurveyQuestion, Long> {

    List<SurveyQuestion> findBySurveyIdOrderById(Long surveyId);
    
    int countBySurveyId(Long surveyId);
}
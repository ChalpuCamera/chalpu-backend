package com.example.chalpu.survey.domain;

import com.example.chalpu.customerfeedback.domain.CustomerFeedback;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "survey_answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class SurveyAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_id", nullable = false)
    private CustomerFeedback feedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private SurveyQuestion question;

    @Column(name = "answer_text", columnDefinition = "TEXT")
    private String answerText;

    @Column(name = "numeric_value")
    private Float numericValue;

    public static SurveyAnswer createAnswer(CustomerFeedback feedback, SurveyQuestion question, 
                                         String answerText, Float numericValue) {
        return SurveyAnswer.builder()
                .feedback(feedback)
                .question(question)
                .answerText(answerText)
                .numericValue(numericValue)
                .build();
    }

    public boolean hasNumericValue() {
        return this.numericValue != null;
    }

    public boolean hasTextAnswer() {
        return this.answerText != null && !this.answerText.trim().isEmpty();
    }

    public boolean isValidAnswer() {
        QuestionType questionType = this.question.getQuestionType();
        
        return switch (questionType) {
            case SLIDER, RATING -> hasNumericValue() && questionType.isValidNumericValue(numericValue);
            case TEXT -> hasTextAnswer();
        };
    }

    public boolean isNumericAnswer() {
        return this.question.getQuestionType().isNumericType();
    }

    public boolean isTextAnswer() {
        return this.question.getQuestionType().isTextType();
    }
}
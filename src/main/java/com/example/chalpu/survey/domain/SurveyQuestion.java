package com.example.chalpu.survey.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "survey_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class SurveyQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    @Column(name = "question_text", length = 255, nullable = false)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    public static SurveyQuestion createQuestion(Survey survey, String questionText, QuestionType questionType) {
        return SurveyQuestion.builder()
                .survey(survey)
                .questionText(questionText)
                .questionType(questionType)
                .build();
    }

    public void updateQuestion(String questionText, QuestionType questionType) {
        this.questionText = questionText;
        this.questionType = questionType;
    }
}
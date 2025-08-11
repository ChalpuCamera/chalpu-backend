package com.example.chalpu.survey.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionType {
    SLIDER("슬라이더", -2.0f, 2.0f),
    TEXT("주관식", null, null),
    RATING("평점", 0f, 10f);

    private final String description;
    private final Float minValue;
    private final Float maxValue;

    public boolean isNumericType() {
        return this == SLIDER || this == RATING;
    }

    public boolean isTextType() {
        return this == TEXT;
    }

    public boolean isValidNumericValue(Float value) {
        if (!isNumericType() || value == null) {
            return false;
        }
        return value >= minValue && value <= maxValue;
    }
}
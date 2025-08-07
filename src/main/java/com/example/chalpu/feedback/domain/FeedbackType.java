package com.example.chalpu.feedback.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FeedbackType {
    FOOD_GUIDE_REQUEST("음식 가이드 요청"),
    APP_IMPROVEMENT("앱 개선 제안");

    private final String description;
}
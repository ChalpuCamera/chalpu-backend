package com.example.chalpu.feedback.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FoodGuideFeedbackDto extends FeedbackRequestDto {
    private String foodName;
}
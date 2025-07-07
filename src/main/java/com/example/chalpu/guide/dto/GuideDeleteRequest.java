package com.example.chalpu.guide.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class GuideDeleteRequest {
    private List<Long> guideIds;
} 
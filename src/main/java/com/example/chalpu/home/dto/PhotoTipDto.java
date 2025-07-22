package com.example.chalpu.home.dto;

import com.example.chalpu.home.domain.PhotoTip;

public record PhotoTipDto(String id, String title, String text) {
    public static PhotoTipDto from(PhotoTip tip) {
        return new PhotoTipDto(tip.getId(), tip.getTitle(), tip.getText());
    }
} 
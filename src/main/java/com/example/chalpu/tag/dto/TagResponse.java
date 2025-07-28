package com.example.chalpu.tag.dto;

import com.example.chalpu.tag.domain.Tag;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TagResponse {
    private final Long tagId;
    private final String tagName;

    public static TagResponse from(Tag tag) {
        return TagResponse.builder()
                .tagId(tag.getId())
                .tagName(tag.getName())
                .build();
    }
} 
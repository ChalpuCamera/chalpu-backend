package com.example.chalpu.guide.dto;

import com.example.chalpu.guide.domain.Guide;
import com.example.chalpu.tag.domain.GuideTag;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class GuideResponse {

    private final Long guideId;
    private final String content;
    private final String guideS3Key;
    private final String fileName;
    private final String imageS3Key;
    private final String svgS3Key;
    private final String categoryName;
    private final String subCategoryName;
    private final List<String> tags;

    public static GuideResponse from(Guide guide, List<GuideTag> guideTags) {
        return GuideResponse.builder()
                .guideId(guide.getId())
                .content(guide.getContent())
                .guideS3Key(guide.getGuideS3Key())
                .fileName(guide.getFileName())
                .imageS3Key(guide.getImageS3Key())
                .svgS3Key(guide.getSvgS3Key())
                .categoryName(guide.getSubCategory().getCategory().getName())
                .subCategoryName(guide.getSubCategory().getName())
                .tags(guideTags.stream()
                        .map(guideTag -> guideTag.getTag().getName())
                        .collect(Collectors.toList()))
                .build();
    }
} 
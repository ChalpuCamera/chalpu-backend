package com.example.chalpu.guide.dto;

import com.example.chalpu.guide.domain.SubCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubCategoryResponse {

    private final Long id;
    private final String name;
    private final String tips;
    private final String categoryName;

    public static SubCategoryResponse from(SubCategory subCategory) {
        return SubCategoryResponse.builder()
                .id(subCategory.getId())
                .name(subCategory.getName())
                .tips(subCategory.getTips())
                .categoryName(subCategory.getCategory().getName())
                .build();
    }
}
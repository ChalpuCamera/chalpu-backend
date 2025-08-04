package com.example.chalpu.guide.service;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.NoticeException;
import com.example.chalpu.guide.domain.SubCategory;
import com.example.chalpu.guide.dto.SubCategoryResponse;
import com.example.chalpu.guide.dto.SubCategoryUpdateRequest;
import com.example.chalpu.guide.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;

    public List<SubCategoryResponse> getAllSubCategories() {
        List<SubCategory> subCategories = subCategoryRepository.findAll();
        return subCategories.stream()
                .map(SubCategoryResponse::from)
                .collect(Collectors.toList());
    }

    public SubCategoryResponse getSubCategoryById(Long id) {
        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow(() -> new NoticeException(ErrorMessage.SUB_CATEGORY_NOT_FOUND));
        return SubCategoryResponse.from(subCategory);
    }

    @Transactional
    public SubCategoryResponse updateSubCategory(Long id, SubCategoryUpdateRequest request) {
        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow(() -> new NoticeException(ErrorMessage.SUB_CATEGORY_NOT_FOUND));
        
        subCategory.setTips(request.getTips());
        
        log.info("event=sub_category_updated, sub_category_id={}", id);
        return SubCategoryResponse.from(subCategory);
    }
}
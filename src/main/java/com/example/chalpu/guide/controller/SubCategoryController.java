package com.example.chalpu.guide.controller;

import com.example.chalpu.common.response.ApiResponse;
import com.example.chalpu.guide.dto.SubCategoryResponse;
import com.example.chalpu.guide.dto.SubCategoryUpdateRequest;
import com.example.chalpu.guide.service.SubCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sub-categories")
@RequiredArgsConstructor
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubCategoryResponse>>> getAllSubCategories() {
        List<SubCategoryResponse> response = subCategoryService.getAllSubCategories();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubCategoryResponse>> getSubCategoryById(@PathVariable("id") Long id) {
        SubCategoryResponse response = subCategoryService.getSubCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<SubCategoryResponse>> updateSubCategory(
            @PathVariable("id") Long id,
            @RequestBody SubCategoryUpdateRequest request) {
        SubCategoryResponse response = subCategoryService.updateSubCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
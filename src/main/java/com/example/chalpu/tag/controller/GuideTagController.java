package com.example.chalpu.tag.controller;

import com.example.chalpu.common.response.ApiResponse;
import com.example.chalpu.tag.dto.TagRequest;
import com.example.chalpu.tag.dto.TagResponse;
import com.example.chalpu.tag.service.GuideTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "가이드-태그 관리 API", description = "특정 가이드에 대한 태그를 추가/삭제/조회하는 API")
@RestController
@RequestMapping("api/guides/{guideId}/tags")
@RequiredArgsConstructor
public class GuideTagController {

    private final GuideTagService guideTagService;

    @Operation(summary = "가이드에 태그 추가", description = "특정 가이드에 새로운 태그를 연결합니다. 태그가 DB에 없으면 새로 생성됩니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<TagResponse>> addTagToGuide(@PathVariable Long guideId, @RequestBody TagRequest request) {
        TagResponse response = guideTagService.addTagToGuide(guideId, request.getTagName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "가이드의 태그 삭제", description = "특정 가이드와 특정 태그의 연결을 끊습니다.")
    @DeleteMapping("/{tagId}")
    public ResponseEntity<ApiResponse<Void>> removeTagFromGuide(@PathVariable Long guideId, @PathVariable Long tagId) {
        guideTagService.removeTagFromGuide(guideId, tagId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "가이드의 모든 태그 조회", description = "특정 가이드에 연결된 모든 태그 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TagResponse>>> getTagsForGuide(@PathVariable Long guideId) {
        List<TagResponse> response = guideTagService.getTagsForGuide(guideId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

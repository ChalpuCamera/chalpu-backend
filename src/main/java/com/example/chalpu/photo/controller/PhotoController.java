package com.example.chalpu.photo.controller;

import com.example.chalpu.common.response.ApiResponse;
import com.example.chalpu.common.response.PageResponse;
import com.example.chalpu.oauth.security.jwt.UserDetailsImpl;
import com.example.chalpu.photo.dto.PhotoResponse;
import com.example.chalpu.photo.service.PhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/foods/{foodId}/photos")
@RequiredArgsConstructor
@Tag(name = "Photo", description = "사진 관련 API")
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping
    @Operation(
        summary = "음식 사진 업로드",
        description = "특정 음식에 사진을 업로드합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    public ResponseEntity<ApiResponse<PhotoResponse>> uploadPhoto(
            @PathVariable Long foodId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        PhotoResponse photo = photoService.uploadPhoto(foodId, file, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("사진 업로드가 완료되었습니다.", photo));
    }

    @GetMapping
    @Operation(
        summary = "음식 사진 목록",
        description = """
                특정 음식의 모든 사진 목록을 조회합니다.
                
                **페이지네이션 파라미터:**
                - `page`: 페이지 번호 (0부터 시작, 기본값: 0)
                - `size`: 페이지 크기 (기본값: 20)
                - `sort`: 정렬 조건 (선택사항)
                
                **요청 예시:**
                ```
                GET /api/foods/{foodId}/photos?page=0&size=10&sort=uploadDate,desc&sort=fileName,asc
                ```
                위처럼 정렬 조건을 리스트로 줘도 되고 아래처럼 String으로 줘도 됩니다.
                ```
                GET /api/foods/{foodId}/photos?page=0&size=10&sort=uploadDate,desc
                ```
                """,
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    public ResponseEntity<ApiResponse<PageResponse<PhotoResponse>>> getPhotos(
            @PathVariable Long foodId,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<PhotoResponse> photos = photoService.getPhotos(foodId, pageable);
        return ResponseEntity.ok(ApiResponse.success("사진 목록 조회가 완료되었습니다.", photos));
    }

    @PatchMapping("/{photoId}/feature")
    @Operation(
        summary = "대표 사진 지정",
        description = "특정 사진을 대표 사진으로 지정합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    public ResponseEntity<ApiResponse<PhotoResponse>> setFeaturedPhoto(
            @PathVariable Long foodId,
            @PathVariable Long photoId,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        PhotoResponse photo = photoService.setFeaturedPhoto(foodId, photoId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("대표 사진 지정이 완료되었습니다.", photo));
    }
} 
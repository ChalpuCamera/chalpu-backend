package com.example.chalpu.photo.service;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.PhotoException;
import com.example.chalpu.common.response.PageResponse;
import com.example.chalpu.photo.domain.Photo;
import com.example.chalpu.photo.dto.PhotoResponse;
import com.example.chalpu.photo.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhotoService {

    private final PhotoRepository photoRepository;

    /**
     * 사진 업로드
     */
    @Transactional
    public PhotoResponse uploadPhoto(Long foodId, MultipartFile file, Long userId) {
        log.info("uploadPhoto 시작 - foodId: {}, fileName: {}, userId: {}", 
                foodId, file.getOriginalFilename(), userId);
        
        try {
            // TODO: 파일 업로드 로직 구현
            Photo photo = createPhotoEntity(file, foodId);
            Photo savedPhoto = photoRepository.save(photo);
            
            log.info("uploadPhoto 성공 - photoId: {}, fileName: {}", 
                    savedPhoto.getId(), savedPhoto.getFileName());
            
            return PhotoResponse.from(savedPhoto);
        } catch (Exception e) {
            log.error("uploadPhoto 실패 - foodId: {}, fileName: {}, error: {}", 
                    foodId, file.getOriginalFilename(), e.getMessage(), e);
            throw new PhotoException(ErrorMessage.PHOTO_UPLOAD_FAILED);
        }
    }

    /**
     * 음식별 사진 목록 조회
     */
    public PageResponse<PhotoResponse> getPhotos(Long foodId, Pageable pageable) {
        log.info("getPhotos 시작 - foodId: {}, page: {}, size: {}", 
                foodId, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<Photo> photoPage = photoRepository.findByFoodItemId(foodId, pageable);
            Page<PhotoResponse> photoResponsePage = photoPage.map(PhotoResponse::from);
            
            log.info("getPhotos 성공 - foodId: {}, totalElements: {}", 
                    foodId, photoPage.getTotalElements());
            
            return PageResponse.from(photoResponsePage);
        } catch (Exception e) {
            log.error("getPhotos 실패 - foodId: {}, error: {}", foodId, e.getMessage(), e);
            throw new PhotoException(ErrorMessage.PHOTO_NOT_FOUND);
        }
    }

    /**
     * 대표 사진 설정
     */
    @Transactional
    public PhotoResponse setFeaturedPhoto(Long foodId, Long photoId, Long userId) {
        log.info("setFeaturedPhoto 시작 - foodId: {}, photoId: {}, userId: {}", 
                foodId, photoId, userId);
        
        try {
            // TODO: 권한 검증 로직 추가
            Photo photo = findPhotoById(photoId);
            
            // TODO: 대표 사진 지정 로직 구현
            
            log.info("setFeaturedPhoto 성공 - foodId: {}, photoId: {}", foodId, photoId);
            
            return PhotoResponse.from(photo);
        } catch (Exception e) {
            log.error("setFeaturedPhoto 실패 - foodId: {}, photoId: {}, error: {}", 
                    foodId, photoId, e.getMessage(), e);
            throw new PhotoException(ErrorMessage.PHOTO_FEATURE_UPDATE_FAILED);
        }
    }

    /**
     * 사진 삭제
     */
    @Transactional
    public void deletePhoto(Long photoId, Long userId) {
        log.info("deletePhoto 시작 - photoId: {}, userId: {}", photoId, userId);
        
        try {
            Photo photo = findPhotoById(photoId);
            
            // TODO: 권한 검증 로직 추가
            // TODO: 파일 시스템에서 실제 파일 삭제 로직 추가
            
            photoRepository.delete(photo);
            
            log.info("deletePhoto 성공 - photoId: {}", photoId);
        } catch (Exception e) {
            log.error("deletePhoto 실패 - photoId: {}, error: {}", photoId, e.getMessage(), e);
            throw new PhotoException(ErrorMessage.PHOTO_DELETE_FAILED);
        }
    }

    // === 내부 유틸리티 메서드들 ===

    /**
     * 사진 엔티티 생성
     */
    private Photo createPhotoEntity(MultipartFile file, Long foodId) {
        return Photo.builder()
                .fileName(file.getOriginalFilename())
                .filePath("temp/path") // TODO: 실제 파일 경로 설정
                .fileSize((int) file.getSize())
                .build();
    }

    /**
     * 사진 ID로 조회
     */
    private Photo findPhotoById(Long photoId) {
        return photoRepository.findById(photoId)
                .orElseThrow(() -> new PhotoException(ErrorMessage.PHOTO_NOT_FOUND));
    }
} 
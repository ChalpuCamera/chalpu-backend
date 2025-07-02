package com.example.chalpu.photo.service;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.PhotoException;
import com.example.chalpu.common.response.PageResponse;
import com.example.chalpu.photo.domain.Photo;
import com.example.chalpu.photo.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhotoService {

    private final PhotoRepository photoRepository;

    @Transactional
    public Photo uploadPhoto(Long foodId, MultipartFile file, Long userId) {
        // TODO: 파일 업로드 로직 구현
        Photo photo = Photo.builder()
                .fileName(file.getOriginalFilename())
                .filePath("temp/path")
                .fileSize((int) file.getSize())
                .build();
        return photoRepository.save(photo);
    }

    public PageResponse<Photo> getPhotos(Long foodId, Pageable pageable) {
        Page<Photo> photoPage = photoRepository.findByFoodItemId(foodId, pageable);
        return PageResponse.from(photoPage);
    }

    @Transactional
    public Photo setFeaturedPhoto(Long foodId, Long photoId, Long userId) {
        // TODO: 권한 검증 및 대표 사진 지정 로직 구현
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new PhotoException(ErrorMessage.PHOTO_NOT_FOUND));
        return photo;
    }
} 
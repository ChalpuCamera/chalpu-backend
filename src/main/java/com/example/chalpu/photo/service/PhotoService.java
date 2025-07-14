package com.example.chalpu.photo.service;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.PhotoException;
import com.example.chalpu.common.response.PageResponse;
import com.example.chalpu.photo.domain.Photo;
import com.example.chalpu.photo.dto.PhotoPresignedUrlResponse;
import com.example.chalpu.photo.dto.PhotoRegisterRequest;
import com.example.chalpu.photo.dto.PhotoResponse;
import com.example.chalpu.photo.dto.PhotoUploadRequest;
import com.example.chalpu.photo.repository.PhotoRepository;
import com.example.chalpu.store.domain.Store;
import com.example.chalpu.store.repository.StoreRepository;
import com.example.chalpu.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final StoreRepository storeRepository;
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.cloudfront.domain}")
    private String cloudfrontDomain;

    public PhotoPresignedUrlResponse generatePresignedUrl(final String username, final PhotoUploadRequest request) {
        // ToDo: 사용자 권한 검증 로직 추가 (e.g., storeId를 받아서 해당 가게의 관리자인지 확인)
        try {
            String s3Key = createS3Key(request.getFileName());
            URL presignedUrl = createPresignedUrl(s3Key);

            log.info("event=presigned_url_generated, username={}, file_name={}, s3_key={}",
                    username, request.getFileName(), s3Key);

            return PhotoPresignedUrlResponse.builder()
                    .presignedUrl(presignedUrl.toString())
                    .s3Key(s3Key)
                    .build();
        } catch (Exception e) {
            log.error("event=presigned_url_generation_failed, username={}, file_name={}, error_message={}",
                    username, request.getFileName(), e.getMessage(), e);
            throw new PhotoException(ErrorMessage.PRESIGNED_URL_GENERATION_FAILED);
        }
    }

    @Transactional
    public PhotoResponse registerPhoto(final String username, final PhotoRegisterRequest request) {
        try {
            Store store = storeRepository.findById(request.getStoreId())
                    .orElseThrow(() -> new PhotoException(ErrorMessage.STORE_NOT_FOUND));
            // ToDo: Add FoodItem logic if foodItemId is not null

            Photo photo = Photo.builder()
                    .s3Key(request.getS3Key())
                    .fileName(request.getFileName())
                    .store(store)
                    .fileSize(request.getFileSize())
                    .imageWidth(request.getImageWidth())
                    .imageHeight(request.getImageHeight())
                    .isActive(true)
                    .isFeatured(false)
                    .build();

            Photo savedPhoto = photoRepository.save(photo);

            log.info("event=photo_registered, photo_id={}, s3_key={}, username={}",
                    savedPhoto.getId(), savedPhoto.getS3Key(), username);

            return PhotoResponse.from(savedPhoto, cloudfrontDomain);
        } catch (Exception e) {
            log.error("event=photo_registration_failed, s3_key={}, username={}, error_message={}",
                    request.getS3Key(), username, e.getMessage(), e);
            throw new PhotoException(ErrorMessage.PHOTO_REGISTRATION_FAILED);
        }
    }

    public PageResponse<PhotoResponse> getPhotosByStore(final Long storeId, final Pageable pageable) {
        try {
            Page<Photo> photoPage = photoRepository.findByStoreIdWithJoin(storeId, pageable);
            return PageResponse.from(photoPage.map(photo -> PhotoResponse.from(photo, cloudfrontDomain)));
        } catch (Exception e) {
            log.error("event=photos_by_store_failed, store_id={}, error_message={}",
                    storeId, e.getMessage(), e);
            throw new PhotoException(ErrorMessage.PHOTO_NOT_FOUND);
        }
    }

    public PageResponse<PhotoResponse> getPhotosByFoodItem(final Long foodItemId, final Pageable pageable) {
        try {
            Page<Photo> photoPage = photoRepository.findByFoodItemIdWithoutJoin(foodItemId, pageable);
            return PageResponse.from(photoPage.map(photo -> PhotoResponse.from(photo, cloudfrontDomain)));
        } catch (Exception e) {
            log.error("event=photos_by_food_item_failed, food_item_id={}, error_message={}",
                    foodItemId, e.getMessage(), e);
            throw new PhotoException(ErrorMessage.PHOTO_NOT_FOUND);
        }
    }

    public PhotoResponse getPhoto(final Long photoId) {
        try {
            Photo photo = findPhotoByIdWithoutJoin(photoId);
            return PhotoResponse.from(photo, cloudfrontDomain);
        } catch (Exception e) {
            log.error("event=photo_get_failed, photo_id={}, error_message={}",
                    photoId, e.getMessage(), e);
            throw new PhotoException(ErrorMessage.PHOTO_NOT_FOUND);
        }
    }

    @Transactional
    public void deletePhoto(final String username, final Long photoId) {
        try {
            Photo photo = findPhotoByIdWithoutJoin(photoId);
            // ToDo: 권한 검증 로직 (username이 이 사진을 삭제할 권한이 있는지)

            deleteS3Object(photo.getS3Key());
            photo.setIsActive(false);

            log.info("event=photo_deleted, photo_id={}, username={}", photoId, username);
        } catch (Exception e) {
            log.error("event=photo_deletion_failed, photo_id={}, username={}, error_message={}",
                    username, photoId, e.getMessage(), e);
            throw new PhotoException(ErrorMessage.PHOTO_DELETE_FAILED);
        }
    }

    private Photo findPhotoByIdWithoutJoin(final Long photoId) {
        return photoRepository.findByIdAndIsActiveTrueWithoutJoin(photoId)
                .orElseThrow(() -> new PhotoException(ErrorMessage.PHOTO_NOT_FOUND));
    }

    private void deleteS3Object(final String s3Key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            log.info("event=s3_object_deleted, s3_key={}", s3Key);
        } catch (Exception e) {
            log.error("event=s3_object_deletion_failed, s3_key={}, error_message={}",
                    s3Key, e.getMessage(), e);
        }
    }

    private URL createPresignedUrl(final String s3Key) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10)) // The URL will be valid for 10 minutes
                .putObjectRequest(objectRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url();
    }

    private String createS3Key(final String fileName) {
        Objects.requireNonNull(fileName, "fileName must not be null");
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            throw new PhotoException(ErrorMessage.PHOTO_INVALID_FORMAT);
        }
        final String fileExtension = fileName.substring(lastDotIndex);
        return "foodPhoto/" + UUID.randomUUID() + fileExtension;
    }
}
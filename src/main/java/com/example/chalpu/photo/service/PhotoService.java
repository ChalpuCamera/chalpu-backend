package com.example.chalpu.photo.service;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.PhotoException;
import com.example.chalpu.common.response.PageResponse;
import com.example.chalpu.photo.domain.Photo;
import com.example.chalpu.photo.dto.*;
import com.example.chalpu.photo.repository.PhotoRepository;
import com.example.chalpu.store.domain.Store;
import com.example.chalpu.store.repository.StoreRepository;
import com.example.chalpu.user.domain.User;
import com.example.chalpu.user.repository.UserRepository;
import com.example.chalpu.store.service.UserStoreRoleService;
import com.example.chalpu.fooditem.domain.FoodItem;
import com.example.chalpu.fooditem.repository.FoodItemRepository;
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
import org.springframework.web.multipart.MultipartFile;

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
    private final UserStoreRoleService userStoreRoleService;
    private final FoodItemRepository foodItemRepository;
    private final PhotoRoomService photoRoomService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.cloudfront.domain}")
    private String cloudfrontDomain;

    public PhotoPresignedUrlResponse generatePresignedUrl(final Long userId, final PhotoUploadRequest request) {
        try {
            String s3Key = createS3Key(request.getFileName());
            URL presignedUrl = createPresignedUrl(s3Key);
            log.info("event=presigned_url_generated, user_id={}, file_name={}, s3_key={}",
                    userId, request.getFileName(), s3Key);
            return PhotoPresignedUrlResponse.builder()
                    .presignedUrl(presignedUrl.toString())
                    .s3Key(s3Key)
                    .build();
        } catch (Exception e) {
            log.error("event=presigned_url_generation_failed, user_id={}, file_name={}, error_message={}",
                    userId, request.getFileName(), e.getMessage(), e);
            throw new PhotoException(ErrorMessage.PRESIGNED_URL_GENERATION_FAILED);
        }
    }

    @Transactional
    public PhotoResponse registerPhoto(final Long userId, final PhotoRegisterRequest request) {
        try {
            Store store = storeRepository.findById(request.getStoreId())
                    .orElseThrow(() -> new PhotoException(ErrorMessage.STORE_NOT_FOUND));

            FoodItem foodItem = null;
            if (request.getFoodItemId() != null) {
                foodItem = foodItemRepository.findById(request.getFoodItemId())
                        .orElseThrow(() -> new PhotoException(ErrorMessage.FOODITEM_NOT_FOUND));
            }
            Photo photo = Photo.builder()
                    .s3Key(request.getS3Key())
                    .fileName(request.getFileName())
                    .store(store)
                    .foodItem(foodItem)
                    .fileSize(request.getFileSize())
                    .imageWidth(request.getImageWidth())
                    .imageHeight(request.getImageHeight())
                    .isActive(true)
                    .isFeatured(false)
                    .build();
            Photo savedPhoto = photoRepository.save(photo);
            log.info("event=photo_registered, photo_id={}, s3_key={}, user_id={}",
                    savedPhoto.getId(), savedPhoto.getS3Key(), userId);
            return PhotoResponse.from(savedPhoto, cloudfrontDomain);
        } catch (Exception e) {
            log.error("event=photo_registration_failed, s3_key={}, user_id={}, error_message={}",
                    request.getS3Key(), userId, e.getMessage(), e);
            throw new PhotoException(ErrorMessage.PHOTO_REGISTRATION_FAILED);
        }
    }

    public PageResponse<PhotoResponse> getPhotosByStore(final Long storeId, final Pageable pageable) {
        try {
            Page<Photo> photoPage = photoRepository.findByStoreIdAndIsActiveTrueWithoutJoin(storeId, pageable);
            return PageResponse.from(photoPage.map(photo -> PhotoResponse.from(photo, cloudfrontDomain)));
        } catch (Exception e) {
            log.error("event=photos_by_store_failed, store_id={}, error_message={}",
                    storeId, e.getMessage(), e);
            throw new PhotoException(ErrorMessage.PHOTO_NOT_FOUND);
        }
    }

    public PageResponse<PhotoResponse> getPhotosByFoodItem(final Long foodItemId, final Pageable pageable) {
        try {
            Page<Photo> photoPage = photoRepository.findByFoodItemIdAndIsActiveTrueWithoutJoin(foodItemId, pageable);
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
    public void deletePhoto(final Long userId, final Long photoId) {
        try {
            Photo photo = findPhotoByIdWithoutJoin(photoId);
            if (!userStoreRoleService.canUserManageStore(userId, photo.getStore().getId())) {
                throw new PhotoException(ErrorMessage.STORE_ACCESS_DENIED);
            }
            deleteS3Object(photo.getS3Key());
            photo.softDelete();
            log.info("event=photo_deleted, photo_id={}, user_id={}", photoId, userId);
        } catch (Exception e) {
            log.error("event=photo_deletion_failed, photo_id={}, user_id={}, error_message={}",
                    photoId, userId, e.getMessage(), e);
            throw new PhotoException(ErrorMessage.PHOTO_DELETE_FAILED);
        }
    }

    @Transactional
    public void setFeaturedPhoto(final Long userId, final PhotoSetFeaturedRequest request) {
        try {
            Photo photo = findPhotoByIdWithoutJoin(request.getPhotoId());
            if (!userStoreRoleService.canUserManageStore(userId, photo.getStore().getId())) {
                throw new PhotoException(ErrorMessage.STORE_ACCESS_DENIED);
            }
            FoodItem foodItem = foodItemRepository.findById(request.getFoodItemId())
                    .orElseThrow(() -> new PhotoException(ErrorMessage.FOODITEM_NOT_FOUND));
            foodItem.setThumbnailUrl(photo.getS3Key());
            foodItemRepository.save(foodItem);
            log.info("event=featured_photo_set, photo_id={}, user_id={}", request.getPhotoId(), userId);
        } catch (Exception e) {
            log.error("event=featured_photo_set_failed, photo_id={}, user_id={}, error_message={}",
                    request.getPhotoId(), userId, e.getMessage(), e);
            throw new PhotoException(ErrorMessage.PHOTO_SET_FEATURED_FAILED);
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

    public byte[] processBackgroundRemovalAsBytes(final Long userId, final MultipartFile file, final PhotoBackgroundRemovalRequest request) {
        try {
            // 포토룸 API로 배경 제거
            byte[] processedImageBytes = photoRoomService.removeBackground(file);
            
            log.info("event=background_removal_processed, user_id={}, file_name={}", userId, request.getFileName());
            
            return processedImageBytes;  // 바이너리 데이터 그대로 반환
            
        } catch (Exception e) {
            log.error("event=background_removal_failed, user_id={}, file_name={}, error_message={}",
                    userId, request.getFileName(), e.getMessage(), e);
            throw new PhotoException(ErrorMessage.PHOTO_BACKGROUND_REMOVAL_FAILED);
        }
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
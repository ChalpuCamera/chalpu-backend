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
        log.info("[PhotoService] generatePresignedUrl for user: {}, fileName: {}", username, request.getFileName());
        // ToDo: 사용자 권한 검증 로직 추가 (e.g., storeId를 받아서 해당 가게의 관리자인지 확인)
        try {
            String s3Key = createS3Key(request.getFileName());
            URL presignedUrl = createPresignedUrl(s3Key);

            return PhotoPresignedUrlResponse.builder()
                    .presignedUrl(presignedUrl.toString())
                    .s3Key(s3Key)
                    .build();
        } catch (Exception e) {
            log.error("[PhotoService] Failed to generate presigned URL for user: {}. Error: {}", username, e.getMessage());
            throw new PhotoException(ErrorMessage.PRESIGNED_URL_GENERATION_FAILED);
        }
    }

    @Transactional
    public PhotoResponse registerPhoto(final String username, final PhotoRegisterRequest request) {
        log.info("[PhotoService] registerPhoto for user: {}, s3Key: {}", username, request.getS3Key());
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

            return PhotoResponse.from(savedPhoto, cloudfrontDomain);
        } catch (Exception e) {
            log.error("[PhotoService] Failed to register photo for user: {}. s3Key: {}. Error: {}", username, request.getS3Key(), e.getMessage());
            throw new PhotoException(ErrorMessage.PHOTO_REGISTRATION_FAILED);
        }
    }

    public PageResponse<PhotoResponse> getPhotosByStore(final Long storeId, final Pageable pageable) {
        log.info("[PhotoService] getPhotosByStore for storeId: {}", storeId);
        try {
            Page<Photo> photoPage = photoRepository.findByStoreId(storeId, pageable);
            return PageResponse.from(photoPage.map(photo -> PhotoResponse.from(photo, cloudfrontDomain)));
        } catch (Exception e) {
            log.error("[PhotoService] Failed to get photos for storeId: {}. Error: {}", storeId, e.getMessage());
            throw new PhotoException(ErrorMessage.PHOTO_NOT_FOUND);
        }
    }
    
    public PhotoResponse getPhoto(final Long photoId) {
        log.info("[PhotoService] getPhoto for photoId: {}", photoId);
        try {
            Photo photo = findPhotoById(photoId);
            return PhotoResponse.from(photo, cloudfrontDomain);
        } catch (Exception e) {
            log.error("[PhotoService] Failed to get photo for photoId: {}. Error: {}", photoId, e.getMessage());
            throw new PhotoException(ErrorMessage.PHOTO_NOT_FOUND);
        }
    }

    @Transactional
    public void deletePhoto(final String username, final Long photoId) {
        log.info("[PhotoService] deletePhoto for user: {}, photoId: {}", username, photoId);
        try {
            Photo photo = findPhotoById(photoId);
            // ToDo: 권한 검증 로직 (username이 이 사진을 삭제할 권한이 있는지)

            deleteS3Object(photo.getS3Key());
            photo.setIsActive(false);
            log.info("[PhotoService] Successfully deleted photoId: {}", photoId);
        } catch (Exception e) {
            log.error("[PhotoService] Failed to delete photo for user: {}, photoId: {}. Error: {}", username, photoId, e.getMessage());
            throw new PhotoException(ErrorMessage.PHOTO_DELETE_FAILED);
        }
    }

    private Photo findPhotoById(final Long photoId) {
        return photoRepository.findById(photoId)
                .orElseThrow(() -> new PhotoException(ErrorMessage.PHOTO_NOT_FOUND));
    }

    private void deleteS3Object(final String s3Key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            log.info("[PhotoService] Successfully deleted S3 object with key: {}", s3Key);
        } catch (Exception e) {
            // S3에서 객체 삭제 실패 시 로깅만 하고 에러를 던지지는 않음 (DB 트랜잭션은 롤백되지 않도록)
            log.error("[PhotoService] Failed to delete S3 object with key: {}. Error: {}", s3Key, e.getMessage());
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
package com.example.chalpu.photo.service;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.PhotoException;
import com.example.chalpu.common.response.PageResponse;
import com.example.chalpu.photo.domain.Photo;
import com.example.chalpu.photo.dto.PhotoRegisterRequest;
import com.example.chalpu.photo.dto.PhotoResponse;
import com.example.chalpu.photo.repository.PhotoRepository;
import com.example.chalpu.store.domain.Store;
import com.example.chalpu.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PhotoService 테스트")
class PhotoServiceTest {

    @Mock
    private PhotoRepository photoRepository;
    
    @Mock
    private StoreRepository storeRepository;
    
    @Mock
    private S3Client s3Client;
    
    @Mock
    private S3Presigner s3Presigner;
    
    @InjectMocks
    private PhotoService photoService;
    
    private Store testStore;
    private Photo testPhoto;
    private PhotoRegisterRequest testRegisterRequest;
    
    @BeforeEach
    void setUp() {
        // 설정값 주입
        ReflectionTestUtils.setField(photoService, "bucket", "test-bucket");
        ReflectionTestUtils.setField(photoService, "cloudfrontDomain", "https://cdn.chalpu.com");
        
        // 테스트 데이터 생성
        testStore = Store.builder()
                .id(1L)
                .storeName("테스트 매장")
                .businessType("한식")
                .address("서울시 강남구")
                .phone("02-1234-5678")
                .businessRegistrationNumber("123-45-67890")
                .build();
        
        testPhoto = Photo.builder()
                .id(1L)
                .store(testStore)
                .s3Key("foodPhoto/test-image.jpg")
                .fileName("test-image.jpg")
                .fileSize(1024)
                .imageWidth(1920)
                .imageHeight(1080)
                .isActive(true)
                .isFeatured(false)
                .build();
        
        testRegisterRequest = PhotoRegisterRequest.builder()
                .s3Key("foodPhoto/test-image.jpg")
                .fileName("test-image.jpg")
                .storeId(1L)
                .fileSize(1024)
                .imageWidth(1920)
                .imageHeight(1080)
                .build();
    }
    
    @Test
    @DisplayName("사진 등록 성공")
    void registerPhoto_Success() {
        // given
        String username = "testuser";
        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(testStore));
        when(photoRepository.save(any(Photo.class))).thenReturn(testPhoto);
        
        // when
        PhotoResponse result = photoService.registerPhoto(username, testRegisterRequest);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStoreId()).isEqualTo(1L);
        assertThat(result.getFileName()).isEqualTo("test-image.jpg");
        assertThat(result.getFileSize()).isEqualTo(1024);
        assertThat(result.getImageWidth()).isEqualTo(1920);
        assertThat(result.getImageHeight()).isEqualTo(1080);
        assertThat(result.getIsFeatured()).isFalse();
        assertThat(result.getImageUrl()).isEqualTo("https://cdn.chalpu.com/foodPhoto/test-image.jpg");
        
        verify(storeRepository).findById(1L);
        verify(photoRepository).save(any(Photo.class));
    }
    
    @Test
    @DisplayName("사진 등록 실패 - 매장 없음")
    void registerPhoto_StoreNotFound_ThrowsException() {
        // given
        String username = "testuser";
        when(storeRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> photoService.registerPhoto(username, testRegisterRequest))
                .isInstanceOf(PhotoException.class)
                .hasMessageContaining(ErrorMessage.STORE_NOT_FOUND.getMessage());
        
        verify(storeRepository).findById(1L);
        verify(photoRepository, never()).save(any(Photo.class));
    }
    
    @Test
    @DisplayName("매장별 사진 목록 조회 성공")
    void getPhotosByStore_Success() {
        // given
        Long storeId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Photo> photos = List.of(testPhoto);
        Page<Photo> photoPage = new PageImpl<>(photos, pageable, 1);
        
        when(photoRepository.findByStoreId(storeId, pageable)).thenReturn(photoPage);
        
        // when
        PageResponse<PhotoResponse> result = photoService.getPhotosByStore(storeId, pageable);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.isHasNext()).isFalse();
        assertThat(result.isHasPrevious()).isFalse();
        
        PhotoResponse photoResponse = result.getContent().get(0);
        assertThat(photoResponse.getId()).isEqualTo(1L);
        assertThat(photoResponse.getStoreId()).isEqualTo(1L);
        assertThat(photoResponse.getFileName()).isEqualTo("test-image.jpg");
        
        verify(photoRepository).findByStoreId(storeId, pageable);
    }
    
    @Test
    @DisplayName("매장별 사진 목록 조회 - 빈 결과")
    void getPhotosByStore_EmptyResult() {
        // given
        Long storeId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Photo> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        
        when(photoRepository.findByStoreId(storeId, pageable)).thenReturn(emptyPage);
        
        // when
        PageResponse<PhotoResponse> result = photoService.getPhotosByStore(storeId, pageable);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(0);
        
        verify(photoRepository).findByStoreId(storeId, pageable);
    }
    
    @Test
    @DisplayName("사진 상세 조회 성공")
    void getPhoto_Success() {
        // given
        Long photoId = 1L;
        when(photoRepository.findById(photoId)).thenReturn(Optional.of(testPhoto));
        
        // when
        PhotoResponse result = photoService.getPhoto(photoId);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStoreId()).isEqualTo(1L);
        assertThat(result.getFileName()).isEqualTo("test-image.jpg");
        assertThat(result.getFileSize()).isEqualTo(1024);
        assertThat(result.getImageWidth()).isEqualTo(1920);
        assertThat(result.getImageHeight()).isEqualTo(1080);
        assertThat(result.getIsFeatured()).isFalse();
        assertThat(result.getImageUrl()).isEqualTo("https://cdn.chalpu.com/foodPhoto/test-image.jpg");
        
        verify(photoRepository).findById(photoId);
    }
    
    @Test
    @DisplayName("사진 상세 조회 실패 - 사진 없음")
    void getPhoto_PhotoNotFound_ThrowsException() {
        // given
        Long photoId = 1L;
        when(photoRepository.findById(photoId)).thenReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> photoService.getPhoto(photoId))
                .isInstanceOf(PhotoException.class)
                .hasMessageContaining(ErrorMessage.PHOTO_NOT_FOUND.getMessage());
        
        verify(photoRepository).findById(photoId);
    }
    
    @Test
    @DisplayName("사진 삭제 성공")
    void deletePhoto_Success() {
        // given
        String username = "testuser";
        Long photoId = 1L;
        when(photoRepository.findById(photoId)).thenReturn(Optional.of(testPhoto));
        
        // when
        photoService.deletePhoto(username, photoId);
        
        // then
        assertThat(testPhoto.getIsActive()).isFalse();
        
        verify(photoRepository).findById(photoId);
        // S3 관련 부분은 패스 (S3Client 호출 검증 생략)
    }
    
    @Test
    @DisplayName("사진 삭제 실패 - 사진 없음")
    void deletePhoto_PhotoNotFound_ThrowsException() {
        // given
        String username = "testuser";
        Long photoId = 1L;
        when(photoRepository.findById(photoId)).thenReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> photoService.deletePhoto(username, photoId))
                .isInstanceOf(PhotoException.class)
                .hasMessageContaining(ErrorMessage.PHOTO_NOT_FOUND.getMessage());
        
        verify(photoRepository).findById(photoId);
        // S3 관련 부분은 패스 (S3Client 호출 검증 생략)
    }
    
    @Test
    @DisplayName("사진 등록 시 FoodItem이 있는 경우")
    void registerPhoto_WithFoodItem_Success() {
        // given
        String username = "testuser";
        PhotoRegisterRequest requestWithFoodItem = PhotoRegisterRequest.builder()
                .s3Key("foodPhoto/test-image.jpg")
                .fileName("test-image.jpg")
                .storeId(1L)
                .foodItemId(10L)
                .fileSize(1024)
                .imageWidth(1920)
                .imageHeight(1080)
                .build();
        
        when(storeRepository.findById(anyLong())).thenReturn(Optional.of(testStore));
        when(photoRepository.save(any(Photo.class))).thenReturn(testPhoto);
        
        // when
        PhotoResponse result = photoService.registerPhoto(username, requestWithFoodItem);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStoreId()).isEqualTo(1L);
        
        verify(storeRepository).findById(1L);
        verify(photoRepository).save(any(Photo.class));
    }
    
    @Test
    @DisplayName("CloudFront 도메인이 null인 경우 URL 생성")
    void photoResponse_NullCloudfrontDomain_ReturnsNullUrl() {
        // given
        ReflectionTestUtils.setField(photoService, "cloudfrontDomain", null);
        when(photoRepository.findById(anyLong())).thenReturn(Optional.of(testPhoto));
        
        // when
        PhotoResponse result = photoService.getPhoto(1L);
        
        // then
        assertThat(result.getImageUrl()).isNull();
        
        verify(photoRepository).findById(1L);
    }
    
    @Test
    @DisplayName("CloudFront 도메인이 슬래시로 끝나는 경우 URL 생성")
    void photoResponse_CloudfrontDomainWithSlash_CreatesCorrectUrl() {
        // given
        ReflectionTestUtils.setField(photoService, "cloudfrontDomain", "https://cdn.chalpu.com/");
        when(photoRepository.findById(anyLong())).thenReturn(Optional.of(testPhoto));
        
        // when
        PhotoResponse result = photoService.getPhoto(1L);
        
        // then
        assertThat(result.getImageUrl()).isEqualTo("https://cdn.chalpu.com/foodPhoto/test-image.jpg");
        
        verify(photoRepository).findById(1L);
    }
} 
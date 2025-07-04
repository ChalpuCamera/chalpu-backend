package com.example.chalpu.guide.service;

import com.example.chalpu.common.response.PageResponse;
import com.example.chalpu.guide.domain.Guide;
import com.example.chalpu.guide.dto.GuidePresignedUrlResponse;
import com.example.chalpu.guide.dto.GuideRegisterRequest;
import com.example.chalpu.guide.dto.GuideResponse;
import com.example.chalpu.guide.dto.GuideUploadRequest;
import com.example.chalpu.guide.repository.GuideRepository;
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
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuideService {

    private final GuideRepository guideRepository;
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final String GUIDE_S3_KEY_PREFIX = "guides/";

    public GuideResponse getGuide(Long guideId) {
        log.info("[GuideService] 가이드 상세 조회 시작. guideId: {}", guideId);
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new RuntimeException("해당 가이드를 찾을 수 없습니다.")); // TODO: GuideException으로 교체
        log.info("[GuideService] 가이드 상세 조회 완료. guideId: {}", guideId);
        return GuideResponse.from(guide);
    }

    public PageResponse<GuideResponse> getAllGuides(Pageable pageable) {
        log.info("[GuideService] 가이드 전체 조회 시작. page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Guide> guides = guideRepository.findAll(pageable);
        Page<GuideResponse> guideResponses = guides.map(GuideResponse::from);
        log.info("[GuideService] 가이드 전체 조회 완료. totalElements: {}", guides.getTotalElements());
        return PageResponse.from(guideResponses);
    }

    public GuidePresignedUrlResponse generatePresignedUrl(GuideUploadRequest request) {
        log.info("[GuideService] Presigned URL 생성 시작. fileName: {}", request.getFileName());
        String s3Key = createS3Key();

        try {
            URL url = createPresignedUrl(s3Key);
            log.info("[GuideService] Presigned URL 생성 완료. s3Key: {}", s3Key);
            return new GuidePresignedUrlResponse(url.toString(), s3Key);
        } catch (Exception e) {
            log.error("[GuideService] Presigned URL 생성 실패.", e);
            // TODO: GuideException 추가 및 전용 에러 메시지 사용
            throw new RuntimeException("Presigned URL 생성에 실패했습니다.", e);
        }
    }

    @Transactional
    public GuideResponse registerGuide(GuideRegisterRequest request) {
        log.info("[GuideService] 가이드 등록 시작. s3Key: {}", request.getS3Key());
        // TODO: S3에 파일이 실제로 존재하는지 확인하는 로직 추가

        Guide guide = Guide.builder()
                .s3Key(request.getS3Key())
                .fileName(request.getFileName())
                .build();

        Guide savedGuide = guideRepository.save(guide);
        log.info("[GuideService] 가이드 등록 완료. guideId: {}", savedGuide.getId());
        return GuideResponse.from(savedGuide);
    }

    @Transactional
    public void deleteGuide(Long guideId) {
        log.info("[GuideService] 가이드 삭제 시작. guideId: {}", guideId);
        
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new RuntimeException("해당 가이드를 찾을 수 없습니다.")); // TODO: GuideException으로 교체

        deleteS3Object(guide.getS3Key());
        
        guideRepository.delete(guide);
        log.info("[GuideService] 가이드 삭제 완료. guideId: {}", guideId);
    }
    
    private void deleteS3Object(String s3Key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            log.info("[GuideService] S3 파일 삭제 완료. s3Key: {}", s3Key);
        } catch (Exception e) {
            log.error("[GuideService] S3 파일 삭제 실패. s3Key: {}", s3Key, e);
            throw new RuntimeException("S3 파일 삭제에 실패했습니다."); // TODO: 전용 예외 처리
        }
    }

    private String createS3Key() {
        return GUIDE_S3_KEY_PREFIX + UUID.randomUUID() + ".xml";
    }

    private URL createPresignedUrl(final String s3Key) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .contentType("application/xml")
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url();
    }
} 
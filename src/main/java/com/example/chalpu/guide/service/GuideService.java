package com.example.chalpu.guide.service;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.NoticeException;
import com.example.chalpu.common.exception.S3Exception;
import com.example.chalpu.common.response.PageResponse;
import com.example.chalpu.guide.domain.Guide;
import com.example.chalpu.guide.domain.SubCategory;
import com.example.chalpu.guide.dto.GuidePresignedUrlRequest;
import com.example.chalpu.guide.dto.GuidePresignedUrlsResponse;
import com.example.chalpu.guide.dto.GuideRegisterRequest;
import com.example.chalpu.guide.dto.GuideResponse;
import com.example.chalpu.guide.dto.GuideUpdateRequest;
import com.example.chalpu.guide.repository.GuideRepository;
import com.example.chalpu.guide.repository.SubCategoryRepository;
import com.example.chalpu.tag.domain.GuideTag;
import com.example.chalpu.tag.domain.Tag;
import com.example.chalpu.tag.repository.GuideTagRepository;
import com.example.chalpu.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GuideService {

    private final GuideRepository guideRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final TagRepository tagRepository;
    private final GuideTagRepository guideTagRepository;
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public GuidePresignedUrlsResponse getPresignedUrls(GuidePresignedUrlRequest request) {
        String uniqueId = UUID.randomUUID().toString();
        String guideS3Key = "guides/" + uniqueId + "-" + request.getFileName() + ".xml";
        String imageS3Key = "guides/images/" + uniqueId + "-" + request.getFileName() + ".png";

        String guideUploadUrl = createPresignedUrl(guideS3Key);
        String imageUploadUrl = createPresignedUrl(imageS3Key);

        return GuidePresignedUrlsResponse.builder()
                .guideS3Key(guideS3Key)
                .guideUploadUrl(guideUploadUrl)
                .imageS3Key(imageS3Key)
                .imageUploadUrl(imageUploadUrl)
                .build();
    }

    @Transactional
    public GuideResponse registerGuide(GuideRegisterRequest request) {
        SubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryId())
                .orElseThrow(() -> new NoticeException(ErrorMessage.SUB_CATEGORY_NOT_FOUND));
        log.info("subCategory: {}", subCategory);
        Guide guide = Guide.builder()
                .content(request.getContent())
                .guideS3Key(request.getGuideS3Key())
                .imageS3Key(request.getImageS3Key())
                .fileName(request.getFileName())
                .subCategory(subCategory)
                .build();
        Guide savedGuide = guideRepository.save(guide);
        log.info("savedGuide: {}", savedGuide);
        List<Tag> tags = findOrCreateTags(request.getTags());
        List<GuideTag> guideTags = tags.stream()
                .map(tag -> GuideTag.builder().guide(savedGuide).tag(tag).build())
                .collect(Collectors.toList());
        guideTagRepository.saveAll(guideTags);
        log.info("guideTags: {}", guideTags);
        return GuideResponse.from(savedGuide, guideTags);
    }

    @Transactional
    public GuideResponse updateGuide(Long guideId, GuideUpdateRequest request) {
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new NoticeException(ErrorMessage.GUIDE_NOT_FOUND));

        SubCategory subCategory = null;
        if (request.getSubCategoryId() != null) {
            subCategory = subCategoryRepository.findById(request.getSubCategoryId())
                    .orElseThrow(() -> new NoticeException(ErrorMessage.SUB_CATEGORY_NOT_FOUND));
        }

        guide.update(request.getContent(), request.getFileName(), subCategory);

        List<GuideTag> guideTags = guideTagRepository.findByGuide(guide);
        return GuideResponse.from(guide, guideTags);
    }

    public GuideResponse findById(Long guideId) {
        Guide guide = guideRepository.findByIdAndIsActiveTrue(guideId)
            .orElseThrow(() -> new NoticeException(ErrorMessage.GUIDE_NOT_FOUND));
        List<GuideTag> guideTags = guideTagRepository.findByGuide(guide);
        return GuideResponse.from(guide, guideTags);
    }

    public PageResponse<GuideResponse> findAll(Pageable pageable) {
        Page<Guide> guidesPage = guideRepository.findAllByIsActiveTrue(pageable);
        List<GuideResponse> guideResponses = guidesPage.getContent().stream()
                .map(guide -> GuideResponse.from(guide, guideTagRepository.findByGuide(guide)))
                .collect(Collectors.toList());
        return PageResponse.from(new PageImpl<>(guideResponses, pageable, guidesPage.getTotalElements()));
    }

    public PageResponse<GuideResponse> findAllBySubCategory(Long subCategoryId, Pageable pageable) {
        Page<Guide> guidesPage = guideRepository.findBySubCategoryIdAndIsActiveTrue(subCategoryId, pageable);
        List<GuideResponse> guideResponses = guidesPage.getContent().stream()
                .map(guide -> GuideResponse.from(guide, guideTagRepository.findByGuide(guide)))
                .collect(Collectors.toList());
        return PageResponse.from(new PageImpl<>(guideResponses, pageable, guidesPage.getTotalElements()));
    }

    @Transactional
    public void deleteGuides(List<Long> guideIds) {
        List<Guide> guides = guideRepository.findAllById(guideIds);
        if (guides.size() != guideIds.size()) {
            throw new NoticeException(ErrorMessage.GUIDE_NOT_FOUND);
        }

        List<String> s3KeysToDelete = guides.stream()
                .flatMap(guide -> java.util.stream.Stream.of(guide.getGuideS3Key(), guide.getImageS3Key()))
                .collect(Collectors.toList());
        deleteS3Objects(s3KeysToDelete);

        guides.forEach(Guide::softDelete);
        guideRepository.saveAll(guides);
    }

    private String createPresignedUrl(String s3Key) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }

    private void deleteS3Objects(List<String> s3Keys) {
        if (s3Keys == null || s3Keys.isEmpty()) {
            return;
        }
        try {
            List<ObjectIdentifier> toDelete = s3Keys.stream()
                    .map(key -> ObjectIdentifier.builder().key(key).build())
                    .collect(Collectors.toList());

            DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(Delete.builder().objects(toDelete).build())
                    .build();

            s3Client.deleteObjects(deleteObjectsRequest);
        } catch (Exception e) {
            throw new S3Exception(ErrorMessage.S3_DELETE_FAILED);
        }
    }

    private List<Tag> findOrCreateTags(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new ArrayList<>();
        }

        return tagNames.stream().map(tagName ->
                tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()))
        ).collect(Collectors.toList());
    }
} 
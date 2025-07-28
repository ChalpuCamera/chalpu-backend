package com.example.chalpu.tag.service;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.GuideTagException;
import com.example.chalpu.common.exception.NoticeException;
import com.example.chalpu.guide.domain.Guide;
import com.example.chalpu.guide.repository.GuideRepository;
import com.example.chalpu.tag.domain.GuideTag;
import com.example.chalpu.tag.domain.Tag;
import com.example.chalpu.tag.dto.TagResponse;
import com.example.chalpu.tag.repository.GuideTagRepository;
import com.example.chalpu.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuideTagService {

    private final GuideRepository guideRepository;
    private final TagRepository tagRepository;
    private final GuideTagRepository guideTagRepository;

    @Transactional
    public TagResponse addTagToGuide(Long guideId, String tagName) {
        // Guide 존재 여부만 확인 (엔티티 조회 없이)
        if (!guideRepository.existsById(guideId)) {
            throw new NoticeException(ErrorMessage.GUIDE_NOT_FOUND);
        }

        // Tag 조회 또는 생성
        Tag tag = tagRepository.findByNameAndIsActiveTrue(tagName)
                .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));

        // 기존 GuideTag 존재 여부 확인 (경량화된 쿼리 사용)
        Optional<GuideTag> existingGuideTag = guideTagRepository.findByGuideIdAndTagIdAndIsActiveTrueWithoutJoin(guideId, tag.getId());

        if (existingGuideTag.isPresent()) {
            GuideTag guideTag = existingGuideTag.get();
            if (guideTag.getIsActive()) {
                throw new GuideTagException(ErrorMessage.TAG_ALREADY_EXISTS);
            } else {
                guideTag.activate();
            }
        } else {
            // Guide 엔티티가 필요한 경우에만 조회
            Guide guide = guideRepository.findById(guideId)
                    .orElseThrow(() -> new NoticeException(ErrorMessage.GUIDE_NOT_FOUND));
            guideTagRepository.save(GuideTag.builder().guide(guide).tag(tag).build());
        }

        return TagResponse.from(tag);
    }

    @Transactional
    public void removeTagFromGuide(Long guideId, Long tagId) {
        GuideTag guideTag = guideTagRepository.findByGuideIdAndTagIdAndIsActiveTrue(guideId, tagId)
                .orElseThrow(() -> new GuideTagException(ErrorMessage.GUIDE_TAG_NOT_FOUND));
        guideTag.softDelete();
    }

    public List<TagResponse> getTagsForGuide(Long guideId) {
        if (!guideRepository.existsById(guideId)) {
            throw new NoticeException(ErrorMessage.GUIDE_NOT_FOUND);
        }
        List<GuideTag> guideTags = guideTagRepository.findByGuideIdAndIsActiveTrue(guideId);
        return guideTags.stream()
                .map(guideTag -> TagResponse.from(guideTag.getTag()))
                .collect(Collectors.toList());
    }
}

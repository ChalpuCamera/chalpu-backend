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
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new NoticeException(ErrorMessage.GUIDE_NOT_FOUND));

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
            guideTagRepository.save(GuideTag.builder().guide(guide).tag(tag).build());
        }
        guide.updateDate();
        return TagResponse.from(tag);
    }

    @Transactional
    public void removeTagFromGuide(Long guideId, Long tagId) {
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new NoticeException(ErrorMessage.GUIDE_NOT_FOUND));

        GuideTag guideTag = guideTagRepository.findByGuideIdAndTagIdAndIsActiveTrue(guideId, tagId)
                .orElseThrow(() -> new GuideTagException(ErrorMessage.GUIDE_TAG_NOT_FOUND));
        guideTag.softDelete();
        guide.updateDate();
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

    @Transactional
    public TagResponse updateTagForGuide(Long guideId, Long tagId, String tagName) {
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new NoticeException(ErrorMessage.GUIDE_NOT_FOUND));

        // 기존 GuideTag 조회
        GuideTag guideTag = guideTagRepository.findByGuideIdAndTagIdAndIsActiveTrue(guideId, tagId)
                .orElseThrow(() -> new GuideTagException(ErrorMessage.GUIDE_TAG_NOT_FOUND));

        // 태그명으로 기존 태그 찾기 또는 새 태그 생성
        Tag newTag = tagRepository.findByNameAndIsActiveTrue(tagName)
                .orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));

        // GuideTag의 태그를 새 태그로 변경
        guideTag.updateTag(newTag);
        guide.updateDate();
        return TagResponse.from(newTag);
    }
}

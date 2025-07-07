package com.example.chalpu.tag.repository;

import com.example.chalpu.guide.domain.Guide;
import com.example.chalpu.tag.domain.GuideTag;
import com.example.chalpu.tag.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GuideTagRepository extends JpaRepository<GuideTag, Long> {
    List<GuideTag> findByGuide(Guide guide);
    List<GuideTag> findByGuideIn(List<Guide> guides);
    Optional<GuideTag> findByGuideAndTag(Guide guide, Tag tag);
    Optional<GuideTag> findByGuideIdAndTagId(Long guideId, Long tagId);
    List<GuideTag> findByGuideId(Long guideId);
    Optional<GuideTag> findByGuideIdAndTagIdAndIsActiveTrue(Long guideId, Long tagId);
    List<GuideTag> findByGuideIdAndIsActiveTrue(Long guideId);
} 
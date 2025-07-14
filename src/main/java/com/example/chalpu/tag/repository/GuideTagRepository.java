package com.example.chalpu.tag.repository;

import com.example.chalpu.guide.domain.Guide;
import com.example.chalpu.tag.domain.GuideTag;
import com.example.chalpu.tag.domain.Tag;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GuideTagRepository extends JpaRepository<GuideTag, Long> {
    @EntityGraph("GuideTag.withGuideAndTag")
    List<GuideTag> findByGuide(Guide guide);
    @EntityGraph("GuideTag.withGuideAndTag")
    List<GuideTag> findByGuideIn(List<Guide> guides);
    @EntityGraph("GuideTag.withGuideAndTag")
    Optional<GuideTag> findByGuideAndTag(Guide guide, Tag tag);
    @EntityGraph("GuideTag.withGuideAndTag")
    Optional<GuideTag> findByGuideIdAndTagId(Long guideId, Long tagId);
    @EntityGraph("GuideTag.withGuideAndTag")
    List<GuideTag> findByGuideId(Long guideId);
    @EntityGraph("GuideTag.withGuideAndTag")
    Optional<GuideTag> findByGuideIdAndTagIdAndIsActiveTrue(Long guideId, Long tagId);
    @EntityGraph("GuideTag.withGuideAndTag")
    List<GuideTag> findByGuideIdAndIsActiveTrue(Long guideId);

    // 최적화된 메서드들 - 연관 엔티티 조회 없이 ID만으로 처리
    @Query("SELECT gt FROM GuideTag gt WHERE gt.guide.id = :guideId AND gt.tag.id = :tagId")
    Optional<GuideTag> findByGuideIdAndTagIdWithoutJoin(@Param("guideId") Long guideId, @Param("tagId") Long tagId);
    
    @Query("SELECT CASE WHEN COUNT(gt) > 0 THEN true ELSE false END FROM GuideTag gt WHERE gt.guide.id = :guideId AND gt.tag.id = :tagId AND gt.isActive = true")
    boolean existsByGuideIdAndTagIdAndIsActiveTrue(@Param("guideId") Long guideId, @Param("tagId") Long tagId);
} 
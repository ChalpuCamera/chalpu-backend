package com.example.chalpu.guide.repository;

import com.example.chalpu.guide.domain.Guide;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GuideRepository extends JpaRepository<Guide, Long> {
    @EntityGraph("Guide.withSubCategoryAndCategory")
    Optional<Guide> findByIdAndIsActiveTrue(Long guideId);

    @EntityGraph("Guide.withSubCategoryAndCategory")
    Page<Guide> findAllByIsActiveTrue(Pageable pageable);

    @EntityGraph("Guide.withSubCategoryAndCategory")
    Page<Guide> findBySubCategoryIdAndIsActiveTrue(Long subCategoryId, Pageable pageable);

    // 경량화된 조회 메서드 (연관 엔티티 조회 없음)
    @Query("SELECT g FROM Guide g WHERE g.id = :guideId AND g.isActive = true")
    Optional<Guide> findByIdAndIsActiveTrueWithoutJoin(@Param("guideId") Long guideId);
} 
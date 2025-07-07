package com.example.chalpu.guide.repository;

import com.example.chalpu.guide.domain.Guide;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuideRepository extends JpaRepository<Guide, Long> {
    Optional<Guide> findByIdAndIsActiveTrue(Long guideId);

    Page<Guide> findAllByIsActiveTrue(Pageable pageable);

    Page<Guide> findBySubCategoryIdAndIsActiveTrue(Long subCategoryId, Pageable pageable);
} 
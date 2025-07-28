package com.example.chalpu.guide.repository;

import com.example.chalpu.guide.domain.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    @EntityGraph("SubCategory.withCategory")
    Optional<SubCategory> findById(Long id);
} 
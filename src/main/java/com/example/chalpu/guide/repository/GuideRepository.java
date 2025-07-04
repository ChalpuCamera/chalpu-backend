package com.example.chalpu.guide.repository;

import com.example.chalpu.guide.domain.Guide;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideRepository extends JpaRepository<Guide, Long> {
} 
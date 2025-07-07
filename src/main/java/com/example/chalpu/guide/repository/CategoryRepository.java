package com.example.chalpu.guide.repository;

import com.example.chalpu.guide.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
} 
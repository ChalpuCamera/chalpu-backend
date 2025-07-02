package com.example.chalpu.photo.repository;

import com.example.chalpu.photo.domain.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    
    List<Photo> findByFoodItemId(Long foodId);
    
    Page<Photo> findByFoodItemId(Long foodId, Pageable pageable);
} 
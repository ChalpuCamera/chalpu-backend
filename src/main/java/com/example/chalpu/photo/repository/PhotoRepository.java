package com.example.chalpu.photo.repository;

import com.example.chalpu.photo.domain.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    @EntityGraph(value = "Photo.withAll")
    List<Photo> findByFoodItemId(Long foodId);

    @EntityGraph(value = "Photo.withAll")
    Page<Photo> findByFoodItemId(Long foodId, Pageable pageable);

    @EntityGraph(value = "Photo.withAll")
    Page<Photo> findByStoreId(Long storeId, Pageable pageable);
    
    @Query("SELECT p FROM Photo p WHERE p.store.id = :storeId")
    Page<Photo> findByStoreIdWithJoin(@Param("storeId") Long storeId, Pageable pageable);

    @Query("SELECT p FROM Photo p WHERE p.foodItem.id = :foodId")
    Page<Photo> findByFoodItemIdWithoutJoin(@Param("foodId") Long foodId, Pageable pageable);

    // 경량화된 조회 메서드 (연관 엔티티 조회 없음)
    @Query("SELECT p FROM Photo p WHERE p.id = :id AND p.isActive = true")
    Optional<Photo> findByIdAndIsActiveTrueWithoutJoin(@Param("id") Long id);
} 
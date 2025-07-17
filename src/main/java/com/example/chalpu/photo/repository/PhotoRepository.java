package com.example.chalpu.photo.repository;

import com.example.chalpu.photo.domain.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    @EntityGraph(value = "Photo.withAll")
    List<Photo> findByFoodItemIdAndIsActiveTrue(Long foodId);

    @Query("SELECT p FROM Photo p WHERE p.store.id = :storeId AND p.isActive = true") 
    List<Photo> findByStoreIdAndIsActiveTrueWithoutJoin(@Param("storeId") Long storeId);

    @EntityGraph(value = "Photo.withAll")
    Page<Photo> findByFoodItemIdAndIsActiveTrue(Long foodId, Pageable pageable);

    @EntityGraph(value = "Photo.withAll")
    Page<Photo> findByStoreIdAndIsActiveTrue(Long storeId, Pageable pageable);
    
    @Query("SELECT p FROM Photo p WHERE p.store.id = :storeId AND p.isActive = true")
    Page<Photo> findByStoreIdAndIsActiveTrueWithoutJoin(@Param("storeId") Long storeId, Pageable pageable);

    @Query("SELECT p FROM Photo p WHERE p.foodItem.id = :foodId AND p.isActive = true")
    Page<Photo> findByFoodItemIdAndIsActiveTrueWithoutJoin(@Param("foodId") Long foodId, Pageable pageable);

    Optional<Photo> findByIdAndIsActiveTrue(Long id);

    // 경량화된 조회 메서드 (연관 엔티티 조회 없음)
    @Query("SELECT p FROM Photo p WHERE p.id = :id AND p.isActive = true")
    Optional<Photo> findByIdAndIsActiveTrueWithoutJoin(@Param("id") Long id);

    /**
     * FoodItem 삭제 시 연관된 Photo들 소프트 딜리트
     * @param foodItemId 음식 아이템 ID
     */
    @Modifying
    @Query("UPDATE Photo p SET p.isActive = false WHERE p.foodItem.id = :foodItemId")
    void softDeleteByFoodItemId(@Param("foodItemId") Long foodItemId);

    /**
     * User 삭제 시 연관된 Photo들 소프트 딜리트
     * @param userId 사용자 ID
     */
    @Modifying
    @Query("UPDATE Photo p SET p.isActive = false WHERE p.user.id = :userId")
    void softDeleteByUserId(@Param("userId") Long userId);
} 
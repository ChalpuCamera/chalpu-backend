package com.example.chalpu.fooditem.repository;

import com.example.chalpu.fooditem.domain.FoodItem;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
 
@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    Page<FoodItem> findByStoreId(Long storeId, Pageable pageable);
    
    // 활성 음식만 조회
    Page<FoodItem> findByStoreIdAndIsActiveTrue(Long storeId, Pageable pageable);
    
    // 매장별 음식명 검색
    Page<FoodItem> findByStoreIdAndFoodNameContaining(Long storeId, String foodName, Pageable pageable);
    
    // 활성 음식만 검색
    Page<FoodItem> findByStoreIdAndIsActiveTrueAndFoodNameContaining(Long storeId, String foodName, Pageable pageable);
    
    // 활성 음식만 단건 조회
    Optional<FoodItem> findByIdAndIsActiveTrue(Long id);
} 
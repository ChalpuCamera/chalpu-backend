package com.example.chalpu.fooditem.repository;

import com.example.chalpu.fooditem.domain.FoodItem;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
 
@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    Page<FoodItem> findByStoreId(Long storeId, Pageable pageable);
    
    // 매장별 음식명 검색
    Page<FoodItem> findByStoreIdAndFoodNameContaining(Long storeId, String foodName, Pageable pageable);
} 
package com.example.chalpu.fooditem.repository;

import com.example.chalpu.fooditem.domain.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
} 
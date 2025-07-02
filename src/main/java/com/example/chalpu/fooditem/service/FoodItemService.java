package com.example.chalpu.fooditem.service;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.FoodException;
import com.example.chalpu.fooditem.domain.FoodItem;
import com.example.chalpu.fooditem.repository.FoodItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;

    public FoodItem getFoodItem(Long foodId) {
        return foodItemRepository.findById(foodId)
                .orElseThrow(() -> new FoodException(ErrorMessage.FOOD_NOT_FOUND));
    }
} 
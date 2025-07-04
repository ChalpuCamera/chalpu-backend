package com.example.chalpu.fooditem.service;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.FoodException;
import com.example.chalpu.common.response.PageResponse;
import com.example.chalpu.fooditem.domain.FoodItem;
import com.example.chalpu.fooditem.dto.FoodItemRequest;
import com.example.chalpu.fooditem.dto.FoodItemResponse;
import com.example.chalpu.fooditem.repository.FoodItemRepository;
import com.example.chalpu.store.domain.Store;
import com.example.chalpu.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;
    private final StoreRepository storeRepository;

    /**
     * 매장별 음식 아이템 목록 조회
     */
    public PageResponse<FoodItemResponse> getFoodItems(Long storeId, Pageable pageable) {
        log.info("getFoodItems 시작 - storeId: {}, page: {}, size: {}", 
                storeId, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<FoodItem> foodItemPage = foodItemRepository.findByStoreId(storeId, pageable);
            Page<FoodItemResponse> foodResponsePage = foodItemPage.map(FoodItemResponse::from);
            
            log.info("getFoodItems 성공 - storeId: {}, totalElements: {}", 
                    storeId, foodItemPage.getTotalElements());
            
            return PageResponse.from(foodResponsePage);
        } catch (Exception e) {
            log.error("getFoodItems 실패 - storeId: {}, error: {}", storeId, e.getMessage(), e);
            throw new FoodException(ErrorMessage.FOOD_NOT_FOUND);
        }
    }

    /**
     * 음식 아이템 단건 조회
     */
    public FoodItemResponse getFoodItem(Long foodId) {
        log.info("getFoodItem 시작 - foodId: {}", foodId);
        
        try {
            FoodItem foodItem = findFoodItemById(foodId);
            
            log.info("getFoodItem 성공 - foodId: {}, foodName: {}", 
                    foodId, foodItem.getFoodName());
            
            return FoodItemResponse.from(foodItem);
        } catch (Exception e) {
            log.error("getFoodItem 실패 - foodId: {}, error: {}", foodId, e.getMessage(), e);
            throw new FoodException(ErrorMessage.FOOD_NOT_FOUND);
        }
    }

    /**
     * 음식 아이템 생성
     */
    @Transactional
    public FoodItemResponse createFoodItem(Long storeId, FoodItemRequest foodItemRequest, Long userId) {
        log.info("createFoodItem 시작 - storeId: {}, foodName: {}, userId: {}", 
                storeId, foodItemRequest.getFoodName(), userId);
        
        try {
            // TODO: 권한 검증 로직 추가
            Store store = findStoreById(storeId);
            
            FoodItem foodItem = createFoodItemEntity(store, foodItemRequest);
            FoodItem savedFoodItem = foodItemRepository.save(foodItem);
            
            log.info("createFoodItem 성공 - foodId: {}, foodName: {}", 
                    savedFoodItem.getId(), savedFoodItem.getFoodName());
            
            return FoodItemResponse.from(savedFoodItem);
        } catch (Exception e) {
            log.error("createFoodItem 실패 - storeId: {}, foodName: {}, error: {}", 
                    storeId, foodItemRequest.getFoodName(), e.getMessage(), e);
            throw new FoodException(ErrorMessage.FOOD_CREATE_FAILED);
        }
    }

    /**
     * 음식 아이템 수정
     */
    @Transactional
    public FoodItemResponse updateFoodItem(Long foodId, FoodItemRequest foodItemRequest, Long userId) {
        log.info("updateFoodItem 시작 - foodId: {}, foodName: {}, userId: {}", 
                foodId, foodItemRequest.getFoodName(), userId);
        
        try {
            // TODO: 권한 검증 로직 추가
            FoodItem foodItem = findFoodItemById(foodId);
            
            // 음식 아이템 정보 업데이트
            // TODO: FoodItem 엔티티에 updateFoodItem 메서드 추가 필요
            
            log.info("updateFoodItem 성공 - foodId: {}, foodName: {}", 
                    foodId, foodItem.getFoodName());
            
            return FoodItemResponse.from(foodItem);
        } catch (Exception e) {
            log.error("updateFoodItem 실패 - foodId: {}, error: {}", foodId, e.getMessage(), e);
            throw new FoodException(ErrorMessage.FOOD_UPDATE_FAILED);
        }
    }

    /**
     * 음식 아이템 삭제
     */
    @Transactional
    public void deleteFoodItem(Long foodId, Long userId) {
        log.info("deleteFoodItem 시작 - foodId: {}, userId: {}", foodId, userId);
        
        try {
            // TODO: 권한 검증 로직 추가
            FoodItem foodItem = findFoodItemById(foodId);
            
            // TODO: 관련 데이터 정리 (메뉴 아이템, 사진 등)
            
            foodItemRepository.delete(foodItem);
            
            log.info("deleteFoodItem 성공 - foodId: {}", foodId);
        } catch (Exception e) {
            log.error("deleteFoodItem 실패 - foodId: {}, error: {}", foodId, e.getMessage(), e);
            throw new FoodException(ErrorMessage.FOOD_UPDATE_FAILED);
        }
    }

    /**
     * 음식 아이템 검색
     */
    public PageResponse<FoodItemResponse> searchFoodItems(Long storeId, String keyword, Pageable pageable) {
        log.info("searchFoodItems 시작 - storeId: {}, keyword: {}, page: {}, size: {}", 
                storeId, keyword, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<FoodItem> foodItemPage = foodItemRepository.findByStoreIdAndFoodNameContaining(
                    storeId, keyword, pageable);
            Page<FoodItemResponse> foodResponsePage = foodItemPage.map(FoodItemResponse::from);
            
            log.info("searchFoodItems 성공 - storeId: {}, keyword: {}, totalElements: {}", 
                    storeId, keyword, foodItemPage.getTotalElements());
            
            return PageResponse.from(foodResponsePage);
        } catch (Exception e) {
            log.error("searchFoodItems 실패 - storeId: {}, keyword: {}, error: {}", 
                    storeId, keyword, e.getMessage(), e);
            throw new FoodException(ErrorMessage.FOOD_NOT_FOUND);
        }
    }

    // === 내부 유틸리티 메서드들 ===

    /**
     * 음식 아이템 ID로 조회
     */
    private FoodItem findFoodItemById(Long foodId) {
        return foodItemRepository.findById(foodId)
                .orElseThrow(() -> new FoodException(ErrorMessage.FOOD_NOT_FOUND));
    }

    /**
     * 매장 ID로 조회
     */
    private Store findStoreById(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new FoodException(ErrorMessage.STORE_NOT_FOUND));
    }

    /**
     * 음식 아이템 엔티티 생성
     */
    private FoodItem createFoodItemEntity(Store store, FoodItemRequest foodItemRequest) {
        return FoodItem.builder()
                .store(store)
                .foodName(foodItemRequest.getFoodName())
                .description(foodItemRequest.getDescription())
                .ingredients(foodItemRequest.getIngredients())
                .cookingMethod(foodItemRequest.getCookingMethod())
                .price(foodItemRequest.getPrice())
                .isActive(foodItemRequest.getIsActive())
                .build();
    }
} 
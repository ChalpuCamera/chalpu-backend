package com.example.chalpu.fooditem.service;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.FoodException;
import com.example.chalpu.common.response.PageResponse;
import com.example.chalpu.fooditem.domain.FoodItem;
import com.example.chalpu.fooditem.dto.FoodItemRequest;
import com.example.chalpu.fooditem.dto.FoodItemResponse;
import com.example.chalpu.fooditem.repository.FoodItemRepository;
import com.example.chalpu.menu.domain.MenuItem;
import com.example.chalpu.menu.repository.MenuItemRepository;
import com.example.chalpu.photo.repository.PhotoRepository;
import com.example.chalpu.store.domain.Store;
import com.example.chalpu.store.repository.StoreRepository;
import com.example.chalpu.store.service.UserStoreRoleService;
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
    private final UserStoreRoleService userStoreRoleService;
    private final MenuItemRepository menuItemRepository;
    private final PhotoRepository photoRepository;

    /**
     * 매장별 음식 아이템 목록 조회 (활성 음식만)
     */
    public PageResponse<FoodItemResponse> getFoodItems(Long storeId, Pageable pageable) {
        try {
            Page<FoodItem> foodItemPage = foodItemRepository.findByStoreIdAndIsActiveTrueWithoutJoin(storeId, pageable);
            Page<FoodItemResponse> foodResponsePage = foodItemPage.map(FoodItemResponse::from);
            return PageResponse.from(foodResponsePage);
        } catch (Exception e) {
            log.error("Failed to retrieve food items: storeId={}", storeId, e);
            throw new FoodException(ErrorMessage.FOOD_NOT_FOUND);
        }
    }

    /**
     * 음식 아이템 단건 조회
     */
    public FoodItemResponse getFoodItem(Long foodId) {
        try {
            FoodItem foodItem = findFoodItemById(foodId);
            return FoodItemResponse.from(foodItem);
        } catch (Exception e) {
            log.error("Food item not found: id={}", foodId, e);
            throw new FoodException(ErrorMessage.FOOD_NOT_FOUND);
        }
    }

    /**
     * 음식 아이템 생성
     */
    @Transactional
    public FoodItemResponse createFoodItem(Long storeId, FoodItemRequest foodItemRequest, Long userId) {
        try {
            validateUserStoreAccess(userId, storeId);
            Store store = findStoreById(storeId);
            
            FoodItem foodItem = FoodItem.createFoodItem(store, foodItemRequest);
            FoodItem savedFoodItem = foodItemRepository.save(foodItem);
            
            log.info("event=food_item_created, food_item_id={}, store_id={}, user_id={}", 
                    savedFoodItem.getId(), storeId, userId);
            
            return FoodItemResponse.from(savedFoodItem);
        } catch (Exception e) {
            log.error("event=food_item_creation_failed, store_id={}, user_id={}, error_message={}", 
                    storeId, userId, e.getMessage(), e);
            throw new FoodException(ErrorMessage.FOOD_CREATE_FAILED);
        }
    }

    /**
     * 음식 아이템 수정
     */
    @Transactional
    public FoodItemResponse updateFoodItem(Long foodItemId, FoodItemRequest request, Long userId) {
        // 권한 검증 - storeId만 조회하여 성능 최적화
        Long storeId = foodItemRepository.findStoreIdByFoodItemId(foodItemId)
                .orElseThrow(() -> new FoodException(ErrorMessage.FOOD_NOT_FOUND));
        
        if (!userStoreRoleService.canUserAccessStore(userId, storeId)) {
            throw new FoodException(ErrorMessage.STORE_ACCESS_DENIED);
        }

        // 실제 업데이트용 - Store 정보 없이 경량화된 조회
        FoodItem foodItem = foodItemRepository.findByIdAndIsActiveTrueWithoutJoin(foodItemId)
                .orElseThrow(() -> new FoodException(ErrorMessage.FOOD_NOT_FOUND));

        foodItem.updateFoodItem(request);
        FoodItem savedFoodItem = foodItemRepository.save(foodItem);

        log.info("event=food_item_updated, food_item_id={}, store_id={}", foodItemId, storeId);
        return FoodItemResponse.from(savedFoodItem);
    }

    /**
     * 음식 아이템 삭제 (소프트 딜리트)
     */
    @Transactional
    public void deleteFoodItem(Long foodItemId, Long userId) {
        // 권한 검증 - storeId만 조회하여 성능 최적화  
        Long storeId = foodItemRepository.findStoreIdByFoodItemId(foodItemId)
                .orElseThrow(() -> new FoodException(ErrorMessage.FOOD_NOT_FOUND));
        
        if (!userStoreRoleService.canUserAccessStore(userId, storeId)) {
            throw new FoodException(ErrorMessage.STORE_ACCESS_DENIED);
        }

        // 1. 연관된 Photo들 소프트 딜리트
        photoRepository.softDeleteByFoodItemId(foodItemId);

        // 2. 연관된 MenuItem들 소프트 딜리트
        List<MenuItem> menuItems = menuItemRepository.findByFoodItemIdAndIsActiveTrue(foodItemId);
        menuItems.forEach(MenuItem::softDelete);

        // 3. FoodItem 자체 소프트 딜리트
        FoodItem foodItem = foodItemRepository.findByIdAndIsActiveTrueWithoutJoin(foodItemId)
                .orElseThrow(() -> new FoodException(ErrorMessage.FOOD_NOT_FOUND));
        foodItem.softDelete();

        foodItemRepository.save(foodItem);

        log.info("event=food_item_deleted, food_item_id={}, store_id={}", foodItemId, storeId);
    }

    /**
     * 음식 아이템 검색 (활성 음식만)
     */
    public PageResponse<FoodItemResponse> searchFoodItems(Long storeId, String keyword, Pageable pageable) {
        try {
            Page<FoodItem> foodItemPage = foodItemRepository.findByStoreIdAndIsActiveTrueAndFoodNameContainingWithoutJoin(
                    storeId, keyword, pageable);
            Page<FoodItemResponse> foodResponsePage = foodItemPage.map(FoodItemResponse::from);
            return PageResponse.from(foodResponsePage);
        } catch (Exception e) {
            log.error("Food search error: storeId={}, keyword={}", storeId, keyword, e);
            throw new FoodException(ErrorMessage.FOOD_NOT_FOUND);
        }
    }


    /**
     * 음식 아이템 ID로 조회 (활성 음식만)
     */
    private FoodItem findFoodItemById(Long foodId) {
        return foodItemRepository.findByIdAndIsActiveTrueWithoutJoin(foodId)
                .orElseThrow(() -> new FoodException(ErrorMessage.FOOD_NOT_FOUND));
    }

    /**
     * 음식 아이템 ID로 조회 (연관 엔티티 없이, 권한 검증용)
     */
    private FoodItem findFoodItemByIdForValidation(Long foodId) {
        return foodItemRepository.findByIdAndIsActiveTrueWithoutJoin(foodId)
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
     * 사용자 매장 접근 권한 검증
     */
    private void validateUserStoreAccess(Long userId, Long storeId) {
        if (!userStoreRoleService.canUserAccessStore(userId, storeId)) {
            throw new FoodException(ErrorMessage.STORE_ACCESS_DENIED);
        }
    }
} 
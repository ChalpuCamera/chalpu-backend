package com.example.chalpu.menu.service;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.FoodException;
import com.example.chalpu.common.exception.MenuException;
import com.example.chalpu.common.response.PageResponse;
import com.example.chalpu.fooditem.domain.FoodItem;
import com.example.chalpu.fooditem.repository.FoodItemRepository;
import com.example.chalpu.menu.domain.Menu;
import com.example.chalpu.menu.domain.MenuItem;
import com.example.chalpu.menu.dto.MenuItemOrderUpdateRequest;
import com.example.chalpu.menu.dto.MenuItemRequest;
import com.example.chalpu.menu.dto.MenuItemResponse;
import com.example.chalpu.menu.repository.MenuItemRepository;
import com.example.chalpu.menu.repository.MenuRepository;
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
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuRepository menuRepository;
    private final FoodItemRepository foodItemRepository;
    private final UserStoreRoleService userStoreRoleService;

    /**
     * 메뉴 아이템 추가
     */
    @Transactional
    public MenuItemResponse addMenuItem(Long menuId, MenuItemRequest menuItemRequest, Long userId) {
        try {
            Menu menu = findActiveMenuById(menuId);
            validateUserStoreManagement(userId, menu.getStore().getId());

            FoodItem foodItem = findActiveFoodItemById(menuItemRequest.getFoodId());

            MenuItem menuItem = MenuItem.createMenuItem(menu, foodItem, menuItemRequest);
            MenuItem savedMenuItem = menuItemRepository.save(menuItem);

            log.info("event=menu_item_added, menu_item_id={}, menu_id={}, food_id={}, user_id={}",
                    savedMenuItem.getId(), menuId, foodItem.getId(), userId);

            return MenuItemResponse.from(savedMenuItem);
        } catch (Exception e) {
            log.error("event=menu_item_add_failed, menu_id={}, food_id={}, user_id={}, error_message={}",
                    menuId, menuItemRequest.getFoodId(), userId, e.getMessage(), e);
            throw new MenuException(ErrorMessage.MENU_ITEM_CREATE_FAILED);
        }
    }

    /**
     * 메뉴 아이템 표시 순서 수정
     */
    @Transactional
    public MenuItemResponse updateMenuItemOrder(Long menuItemId, MenuItemOrderUpdateRequest request, Long userId) {
        try {
            MenuItem menuItem = findActiveMenuItemById(menuItemId);
            validateUserStoreManagement(userId, menuItem.getMenu().getStore().getId());

            menuItem.updateDisplayOrder(request.getDisplayOrder());

            log.info("event=menu_item_order_updated, menu_item_id={}, new_order={}, user_id={}",
                    menuItemId, request.getDisplayOrder(), userId);

            return MenuItemResponse.from(menuItem);
        } catch (Exception e) {
            log.error("event=menu_item_order_update_failed, menu_item_id={}, user_id={}, error_message={}",
                    menuItemId, userId, e.getMessage(), e);
            throw new MenuException(ErrorMessage.MENU_ITEM_UPDATE_FAILED);
        }
    }

    /**
     * 메뉴 아이템 목록 조회
     */
    public PageResponse<MenuItemResponse> getMenuItems(Long menuId, Long userId, Pageable pageable) {
        try {
            Menu menu = findActiveMenuById(menuId);
            validateUserStoreAccess(userId, menu.getStore().getId());

            Page<MenuItem> menuItemPage = menuItemRepository.findByMenuIdAndIsActiveTrue(menuId, pageable);
            Page<MenuItemResponse> menuItemResponsePage = menuItemPage.map(MenuItemResponse::from);
            return PageResponse.from(menuItemResponsePage);
        } catch (Exception e) {
            log.error("event=menu_items_get_failed, menu_id={}, user_id={}, error_message={}",
                    menuId, userId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 메뉴 아이템 삭제 (소프트 딜리트)
     */
    @Transactional
    public void removeMenuItem(Long menuId, Long menuItemId, Long userId) {
        try {
            MenuItem menuItem = findActiveMenuItemById(menuItemId);
            validateUserStoreManagement(userId, menuItem.getMenu().getStore().getId());

            if (!menuItem.getMenu().getId().equals(menuId)) {
                throw new MenuException(ErrorMessage.MENU_ITEM_NOT_IN_MENU);
            }

            menuItem.softDelete();

            log.info("event=menu_item_removed, menu_item_id={}, menu_id={}, user_id={}", 
                    menuItemId, menuId, userId);
        } catch (Exception e) {
            log.error("event=menu_item_remove_failed, menu_item_id={}, user_id={}, error_message={}",
                    menuItemId, userId, e.getMessage(), e);
            throw new MenuException(ErrorMessage.MENU_ITEM_DELETE_FAILED);
        }
    }

    /**
     * 메뉴에 속한 모든 메뉴 아이템 비활성화 (소프트 딜리트)
     */
    @Transactional
    public void softDeleteMenuItemsByMenu(Menu menu) {
        List<MenuItem> menuItems = menuItemRepository.findByMenuAndIsActiveTrue(menu);
        menuItems.forEach(MenuItem::softDelete);
        log.info("event=menu_items_deleted_by_menu, menu_id={}, count={}", 
                menu.getId(), menuItems.size());
    }

    private Menu findActiveMenuById(Long menuId) {
        return menuRepository.findByIdAndIsActiveTrue(menuId)
                .orElseThrow(() -> new MenuException(ErrorMessage.MENU_NOT_FOUND));
    }

    private FoodItem findActiveFoodItemById(Long foodId) {
        return foodItemRepository.findByIdAndIsActiveTrueWithoutJoin(foodId)
                .orElseThrow(() -> new FoodException(ErrorMessage.FOOD_NOT_FOUND));
    }

    private MenuItem findActiveMenuItemById(Long menuItemId) {
        return menuItemRepository.findByIdAndIsActiveTrue(menuItemId)
                .orElseThrow(() -> new MenuException(ErrorMessage.MENU_ITEM_NOT_FOUND));
    }

    private void validateUserStoreAccess(Long userId, Long storeId) {
        if (!userStoreRoleService.canUserAccessStore(userId, storeId)) {
            throw new MenuException(ErrorMessage.STORE_ACCESS_DENIED);
        }
    }

    private void validateUserStoreManagement(Long userId, Long storeId) {
        if (!userStoreRoleService.canUserManageStore(userId, storeId)) {
            throw new MenuException(ErrorMessage.STORE_ACCESS_DENIED);
        }
    }
}
package com.example.chalpu.menu.service;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.MenuException;
import com.example.chalpu.common.exception.FoodException;
import com.example.chalpu.common.response.PageResponse;
import com.example.chalpu.menu.domain.Menu;
import com.example.chalpu.menu.domain.MenuItem;
import com.example.chalpu.menu.dto.MenuRequest;
import com.example.chalpu.menu.dto.MenuResponse;
import com.example.chalpu.menu.dto.MenuItemRequest;
import com.example.chalpu.menu.dto.MenuItemResponse;
import com.example.chalpu.menu.repository.MenuRepository;
import com.example.chalpu.menu.repository.MenuItemRepository;
import com.example.chalpu.store.domain.Store;
import com.example.chalpu.store.repository.StoreRepository;
import com.example.chalpu.fooditem.domain.FoodItem;
import com.example.chalpu.fooditem.repository.FoodItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;
    private final StoreRepository storeRepository;
    private final FoodItemRepository foodItemRepository;

    /**
     * 매장별 메뉴 목록 조회
     */
    public PageResponse<MenuResponse> getMenus(Long storeId, Pageable pageable) {
        log.info("getMenus 시작 - storeId: {}, page: {}, size: {}", 
                storeId, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<Menu> menuPage = menuRepository.findByStoreId(storeId, pageable);
            Page<MenuResponse> menuResponsePage = menuPage.map(MenuResponse::from);
            
            log.info("getMenus 성공 - storeId: {}, totalElements: {}", 
                    storeId, menuPage.getTotalElements());
            
            return PageResponse.from(menuResponsePage);
        } catch (Exception e) {
            log.error("getMenus 실패 - storeId: {}, error: {}", storeId, e.getMessage(), e);
            throw new MenuException(ErrorMessage.MENU_NOT_FOUND);
        }
    }

    /**
     * 메뉴 생성
     */
    @Transactional
    public MenuResponse createMenu(Long storeId, MenuRequest menuRequest, Long userId) {
        log.info("createMenu 시작 - storeId: {}, menuName: {}, userId: {}", 
                storeId, menuRequest.getMenuName(), userId);
        
        try {
            // TODO: 권한 검증 로직 추가
            Store store = findStoreById(storeId);
            
            Menu menu = createMenuEntity(store, menuRequest);
            Menu savedMenu = menuRepository.save(menu);
            
            log.info("createMenu 성공 - menuId: {}, menuName: {}", 
                    savedMenu.getId(), savedMenu.getMenuName());
            
            return MenuResponse.from(savedMenu);
        } catch (Exception e) {
            log.error("createMenu 실패 - storeId: {}, menuName: {}, error: {}", 
                    storeId, menuRequest.getMenuName(), e.getMessage(), e);
            throw new MenuException(ErrorMessage.MENU_CREATE_FAILED);
        }
    }

    /**
     * 메뉴 수정
     */
    @Transactional
    public MenuResponse updateMenu(Long menuId, MenuRequest menuRequest, Long userId) {
        log.info("updateMenu 시작 - menuId: {}, menuName: {}, userId: {}", 
                menuId, menuRequest.getMenuName(), userId);
        
        try {
            // TODO: 권한 검증 로직 추가
            Menu menu = getMenu(menuId);
            
            // 메뉴 정보 업데이트 (직접 필드 업데이트)
            // TODO: Menu 엔티티에 updateMenu 메서드 추가 필요
            
            log.info("updateMenu 성공 - menuId: {}, menuName: {}", 
                    menu.getId(), menu.getMenuName());
            
            return MenuResponse.from(menu);
        } catch (Exception e) {
            log.error("updateMenu 실패 - menuId: {}, error: {}", menuId, e.getMessage(), e);
            throw new MenuException(ErrorMessage.MENU_UPDATE_FAILED);
        }
    }

    /**
     * 메뉴 삭제
     */
    @Transactional
    public void deleteMenu(Long menuId, Long userId) {
        log.info("deleteMenu 시작 - menuId: {}, userId: {}", menuId, userId);
        
        try {
            // TODO: 권한 검증 로직 추가
            Menu menu = getMenu(menuId);
            
            // 메뉴 아이템들도 함께 삭제
            // TODO: MenuItemRepository에 deleteByMenuId 메서드 추가 필요
            menuRepository.delete(menu);
            
            log.info("deleteMenu 성공 - menuId: {}", menuId);
        } catch (Exception e) {
            log.error("deleteMenu 실패 - menuId: {}, error: {}", menuId, e.getMessage(), e);
            throw new MenuException(ErrorMessage.MENU_UPDATE_FAILED);
        }
    }

    /**
     * 메뉴 아이템 추가
     */
    @Transactional
    public MenuItemResponse addMenuItem(Long menuId, MenuItemRequest menuItemRequest, Long userId) {
        log.info("addMenuItem 시작 - menuId: {}, foodId: {}, userId: {}", 
                menuId, menuItemRequest.getFoodId(), userId);
        
        try {
            // TODO: 권한 검증 로직 추가
            Menu menu = getMenu(menuId);
            FoodItem foodItem = findFoodItemById(menuItemRequest.getFoodId());
            
            MenuItem menuItem = createMenuItemEntity(menu, foodItem, menuItemRequest);
            MenuItem savedMenuItem = menuItemRepository.save(menuItem);
            
            log.info("addMenuItem 성공 - menuItemId: {}, menuId: {}, foodId: {}", 
                    savedMenuItem.getId(), menuId, menuItemRequest.getFoodId());
            
            return MenuItemResponse.from(savedMenuItem);
        } catch (Exception e) {
            log.error("addMenuItem 실패 - menuId: {}, foodId: {}, error: {}", 
                    menuId, menuItemRequest.getFoodId(), e.getMessage(), e);
            throw new MenuException(ErrorMessage.MENU_ITEM_CREATE_FAILED);
        }
    }

    /**
     * 메뉴 아이템 삭제
     */
    @Transactional
    public void removeMenuItem(Long menuId, Long menuItemId, Long userId) {
        log.info("removeMenuItem 시작 - menuId: {}, menuItemId: {}, userId: {}", 
                menuId, menuItemId, userId);
        
        try {
            // TODO: 권한 검증 로직 추가
            MenuItem menuItem = findMenuItemById(menuItemId);
            
            // 메뉴 ID 일치 확인
            if (!menuItem.getMenu().getId().equals(menuId)) {
                throw new MenuException(ErrorMessage.MENU_ITEM_NOT_FOUND);
            }
            
            menuItemRepository.delete(menuItem);
            
            log.info("removeMenuItem 성공 - menuItemId: {}", menuItemId);
        } catch (Exception e) {
            log.error("removeMenuItem 실패 - menuItemId: {}, error: {}", menuItemId, e.getMessage(), e);
            throw new MenuException(ErrorMessage.MENU_ITEM_CREATE_FAILED);
        }
    }

    /**
     * 메뉴 조회
     */
    public Menu getMenu(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(ErrorMessage.MENU_NOT_FOUND));
    }

    // === 내부 유틸리티 메서드들 ===

    /**
     * 매장 ID로 조회
     */
    private Store findStoreById(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new MenuException(ErrorMessage.STORE_NOT_FOUND));
    }

    /**
     * 음식 아이템 ID로 조회
     */
    private FoodItem findFoodItemById(Long foodId) {
        return foodItemRepository.findById(foodId)
                .orElseThrow(() -> new FoodException(ErrorMessage.FOOD_NOT_FOUND));
    }

    /**
     * 메뉴 아이템 ID로 조회
     */
    private MenuItem findMenuItemById(Long menuItemId) {
        return menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new MenuException(ErrorMessage.MENU_ITEM_NOT_FOUND));
    }

    /**
     * 메뉴 엔티티 생성
     */
    private Menu createMenuEntity(Store store, MenuRequest menuRequest) {
        return Menu.builder()
                .store(store)
                .menuName(menuRequest.getMenuName())
                .description(menuRequest.getDescription())
                .isActive(menuRequest.getIsActive())
                .build();
    }

    /**
     * 메뉴 아이템 엔티티 생성
     */
    private MenuItem createMenuItemEntity(Menu menu, FoodItem foodItem, MenuItemRequest menuItemRequest) {
        return MenuItem.builder()
                .menu(menu)
                .foodItem(foodItem)
                .displayOrder(menuItemRequest.getDisplayOrder())
                .build();
    }
} 
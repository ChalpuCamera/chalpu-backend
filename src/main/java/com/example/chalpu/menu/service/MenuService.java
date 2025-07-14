package com.example.chalpu.menu.service;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.MenuException;
import com.example.chalpu.common.exception.StoreException;
import com.example.chalpu.common.response.PageResponse;
import com.example.chalpu.menu.domain.Menu;
import com.example.chalpu.menu.dto.MenuRequest;
import com.example.chalpu.menu.dto.MenuResponse;
import com.example.chalpu.menu.repository.MenuRepository;
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
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final UserStoreRoleService userStoreRoleService;
    private final MenuItemService menuItemService;

    /**
     * 매장별 메뉴 목록 조회 (활성 메뉴만)
     */
    public PageResponse<MenuResponse> getMenus(Long storeId, Pageable pageable) {
        try {
            Page<Menu> menuPage = menuRepository.findByStoreIdAndIsActiveTrueWithoutJoin(storeId, pageable);
            Page<MenuResponse> menuResponsePage = menuPage.map(MenuResponse::from);
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
        validateUserStoreAccess(userId, storeId);
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(ErrorMessage.STORE_NOT_FOUND));
        Menu menu = menuRepository.save(Menu.createMenu(store, menuRequest));
        log.info("event=menu_created, menu_id={}, store_id={}, user_id={}", menu.getId(), storeId, userId);
            
        return MenuResponse.from(menu);
    }

    /**
     * 메뉴 수정
     */
    @Transactional
    public MenuResponse updateMenu(Long menuId, MenuRequest menuRequest, Long userId) {
        Menu menu = findMenuByIdForValidation(menuId);
        validateUserStoreAccess(userId, menu.getStore().getId());
            menu.updateMenu(menuRequest);
        log.info("event=menu_updated, menu_id={}, user_id={}", menuId, userId);
            
            return MenuResponse.from(menu);
    }

    /**
     * 메뉴 삭제 (소프트 딜리트)
     */
    @Transactional
    public void deleteMenu(Long menuId, Long userId) {
        try {
            Menu menu = findMenuByIdForValidation(menuId);
            validateUserStoreManagement(userId, menu.getStore().getId());
            
            // 메뉴 비활성화
            menu.softDelete();
            
            // 메뉴 아이템 비활성화 (위임)
            menuItemService.softDeleteMenuItemsByMenu(menu);
            
            log.info("event=menu_deleted, menu_id={}, user_id={}", menuId, userId);
        } catch (Exception e) {
            log.error("event=menu_deletion_failed, menu_id={}, user_id={}, error_message={}",
                    menuId, userId, e.getMessage(), e);
            throw new MenuException(ErrorMessage.MENU_DELETE_FAILED);
        }
    }


    private Menu findMenuByIdForValidation(Long menuId) {
        return menuRepository.findByIdAndIsActiveTrueWithoutJoin(menuId)
                .orElseThrow(() -> new MenuException(ErrorMessage.MENU_NOT_FOUND));
    }

    private void validateUserStoreManagement(Long userId, Long storeId) {
        if (!userStoreRoleService.canUserManageStore(userId, storeId)) {
            throw new MenuException(ErrorMessage.STORE_ACCESS_DENIED);
        }
    }

    private void validateUserStoreAccess(Long userId, Long storeId) {
        if (!userStoreRoleService.canUserManageStore(userId, storeId)) {
            throw new MenuException(ErrorMessage.STORE_ACCESS_DENIED);
        }
    }
} 
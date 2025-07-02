package com.example.chalpu.menu.service;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.MenuException;
import com.example.chalpu.common.response.PageResponse;
import com.example.chalpu.menu.domain.Menu;
import com.example.chalpu.menu.domain.MenuItem;
import com.example.chalpu.menu.repository.MenuRepository;
import com.example.chalpu.menu.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;

    public PageResponse<Menu> getMenus(Long storeId, Pageable pageable) {
        Page<Menu> menuPage = menuRepository.findByStoreId(storeId, pageable);
        return PageResponse.from(menuPage);
    }

    public Menu getMenu(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new MenuException(ErrorMessage.MENU_NOT_FOUND));
    }

    @Transactional
    public Menu createMenu(Long storeId, Menu menuRequest, Long userId) {
        // TODO: 권한 검증 로직 추가
        return menuRepository.save(menuRequest);
    }

    @Transactional
    public MenuItem addMenuItem(Long menuId, MenuItem menuItemRequest, Long userId) {
        // TODO: 권한 검증 로직 추가
        // 메뉴 존재 여부 확인
        getMenu(menuId);
        return menuItemRepository.save(menuItemRequest);
    }
} 
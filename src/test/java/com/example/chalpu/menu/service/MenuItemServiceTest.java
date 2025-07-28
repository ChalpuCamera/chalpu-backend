package com.example.chalpu.menu.service;

import com.example.chalpu.common.exception.MenuException;
import com.example.chalpu.fooditem.domain.FoodItem;
import com.example.chalpu.fooditem.repository.FoodItemRepository;
import com.example.chalpu.menu.domain.Menu;
import com.example.chalpu.menu.domain.MenuItem;
import com.example.chalpu.menu.dto.MenuItemRequest;
import com.example.chalpu.menu.repository.MenuItemRepository;
import com.example.chalpu.menu.repository.MenuRepository;
import com.example.chalpu.store.domain.Store;
import com.example.chalpu.store.service.UserStoreRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceTest {

    @InjectMocks
    private MenuItemService menuItemService;

    @Mock
    private MenuItemRepository menuItemRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private FoodItemRepository foodItemRepository;
    @Mock
    private UserStoreRoleService userStoreRoleService;

    private Store store;
    private Menu menu;
    private FoodItem foodItem;
    private MenuItem menuItem;
    private Long userId = 1L;
    private Long storeId = 1L;
    private Long menuId = 1L;
    private Long foodId = 1L;
    private Long menuItemId = 1L;

    @BeforeEach
    void setUp() {
        store = Store.builder().id(storeId).build();
        menu = Menu.builder().id(menuId).store(store).menuName("기본 메뉴").isActive(true).build();
        foodItem = FoodItem.builder().id(foodId).store(store).foodName("기본 음식").build();
        menuItem = MenuItem.builder().id(menuItemId).menu(menu).foodItem(foodItem).isActive(true).build();
    }

    @Nested
    @DisplayName("메뉴 아이템 추가 테스트")
    class AddMenuItemTest {
        @Test
        @DisplayName("성공")
        void addMenuItem_success() {
            // given
            MenuItemRequest request = new MenuItemRequest(foodId, 1);
            given(menuRepository.findByIdAndIsActiveTrue(menuId)).willReturn(Optional.of(menu));
            given(foodItemRepository.findByIdAndIsActiveTrue(foodId)).willReturn(Optional.of(foodItem));
            given(userStoreRoleService.canUserManageStore(userId, storeId)).willReturn(true);
            given(menuItemRepository.save(any(MenuItem.class))).willReturn(menuItem);

            // when
            menuItemService.addMenuItem(menuId, request, userId);

            // then
            verify(menuItemRepository).save(any(MenuItem.class));
        }

        @Test
        @DisplayName("실패 - 메뉴 없음")
        void addMenuItem_fail_menuNotFound() {
            // given
            MenuItemRequest request = new MenuItemRequest(foodId, 1);
            given(menuRepository.findByIdAndIsActiveTrue(menuId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> menuItemService.addMenuItem(menuId, request, userId))
                    .isInstanceOf(MenuException.class);
        }
    }

    @Nested
    @DisplayName("메뉴 아이템 삭제 테스트")
    class RemoveMenuItemTest {
        @Test
        @DisplayName("성공")
        void removeMenuItem_success() {
            // given
            given(menuItemRepository.findByIdAndIsActiveTrue(menuItemId)).willReturn(Optional.of(menuItem));
            given(userStoreRoleService.canUserManageStore(userId, storeId)).willReturn(true);

            // when
            menuItemService.removeMenuItem(menuId, menuItemId, userId);

            // then
            verify(menuItemRepository, never()).delete(any(MenuItem.class));
        }

        @Test
        @DisplayName("실패 - 메뉴 아이템이 다른 메뉴에 속함")
        void removeMenuItem_fail_itemNotInMenu() {
            // given
            Long anotherMenuId = 2L;
            given(menuItemRepository.findByIdAndIsActiveTrue(menuItemId)).willReturn(Optional.of(menuItem));
            given(userStoreRoleService.canUserManageStore(userId, storeId)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> menuItemService.removeMenuItem(anotherMenuId, menuItemId, userId))
                    .isInstanceOf(MenuException.class);
            verify(menuItemRepository, never()).delete(any(MenuItem.class));
        }
    }
} 
package com.example.chalpu.menu.service;

import com.example.chalpu.common.exception.MenuException;
import com.example.chalpu.common.response.PageResponse;
import com.example.chalpu.menu.domain.Menu;
import com.example.chalpu.menu.dto.MenuRequest;
import com.example.chalpu.menu.dto.MenuResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @InjectMocks
    private MenuService menuService;

    @Mock
    private MenuRepository menuRepository;
    @Mock
    private UserStoreRoleService userStoreRoleService;
    @Mock
    private MenuItemService menuItemService;

    private Store store;
    private Menu menu;
    private Long userId = 1L;
    private Long storeId = 1L;
    private Long menuId = 1L;

    @BeforeEach
    void setUp() {
        store = Store.builder().id(storeId).build();
        menu = Menu.builder().id(menuId).store(store).menuName("기본 메뉴").isActive(true).build();
    }

    @Nested
    @DisplayName("메뉴 생성 테스트")
    class CreateMenuTest {
        @Test
        @DisplayName("성공")
        void createMenu_success() {
            // given
            MenuRequest request = new MenuRequest("새 메뉴", "설명", true);
            given(userStoreRoleService.canUserManageStore(userId, storeId)).willReturn(true);
            given(menuRepository.save(any(Menu.class))).willReturn(menu);

            // when
            MenuResponse response = menuService.createMenu(storeId, request, userId);

            // then
            assertThat(response.getMenuName()).isEqualTo(menu.getMenuName());
            verify(userStoreRoleService).canUserManageStore(userId, storeId);
            verify(menuRepository).save(any(Menu.class));
        }

        @Test
        @DisplayName("실패 - 권한 없음")
        void createMenu_fail_unauthorized() {
            // given
            MenuRequest request = new MenuRequest("새 메뉴", "설명", true);
            given(userStoreRoleService.canUserManageStore(userId, storeId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> menuService.createMenu(storeId, request, userId))
                    .isInstanceOf(MenuException.class);
            verify(menuRepository, never()).save(any(Menu.class));
        }
    }

    @Nested
    @DisplayName("메뉴 수정 테스트")
    class UpdateMenuTest {
        @Test
        @DisplayName("성공")
        void updateMenu_success() {
            // given
            MenuRequest request = new MenuRequest("수정된 메뉴", "수정된 설명", false);
            given(menuRepository.findByIdAndIsActiveTrue(menuId)).willReturn(Optional.of(menu));
            given(userStoreRoleService.canUserManageStore(userId, storeId)).willReturn(true);

            // when
            MenuResponse response = menuService.updateMenu(menuId, request, userId);

            // then
            assertThat(response.getMenuName()).isEqualTo("수정된 메뉴");
            assertThat(response.getIsActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("메뉴 삭제 테스트 (소프트 딜리트)")
    class DeleteMenuTest {
        @Test
        @DisplayName("성공")
        void deleteMenu_success() {
            // given
            given(menuRepository.findByIdAndIsActiveTrue(menuId)).willReturn(Optional.of(menu));
            given(userStoreRoleService.canUserManageStore(userId, storeId)).willReturn(true);

            // when
            menuService.deleteMenu(menuId, userId);

            // then
            assertThat(menu.getIsActive()).isFalse();
            verify(userStoreRoleService).canUserManageStore(userId, storeId);
            verify(menuItemService).softDeleteMenuItemsByMenu(menu);
        }
    }

    @Nested
    @DisplayName("메뉴 목록 조회 테스트")
    class GetMenusTest {
        @Test
        @DisplayName("성공")
        void getMenus_success() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Menu> menuPage = new PageImpl<>(Collections.singletonList(menu), pageable, 1);
            given(menuRepository.findByStoreIdAndIsActiveTrue(storeId, pageable)).willReturn(menuPage);

            // when
            PageResponse<MenuResponse> response = menuService.getMenus(storeId, pageable);

            // then
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getContent().get(0).getMenuName()).isEqualTo(menu.getMenuName());
        }
    }
} 
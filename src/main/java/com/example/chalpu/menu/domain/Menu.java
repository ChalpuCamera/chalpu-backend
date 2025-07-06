package com.example.chalpu.menu.domain;

import com.example.chalpu.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import com.example.chalpu.store.domain.Store;
import com.example.chalpu.menu.dto.MenuRequest;

@Entity
@Table(name = "menus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Menu extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(length = 100, nullable = false)
    private String menuName;

    private String description;

    @Builder.Default
    private Boolean isActive = true;

    // == 생성 메서드 == //
    public static Menu createMenu(Store store, MenuRequest menuRequest) {
        return Menu.builder()
                .store(store)
                .menuName(menuRequest.getMenuName())
                .description(menuRequest.getDescription())
                .isActive(menuRequest.getIsActive())
                .build();
    }

    // == 비즈니스 로직 == //
    /**
     * 메뉴 정보 수정
     */
    public void updateMenu(MenuRequest menuRequest) {
        this.menuName = menuRequest.getMenuName();
        this.description = menuRequest.getDescription();
        this.isActive = menuRequest.getIsActive();
    }

    /**
     * 메뉴 비활성화 (소프트 딜리트)
     */
    public void softDelete() {
        this.isActive = false;
    }
} 
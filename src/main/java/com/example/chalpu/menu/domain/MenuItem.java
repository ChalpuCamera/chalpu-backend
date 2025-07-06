package com.example.chalpu.menu.domain;

import jakarta.persistence.*;
import lombok.*;
import com.example.chalpu.fooditem.domain.FoodItem;
import com.example.chalpu.menu.dto.MenuItemRequest;

@Entity
@Table(name = "menu_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 PK

    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    private FoodItem foodItem;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 1;

    @Builder.Default
    private Boolean isActive = true;

    // == 생성 메서드 == //
    public static MenuItem createMenuItem(Menu menu, FoodItem foodItem, MenuItemRequest menuItemRequest) {
        return MenuItem.builder()
                .menu(menu)
                .foodItem(foodItem)
                .displayOrder(menuItemRequest.getDisplayOrder())
                .isActive(true)
                .build();
    }
    
    // == 비즈니스 로직 == //
    /**
     * 메뉴 아이템 비활성화 (소프트 딜리트)
     */
    public void softDelete() {
        this.isActive = false;
    }

    /**
     * 메뉴 아이템 표시 순서 변경
     */
    public void updateDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
} 
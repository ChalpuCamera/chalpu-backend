package com.example.chalpu.menu.domain;

import jakarta.persistence.*;
import lombok.*;
import com.example.chalpu.fooditem.domain.FoodItem;

@Entity
@Table(name = "menu_items")
@Data
@NoArgsConstructor
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
} 
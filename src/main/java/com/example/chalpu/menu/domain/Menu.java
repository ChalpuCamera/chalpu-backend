package com.example.chalpu.menu.domain;

import com.example.chalpu.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import com.example.chalpu.store.domain.Store;

@Entity
@Table(name = "menus")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
} 
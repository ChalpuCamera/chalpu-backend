package com.example.chalpu.menu.domain;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import com.example.chalpu.store.domain.Store;

@Entity
@Table(name = "menus")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu {
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

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private Timestamp updatedAt;
} 
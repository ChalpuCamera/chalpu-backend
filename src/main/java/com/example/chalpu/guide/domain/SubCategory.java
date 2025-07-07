package com.example.chalpu.guide.domain;

import com.example.chalpu.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "sub_categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubCategory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Builder
    public SubCategory(String name) {
        this.name = name;
    }
} 
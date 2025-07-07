package com.example.chalpu.tag.domain;

import com.example.chalpu.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "tags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false)
    private Boolean isActive;

    @Builder
    public Tag(String name) {
        this.name = name;
        this.isActive = true;
    }

    public void softDelete() {
        this.isActive = false;
    }
} 
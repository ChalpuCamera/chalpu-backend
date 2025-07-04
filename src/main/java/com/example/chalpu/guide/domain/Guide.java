package com.example.chalpu.guide.domain;

import com.example.chalpu.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "guides")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Guide extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guide_id")
    private Long id;

    @Column(length = 500, nullable = false, unique = true)
    private String s3Key;

    @Column(length = 255, nullable = false)
    private String fileName;

    @Builder
    private Guide(String s3Key, String fileName) {
        this.s3Key = s3Key;
        this.fileName = fileName;
    }
} 
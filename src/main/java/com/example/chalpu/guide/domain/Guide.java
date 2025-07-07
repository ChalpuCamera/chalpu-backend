package com.example.chalpu.guide.domain;

import com.example.chalpu.common.entity.BaseTimeEntity;
import com.example.chalpu.tag.domain.Tag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "guides")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Guide extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guide_id")
    private Long id;

    @Column(length = 500, nullable = false, unique = true)
    private String guideS3Key;

    @Column(length = 500, nullable = false, unique = true)
    private String imageS3Key;

    @Column(length = 100, nullable = false)
    private String fileName;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id", nullable = false)
    private SubCategory subCategory;

    @Column(nullable = false)
    private Boolean isActive;

    @Builder
    public Guide(String content, String guideS3Key, String imageS3Key, String fileName,
                 SubCategory subCategory) {
        this.content = content;
        this.guideS3Key = guideS3Key;
        this.imageS3Key = imageS3Key;
        this.fileName = fileName;
        this.subCategory = subCategory;
        this.isActive = true;
    }

    public void update(String content, String fileName, SubCategory subCategory) {
        if (content != null) {
            this.content = content;
        }
        if (fileName != null) {
            this.fileName = fileName;
        }
        if (subCategory != null) {
            this.subCategory = subCategory;
        }
    }

    public void softDelete() {
        this.isActive = false;
    }
} 
package com.example.chalpu.tag.domain;

import com.example.chalpu.common.entity.BaseTimeEntity;
import com.example.chalpu.guide.domain.Guide;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "guide_tags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuideTag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id", nullable = false)
    private Guide guide;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @Column(nullable = false)
    private Boolean isActive;

    @Builder
    public GuideTag(Guide guide, Tag tag) {
        this.guide = guide;
        this.tag = tag;
        this.isActive = true;
    }

    public void softDelete() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }
} 
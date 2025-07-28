package com.example.chalpu.photo.domain;

import com.example.chalpu.common.entity.BaseTimeEntity;
import com.example.chalpu.user.domain.User;
import com.example.chalpu.fooditem.domain.FoodItem;
import com.example.chalpu.store.domain.Store;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;

@NamedEntityGraph(
    name = "Photo.withAll",
    attributeNodes = {
        @NamedAttributeNode("user"),
        @NamedAttributeNode("store"),
        @NamedAttributeNode("foodItem")
    }
)
@Entity
@Table(name = "photos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Photo extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    private FoodItem foodItem;

    @Column(length = 500, nullable = false, unique = true)
    private String s3Key;

    @Column(length = 255, nullable = false)
    private String fileName;

    private String filter;
    private Integer fileSize;
    private Integer imageWidth;
    private Integer imageHeight;

    @Builder.Default
    private Boolean isActive = true;

    public void softDelete() {
        this.isActive = false;
    }

}
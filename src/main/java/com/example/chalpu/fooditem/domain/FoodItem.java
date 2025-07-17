package com.example.chalpu.fooditem.domain;

import com.example.chalpu.common.entity.BaseTimeEntity;
import com.example.chalpu.fooditem.dto.FoodItemRequest;
import com.example.chalpu.store.domain.Store;
import com.example.chalpu.photo.domain.Photo;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NamedEntityGraph(name = "FoodItem.withStore", attributeNodes = @NamedAttributeNode("store"))
@Entity
@Table(name = "food_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class FoodItem extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToMany(mappedBy = "foodItem", fetch = FetchType.LAZY)
    private List<Photo> photos = new ArrayList<>();

    @Column(length = 100, nullable = false)
    private String foodName;

    private String description;
    private String ingredients;
    private String cookingMethod;
    private BigDecimal price;
    private String thumbnailUrl;
    
    @Builder.Default
    private Boolean isActive = true;

    // 정적 팩토리 메서드
    public static FoodItem createFoodItem(Store store, FoodItemRequest request) {
        return FoodItem.builder()
                .store(store)
                .foodName(request.getFoodName())
                .description(request.getDescription())
                .ingredients(request.getIngredients())
                .cookingMethod(request.getCookingMethod())
                .price(request.getPrice())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
    }

    // 업데이트 메서드
    public void updateFoodItem(FoodItemRequest request) {
        this.foodName = request.getFoodName();
        this.description = request.getDescription();
        this.ingredients = request.getIngredients();
        this.cookingMethod = request.getCookingMethod();
        this.price = request.getPrice();
        if (request.getIsActive() != null) {
            this.isActive = request.getIsActive();
        }
    }

    // 소프트 딜리트
    public void softDelete() {
        this.isActive = false;
        // 연관된 Photo들도 소프트 딜리트
        this.photos.forEach(Photo::softDelete);
    }
} 
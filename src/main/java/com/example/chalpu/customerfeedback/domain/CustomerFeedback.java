package com.example.chalpu.customerfeedback.domain;

import com.example.chalpu.common.entity.BaseTimeEntity;
import com.example.chalpu.customer.domain.Customer;
import com.example.chalpu.fooditem.domain.FoodItem;
import com.example.chalpu.store.domain.Store;
import com.example.chalpu.survey.domain.Survey;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class CustomerFeedback extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private FoodItem foodItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    public static CustomerFeedback createFeedback(FoodItem foodItem, Store store, 
                                                Customer customer, Survey survey) {
        return CustomerFeedback.builder()
                .foodItem(foodItem)
                .store(store)
                .customer(customer)
                .survey(survey)
                .build();
    }

    public void deactivate() {
        this.isActive = false;
    }

    public boolean isActive() {
        return this.isActive != null && this.isActive;
    }
}
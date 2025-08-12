package com.example.chalpu.customer.dto;

import com.example.chalpu.customer.domain.CustomerTaste;
import lombok.Data;

@Data
public class CustomerTasteRequest {
    private Integer spicyLevel;
    private Integer mealAmount;
    private Integer mealSpending;

    public CustomerTaste toEntity() {
        return CustomerTaste.builder()
                .spicyLevel(spicyLevel)
                .mealAmount(mealAmount)
                .mealSpending(mealSpending)
                .build();
    }
}
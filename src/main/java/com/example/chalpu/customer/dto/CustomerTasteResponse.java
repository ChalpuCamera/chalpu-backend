package com.example.chalpu.customer.dto;

import com.example.chalpu.customer.domain.CustomerTaste;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerTasteResponse {
    private Integer spicyLevel;
    private Integer mealAmount;
    private Integer mealSpending;

    public static CustomerTasteResponse from(CustomerTaste customerTaste) {
        if (customerTaste == null) {
            return null;
        }
        
        return CustomerTasteResponse.builder()
                .spicyLevel(customerTaste.getSpicyLevel())
                .mealAmount(customerTaste.getMealAmount())
                .mealSpending(customerTaste.getMealSpending())
                .build();
    }
}
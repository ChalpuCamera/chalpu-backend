package com.example.chalpu.customer.dto;

import com.example.chalpu.customer.domain.CustomerTaste;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "고객 취향 설정 요청")
public class CustomerTasteRequest {
    
    @Schema(description = "매운맛 선호도 (1: 매우 싫음 ~ 5: 매우 좋음)", example = "3", minimum = "1", maximum = "5")
    private Integer spicyLevel;
    
    @Schema(description = "식사량 선호도 (1: 매우 적음 ~ 5: 매우 많음)", example = "2", minimum = "1", maximum = "5")
    private Integer mealAmount;
    
    @Schema(description = "식비 수준 (1: 매우 저렴 ~ 5: 매우 비쌈)", example = "4", minimum = "1", maximum = "5")
    private Integer mealSpending;

    public CustomerTaste toEntity() {
        return CustomerTaste.builder()
                .spicyLevel(spicyLevel)
                .mealAmount(mealAmount)
                .mealSpending(mealSpending)
                .build();
    }
}
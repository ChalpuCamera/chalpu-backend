package com.example.chalpu.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "매장 생성/수정 요청")
public class StoreRequest {
    
    @Schema(description = "매장명", example = "맛있는 식당", required = true)
    private String storeName;
    
    @Schema(description = "가게 유형", example = "한식")
    private String businessType;
    
    @Schema(description = "주소", example = "서울시 강남구 테헤란로 123")
    private String address;
    
    @Schema(description = "전화번호", example = "02-1234-5678")
    private String phone;
    
    @Schema(description = "사업자 등록번호", example = "123-45-67890")
    private String businessRegistrationNumber;
} 
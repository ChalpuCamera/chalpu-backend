package com.example.chalpu.store.domain;

import com.example.chalpu.common.entity.BaseTimeEntity;
import com.example.chalpu.store.dto.StoreRequest;
import jakarta.persistence.*;
import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "stores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long id;

    @Column(length = 100, nullable = false)
    private String storeName;

    @Schema(description = "가게 유형 (한식, 양식, 중식)")
    @Column(length = 50)
    private String businessType;

    @Column(name = "address")
    private String address;

    @Schema(description = "가게 번호")
    @Column(length = 20)
    private String phone;

    @Schema(description = "사업자 등록번호")
    @Column(length = 50, unique = true)
    private String businessRegistrationNumber;

    public static Store createStore(StoreRequest storeRequest){
        return Store.builder()
                .storeName(storeRequest.getStoreName())
                .businessType(storeRequest.getBusinessType())
                .address(storeRequest.getAddress())
                .phone(storeRequest.getPhone())
                .businessRegistrationNumber(storeRequest.getBusinessRegistrationNumber())
                .build();
    }

    public void updateStore(StoreRequest storeRequest) {
        this.storeName = storeRequest.getStoreName();
        this.businessType = storeRequest.getBusinessType();
        this.address = storeRequest.getAddress();
        this.phone = storeRequest.getPhone();
        this.businessRegistrationNumber = storeRequest.getBusinessRegistrationNumber();
    }
} 
package com.example.chalpu.store.domain;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;

@Entity
@Table(name = "stores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long id;

    @Column(length = 100, nullable = false)
    private String storeName;

    @Column(length = 50)
    private String businessType;

    @Column(nullable = false)
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(length = 50, unique = true)
    private String businessRegistrationNumber;

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private Timestamp updatedAt;
} 
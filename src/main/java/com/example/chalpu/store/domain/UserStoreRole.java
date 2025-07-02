package com.example.chalpu.store.domain;

import com.example.chalpu.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import com.example.chalpu.user.domain.User;


@Entity
@Table(name = "user_store_roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStoreRole extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 PK

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StoreRoleType roleType;

    private BigDecimal ownershipPercentage;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
} 
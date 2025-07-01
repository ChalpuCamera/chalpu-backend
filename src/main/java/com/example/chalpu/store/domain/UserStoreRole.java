package com.example.chalpu.store.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(
    name = "user_store_roles",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "store_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStoreRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 PK

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private com.example.chalpu.user.domain.User user;

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

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private Timestamp updatedAt;
} 
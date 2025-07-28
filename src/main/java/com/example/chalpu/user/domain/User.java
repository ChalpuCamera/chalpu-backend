package com.example.chalpu.user.domain;

import com.example.chalpu.common.entity.BaseTimeEntity;
import com.example.chalpu.oauth.model.AuthProvider;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(length = 100, unique = true, nullable = false)
    private String email;

    @Column(length = 100)
    private String name;

    @Column(length = 100)
    private String providerUserId;

    @Column(length = 20, unique = true)
    private String phone;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "social_id")
    private String socialId;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AuthProvider provider;
    
    private String uuid;
    
    @Column(name = "last_login")
    private String lastLogin;

    @Enumerated(EnumType.STRING)
    private Role role;
    
    private String picture;

    public void updateOAuth2Info(String name, String picture) {
        this.name = name;
        this.picture = picture;
    }

    public void softDelete() {
        this.isActive = false;
        super.setDeletedAt(LocalDateTime.now());
    }

    public void activate() {
        this.isActive = true;
        super.setDeletedAt(null);
    }
}

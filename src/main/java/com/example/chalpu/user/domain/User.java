package com.example.chalpu.user.domain;

import com.example.chalpu.common.entity.BaseTimeEntity;
import com.example.chalpu.oauth.model.AuthProvider;
import com.example.chalpu.oauth.model.RefreshToken;
import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;

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

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 100, nullable = false)
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
    
    // 프로필 이미지 URL
    private String picture;

    // OAuth 정보 업데이트 (기존 사용자)
    public void updateOAuth2Info(String name, String picture) {
        this.name = name;
        this.picture = picture;
    }
}

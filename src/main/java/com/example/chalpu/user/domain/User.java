package com.example.chalpu.user.domain;

import com.example.chalpu.common.entity.BaseTimeEntity;
import com.example.chalpu.oauth.model.AuthProvider;
import com.example.chalpu.oauth.model.RefreshToken;
import jakarta.persistence.*;
import lombok.*;

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
    private Long id;

    private String name;
    
    private String nickname;
    
    private String email;
    
    private String mdn;
    
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

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private RefreshToken refreshToken;

    // OAuth 정보 업데이트 (기존 사용자)
    public void updateOAuth2Info(String name, String picture) {
        this.name = name;
        this.picture = picture;
    }
}

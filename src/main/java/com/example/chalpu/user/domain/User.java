package com.example.chalpu.user.domain;

import com.example.chalpu.common.entity.BaseTimeEntity;
import com.example.chalpu.oauth.model.AuthProvider;
import com.example.chalpu.oauth.model.RefreshToken;
import com.example.chalpu.store.domain.UserStoreRole;
import com.example.chalpu.fcm.domain.UserFCMToken;
import com.example.chalpu.photo.domain.Photo;
import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    
    // 프로필 이미지 URL
    private String picture;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserStoreRole> userStoreRoles = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)  
    private List<Photo> photos = new ArrayList<>();

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private RefreshToken refreshToken;

    // OAuth 정보 업데이트 (기존 사용자)
    public void updateOAuth2Info(String name, String picture) {
        this.name = name;
        this.picture = picture;
    }

    public void softDelete() {
        this.isActive = false;
        
        // 연관된 UserStoreRole들 소프트 딜리트
        this.userStoreRoles.forEach(UserStoreRole::softDelete);
        
        // 연관된 Photo들 소프트 딜리트  
        this.photos.forEach(Photo::softDelete);
        
        // RefreshToken은 하드 딜리트 (cascade로 자동 처리됨)
        // UserFCMToken들은 Repository를 통해 별도 처리 필요
    }
}

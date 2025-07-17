package com.example.chalpu.user.service;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.UserException;
import com.example.chalpu.oauth.dto.UserInfoDTO;
import com.example.chalpu.oauth.security.jwt.UserDetailsImpl;
import com.example.chalpu.user.domain.User;
import com.example.chalpu.user.repository.UserRepository;
import com.example.chalpu.photo.repository.PhotoRepository;
import com.example.chalpu.store.repository.UserStoreRoleRepository;
import com.example.chalpu.fcm.repository.UserFCMTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final UserStoreRoleRepository userStoreRoleRepository;
    private final UserFCMTokenRepository userFCMTokenRepository;

    public UserInfoDTO getCurrentUser(UserDetailsImpl currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new UserException(ErrorMessage.USER_NOT_FOUND));
        
        return UserInfoDTO.fromEntity(user);
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException(ErrorMessage.USER_NOT_FOUND));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorMessage.USER_INVALID_CREDENTIALS));
    }

    /**
     * 이메일 중복 체크
     *
     * @param email 확인할 이메일
     * @return 중복되지 않은 경우 true, 중복된 경우 false
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    @Transactional
    public void softDelete(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorMessage.USER_NOT_FOUND));
        
        // 1. 연관된 Photo들 소프트 딜리트
        photoRepository.softDeleteByUserId(userId);
        
        // 2. 연관된 UserStoreRole들 소프트 딜리트  
        userStoreRoleRepository.softDeleteByUserId(userId);
        
        // 3. 연관된 UserFCMToken들 소프트 딜리트
        userFCMTokenRepository.softDeleteByUserId(userId);
        
        // 4. User 자체 소프트 딜리트 (RefreshToken은 cascade로 하드 딜리트됨)
        user.softDelete();
    }
}
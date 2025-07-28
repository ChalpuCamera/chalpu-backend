package com.example.chalpu.oauth.service;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.RefreshTokenException;
import com.example.chalpu.common.exception.UserException;
import com.example.chalpu.oauth.model.RefreshToken;
import com.example.chalpu.oauth.repository.RefreshTokenRepository;
import com.example.chalpu.oauth.security.jwt.JwtTokenProvider;
import com.example.chalpu.user.domain.User;
import com.example.chalpu.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public boolean validateRefreshToken(String tokenValue) {
        // 1. DB에 토큰이 존재하는지 확인
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByRefreshToken(tokenValue);
        
        if (refreshTokenOpt.isEmpty()) {
            log.error("존재하지 않는 Refresh Token: {}", tokenValue);
            return false;
        }

        jwtTokenProvider.validateToken(tokenValue);

        // 3. Refresh Token 타입인지 확인
        if (!jwtTokenProvider.isRefreshToken(tokenValue)) {
            log.error("Refresh Token이 아닌 토큰: {}", tokenValue);
            return false;
        }
        
        return true;
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByRefreshToken(String tokenValue) {
        return refreshTokenRepository.findByRefreshToken(tokenValue)
                .map(RefreshToken::getUser)
                .filter(user -> user.getIsActive()); // 활성 사용자만 반환
    }

    @Transactional
    public void saveRefreshToken(String refreshToken, Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserException(ErrorMessage.USER_NOT_FOUND));

            // 기존 리프레시 토큰이 있으면 업데이트, 없으면 새로 저장
            refreshTokenRepository.findByUser(user)
                    .ifPresentOrElse(
                            token -> {
                                token.updateRefreshToken(refreshToken);
                                log.info("event=refresh_token_updated, user_id={}", userId);
                            },
                            () -> {
                                RefreshToken newRefreshToken = RefreshToken.builder()
                                        .refreshToken(refreshToken)
                                        .user(user)
                                        .build();
                                refreshTokenRepository.save(newRefreshToken);
                                log.info("event=refresh_token_created, user_id={}", userId);
                            }
                    );
        } catch (Exception e) {
            log.error("event=refresh_token_save_failed, user_id={}, error_message={}", userId, e.getMessage(), e);
            throw new RefreshTokenException(ErrorMessage.REFRESH_TOKEN_SAVE_ERROR);
        }
    }

    @Transactional
    public void deleteRefreshTokenByUserId(Long userId) {
        try {
            refreshTokenRepository.deleteByUserId(userId);
            log.info("event=refresh_token_deleted, user_id={}", userId);
        } catch (Exception e) {
            log.error("event=refresh_token_delete_failed, user_id={}, error_message={}", userId, e.getMessage(), e);
            throw new RefreshTokenException(ErrorMessage.REFRESH_TOKEN_DELETE_ERROR);
        }
    }
}

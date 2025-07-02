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
        
        // 2. JWT 토큰의 유효기간 검증
        if (!jwtTokenProvider.validateToken(tokenValue)) {
            log.error("만료되거나 유효하지 않은 Refresh Token: {}", tokenValue);
            return false;
        }
        
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
            Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);
            if (existingToken.isPresent()) {
                // 기존 토큰 업데이트
                RefreshToken token = existingToken.get();
                token.updateRefreshToken(refreshToken);
                log.info("기존 Refresh Token 업데이트: userId={}", userId);
            } else {
                // 새 리프레시 토큰 저장
                RefreshToken newRefreshToken = RefreshToken.builder()
                        .refreshToken(refreshToken)
                        .user(user)
                        .build();

                refreshTokenRepository.save(newRefreshToken);
                log.info("새 Refresh Token 저장 완료: userId={}", userId);
            }

        } catch (Exception e) {
            log.error("Refresh Token 저장 실패: userId={}, error={}", userId, e.getMessage());
            throw new RefreshTokenException(ErrorMessage.REFRESH_TOKEN_SAVE_ERROR);
        }
    }

    @Transactional
    public void deleteRefreshTokenByUserId(Long userId) {
        try {
            refreshTokenRepository.deleteByUserId(userId);
            log.info("사용자 ID {}의 Refresh Token 삭제 완료", userId);
        } catch (Exception e) {
            log.error("사용자 ID {}의 Refresh Token 삭제 실패: {}", userId, e.getMessage());
            throw new RefreshTokenException(ErrorMessage.REFRESH_TOKEN_DELETE_ERROR);
        }
    }
}

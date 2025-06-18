package com.example.chalpu.oauth.service;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.RefreshTokenException;
import com.example.chalpu.oauth.model.RefreshToken;
import com.example.chalpu.oauth.repository.RefreshTokenRepository;
import com.example.chalpu.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(readOnly = true)
    public boolean validateRefreshToken(String tokenValue) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByRefreshToken(tokenValue);
        
        if (refreshTokenOpt.isEmpty()) {
            log.error("존재하지 않는 Refresh Token: {}", tokenValue);
            return false;
        }
        
        return true;
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByRefreshToken(String tokenValue) {
        return refreshTokenRepository.findByRefreshToken(tokenValue)
                .map(RefreshToken::getUser);
    }

    public void deleteRefreshToken(String tokenValue) {
        try {
            refreshTokenRepository.deleteByRefreshToken(tokenValue);
            log.info("Refresh Token 삭제 완료: 토큰 값 = {}", tokenValue);
        } catch (Exception e) {
            log.error("Refresh Token 삭제 실패: {}", e.getMessage());
            throw new RefreshTokenException(ErrorMessage.REFRESH_TOKEN_DELETE_ERROR);
        }
    }
}

package com.example.chalpu.oauth.security.oauth2;

import com.example.chalpu.customer.domain.Customer;
import com.example.chalpu.customer.repository.CustomerRepository;
import com.example.chalpu.customer.service.CustomerRefreshTokenService;
import com.example.chalpu.oauth.dto.TokenDTO;
import com.example.chalpu.oauth.security.jwt.JwtTokenProvider;
import com.example.chalpu.oauth.security.jwt.UserDetailsImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomerRepository customerRepository;
    private final CustomerRefreshTokenService customerRefreshTokenService;

    @Value("${oauth2.redirect.success-url}")
    private String redirectSuccessUrl;
    @Value("${oauth2.redirect.failure-url}")
    private String redirectFailureUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // AuthService를 통해 토큰 생성 (Customer용 역할로 설정)
            TokenDTO tokenDTO = jwtTokenProvider.generateTokens(userDetails.getId(), userDetails.getEmail(), "CUSTOMER");

            // Customer 조회
            Customer customer = customerRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            
            // Customer Refresh Token 저장
            customerRefreshTokenService.createRefreshToken(customer, tokenDTO.getRefreshToken());


            // Refresh Token을 HttpOnly 쿠키로 설정
            Cookie refreshTokenCookie = new Cookie("refreshToken", tokenDTO.getRefreshToken());
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일
            response.addCookie(refreshTokenCookie);

            log.info("OAuth2 로그인 성공: userId={}, email={}, provider={}", 
                    userDetails.getId(), userDetails.getEmail(), userDetails.getProvider());

            // Access Token과 userId만 URL 파라미터로 전달
            String targetUrl = UriComponentsBuilder.fromUriString(redirectSuccessUrl)
                    .queryParam("accessToken", tokenDTO.getAccessToken())
                    .queryParam("userId", userDetails.getId())
                    .build().toUriString();

            // 리다이렉트 수행
            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {
            log.error("OAuth2 인증 성공 처리 중 오류 발생: {}", e.getMessage(), e);

            // 오류 발생 시 실패 URL로 리다이렉트
            String errorUrl = UriComponentsBuilder.fromUriString(redirectFailureUrl)
                    .queryParam("error", URLEncoder.encode("로그인 처리 중 오류가 발생했습니다.", StandardCharsets.UTF_8))
                    .build().toUriString();

            getRedirectStrategy().sendRedirect(request, response, errorUrl);
        }
    }
}
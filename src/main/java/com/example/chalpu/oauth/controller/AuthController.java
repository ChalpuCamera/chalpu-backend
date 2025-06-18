package com.example.chalpu.oauth.controller;

import com.example.chalpu.common.response.ApiResponse;
import com.example.chalpu.oauth.dto.AccessTokenDTO;
import com.example.chalpu.oauth.dto.RefreshTokenDTO;
import com.example.chalpu.oauth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AccessTokenDTO>> refresh(@RequestBody RefreshTokenDTO refreshToken) {

        AccessTokenDTO newAccessToken = authService.refreshToken(refreshToken);

        return ResponseEntity.ok(ApiResponse.success("토큰 갱신이 완료되었습니다.", newAccessToken));
    }

    @Operation(summary = "로그아웃 처리", description = "리프레쉬 토큰 받아서 삭제후 200 반환")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody RefreshTokenDTO refreshToken){
        // AuthService에서 토큰 삭제 처리
        authService.logout(refreshToken);

        return ResponseEntity.ok(ApiResponse.success());
    }
}


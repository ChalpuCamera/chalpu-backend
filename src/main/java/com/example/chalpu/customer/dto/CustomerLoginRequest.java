package com.example.chalpu.customer.dto;

import com.example.chalpu.fcm.domain.DeviceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "고객용 소셜 로그인 요청")
public class CustomerLoginRequest {

    @NotBlank(message = "액세스 토큰은 필수입니다")
    @Schema(description = "소셜 플랫폼 액세스 토큰", example = "ya29.a0AfH6SMC...")
    private String accessToken;

    @Schema(description = "FCM 토큰", example = "fcm_token_string")
    private String fcmToken;

    @Schema(description = "디바이스 타입", example = "ANDROID")
    private DeviceType deviceType;
}
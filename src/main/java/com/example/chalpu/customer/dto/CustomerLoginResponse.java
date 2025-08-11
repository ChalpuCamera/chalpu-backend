package com.example.chalpu.customer.dto;

import com.example.chalpu.oauth.dto.TokenDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "고객용 소셜 로그인 응답")
public class CustomerLoginResponse {

    @Schema(description = "JWT 토큰 정보")
    private TokenDTO tokenDTO;

    @Schema(description = "고객 ID", example = "123")
    private Long customerId;
}
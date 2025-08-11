package com.example.chalpu.customer.dto;

import com.example.chalpu.customer.domain.Customer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "고객 정보 응답")
public class CustomerResponse {

    @Schema(description = "고객 ID", example = "123")
    private Long id;

    @Schema(description = "닉네임", example = "음식러버")
    private String nickname;

    @Schema(description = "성별", example = "FEMALE")
    private Customer.Gender gender;

    @Schema(description = "나이", example = "25")
    private Integer age;

    @Schema(description = "피드백 작성 수", example = "5")
    private Integer feedbackCount;

    @Schema(description = "이메일", example = "customer@example.com")
    private String email;

    @Schema(description = "프로필 이미지 URL")
    private String picture;

    public static CustomerResponse from(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .nickname(customer.getNickname())
                .gender(customer.getGender())
                .age(customer.getAge())
                .feedbackCount(customer.getFeedbackCount())
                .email(customer.getEmail())
                .picture(customer.getPicture())
                .build();
    }
}
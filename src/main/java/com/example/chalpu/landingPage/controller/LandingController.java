package com.example.chalpu.landingPage.controller;

import com.example.chalpu.common.response.ApiResponse;
import com.example.chalpu.landingPage.domain.Landing;
import com.example.chalpu.landingPage.dto.LandingRequest;
import com.example.chalpu.landingPage.repository.LandingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity; // ResponseEntity 임포트

@RestController
@RequestMapping("/landing")
@RequiredArgsConstructor
@Slf4j
public class LandingController {
    private final LandingRepository landingRepository;

    @PostMapping("/inquiry")
    public ResponseEntity<ApiResponse<Void>> inquiry(@RequestBody LandingRequest request) {
        // Landing 엔티티 생성
        Landing landing = Landing.builder() 
            .email(request.getEmail())
            .phone(request.getPhone())
            .message(request.getMessage())
            .build();
        
        // 데이터베이스에 저장
        landingRepository.save(landing);
        
        // 성공 응답 반환 (ResponseEntity로 감싸기)
        return ResponseEntity.ok(ApiResponse.success());
    }
}
package com.example.chalpu.home.controller;

import com.example.chalpu.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.example.chalpu.home.domain.PhotoTip;
import com.example.chalpu.home.dto.PhotoTipDto;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    @GetMapping("/tips")
    public ApiResponse<List<PhotoTipDto>> getAllTips() {
        return ApiResponse.success(Arrays.stream(PhotoTip.values())
                .map(PhotoTipDto::from)
                .toList());
    }

    @GetMapping("/tips/{id}")
    public ApiResponse<PhotoTipDto> getTipById(@PathVariable String id) {
        return ApiResponse.success(PhotoTip.findById(id)
                .map(PhotoTipDto::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }
}

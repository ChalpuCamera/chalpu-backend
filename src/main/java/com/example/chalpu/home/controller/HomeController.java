package com.example.chalpu.home.controller;

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
    public List<PhotoTipDto> getAllTips() {
        return Arrays.stream(PhotoTip.values())
                .map(PhotoTipDto::from)
                .toList();
    }

    @GetMapping("/tips/{id}")
    public PhotoTipDto getTipById(@PathVariable String id) {
        return PhotoTip.findById(id)
                .map(PhotoTipDto::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}

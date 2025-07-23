package com.example.chalpu.photo.service;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.PhotoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoRoomService {

    private final RestTemplate restTemplate;

    @Value("${photoroom.api.url:https://sdk.photoroom.com/v1/segment}")
    private String photoRoomApiUrl;

    @Value("${photoroom.api.key}")
    private String photoRoomApiKey;

    public byte[] removeBackground(MultipartFile imageFile) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("X-Api-Key", photoRoomApiKey);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image_file", new ByteArrayResource(imageFile.getBytes()) {
                @Override
                public String getFilename() {
                    return imageFile.getOriginalFilename();
                }
            });

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<byte[]> response = restTemplate.postForEntity(
                    photoRoomApiUrl,
                    requestEntity,
                    byte[].class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("event=photoroom_api_failed, status_code={}", response.getStatusCode());
                throw new PhotoException(ErrorMessage.PHOTO_BACKGROUND_REMOVAL_FAILED);
            }

            log.info("event=background_removed_successfully, file_name={}", imageFile.getOriginalFilename());
            return response.getBody();

        } catch (IOException e) {
            log.error("event=photoroom_api_io_error, file_name={}, error_message={}", 
                     imageFile.getOriginalFilename(), e.getMessage(), e);
            throw new PhotoException(ErrorMessage.PHOTO_BACKGROUND_REMOVAL_FAILED);
        } catch (Exception e) {
            log.error("event=photoroom_api_unexpected_error, file_name={}, error_message={}", 
                     imageFile.getOriginalFilename(), e.getMessage(), e);
            throw new PhotoException(ErrorMessage.PHOTO_BACKGROUND_REMOVAL_FAILED);
        }
    }
}
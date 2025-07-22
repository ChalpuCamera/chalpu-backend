package com.example.chalpu.fcm.config;

import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.NotificationException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.*;

/**
 * Firebase Configuration
 * Firebase Admin SDK 초기화 및 설정을 담당하는 클래스
 *
 */
@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${fcm.service-account-key-json}")
    private String serviceAccountKeyJson;

    @PostConstruct
    public void initializeFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount = new ByteArrayInputStream(serviceAccountKeyJson.getBytes());

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("Firebase 초기화 완료");
            } else {
                log.info("Firebase는 이미 초기화되어 있습니다.");
            }

        } catch (IOException e) {
            log.error("Firebase 초기화 실패: Service Account Key JSON 파싱 실패 - {}", e.getMessage());
            throw new NotificationException(ErrorMessage.NOTIFICATION_FIREBASE_INIT_FAILED);
        } catch (Exception e) {
            log.error("Firebase 초기화 중 예상치 못한 오류 발생: {}", e.getMessage());
            throw new NotificationException(ErrorMessage.NOTIFICATION_FIREBASE_INIT_FAILED);
        }
    }

}


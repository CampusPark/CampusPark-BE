package org.example.campuspark.global.config;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechSettings;
import org.example.campuspark.global.handler.AudioWebSocketHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    // 웹소켓 설정 상수
    private static final int MAX_MESSAGE_BUFFER_SIZE = 1024 * 1024; // 1MB
    private static final long MAX_SESSION_IDLE_TIMEOUT = 30 * 60 * 1000L; // 30분

    // GCP 설정 값
    @Value("${spring.cloud.gcp.credentials.location:classpath:dalbit-471516-f3fb27a24dac.json}")
    private String credentialsLocation;

    @Value("${spring.cloud.gcp.project-id:dalbit-471516}")
    private String projectId;

    /**
     * WebSocket 컨테이너 설정
     * - 최대 메시지 버퍼 크기 및 세션 타임아웃 설정
     */
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(MAX_MESSAGE_BUFFER_SIZE);
        container.setMaxBinaryMessageBufferSize(MAX_MESSAGE_BUFFER_SIZE);
        container.setMaxSessionIdleTimeout(MAX_SESSION_IDLE_TIMEOUT);

        logger.info("WebSocket 컨테이너 설정: 최대 메시지 버퍼 = {}bytes, 세션 타임아웃 = {}ms",
                MAX_MESSAGE_BUFFER_SIZE, MAX_SESSION_IDLE_TIMEOUT);

        return container;
    }

    /**
     * Google Cloud Speech-to-Text 클라이언트 빈 생성
     * - 서비스 계정 키를 사용하여 인증 설정
     */
    @Bean
    public SpeechClient speechClient() throws IOException {
        String resourcePath = credentialsLocation.replace("classpath:", "");
        Resource credentialResource = new ClassPathResource(resourcePath);

        try (InputStream credentialsStream = credentialResource.getInputStream()) {
            GoogleCredentials credentials = ServiceAccountCredentials.fromStream(credentialsStream);

            SpeechSettings speechSettings = SpeechSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();

            logger.info("SpeechClient 초기화: 프로젝트 ID = {}", projectId);
            return SpeechClient.create(speechSettings);
        } catch (IOException e) {
            logger.error("SpeechClient 생성 실패: {}", e.getMessage());
            throw e;
        }
    }

    @Bean
    public AudioWebSocketHandler audioWebSocketHandler() throws IOException {
        return new AudioWebSocketHandler(speechClient());
    }

    /**
     * WebSocket 핸들러 등록
     * - 오디오 스트림 처리를 위한 핸들러를 /ws/audio 경로에 매핑
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        try {
            registry.addHandler(audioWebSocketHandler(), "/ws/audio")
                    .setAllowedOrigins("*");

            logger.info("WebSocket 핸들러 등록 완료: 경로 = /ws/audio");
        } catch (Exception e) {
            logger.error("WebSocket 핸들러 등록 실패: {}", e.getMessage(), e);
        }
    }
}
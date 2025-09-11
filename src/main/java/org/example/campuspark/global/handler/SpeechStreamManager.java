package org.example.campuspark.global.handler;

import com.google.api.gax.rpc.BidiStream;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.cloud.speech.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Google Speech-to-Text 스트림을 관리하는 클래스
 *
 * Google Cloud Speech API와의 양방향 스트리밍 연결을 생성하고 관리합니다.
 * 각 WebSocket 세션에 대해 독립적인 STT 스트림을 생성하며,
 * 실시간 음성 인식 결과를 처리하여 클라이언트에게 전달합니다.
 *
 * 주요 기능:
 * - STT 스트림 생성 및 초기 설정
 * - 실시간 음성 인식 결과 처리
 * - 중간 결과와 최종 결과 구분 처리
 * - 에러 상황 처리 및 로깅
 */
public class SpeechStreamManager {
    private static final Logger logger = LoggerFactory.getLogger(SpeechStreamManager.class);

    private final SpeechClient speechClient;
    private final ConcurrentHashMap<String, SessionData> sessionDataMap;

    public SpeechStreamManager(SpeechClient speechClient, ConcurrentHashMap<String, SessionData> sessionDataMap) {
        this.speechClient = speechClient;
        this.sessionDataMap = sessionDataMap;
    }

    /**
     * 새로운 WebSocket 세션에 대한 Google STT 스트림을 생성합니다.
     *
     * @param session WebSocket 세션
     * @return 생성된 양방향 스트림
     * @throws RuntimeException STT 스트림 생성 실패 시
     */
    public BidiStream<StreamingRecognizeRequest, StreamingRecognizeResponse> createStream(WebSocketSession session) {
        try {
            // 음성 인식 설정
            RecognitionConfig recognitionConfig = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.WEBM_OPUS)
                    .setLanguageCode("ko-KR")
                    .setSampleRateHertz(48000)
                    .build();

            // 스트리밍 설정
            StreamingRecognitionConfig streamingConfig = StreamingRecognitionConfig.newBuilder()
                    .setConfig(recognitionConfig)
                    .setInterimResults(true) // 중간 결과도 받기
                    .build();

            // 초기 요청 생성
            StreamingRecognizeRequest initialRequest = StreamingRecognizeRequest.newBuilder()
                    .setStreamingConfig(streamingConfig)
                    .build();

            // 스트림 생성 및 초기 요청 전송
            BidiStreamingCallable<StreamingRecognizeRequest, StreamingRecognizeResponse> callable =
                    speechClient.streamingRecognizeCallable();
            BidiStream<StreamingRecognizeRequest, StreamingRecognizeResponse> stream = callable.call();
            stream.send(initialRequest);

            logger.info("세션 {}에 대한 STT 스트림 생성 완료", session.getId());
            return stream;

        } catch (Exception e) {
            logger.error("세션 {}의 STT 스트림 생성 실패: {}", session.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to create STT stream", e);
        }
    }

    /**
     * STT 응답을 처리하는 스레드를 생성합니다.
     *
     * @param session WebSocket 세션
     * @param stream STT 스트림
     * @return 응답 처리 스레드
     */
    public Thread createResponseThread(WebSocketSession session,
                                     BidiStream<StreamingRecognizeRequest, StreamingRecognizeResponse> stream) {
        Thread responseThread = new Thread(() -> processStreamResponses(session, stream));

        responseThread.setDaemon(true);
        responseThread.setName("STT-Response-" + session.getId());
        return responseThread;
    }

    /**
     * STT 스트림의 응답을 처리합니다.
     *
     * @param session WebSocket 세션
     * @param stream STT 스트림
     */
    private void processStreamResponses(WebSocketSession session,
                                      BidiStream<StreamingRecognizeRequest, StreamingRecognizeResponse> stream) {
        try {
            for (StreamingRecognizeResponse response : stream) {
                // 세션이 종료되었으면 루프 종료
                if (!sessionDataMap.containsKey(session.getId())) {
                    logger.debug("세션 {}이 종료되어 응답 처리 중단", session.getId());
                    break;
                }

                processRecognitionResults(session, response);
            }
        } catch (Exception e) {
            SessionData sessionData = sessionDataMap.get(session.getId());
            if (sessionData != null && sessionData.isActive()) {
                logger.error("세션 {}의 STT 응답 처리 중 오류: {}", session.getId(), e.getMessage(), e);
            } else {
                logger.debug("비활성 세션 {}의 STT 응답 처리 종료", session.getId());
            }
        }
    }

    /**
     * 음성 인식 결과를 처리하여 클라이언트에게 전송합니다.
     *
     * @param session WebSocket 세션
     * @param response STT 응답
     */
    private void processRecognitionResults(WebSocketSession session, StreamingRecognizeResponse response) {
        for (StreamingRecognitionResult result : response.getResultsList()) {
            if (result.getAlternativesCount() > 0) {
                String transcript = result.getAlternatives(0).getTranscript();
                boolean isFinal = result.getIsFinal();

                // 최종 결과는 ✅, 중간 결과는 ... 표시
                String formattedText = transcript + (isFinal ? " ✅" : " ...");

                logger.debug("음성 인식 결과 [{}]: {} (최종: {})",
                           session.getId(), transcript, isFinal);

                sendTextToClient(session, formattedText);
            }
        }
    }

    /**
     * 클라이언트에게 텍스트 메시지를 전송합니다.
     *
     * @param session WebSocket 세션
     * @param text 전송할 텍스트
     */
    private void sendTextToClient(WebSocketSession session, String text) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(text));
            } else {
                logger.debug("세션 {}이 닫혀있어 메시지 전송 불가", session.getId());
            }
        } catch (IOException e) {
            logger.error("세션 {}에 메시지 전송 실패: {}", session.getId(), e.getMessage());
        }
    }
}

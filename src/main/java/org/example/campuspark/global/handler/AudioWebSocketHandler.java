//package org.example.campuspark.global.handler;
//
//import com.google.api.gax.rpc.BidiStream;
//import com.google.cloud.speech.v1.*;
//import com.google.protobuf.ByteString;
//import org.springframework.web.socket.*;
//import org.springframework.web.socket.handler.BinaryWebSocketHandler;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.util.concurrent.ConcurrentHashMap;
//import org.springframework.lang.NonNull;
//
///**
// * 실시간 오디오 스트리밍을 위한 WebSocket 핸들러
// *
// * 클라이언트로부터 오디오 데이터를 실시간으로 수신하여 Google Cloud Speech-to-Text API로 전달하고,
// * 음성 인식 결과를 다시 클라이언트에게 전송하는 양방향 통신을 담당합니다.
// *
// * 주요 기능:
// * - WebSocket을 통한 실시간 오디오 데이터 수신
// * - Google STT API와의 스트리밍 연결 관리
// * - 세션별 독립적인 음성 인식 처리
// * - 비활성 세션 자동 정리 및 리소스 관리
// * - 에러 상황 처리 및 로깅
// *
// * 사용되는 오디오 포맷: WEBM_OPUS, 48kHz, 한국어
// */
//public class AudioWebSocketHandler extends BinaryWebSocketHandler {
//
//    private static final Logger logger = LoggerFactory.getLogger(AudioWebSocketHandler.class);
//
//    private final SpeechClient speechClient;
//    private final ConcurrentHashMap<String, SessionData> sessionDataMap = new ConcurrentHashMap<>();
//    private final SpeechStreamManager speechStreamManager;
//    private final SessionCleanupTask sessionCleanupTask;
//
//    public AudioWebSocketHandler(SpeechClient speechClient) {
//        this.speechClient = speechClient;
//        this.speechStreamManager = new SpeechStreamManager(speechClient, sessionDataMap);
//        this.sessionCleanupTask = new SessionCleanupTask(sessionDataMap);
//
//        // 세션 정리 작업 시작
//        this.sessionCleanupTask.start();
//
//        logger.info("AudioWebSocketHandler 초기화 완료");
//    }
//
//    @Override
//    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
//        try {
//            logger.info("세션 연결 시도: {} (리모트 주소: {}, URI: {})",
//                       session.getId(),
//                       session.getRemoteAddress(),
//                       session.getUri());
//
//            // STT 스트림 생성
//            BidiStream<StreamingRecognizeRequest, StreamingRecognizeResponse> stream =
//                speechStreamManager.createStream(session);
//
//            // 응답 처리 스레드 생성 및 시작
//            Thread responseThread = speechStreamManager.createResponseThread(session, stream);
//            responseThread.start();
//
//            // 세션 데이터 저장
//            SessionData sessionData = new SessionData(session, stream, responseThread);
//            sessionDataMap.put(session.getId(), sessionData);
//
//            logger.info("세션 연결 완료: {} (현재 활성 세션: {}개)",
//                       session.getId(), sessionDataMap.size());
//
//        } catch (Exception e) {
//            logger.error("세션 연결 실패: {} - {}", session.getId(), e.getMessage(), e);
//            closeSessionSafely(session, CloseStatus.SERVER_ERROR);
//        }
//    }
//
//    @Override
//    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
//        String sessionId = session.getId();
//        SessionData sessionData = sessionDataMap.remove(sessionId);
//
//        logger.info("세션 종료: {} (상태 코드: {}, 사유: {}, 활성 상태: {})",
//                   sessionId,
//                   status.getCode(),
//                   status.getReason(),
//                   sessionData != null ? sessionData.isActive() : "세션 없음");
//
//        if (sessionData != null) {
//            sessionData.close();
//            logger.info("세션 자원 정리 완료: {} (남은 세션: {}개)",
//                       sessionId, sessionDataMap.size());
//        }
//    }
//
//    @Override
//    protected void handleBinaryMessage(@NonNull WebSocketSession session, @NonNull BinaryMessage message) {
//        String sessionId = session.getId();
//        SessionData sessionData = sessionDataMap.get(sessionId);
//
//        if (sessionData == null || !sessionData.isActive()) {
//            logger.warn("비활성 세션에서 메시지 수신: {}", sessionId);
//            return;
//        }
//
//        try {
//            byte[] audioBytes = extractAudioData(message);
//
//            if (audioBytes.length == 0) {
//                logger.debug("빈 오디오 데이터 수신: {}", sessionId);
//                return;
//            }
//
//            // 첫 오디오 데이터 수신 로깅
//            if (isFirstAudioData(sessionData)) {
//                logger.info("첫 오디오 데이터 수신: {} ({} 바이트)", sessionId, audioBytes.length);
//            } else {
//                logger.debug("오디오 데이터 수신: {} ({} 바이트)", sessionId, audioBytes.length);
//            }
//
//            // 마지막 오디오 수신 시간 업데이트
//            sessionData.updateLastAudioSentTimestamp();
//
//            // Google STT로 오디오 데이터 전송
//            sendAudioToSpeechAPI(sessionData, audioBytes);
//
//        } catch (Exception e) {
//            logger.error("오디오 데이터 처리 오류: {} - {}", sessionId, e.getMessage(), e);
//            cleanupSessionData(sessionId);
//        }
//    }
//
//    @Override
//    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) {
//        logger.error("WebSocket 전송 오류: {} - {}", session.getId(), exception.getMessage(), exception);
//        cleanupSessionData(session.getId());
//    }
//
//    /**
//     * 바이너리 메시지에서 오디오 데이터를 추출합니다.
//     */
//    private byte[] extractAudioData(BinaryMessage message) {
//        ByteBuffer payload = message.getPayload();
//        byte[] audioBytes = new byte[payload.remaining()];
//        payload.get(audioBytes);
//        return audioBytes;
//    }
//
//    /**
//     * 첫 번째 오디오 데이터인지 확인합니다.
//     */
//    private boolean isFirstAudioData(SessionData sessionData) {
//        return sessionData.getLastAudioSentTimestamp() == System.currentTimeMillis();
//    }
//
//    /**
//     * Google Speech API로 오디오 데이터를 전송합니다.
//     */
//    private void sendAudioToSpeechAPI(SessionData sessionData, byte[] audioBytes) {
//        StreamingRecognizeRequest audioRequest = StreamingRecognizeRequest.newBuilder()
//                .setAudioContent(ByteString.copyFrom(audioBytes))
//                .build();
//
//        sessionData.getStream().send(audioRequest);
//    }
//
//    /**
//     * 세션을 안전하게 종료합니다.
//     */
//    private void closeSessionSafely(WebSocketSession session, CloseStatus status) {
//        try {
//            if (session.isOpen()) {
//                session.close(status);
//            }
//        } catch (IOException ex) {
//            logger.error("세션 종료 실패: {} - {}", session.getId(), ex.getMessage());
//        }
//    }
//
//    /**
//     * 세션 데이터를 정리합니다.
//     */
//    private void cleanupSessionData(String sessionId) {
//        SessionData sessionData = sessionDataMap.remove(sessionId);
//        if (sessionData != null) {
//            sessionData.close();
//            logger.debug("세션 {} 데이터 정리 완료", sessionId);
//        }
//    }
//}

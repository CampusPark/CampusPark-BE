package org.example.campuspark.global.handler;

import com.google.api.gax.rpc.BidiStream;
import com.google.cloud.speech.v1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

/**
 * WebSocket 세션별 데이터를 관리하는 클래스
 *
 * 각 클라이언트 연결에 대해 Google STT 스트림, 응답 처리 스레드,
 * 세션 상태 등의 정보를 캡슐화하여 관리합니다.
 *
 * 주요 기능:
 * - Google STT 양방향 스트림 관리
 * - 세션 활성 상태 추적
 * - 마지막 오디오 수신 시간 기록
 * - 리소스 정리 및 스트림 종료
 */
public class SessionData {
    private static final Logger logger = LoggerFactory.getLogger(SessionData.class);

    private final WebSocketSession session;
    private final BidiStream<StreamingRecognizeRequest, StreamingRecognizeResponse> stream;
    private final Thread responseThread;
    private boolean isActive = true;
    private long lastAudioSentTimestamp = System.currentTimeMillis();

    public SessionData(WebSocketSession session,
                      BidiStream<StreamingRecognizeRequest, StreamingRecognizeResponse> stream,
                      Thread responseThread) {
        this.session = session;
        this.stream = stream;
        this.responseThread = responseThread;
    }

    /**
     * 세션 리소스를 정리하고 스트림을 종료합니다.
     */
    public void close() {
        isActive = false;
        try {
            stream.closeSend();
            logger.debug("세션 {} 스트림 종료 완료", session.getId());
        } catch (Exception e) {
            logger.error("세션 {} 스트림 종료 중 오류: {}", session.getId(), e.getMessage());
        }
    }

    // Getters
    public WebSocketSession getSession() {
        return session;
    }

    public BidiStream<StreamingRecognizeRequest, StreamingRecognizeResponse> getStream() {
        return stream;
    }

    public Thread getResponseThread() {
        return responseThread;
    }

    public boolean isActive() {
        return isActive;
    }

    public long getLastAudioSentTimestamp() {
        return lastAudioSentTimestamp;
    }

    public void updateLastAudioSentTimestamp() {
        this.lastAudioSentTimestamp = System.currentTimeMillis();
    }
}

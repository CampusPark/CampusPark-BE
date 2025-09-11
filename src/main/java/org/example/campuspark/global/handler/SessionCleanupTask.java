package org.example.campuspark.global.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 비활성 WebSocket 세션을 정리하는 백그라운드 작업 클래스
 *
 * 일정 시간 동안 오디오 데이터를 받지 못한 세션들을 주기적으로 감지하고 정리합니다.
 * 메모리 누수를 방지하고 불필요한 Google STT 스트림 연결을 정리하여
 * 시스템 리소스를 효율적으로 관리합니다.
 *
 * 주요 기능:
 * - 주기적인 세션 상태 확인 (10초 간격)
 * - 비활성 세션 감지 (25초 이상 오디오 없음)
 * - 자동 세션 정리 및 리소스 해제
 * - 백그라운드 데몬 스레드로 동작
 */
public class SessionCleanupTask {
    private static final Logger logger = LoggerFactory.getLogger(SessionCleanupTask.class);

    private static final long CLEANUP_INTERVAL_MS = 10000; // 10초마다 확인
    private static final long SESSION_TIMEOUT_MS = 25000;  // 25초 이상 비활성시 정리

    private final ConcurrentHashMap<String, SessionData> sessionDataMap;
    private final Thread cleanupThread;
    private volatile boolean isRunning = true;

    public SessionCleanupTask(ConcurrentHashMap<String, SessionData> sessionDataMap) {
        this.sessionDataMap = sessionDataMap;
        this.cleanupThread = createCleanupThread();
    }

    /**
     * 세션 정리 작업을 시작합니다.
     */
    public void start() {
        cleanupThread.start();
        logger.info("세션 정리 작업 시작됨 ({}초 간격, {}초 타임아웃)",
                   CLEANUP_INTERVAL_MS / 1000, SESSION_TIMEOUT_MS / 1000);
    }

    /**
     * 세션 정리 작업을 중지합니다.
     */
    public void stop() {
        isRunning = false;
        cleanupThread.interrupt();
        logger.info("세션 정리 작업 중지됨");
    }

    /**
     * 세션 정리 백그라운드 스레드를 생성합니다.
     *
     * @return 정리 작업 스레드
     */
    private Thread createCleanupThread() {
        Thread thread = new Thread(this::runCleanupLoop);
        thread.setDaemon(true);
        thread.setName("SessionCleanup");
        return thread;
    }

    /**
     * 세션 정리 작업의 메인 루프입니다.
     */
    private void runCleanupLoop() {
        while (isRunning) {
            try {
                Thread.sleep(CLEANUP_INTERVAL_MS);
                cleanupInactiveSessions();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.info("세션 정리 스레드 인터럽트됨");
                break;
            } catch (Exception e) {
                logger.error("세션 정리 작업 중 오류: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 비활성 세션들을 찾아서 정리합니다.
     */
    private void cleanupInactiveSessions() {
        long currentTime = System.currentTimeMillis();
        AtomicInteger cleanedCount = new AtomicInteger(0);

        // 타임아웃된 세션들을 찾아서 정리
        sessionDataMap.entrySet().removeIf(entry -> {
            String sessionId = entry.getKey();
            SessionData sessionData = entry.getValue();

            if (shouldCleanupSession(sessionData, currentTime)) {
                cleanupSession(sessionId, sessionData);
                cleanedCount.incrementAndGet();
                return true; // 맵에서 제거
            }
            return false; // 유지
        });

        if (cleanedCount.get() > 0) {
            logger.info("{}개의 비활성 세션 정리 완료 (현재 활성 세션: {}개)",
                       cleanedCount.get(), sessionDataMap.size());
        }
    }

    /**
     * 세션이 정리되어야 하는지 확인합니다.
     *
     * @param sessionData 세션 데이터
     * @param currentTime 현재 시간
     * @return 정리 필요 여부
     */
    private boolean shouldCleanupSession(SessionData sessionData, long currentTime) {
        return sessionData.isActive() &&
               (currentTime - sessionData.getLastAudioSentTimestamp() > SESSION_TIMEOUT_MS);
    }

    /**
     * 개별 세션을 정리합니다.
     *
     * @param sessionId 세션 ID
     * @param sessionData 세션 데이터
     */
    private void cleanupSession(String sessionId, SessionData sessionData) {
        logger.info("세션 {} 타임아웃으로 정리 시작 ({}초 동안 오디오 없음)",
                   sessionId, SESSION_TIMEOUT_MS / 1000);

        try {
            // WebSocket 세션 종료
            if (sessionData.getSession().isOpen()) {
                sessionData.getSession().close(CloseStatus.SERVER_ERROR);
            }
        } catch (IOException e) {
            logger.error("세션 {} WebSocket 종료 실패: {}", sessionId, e.getMessage());
        }

        // 스트림 및 기타 리소스 정리
        sessionData.close();

        logger.debug("세션 {} 정리 완료", sessionId);
    }

    /**
     * 현재 활성 세션 수를 반환합니다.
     *
     * @return 활성 세션 수
     */
    public int getActiveSessionCount() {
        return sessionDataMap.size();
    }
}

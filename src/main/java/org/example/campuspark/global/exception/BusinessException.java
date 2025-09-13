package org.example.campuspark.global.exception;

import lombok.Getter;

/**
 * 비즈니스 로직 예외를 처리하는 커스텀 예외 클래스
 * RuntimeException을 상속받아 unchecked exception으로 처리
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}

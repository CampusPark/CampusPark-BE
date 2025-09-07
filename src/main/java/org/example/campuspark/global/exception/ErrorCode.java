package org.example.campuspark.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E999", "서버 내부 에러가 발생했습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "E001", "요청한 리소스를 찾을 수 없습니다."),
    RESOURCE_ALREADY_EXISTS(HttpStatus.CONFLICT, "E002", "이미 존재하는 리소스입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E003", "인증이 필요한 요청입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "E004", "해당 리소스에 접근할 권한이 없습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "E005", "잘못된 요청입니다. 입력 값을 확인해주세요."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "E006", "유효성 검사에 실패했습니다. 올바른 값을 입력해 주세요");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

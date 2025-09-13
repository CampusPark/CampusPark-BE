package org.example.campuspark.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 비즈니스 예외 코드를 정의하는 enum
 * HTTP 상태 코드와 메시지를 함께 관리하여 일관된 예외 처리를 제공
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E999", "서버 내부 에러가 발생했습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "E001", "요청한 리소스를 찾을 수 없습니다."),
    RESOURCE_ALREADY_EXISTS(HttpStatus.CONFLICT, "E002", "이미 존재하는 리소스입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E003", "인증이 필요한 요청입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "E004", "해당 리소스에 접근할 권한이 없습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "E005", "잘못된 요청입니다. 입력 값을 확인해주세요."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "E006", "유효성 검사에 실패했습니다. 올바른 값을 입력해 주세요"),
    ERROR_IN_GEOCODING(HttpStatus.BAD_REQUEST, "E007", "주소 변환 중 오류가 발생했습니다."),
    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "지원하지 않는 HTTP 메서드입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "엔티티를 찾을 수 없습니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C005", "잘못된 타입 값입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C006", "접근이 거부되었습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    EMAIL_DUPLICATION(HttpStatus.CONFLICT, "U002", "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U003", "잘못된 비밀번호입니다."),

    // ParkingSpace
    PARKING_SPACE_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "주차공간을 찾을 수 없습니다."),
    PARSING_ERROR(HttpStatus.BAD_REQUEST, "P006", "주차공간 번호 분석에 실패했습니다."),
    NO_NEARBY_PARKING_SPACES_FOUND(HttpStatus.NOT_FOUND, "P007", "주변에 주차공간이 없습니다."),
    INVALID_PARKING_SPACE_SELECTION(HttpStatus.BAD_REQUEST, "P008", "잘못된 주차공간 선택입니다."),
    PARKING_SPACE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "P002", "주차공간에 접근할 권한이 없습니다."),
    MAX_PARKING_SPACE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "P003", "주차공간 등록 한도를 초과했습니다."),
    CANNOT_DEACTIVATE_WITH_RESERVATIONS(HttpStatus.BAD_REQUEST, "P004", "예약이 있는 주차공간은 비활성화할 수 없습니다."),
    CANNOT_DELETE_WITH_RESERVATIONS(HttpStatus.BAD_REQUEST, "P005", "예약이 있는 주차공간은 삭제할 수 없습니다."),

    // Reservation
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "예약을 찾을 수 없습니다."),
    RESERVATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "R002", "예약에 접근할 권한이 없습니다."),
    RESERVATION_TIME_CONFLICT(HttpStatus.CONFLICT, "R003", "이미 예약된 시간입니다."),
    INVALID_RESERVATION_TIME(HttpStatus.BAD_REQUEST, "R004", "잘못된 예약 시간입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

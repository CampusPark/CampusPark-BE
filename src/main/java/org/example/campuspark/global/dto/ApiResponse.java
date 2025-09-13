package org.example.campuspark.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * API 응답을 위한 공통 DTO 클래스
 * 일관된 응답 구조를 제공하여 클라이언트의 응답 처리를 단순화
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String code; // Added error code field
    private LocalDateTime timestamp;

    // Constructor for success responses
    private ApiResponse(boolean success, String message, T data, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }

    // Constructor for error responses
    private ApiResponse(boolean success, String message, String code, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.timestamp = timestamp;
    }

    // 성공 응답 생성 메서드들
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now());
    }

    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(true, message, null, LocalDateTime.now());
    }

    // 실패 응답 생성 메서드들
    public static <T> ApiResponse<T> error(String message, String code) {
        return new ApiResponse<>(false, message, code, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(String message, String code, T data) {
        ApiResponse<T> response = new ApiResponse<>(false, message, code, LocalDateTime.now());
        response.data = data;
        return response;
    }
}

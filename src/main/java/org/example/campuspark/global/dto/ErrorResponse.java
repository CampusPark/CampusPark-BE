package org.example.campuspark.global.dto;

import lombok.Builder;

@Builder
public record ErrorResponse (
    String errorCode,
    String message
){
}

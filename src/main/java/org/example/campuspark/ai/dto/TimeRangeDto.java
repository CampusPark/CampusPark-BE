package org.example.campuspark.ai.dto;

import java.time.LocalDateTime;

public record TimeRangeDto(
    LocalDateTime startTime,
    LocalDateTime endTime
) {}
